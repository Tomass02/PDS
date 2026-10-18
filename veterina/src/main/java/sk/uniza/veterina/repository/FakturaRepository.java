package sk.uniza.veterina.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import sk.uniza.veterina.model.Faktura;
import sk.uniza.veterina.model.OptionItem;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Types;
import java.time.LocalDate;
import java.util.List;

@Repository
public class FakturaRepository {
    private final JdbcTemplate jdbc;
    public FakturaRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    private static final String BASE = """
        SELECT f.id_faktura, f.id_vysetrenie,
               'Vysetrenie #' || f.id_vysetrenie vysetrenie,
               f.suma_celkom, f.datum_vystavenia, f.datum_splatnosti, f.stav_uhrady,
               CASE WHEN f.scan_faktury IS NULL THEN 0 ELSE 1 END ma_scan
        FROM faktura f
        """;

    public List<Faktura> findAll() { return jdbc.query(BASE + " ORDER BY f.id_faktura", (rs, n) -> map(rs)); }
    public Faktura findById(Long id) { return jdbc.queryForObject(BASE + " WHERE f.id_faktura=?", (rs, n) -> map(rs), id); }
    private Faktura map(java.sql.ResultSet rs) throws java.sql.SQLException {
        return new Faktura(rs.getLong("id_faktura"), rs.getLong("id_vysetrenie"), rs.getString("vysetrenie"),
                rs.getBigDecimal("suma_celkom"), rs.getDate("datum_vystavenia").toLocalDate(),
                rs.getDate("datum_splatnosti").toLocalDate(), rs.getString("stav_uhrady"), rs.getInt("ma_scan") == 1);
    }
    public List<OptionItem> options() {
        return jdbc.query("SELECT id_faktura, 'Faktura #' || id_faktura || ' - ' || TO_CHAR(suma_celkom, 'FM9999990D00') || ' EUR' label FROM faktura ORDER BY id_faktura DESC",
                (rs, n) -> new OptionItem(rs.getLong("id_faktura"), rs.getString("label")));
    }
    public void insert(Long idVysetrenie, BigDecimal suma, LocalDate vystavenie, LocalDate splatnost, String stav, byte[] scan) {
        jdbc.update(conn -> {
            var ps = conn.prepareStatement("INSERT INTO faktura (id_vysetrenie, suma_celkom, datum_vystavenia, datum_splatnosti, stav_uhrady, scan_faktury) VALUES (?,?,?,?,?,?)");
            ps.setLong(1, idVysetrenie); ps.setBigDecimal(2, suma); ps.setDate(3, Date.valueOf(vystavenie));
            ps.setDate(4, Date.valueOf(splatnost)); ps.setString(5, stav);
            if (scan == null) ps.setNull(6, Types.BLOB); else ps.setBlob(6, new ByteArrayInputStream(scan));
            return ps;
        });
    }
    public void update(Long id, Long idVysetrenie, BigDecimal suma, LocalDate vystavenie, LocalDate splatnost, String stav, byte[] scan) {
        if (scan != null && scan.length > 0) {
            jdbc.update(conn -> {
                var ps = conn.prepareStatement("UPDATE faktura SET id_vysetrenie=?, suma_celkom=?, datum_vystavenia=?, datum_splatnosti=?, stav_uhrady=?, scan_faktury=? WHERE id_faktura=?");
                ps.setLong(1, idVysetrenie); ps.setBigDecimal(2, suma); ps.setDate(3, Date.valueOf(vystavenie));
                ps.setDate(4, Date.valueOf(splatnost)); ps.setString(5, stav);
                ps.setBlob(6, new ByteArrayInputStream(scan)); ps.setLong(7, id);
                return ps;
            });
        } else {
            jdbc.update("UPDATE faktura SET id_vysetrenie=?, suma_celkom=?, datum_vystavenia=?, datum_splatnosti=?, stav_uhrady=? WHERE id_faktura=?",
                    idVysetrenie, suma, Date.valueOf(vystavenie), Date.valueOf(splatnost), stav, id);
        }
    }
    public void delete(Long id) { jdbc.update("DELETE FROM faktura WHERE id_faktura=?", id); }
}
