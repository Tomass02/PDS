-- Spustaj ako skript v SQLcl, SQL*Plus alebo SQL Developer (F5).
-- Experiment je urceny pre testovaciu schemu vytvorenu predoslymi skriptmi.
-- Priprav aspon 100 000 faktur so suvisiacimi zaznamami a datumami
-- rozlozenymi do viacerych rokov. Povodne indexy zo schemy ponechaj.
-- Tri experimentalne indexy nizsie pred prvym meranim nesmu existovat.
-- Na zobrazenie planu su potrebne opravnenia k V$SQL a pre DBMS_XPLAN.
-- DDL prikazy CREATE/DROP vykonavaju implicitny COMMIT.
-- Ak skript skonci chybou po vytvoreni indexu, odstran ho rucne.
WHENEVER SQLERROR EXIT SQL.SQLCODE
SET SERVEROUTPUT ON SIZE UNLIMITED
SET TIMING ON

-- 1. Procedura: 10 majitelov s najvyssou sumou faktur splatnych v roku
-- 2025, ku ktorym nie je evidovana ziadna platba.
-- Faktury s ciastocnou platbou nie su zahrnute.
CREATE OR REPLACE PROCEDURE p_vypis_top_majitelov IS
BEGIN
    DBMS_OUTPUT.PUT_LINE(
        'ID | Majitel | Pocet faktur | Pocet zvierat | Suma | Najstarsia splatnost'
    );

    FOR r IN (
        SELECT /*+ GATHER_PLAN_STATISTICS QB_NAME(report_top_majitelia) */
               m.id_majitel,
               o.meno,
               o.priezvisko,
               COUNT(*) AS pocet_faktur,
               COUNT(DISTINCT z.id_zviera) AS pocet_zvierat,
               SUM(f.suma_celkom) AS celkova_suma,
               MIN(f.datum_splatnosti) AS najstarsia_splatnost
        FROM faktura f
        JOIN vysetrenie v
            ON v.id_vysetrenie = f.id_vysetrenie
        JOIN zdravotna_karta zk
            ON zk.id_zdravotna_karta = v.id_zdravotna_karta
        JOIN zviera z
            ON z.id_zviera = zk.id_zviera
        JOIN majitel m
            ON m.id_majitel = z.id_majitel
        JOIN osoba o
            ON o.id_osoba = m.id_osoba
        WHERE f.datum_splatnosti >= DATE '2025-01-01'
          AND f.datum_splatnosti <  DATE '2026-01-01'
          AND NOT EXISTS (
              SELECT 1
              FROM platba p
              WHERE p.id_faktura = f.id_faktura
          )
        GROUP BY m.id_majitel, o.meno, o.priezvisko
        ORDER BY celkova_suma DESC, m.id_majitel
        FETCH FIRST 10 ROWS ONLY
    ) LOOP
        DBMS_OUTPUT.PUT_LINE(
            r.id_majitel || ' | ' || r.meno || ' ' || r.priezvisko
            || ' | ' || r.pocet_faktur
            || ' | ' || r.pocet_zvierat
            || ' | ' || TO_CHAR(r.celkova_suma, 'TM9')
            || ' | ' || TO_CHAR(r.najstarsia_splatnost, 'YYYY-MM-DD')
        );
    END LOOP;
END;
/
SHOW ERRORS PROCEDURE p_vypis_top_majitelov

-- Posledny kurzor po volani procedury moze byt PL/SQL blok, nie jej SELECT.
-- Preto najdeme SQL_ID a cislo child kurzora podla znacky v SELECT-e.
CREATE OR REPLACE PROCEDURE p_vypis_plan_reportu AUTHID CURRENT_USER IS
    v_sql_id       VARCHAR2(13);
    v_child_number NUMBER;
BEGIN
    EXECUTE IMMEDIATE q'[
        SELECT sql_id, child_number
        FROM (
            SELECT sql_id, child_number
            FROM v$sql
            WHERE parsing_schema_name = SYS_CONTEXT('USERENV', 'CURRENT_SCHEMA')
              AND INSTR(UPPER(sql_text), 'QB_NAME(REPORT_TOP_' || 'MAJITELIA)') > 0
            ORDER BY last_active_time DESC, child_number DESC
        )
        WHERE ROWNUM = 1
    ]' INTO v_sql_id, v_child_number;

    DBMS_OUTPUT.PUT_LINE('SQL_ID: ' || v_sql_id || ', CHILD: ' || v_child_number);
    FOR r IN (
        SELECT plan_table_output
        FROM TABLE(DBMS_XPLAN.DISPLAY_CURSOR(v_sql_id, v_child_number, 'ALLSTATS LAST +COST'))
    ) LOOP
        DBMS_OUTPUT.PUT_LINE(r.plan_table_output);
    END LOOP;
END;
/
SHOW ERRORS PROCEDURE p_vypis_plan_reportu

-- 2. Kontrola objemu dat a statistiky pred vsetkymi meraniami.
SELECT COUNT(*) AS pocet_faktur FROM faktura;
SELECT COUNT(*) AS pocet_faktur_v_obdobi
FROM faktura
WHERE datum_splatnosti >= DATE '2025-01-01'
  AND datum_splatnosti <  DATE '2026-01-01';
SELECT COUNT(*) AS pocet_faktur_bez_platby
FROM faktura f
WHERE f.datum_splatnosti >= DATE '2025-01-01'
  AND f.datum_splatnosti <  DATE '2026-01-01'
  AND NOT EXISTS (
      SELECT 1 FROM platba p WHERE p.id_faktura = f.id_faktura
  );

BEGIN
    DBMS_STATS.GATHER_TABLE_STATS(USER, 'FAKTURA', cascade => TRUE);
    DBMS_STATS.GATHER_TABLE_STATS(USER, 'VYSETRENIE', cascade => TRUE);
    DBMS_STATS.GATHER_TABLE_STATS(USER, 'ZDRAVOTNA_KARTA', cascade => TRUE);
    DBMS_STATS.GATHER_TABLE_STATS(USER, 'ZVIERA', cascade => TRUE);
    DBMS_STATS.GATHER_TABLE_STATS(USER, 'MAJITEL', cascade => TRUE);
    DBMS_STATS.GATHER_TABLE_STATS(USER, 'OSOBA', cascade => TRUE);
    DBMS_STATS.GATHER_TABLE_STATS(USER, 'PLATBA', cascade => TRUE);
END;
/

-- 3. Vyhodiskovy stav bez experimentálnych indexov.
PROMPT === VYCHODISKOVY STAV ===
EXEC p_vypis_top_majitelov;
EXEC p_vypis_plan_reportu;

-- 4. Index faktury: zacina filtrovanou splatnostou a obsahuje vsetky
-- stlpce faktury pouzite v reporte. Uz existujuci index na stav a splatnost
-- zacina stavom, ktory tento report nefiltruje.
CREATE INDEX faktura_splatnost_report_ix
    ON faktura (datum_splatnosti, id_vysetrenie, id_faktura, suma_celkom);

PROMPT === INDEX FAKTURA ===
EXEC p_vypis_top_majitelov;
EXEC p_vypis_plan_reportu;
DROP INDEX faktura_splatnost_report_ix;

-- 5. Index vysetrenia: obsahuje kluc spojenia s fakturou aj kartu potrebnu
-- pre dalsie spojenie. Primarny kluc na id_vysetrenie uz existuje, takze
-- prinosom by mohol byt iba pristup bez citania tabulky vysetrenie.
CREATE INDEX vysetrenie_report_ix
    ON vysetrenie (id_vysetrenie, id_zdravotna_karta);

PROMPT === INDEX VYSETRENIE ===
EXEC p_vypis_top_majitelov;
EXEC p_vypis_plan_reportu;
DROP INDEX vysetrenie_report_ix;

-- 6. Index zvierata: obsahuje kluc spojenia s kartou aj majitela potrebneho
-- pre dalsie spojenie. Primarny kluc na id_zviera uz existuje.
CREATE INDEX zviera_report_ix
    ON zviera (id_zviera, id_majitel);

PROMPT === INDEX ZVIERA ===
EXEC p_vypis_top_majitelov;
EXEC p_vypis_plan_reportu;
DROP INDEX zviera_report_ix;

-- Kazdy index sa meria samostatne proti rovnakemu vychodiskovemu stavu.
-- Zaznamenaj celkovu odhadovanu cenu planu, Buffers, A-Rows a A-Time.
-- Vysledky reportu sa nesmu zmenit. Merania opakuj za rovnakych podmienok.
-- SET TIMING meria cele volanie vratane vypisu, nie iba samotny SELECT.
-- Plan sa hlada medzi kurzormi aktualnej schemy, preto experiment vykonavaj
-- bez sucasneho spustania tohto reportu v inych relaciach.