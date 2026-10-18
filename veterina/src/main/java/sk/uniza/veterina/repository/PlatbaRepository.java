package sk.uniza.veterina.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import sk.uniza.veterina.model.Platba;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class PlatbaRepository {
    private final JdbcTemplate jdbc;
    public PlatbaRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    private static final String BASE = """
        SELECT p.id_platba, p.id_faktura, 'Faktura #' || p.id_faktura faktura,
               p.datum_cas_platby, p.suma, p.sposob_platby, p.variabilny_symbol
        FROM platba p
        """;
    public List<Platba> findAll() { return jdbc.query(BASE + " ORDER BY p.datum_cas_platby DESC", (rs, n) -> map(rs)); }
    public Platba findById(Long id) { return jdbc.queryForObject(BASE + " WHERE p.id_platba=?", (rs, n) -> map(rs), id); }
    private Platba map(java.sql.ResultSet rs) throws java.sql.SQLException {
        return new Platba(rs.getLong("id_platba"), rs.getLong("id_faktura"), rs.getString("faktura"),
                rs.getTimestamp("datum_cas_platby").toLocalDateTime(), rs.getBigDecimal("suma"), rs.getString("sposob_platby"), rs.getString("variabilny_symbol"));
    }
    public void insert(Long idFaktura, LocalDateTime datum, BigDecimal suma, String sposob, String vs) {
        jdbc.update("INSERT INTO platba (id_faktura, datum_cas_platby, suma, sposob_platby, variabilny_symbol) VALUES (?,?,?,?,?)", idFaktura, Timestamp.valueOf(datum), suma, sposob, vs);
    }
    public void update(Long id, Long idFaktura, LocalDateTime datum, BigDecimal suma, String sposob, String vs) {
        jdbc.update("UPDATE platba SET id_faktura=?, datum_cas_platby=?, suma=?, sposob_platby=?, variabilny_symbol=? WHERE id_platba=?", idFaktura, Timestamp.valueOf(datum), suma, sposob, vs, id);
    }
    public void delete(Long id) { jdbc.update("DELETE FROM platba WHERE id_platba=?", id); }
}
