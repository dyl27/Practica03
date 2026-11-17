package mx.unam.ciencias.library.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import mx.unam.ciencias.library.model.Book;
import mx.unam.ciencias.library.model.BookFilter;
import mx.unam.ciencias.library.repository.BookRepository;

@Service
public class BookServiceImpl implements BookService {
    private final BookRepository repository;

    public BookServiceImpl(BookRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Book> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<Book> findById(int id) {
        return repository.findById(id);
    }

    @Override
    public List<Book> search(String text) {
        if (text == null || text.isBlank()) {
            return repository.findAll();
        }
        return repository.search(text.trim());
    }

    @Override
    public Book save(Book book) {
        book.setName(book.getName().trim());
        book.setAuthor(book.getAuthor().trim());

        if (book.getId() == null) {
            return repository.insert(book);
        }
        if (repository.update(book) == 0) {
            throw new IllegalArgumentException("No existe el libro con id " + book.getId());
        }
        return book;
    }

    @Override
    public boolean delete(int id) {
        return repository.deleteById(id) > 0;
    }

    private static final int MAX_PAGE_SIZE = 100;

    @Override
    public long count() {
        return repository.count();
    }

    @Override
    public long countFiltered(BookFilter filter) {
        return repository.countFiltered(normalize(filter));
    }

    @Override
    public List<Book> findPage(BookFilter filter) {
        return repository.findPage(normalize(filter));
    }

    @Override
    public List<String> findAuthors() {
        return repository.findAuthors();
    }
    private BookFilter normalize(BookFilter f) {
        String search = f.search() == null ? "" : f.search().trim();
        int offset = Math.max(f.offset(), 0);
        int limit = (f.limit() <= 0 || f.limit() > MAX_PAGE_SIZE) ? MAX_PAGE_SIZE : f.limit();
        return new BookFilter(search, f.columnSearches(), f.authors(), f.sortColumn(), f.asc(), offset, limit);
    }
}
