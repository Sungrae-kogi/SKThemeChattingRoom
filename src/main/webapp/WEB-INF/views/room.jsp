<%@ page language="java" contentType="text/html; charset=UTF-8" %>
    <%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
        <!doctype html>
        <html lang="en">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1">
            <title>SK 메신저</title>
            <%-- <link rel="stylesheet" href="/css/chat.css">--%>
                <spring:url value="/css/chat.css" var="cssUrl" />
                <link rel="stylesheet" href="${cssUrl}" />
        </head>

        <body>

            <div class="chat-wrap" id="chatScreen">
                <div class="header">SK 사내 익명 톡</div>
                <div id="userList" class="user-list">
                    접속자:
                </div>
                <div id="chatBox">
                    <div id="observer-target" style="height: 1px;"></div>
                </div>
                <div class="input-wrap">
                    <!-- accept="image/*" 는 파일 선택 창에서 오직 이미지 파일만 보이도록 필터링 해주는 옵션-->
                    <input type="file" id="imageInput" accept="image/*" style="display: none;">
                    <button onclick="document.getElementById('imageInput').click()">사진</button>
                    <input type="text" id="msgInput" placeholder="메시지 입력..." />
                    <button class="btn-send" id="btnSend">
                        전송
                    </button>
                </div>
            <script>
                // 서버(ChatController)에서 넘겨준 로그인 아이디를 전역 변수로 저장
                const myUsername = "${username}";
            </script>
            <spring:url value="/js/chat.js" var="jsUrl" />
            <script src="${jsUrl}"></script>

                <spring:url value="/js/chat-history.js" var="historyJsUrl" />
                <script src="${historyJsUrl}"></script>
        </body>

        </html>