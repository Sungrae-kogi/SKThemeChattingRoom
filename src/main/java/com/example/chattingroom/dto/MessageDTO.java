package com.example.chattingroom.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageDTO {
    private String sender;
    private String content;

    private LocalDateTime sendTime;
}
