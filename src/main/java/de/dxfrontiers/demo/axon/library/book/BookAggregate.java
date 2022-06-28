package de.dxfrontiers.demo.axon.library.book;

import de.dxfrontiers.demo.axon.library.book.command.AddBookCommand;
import de.dxfrontiers.demo.axon.library.book.event.BookAddedEvent;
import de.dxfrontiers.demo.axon.library.exception.DuplicateIdException;
import lombok.Data;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventhandling.Timestamp;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateCreationPolicy;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.modelling.command.CreationPolicy;
import org.axonframework.spring.stereotype.Aggregate;

import java.time.Instant;
import java.util.UUID;

@Data
@Aggregate(type = BookAggregate.TYPE)
public class BookAggregate {

    public static final String TYPE = "Book";

    @AggregateIdentifier
    private UUID id;

    private String isbn;
    private String author;
    private String title;

    private Type type;

    private Instant addedAt;

    @CommandHandler
    @CreationPolicy(AggregateCreationPolicy.CREATE_IF_MISSING)
    public void initialize(AddBookCommand command) {
        requireNotInitialized();

        AggregateLifecycle.apply(
            BookAddedEvent.builder()
                .bookId(command.getBookId())
                .isbn(command.getIsbn())
                .author(command.getAuthor())
                .title(command.getTitle())
                .type(command.getType() == null ? Type.UNKNOWN : command.getType())
                .build()
        );
    }

    private void requireNotInitialized() {
        if (id != null) {
            throw new DuplicateIdException();
        }
    }

    @EventSourcingHandler
    public void on(
        BookAddedEvent event,
        @Timestamp Instant instant
    ) {
        id = event.getBookId();

        isbn = event.getIsbn();
        author = event.getAuthor();
        title = event.getTitle();

        type = event.getType();

        addedAt = instant;
    }

    public enum Type {
        BOOK,
        MAGAZINE,
        UNKNOWN,
    }
}
