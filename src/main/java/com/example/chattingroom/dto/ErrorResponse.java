package com.example.chattingroom.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private int status;
    private String error;
    private String message;

    // 정적 팩토리 메소드
    public static ResponseEntity<ErrorResponse> toResponseEntity(HttpStatus status, String message){
        return ResponseEntity.status(status)
                .body(new ErrorResponse(status.value(), status.name(), message));
    }
}
