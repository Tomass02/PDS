--VYCISTENIE
DELETE FROM predpis;
DELETE FROM platba;
DELETE FROM polozka_faktury;
DELETE FROM faktura;
DELETE FROM vysetrenie;
DELETE FROM zdravotna_karta;
DELETE FROM zviera;
DELETE FROM majitel;
DELETE FROM veterinar;
DELETE FROM osoba;
COMMIT;

-- 1. OSOBA 
-- VETERINAR
INSERT INTO osoba (id_osoba, meno, priezvisko, telefon, email) VALUES (1, 'Marek', 'Horváth', '+421901111111', 'm.horvath@vet.sk');
INSERT INTO osoba (id_osoba, meno, priezvisko, telefon, email) VALUES (2, 'Lucia', 'Kováčová', '+421902222222', 'l.kovacova@vet.sk');
INSERT INTO osoba (id_osoba, meno, priezvisko, telefon, email) VALUES (3, 'Peter', 'Varga', '+421903333333', 'p.varga@vet.sk');
INSERT INTO osoba (id_osoba, meno, priezvisko, telefon, email) VALUES (4, 'Zuzana', 'Novotná', '+421904444444', 'z.novotna@vet.sk');
INSERT INTO osoba (id_osoba, meno, priezvisko, telefon, email) VALUES (5, 'Michal', 'Baláž', '+421905555555', 'm.balaz@vet.sk');

INSERT INTO osoba (id_osoba, meno, priezvisko, telefon, email) VALUES (6, 'Ján', 'Tóth', '+421911100200', 'jan.toth@gmail.com');
INSERT INTO osoba (id_osoba, meno, priezvisko, telefon, email) VALUES (7, 'Katarína', 'Molnárová', '+421912200300', 'katka.m@azet.sk');
INSERT INTO osoba (id_osoba, meno, priezvisko, telefon, email) VALUES (8, 'Tomáš', 'Sloboda', '+421913300400', 'tomas.sloboda@centrum.sk');
INSERT INTO osoba (id_osoba, meno, priezvisko, telefon, email) VALUES (9, 'Veronika', 'Krausová', '+421914400500', 'v.krausova@gmail.com');
INSERT INTO osoba (id_osoba, meno, priezvisko, telefon, email) VALUES (10, 'Martin', 'Urbánek', '+421915500600', 'm.urbanek@gmail.com');
INSERT INTO osoba (id_osoba, meno, priezvisko, telefon, email) VALUES (11, 'Lenka', 'Bieliková', '+421916600700', 'lenka.bielik@gmail.com');
INSERT INTO osoba (id_osoba, meno, priezvisko, telefon, email) VALUES (12, 'Jakub', 'Gajdoš', '+421917700800', 'jgajdos@yahoo.com');
INSERT INTO osoba (id_osoba, meno, priezvisko, telefon, email) VALUES (13, 'Simona', 'Šimková', '+421918800900', 'simona.simkova@gmail.com');
INSERT INTO osoba (id_osoba, meno, priezvisko, telefon, email) VALUES (14, 'Filip', 'Chovan', '+421919900100', 'f.chovan@seznam.cz');
INSERT INTO osoba (id_osoba, meno, priezvisko, telefon, email) VALUES (15, 'Monika', 'Kollárová', '+421920000200', 'monika.kollarova@gmail.com');

-- 2. VETERINAR 
INSERT INTO veterinar (id_osoba, specializacia) VALUES (1, 'Chirurgia');
INSERT INTO veterinar (id_osoba, specializacia) VALUES (2, 'Dermatológia');
INSERT INTO veterinar (id_osoba, specializacia) VALUES (3, 'Stomatológia');
INSERT INTO veterinar (id_osoba, specializacia) VALUES (4, 'Kardiológia');
INSERT INTO veterinar (id_osoba, specializacia) VALUES (5, 'Všeobecná medicína');

-- 3. MAJITEL
INSERT INTO majitel (id_osoba, adresa) VALUES (6, t_adresa('Vysokoškolákov 12', 'Žilina', '01008', 'Slovensko'));
INSERT INTO majitel (id_osoba, adresa) VALUES (7, t_adresa('Májová 5', 'Bratislava', '81101', 'Slovensko'));
INSERT INTO majitel (id_osoba, adresa) VALUES (8, t_adresa('Hlavná 44', 'Košice', '04001', 'Slovensko'));
INSERT INTO majitel (id_osoba, adresa) VALUES (9, t_adresa('Záhradná 8', 'Banská Bystrica', '97401', 'Slovensko'));
INSERT INTO majitel (id_osoba, adresa) VALUES (10, t_adresa('Obchodná 15', 'Nitra', '94901', 'Slovensko'));
INSERT INTO majitel (id_osoba, adresa) VALUES (11, t_adresa('Školská 3', 'Trnava', '91701', 'Slovensko'));
INSERT INTO majitel (id_osoba, adresa) VALUES (12, t_adresa('Lesná 21', 'Martin', '03601', 'Slovensko'));
INSERT INTO majitel (id_osoba, adresa) VALUES (13, t_adresa('Kukučínova 7', 'Trenčín', '91101', 'Slovensko'));
INSERT INTO majitel (id_osoba, adresa) VALUES (14, t_adresa('Družstevná 19', 'Poprad', '05801', 'Slovensko'));
INSERT INTO majitel (id_osoba, adresa) VALUES (15, t_adresa('Ulica SNP 100', 'Prešov', '08001', 'Slovensko'));

-- 4. ZVIERA
INSERT INTO zviera (id_majitel, meno, druh, datum_narodenia, plemeno, fotografia) 
VALUES (1, 'Dunčo', 'Pes', DATE '2020-05-12', 'Nemecký ovčiak', EMPTY_BLOB());

INSERT INTO zviera (id_majitel, meno, druh, datum_narodenia, plemeno, fotografia) 
VALUES (2, 'Luna', 'Mačka', DATE '2021-08-20', 'Britská krátkosrstá', EMPTY_BLOB());

INSERT INTO zviera (id_majitel, meno, druh, datum_narodenia, plemeno, fotografia) 
VALUES (3, 'Max', 'Pes', DATE '2019-03-01', 'Labrador', EMPTY_BLOB());

INSERT INTO zviera (id_majitel, meno, druh, datum_narodenia, plemeno, fotografia) 
VALUES (4, 'Bella', 'Pes', DATE '2022-01-15', 'Jazvečík', EMPTY_BLOB());

INSERT INTO zviera (id_majitel, meno, druh, datum_narodenia, plemeno, fotografia) 
VALUES (5, 'Mica', 'Mačka', DATE '2020-11-10', 'Siamka', EMPTY_BLOB());

INSERT INTO zviera (id_majitel, meno, druh, datum_narodenia, plemeno, fotografia) 
VALUES (6, 'Boby', 'Králik', DATE '2022-06-05', 'Zakrpatený králik', EMPTY_BLOB());

INSERT INTO zviera (id_majitel, meno, druh, datum_narodenia, plemeno, fotografia) 
VALUES (7, 'Rocky', 'Pes', DATE '2018-09-30', 'Bígl', EMPTY_BLOB());

INSERT INTO zviera (id_majitel, meno, druh, datum_narodenia, plemeno, fotografia) 
VALUES (8, 'Charlie', 'Pes', DATE '2021-04-18', 'Zlatý retriever', EMPTY_BLOB());

INSERT INTO zviera (id_majitel, meno, druh, datum_narodenia, plemeno, fotografia) 
VALUES (9, 'Felix', 'Mačka', DATE '2023-02-14', 'Kríženec', EMPTY_BLOB());

INSERT INTO zviera (id_majitel, meno, druh, datum_narodenia, plemeno, fotografia) 
VALUES (10, 'Apo', 'Pes', DATE '2017-12-01', 'Kordobská doga', EMPTY_BLOB());

-- 5. ZDRAVOTNA_KARTA
INSERT INTO zdravotna_karta (id_zviera, datum_zalozenia, poznamka) VALUES (1, DATE '2020-05-15', 'Založená po narodení');
INSERT INTO zdravotna_karta (id_zviera, datum_zalozenia, poznamka) VALUES (2, DATE '2021-08-25', 'Založená pri prvej prehliadke');
INSERT INTO zdravotna_karta (id_zviera, datum_zalozenia, poznamka) VALUES (3, DATE '2019-03-10', 'Založená pri očkovaní');
INSERT INTO zdravotna_karta (id_zviera, datum_zalozenia, poznamka) VALUES (4, DATE '2022-01-20', 'Pravidelný pacient');
INSERT INTO zdravotna_karta (id_zviera, datum_zalozenia, poznamka) VALUES (5, DATE '2020-11-15', 'Bez osobitných rizík');
INSERT INTO zdravotna_karta (id_zviera, datum_zalozenia, poznamka) VALUES (6, DATE '2022-06-10', 'Bylinožravec, opatrnosť pri liekoch');
INSERT INTO zdravotna_karta (id_zviera, datum_zalozenia, poznamka) VALUES (7, DATE '2018-10-05', 'Starší pacient');
INSERT INTO zdravotna_karta (id_zviera, datum_zalozenia, poznamka) VALUES (8, DATE '2021-05-01', 'Aktívny pes, športový výcvik');
INSERT INTO zdravotna_karta (id_zviera, datum_zalozenia, poznamka) VALUES (9, DATE '2023-03-01', 'Mladá mačka');
INSERT INTO zdravotna_karta (id_zviera, datum_zalozenia, poznamka) VALUES (10, DATE '2018-01-10', 'Karta pacienta po úraze');

-- 6. VYSETRENIE
INSERT INTO vysetrenie (id_zdravotna_karta, id_veterinar, datum_cas_vysetrenia, diagnoza, popis_liecby, detaily_stavu)
VALUES (1, 1, TIMESTAMP '2024-01-10 09:00:00', 'Zápal zvukovodu', 'Aplikované ušné kvapky, kľudový režim', t_zdravotny_stav(38.8, 110, 28.5, 'Nízka'));

INSERT INTO vysetrenie (id_zdravotna_karta, id_veterinar, datum_cas_vysetrenia, diagnoza, popis_liecby, detaily_stavu)
VALUES (2, 2, TIMESTAMP '2024-01-12 10:30:00', 'Preventívna prehliadka', 'Očkovanie proti besnote', t_zdravotny_stav(38.2, 130, 4.2, 'Nízka'));

INSERT INTO vysetrenie (id_zdravotna_karta, id_veterinar, datum_cas_vysetrenia, diagnoza, popis_liecby, detaily_stavu)
VALUES (3, 1, TIMESTAMP '2024-01-15 14:15:00', 'Zlomenina labky', 'RTG vyšetrenie a sádrovanie', t_zdravotny_stav(39.1, 140, 32.0, 'Vysoká'));

INSERT INTO vysetrenie (id_zdravotna_karta, id_veterinar, datum_cas_vysetrenia, diagnoza, popis_liecby, detaily_stavu)
VALUES (4, 3, TIMESTAMP '2024-02-01 11:00:00', 'Zubný kameň', 'Čistenie zubov ultrazvukom', t_zdravotny_stav(38.5, 95, 8.1, 'Nízka'));

INSERT INTO vysetrenie (id_zdravotna_karta, id_veterinar, datum_cas_vysetrenia, diagnoza, popis_liecby, detaily_stavu)
VALUES (5, 2, TIMESTAMP '2024-02-05 15:45:00', 'Alergická reakcia', 'Podané antihistaminiká', t_zdravotny_stav(38.7, 125, 3.8, 'Stredná'));

INSERT INTO vysetrenie (id_zdravotna_karta, id_veterinar, datum_cas_vysetrenia, diagnoza, popis_liecby, detaily_stavu)
VALUES (6, 5, TIMESTAMP '2024-02-10 08:30:00', 'Tráviace ťažkosti', 'Diéta a probiotiká', t_zdravotny_stav(39.0, 150, 1.8, 'Stredná'));

INSERT INTO vysetrenie (id_zdravotna_karta, id_veterinar, datum_cas_vysetrenia, diagnoza, popis_liecby, detaily_stavu)
VALUES (7, 4, TIMESTAMP '2024-02-18 13:00:00', 'Aritmia', 'EKG vyšetrenie, predpísané lieky', t_zdravotny_stav(38.4, 160, 14.5, 'Vysoká'));

INSERT INTO vysetrenie (id_zdravotna_karta, id_veterinar, datum_cas_vysetrenia, diagnoza, popis_liecby, detaily_stavu)
VALUES (8, 1, TIMESTAMP '2024-03-02 16:20:00', 'Tržná rana', 'Šitie rany v lokálnej anestézii', t_zdravotny_stav(38.9, 115, 29.0, 'Stredná'));

INSERT INTO vysetrenie (id_zdravotna_karta, id_veterinar, datum_cas_vysetrenia, diagnoza, popis_liecby, detaily_stavu)
VALUES (9, 2, TIMESTAMP '2024-03-10 10:00:00', 'Odčervenie', 'Podaná odčervovacia tableta', t_zdravotny_stav(38.1, 120, 3.5, 'Nízka'));

INSERT INTO vysetrenie (id_zdravotna_karta, id_veterinar, datum_cas_vysetrenia, diagnoza, popis_liecby, detaily_stavu)
VALUES (10, 5, TIMESTAMP '2024-03-15 09:15:00', 'Rutinná kontrola', 'Kontrola celkového zdravotného stavu', t_zdravotny_stav(38.3, 100, 42.0, 'Nízka'));

-- 7. FAKTURA 
INSERT INTO faktura (id_vysetrenie, suma_celkom, datum_vystavenia, datum_splatnosti, stav_uhrady, scan_faktury)
VALUES (1, 45.00, DATE '2024-01-10', DATE '2024-01-24', 'UHRADENA', EMPTY_BLOB());

INSERT INTO faktura (id_vysetrenie, suma_celkom, datum_vystavenia, datum_splatnosti, stav_uhrady, scan_faktury)
VALUES (2, 30.00, DATE '2024-01-12', DATE '2024-01-26', 'UHRADENA', EMPTY_BLOB());

INSERT INTO faktura (id_vysetrenie, suma_celkom, datum_vystavenia, datum_splatnosti, stav_uhrady, scan_faktury)
VALUES (3, 180.00, DATE '2024-01-15', DATE '2024-01-29', 'UHRADENA', EMPTY_BLOB());

INSERT INTO faktura (id_vysetrenie, suma_celkom, datum_vystavenia, datum_splatnosti, stav_uhrady, scan_faktury)
VALUES (4, 75.00, DATE '2024-02-01', DATE '2024-02-15', 'UHRADENA', EMPTY_BLOB());

INSERT INTO faktura (id_vysetrenie, suma_celkom, datum_vystavenia, datum_splatnosti, stav_uhrady, scan_faktury)
VALUES (5, 50.00, DATE '2024-02-05', DATE '2024-02-19', 'NEUHRADENA', EMPTY_BLOB());

INSERT INTO faktura (id_vysetrenie, suma_celkom, datum_vystavenia, datum_splatnosti, stav_uhrady, scan_faktury)
VALUES (6, 35.00, DATE '2024-02-10', DATE '2024-02-24', 'UHRADENA', EMPTY_BLOB());

INSERT INTO faktura (id_vysetrenie, suma_celkom, datum_vystavenia, datum_splatnosti, stav_uhrady, scan_faktury)
VALUES (7, 120.00, DATE '2024-02-18', DATE '2024-03-03', 'NEUHRADENA', EMPTY_BLOB());

INSERT INTO faktura (id_vysetrenie, suma_celkom, datum_vystavenia, datum_splatnosti, stav_uhrady, scan_faktury)
VALUES (8, 90.00, DATE '2024-03-02', DATE '2024-03-16', 'UHRADENA', EMPTY_BLOB());

INSERT INTO faktura (id_vysetrenie, suma_celkom, datum_vystavenia, datum_splatnosti, stav_uhrady, scan_faktury)
VALUES (9, 20.00, DATE '2024-03-10', DATE '2024-03-24', 'UHRADENA', EMPTY_BLOB());

INSERT INTO faktura (id_vysetrenie, suma_celkom, datum_vystavenia, datum_splatnosti, stav_uhrady, scan_faktury)
VALUES (10, 25.00, DATE '2024-03-15', DATE '2024-03-29', 'UHRADENA', EMPTY_BLOB());

-- 8. POLOZKA_FAKTURY
INSERT INTO polozka_faktury (id_faktura, nazov, mnozstvo, jednotkova_cena, sadzba_dph) VALUES (1, 'Konzultácia a ošetrenie uší', 1, 45.00, 20.00);
INSERT INTO polozka_faktury (id_faktura, nazov, mnozstvo, jednotkova_cena, sadzba_dph) VALUES (2, 'Vstupné vyšetrenie a vakcína', 1, 30.00, 20.00);
INSERT INTO polozka_faktury (id_faktura, nazov, mnozstvo, jednotkova_cena, sadzba_dph) VALUES (3, 'RTG vyšetrenie a sadrovanie', 1, 180.00, 20.00);
INSERT INTO polozka_faktury (id_faktura, nazov, mnozstvo, jednotkova_cena, sadzba_dph) VALUES (4, 'Dentálna hygiena ultrazvukom', 1, 75.00, 20.00);
INSERT INTO polozka_faktury (id_faktura, nazov, mnozstvo, jednotkova_cena, sadzba_dph) VALUES (5, 'Aplikácia liečiv a pohotovosť', 1, 50.00, 20.00);
INSERT INTO polozka_faktury (id_faktura, nazov, mnozstvo, jednotkova_cena, sadzba_dph) VALUES (6, 'Konzultácia a diétny balíček', 1, 35.00, 20.00);
INSERT INTO polozka_faktury (id_faktura, nazov, mnozstvo, jednotkova_cena, sadzba_dph) VALUES (7, 'EKG vyšetrenie a diagnostika', 1, 120.00, 20.00);
INSERT INTO polozka_faktury (id_faktura, nazov, mnozstvo, jednotkova_cena, sadzba_dph) VALUES (8, 'Chirurgické šitie rany', 1, 90.00, 20.00);
INSERT INTO polozka_faktury (id_faktura, nazov, mnozstvo, jednotkova_cena, sadzba_dph) VALUES (9, 'Odčervovacia kúra', 1, 20.00, 20.00);
INSERT INTO polozka_faktury (id_faktura, nazov, mnozstvo, jednotkova_cena, sadzba_dph) VALUES (10, 'Preventívna prehliadka', 1, 25.00, 20.00);

-- 9. PLATBA 
INSERT INTO platba (id_faktura, datum_cas_platby, suma, sposob_platby, variabilny_symbol) VALUES (1, TIMESTAMP '2024-01-10 09:30:00', 45.00, 'KARTA', '2024011001');
INSERT INTO platba (id_faktura, datum_cas_platby, suma, sposob_platby, variabilny_symbol) VALUES (2, TIMESTAMP '2024-01-12 11:00:00', 30.00, 'HOTOVOSŤ', '2024011202');
INSERT INTO platba (id_faktura, datum_cas_platby, suma, sposob_platby, variabilny_symbol) VALUES (3, TIMESTAMP '2024-01-16 10:00:00', 180.00, 'PREVOD', '2024011503');
INSERT INTO platba (id_faktura, datum_cas_platby, suma, sposob_platby, variabilny_symbol) VALUES (4, TIMESTAMP '2024-02-01 11:30:00', 75.00, 'KARTA', '2024020104');
INSERT INTO platba (id_faktura, datum_cas_platby, suma, sposob_platby, variabilny_symbol) VALUES (5, TIMESTAMP '2024-02-06 12:00:00', 25.00, 'HOTOVOSŤ', '2024020505');
INSERT INTO platba (id_faktura, datum_cas_platby, suma, sposob_platby, variabilny_symbol) VALUES (6, TIMESTAMP '2024-02-10 09:00:00', 35.00, 'HOTOVOSŤ', '2024021006');
INSERT INTO platba (id_faktura, datum_cas_platby, suma, sposob_platby, variabilny_symbol) VALUES (7, TIMESTAMP '2024-02-19 14:00:00', 60.00, 'PREVOD', '2024021807');
INSERT INTO platba (id_faktura, datum_cas_platby, suma, sposob_platby, variabilny_symbol) VALUES (8, TIMESTAMP '2024-03-02 17:00:00', 90.00, 'KARTA', '2024030208');
INSERT INTO platba (id_faktura, datum_cas_platby, suma, sposob_platby, variabilny_symbol) VALUES (9, TIMESTAMP '2024-03-10 10:15:00', 20.00, 'HOTOVOSŤ', '2024031009');
INSERT INTO platba (id_faktura, datum_cas_platby, suma, sposob_platby, variabilny_symbol) VALUES (10, TIMESTAMP '2024-03-15 09:30:00', 25.00, 'KARTA', '2024031510');

-- PREDPIS 
INSERT INTO predpis (id_vysetrenie, nazov_lieku, datum_od, datum_do, poznamka) VALUES (1, 'Otibiovin kvapky', DATE '2024-01-10', DATE '2024-01-17', 'Podávať 3x denne po 2 kvapky do ucha');
INSERT INTO predpis (id_vysetrenie, nazov_lieku, datum_od, datum_do, poznamka) VALUES (2, 'NEXGARD žuvacia tableta', DATE '2024-01-12', DATE '2024-01-12', 'Jednorazovo na parazity');
INSERT INTO predpis (id_vysetrenie, nazov_lieku, datum_od, datum_do, poznamka) VALUES (3, 'Meloxidyl sirup', DATE '2024-01-15', DATE '2024-01-25', 'Proti bolesti, podávať raz denne');
INSERT INTO predpis (id_vysetrenie, nazov_lieku, datum_od, datum_do, poznamka) VALUES (3, 'Amoksiklav 250mg', DATE '2024-01-15', DATE '2024-01-22', 'Antibiotiká, 1 tableta po 12 hodín');
INSERT INTO predpis (id_vysetrenie, nazov_lieku, datum_od, datum_do, poznamka) VALUES (5, 'Zyrtec kvapky', DATE '2024-02-05', DATE '2024-02-12', '5 kvapiek ráno');
INSERT INTO predpis (id_vysetrenie, nazov_lieku, datum_od, datum_do, poznamka) VALUES (6, 'Canikur forte pasta', DATE '2024-02-10', DATE '2024-02-15', 'Podávať priamo do tlamy');
INSERT INTO predpis (id_vysetrenie, nazov_lieku, datum_od, datum_do, poznamka) VALUES (7, 'Fortekor 5mg', DATE '2024-02-18', DATE '2024-05-18', 'Užívanie na srdce, 1/2 tablety denne');
INSERT INTO predpis (id_vysetrenie, nazov_lieku, datum_od, datum_do, poznamka) VALUES (8, 'Betadine masť', DATE '2024-03-02', DATE '2024-03-09', 'Potierať ranu 2x denne');
INSERT INTO predpis (id_vysetrenie, nazov_lieku, datum_od, datum_do, poznamka) VALUES (9, 'Caniverm pasta', DATE '2024-03-10', DATE '2024-03-10', 'Jednorazová dávka');
INSERT INTO predpis (id_vysetrenie, nazov_lieku, datum_od, datum_do, poznamka) VALUES (10, 'Bravecto tableta', DATE '2024-03-15', DATE '2024-06-15', 'Ochrana proti kliešťom na 3 mesiace');
COMMIT;
SELECT * FROM osoba;
SELECT * FROM veterinar;
SELECT * FROM majitel;
SELECT * FROM zviera;
SELECT * FROM zdravotna_karta;
SELECT * FROM vysetrenie;   
SELECT * FROM faktura;
SELECT * FROM polozka_faktury;
SELECT * FROM platba;
SELECT * FROM predpis;
