package com.example.chattingroom.service;

import com.example.chattingroom.dto.MessageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatBufferService {
    private final RedisTemplate<String, Object> redisTemplate;

    // 메인 대기열 바구니
    private static final String BUFFER_KEY = "chat:buffer";

    // 채팅을 치면 해당 함수가 호출
    public void saveToBuffer(MessageDTO message) {
        // Redis의 List 구조 -> redisTemplate의 opsForList() 순서가 있는 연결리스트 구조
        redisTemplate.opsForList().rightPush(BUFFER_KEY, message);

        log.info("Redis 임시 보관 완료 : " + message.getContent());
    }

    // 현재 버퍼에 저장되어 있는 전체 메세지를 반환.
    public List<MessageDTO> getBufferedMessages() {
        // Redis 에 저장된 모든 요소를 List로 변환해서 반환.

        List<Object> resultList = redisTemplate.opsForList().range(BUFFER_KEY, 0, -1);
        List<MessageDTO> messages = new ArrayList<>();

        // 현재 버퍼 리스트를 가져오는데, 그게 null일 가능성도 존재함.
        if (resultList == null) {
            return messages; // null 일 경우 안전하게 빈 리스트 반환하여 NullPointerException 방지
        }

        for (Object obj : resultList) {
            if (obj instanceof MessageDTO) {
                messages.add((MessageDTO) obj);
            }
        }
        return messages;
    }
}
