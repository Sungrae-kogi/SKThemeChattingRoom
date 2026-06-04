package com.example.chattingroom.config;

import com.example.chattingroom.dto.ErrorResponse;
import com.example.chattingroom.enums.ErrorCode;
import com.example.chattingroom.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.reactive.function.client.WebClientException;

// 이 클래스는 모든 컨트롤러에서 발생하는 에러를 감시하고 캐치합니다.
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * ErrorResponse 내부에 Status 를 담았음에도, ResponseEntity.status()에 status를 반복해서 적은
     * 이유는. 수신자가 다르기 때문.
     * ResponseEntity.status()의 status는 이 통신을 브라우저가 받았을때 '브라우저'가 통신의 상태를 파악하기 위함이고.
     *
     * ErrorResponse에 담았던 status는 해당 통신을 받아서 처리할 JavaScript가 읽기 편하도록 JSON형식으로 담아준것.
     *
     * 따라서, 실제로는 이런 반복적인 타이핑을 해결하기위해 "정적 팩토리 메소드"를 사용.
     * 
     * @param exception
     * @return
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxSizeException(MaxUploadSizeExceededException exception) {
        log.warn("파일 용량 초과: {}", exception.getMessage());

        return ErrorResponse.toResponseEntity(ErrorCode.FILE_SIZE_EXCEEDED);
    }

    // 외부 API 통신 폭발 방어
    @ExceptionHandler(WebClientException.class)
    public ResponseEntity<ErrorResponse> handleWebClientException(WebClientException exception) {
        log.error("Gemini API 통신 오류 발생: {}", exception.getMessage());

        return ErrorResponse.toResponseEntity(ErrorCode.AI_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException exception) {
        log.warn("잘못된 요청: {}", exception.getMessage());

        return ErrorResponse.toResponseEntity(ErrorCode.INVALID_REQUEST);
    }

    // 비즈니스 공통 예외 처리
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException exception) {
        log.warn("비즈니스 오류 발생: {}", exception.getMessage());

        return ErrorResponse.toResponseEntity(exception.getErrorCode());
    }

    // 깔때기구조, 세세한 범위 of 오류를 잡다가 마지막에는 큰 범위로.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllException(Exception exception) {
        log.error("서버 내부 오류 발생: ", exception);

        return ErrorResponse.toResponseEntity(ErrorCode.INTERNAL_SERVER_ERROR);
    }
}
