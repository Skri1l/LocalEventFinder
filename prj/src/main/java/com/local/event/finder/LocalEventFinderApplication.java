package com.local.event.finder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LocalEventFinderApplication {
    public static void main(String[] args) {
        SpringApplication.run(LocalEventFinderApplication.class, args);
    }
}