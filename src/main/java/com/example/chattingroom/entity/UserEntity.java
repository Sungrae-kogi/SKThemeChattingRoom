package com.example.chattingroom.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {
    private Long id;
    private String username;
    private String password;
    private LocalDateTime createdAt;
}
