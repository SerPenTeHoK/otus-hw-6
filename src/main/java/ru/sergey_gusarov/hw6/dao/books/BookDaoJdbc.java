package ru.sergey_gusarov.hw6.dao.books;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.sergey_gusarov.hw6.domain.books.Author;
import ru.sergey_gusarov.hw6.domain.books.Book;
import ru.sergey_gusarov.hw6.domain.books.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class BookDaoJdbc implements BookDao {
    private final NamedParameterJdbcOperations jdbc;
    private final AuthorDao authorDao;
    private final GenreDao genreDao;

    public BookDaoJdbc(NamedParameterJdbcOperations jdbc, AuthorDao authorDao, GenreDao genreDao) {
        this.jdbc = jdbc;
        this.authorDao = authorDao;
        this.genreDao = genreDao;
    }

    @Override
    public int count() {
        final HashMap<String, Object> params = new HashMap<>(0);
        return jdbc.queryForObject("select count(*) from book", params, Integer.class);
        //return jdbc.queryForObject("select count(*) from book", Integer.class);
    }

    @Override
    public void insert(Book book) {
        final HashMap<String, Object> params = new HashMap<>(2);
        params.put("id", book.getId());
        params.put("title", book.getTitle());
        jdbc.update("insert into book (id, `title`) values (:id, :title)", params);
        //jdbc.update("insert into book (id, `title`) values (?, ?)", book.getId(), book.getTitle());
    }

    @Override
    public Book getById(int id) {
        final HashMap<String, Object> params = new HashMap<>(1);
        params.put("id", id);
        return jdbc.queryForObject("select * from book where id = :id", params, new BookMapper());
        //return jdbc.queryForObject("select * from book where id = ?", new Object[]{id}, new BookMapper());
    }

    @Override
    public List<Book> findAll() {
        return jdbc.query("select * from book", new BookMapper());
        //return jdbc.queryForList("select * from book", Book.class, new BookMapper());
    }


    private class BookMapper implements RowMapper<Book> {
        @Override
        public Book mapRow(ResultSet resultSet, int i) throws SQLException {
            int id = resultSet.getInt("id");
            String name = resultSet.getString("title");
            List<Genre> genres = new ArrayList<>();
            List<Author> authors = new ArrayList<>();

            final HashMap<String, Object> params = new HashMap<>(1);
            params.put("id", id);
            List<Map<String, Object>> listMapAuthors = jdbc.queryForList("select AUTHOR_ID from BOOK_AUTHOR_REL where BOOK_ID = :id", params);
            for (Map map : listMapAuthors){
              Integer idAuthor = (Integer) map.get("AUTHOR_ID");
              authors.add(authorDao.getById(idAuthor));
            }
            List<Map<String, Object>> listMapGenre = jdbc.queryForList("select GENRE_ID from BOOK_GENRE_REL where BOOK_ID = :id", params);
            for (Map map : listMapGenre){
                Integer idGenre = (Integer) map.get("GENRE_ID");
                genres.add(genreDao.getById(idGenre));
            }

            return new Book(id, name, genres, authors);
        }
    }
}
