package ru.sergey_gusarov.hw6.dao.books;

import ru.sergey_gusarov.hw6.domain.books.Book;

import java.util.List;

public interface BookDao {
    public static final String TABLE_NAME = "BOOK";
    public static final String ID_COLUMN = "ID";
    public static final String TITLE_COLUMN = "TITLE";

    int count();

    void insert(Book book);

    Book getById(int id);

    List<Book> findAll();

    void update(Book book);

    void delete(Book book);

}
