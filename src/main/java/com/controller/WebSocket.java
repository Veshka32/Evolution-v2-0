package com.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class WebSocket {

    @MessageMapping("/chat/{id}")
    @SendTo("/topic/messages/{id}")
    public String greeting(@DestinationVariable("id") int id, String message, Principal principal) {
        String s = principal.getName();
        return message + "back" + id + "principal name=" + s;
    }

    
}