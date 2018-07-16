package ru.sergey_gusarov.hw6.domain.books;

import java.util.List;

public class Book {
    private final int id;
    private final String title;
    private List<Genre> genres;
    private List<Author> authors;

    public Book(int id, String title, List<Genre> genres, List<Author> authors) {

        this.id = id;
        this.title = title;
        this.genres = genres;
        this.authors = authors;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public int getId() {

        return id;
    }

    public String getTitle() {
        return title;
    }
}
