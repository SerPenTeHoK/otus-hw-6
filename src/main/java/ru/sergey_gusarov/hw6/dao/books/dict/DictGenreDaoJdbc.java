package ru.sergey_gusarov.hw6.dao.books.dict;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.sergey_gusarov.hw6.domain.books.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Repository
public class DictGenreDaoJdbc implements DictGenreDao {
    private final NamedParameterJdbcOperations jdbc;

    public DictGenreDaoJdbc(NamedParameterJdbcOperations jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public int count() {
        return jdbc.queryForObject("select count(*) from Genre", Collections.EMPTY_MAP, Integer.class);
    }

    @Override
    public void insert(Genre genre) {
        final HashMap<String, Object> params = new HashMap<>(2);
        params.put("id", genre.getId());
        params.put("name", genre.getName());
        jdbc.update("insert into Genre (id, `name`) values (:id, :name)", params);
    }

    @Override
    public Genre getById(int id) {
        final HashMap<String, Object> params = new HashMap<>(1);
        params.put("id", id);
        return jdbc.queryForObject("select * from Genre where id = :id", params, new GenreMapper());
    }

    @Override
    public Genre getByName(String name) {
        final HashMap<String, Object> params = new HashMap<>(1);
        params.put("name", name);
        return jdbc.queryForObject("select * from Genre where name = :name", params, new GenreMapper());
    }

    @Override
    public List<Genre> findAll() {
        return jdbc.query("select * from Genre", new GenreMapper());
    }

    private static class GenreMapper implements RowMapper<Genre> {
        @Override
        public Genre mapRow(ResultSet resultSet, int i) throws SQLException {
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            return new Genre(id, name);
        }
    }
}
