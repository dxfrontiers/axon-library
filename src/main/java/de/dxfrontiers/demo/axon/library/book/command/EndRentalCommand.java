package de.dxfrontiers.demo.axon.library.book.command;

import lombok.Builder;
import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.UUID;

@Value
@Builder
public class EndRentalCommand {

    @TargetAggregateIdentifier
    private final UUID bookId;
}
