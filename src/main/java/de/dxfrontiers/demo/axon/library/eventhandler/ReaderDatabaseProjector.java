package de.dxfrontiers.demo.axon.library.eventhandler;

import de.dxfrontiers.demo.axon.library.config.axon.ProcessingGroups;
import de.dxfrontiers.demo.axon.library.persistence.ReaderEntity;
import de.dxfrontiers.demo.axon.library.persistence.ReaderRepository;
import de.dxfrontiers.demo.axon.library.reader.event.ReaderRegisteredEvent;
import lombok.RequiredArgsConstructor;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

@Component
@ProcessingGroup(ProcessingGroups.READER_DB)
@RequiredArgsConstructor
public class ReaderDatabaseProjector {

    private final ReaderRepository readerRepository;

    @EventHandler
    public void on(ReaderRegisteredEvent event) {
        readerRepository.save(
            new ReaderEntity()
                .setReaderId(event.getReaderId())
                .setName(event.getName())
                .setAddress(event.getAddress())
        );
    }
}
