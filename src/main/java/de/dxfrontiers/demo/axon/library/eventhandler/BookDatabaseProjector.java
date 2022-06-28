package de.dxfrontiers.demo.axon.library.eventhandler;

import de.dxfrontiers.demo.axon.library.book.event.BookAddedEvent;
import de.dxfrontiers.demo.axon.library.book.event.RentalEndedEvent;
import de.dxfrontiers.demo.axon.library.book.event.RentalStartedEvent;
import de.dxfrontiers.demo.axon.library.config.axon.ProcessingGroups;
import de.dxfrontiers.demo.axon.library.exception.EntityNotFoundException;
import de.dxfrontiers.demo.axon.library.persistence.BookEntity;
import de.dxfrontiers.demo.axon.library.persistence.BookRepository;
import lombok.RequiredArgsConstructor;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

@Component
@ProcessingGroup(ProcessingGroups.BOOK_DB)
@RequiredArgsConstructor
public class BookDatabaseProjector {

    private final BookRepository bookRepository;

    @EventHandler
    public void on(BookAddedEvent event) {
        bookRepository.save(
            new BookEntity()
                .setBookId(event.getBookId())
                .setIsbn(event.getIsbn())
                .setAuthor(event.getAuthor())
                .setTitle(event.getTitle())
        );
    }

    @EventHandler
    public void on(RentalStartedEvent event) {
        BookEntity book = bookRepository
            .findOneByBookId(event.getBookId().toString())
            .orElseThrow(EntityNotFoundException::new);

        book.setRented(true);

        bookRepository.save(book);
    }

    @EventHandler
    public void on(RentalEndedEvent event) {
        BookEntity book = bookRepository
            .findOneByBookId(event.getBookId().toString())
            .orElseThrow(EntityNotFoundException::new);

        book.setRented(false);

        bookRepository.save(book);
    }
}
