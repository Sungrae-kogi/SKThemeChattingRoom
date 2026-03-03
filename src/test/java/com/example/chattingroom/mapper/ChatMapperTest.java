package com.example.chattingroom.mapper;

import com.example.chattingroom.dto.MessageDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/*
    SpringBootTest는 통합 테스트 환경 -> SpringBoot와 DB를 통째료 실행하므로 테스트에 시간이 소요.

    단위 테스트는 ChatService 라는 자바 파일 하나만 떼어내 빠른 시간에 검증.
    ChatService의 검증에는 @AutoWired로 주입받는 ChatMapper가 반드시 필요한데 이건 DB와 연관되어있다.
    이것을 Mockito 라이브러리가 가짜(Mock) 객체를 만들어서 연기를 시키게 함.

 */

@SpringBootTest
@Transactional
@Testcontainers         // 도커 컨테이너 준비 선언
public class ChatMapperTest {

    /*
        도커 세팅   - 도커에게 MariaDB 10.6 창고를 임시로 띄우라는 명령
     */
    @Container
    static MariaDBContainer<?> mariadb = new MariaDBContainer<>("mariadb:10.11").withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    /*
        도커 세팅   - 로컬 application.properties에 적힌 dB 주소를 무시하고, 방금 도커가 띄운 '가짜 DB 주소'로 연결을 바꿈
     */
    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url", mariadb::getJdbcUrl);
        registry.add("spring.datasource.username", mariadb::getUsername);
        registry.add("spring.datasource.password", mariadb::getPassword);

        // 테스트 할 때 무조건 schema.sql을 실행해서 테이블을 만들어
        registry.add("spring.sql.init.mode", () -> "always");
    }

    @Autowired
    private ChatMapper chatMapper;

    @Test
    @DisplayName("DB에 채팅을 넣고 조회하면 정상적으로 나와야 한다.")
    void insertAndSelectTest(){

        // Given
        String testSender = "테스트봇";
        String testContent = "도커에서 실행 중입니다.";

        // When
        chatMapper.insertMessage(testSender,testContent);

        List<MessageDTO> recentList = chatMapper.selectRecentMessages();

        // Then
        // 리스트가 비어있지 않아야 함을 보장해야한다.
        assertThat(recentList).isNotEmpty();

        // 작성한 쿼리는 시간순(ASC)이므로 방금 넣은 최신 채팅은 리스트의 맨 마지막에 존재해야만 합니다.
        MessageDTO lastMsg = recentList.get(recentList.size()-1);

        // lastMsg의 작성자와, 내용이 마지막으로 전송한 채팅과 일치해야만 합니다.
        assertThat(lastMsg.getSender()).isEqualTo(testSender);
        assertThat(lastMsg.getContent()).isEqualTo(testContent);

    }
}
