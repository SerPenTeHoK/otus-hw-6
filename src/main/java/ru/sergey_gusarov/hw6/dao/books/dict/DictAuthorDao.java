package ru.sergey_gusarov.hw6.dao.books.dict;

import ru.sergey_gusarov.hw6.domain.books.Author;

import java.util.List;

public interface DictAuthorDao {
    int count();

    void insert(Author author);

    Author getById(int id);

    Author getByName(String name);

    List<Author> findAll();

}
