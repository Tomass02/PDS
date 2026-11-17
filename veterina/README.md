# Veterinarna ambulancia - Java verzia

Aplikacia je prerobena z Flasku do **Java Spring Boot + Thymeleaf**. Angular nie je potrebny: frontend je jednoduche HTML/Thymeleaf GUI a backend je rozdeleny do samostatnych controllerov, repository a modelov, aby nebol cely projekt v jednom neprehladnom subore.

## CRUD

CRUD je implementovany nad vsetkymi tabulkami:

- OSOBA
- MAJITEL
- VETERINAR
- ZVIERA
- ZDRAVOTNA_KARTA
- VYSETRENIE
- FAKTURA
- POLOZKA_FAKTURY
- PLATBA
- PREDPIS

Katalog `LIEK` je samostatna, iba na citanie pouzivana tabulka v databaze
Obelix; formular predpisu nacitava lieky cez database link.

Dalej zostava:

- objektovy vystup nad `T_ZDRAVOTNY_STAV`,
- volanie `vypocitaj_rizikovy_index()`,
- triedenie objektov cez MAP metodu,
- JSON report generovany priamo v Oracle,
- BLOB fotografia zvierata,
- BLOB scan faktury,
- povodne SQL skripty `1CreateDbSchema.sql` a `2Checkpoint.sql`.

## Struktura

```text
src/main/java/sk/uniza/veterina/
  controller/     # HTTP requesty a GUI
  model/          # datove modely
  repository/     # SQL a komunikacia s Oracle
  VeterinaApplication.java

src/main/resources/
  templates/      # HTML/Thymeleaf GUI
  static/css/     # CSS
  application.properties
```

## 1. Databaza

Katalog liekov je ulozeny na vzdialenom serveri Obelix. Pouzite Oracle konto musi
existovat na oboch serveroch s rovnakym heslom; database link pouziva prihlasenie
aktualneho pouzivatela a neuklada heslo do skriptu.

1. Na Obelixe, po pripojeni ako `HODAS28`, spusti raz
   `DbScripts/0CreateLiekOnObelix.sql`. Skript vytvori tabulku `LIEK` a vlozi
   vzorovy katalog.
2. Pre novu, prazdnu schemu Oradb spusti:

```sql
@1CreateDbSchema.sql
@2Checkpoint.sql
```

`1CreateDbSchema.sql` vytvori database link `LIEK_OBELIX_LINK` a synonymum
`LIEK`; predpisy ukladaju `ID_LIEK` a nacitavaju nazov lieku cez link.
Oracle nevie vynutit cudzi kluc napriec databazami, preto trigger overuje
existenciu lieku pri zapise a pouzite lieky nevymazavaj z katalogu.
Ak na Obelixe pri vytvarani tabulky dostanes `ORA-01950`, poziadaj spravcu
databazy o kvotu v predvolenom tablespace pouzivatela `HODAS28`.

Pre uz vytvorenu Oradb schemu tieto inicializacne skripty znovu nespustaj:
po vytvoreni `LIEK` na Obelixe spusti na Oradb iba
`DbScripts/3MigrateExistingPredpis.sql`. Migracia zachova existujuce predpisy;
ak sa niektory stary nazov lieku v Obelixe nenajde, dopln ho tam a migraciu
zopakuj. Aplikacia sa pripaja na Oradb pod schemou vlastniacou predpisy a
database link.

## 2. Nastavenie pripojenia

V koreni projektu skopiruj:

```powershell
Copy-Item .env.example .env
```

A uprav `.env`:

```env
DB_USER=username
DB_PASSWORD=TVOJE_HESLO
DB_HOST=oradb.fri.uniza.sk
DB_PORT=1521
DB_SERVICE=orclpdb.fri.uniza.sk
```

`.env` je v `.gitignore`, aby sa heslo neposielalo do Git repozitara.

## 3. Spustenie

Potrebujes Java 17+ a Maven.

Z korena projektu:

```powershell
mvn spring-boot:run
```

Potom otvor:

```text
http://localhost:8080
```

## Spustenie cez Docker

Potrebujes Docker s podporou Docker Compose. Najprv vytvor `.env` podla postupu
vyssie a nakonfiguruj pristup k Oracle databaze, ktora musi byt dostupna zo siete
Docker kontajnera. Databazu ani jej inicializacne skripty kontajner nespusta.

V adresari `veterina` spusti:

docker compose up --build

Aplikacia bude dostupna na `http://localhost:8080`. Na zastavenie pouzi
`Ctrl+C`; na odstranenie kontajnera spusti `docker compose down`.
