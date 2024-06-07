package com.example.gpq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class GpqApplication {

    public static void main(String[] args) {
        SpringApplication.run(GpqApplication.class, args);
    }

}
