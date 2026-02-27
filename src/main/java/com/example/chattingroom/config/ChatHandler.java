package com.example.chattingroom.config;

import com.example.chattingroom.dto.MessageDTO;
import com.example.chattingroom.service.ChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URLDecoder;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatHandler
        extends TextWebSocketHandler {
    @Autowired
    private ChatService chatService;

    // 1. 로그 기록용 로거
    private static final Logger log =
            LoggerFactory.getLogger(ChatHandler.class);

    // 2. 세션과 닉네임을 짝지어 기억하는 명부 (동시성 안전)
    private static final Map<WebSocketSession, String>
            sessionMap = new ConcurrentHashMap<>();

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
            String oldSender = dto.getSender();
            String oldContent = dto.getContent();

            TextMessage oldMsg = new TextMessage(
              oldSender + ": " + oldContent
            );

            // 본인에게만 전송.
            session.sendMessage(oldMsg);
        }
    }

    @Override
    protected void handleTextMessage(
            WebSocketSession session,
            TextMessage message) throws Exception {

        String payload = message.getPayload();

        // 남들에게 먼저 발송
        for (WebSocketSession s : sessionMap.keySet()){
            s.sendMessage(new TextMessage(payload));
        }

        // 콜론(:)을 기준으로 닉네임과 내용 분리
        int idx = payload.indexOf(":");
        if (idx > -1){
            String sender = payload.substring(0, idx).trim();
            String content = payload.substring(idx+1).trim();

            // 비동기 스레드에 DB 저장 지시    -> 트래픽이 있더라도, 채팅 서버에는 부하x
            chatService.saveMessageAsync(sender, content);
        }

        // 로그 출력 (내용은 DEBUG, 행위는 INFO)
        log.debug("[메시지 수신] 내용: {}", payload);
        log.info("[전송 처리] 세션 ID: {}, 길이: {} Byte",
                session.getId(), payload.length());

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