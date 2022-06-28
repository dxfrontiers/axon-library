package de.dxfrontiers.demo.axon.library.book;

import de.dxfrontiers.demo.axon.library.book.command.AddBookCommand;
import de.dxfrontiers.demo.axon.library.book.event.BookAddedEvent;
import de.dxfrontiers.demo.axon.library.exception.DuplicateIdException;
import de.dxfrontiers.demo.axon.library.persistence.ReaderRepository;
import org.assertj.core.api.AssertionsForClassTypes;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

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
}
