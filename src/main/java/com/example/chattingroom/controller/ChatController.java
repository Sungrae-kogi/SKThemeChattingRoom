package com.example.chattingroom.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ChatController {

    @GetMapping("/room")
    public String chatRoom(){
        return "room";  //  그냥 @Controller이므로 text 리턴시 .jsp 를 찾음
    }
}
