package mx.unam.ciencias.library.model;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "books")
public class Book {
    @Id
    private Integer id;

    @NotBlank(message = "{validation.name.required}")
    @Size(max = 200, message = "{validation.name.size}")
    private String name;

    @NotBlank(message = "{validation.author.required}")
    @Size(max = 150, message = "{validation.author.size}")
    private String author;

    @NotNull(message = "{validation.price.required}")
    @DecimalMin(value = "0.0", message = "{validation.price.min}")
    private BigDecimal price;

    public Book() {
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
}
