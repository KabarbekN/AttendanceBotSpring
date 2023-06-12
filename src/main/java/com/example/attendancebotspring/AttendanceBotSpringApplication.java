package com.example.attendancebotspring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AttendanceBotSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(AttendanceBotSpringApplication.class, args);
    }

}
