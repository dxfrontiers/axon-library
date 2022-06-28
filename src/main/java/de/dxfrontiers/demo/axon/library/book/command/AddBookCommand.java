package de.dxfrontiers.demo.axon.library.book.command;

import de.dxfrontiers.demo.axon.library.book.BookAggregate;
import lombok.Builder;
import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.UUID;

@Value
@Builder
public class AddBookCommand {

    @TargetAggregateIdentifier
    @Builder.Default
    private final UUID bookId = UUID.randomUUID();

    private final String isbn;
    private final String author;
    private final String title;

    private final BookAggregate.Type type;
}
