package de.dxfrontiers.demo.axon.library.reader;

import de.dxfrontiers.demo.axon.library.exception.DuplicateIdException;
import de.dxfrontiers.demo.axon.library.reader.command.RegisterReaderCommand;
import de.dxfrontiers.demo.axon.library.reader.event.ReaderRegisteredEvent;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class ReaderAggregateTest {

    private AggregateTestFixture<ReaderAggregate> fixture = new AggregateTestFixture(ReaderAggregate.class);

    @Test
    public void registerReader() {
        UUID readerId = UUID.randomUUID();

        fixture
            .givenNoPriorActivity()
            .when(
                RegisterReaderCommand.builder()
                    .readerId(readerId)
                    .name("Homer Simpson")
                    .address("Evergreen Terrace 742, 1337 Springfield")
                    .build()
            )
            .expectSuccessfulHandlerExecution()
            .expectEvents(
                ReaderRegisteredEvent.builder()
                    .readerId(readerId)
                    .name("Homer Simpson")
                    .address("Evergreen Terrace 742, 1337 Springfield")
                    .build()
            )
            .expectState(reader -> {
                assertThat(reader.getId()).isEqualTo(readerId);
                assertThat(reader.getName()).isEqualTo("Homer Simpson");
                assertThat(reader.getAddress()).isEqualTo("Evergreen Terrace 742, 1337 Springfield");
                assertThat(reader.getRegisteredAt()).isEqualTo(fixture.currentTime());
            });
    }

    @Test
    public void duplicateReaderIdIsForbidden() {
        UUID readerId = UUID.randomUUID();

        fixture
            .given(
                ReaderRegisteredEvent.builder()
                    .readerId(readerId)
                    .name("Homer Simpson")
                    .address("Evergreen Terrace 742, 1337 Springfield")
                    .build()
            )
            .when(
                RegisterReaderCommand.builder()
                    .readerId(readerId)
                    .name("Harry Potter")
                    .address("Privet Drive 4, 4242 Little Whingings")
                    .build()
            )
            .expectNoEvents()
            .expectException(DuplicateIdException.class);
    }
}
