import random
import tempfile
import unittest
from pathlib import Path
from unittest.mock import patch

import generate_data


class FakeVariable:
    def __init__(self):
        self.values = []

    def getvalue(self, index):
        return [self.values[index]]


class FakeCursor:
    def __init__(self, connection):
        self.connection = connection
        self.result = []
        self.output = None

    def __enter__(self):
        return self

    def __exit__(self, *args):
        pass

    def execute(self, sql):
        if "FROM majitel" in sql:
            self.result = [(value,) for value in self.connection.owners]
        elif "FROM veterinar" in sql:
            self.result = [(value,) for value in self.connection.veterinarians]
        else:
            raise AssertionError(sql)

    def fetchall(self):
        return self.result

    def var(self, typ, arraysize):
        return FakeVariable()

    def setinputsizes(self, *, new_id):
        self.output = new_id

    def executemany(self, sql, rows):
        self.connection.batches.append((sql, rows))
        if self.output is not None:
            start = self.connection.next_id
            self.output.values = list(range(start, start + len(rows)))
            self.connection.next_id += len(rows)


class FakeConnection:
    def __init__(self, owners=(1, 2), veterinarians=(10, 11)):
        self.owners = owners
        self.veterinarians = veterinarians
        self.next_id = 100
        self.batches = []
        self.committed = False

    def cursor(self):
        return FakeCursor(self)

    def commit(self):
        self.committed = True


class GeneratorTests(unittest.TestCase):
    def test_seed_and_dates(self):
        def generate(seed):
            rng = random.Random(seed)
            return [generate_data.Generator.generate(rng, [1, 2], [3, 4]) for _ in range(1000)]

        records = generate(42)
        self.assertEqual(records, generate(42))
        self.assertNotEqual(records, generate(43))
        self.assertTrue(any(record.due_date.year == 2025 and not record.paid for record in records))
        self.assertTrue(any(record.paid for record in records))
        self.assertTrue(all(record.birth_date <= record.examination_date < record.due_date for record in records))

    def test_batches_keep_generated_foreign_keys_and_payments(self):
        connection = FakeConnection()
        generate_data.populate(connection, 1001, 42)
        self.assertTrue(connection.committed)
        first = connection.batches[:5]
        self.assertEqual([len(rows) for _, rows in first[:4]], [1000] * 4)
        animals, cards, exams, invoices, payments = (rows for _, rows in first)
        self.assertEqual([row["animal_id"] for row in cards], list(range(100, 1100)))
        self.assertEqual([row["card_id"] for row in exams], list(range(1100, 2100)))
        self.assertEqual([row["exam_id"] for row in invoices], list(range(2100, 3100)))
        paid = {3100 + i: row["amount"] for i, row in enumerate(invoices) if row["status"] == "UHRADENA"}
        self.assertEqual({row["invoice_id"]: row["amount"] for row in payments}, paid)
        self.assertEqual(sum(len(rows) for sql, rows in connection.batches if "INSERT INTO faktura" in sql), 1001)

    def test_100000_records_and_missing_parent(self):
        connection = FakeConnection()
        sizes = []
        with patch.object(generate_data, "insert_batch", side_effect=lambda _, rows: sizes.append(len(rows))):
            generate_data.populate(connection, 100000, 42)
        self.assertEqual(sum(sizes), 100000)
        self.assertEqual(len(sizes), 100)
        self.assertTrue(connection.committed)
        missing = FakeConnection(owners=())
        with self.assertRaisesRegex(ValueError, "majitela"):
            generate_data.populate(missing, 1, 42)
        self.assertFalse(missing.committed)

    def test_batch_failure_does_not_commit(self):
        connection = FakeConnection()
        with patch.object(generate_data, "insert_batch", side_effect=[None, RuntimeError("insert failed")]):
            with self.assertRaisesRegex(RuntimeError, "insert failed"):
                generate_data.populate(connection, 1001, 42)
        self.assertFalse(connection.committed)

    def test_reads_env_and_rejects_missing_settings(self):
        with tempfile.TemporaryDirectory() as directory:
            env = Path(directory) / ".env"
            env.write_text(
                "DB_USER=test\nDB_PASSWORD=test\nDB_HOST=localhost\n"
                "DB_PORT=1521\nDB_SERVICE=testdb\n", encoding="utf-8"
            )
            config = generate_data.connection_settings(env)
            self.assertEqual(config["user"], "test")
            self.assertIn("testdb", config["dsn"])
            env.write_text("DB_USER=test\n", encoding="utf-8")
            with self.assertRaisesRegex(ValueError, "DB_PASSWORD"):
                generate_data.connection_settings(env)


if __name__ == "__main__":
    unittest.main()
