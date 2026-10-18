package sk.uniza.veterina.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import sk.uniza.veterina.model.Majitel;
import sk.uniza.veterina.model.OptionItem;

import java.util.List;

@Repository
public class MajitelRepository {
    private final JdbcTemplate jdbc;
    public MajitelRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    private static final String BASE = """
        SELECT m.id_majitel, m.id_osoba,
               o.meno || ' ' || o.priezvisko osoba,
               m.adresa.ulica ulica, m.adresa.mesto mesto,
               m.adresa.psc psc, m.adresa.stat stat
        FROM majitel m JOIN osoba o ON o.id_osoba = m.id_osoba
        """;

    public List<Majitel> findAll() {
        return jdbc.query(BASE + " ORDER BY m.id_majitel", (rs, n) -> map(rs));
    }

    public Majitel findById(Long id) {
        return jdbc.queryForObject(BASE + " WHERE m.id_majitel=?", (rs, n) -> map(rs), id);
    }

    private Majitel map(java.sql.ResultSet rs) throws java.sql.SQLException {
        return new Majitel(rs.getLong("id_majitel"), rs.getLong("id_osoba"), rs.getString("osoba"),
                rs.getString("ulica"), rs.getString("mesto"), rs.getString("psc"), rs.getString("stat"));
    }

    public List<OptionItem> options() {
        return jdbc.query("""
            SELECT m.id_majitel,
                   o.meno || ' ' || o.priezvisko || ' (#' || m.id_majitel || ')' label
            FROM majitel m JOIN osoba o ON o.id_osoba=m.id_osoba
            ORDER BY o.priezvisko, o.meno
            """, (rs, n) -> new OptionItem(rs.getLong("id_majitel"), rs.getString("label")));
    }

    public void insert(Long idOsoba, String ulica, String mesto, String psc, String stat) {
        jdbc.update("INSERT INTO majitel (id_osoba, adresa) VALUES (?, t_adresa(?,?,?,?))", idOsoba, ulica, mesto, psc, stat);
    }

    public void update(Long id, Long idOsoba, String ulica, String mesto, String psc, String stat) {
        jdbc.update("UPDATE majitel SET id_osoba=?, adresa=t_adresa(?,?,?,?) WHERE id_majitel=?", idOsoba, ulica, mesto, psc, stat, id);
    }

    public void delete(Long id) { jdbc.update("DELETE FROM majitel WHERE id_majitel=?", id); }
}
