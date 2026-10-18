package sk.uniza.veterina.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class ReportRepository {
    private final JdbcTemplate jdbc;
    public ReportRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public List<Map<String, Object>> objectReport() {
        return jdbc.queryForList("""
            SELECT v.id_vysetrenie, z.meno zviera,
                   v.detaily_stavu.teplota_c teplota_c,
                   v.detaily_stavu.srdecn_tep srdecny_tep,
                   v.detaily_stavu.hmotnost_kg hmotnost_kg,
                   v.detaily_stavu.zavaznost zavaznost,
                   v.detaily_stavu.vypocitaj_rizikovy_index() rizikovy_index
            FROM vysetrenie v
            JOIN zdravotna_karta zk ON zk.id_zdravotna_karta=v.id_zdravotna_karta
            JOIN zviera z ON z.id_zviera=zk.id_zviera
            ORDER BY v.detaily_stavu
            """);
    }

    public String jsonReport() {
        String result = jdbc.query("""
            SELECT JSON_ARRAYAGG(
                JSON_OBJECT(
                    'id' VALUE z.id_zviera,
                    'meno' VALUE z.meno,
                    'druh' VALUE z.druh,
                    'datum_narodenia' VALUE TO_CHAR(z.datum_narodenia, 'YYYY-MM-DD'),
                    'majitel' VALUE o.meno || ' ' || o.priezvisko
                ) RETURNING CLOB
            ) json_data
            FROM zviera z
            JOIN majitel m ON m.id_majitel=z.id_majitel
            JOIN osoba o ON o.id_osoba=m.id_osoba
            """, rs -> rs.next() ? rs.getString("json_data") : null);
        return result == null ? "[]" : result;
    }
}
