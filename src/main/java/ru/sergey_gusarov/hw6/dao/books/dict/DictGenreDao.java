package ru.sergey_gusarov.hw6.dao.books.dict;

import ru.sergey_gusarov.hw6.domain.books.Genre;

import java.util.List;

public interface DictGenreDao {
    public static final String TABLE_NAME = "GENRE";
    public static final String ID_COLUMN = "ID";
    public static final String NAME_COLUMN = "NAME";



    int count();

    void insert(Genre genre);

    Genre getById(int id);

    Genre getByName(String name);

    List<Genre> findAll();

    void update(Genre genre);

    void delete(Genre genre);
}
