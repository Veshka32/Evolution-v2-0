package com.controller;

import com.services.implementations.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainPage {

    @Autowired
    GameService gameService;

    @GetMapping({"/", "index"})
    public String hello(Model model) {
        model.addAttribute("games", gameService.getAllGames());
        return "index";
    }

    @GetMapping("/test")
    public String test() {
        return "test";
    }
}
