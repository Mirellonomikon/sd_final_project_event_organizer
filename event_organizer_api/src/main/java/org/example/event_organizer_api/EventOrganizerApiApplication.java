package org.example.event_organizer_api;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EventOrganizerApiApplication {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();
        System.setProperty("spring.mail.username", dotenv.get("SPRING_MAIL_USERNAME"));
        System.setProperty("spring.mail.password", dotenv.get("SPRING_MAIL_PASSWORD"));
        SpringApplication.run(EventOrganizerApiApplication.class, args);
    }

}
