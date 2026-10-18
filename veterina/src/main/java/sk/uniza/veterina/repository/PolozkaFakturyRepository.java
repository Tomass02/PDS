package sk.uniza.veterina.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import sk.uniza.veterina.model.PolozkaFaktury;

import java.math.BigDecimal;
import java.util.List;

@Repository
public class PolozkaFakturyRepository {
    private final JdbcTemplate jdbc;
    public PolozkaFakturyRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    private static final String BASE = """
        SELECT p.id_polozka_faktury, p.id_faktura, 'Faktura #' || p.id_faktura faktura,
               p.nazov, p.mnozstvo, p.jednotkova_cena, p.sadzba_dph
        FROM polozka_faktury p
        """;
    public List<PolozkaFaktury> findAll() { return jdbc.query(BASE + " ORDER BY p.id_polozka_faktury", (rs, n) -> map(rs)); }
    public PolozkaFaktury findById(Long id) { return jdbc.queryForObject(BASE + " WHERE p.id_polozka_faktury=?", (rs, n) -> map(rs), id); }
    private PolozkaFaktury map(java.sql.ResultSet rs) throws java.sql.SQLException {
        return new PolozkaFaktury(rs.getLong("id_polozka_faktury"), rs.getLong("id_faktura"), rs.getString("faktura"),
                rs.getString("nazov"), rs.getBigDecimal("mnozstvo"), rs.getBigDecimal("jednotkova_cena"), rs.getBigDecimal("sadzba_dph"));
    }
    public void insert(Long idFaktura, String nazov, BigDecimal mnozstvo, BigDecimal cena, BigDecimal dph) {
        jdbc.update("INSERT INTO polozka_faktury (id_faktura, nazov, mnozstvo, jednotkova_cena, sadzba_dph) VALUES (?,?,?,?,?)", idFaktura, nazov, mnozstvo, cena, dph);
    }
    public void update(Long id, Long idFaktura, String nazov, BigDecimal mnozstvo, BigDecimal cena, BigDecimal dph) {
        jdbc.update("UPDATE polozka_faktury SET id_faktura=?, nazov=?, mnozstvo=?, jednotkova_cena=?, sadzba_dph=? WHERE id_polozka_faktury=?", idFaktura, nazov, mnozstvo, cena, dph, id);
    }
    public void delete(Long id) { jdbc.update("DELETE FROM polozka_faktury WHERE id_polozka_faktury=?", id); }
}
