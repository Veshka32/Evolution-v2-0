package com.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller("/game")
public class GameController {


    @GetMapping("/create")
    public String createGame() {
        return null;
    }

}
