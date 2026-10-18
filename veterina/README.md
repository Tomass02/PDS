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

V Oracle najprv spusti:

```sql
@1CreateDbSchema.sql
@2Checkpoint.sql
```

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
