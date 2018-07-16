package ru.sergey_gusarov.hw6.dao.books;

import ru.sergey_gusarov.hw6.domain.books.Book;

import java.util.List;

public interface BookDao {
    int count();

    void insert(Book book);

    Book getById(int id);

    List<Book> findAll();

}
