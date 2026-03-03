# SK Theme Chatting Room 💬
Spring Boot와 WebSocket을 활용한
실시간 익명 웹 채팅 서비스 (저작권 이슈 없음)

## 📌 프로젝트 소개
단일 채팅방에 접속한 여러 사용자가
실시간으로 대화를 나눌 수 있는 웹 메신저입니다.
메모리 기반의 세션 관리와 비동기 DB 저장을 통해
빠른 채팅 전송 속도를 보장합니다.

## 🛠 기술 스택
- **Backend:** Java, Spring Boot, WebSocket, MyBatis
- **Database:** MariaDB
- **Frontend:** JSP, HTML/CSS, Vanilla JS
- **Test & CI:** JUnit5, Mockito, AssertJ,
  Testcontainers, GitHub Actions, Docker

## ✨ 주요 기능 및 아키텍처
- **실시간 다중 통신:** WebSocket 기반 지연 없는 1:N 채팅
- **비동기 DB 저장:** `@Async`를 활용해
  메인 스레드 병목 없이 백그라운드에서 채팅 내역 영구 저장
- **CI 자동화 파이프라인:** GitHub Actions를 활용하여
  메인 브랜치 Push 시 자동 빌드 및 통합 테스트 수행
- **멱등성 있는 통합 테스트:** Testcontainers를 도입하여
  로컬 DB 환경에 의존하지 않는 독립적인 테스트 환경(Docker) 구축

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