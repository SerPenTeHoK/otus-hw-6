package ru.sergey_gusarov.hw6.dao.books;

import ru.sergey_gusarov.hw6.domain.books.Author;

import java.util.List;

public interface AuthorDao {
    int count();

    void insert(Author author);

    Author getById(int id);

    Author getByName(String name);

    List<Author> findAll();

}
