package de.dxfrontiers.demo.axon.library.web;

import de.dxfrontiers.demo.axon.library.book.BookAggregate;
import de.dxfrontiers.demo.axon.library.book.command.AddBookCommand;
import de.dxfrontiers.demo.axon.library.persistence.BookEntity;
import de.dxfrontiers.demo.axon.library.persistence.BookRepository;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.gateway.EventGateway;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final CommandGateway commandGateway;
    private final BookRepository bookRepository;
    private final EventGateway eventGateway;

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
}
