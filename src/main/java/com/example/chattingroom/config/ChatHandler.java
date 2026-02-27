package com.example.chattingroom.config;

import com.example.chattingroom.dto.MessageDTO;
import com.example.chattingroom.service.ChatService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URLDecoder;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatHandler extends TextWebSocketHandler {

    @Autowired
    private ChatService chatService;

    // 1. 로그 기록용 로거
    private static final Logger log =
            LoggerFactory.getLogger(ChatHandler.class);

    // 2. 세션과 닉네임을 짝지어 기억하는 명부 (동시성 안전)
    private static final Map<WebSocketSession, String>
            sessionMap = new ConcurrentHashMap<>();

    // 자동 포장 기계
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature
                    .WRITE_DATES_AS_TIMESTAMPS);

    public ChatHandler(ChatService chatService){
        this.chatService = chatService;
    }

    @Override
    public void afterConnectionEstablished(
            WebSocketSession session) throws Exception {

        log.info("[접속 성공] 세션 ID: {}",
                session.getId());

        // URL에서 닉네임 안전하게 추출 (?name=홍길동)
        String query = session.getUri().getQuery();
        String name = "익명";

        if (query != null && query.contains("name=")) {
            name = query.substring(
                    query.indexOf("name=") + 5);
            name = URLDecoder.decode(name, "UTF-8");
        }

        // 명부에 세션과 닉네임 등록
        sessionMap.put(session, name);

        // 전원에게 갱신된 접속자 명단 쏘기
        broadcastUserList();

        List<MessageDTO> history = chatService.getRecentMessages();

        // 새로 입장한 사람에게 이전 채팅히스토리 제공.
        for(MessageDTO dto : history){
            // 받아온 DTO를 JSON 문자열로 변환.
            String jsonStr = mapper.writeValueAsString(dto);

            // 본인에게만 전송.    ->  본인 session
            session.sendMessage(new TextMessage(jsonStr));
        }
    }

    @Override
    protected void handleTextMessage(
            WebSocketSession session,
            TextMessage message) throws Exception {

        String payload = message.getPayload();
        // 로그 출력 (내용은 DEBUG, 행위는 INFO)
        log.debug("[메시지 수신] 내용: {}", payload);

        log.info("[전송 처리] 세션 ID: {}, 길이: {} Byte",
                session.getId(), payload.length());

        // 1. 시스템 메시지 (명단 업데이트 등) 처리
        // 프론트에서 단순 문자열로 보내고 있다면 이 조건을 탑니다.
        if (payload.startsWith("[USERS]")) {
            for (WebSocketSession s : sessionMap.keySet()) {
                s.sendMessage(new TextMessage(payload));
            }
            // 시스템 메시지 처리가 끝났으니 여기서 메서드 종료!
            return;
        }

        // 2. 수신된 JSON 텍스트 해체 (역직렬화)

        // JsonNode : JSON 데이터의 트리 구조를 표현
        JsonNode node = mapper.readTree(payload);
        String sender = node.get("sender").asText();
        String content = node.get("content").asText();

        // 3. 백그라운드 스레드에 DB 저장 지시 (비동기)
        chatService.saveMessageAsync(sender, content);

        // 4. 남들에게 방송할 새로운 DTO 상자 조립
        MessageDTO broadcastDto = new MessageDTO();
        broadcastDto.setSender(sender);
        broadcastDto.setContent(content);

        // ★ 핵심: 방송하는 시점의 서버 시간을 정확히 찍어줍니다!
        // (이 덕분에 DB 시간과 화면 시간이 일치하게 됩니다)
        broadcastDto.setSendTime(LocalDateTime.now());

        // 5. 조립된 상자를 다시 JSON 문자열로 포장 (직렬화)
        String broadcastJson =
                mapper.writeValueAsString(broadcastDto);

        // 6. 채팅방의 모든 사람에게 예쁘게 발송
        for (WebSocketSession s : sessionMap.keySet()) {
            s.sendMessage(new TextMessage(broadcastJson));
        }



    }

    @Override
    public void afterConnectionClosed(
            WebSocketSession session,
            CloseStatus status) throws Exception {

        log.info("[접속 종료] 세션 ID: {}",
                session.getId());

        // 나간 사람은 명부에서 지우기
        sessionMap.remove(session);

        // 전원에게 갱신된 접속자 명단 다시 쏘기
        broadcastUserList();
    }

    // 접속자 명단 문자열을 만들어 방송하는 전용 메서드
    private void broadcastUserList() throws Exception {

        // 접속자 이름들을 쉼표로 예쁘게 연결
        String users = String.join(", ",
                sessionMap.values());

        // 클라이언트가 명단임을 알 수 있게 [USERS] 꼬리표 부착
        TextMessage msg = new TextMessage(
                "[USERS]" + users);

        // 모든 세션에 전송
        for (WebSocketSession s : sessionMap.keySet()) {
            s.sendMessage(msg);
        }
    }
}