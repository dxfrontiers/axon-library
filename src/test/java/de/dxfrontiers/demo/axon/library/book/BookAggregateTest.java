package de.dxfrontiers.demo.axon.library.book;

import de.dxfrontiers.demo.axon.library.book.command.AddBookCommand;
import de.dxfrontiers.demo.axon.library.book.command.ExtendRentalCommand;
import de.dxfrontiers.demo.axon.library.book.command.StartRentalCommand;
import de.dxfrontiers.demo.axon.library.book.event.BookAddedEvent;
import de.dxfrontiers.demo.axon.library.book.event.RentalExtendedEvent;
import de.dxfrontiers.demo.axon.library.book.event.RentalStartedEvent;
import de.dxfrontiers.demo.axon.library.exception.DuplicateIdException;
import de.dxfrontiers.demo.axon.library.exception.EntityNotFoundException;
import de.dxfrontiers.demo.axon.library.exception.OperationNotPossibleException;
import de.dxfrontiers.demo.axon.library.exception.UnauthorizedAccessException;
import de.dxfrontiers.demo.axon.library.persistence.ReaderEntity;
import de.dxfrontiers.demo.axon.library.persistence.ReaderRepository;
import org.assertj.core.api.AssertionsForClassTypes;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookAggregateTest {

    private AggregateTestFixture<BookAggregate> fixture;

    @Mock
    private ReaderRepository readerRepository;

    @BeforeEach
    public void setup() {
        fixture = new AggregateTestFixture(BookAggregate.class);
        fixture.registerInjectableResource(readerRepository);
    }

    @Nested
    class Initialize {

        @Test
        public void initializeBook() {
            UUID bookId = UUID.randomUUID();

            fixture
                .givenNoPriorActivity()
                .when(
                    AddBookCommand.builder()
                        .bookId(bookId)
                        .isbn("some-isbn")
                        .author("Douglas Adams")
                        .title("The Hitchhiker's Guide to the Galaxy")
                        .type(BookAggregate.Type.BOOK)
                        .build()
                )
                .expectSuccessfulHandlerExecution()
                .expectEvents(
                    BookAddedEvent.builder()
                        .bookId(bookId)
                        .isbn("some-isbn")
                        .author("Douglas Adams")
                        .title("The Hitchhiker's Guide to the Galaxy")
                        .type(BookAggregate.Type.BOOK)
                        .build()
                )
                .expectState(book -> {
                    assertThat(book.getId()).isEqualTo(bookId);
                    AssertionsForClassTypes.assertThat(book.getIsbn()).isEqualTo("some-isbn");
                    AssertionsForClassTypes.assertThat(book.getAuthor()).isEqualTo("Douglas Adams");
                    AssertionsForClassTypes.assertThat(book.getTitle()).isEqualTo("The Hitchhiker's Guide to the Galaxy");
                    assertThat(book.getType()).isEqualTo(BookAggregate.Type.BOOK);
                });
        }

        @Test
        public void duplicateBookIdIsForbidden() {
            UUID bookId = UUID.randomUUID();

            fixture
                .given(
                    BookAddedEvent.builder()
                        .bookId(bookId)
                        .isbn("some-isbn")
                        .author("Douglas Adams")
                        .title("The Hitchhiker's Guide to the Galaxy")
                        .type(BookAggregate.Type.BOOK)
                        .build()
                )
                .when(
                    AddBookCommand.builder()
                        .bookId(bookId)
                        .isbn("some-other-isbn")
                        .author("Neal Stephenson")
                        .title("Cryptonomicon")
                        .build()
                )
                .expectNoEvents()
                .expectException(DuplicateIdException.class);
        }
    }

    @Nested
    class StartRental {
        @Test
        public void rentalForBooksIs4Weeks() {
            UUID bookId = UUID.randomUUID();
            UUID readerId = UUID.randomUUID();

            when(readerRepository.findOneByReaderId(readerId.toString()))
                .thenReturn(Optional.of(new ReaderEntity()));

            fixture
                .given(
                    BookAddedEvent.builder()
                        .bookId(bookId)
                        .isbn("some-isbn")
                        .author("Douglas Adams")
                        .title("The Hitchhiker's Guide to the Galaxy")
                        .type(BookAggregate.Type.BOOK)
                        .build()
                )
                .when(
                    StartRentalCommand.builder()
                        .bookId(bookId)
                        .readerId(readerId)
                        .build()
                )
                .expectSuccessfulHandlerExecution()
                .expectEvents(
                    RentalStartedEvent.builder()
                        .bookId(bookId)
                        .readerId(readerId)
                        .duration(Duration.of(28, ChronoUnit.DAYS))
                        .build()
                )
                .expectState(book -> {
                    assertThat(book.getRentals()).hasSize(1);
                    assertThat(book.getRentals().get(0)).satisfies(rental -> {
                        assertThat(rental.getReaderId()).isEqualTo(readerId);
                        assertThat(rental.getStartedAt()).isEqualTo(fixture.currentTime());
                        assertThat(rental.getExpectedReturnDate()).isEqualTo(fixture.currentTime().plus(Duration.of(28, ChronoUnit.DAYS)));
                    });
                });
        }

        @Test
        public void rentalForMagazinesIs1Week() {
            UUID bookId = UUID.randomUUID();
            UUID readerId = UUID.randomUUID();

            when(readerRepository.findOneByReaderId(readerId.toString()))
                .thenReturn(Optional.of(new ReaderEntity()));

            fixture
                .given(
                    BookAddedEvent.builder()
                        .bookId(bookId)
                        .isbn("some-isbn")
                        .author("Java Magazin")
                        .title("Ausgabe 1.2022")
                        .type(BookAggregate.Type.MAGAZINE)
                        .build()
                )
                .when(
                    StartRentalCommand.builder()
                        .bookId(bookId)
                        .readerId(readerId)
                        .build()
                )
                .expectSuccessfulHandlerExecution()
                .expectEvents(
                    RentalStartedEvent.builder()
                        .bookId(bookId)
                        .readerId(readerId)
                        .duration(Duration.of(7, ChronoUnit.DAYS))
                        .build()
                )
                .expectState(book -> {
                    assertThat(book.getRentals()).hasSize(1);
                    assertThat(book.getRentals().get(0)).satisfies(rental -> {
                        assertThat(rental.getReaderId()).isEqualTo(readerId);
                        assertThat(rental.getStartedAt()).isEqualTo(fixture.currentTime());
                        assertThat(rental.getExpectedReturnDate()).isEqualTo(fixture.currentTime().plus(Duration.of(7, ChronoUnit.DAYS)));
                    });
                });
        }

        @Test
        public void rentalForUnknownMediaIs2Weeks() {
            UUID bookId = UUID.randomUUID();
            UUID readerId = UUID.randomUUID();

            when(readerRepository.findOneByReaderId(readerId.toString()))
                .thenReturn(Optional.of(new ReaderEntity()));

            fixture
                .given(
                    BookAddedEvent.builder()
                        .bookId(bookId)
                        .isbn("some-isbn")
                        .author("Douglas Adams")
                        .title("The Hitchhiker's Guide to the Galaxy")
                        .type(BookAggregate.Type.UNKNOWN)
                        .build()
                )
                .when(
                    StartRentalCommand.builder()
                        .bookId(bookId)
                        .readerId(readerId)
                        .build()
                )
                .expectSuccessfulHandlerExecution()
                .expectEvents(
                    RentalStartedEvent.builder()
                        .bookId(bookId)
                        .readerId(readerId)
                        .duration(Duration.of(14, ChronoUnit.DAYS))
                        .build()
                )
                .expectState(book -> {
                    assertThat(book.getRentals()).hasSize(1);
                    assertThat(book.getRentals().get(0)).satisfies(rental -> {
                        assertThat(rental.getReaderId()).isEqualTo(readerId);
                        assertThat(rental.getStartedAt()).isEqualTo(fixture.currentTime());
                        assertThat(rental.getExpectedReturnDate()).isEqualTo(fixture.currentTime().plus(Duration.of(14, ChronoUnit.DAYS)));
                    });
                });
        }

        @Test
        public void rentalToUnknownReaderIsImpossible() {
            UUID bookId = UUID.randomUUID();
            UUID readerId = UUID.randomUUID();

            fixture
                .given(
                    BookAddedEvent.builder()
                        .bookId(bookId)
                        .isbn("some-isbn")
                        .author("Douglas Adams")
                        .title("The Hitchhiker's Guide to the Galaxy")
                        .type(BookAggregate.Type.BOOK)
                        .build()
                )
                .when(
                    StartRentalCommand.builder()
                        .bookId(bookId)
                        .readerId(readerId)
                        .build()
                )
                .expectNoEvents()
                .expectException(EntityNotFoundException.class);
        }

        @Test
        public void rentalForBookWithActiveRentalNotPossible() {
            UUID bookId = UUID.randomUUID();
            UUID readerId = UUID.randomUUID();

            when(readerRepository.findOneByReaderId(readerId.toString()))
                .thenReturn(Optional.of(new ReaderEntity()));

            fixture
                .given(
                    BookAddedEvent.builder()
                        .bookId(bookId)
                        .isbn("some-isbn")
                        .author("Douglas Adams")
                        .title("The Hitchhiker's Guide to the Galaxy")
                        .type(BookAggregate.Type.BOOK)
                        .build(),
                    RentalStartedEvent.builder()
                        .bookId(bookId)
                        .readerId(UUID.randomUUID())
                        .duration(Duration.of(14, ChronoUnit.DAYS))
                        .build()
                )
                .when(
                    StartRentalCommand.builder()
                        .bookId(bookId)
                        .readerId(readerId)
                        .build()
                )
                .expectNoEvents()
                .expectException(OperationNotPossibleException.class);
        }
    }

    @Nested
    class ExtendRental {

        @Test
        public void extensionDurationForBooksIs2Weeks() {
            UUID bookId = UUID.randomUUID();
            UUID readerId = UUID.randomUUID();

            fixture
                .given(
                    BookAddedEvent.builder()
                        .bookId(bookId)
                        .isbn("some-isbn")
                        .author("Douglas Adams")
                        .title("The Hitchhiker's Guide to the Galaxy")
                        .type(BookAggregate.Type.BOOK)
                        .build(),
                    RentalStartedEvent.builder()
                        .bookId(bookId)
                        .readerId(readerId)
                        .duration(Duration.of(28, ChronoUnit.DAYS))
                        .build()
                )
                .when(
                    ExtendRentalCommand.builder()
                        .bookId(bookId)
                        .readerId(readerId)
                        .build()
                )
                .expectSuccessfulHandlerExecution()
                .expectEvents(
                    RentalExtendedEvent.builder()
                        .bookId(bookId)
                        .extendDuration(Duration.of(14, ChronoUnit.DAYS))
                        .build()
                );
        }

        @Test
        public void extensionDurationForUnknownMediaIs1Week() {
            UUID bookId = UUID.randomUUID();
            UUID readerId = UUID.randomUUID();

            fixture
                .given(
                    BookAddedEvent.builder()
                        .bookId(bookId)
                        .isbn("some-isbn")
                        .author("Douglas Adams")
                        .title("The Hitchhiker's Guide to the Galaxy")
                        .type(BookAggregate.Type.UNKNOWN)
                        .build(),
                    RentalStartedEvent.builder()
                        .bookId(bookId)
                        .readerId(readerId)
                        .duration(Duration.of(14, ChronoUnit.DAYS))
                        .build()
                )
                .when(
                    ExtendRentalCommand.builder()
                        .bookId(bookId)
                        .readerId(readerId)
                        .build()
                )
                .expectSuccessfulHandlerExecution()
                .expectEvents(
                    RentalExtendedEvent.builder()
                        .bookId(bookId)
                        .extendDuration(Duration.of(7, ChronoUnit.DAYS))
                        .build()
                );
        }

        @Test
        public void extensionForMagazinesNotPossible() {
            UUID bookId = UUID.randomUUID();
            UUID readerId = UUID.randomUUID();

            fixture
                .given(
                    BookAddedEvent.builder()
                        .bookId(bookId)
                        .isbn("some-isbn")
                        .author("Douglas Adams")
                        .title("The Hitchhiker's Guide to the Galaxy")
                        .type(BookAggregate.Type.MAGAZINE)
                        .build(),
                    RentalStartedEvent.builder()
                        .bookId(bookId)
                        .readerId(readerId)
                        .duration(Duration.of(7, ChronoUnit.DAYS))
                        .build()
                )
                .when(
                    ExtendRentalCommand.builder()
                        .bookId(bookId)
                        .readerId(readerId)
                        .build()
                )
                .expectNoEvents()
                .expectException(OperationNotPossibleException.class);
        }

        @Test
        @Disabled
        public void extensionForNotRentedBookNotPossible() {
            UUID bookId = UUID.randomUUID();
            UUID readerId = UUID.randomUUID();

            fixture
                .given(
                    BookAddedEvent.builder()
                        .bookId(bookId)
                        .isbn("some-isbn")
                        .author("Douglas Adams")
                        .title("The Hitchhiker's Guide to the Galaxy")
                        .type(BookAggregate.Type.MAGAZINE)
                        .build(),
                    RentalStartedEvent.builder()
                        .bookId(bookId)
                        .readerId(readerId)
                        .duration(Duration.of(7, ChronoUnit.DAYS))
                        .build()
//                    RentalEndedEvent.builder()
//                        .bookId(bookId)
//                        .build()
                )
                .when(
                    ExtendRentalCommand.builder()
                        .bookId(bookId)
                        .readerId(readerId)
                        .build()
                )
                .expectNoEvents()
                .expectException(OperationNotPossibleException.class);
        }

        @Test
        public void extensionNotPossibleForOtherUserThanLendingUser() {
            UUID bookId = UUID.randomUUID();
            UUID readerId = UUID.randomUUID();

            fixture
                .given(
                    BookAddedEvent.builder()
                        .bookId(bookId)
                        .isbn("some-isbn")
                        .author("Douglas Adams")
                        .title("The Hitchhiker's Guide to the Galaxy")
                        .type(BookAggregate.Type.MAGAZINE)
                        .build(),
                    RentalStartedEvent.builder()
                        .bookId(bookId)
                        .readerId(readerId)
                        .duration(Duration.of(7, ChronoUnit.DAYS))
                        .build()
                )
                .when(
                    ExtendRentalCommand.builder()
                        .bookId(bookId)
                        .readerId(UUID.randomUUID())
                        .build()
                )
                .expectNoEvents()
                .expectException(UnauthorizedAccessException.class);
        }
    }
}
