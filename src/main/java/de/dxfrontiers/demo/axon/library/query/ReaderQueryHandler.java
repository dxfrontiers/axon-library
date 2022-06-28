package de.dxfrontiers.demo.axon.library.query;

import de.dxfrontiers.demo.axon.library.persistence.ReaderEntity;
import de.dxfrontiers.demo.axon.library.persistence.ReaderRepository;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ReaderQueryHandler {

    private final ReaderRepository readerRepository;

    @QueryHandler
    public ReaderEntity findAllReaders(FindReaderQuery query) {
        return readerRepository
            .findOneByReaderId(query.id.toString())
            .orElse(null);
    }

    @Builder
    @Data
    public static final class FindReaderQuery {
        private final UUID id;
    }
}
