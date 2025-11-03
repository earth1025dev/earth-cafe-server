package com.example.earthcafeserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.example.earthcafeserver")
@EnableJpaRepositories(basePackages = "com.example.earthcafeserver")
public class EarthCafeServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EarthCafeServerApplication.class, args);
    }
}