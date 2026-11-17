package mx.unam.ciencias.library.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import mx.unam.ciencias.library.model.Book;
import mx.unam.ciencias.library.model.BookFilter;
import mx.unam.ciencias.library.model.BookFilter.ColumnSearch;
import mx.unam.ciencias.library.service.BookService;

@Controller
@RequestMapping("/books")
public class BookController {
    private final BookService service;

    public BookController(BookService service) {
        this.service = service;
    }
    @GetMapping
    public String list() {
        return "books/list";
    }
    @GetMapping("/data")
    @ResponseBody
    public DataTablesResponse data(@RequestParam Map<String, String> params) {
        BookFilter filter = new BookFilter(
                params.getOrDefault("search[value]", ""),
                columnSearches(params),
                authors(params),
                intParam(params, "order[0][column]", 2),
                !"desc".equalsIgnoreCase(params.get("order[0][dir]")),
                intParam(params, "start", 0),
                intParam(params, "length", 10));
        List<Map<String, String>> authorOptions = service.findAuthors().stream()
                .map(a -> Map.of("label", a, "value", a))
                .toList();

        return new DataTablesResponse(
                intParam(params, "draw", 0),
                service.count(),
                service.countFiltered(filter),
                service.findPage(filter),
                Map.of("author", authorOptions));
    }

    public record DataTablesResponse(int draw,
                                     long recordsTotal,
                                     long recordsFiltered,
                                     List<Book> data,
                                     Map<String, Object> columnControl) {
    }
    private List<ColumnSearch> columnSearches(Map<String, String> params) {
        List<ColumnSearch> result = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            String prefix = "columns[" + i + "][columnControl][search]";
            String logic = params.get(prefix + "[logic]");
            if (logic != null) {
                result.add(new ColumnSearch(i, logic, params.getOrDefault(prefix + "[value]", "")));
            }
        }
        return result;
    }
    private List<String> authors(Map<String, String> params) {
        List<String> result = new ArrayList<>();
        for (int k = 0; params.containsKey("columns[3][columnControl][list][" + k + "]"); k++) {
            result.add(params.get("columns[3][columnControl][list][" + k + "]"));
        }
        return result;
    }

    private int intParam(Map<String, String> params, String name, int defaultValue) {
        try {
            return Integer.parseInt(params.get(name));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("book", new Book());
        return "books/form";
    }
    @PostMapping
    public String create(@Valid @ModelAttribute("book") Book book,
                         BindingResult result,
                         RedirectAttributes redirect) {
        book.setId(null);
        if (result.hasErrors()) {
            return "books/form";
        }
        service.save(book);
        redirect.addFlashAttribute("message", "book.created");
        return "redirect:/books";
    }
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable int id, Model model) {
        if (!model.containsAttribute("book")) {
            model.addAttribute("book", findOr404(id));
        }
        return "books/form";
    }
    
    @PutMapping("/{id}")
    public String update(@PathVariable int id,
                         @Valid @ModelAttribute("book") Book book,
                         BindingResult result,
                         RedirectAttributes redirect) {
        book.setId(id);
        if (result.hasErrors()) {
            redirect.addFlashAttribute("org.springframework.validation.BindingResult.book", result);
            redirect.addFlashAttribute("book", book);
            return "redirect:/books/" + id + "/edit";
        }
        service.save(book);
        redirect.addFlashAttribute("message", "book.updated");
        return "redirect:/books";
    }
    
    @DeleteMapping("/{id}")
    public String delete(@PathVariable int id, RedirectAttributes redirect) {
        boolean deleted = service.delete(id);
        redirect.addFlashAttribute("message", deleted ? "book.deleted" : "book.notfound");
        return "redirect:/books";
    }

    private Book findOr404(int id) {
        return service.findById(id)
                      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
