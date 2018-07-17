package ru.sergey_gusarov.hw6.dao.books.dict;

import ru.sergey_gusarov.hw6.domain.books.Genre;

import java.util.List;

public interface DictGenreDao {
    int count();

    void insert(Genre genre);

    Genre getById(int id);

    Genre getByName(String name);

    List<Genre> findAll();
}
