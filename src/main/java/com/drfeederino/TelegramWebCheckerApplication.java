package com.drfeederino;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TelegramWebCheckerApplication {

    public static void main(String[] args) {
        SpringApplication.run(
                TelegramWebCheckerApplication.class, args);
    }

}
