package de.dxfrontiers.demo.axon.library.book.event;

import de.dxfrontiers.demo.axon.library.book.BookAggregate;
import lombok.Builder;
import lombok.Value;
import org.axonframework.serialization.Revision;

import java.util.UUID;

@Revision("1")
@Value
@Builder
public class BookAddedEvent {

    private final UUID bookId;

    private final String isbn;
    private final String author;
    private final String title;

    private final BookAggregate.Type type;
}
