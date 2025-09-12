package de.dxfrontiers.demo.axon.library.config.axon;

import lombok.extern.slf4j.Slf4j;
import org.axonframework.config.EventProcessingConfigurer;
import org.axonframework.eventhandling.ErrorHandler;
import org.axonframework.eventhandling.ListenerInvocationErrorHandler;
import org.axonframework.eventhandling.PropagatingErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class AxonEventProcessingConfiguration {

    /**
     * Axon, by default, registers {@link org.axonframework.eventhandling.LoggingErrorHandler}
     * as {@link EventProcessingConfigurer#registerDefaultListenerInvocationErrorHandler default listener invocation handler},
     * which simply logs {@link org.axonframework.eventhandling.EventHandler} errors and proceeds
     * with the next event.
     *
     * This method registers a wrapped {@link PropagatingErrorHandler}, which logs <em>and</em> rethrows any errors, thus
     * enforcing retry error mode within {@link org.axonframework.eventhandling.TrackingEventProcessor}. The same is
     * registered as {@link ErrorHandler} for the event processor itself to ensure that {@link Error}s, which are not covered
     * by {@link ListenerInvocationErrorHandler}s, are logged as well. The reason for having two layers of logging in place
     * is that the event handler level is called for a single event failing and earlier, thus preserving contextual information
     * such as tracing information for the logs.
     *
     * @see <a href="https://docs.axoniq.io/reference-guide/configuring-infrastructure-components/event-processing/event-processors">Axon Documentation</a>
     */
    @Autowired
    public void configureErrorPropagation(EventProcessingConfigurer config) {
        config.registerDefaultErrorHandler(configuration ->
            (ErrorHandler) ctx -> {
                if (ctx.error() instanceof Error) {
                    log.error("Releasing claim on token potentially, due to failed event processor: {}",
                        ctx.eventProcessor(), ctx.error());
                }
                PropagatingErrorHandler.INSTANCE.handleError(ctx);
            }
        );

        config.registerDefaultListenerInvocationErrorHandler(configuration ->
            (ListenerInvocationErrorHandler) (exception, event, eventHandler) -> {
                log.warn("Releasing claim on token potentially, due to failed event handler [{}] failed to handle event [{}] ({}): {}",
                    eventHandler.getTargetType().getSimpleName(),
                    event.getIdentifier(),
                    event.getPayloadType().getName(),
                    event.getPayload(),
                    exception);
                PropagatingErrorHandler.INSTANCE.onError(exception, event, eventHandler);
            }
        );
    }
}
