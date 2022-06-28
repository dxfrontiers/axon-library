package de.dxfrontiers.demo.axon.library.reader;

import de.dxfrontiers.demo.axon.library.exception.DuplicateIdException;
import de.dxfrontiers.demo.axon.library.reader.command.RegisterReaderCommand;
import de.dxfrontiers.demo.axon.library.reader.event.ReaderRegisteredEvent;
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
@Aggregate(type = ReaderAggregate.TYPE)
public class ReaderAggregate {

    public static final String TYPE = "Reader";

    @AggregateIdentifier
    private UUID id;

    private String name;

    private String address;

    private Instant registeredAt;

    @CommandHandler
    @CreationPolicy(AggregateCreationPolicy.CREATE_IF_MISSING)
    public void initialize(RegisterReaderCommand command) {
        requireNotInitialized();

        AggregateLifecycle.apply(
            ReaderRegisteredEvent.builder()
                .readerId(command.getReaderId())
                .name(command.getName())
                .address(command.getAddress())
                .build()
        );
    }

    @EventSourcingHandler
    public void on(
        ReaderRegisteredEvent event,
        @Timestamp Instant instant
    ) {
        id = event.getReaderId();

        name = event.getName();
        address = event.getAddress();

        registeredAt = instant;
    }

    private void requireNotInitialized() {
        if (id != null) {
            throw new DuplicateIdException();
        }
    }
}
