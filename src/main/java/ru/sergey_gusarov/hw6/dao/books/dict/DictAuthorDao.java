package ru.sergey_gusarov.hw6.dao.books.dict;

import ru.sergey_gusarov.hw6.domain.books.Author;

import java.util.List;

public interface DictAuthorDao {
    String TABLE_NAME = "AUTHOR";
    String ID_COLUMN = "ID";
    String NAME_COLUMN = "NAME";

    int count();

    int insert(Author author);

    Author getById(int id);

    Author getByName(String name);

    List<Author> findAll();

    int update(Author author);

    int delete(Author author);

}
