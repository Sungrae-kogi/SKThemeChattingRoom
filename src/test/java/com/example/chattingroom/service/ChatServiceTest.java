package com.example.chattingroom.service;

import com.example.chattingroom.dto.MessageDTO;
import com.example.chattingroom.mapper.ChatMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

// 스프링을 켜지 않고, Mockito 로봇들만 깨움.
@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    // 가짜 객체로 DB연결을 하지않음.
    @Mock
    private ChatMapper  chatMapper;

    // 가짜 매퍼를 서비스 객체 안에 주입.     ChatService 에 ChatMapper가 필요해서
    // InjectMocks가 진짜로 테스트할 타겟 본체 -> 이 어노테이션이 붙은 객체를 생성할 때, 위에서 만든 @Mock 가짜 부품들을 본체 안에 조립.
    @InjectMocks
    private ChatService chatService;

    @Test
    @DisplayName("DB 없이 서비스의 조회 로직만 검증")
    void getRecentMessages() {

        // Given -> 상황 준비
        List<MessageDTO> fakeList = new ArrayList<>();
        MessageDTO fakeMsg = new MessageDTO();
        fakeMsg.setSender("가짜유저");
        fakeMsg.setContent("가짜 메시지");
        fakeMsg.setSendTime(LocalDateTime.now());
        fakeList.add(fakeMsg);

        // 가짜 객체에게 연기를 지시 : 누군가 selectRecentMessages()를 시키면, DB를 조회하지말고 이 Data를 반환하라.
        given(chatMapper.selectRecentMessages())
                .willReturn(fakeList);

        // When -> 실제 로직
        List<MessageDTO> result = chatService.getRecentMessages();

        // Then -> 검증
        verify(chatMapper, times(1)).selectRecentMessages();
    }
}