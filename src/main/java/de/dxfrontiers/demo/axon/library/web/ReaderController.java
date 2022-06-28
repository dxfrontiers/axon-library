package de.dxfrontiers.demo.axon.library.web;

import de.dxfrontiers.demo.axon.library.persistence.ReaderEntity;
import de.dxfrontiers.demo.axon.library.persistence.ReaderRepository;
import de.dxfrontiers.demo.axon.library.query.ReaderQueryHandler;
import de.dxfrontiers.demo.axon.library.reader.command.RegisterReaderCommand;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/readers")
@RequiredArgsConstructor
public class ReaderController {

    private final CommandGateway commandGateway;
    private final ReaderRepository readerRepository;
    private final QueryGateway queryGateway;

    @GetMapping
    public List<ReaderEntity> listReaders() {
        return readerRepository.findAll();
    }

    @GetMapping("/{id}")
    public ReaderEntity listReadersAxon(@PathVariable UUID id) throws ExecutionException, InterruptedException {
        return queryGateway.query(
            ReaderQueryHandler.FindReaderQuery.builder().id(id).build(),
            ReaderEntity.class
        ).get();
    }

    @PostMapping("/register-command")
    public void registerReader(
        @RequestParam String name,
        @RequestParam String address
    ) {
        commandGateway.sendAndWait(
            RegisterReaderCommand.builder()
                .name(name)
                .address(address)
                .build()
        );
    }
}
