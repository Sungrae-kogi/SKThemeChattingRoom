# SK Theme Chatting Room 💬
Spring Boot와 WebSocket을 활용한 실시간 익명 웹 채팅 서비스
저작권이슈 없음.

## 📌 프로젝트 소개
단일 채팅방에 접속한 여러 사용자가
실시간으로 대화를 나눌 수 있는 웹 메신저입니다.
메모리 기반의 세션 관리와 비동기 DB 저장을 통해
빠른 채팅 전송 속도를 보장합니다.

## 🛠 기술 스택
- **Backend:** Java, Spring Boot, WebSocket, MyBatis
- **Database:** MariaDB
- **Frontend:** JSP, HTML/CSS, Vanilla JS

## ✨ 주요 기능
- **실시간 다중 통신:** WebSocket 기반 지연 없는 1:N 채팅
- **접속자 동기화:** 입장/퇴장 시 상단 접속자 명단 실시간 갱신
- **직관적인 UI:** 내 메시지(우측)와 타인 메시지(좌측) 분리 처리
- **비동기 DB 저장:** `@Async`를 활용해
  메인 스레드 병목 없이 백그라운드에서 채팅 내역 영구 저장
- **과거 내역 로드:** 방 입장 시 최근 대화 50건을
  DB에서 조회하여 화면에 자동 출력

## 🚀 로컬 실행 방법

1. **설정 파일 복사 및 세팅**
   `src/main/resources` 경로의 `application.properties.template`
   파일을 복사하여 `application.properties`로 이름을 변경합니다.
   본인의 로컬 MariaDB 계정 정보로 내부 빈칸을 채워주세요.

2. **DB 테이블 생성**
   MariaDB에 접속하여 아래 쿼리로 테이블을 생성합니다.
   ```sql
   CREATE TABLE CHAT_MESSAGE (
       id BIGINT AUTO_INCREMENT PRIMARY KEY,
       sender VARCHAR(50) NOT NULL,
       content TEXT NOT NULL,
       send_time DATETIME DEFAULT CURRENT_TIMESTAMP
   );