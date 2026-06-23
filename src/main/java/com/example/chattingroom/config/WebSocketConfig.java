package com.example.chattingroom.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/*
    직접 메소드를 호출하는 곳은 X, 스프링 프레임워크가 알아서 호출.
    자동으로 Configuration 등록된 빈을 찾아서 ApplicationContext에 등록한 뒤, registerWebSocketHandlers로 /chat이라는 대기실을 만들어 두고. 
    브라우저 측에서 new WebSocket("/chat") 을 호출하기를 대기함.
*/
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    // 소켓 파이프가 열린 이후는 chatHandler가 모든 일을 전담, 채팅시 ChatHandler 내부의 handleTextMessage가
    // 동작
    @Autowired
    private ChatHandler chatHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // "/chat" 주소로 통신 파이프를 엽니다.
        registry.addHandler(chatHandler, "/chat").setAllowedOrigins("*");
    }
}
