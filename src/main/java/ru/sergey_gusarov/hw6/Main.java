package ru.sergey_gusarov.hw6;

import org.h2.tools.Console;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import ru.sergey_gusarov.hw6.dao.PersonDao;
import ru.sergey_gusarov.hw6.dao.books.BookDao;
import ru.sergey_gusarov.hw6.domain.books.Book;

import java.util.List;

@SpringBootApplication
public class Main {

    public static void main(String[] args) throws Exception {

        ApplicationContext context = SpringApplication.run(Main.class);
        PersonDao dao = context.getBean(PersonDao.class);
        BookDao bookDao = context.getBean(BookDao.class);

        List<Book> books = null;
        books = bookDao.findAll();
        Book testBook1 = bookDao.getById(1);
        Book testBook2 = bookDao.getById(2);
        Book testBook3 = bookDao.getById(3);

        System.out.println("All book count " + bookDao.count());

/*
        System.out.println("All count " + dao.count());
        dao.insert(new Person(2, "ivan"));
        System.out.println("All count " + dao.count());
        Person ivan = dao.getById(2);
        System.out.println("Ivan id: " + ivan.getId() + " name: " + ivan.getName())
*/
        Console.main(args);
    }
}
