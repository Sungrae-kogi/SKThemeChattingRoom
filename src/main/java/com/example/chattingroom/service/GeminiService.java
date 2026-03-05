package com.example.chattingroom.service;

import com.example.chattingroom.dto.GeminiRequest;
import com.example.chattingroom.dto.GeminiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class GeminiService {
    // 객체 주입에는 final을 붙임, RequiredArgsConstructor가 알아서 생성.
    private final WebClient webClient;

    @Value("${gemini.api.url}")
    private String apiUrl;

    @Value("${gemini.api.key}")
    private String apiKey;

    // Mono를 반환하는 비동기 메서드
    public Mono<String> summarizeChat(String chatHistory) {

        // AI에게 내릴 프롬프트 작성
        String prompt = "너는 친절한 채팅방 요약 봇이야. " + "다음 대화 내용을 3줄로 요약해줘:\n" + chatHistory;

        // 구글 규격에 맞는 DTO 조립.    -> static으로 선언해서 상위 범위의 인스턴스가 필요가없다.
        GeminiRequest.Part part = new GeminiRequest.Part(prompt);
        GeminiRequest.Content content = new GeminiRequest.Content(Collections.singletonList(part));
        GeminiRequest requestDto = new GeminiRequest(Collections.singletonList(content));

        // ▼ 실무 정석(인코딩 방어): URI.create()를 사용하여 스프링의 자동 변환을 강제로 막습니다!
        URI targetUri = URI.create(apiUrl.trim() + "?key=" + apiKey.trim());

        return webClient.post()
                .uri(targetUri) // 변환이 금지된 순수 URI 객체를 통째로 넣습니다.
                .bodyValue(requestDto)
                .retrieve()
                .bodyToMono(GeminiResponse.class)
                .map(response -> response
                        .getCandidates().get(0)
                        .getContent().getParts().get(0)
                        .getText());
    }
}
