package sk.uniza.veterina.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import sk.uniza.veterina.model.OptionItem;

import java.util.List;

@Repository
public class LiekRepository {
    private final JdbcTemplate jdbc;

    public LiekRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<OptionItem> options() {
        return jdbc.query(
                "SELECT id_liek, nazov_lieku FROM liek ORDER BY nazov_lieku",
                (rs, n) -> new OptionItem(rs.getLong("id_liek"), rs.getString("nazov_lieku"))
        );
    }
}
