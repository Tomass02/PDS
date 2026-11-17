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

Samostatny experiment s indexom je v [3Index.sql](../DbScripts/3Index.sql).
V testovacej scheme priprav aspon 100 000 faktur so suvisiacimi zaznamami
a skript spusti cez SQLcl, SQL*Plus alebo SQL Developer (F5). Vytvori
proceduru reportu a proceduru na zobrazenie skutocneho planu. Porovna stav
bez noveho indexu s tromi indexmi, kazdym samostatne; po merani kazdy zmaze.
Plan vyzaduje pristup k `V$SQL` a opravnenia pre `DBMS_XPLAN`. Ak skript
skonci chybou po vytvoreni indexu, treba ho odstranit rucne.

### Generovanie testovacich dat

Generovanie spustaj iba v testovacej databaze. Po vytvoreni schemy a
testovacich majitelov a veterinarov (napr. pomocou `2Checkpoint.sql`)
spusti z korena repozitara v PowerShelli (potrebujes Python 3.10+):

```powershell
.\generator\setup.ps1
.\generator\.venv\Scripts\python.exe generator\generate_data.py -n 100000 --seed 42
```

Skript `setup.ps1` vytvori `generator\.venv` a nainstaluje zavislosti;
existujuce prostredie pouzije bez prepisania.

Parameter `-n` urcuje pocet novych faktur, predvolene 1000. Volitelny
`--seed` umoznuje zopakovat rovnake nahodne hodnoty pri rovnakom zozname
majitelov a veterinarov; databazove ID sa mozu lisit. Kazde spustenie
pridava dalsie zaznamy, aj pri pouziti rovnakeho seedu.

Pripojenie sa nacita zo suboru `veterina\.env` (pozri cast 2). Iny subor
mozno zadat cez `--env-file`.

#### Ako generator funguje

[Generator](../generator/generate_data.py) postupuje takto:

1. Z databazy nacita existujucich majitelov a veterinarov. Z kazdeho musi
   existovat aspon jeden zaznam.
2. Metoda `Generator.generate()` nahodne vyberie majitela a veterinara a
   vytvori udaje zvierata, vysetrenia a faktury. Vysetrenia su z rokov
   2023 az 2026; faktury su splatne o 7 az 30 kalendarnych dni neskor.
3. Pre kazdy zaznam vlozi zviera, jeho zdravotnu kartu, vysetrenie a fakturu.
   Ziskane databazove ID pouzije na prepojenie tychto zaznamov.
4. Priblizne pri 60 % faktur vytvori platbu v plnej sume. Ostatne faktury
   zostanu bez platby. Udaje su umele a cast z nich moze vyhovovat filtru
   reportu v `3Index.sql`.
5. Zaznamy vklada po davkach najviac 1000, ale potvrdi ich az po dokonceni
   celeho generovania. Pri chybe pred potvrdenim sa nepotvrdene zmeny vratia.

#### Testy generatora

Z korena repozitara:

```powershell
.\generator\.venv\Scripts\python.exe -m unittest discover -s generator -p test_generate_data.py -v
```

Testy pouzivaju simulovane databazove pripojenie a nezapisuju do Oracle.

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
