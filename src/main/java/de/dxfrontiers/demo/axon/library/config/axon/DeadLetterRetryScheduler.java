package de.dxfrontiers.demo.axon.library.config.axon;

import lombok.extern.slf4j.Slf4j;
import org.axonframework.config.EventProcessingConfiguration;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.messaging.deadletter.DeadLetter;
import org.axonframework.messaging.deadletter.SequencedDeadLetterProcessor;
import org.axonframework.messaging.deadletter.SequencedDeadLetterQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.Iterator;
import java.util.Optional;

@Component
@Slf4j
public class DeadLetterRetryScheduler {

    @Autowired
    private EventProcessingConfiguration config;

    @Scheduled(fixedDelay = 10000) // every 10s
    public void retryAllSequences() {
        log.info("executing scheduled dlq sweep");
        Optional<SequencedDeadLetterProcessor<EventMessage<?>>> optionalLetterProcessor =
            config.sequencedDeadLetterProcessor("book-db");
        if (!optionalLetterProcessor.isPresent()) {
            log.info("failed to get processor");

            return;
        }
        SequencedDeadLetterProcessor<EventMessage<?>> letterProcessor = optionalLetterProcessor.get();

        // Retrieve all the dead lettered event sequences:
        Iterable<Iterable<DeadLetter<? extends EventMessage<?>>>> deadLetterSequences =
            config.deadLetterQueue("book-db")
                .map(SequencedDeadLetterQueue::deadLetters)
                .orElseThrow(() -> new IllegalArgumentException("No such Processing Group"));
        log.info("iterating on sequendes...");
        // Iterate over all sequences:
        for (Iterable<DeadLetter<? extends EventMessage<?>>> sequence : deadLetterSequences) {
            Iterator<DeadLetter<? extends EventMessage<?>>> sequenceIterator = sequence.iterator();
            String firstLetterId = sequenceIterator.next()
                .message()
                .getIdentifier();

            // SequencedDeadLetterProcessor#process automatically retries an entire sequence.
            // Hence, we only need to filter on the first entry of the sequence:
            letterProcessor.process(deadLetter -> deadLetter.message().getIdentifier().equals(firstLetterId));
        }
    }
}
