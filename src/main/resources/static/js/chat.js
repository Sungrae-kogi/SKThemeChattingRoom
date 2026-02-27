// 1. 입장 시 익명 이름 설정창
let username = "익명";
let ws;
let lastSendTime = 0;

// 입장 로직
document.getElementById("btnJoin").addEventListener("click", startChat);

document.getElementById("nameInput").addEventListener("keyup", function (e) {
    if (e.key === 'Enter')
        startChat();
});

function startChat() {
    let inputName = document.getElementById("nameInput").value.trim();

    if (inputName !== "") {
        username = inputName;
    }

    // 로그인 창 숨김, 채팅창 띄우기
    document.getElementById("loginScreen").style.display = "none";
    document.getElementById("chatScreen").style.display = "block";

    connectWebSocket();
}


// 2. 서버의 "/chat" 주소로 웹소켓 연결
function connectWebSocket() {
    ws = new WebSocket("ws://" + location.host + "/chat?name=" + encodeURIComponent(username));
    ws.onmessage = function (event) {
        let data = event.data;
        let box = document.getElementById("chatBox");

        // 1. 접속자 명단 업데이트인지 확인
        if (data.startsWith("[USERS]")) {
            let userStr = data.substring(7);
            document.getElementById("userList")
                .innerText = "접속자: " + userStr;

        } else {
            // 2. 일반 채팅일 경우 콜론(:) 위치 찾기
            let idx = data.indexOf(":");

            if (idx > -1) {
                // 이름과 내용 분리
                let sender = data.substring(0, idx).trim();
                let content = data.substring(idx + 1).trim();

                let timeStr = getFormatTime();

                // 내가 보낸 메시지라면? (우측 정렬)
                if (sender === username) {
                    box.innerHTML +=
                        "<div class='msg-row msg-me'>" +
                        "<span class='msg-time'>"+
                        timeStr + "</span>" +
                        "<div class='msg-bubble'>" +
                        content +
                        "</div></div>";

                    // 남이 보낸 메시지라면? (좌측 정렬 + 이름 표시)
                } else {
                    box.innerHTML +=
                        "<div class='msg-sender'>" +
                        sender +
                        "</div>" +
                        "<div class='msg-row msg-other'>" +
                        "<div class='msg-bubble'>" + content + "</div>" +
                        "<span class='msg-time'>" +
                        timeStr + "</span>" +
                        "</div>";
                }

            } else {
                // 콜론이 없는 시스템 메시지 처리
                box.innerHTML +=
                    "<p style='text-align:center; color:#999;'>" +
                    data + "</p>";
            }

            // 3. 메시지 추가 후 스크롤을 맨 아래로 내리기
            box.scrollTop = box.scrollHeight;
        }
    };
}

// 3. 채팅 전송 로직
document.getElementById("btnSend").addEventListener("click", sendMsg);

// 엔터를 쳐도 전송.
document.getElementById("msgInput").addEventListener("keyup", function (e) {
    if (e.key === 'Enter') sendMsg();
});


function sendMsg() {
    let input = document.getElementById("msgInput");
    let text = input.value.trim();

    if (text === "") {
        input.value = "";
        return;
    }

    let now = Date.now();
    if (now - lastSendTime < 1000) {
        alert("도배 방지: 1초 후에 다시 보내주세요!");
        return;
    }

    ws.send(username + ": " + text);
    lastSendTime = now;
    input.value = "";
}

// 메시지 전송시 표기할 시간 추출
function getFormatTime(){
    let now = new Date();
    let ampm = now.getHours() < 12? "오전" : "오후";
    let h = now.getHours() % 12 || 12;
    let m = now.getMinutes();

    // 분이 한 자리수면 앞에 0 붙이기
    if (m < 10) m = "0" + m;
    return ampm + " " + h + ":" + m;
}