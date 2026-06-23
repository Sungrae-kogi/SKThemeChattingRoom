package com.example.chattingroom.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.security.Principal;

@Controller
public class ChatController {

    @GetMapping("/room")
    public String chatRoom(Model model, Principal principal){
        if (principal != null) {
            model.addAttribute("username", principal.getName());
        } else {
            model.addAttribute("username", "익명");
        }
        return "room";  //  그냥 @Controller이므로 text 리턴시 .jsp 를 찾음
    }
}
