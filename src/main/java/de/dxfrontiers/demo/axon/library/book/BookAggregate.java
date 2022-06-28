package de.dxfrontiers.demo.axon.library.book;

import de.dxfrontiers.demo.axon.library.book.command.AddBookCommand;
import de.dxfrontiers.demo.axon.library.book.command.StartRentalCommand;
import de.dxfrontiers.demo.axon.library.book.event.BookAddedEvent;
import de.dxfrontiers.demo.axon.library.book.event.RentalStartedEvent;
import de.dxfrontiers.demo.axon.library.exception.DuplicateIdException;
import de.dxfrontiers.demo.axon.library.exception.EntityNotFoundException;
import de.dxfrontiers.demo.axon.library.exception.OperationNotPossibleException;
import de.dxfrontiers.demo.axon.library.persistence.ReaderRepository;
import lombok.Data;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventhandling.Timestamp;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateCreationPolicy;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.modelling.command.CreationPolicy;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
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

    private List<Rental> rentals = new ArrayList<>();

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

    @CommandHandler
    public void handle(
        StartRentalCommand command,
        @Autowired ReaderRepository readerRepository
    ) {
        requireExistingReader(readerRepository, command.getReaderId());
        requireNoActiveRental();

        Duration rentalDuration;
        if (type == Type.BOOK) {
            rentalDuration = Duration.of(28, ChronoUnit.DAYS);
        } else if (type == Type.MAGAZINE) {
            rentalDuration = Duration.of(7, ChronoUnit.DAYS);
        } else {
            rentalDuration = Duration.of(14, ChronoUnit.DAYS);
        }

        AggregateLifecycle.apply(
            RentalStartedEvent.builder()
                .bookId(command.getBookId())
                .readerId(command.getReaderId())
                .duration(rentalDuration)
                .build()
        );
    }

    private void requireExistingReader(ReaderRepository readerRepository, UUID readerId) {
        readerRepository.findOneByReaderId(readerId.toString()).orElseThrow(EntityNotFoundException::new);
    }

    private void requireNoActiveRental() {
        if (rentals.size() > 0 && rentals.get(rentals.size() - 1).isActive()) {
            throw new OperationNotPossibleException();
        }
    }

    @EventSourcingHandler
    public void on(
        RentalStartedEvent event,
        @Timestamp Instant instant
    ) {
        rentals.add(
            new Rental()
                .setReaderId(event.getReaderId())
                .setStartedAt(instant)
                .setExpectedReturnDate(instant.plus(event.getDuration()))
        );
    }

    public enum Type {
        BOOK,
        MAGAZINE,
        UNKNOWN,
    }

    @Data
    public static class Rental {
        private Instant startedAt;
        private UUID readerId;
        private Instant expectedReturnDate;
        private Instant returnedAt;

        public boolean isActive() {
            return returnedAt == null;
        }
    }
}
