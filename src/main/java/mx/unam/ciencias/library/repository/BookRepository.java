package mx.unam.ciencias.library.repository;

import java.util.List;
import java.util.Optional;

import mx.unam.ciencias.library.model.Book;
import mx.unam.ciencias.library.model.BookFilter;

public interface BookRepository {
    List<Book> findAll();

    Optional<Book> findById(int id);

    List<Book> search(String text);

    Book insert(Book book);

    int update(Book book);

    int deleteById(int id);

    long count();

    long countFiltered(BookFilter filter);

    List<Book> findPage(BookFilter filter);

    List<String> findAuthors();
}
