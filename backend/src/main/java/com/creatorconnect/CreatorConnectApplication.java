package com.creatorconnect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * CreatorConnect - Influencer Marketing Marketplace Platform.
 *
 * Entry point for the Spring Boot application. Bootstraps the embedded
 * server, component scanning, JPA repositories and security configuration.
 */
@SpringBootApplication
@EnableAsync
public class CreatorConnectApplication {

    public static void main(String[] args) {
        SpringApplication.run(CreatorConnectApplication.class, args);
    }
}
