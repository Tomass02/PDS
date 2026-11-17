"""Generate linked test records for the Oracle index experiment."""

import argparse
import random
import sys
from dataclasses import dataclass
from datetime import datetime, timedelta
from decimal import Decimal
from pathlib import Path

import oracledb
from dotenv import dotenv_values


DEFAULT_ENV_FILE = Path(__file__).resolve().parent.parent / "veterina" / ".env"
BATCH_SIZE = 1000


@dataclass(frozen=True)
class GeneratedRecord:
    owner_id: int
    veterinarian_id: int
    animal_name: str
    species: str
    birth_date: datetime
    examination_date: datetime
    temperature: Decimal
    heart_rate: int
    weight: Decimal
    severity: str
    amount: Decimal
    due_date: datetime
    paid: bool


class Generator:
    @staticmethod
    def generate(rng: random.Random, owner_ids: list[int], veterinarian_ids: list[int]) -> GeneratedRecord:
        examination_date = datetime(2023, 1, 1) + timedelta(
            days=rng.randrange(1461), hours=rng.randrange(8, 18), minutes=rng.randrange(60)
        )
        return GeneratedRecord(
            owner_id=rng.choice(owner_ids),
            veterinarian_id=rng.choice(veterinarian_ids),
            animal_name=rng.choice(("Luna", "Max", "Bella", "Mia", "Rocky", "Boby")),
            species=rng.choice(("Pes", "Macka", "Kralik")),
            birth_date=datetime(2018, 1, 1) + timedelta(days=rng.randrange(1096)),
            examination_date=examination_date,
            temperature=Decimal(rng.randrange(370, 405)) / 10,
            heart_rate=rng.randrange(60, 181),
            weight=Decimal(rng.randrange(100, 5001)) / 100,
            severity=rng.choice(("Nizka", "Stredna", "Vysoka")),
            amount=Decimal(rng.randrange(1000, 50001)) / 100,
            due_date=examination_date.replace(hour=0, minute=0) + timedelta(days=rng.randrange(7, 31)),
            paid=rng.random() < 0.6,
        )


def insert_returning(connection: oracledb.Connection, sql: str, rows: list[dict]) -> list[int]:
    with connection.cursor() as cursor:
        ids = cursor.var(int, arraysize=len(rows))
        cursor.setinputsizes(new_id=ids)
        cursor.executemany(sql, rows)
        return [ids.getvalue(index)[0] for index in range(len(rows))]


def insert_batch(connection: oracledb.Connection, records: list[GeneratedRecord]) -> None:
    animal_ids = insert_returning(
        connection,
        """INSERT INTO zviera (id_majitel, meno, druh, datum_narodenia)
           VALUES (:owner_id, :name, :species, :birth_date)
           RETURNING id_zviera INTO :new_id""",
        [
            {"owner_id": record.owner_id, "name": record.animal_name,
             "species": record.species, "birth_date": record.birth_date}
            for record in records
        ],
    )
    card_ids = insert_returning(
        connection,
        """INSERT INTO zdravotna_karta (id_zviera, datum_zalozenia)
           VALUES (:animal_id, :birth_date)
           RETURNING id_zdravotna_karta INTO :new_id""",
        [
            {"animal_id": animal_id, "birth_date": record.birth_date}
            for animal_id, record in zip(animal_ids, records)
        ],
    )
    examination_ids = insert_returning(
        connection,
        """INSERT INTO vysetrenie
           (id_zdravotna_karta, id_veterinar, datum_cas_vysetrenia, detaily_stavu)
           VALUES (:card_id, :veterinarian_id, :exam_date,
                   t_zdravotny_stav(:temperature, :heart_rate, :weight, :severity))
           RETURNING id_vysetrenie INTO :new_id""",
        [
            {"card_id": card_id, "veterinarian_id": record.veterinarian_id,
             "exam_date": record.examination_date, "temperature": record.temperature,
             "heart_rate": record.heart_rate, "weight": record.weight, "severity": record.severity}
            for card_id, record in zip(card_ids, records)
        ],
    )
    invoice_ids = insert_returning(
        connection,
        """INSERT INTO faktura
           (id_vysetrenie, suma_celkom, datum_vystavenia, datum_splatnosti, stav_uhrady)
           VALUES (:exam_id, :amount, :issued, :due_date, :status)
           RETURNING id_faktura INTO :new_id""",
        [
            {"exam_id": exam_id, "amount": record.amount, "issued": record.examination_date,
             "due_date": record.due_date, "status": "UHRADENA" if record.paid else "NEUHRADENA"}
            for exam_id, record in zip(examination_ids, records)
        ],
    )
    payments = [
        {"invoice_id": invoice_id, "payment_date": record.examination_date,
         "amount": record.amount, "method": "KARTA"}
        for invoice_id, record in zip(invoice_ids, records) if record.paid
    ]
    if payments:
        with connection.cursor() as cursor:
            cursor.executemany(
                """INSERT INTO platba (id_faktura, datum_cas_platby, suma, sposob_platby)
                   VALUES (:invoice_id, :payment_date, :amount, :method)""",
                payments,
            )


def populate(connection: oracledb.Connection, count: int, seed: int | None) -> None:
    if count < 1:
        raise ValueError("Pocet zaznamov musi byt kladne cislo.")
    with connection.cursor() as cursor:
        cursor.execute("SELECT id_majitel FROM majitel ORDER BY id_majitel")
        owner_ids = [row[0] for row in cursor.fetchall()]
        cursor.execute("SELECT id_veterinar FROM veterinar ORDER BY id_veterinar")
        veterinarian_ids = [row[0] for row in cursor.fetchall()]
    if not owner_ids or not veterinarian_ids:
        raise ValueError("Databaza musi obsahovat aspon jedneho majitela a veterinara.")

    rng = random.Random(seed)
    for offset in range(0, count, BATCH_SIZE):
        size = min(BATCH_SIZE, count - offset)
        records = [Generator.generate(rng, owner_ids, veterinarian_ids) for _ in range(size)]
        insert_batch(connection, records)
    connection.commit()


def connection_settings(env_file: Path) -> dict[str, str]:
    if not env_file.is_file():
        raise FileNotFoundError(f"Subor .env neexistuje: {env_file}")
    values = dotenv_values(env_file)
    missing = [key for key in ("DB_USER", "DB_PASSWORD", "DB_HOST", "DB_SERVICE") if not values.get(key)]
    user, password = values.get("DB_USER"), values.get("DB_PASSWORD")
    host, service = values.get("DB_HOST"), values.get("DB_SERVICE")
    if not user or not password or not host or not service:
        raise ValueError("V subore .env chybaju: " + ", ".join(missing))
    port = int(values.get("DB_PORT") or "1521")
    if not 1 <= port <= 65535:
        raise ValueError("DB_PORT musi byt v rozsahu 1 az 65535.")
    return {
        "user": user,
        "password": password,
        "dsn": oracledb.makedsn(host, port, service_name=service),
    }


def positive_int(value: str) -> int:
    try:
        number = int(value)
    except ValueError as exc:
        raise argparse.ArgumentTypeError("Zadajte cele cislo.") from exc
    if number < 1:
        raise argparse.ArgumentTypeError("Pocet musi byt kladne cislo.")
    return number


def main() -> int:
    parser = argparse.ArgumentParser(description="Generovanie testovacich zaznamov do Oracle.")
    parser.add_argument("-n", "--count", type=positive_int, default=1000, help="pocet faktur (default: 1000)")
    parser.add_argument("--seed", type=int, help="seed pre opakovatelne generovanie")
    parser.add_argument("--env-file", type=Path, default=DEFAULT_ENV_FILE, help="cesta k suboru .env")
    args = parser.parse_args()
    try:
        with oracledb.connect(**connection_settings(args.env_file)) as connection:
            populate(connection, args.count, args.seed)
    except (OSError, ValueError, oracledb.Error) as exc:
        print(f"Generovanie zlyhalo: {exc}", file=sys.stderr)
        return 1
    print(f"Ulozenych faktur: {args.count}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
