package sk.uniza.veterina.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import sk.uniza.veterina.model.OptionItem;
import sk.uniza.veterina.model.Veterinar;

import java.util.List;

@Repository
public class VeterinarRepository {
    private final JdbcTemplate jdbc;
    public VeterinarRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    private static final String BASE = """
        SELECT v.id_veterinar, v.id_osoba, o.meno || ' ' || o.priezvisko osoba, v.specializacia
        FROM veterinar v JOIN osoba o ON o.id_osoba=v.id_osoba
        """;

    public List<Veterinar> findAll() {
        return jdbc.query(BASE + " ORDER BY v.id_veterinar", (rs, n) -> new Veterinar(rs.getLong("id_veterinar"), rs.getLong("id_osoba"), rs.getString("osoba"), rs.getString("specializacia")));
    }
    public Veterinar findById(Long id) {
        return jdbc.queryForObject(BASE + " WHERE v.id_veterinar=?", (rs, n) -> new Veterinar(rs.getLong("id_veterinar"), rs.getLong("id_osoba"), rs.getString("osoba"), rs.getString("specializacia")), id);
    }
    public List<OptionItem> options() {
        return jdbc.query("""
            SELECT v.id_veterinar, o.meno || ' ' || o.priezvisko || ' - ' || v.specializacia label
            FROM veterinar v JOIN osoba o ON o.id_osoba=v.id_osoba ORDER BY o.priezvisko, o.meno
            """, (rs, n) -> new OptionItem(rs.getLong("id_veterinar"), rs.getString("label")));
    }
    public void insert(Long idOsoba, String specializacia) {
        jdbc.update("INSERT INTO veterinar (id_osoba, specializacia) VALUES (?,?)", idOsoba, specializacia);
    }
    public void update(Long id, Long idOsoba, String specializacia) {
        jdbc.update("UPDATE veterinar SET id_osoba=?, specializacia=? WHERE id_veterinar=?", idOsoba, specializacia, id);
    }
    public void delete(Long id) { jdbc.update("DELETE FROM veterinar WHERE id_veterinar=?", id); }
}
