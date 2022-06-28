package de.dxfrontiers.demo.axon.library.book.command;

import lombok.Builder;
import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.UUID;

@Value
@Builder
public class ExtendRentalCommand {

    @TargetAggregateIdentifier
    private final UUID bookId;

    private final UUID readerId;
}
