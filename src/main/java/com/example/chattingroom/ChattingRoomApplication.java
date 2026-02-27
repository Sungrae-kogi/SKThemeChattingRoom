package com.example.chattingroom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class ChattingRoomApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChattingRoomApplication.class, args);
    }

}
