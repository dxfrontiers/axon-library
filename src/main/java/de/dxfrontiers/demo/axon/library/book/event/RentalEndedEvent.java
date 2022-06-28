package de.dxfrontiers.demo.axon.library.book.event;

import lombok.Builder;
import lombok.Value;
import org.axonframework.serialization.Revision;

import java.util.UUID;

@Revision("1")
@Value
@Builder
public class RentalEndedEvent {

    private final UUID bookId;
}
