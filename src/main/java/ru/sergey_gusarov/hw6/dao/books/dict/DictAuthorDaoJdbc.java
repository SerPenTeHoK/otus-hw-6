package ru.sergey_gusarov.hw6.dao.books.dict;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.sergey_gusarov.hw6.domain.books.Author;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Repository
public class DictAuthorDaoJdbc implements DictAuthorDao {
    public static final String SQL_COUNT = "select count(*) from " + DictAuthorDao.TABLE_NAME;
    public static final String SQL_FIND_ALL = "select * from " + DictAuthorDao.TABLE_NAME;
    public static final String SQL_FIND_BY_ID = SQL_FIND_ALL + " where " + DictAuthorDao.ID_COLUMN +
            " = :" + DictAuthorDao.ID_COLUMN;
    public static final String SQL_INSERT = "insert into " + DictAuthorDao.TABLE_NAME +
            " ("+DictAuthorDao.ID_COLUMN + ", " + DictAuthorDao.NAME_COLUMN +
            ") values (:" + DictAuthorDao.ID_COLUMN +" ,:" + DictAuthorDao.NAME_COLUMN + ")";
    public static final String SQL_UPDATE = "update " + DictAuthorDao.TABLE_NAME +
            " set " + DictAuthorDao.NAME_COLUMN + " = ? where " + DictAuthorDao.ID_COLUMN +
            " = :"+DictAuthorDao.ID_COLUMN;
    public static final String SQL_DELETE = "delete from " + DictAuthorDao.TABLE_NAME +
            " where " + DictAuthorDao.ID_COLUMN + " = :" + DictAuthorDao.ID_COLUMN;

    private final NamedParameterJdbcOperations jdbc;

    public DictAuthorDaoJdbc(NamedParameterJdbcOperations jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public int count() {
        return jdbc.queryForObject(SQL_COUNT, Collections.EMPTY_MAP, Integer.class);
    }

    @Override
    public void insert(Author author) {
        final HashMap<String, Object> params = new HashMap<>(2);
        params.put(DictAuthorDao.ID_COLUMN, author.getId());
        params.put(DictAuthorDao.NAME_COLUMN, author.getName());
        jdbc.update(SQL_INSERT, params);
    }

    @Override
    public Author getById(int id) {
        final HashMap<String, Object> params = new HashMap<>(1);
        params.put(DictAuthorDao.ID_COLUMN, id);
        return jdbc.queryForObject(SQL_FIND_BY_ID, params, new AuthorMapper());
    }

    @Override
    public Author getByName(String name) {
        final HashMap<String, Object> params = new HashMap<>(1);
        params.put(DictAuthorDao.NAME_COLUMN, name);
        return jdbc.queryForObject("select * from Author where name = :name", params, new AuthorMapper());
    }

    @Override
    public List<Author> findAll() {
        return jdbc.query(SQL_FIND_ALL, new AuthorMapper());
    }

    @Override
    public void update(Author author) {
        final HashMap<String, Object> params = new HashMap<>(2);
        params.put(DictAuthorDao.ID_COLUMN, author.getId());
        params.put(DictAuthorDao.NAME_COLUMN, author.getName());
        jdbc.update(SQL_UPDATE, params);
    }

    @Override
    public void delete(Author author) {
        final HashMap<String, Object> params = new HashMap<>(1);
        params.put(DictAuthorDao.ID_COLUMN, author.getId());
        jdbc.update(SQL_DELETE, params);
    }

    private static class AuthorMapper implements RowMapper<Author> {
        @Override
        public Author mapRow(ResultSet resultSet, int i) throws SQLException {
            int id = resultSet.getInt(DictAuthorDao.ID_COLUMN);
            String name = resultSet.getString(DictAuthorDao.NAME_COLUMN);
            return new Author(id, name);
        }
    }

}
