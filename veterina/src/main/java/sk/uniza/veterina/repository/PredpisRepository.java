package sk.uniza.veterina.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import sk.uniza.veterina.model.Predpis;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Repository
public class PredpisRepository {
    private final JdbcTemplate jdbc;
    public PredpisRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    private static final String BASE = """
        SELECT p.id_predpis, p.id_vysetrenie, 'Vysetrenie #' || p.id_vysetrenie vysetrenie,
               p.nazov_lieku, p.datum_od, p.datum_do, p.poznamka
        FROM predpis p
        """;
    public List<Predpis> findAll() { return jdbc.query(BASE + " ORDER BY p.id_predpis", (rs, n) -> map(rs)); }
    public Predpis findById(Long id) { return jdbc.queryForObject(BASE + " WHERE p.id_predpis=?", (rs, n) -> map(rs), id); }
    private Predpis map(java.sql.ResultSet rs) throws java.sql.SQLException {
        Date od = rs.getDate("datum_od"); Date doD = rs.getDate("datum_do");
        return new Predpis(rs.getLong("id_predpis"), rs.getLong("id_vysetrenie"), rs.getString("vysetrenie"), rs.getString("nazov_lieku"),
                od == null ? null : od.toLocalDate(), doD == null ? null : doD.toLocalDate(), rs.getString("poznamka"));
    }
    public void insert(Long idVysetrenie, String nazov, LocalDate od, LocalDate doD, String poznamka) {
        jdbc.update("INSERT INTO predpis (id_vysetrenie, nazov_lieku, datum_od, datum_do, poznamka) VALUES (?,?,?,?,?)",
                idVysetrenie, nazov, od == null ? null : Date.valueOf(od), doD == null ? null : Date.valueOf(doD), poznamka);
    }
    public void update(Long id, Long idVysetrenie, String nazov, LocalDate od, LocalDate doD, String poznamka) {
        jdbc.update("UPDATE predpis SET id_vysetrenie=?, nazov_lieku=?, datum_od=?, datum_do=?, poznamka=? WHERE id_predpis=?",
                idVysetrenie, nazov, od == null ? null : Date.valueOf(od), doD == null ? null : Date.valueOf(doD), poznamka, id);
    }
    public void delete(Long id) { jdbc.update("DELETE FROM predpis WHERE id_predpis=?", id); }
}
