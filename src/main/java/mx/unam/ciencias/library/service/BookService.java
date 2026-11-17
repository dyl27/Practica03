package mx.unam.ciencias.library.service;

import java.util.List;
import java.util.Optional;

import mx.unam.ciencias.library.model.Book;
import mx.unam.ciencias.library.model.BookFilter;

public interface BookService {
    List<Book> findAll();

    Optional<Book> findById(int id);

    List<Book> search(String text);

    Book save(Book book);

    boolean delete(int id);

    long count();

    long countFiltered(BookFilter filter);

    List<Book> findPage(BookFilter filter);

    List<String> findAuthors();
}
