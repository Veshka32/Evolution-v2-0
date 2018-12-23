package com.controller;

import com.services.implementations.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class MainPage {

    @Autowired
    GameService gameService;

    @GetMapping({"/", "index"})
    public String hello(Model model, Principal principal) {
        if (principal != null) {
            model.addAttribute("games", gameService.getGamesToJoin(principal.getName()));
            gameService.getCurrentGame(principal.getName())
                    .ifPresent(g -> model.addAttribute("current", g));
        }
        return "index";
    }
}
