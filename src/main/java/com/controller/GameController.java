package com.controller;

import com.services.implementations.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.Optional;

@Controller
public class GameController {
    private static final String GAME_ID = "gameId";

    @Autowired
    GameService gameService;


    @PostMapping("/game/create")
    public String createGame(@RequestParam("number") int numberOfPlayer, Principal principal) {
        gameService.createGame(numberOfPlayer, principal.getName());
        return "redirect:/game";
    }

    @GetMapping("/game/join")
    public String joinGame(@RequestParam(GAME_ID) int id, Principal principal) {
        gameService.joinGame(id, principal.getName());
        return "redirect:/game";
    }

    @GetMapping("/game/leave")
    public String leaveGame() {
        return "redirect:/index";
    }

    @GetMapping("/game")
    public String gamePage(Principal principal, HttpServletResponse response) {
        Optional<Integer> id = gameService.getGame(principal.getName());
        if (!id.isPresent()) return "redirect:/index";
        response.addCookie(new Cookie(GAME_ID, id.get().toString()));
        return "game";
    }
}
