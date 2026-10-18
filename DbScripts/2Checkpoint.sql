SET SERVEROUTPUT ON;

-- ============================================================
-- CHECKPOINT doplnenie k 1CreateDbSchema.sql
-- 1) implementacia metod objektoveho typu t_zdravotny_stav
-- 2) vzorove data do vsetkych tabuliek
-- 3) PL/SQL procedura
-- 4) ukazkove vystupy: objektova metoda + JSON z relacnych dat
-- ============================================================

CREATE OR REPLACE TYPE BODY t_zdravotny_stav AS
    CONSTRUCTOR FUNCTION t_zdravotny_stav (
        p_teplota_c   NUMBER,
        p_srdecn_tep  NUMBER,
        p_hmotnost_kg NUMBER,
        p_zavaznost   VARCHAR2
    ) RETURN SELF AS RESULT IS
    BEGIN
        SELF.teplota_c   := p_teplota_c;
        SELF.srdecn_tep  := p_srdecn_tep;
        SELF.hmotnost_kg := p_hmotnost_kg;
        SELF.zavaznost   := p_zavaznost;
        RETURN;
    END;

    -- Demonstračný index pre potreby semestrálnej práce.
    -- Nie je určený na reálne veterinárne rozhodovanie.
    MEMBER FUNCTION vypocitaj_rizikovy_index RETURN NUMBER IS
        v_index NUMBER := 0;
    BEGIN
        v_index := CASE LOWER(NVL(SELF.zavaznost, ''))
            WHEN 'nizka'    THEN 10
            WHEN 'stredna'  THEN 30
            WHEN 'vysoka'   THEN 60
            WHEN 'kriticka' THEN 90
            ELSE 0
        END;

        IF SELF.teplota_c IS NOT NULL AND (SELF.teplota_c < 37 OR SELF.teplota_c > 40) THEN
            v_index := v_index + 10;
        END IF;

        IF SELF.srdecn_tep IS NOT NULL AND SELF.srdecn_tep > 140 THEN
            v_index := v_index + 10;
        END IF;

        RETURN LEAST(v_index, 100);
    END;

    MEMBER FUNCTION to_json_string RETURN CLOB IS
        v_json CLOB;
    BEGIN
        SELECT JSON_OBJECT(
                   'teplota_c' VALUE SELF.teplota_c,
                   'srdecny_tep' VALUE SELF.srdecn_tep,
                   'hmotnost_kg' VALUE SELF.hmotnost_kg,
                   'zavaznost' VALUE SELF.zavaznost,
                   'rizikovy_index' VALUE SELF.vypocitaj_rizikovy_index()
                   RETURNING CLOB
               )
        INTO v_json
        FROM dual;

        RETURN v_json;
    END;

    MAP MEMBER FUNCTION sort_key RETURN NUMBER IS
    BEGIN
        RETURN SELF.vypocitaj_rizikovy_index();
    END;
END;
/

-- ----------------------------
-- Vzorove data
-- ----------------------------

INSERT INTO osoba (id_osoba, meno, priezvisko, telefon, email)
VALUES (1, 'Jana', 'Novakova', '+421900111222', 'jana.novakova@example.com');
INSERT INTO osoba (id_osoba, meno, priezvisko, telefon, email)
VALUES (2, 'Peter', 'Mravec', '+421900333444', 'peter.mravec@example.com');
INSERT INTO osoba (id_osoba, meno, priezvisko, telefon, email)
VALUES (3, 'Lucia', 'Vetrová', '+421900555666', 'lucia.vetrova@veterina.sk');
INSERT INTO osoba (id_osoba, meno, priezvisko, telefon, email)
VALUES (4, 'Martin', 'Kovac', '+421900777888', 'martin.kovac@veterina.sk');

INSERT INTO majitel (id_majitel, id_osoba, adresa)
VALUES (1, 1, t_adresa('Hlavna 10', 'Zilina', '01001', 'Slovensko'));
INSERT INTO majitel (id_majitel, id_osoba, adresa)
VALUES (2, 2, t_adresa('Kvetna 7', 'Martin', '03601', 'Slovensko'));

INSERT INTO veterinar (id_veterinar, id_osoba, specializacia)
VALUES (1, 3, 'Mala zvierata');
INSERT INTO veterinar (id_veterinar, id_osoba, specializacia)
VALUES (2, 4, 'Chirurgia');

INSERT INTO zviera (id_zviera, id_majitel, meno, druh, datum_narodenia, plemeno)
VALUES (1, 1, 'Beny', 'Pes', DATE '2020-05-10', 'Labrador');
INSERT INTO zviera (id_zviera, id_majitel, meno, druh, datum_narodenia, plemeno)
VALUES (2, 2, 'Mica', 'Macka', DATE '2022-03-14', 'Britska kratkosrsta');
-- Zvieratko bez zavislych zaznamov je vhodne na demonstraciu DELETE v GUI.
INSERT INTO zviera (id_zviera, id_majitel, meno, druh, datum_narodenia, plemeno)
VALUES (3, 1, 'Riki', 'Kralik', DATE '2024-01-20', NULL);

INSERT INTO zdravotna_karta (id_zdravotna_karta, id_zviera, datum_zalozenia, poznamka)
VALUES (1, 1, DATE '2024-01-10', 'Pravidelne kontroly');
INSERT INTO zdravotna_karta (id_zdravotna_karta, id_zviera, datum_zalozenia, poznamka)
VALUES (2, 2, DATE '2024-06-01', 'Bez alergii');

INSERT INTO vysetrenie (
    id_vysetrenie, id_zdravotna_karta, id_veterinar,
    datum_cas_vysetrenia, diagnoza, popis_liecby, detaily_stavu
) VALUES (
    1, 1, 1, TIMESTAMP '2026-10-10 09:30:00',
    'Zapalen ucha', 'Kvapky 2x denne',
    t_zdravotny_stav(38.7, 95, 28.40, 'nizka')
);

INSERT INTO vysetrenie (
    id_vysetrenie, id_zdravotna_karta, id_veterinar,
    datum_cas_vysetrenia, diagnoza, popis_liecby, detaily_stavu
) VALUES (
    2, 2, 2, TIMESTAMP '2026-10-12 14:15:00',
    'Dehydratacia', 'Infuzia a kontrola o 2 dni',
    t_zdravotny_stav(40.4, 155, 4.80, 'vysoka')
);

INSERT INTO faktura (id_faktura, id_vysetrenie, suma_celkom, datum_vystavenia, datum_splatnosti, stav_uhrady)
VALUES (1, 1, 45.00, DATE '2026-10-10', DATE '2026-10-24', 'UHRADENA');
INSERT INTO faktura (id_faktura, id_vysetrenie, suma_celkom, datum_vystavenia, datum_splatnosti, stav_uhrady)
VALUES (2, 2, 88.50, DATE '2026-10-12', DATE '2026-10-26', 'NEUHRADENA');

INSERT INTO polozka_faktury (id_polozka_faktury, id_faktura, nazov, mnozstvo, jednotkova_cena, sadzba_dph)
VALUES (1, 1, 'Vysetrenie', 1, 25.00, 23);
INSERT INTO polozka_faktury (id_polozka_faktury, id_faktura, nazov, mnozstvo, jednotkova_cena, sadzba_dph)
VALUES (2, 1, 'Lieky', 1, 20.00, 23);
INSERT INTO polozka_faktury (id_polozka_faktury, id_faktura, nazov, mnozstvo, jednotkova_cena, sadzba_dph)
VALUES (3, 2, 'Vysetrenie a infuzia', 1, 88.50, 23);

INSERT INTO platba (id_platba, id_faktura, datum_cas_platby, suma, sposob_platby, variabilny_symbol)
VALUES (1, 1, TIMESTAMP '2026-10-10 10:05:00', 45.00, 'KARTA', '202610001');

INSERT INTO predpis (id_predpis, id_vysetrenie, nazov_lieku, datum_od, datum_do, poznamka)
VALUES (1, 1, 'Otodine', DATE '2026-10-10', DATE '2026-10-17', '2x denne');
INSERT INTO predpis (id_predpis, id_vysetrenie, nazov_lieku, datum_od, datum_do, poznamka)
VALUES (2, 2, 'Rehydratacny roztok', DATE '2026-10-12', DATE '2026-10-14', 'Podla odporucania veterinara');

COMMIT;

-- ----------------------------
-- PL/SQL: jednoducha procedura
-- ----------------------------
CREATE OR REPLACE PROCEDURE p_vypis_zviera (
    p_id_zviera IN zviera.id_zviera%TYPE
) IS
    v_meno    zviera.meno%TYPE;
    v_druh    zviera.druh%TYPE;
    v_majitel VARCHAR2(120);
BEGIN
    SELECT z.meno,
           z.druh,
           o.meno || ' ' || o.priezvisko
    INTO v_meno, v_druh, v_majitel
    FROM zviera z
    JOIN majitel m ON m.id_majitel = z.id_majitel
    JOIN osoba o ON o.id_osoba = m.id_osoba
    WHERE z.id_zviera = p_id_zviera;

    DBMS_OUTPUT.PUT_LINE(v_meno || ' (' || v_druh || '), majitel: ' || v_majitel);
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        DBMS_OUTPUT.PUT_LINE('Zvieratko s danym ID neexistuje.');
END;
/

-- ----------------------------
-- VYSTUP 1: objektovy typ + volanie member metody
-- ----------------------------
SELECT v.id_vysetrenie,
       z.meno AS zviera,
       v.diagnoza,
       v.detaily_stavu.teplota_c AS teplota_c,
       v.detaily_stavu.srdecn_tep AS srdecny_tep,
       v.detaily_stavu.zavaznost AS zavaznost,
       v.detaily_stavu.vypocitaj_rizikovy_index() AS rizikovy_index
FROM vysetrenie v
JOIN zdravotna_karta zk ON zk.id_zdravotna_karta = v.id_zdravotna_karta
JOIN zviera z ON z.id_zviera = zk.id_zviera
ORDER BY v.detaily_stavu;

-- ----------------------------
-- VYSTUP 2: JSON dokument generovany z relacnych dat
-- ----------------------------
SELECT JSON_ARRAYAGG(
           JSON_OBJECT(
               'id' VALUE id_zviera,
               'meno' VALUE meno,
               'druh' VALUE druh,
               'datum_narodenia' VALUE TO_CHAR(datum_narodenia, 'YYYY-MM-DD'),
               'majitel' VALUE majitel
           )
           RETURNING CLOB
       ) AS json_report
FROM (
    SELECT z.id_zviera,
           z.meno,
           z.druh,
           z.datum_narodenia,
           o.meno || ' ' || o.priezvisko AS majitel
    FROM zviera z
    JOIN majitel m ON m.id_majitel = z.id_majitel
    JOIN osoba o ON o.id_osoba = m.id_osoba
    ORDER BY z.id_zviera
);

BEGIN
    p_vypis_zviera(1);
END;
/
