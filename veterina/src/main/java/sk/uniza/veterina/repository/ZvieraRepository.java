package sk.uniza.veterina.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import sk.uniza.veterina.model.OptionItem;
import sk.uniza.veterina.model.Zviera;

import java.io.ByteArrayInputStream;
import java.sql.Date;
import java.sql.Types;
import java.time.LocalDate;
import java.util.List;

@Repository
public class ZvieraRepository {
    private final JdbcTemplate jdbc;
    public ZvieraRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    private static final String BASE = """
        SELECT z.id_zviera, z.id_majitel,
               o.meno || ' ' || o.priezvisko majitel,
               z.meno, z.druh, z.datum_narodenia, z.plemeno,
               CASE WHEN z.fotografia IS NULL THEN 0 ELSE 1 END ma_fotografiu
        FROM zviera z
        JOIN majitel m ON m.id_majitel=z.id_majitel
        JOIN osoba o ON o.id_osoba=m.id_osoba
        """;

    public List<Zviera> findAll() {
        return jdbc.query(BASE + " ORDER BY z.id_zviera", (rs, n) -> map(rs));
    }
    public Zviera findById(Long id) {
        return jdbc.queryForObject(BASE + " WHERE z.id_zviera=?", (rs, n) -> map(rs), id);
    }
    private Zviera map(java.sql.ResultSet rs) throws java.sql.SQLException {
        return new Zviera(rs.getLong("id_zviera"), rs.getLong("id_majitel"), rs.getString("majitel"), rs.getString("meno"), rs.getString("druh"), rs.getDate("datum_narodenia").toLocalDate(), rs.getString("plemeno"), rs.getInt("ma_fotografiu") == 1);
    }
    public List<OptionItem> options() {
        return jdbc.query("SELECT id_zviera, meno || ' (' || druh || ')' label FROM zviera ORDER BY meno",
                (rs, n) -> new OptionItem(rs.getLong("id_zviera"), rs.getString("label")));
    }
    public void insert(Long idMajitel, String meno, String druh, LocalDate datum, String plemeno, byte[] foto) {
        jdbc.update(conn -> {
            var ps = conn.prepareStatement("INSERT INTO zviera (id_majitel, meno, druh, datum_narodenia, plemeno, fotografia) VALUES (?,?,?,?,?,?)");
            ps.setLong(1, idMajitel); ps.setString(2, meno); ps.setString(3, druh);
            ps.setDate(4, Date.valueOf(datum)); ps.setString(5, plemeno);
            if (foto == null) ps.setNull(6, Types.BLOB); else ps.setBlob(6, new ByteArrayInputStream(foto));
            return ps;
        });
    }
    public void update(Long id, Long idMajitel, String meno, String druh, LocalDate datum, String plemeno, byte[] foto) {
        if (foto != null && foto.length > 0) {
            jdbc.update(conn -> {
                var ps = conn.prepareStatement("UPDATE zviera SET id_majitel=?, meno=?, druh=?, datum_narodenia=?, plemeno=?, fotografia=? WHERE id_zviera=?");
                ps.setLong(1, idMajitel); ps.setString(2, meno); ps.setString(3, druh);
                ps.setDate(4, Date.valueOf(datum)); ps.setString(5, plemeno);
                ps.setBlob(6, new ByteArrayInputStream(foto)); ps.setLong(7, id);
                return ps;
            });
        } else {
            jdbc.update("UPDATE zviera SET id_majitel=?, meno=?, druh=?, datum_narodenia=?, plemeno=? WHERE id_zviera=?",
                    idMajitel, meno, druh, Date.valueOf(datum), plemeno, id);
        }
    }
    public void delete(Long id) { jdbc.update("DELETE FROM zviera WHERE id_zviera=?", id); }
}
