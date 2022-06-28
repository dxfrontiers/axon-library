package de.dxfrontiers.demo.axon.library.persistence;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "BOOK")
@Data
public class BookEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @Column(name = "BOOK_ID", unique = true)
    private String bookId;

    @Column(name = "ISBN")
    private String isbn;

    @Column(name = "AUTHOR")
    private String author;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "RENTED")
    private boolean rented;

    public BookEntity setBookId(UUID bookId) {
        this.bookId = bookId.toString();
        return this;
    }

    public UUID getBookId() {
        return UUID.fromString(bookId);
    }
}
