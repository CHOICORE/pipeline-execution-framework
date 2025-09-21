package me.choicore.samples;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
class Application {
    void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
