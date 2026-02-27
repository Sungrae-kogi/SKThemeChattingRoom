package com.example.chattingroom.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageDTO {
    private String sender;
    private String content;

    @JsonFormat(pattern = "a h:mm", locale = "ko")
    private LocalDateTime sendTime;
}
