package de.dxfrontiers.demo.axon.library.web;

import de.dxfrontiers.demo.axon.library.book.BookAggregate;
import de.dxfrontiers.demo.axon.library.book.command.AddBookCommand;
import de.dxfrontiers.demo.axon.library.book.command.EndRentalCommand;
import de.dxfrontiers.demo.axon.library.book.command.ExtendRentalCommand;
import de.dxfrontiers.demo.axon.library.book.command.StartRentalCommand;
import de.dxfrontiers.demo.axon.library.persistence.BookEntity;
import de.dxfrontiers.demo.axon.library.persistence.BookRepository;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final CommandGateway commandGateway;
    private final BookRepository bookRepository;

    @GetMapping
    public List<BookEntity> listAllBooks() {
        return bookRepository.findAll();
    }

    @PostMapping("/add-command")
    public void addBook(
        @RequestParam String isbn,
        @RequestParam String author,
        @RequestParam String title,
        @RequestParam(required = false) BookAggregate.Type type
    ) {
        commandGateway.sendAndWait(
            AddBookCommand.builder()
                .isbn(isbn)
                .author(author)
                .title(title)
                .type(type)
                .build()
        );
    }

    @PostMapping("/start-rental-command")
    public void startRental(
        @RequestParam UUID bookId,
        @RequestParam UUID readerId
    ) {
        commandGateway.sendAndWait(
            StartRentalCommand.builder()
                .bookId(bookId)
                .readerId(readerId)
                .build()
        );
    }

    @PostMapping("/extend-rental-command")
    public void extendRental(
        @RequestParam UUID bookId,
        @RequestParam UUID readerId
    ) {
        commandGateway.sendAndWait(
            ExtendRentalCommand.builder()
                .bookId(bookId)
                .readerId(readerId)
                .build()
        );
    }

    @PostMapping("/end-rental-command")
    public void endRental(
        @RequestParam UUID bookId
    ) {
        commandGateway.sendAndWait(
            EndRentalCommand.builder()
                .bookId(bookId)
                .build()
        );
    }
}
