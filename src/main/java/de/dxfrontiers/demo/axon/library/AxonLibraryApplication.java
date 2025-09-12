package de.dxfrontiers.demo.axon.library;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AxonLibraryApplication {

    public static void main(String[] args) {
        SpringApplication.run(AxonLibraryApplication.class, args);
    }

}
