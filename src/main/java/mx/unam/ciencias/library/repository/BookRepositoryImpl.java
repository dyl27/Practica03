package mx.unam.ciencias.library.repository;

import java.sql.PreparedStatement;
import java.math.BigDecimal;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import mx.unam.ciencias.library.model.Book;
import mx.unam.ciencias.library.model.BookFilter;
import mx.unam.ciencias.library.model.BookFilter.ColumnSearch;

@Repository
public class BookRepositoryImpl implements BookRepository {
    private static final String SELECT = "SELECT id, name, author, price FROM books";
    private static final Map<Integer, String> COLUMNS = Map.of(
            1, "id",
            2, "name",
            3, "author",
            4, "price");

    private static final List<String> NUMERIC = List.of("id", "price");
    private static final RowMapper<Book> BOOK_MAPPER = (rs, rowNum) -> {
        Book book = new Book();
        book.setId(rs.getInt("id"));
        book.setName(rs.getString("name"));
        book.setAuthor(rs.getString("author"));
        book.setPrice(rs.getBigDecimal("price"));
        return book;
    };

    private final JdbcTemplate jdbc;

    public BookRepositoryImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<Book> findAll() {
        return jdbc.query(SELECT + " ORDER BY name", BOOK_MAPPER);
    }

    @Override
    public Optional<Book> findById(int id) {
        return jdbc.query(SELECT + " WHERE id = ?", BOOK_MAPPER, id)
                   .stream()
                   .findFirst();
    }

    @Override
    public List<Book> search(String text) {
        String like = "%" + text + "%";
        return jdbc.query(SELECT + " WHERE name LIKE ? OR author LIKE ? ORDER BY name",
                          BOOK_MAPPER, like, like);
    }

    @Override
    public Book insert(Book book) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO books (name, author, price) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, book.getName());
            ps.setString(2, book.getAuthor());
            ps.setBigDecimal(3, book.getPrice());
            return ps;
        }, keyHolder);
        book.setId(keyHolder.getKey().intValue());
        return book;
    }

    @Override
    public int update(Book book) {
        return jdbc.update("UPDATE books SET name = ?, author = ?, price = ? WHERE id = ?",
                           book.getName(), book.getAuthor(), book.getPrice(), book.getId());
    }

    @Override
    public int deleteById(int id) {
        return jdbc.update("DELETE FROM books WHERE id = ?", id);
    }

    @Override
    public long count() {
        return jdbc.queryForObject("SELECT COUNT(*) FROM books", Long.class);
    }

    @Override
    public long countFiltered(BookFilter filter) {
        List<Object> params = new ArrayList<>();
        String where = where(filter, params);
        return jdbc.queryForObject("SELECT COUNT(*) FROM books" + where, Long.class, params.toArray());
    }

    @Override
    public List<Book> findPage(BookFilter filter) {
        List<Object> params = new ArrayList<>();
        String where = where(filter, params);
        String orderBy = COLUMNS.getOrDefault(filter.sortColumn(), "name");

        String sql = SELECT + where
                + " ORDER BY " + orderBy + (filter.asc() ? " ASC" : " DESC")
                + " LIMIT ? OFFSET ?";
        params.add(filter.limit());
        params.add(filter.offset());

        return jdbc.query(sql, BOOK_MAPPER, params.toArray());
    }

    @Override
    public List<String> findAuthors() {
        return jdbc.queryForList("SELECT DISTINCT author FROM books ORDER BY author", String.class);
    }

    private String where(BookFilter filter, List<Object> params) {
        List<String> conditions = new ArrayList<>();
        if (!filter.search().isEmpty()) {
            String like = "%" + filter.search() + "%";
            conditions.add("(name LIKE ? OR author LIKE ? OR CAST(price AS CHAR) LIKE ?)");
            params.add(like);
            params.add(like);
            params.add(like);
        }
        for (ColumnSearch cs : filter.columnSearches()) {
            String column = COLUMNS.get(cs.column());
            if (column != null) {
                String condition = NUMERIC.contains(column)
                        ? numberCondition(column, cs.logic(), cs.value(), params)
                        : textCondition(column, cs.logic(), cs.value(), params);
                if (condition != null) {
                    conditions.add(condition);
                }
            }
        }
        if (!filter.authors().isEmpty()) {
            conditions.add("author IN (" + String.join(", ", filter.authors().stream().map(a -> "?").toList()) + ")");
            params.addAll(filter.authors());
        }

        return conditions.isEmpty() ? "" : " WHERE " + String.join(" AND ", conditions);
    }

    private String textCondition(String column, String logic, String value, List<Object> params) {
        switch (logic) {
            case "empty":    return "(" + column + " IS NULL OR " + column + " = '')";
            case "notEmpty": return "(" + column + " IS NOT NULL AND " + column + " <> '')";
            default:         break;
        }
        if (value.isEmpty()) {
            return null;
        }
        switch (logic) {
            case "equal":       params.add(value);             return column + " = ?";
            case "notEqual":    params.add(value);             return column + " <> ?";
            case "starts":      params.add(value + "%");       return column + " LIKE ?";
            case "ends":        params.add("%" + value);       return column + " LIKE ?";
            case "notContains": params.add("%" + value + "%"); return column + " NOT LIKE ?";
            default:            params.add("%" + value + "%"); return column + " LIKE ?";
        }
    }

    private String numberCondition(String column, String logic, String value, List<Object> params) {
        switch (logic) {
            case "empty":    return column + " IS NULL";
            case "notEmpty": return column + " IS NOT NULL";
            default:         break;
        }
        BigDecimal number;
        try {
            number = new BigDecimal(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
        String operator = switch (logic) {
            case "notEqual"       -> "<>";
            case "greater"        -> ">";
            case "greaterOrEqual" -> ">=";
            case "less"           -> "<";
            case "lessOrEqual"    -> "<=";
            default               -> "=";
        };
        params.add(number);
        return column + " " + operator + " ?";
    }
}
