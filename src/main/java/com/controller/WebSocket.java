package com.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class WebSocket {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat/{id}")
    public void greeting(@DestinationVariable("id") int id, String message, Principal principal) {
        String user = principal.getName();
        messagingTemplate.convertAndSendToUser(user, "/queue/messages", message + "back" + id + "principal name=" + user);
    }


}