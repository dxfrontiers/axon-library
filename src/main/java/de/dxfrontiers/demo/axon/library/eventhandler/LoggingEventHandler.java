package de.dxfrontiers.demo.axon.library.eventhandler;

import de.dxfrontiers.demo.axon.library.config.axon.ProcessingGroups;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ProcessingGroup(ProcessingGroups.LOGGING)
public class LoggingEventHandler {

    @EventHandler
    public void on(Object event) {
        log.info("{}", event);
    }
}
