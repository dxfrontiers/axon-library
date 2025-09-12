package de.dxfrontiers.demo.axon.library.config.axon;

import de.dxfrontiers.demo.axon.library.book.event.BookAddedEvent;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.messaging.deadletter.DeadLetter;
import org.axonframework.messaging.deadletter.Decisions;
import org.axonframework.messaging.deadletter.EnqueueDecision;
import org.axonframework.messaging.deadletter.EnqueuePolicy;

import java.time.Duration;
import java.time.Instant;

@Slf4j
public class CustomEnqueuePolicy implements EnqueuePolicy<EventMessage<?>> {
    @Override
    public EnqueueDecision<EventMessage<?>> decide(DeadLetter<? extends EventMessage<?>> letter, Throwable cause) {
        if (cause instanceof NullPointerException) {
            return Decisions.doNotEnqueue();
        }
        if (letter.message().getPayload() instanceof BookAddedEvent &&
            letter.enqueuedAt().isAfter(Instant.now().plus(Duration.ofMinutes(5L)))) {
            return Decisions.evict();
        }
        return Decisions.enqueue(cause);
    }
}
