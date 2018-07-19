package ru.sergey_gusarov.hw6.dao.books;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.sergey_gusarov.hw6.dao.books.dict.DictAuthorDao;
import ru.sergey_gusarov.hw6.dao.books.dict.DictGenreDao;
import ru.sergey_gusarov.hw6.domain.books.Author;
import ru.sergey_gusarov.hw6.domain.books.Book;
import ru.sergey_gusarov.hw6.domain.books.Genre;
import ru.sergey_gusarov.hw6.jdbc.InsertAuthor;
import ru.sergey_gusarov.hw6.jdbc.InsertBookAuthorRelProvider;
import ru.sergey_gusarov.hw6.jdbc.InsertBookGenreRelProvider;
import ru.sergey_gusarov.hw6.jdbc.InsertGenre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Transactional
public class BookDaoJdbc implements BookDao {
    private final NamedParameterJdbcOperations jdbc;
    private final DictAuthorDao dictAuthorDao;
    private final DictGenreDao dictGenreDao;

    @Autowired
    private InsertAuthor InsertAuthor;
    @Autowired
    private InsertBookAuthorRelProvider insertBookAuthorRelProvider;
    @Autowired
    private InsertBookGenreRelProvider insertBookGenreRelProvider;
    @Autowired
    private InsertGenre insertGenre;

    public static final String SQL_COUNT = "select count(*) from " + BookDao.TABLE_NAME;
    public static final String SQL_INSERT = "insert into " + BookDao.TABLE_NAME +
            " ("+BookDao.ID_COLUMN + ", " + BookDao.TITLE_COLUMN +
            ") values (:" + BookDao.ID_COLUMN +" ,:" + BookDao.TITLE_COLUMN + ")";
    public static final String SQL_UPDATE = "update " + BookDao.TABLE_NAME + " set " + BookDao.TITLE_COLUMN + " = ? where " + BookDao.ID_COLUMN + " = :"+BookDao.ID_COLUMN;
    public static final String SQL_DELETE = "delete from " + BookDao.TABLE_NAME + " where " + BookDao.ID_COLUMN + " = :" + BookDao.ID_COLUMN;

    // union - стараюсь без яркой необходимсти не использовать
    private static final String SQL_FIND_ALL = "select b.id as book_id, b.title, " +
            "a.id as AUTHOR_ID, a.name as AUTHOR_NAME, " +
            "g.id as GENRE_ID, g.name as GENRE_NAME " +
            "from book b " +
            "left join BOOK_AUTHOR_REL ba on ba.book_id = b.id " +
            "left join AUTHOR a on a.id = ba.author_id " +
            "left join BOOK_GENRE_REL bg on bg.book_id = b.id " +
            "left join GENRE g on g.id = bg.genre_id";
    public static final String SQL_FIND_BY_ID = SQL_FIND_ALL + " where " + BookDao.ID_COLUMN + " = :" + BookDao.ID_COLUMN;


    public BookDaoJdbc(NamedParameterJdbcOperations jdbc, DictAuthorDao dictAuthorDao, DictGenreDao dictGenreDao) {
        this.jdbc = jdbc;
        this.dictAuthorDao = dictAuthorDao;
        this.dictGenreDao = dictGenreDao;
    }

    @Override
    public int count() {
        return jdbc.queryForObject(SQL_COUNT, Collections.EMPTY_MAP, Integer.class);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public int insertWithAuthorAndGenre(Book book) {
        HashMap<String, Object> paramMap = new HashMap<>(2);
        paramMap.put(BookDao.ID_COLUMN, book.getId());
        paramMap.put(BookDao.TITLE_COLUMN, book.getTitle());
        jdbc.update(SQL_INSERT, paramMap);

        Set<Author> authors = book.getAuthors();
        if (authors != null) {
            for (Author author : authors) {
                paramMap = new HashMap<String, Object>();
                paramMap.put(InsertAuthor.ID_PARAMETER, author.getId());
                paramMap.put(InsertAuthor.NAME_PARAMETER, author.getName());
                InsertAuthor.updateByNamedParam(paramMap);
                paramMap = new HashMap<String, Object>();
                paramMap.put(insertBookAuthorRelProvider.BOOK_ID_PARAMETER, author.getId());
                paramMap.put(insertBookAuthorRelProvider.AUTHOR_ID_PARAMETER, book.getId());
                insertBookAuthorRelProvider.updateByNamedParam(paramMap);
            }
        }
        InsertAuthor.flush();
        insertBookAuthorRelProvider.flush();

        Set<Genre> genres = book.getGenres();
        if (genres != null) {
            for (Genre genre : genres) {
                paramMap = new HashMap<String, Object>();
                paramMap.put(insertGenre.ID_PARAMETER, genre.getId());
                paramMap.put(insertGenre.NAME_PARAMETER, genre.getName());
                insertGenre.updateByNamedParam(paramMap);
                paramMap = new HashMap<String, Object>();
                paramMap.put(insertBookGenreRelProvider.BOOK_ID_PARAMETER, genre.getId());
                paramMap.put(insertBookGenreRelProvider.GENRE_ID_PARAMETER, book.getId());
                insertBookGenreRelProvider.updateByNamedParam(paramMap);
            }
        }
        insertGenre.flush();
        insertBookGenreRelProvider.flush();

        return -1;
    }

    @Override
    public int insert(Book book) {
        final HashMap<String, Object> params = new HashMap<>(2);
        params.put(BookDao.ID_COLUMN, book.getId());
        params.put(BookDao.TITLE_COLUMN, book.getTitle());
        return jdbc.update(SQL_INSERT, params);
    }

    @Override
    public Book getById(int id) {
        final HashMap<String, Object> params = new HashMap<>(1);
        params.put(BookDao.ID_COLUMN, id);
        List<Book> books = jdbc.query(SQL_FIND_BY_ID, params, new BooksResultMapper());
        if(books.size() > 1)
            throw new ArrayIndexOutOfBoundsException("Найдено более одно значения по ID") ;
        //if(books.isEmpty())

        return books.get(0);
    }

    @Override
    public List<Book> findAll() {
        return jdbc.query(SQL_FIND_ALL, new BooksResultMapper());
    }

    @Override
    public int update(Book book) {
        final HashMap<String, Object> params = new HashMap<>(2);
        params.put(BookDao.ID_COLUMN, book.getId());
        params.put(BookDao.TITLE_COLUMN, book.getTitle());
        return jdbc.update(SQL_UPDATE, params);
    }

    @Override
    public int delete(Book book) {
        final HashMap<String, Object> params = new HashMap<>(1);
        params.put(BookDao.ID_COLUMN, book.getId());
        return jdbc.update(SQL_DELETE, params);
    }

    private Book getBook(Map<Integer, String> genres, Map<Integer, String> authors, Integer id, String title) {
        Set<Genre> genreList = genres.entrySet().stream().
                filter((k) -> k.getKey() > 0).
                map(k -> new Genre(k.getKey(), k.getValue())).
                collect(Collectors.toSet());
        Set<Author> authorList = new HashSet<>();
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
                id = rs.getInt(BookDao.ID_COLUMN);
                if (lastId != id) {
                    title = rs.getString(BookDao.TITLE_COLUMN);
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

    /*
    private class BookMapper implements RowMapper<Book> {
        @Override
        public Book mapRow(ResultSet resultSet, int i) throws SQLException {
            int id = resultSet.getInt(BookDao.ID_COLUMN);
            String name = resultSet.getString(BookDao.TITLE_COLUMN);
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
    */
}
