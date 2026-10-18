
package sk.uniza.veterina.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import sk.uniza.veterina.model.OptionItem;
import sk.uniza.veterina.model.Vysetrenie;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class VysetrenieRepository {
    private final JdbcTemplate jdbc;

    public VysetrenieRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final String BASE = """
        SELECT v.id_vysetrenie, v.id_zdravotna_karta, z.meno zviera,
               v.id_veterinar, o.meno || ' ' || o.priezvisko veterinar,
               v.datum_cas_vysetrenia, v.diagnoza, v.popis_liecby,
               v.detaily_stavu.teplota_c teplota_c,
               v.detaily_stavu.srdecn_tep srdecny_tep,
               v.detaily_stavu.hmotnost_kg hmotnost_kg,
               v.detaily_stavu.zavaznost zavaznost
        FROM vysetrenie v
        JOIN zdravotna_karta zk ON zk.id_zdravotna_karta=v.id_zdravotna_karta
        JOIN zviera z ON z.id_zviera=zk.id_zviera
        JOIN veterinar vet ON vet.id_veterinar=v.id_veterinar
        JOIN osoba o ON o.id_osoba=vet.id_osoba
        """;

    public List<Vysetrenie> findAll() {
        return jdbc.query(BASE + " ORDER BY v.datum_cas_vysetrenia DESC",
                (rs, n) -> map(rs));
    }

    public Vysetrenie findById(Long id) {
        return jdbc.queryForObject(BASE + " WHERE v.id_vysetrenie=?",
                (rs, n) -> map(rs), id);
    }

    private Vysetrenie map(java.sql.ResultSet rs) throws java.sql.SQLException {

        int tepValue = rs.getInt("srdecny_tep");
        Integer tep = rs.wasNull() ? null : tepValue;

        return new Vysetrenie(
                rs.getLong("id_vysetrenie"),
                rs.getLong("id_zdravotna_karta"),
                rs.getString("zviera"),
                rs.getLong("id_veterinar"),
                rs.getString("veterinar"),
                rs.getTimestamp("datum_cas_vysetrenia").toLocalDateTime(),
                rs.getString("diagnoza"),
                rs.getString("popis_liecby"),
                rs.getBigDecimal("teplota_c"),
                tep,
                rs.getBigDecimal("hmotnost_kg"),
                rs.getString("zavaznost")
        );
    }

    public List<OptionItem> options() {
        return jdbc.query("""
            SELECT v.id_vysetrenie,
                   z.meno || ' - ' || TO_CHAR(v.datum_cas_vysetrenia, 'YYYY-MM-DD HH24:MI') || ' (#' || v.id_vysetrenie || ')' label
            FROM vysetrenie v
            JOIN zdravotna_karta zk ON zk.id_zdravotna_karta=v.id_zdravotna_karta
            JOIN zviera z ON z.id_zviera=zk.id_zviera
            ORDER BY v.datum_cas_vysetrenia DESC
            """, (rs, n) -> new OptionItem(
                rs.getLong("id_vysetrenie"),
                rs.getString("label")
            ));
    }

    public void insert(Long idKarta, Long idVeterinar, LocalDateTime datumCas,
                       String diagnoza, String popisLiecby,
                       BigDecimal teplota, Integer tep,
                       BigDecimal hmotnost, String zavaznost) {

        jdbc.update("""
            INSERT INTO vysetrenie
              (id_zdravotna_karta, id_veterinar, datum_cas_vysetrenia,
               diagnoza, popis_liecby, detaily_stavu)
            VALUES (?, ?, ?, ?, ?, t_zdravotny_stav(?,?,?,?))
            """,
            idKarta, idVeterinar, Timestamp.valueOf(datumCas),
            diagnoza, popisLiecby, teplota, tep, hmotnost, zavaznost
        );
    }

    public void update(Long id, Long idKarta, Long idVeterinar,
                       LocalDateTime datumCas, String diagnoza,
                       String popisLiecby, BigDecimal teplota,
                       Integer tep, BigDecimal hmotnost, String zavaznost) {

        jdbc.update("""
            UPDATE vysetrenie
            SET id_zdravotna_karta=?,
                id_veterinar=?,
                datum_cas_vysetrenia=?,
                diagnoza=?,
                popis_liecby=?,
                detaily_stavu=t_zdravotny_stav(?,?,?,?)
            WHERE id_vysetrenie=?
            """,
            idKarta, idVeterinar, Timestamp.valueOf(datumCas),
            diagnoza, popisLiecby, teplota, tep, hmotnost,
            zavaznost, id
        );
    }

    public void delete(Long id) {
        jdbc.update("DELETE FROM vysetrenie WHERE id_vysetrenie=?", id);
    }
}
