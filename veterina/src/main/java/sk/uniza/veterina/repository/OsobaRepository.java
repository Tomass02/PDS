package sk.uniza.veterina.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import sk.uniza.veterina.model.OptionItem;
import sk.uniza.veterina.model.Osoba;

import java.util.List;

@Repository
public class OsobaRepository {
    private final JdbcTemplate jdbc;
    public OsobaRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public List<Osoba> findAll() {
        return jdbc.query("SELECT id_osoba, meno, priezvisko, telefon, email FROM osoba ORDER BY id_osoba",
                (rs, n) -> new Osoba(rs.getLong("id_osoba"), rs.getString("meno"), rs.getString("priezvisko"), rs.getString("telefon"), rs.getString("email")));
    }

    public Osoba findById(Long id) {
        return jdbc.queryForObject("SELECT id_osoba, meno, priezvisko, telefon, email FROM osoba WHERE id_osoba=?",
                (rs, n) -> new Osoba(rs.getLong("id_osoba"), rs.getString("meno"), rs.getString("priezvisko"), rs.getString("telefon"), rs.getString("email")), id);
    }

    public List<OptionItem> options() {
        return jdbc.query("SELECT id_osoba, meno || ' ' || priezvisko label FROM osoba ORDER BY priezvisko, meno",
                (rs, n) -> new OptionItem(rs.getLong("id_osoba"), rs.getString("label")));
    }

    public void insert(String meno, String priezvisko, String telefon, String email) {
        jdbc.update("INSERT INTO osoba (meno, priezvisko, telefon, email) VALUES (?,?,?,?)", meno, priezvisko, telefon, email);
    }

    public void update(Long id, String meno, String priezvisko, String telefon, String email) {
        jdbc.update("UPDATE osoba SET meno=?, priezvisko=?, telefon=?, email=? WHERE id_osoba=?", meno, priezvisko, telefon, email, id);
    }

    public void delete(Long id) { jdbc.update("DELETE FROM osoba WHERE id_osoba=?", id); }
}
