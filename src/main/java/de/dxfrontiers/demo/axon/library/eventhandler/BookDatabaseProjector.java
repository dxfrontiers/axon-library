package de.dxfrontiers.demo.axon.library.eventhandler;

import de.dxfrontiers.demo.axon.library.book.event.BookAddedEvent;
import de.dxfrontiers.demo.axon.library.config.axon.ProcessingGroups;
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
}
