package com.example.chattingroom.service;

import com.example.chattingroom.dto.GeminiRequest;
import com.example.chattingroom.dto.GeminiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Service
public class GeminiService {
    private final WebClient webClient;

    @Value("${gemini.api.url}")
    private String apiUrl;

    @Value("${gemini.api.key}")
    private String apiKey;

    public GeminiService(WebClient webClient) {
        this.webClient = webClient;
    }

    // Mono를 반환하는 비동기 메서드
    public Mono<String> summarizeChat(String chatHistory) {

        // AI에게 내릴 프롬프트 작성
        String prompt = "너는 친절한 채팅방 요약 봇이야. " + "다음 대화 내용을 3줄로 요약해줘:\n" + chatHistory;

        // 구글 규격에 맞는 DTO 조립.    -> static으로 선언해서 상위 범위의 인스턴스가 필요가없다.
        GeminiRequest.Part part = new GeminiRequest.Part(prompt);
        GeminiRequest.Content content = new GeminiRequest.Content(Collections.singletonList(part));
        GeminiRequest requestDto = new GeminiRequest(Collections.singletonList(content));

        // WebClient로 전송
        return webClient.post().uri(apiUrl + "?key=" + apiKey).bodyValue(requestDto).retrieve().bodyToMono(GeminiResponse.class)
                // 구글의 응답을 Response DTO로 받고 text를 빼냅니다.
                .map(response -> response.getCandidates().get(0)
                        .getContent().getParts().get(0)
                        .getText());
    }
}
