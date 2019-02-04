package com.controller;

import com.services.implementations.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.Map;

@Controller
public class MainPage {

    @Autowired
    GameService gameService;

    @GetMapping({"/", "index"})
    public String hello(Model model, Principal principal) {
        if (principal != null) {
            Map<String, Object> details = (Map<String, Object>) ((OAuth2Authentication) principal).getUserAuthentication().getDetails();

            //model.addAttribute("games", gameService.getGamesToJoin(principal.getName()));
            model.addAttribute("name",details.get("name"));
            gameService.getCurrentGame((String)details.get("name"))
                    .ifPresent(g -> model.addAttribute("current", g));
        }
        return "index";
    }
}
