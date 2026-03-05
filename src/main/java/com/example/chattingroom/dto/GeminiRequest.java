package com.example.chattingroom.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 멀티모달(Multimodal) 설계: 구글 Gemini는 텍스트만 읽는 게 아니라, 사진도 보고 영상도 봅니다. 그래서 parts(부품들)라는 배열 안에 text(글자)도 넣고, image(사진)도 동시에 넣어서 섞어 보낼 수 있도록 확장성을 고려한 아키텍처로 짜여 있는 것입니다.
 *
 * 대화 기록(History) 설계: contents가 배열인 이유는, "내가 예전에 한 말"과 "AI가 대답한 말"을 차곡차곡 쌓아서 한 번에 보내기 위함입니다.
 */

@Data
@AllArgsConstructor
public class GeminiRequest {

    // 내부 정적 클래스(static class) 구조

    //가장 바깥쪽 배열
    private List<Content> contents;

    @Data
    @AllArgsConstructor
    public static class Content{
        // 부품들 배열.
        private List<Part> parts;
    }

    @Data
    @AllArgsConstructor
    public static class Part {
        // 실제 텍스트 데이터   - AI에 질문하고 싶은 질문내용.
        private String text;
    }
}
