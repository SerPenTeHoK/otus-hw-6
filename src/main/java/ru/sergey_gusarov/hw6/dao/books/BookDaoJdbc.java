package ru.sergey_gusarov.hw6.dao.books;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.sergey_gusarov.hw6.dao.books.dict.DictAuthorDao;
import ru.sergey_gusarov.hw6.dao.books.dict.DictGenreDao;
import ru.sergey_gusarov.hw6.domain.books.Author;
import ru.sergey_gusarov.hw6.domain.books.Book;
import ru.sergey_gusarov.hw6.domain.books.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class BookDaoJdbc implements BookDao {
    private final NamedParameterJdbcOperations jdbc;
    private final DictAuthorDao dictAuthorDao;
    private final DictGenreDao dictGenreDao;

    private String sqlCount = "select count(*) from book";
    private String sqlInsert = "insert into book (id, `title`) values (:id, :title)";
    private String sqlFindById = "select * from book where id = :id";
    // union - стараюсь без яркой необходимсти не использовать
    private String sqlFindAll = "select b.id as book_id, b.title, " +
            "a.id as author_id, a.name as author_name, " +
            "g.id as genre_id, g.name as genre_name " +
            "from book b " +
            "left join BOOK_AUTHOR_REL ba on ba.book_id = b.id " +
            "left join author a on a.id = ba.author_id " +
            "left join book_genre_REL bg on bg.book_id = b.id " +
            "left join genre g on g.id = bg.genre_id";


    public BookDaoJdbc(NamedParameterJdbcOperations jdbc, DictAuthorDao dictAuthorDao, DictGenreDao dictGenreDao) {
        this.jdbc = jdbc;
        this.dictAuthorDao = dictAuthorDao;
        this.dictGenreDao = dictGenreDao;
    }

    @Override
    public int count() {
        return jdbc.queryForObject(sqlCount, Collections.EMPTY_MAP, Integer.class);
    }

    @Override
    public void insert(Book book) {
        final HashMap<String, Object> params = new HashMap<>(2);
        params.put("id", book.getId());
        params.put("title", book.getTitle());
        jdbc.update(sqlInsert, params);
    }

    @Override
    public Book getById(int id) {
        final HashMap<String, Object> params = new HashMap<>(1);
        params.put("id", id);
        return jdbc.queryForObject(sqlFindById, params, new BookMapper());
    }

    @Override
    public List<Book> findAll() {
        return jdbc.query(sqlFindAll, new BooksResultMapper());
    }

    private Book getBook(Map<Integer, String> genres, Map<Integer, String> authors, Integer id, String title) {
        List<Genre> genreList = genres.entrySet().stream().
                filter((k) -> k.getKey() > 0).
                map(k -> new Genre(k.getKey(), k.getValue())).
                collect(Collectors.toList());
        ArrayList<Author> authorList = new ArrayList<>();
        authors.forEach((k, v) -> {
                    if (k > 0) {
                        authorList.add(new Author(k, v));
                    }
                }
        );
        return new Book(id, title, genreList, authorList);
    }

    private class BooksResultMapper implements ResultSetExtractor<List<Book>> {
        @Override
        public List<Book> extractData(ResultSet rs) throws SQLException,
                DataAccessException {
            List<Book> list = new ArrayList<Book>();
            int lastId = -1;

            Map<Integer, String> genres = new HashMap<>();
            Map<Integer, String> authors = new HashMap<>();
            Integer id = null;
            String title = null;
            while (rs.next()) {
                id = rs.getInt("id");
                if (lastId != id) {
                    title = rs.getString("title");
                    if (lastId != -1) {
                        Book b = getBook(genres, authors, id, title);
                        list.add(b);
                        genres = new HashMap<>();
                        authors = new HashMap<>();
                    }
                    lastId = id;
                }
                authors.put(rs.getInt("author_id"), rs.getString("author_name"));
                genres.put(rs.getInt("genre_id"), rs.getString("genre_name"));
            }
            if (lastId != -1) {
                Book b = getBook(genres, authors, id, title);
                list.add(b);
            }
            return list;
        }
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
            for (Map map : listMapAuthors) {
                Integer idAuthor = (Integer) map.get("AUTHOR_ID");
                authors.add(dictAuthorDao.getById(idAuthor));
            }
            List<Map<String, Object>> listMapGenre = jdbc.queryForList("select GENRE_ID from BOOK_GENRE_REL where BOOK_ID = :id", params);
            for (Map map : listMapGenre) {
                Integer idGenre = (Integer) map.get("GENRE_ID");
                genres.add(dictGenreDao.getById(idGenre));
            }
            return new Book(id, name, genres, authors);
        }
    }
}
