package de.dxfrontiers.demo.axon.library.reader.command;

import lombok.Builder;
import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.UUID;

@Value
@Builder
public class RegisterReaderCommand {

    @TargetAggregateIdentifier
    @Builder.Default
    private final UUID readerId = UUID.randomUUID();

    private final String name;

    private final String address;
}
