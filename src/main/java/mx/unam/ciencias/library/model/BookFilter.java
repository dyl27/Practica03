package mx.unam.ciencias.library.model;

import java.util.List;

public record BookFilter(String search,
                         List<ColumnSearch> columnSearches,
                         List<String> authors,
                         int sortColumn,
                         boolean asc,
                         int offset,
                         int limit) {
    public record ColumnSearch(int column, String logic, String value) {
    }
}
