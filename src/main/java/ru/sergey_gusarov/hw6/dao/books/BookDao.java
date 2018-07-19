package ru.sergey_gusarov.hw6.dao.books;

import ru.sergey_gusarov.hw6.domain.books.Book;

import java.util.List;

public interface BookDao {
    String TABLE_NAME = "BOOK";
    String ID_COLUMN = "ID";
    String TITLE_COLUMN = "TITLE";

    int count();

    int insertWithAuthorAndGenre(Book book);

    int insert(Book book);

    Book getById(int id);

    List<Book> findAll();

    int update(Book book);

    int delete(Book book);

}
