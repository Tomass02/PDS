package sk.uniza.veterina.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import sk.uniza.veterina.model.OptionItem;
import sk.uniza.veterina.model.ZdravotnaKarta;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Repository
public class ZdravotnaKartaRepository {
    private final JdbcTemplate jdbc;
    public ZdravotnaKartaRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    private static final String BASE = """
        SELECT zk.id_zdravotna_karta, zk.id_zviera, z.meno zviera, zk.datum_zalozenia, zk.poznamka
        FROM zdravotna_karta zk JOIN zviera z ON z.id_zviera=zk.id_zviera
        """;

    public List<ZdravotnaKarta> findAll() {
        return jdbc.query(BASE + " ORDER BY zk.id_zdravotna_karta", (rs, n) -> map(rs));
    }
    public ZdravotnaKarta findById(Long id) {
        return jdbc.queryForObject(BASE + " WHERE zk.id_zdravotna_karta=?", (rs, n) -> map(rs), id);
    }
    private ZdravotnaKarta map(java.sql.ResultSet rs) throws java.sql.SQLException {
        return new ZdravotnaKarta(rs.getLong("id_zdravotna_karta"), rs.getLong("id_zviera"), rs.getString("zviera"), rs.getDate("datum_zalozenia").toLocalDate(), rs.getString("poznamka"));
    }
    public List<OptionItem> options() {
        return jdbc.query("""
            SELECT zk.id_zdravotna_karta, z.meno || ' - karta #' || zk.id_zdravotna_karta label
            FROM zdravotna_karta zk JOIN zviera z ON z.id_zviera=zk.id_zviera ORDER BY z.meno
            """, (rs, n) -> new OptionItem(rs.getLong("id_zdravotna_karta"), rs.getString("label")));
    }
    public void insert(Long idZviera, LocalDate datum, String poznamka) {
        jdbc.update("INSERT INTO zdravotna_karta (id_zviera, datum_zalozenia, poznamka) VALUES (?,?,?)", idZviera, Date.valueOf(datum), poznamka);
    }
    public void update(Long id, Long idZviera, LocalDate datum, String poznamka) {
        jdbc.update("UPDATE zdravotna_karta SET id_zviera=?, datum_zalozenia=?, poznamka=? WHERE id_zdravotna_karta=?", idZviera, Date.valueOf(datum), poznamka, id);
    }
    public void delete(Long id) { jdbc.update("DELETE FROM zdravotna_karta WHERE id_zdravotna_karta=?", id); }
}
