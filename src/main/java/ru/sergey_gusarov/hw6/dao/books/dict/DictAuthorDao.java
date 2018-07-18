package ru.sergey_gusarov.hw6.dao.books.dict;

import ru.sergey_gusarov.hw6.domain.books.Author;

import java.util.List;

public interface DictAuthorDao {
    public static final String TABLE_NAME = "AUTHOR";
    public static final String ID_COLUMN = "ID";
    public static final String NAME_COLUMN = "NAME";

    int count();

    void insert(Author author);

    Author getById(int id);

    Author getByName(String name);

    List<Author> findAll();

    void update(Author author);

    void delete(Author author);

}
