package com.example.chattingroom.dto;

import com.example.chattingroom.enums.ErrorCode;
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

    // 기존 정적 팩토리 메소드 유지
    public static ResponseEntity<ErrorResponse> toResponseEntity(HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .body(new ErrorResponse(status.value(), status.name(), message));
    }

    // ErrorCode를 활용한 정적 팩토리 메소드 추가
    public static ResponseEntity<ErrorResponse> toResponseEntity(ErrorCode errorCode) {
        return ResponseEntity.status(errorCode.getStatus())
                .body(new ErrorResponse(errorCode.getStatus().value(), errorCode.getStatus().name(),
                        errorCode.getMessage()));
    }
}
