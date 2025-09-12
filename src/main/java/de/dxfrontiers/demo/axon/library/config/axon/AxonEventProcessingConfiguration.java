package de.dxfrontiers.demo.axon.library.config.axon;

import org.axonframework.common.jpa.EntityManagerProvider;
import org.axonframework.common.transaction.TransactionManager;
import org.axonframework.config.ConfigurerModule;
import org.axonframework.eventhandling.deadletter.jpa.JpaSequencedDeadLetterQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


    @Configuration
    public class AxonEventProcessingConfiguration {
        // omitting other configuration methods...
        @Bean
        public ConfigurerModule deadLetterQueueConfigurerModule() {
            // Replace "my-processing-group" for the processing group you want to configure the DLQ on.
            return configurer ->
                configurer.eventProcessing().registerDeadLetterQueue(
                    "book-db",
                    config -> JpaSequencedDeadLetterQueue.builder()
                        .processingGroup("book-db")
                        .maxSequences(5)
                        .maxSequenceSize(5)
                        .entityManagerProvider(config.getComponent(EntityManagerProvider.class))
                        .transactionManager(config.getComponent(TransactionManager.class))
                        .serializer(config.serializer())
                        .build()
                );
        }


        @Bean
        public ConfigurerModule enqueuePolicyConfigurerModule() {
            // Replace "my-processing-group" for the processing group you want to configure the policy on.
            return configurer -> configurer.eventProcessing()
                .registerDeadLetterPolicy("book-db", config -> new CustomEnqueuePolicy());
        }
    }



