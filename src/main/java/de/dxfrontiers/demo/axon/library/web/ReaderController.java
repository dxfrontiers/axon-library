package de.dxfrontiers.demo.axon.library.web;

import de.dxfrontiers.demo.axon.library.reader.command.RegisterReaderCommand;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/readers")
@RequiredArgsConstructor
public class ReaderController {

    private final CommandGateway commandGateway;

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
