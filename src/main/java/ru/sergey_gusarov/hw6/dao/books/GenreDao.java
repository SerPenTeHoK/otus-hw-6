package ru.sergey_gusarov.hw6.dao.books;

import ru.sergey_gusarov.hw6.domain.books.Genre;

import java.util.List;

public interface GenreDao {
    int count();

    void insert(Genre genre);

    Genre getById(int id);

    Genre getByName(String name);

    List<Genre> findAll();
}
