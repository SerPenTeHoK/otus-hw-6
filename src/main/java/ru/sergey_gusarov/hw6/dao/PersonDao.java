package ru.sergey_gusarov.hw6.dao;

import ru.sergey_gusarov.hw6.domain.Person;

import java.util.List;

public interface PersonDao {

    int count();

    void insert(Person person);

    Person getById(int id);

    List<Person> getAll();
}
