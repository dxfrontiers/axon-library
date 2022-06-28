package de.dxfrontiers.demo.axon.library.book.event;

import lombok.Builder;
import lombok.Value;
import org.axonframework.serialization.Revision;

import java.time.Duration;
import java.util.UUID;

@Revision("1")
@Value
@Builder
public class RentalExtendedEvent {

    private final UUID bookId;
    private final Duration extendDuration;
}
