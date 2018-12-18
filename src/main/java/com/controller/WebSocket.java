package com.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class WebSocket {

    @MessageMapping("/chat/{id}")
    @SendTo("/topic/messages/{id}")
    public String greeting(@DestinationVariable("id") int id, String message){
        return message + "back"+id;
    }

}