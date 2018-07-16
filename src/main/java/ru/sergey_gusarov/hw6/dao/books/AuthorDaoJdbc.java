package ru.sergey_gusarov.hw6.dao.books;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import ru.sergey_gusarov.hw6.domain.books.Author;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

@Repository
public class AuthorDaoJdbc implements AuthorDao {
    private final NamedParameterJdbcOperations jdbc;

    public AuthorDaoJdbc(NamedParameterJdbcOperations jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public int count() {
        return jdbc.queryForObject("select count(*) from Author", (SqlParameterSource) null, Integer.class);
    }

    @Override
    public void insert(Author author) {
        final HashMap<String, Object> params = new HashMap<>(2);
        params.put("id", author.getId());
        params.put("name", author.getName());
        jdbc.update("insert into Author (id, `name`) values (:id, :name)", params);
    }

    @Override
    public Author getById(int id) {
        final HashMap<String, Object> params = new HashMap<>(1);
        params.put("id", id);
        return jdbc.queryForObject("select * from Author where id = :id", params, new AuthorMapper());
    }

    @Override
    public Author getByName(String name) {
        final HashMap<String, Object> params = new HashMap<>(1);
        params.put("name", name);
        return jdbc.queryForObject("select * from Genre where name = :name", params, new AuthorMapper());
    }

    @Override
    public List<Author> findAll() {
        return jdbc.query("select * from Author", new AuthorMapper());
    }

    private static class AuthorMapper implements RowMapper<Author> {
        @Override
        public Author mapRow(ResultSet resultSet, int i) throws SQLException {
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            return new Author(id, name);
        }
    }

}
