package de.dxfrontiers.demo.axon.library.reader.event;

import lombok.Builder;
import lombok.Value;
import org.axonframework.serialization.Revision;

import java.util.UUID;

@Revision("1")
@Value
@Builder
public class ReaderRegisteredEvent {

    private final UUID readerId;

    private final String name;

    private final String address;

}
