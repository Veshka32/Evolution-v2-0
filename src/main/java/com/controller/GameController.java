package com.controller;

import com.model.game.Game;
import com.model.game.Move;
import com.model.game.constants.MoveType;
import com.services.implementations.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;

@Controller
public class GameController {

    @Autowired
    GameService gameService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat/{id}")
    public void getMessage(@DestinationVariable("id") int id, Move message, Principal principal) {
        gameService.makeMove(message, id);
        for (String player : gameService.getGame(id).getPlayers()) {
            messagingTemplate.convertAndSendToUser(player, "/queue/messages", gameService.getGame(id).getLightWeightJson(player)); //send light json
        }
    }

    @PostMapping("/game/create")
    public String createGame(@RequestParam("number") int numberOfPlayer, Principal principal, Model model, HttpServletResponse response) {
        Game g = gameService.createGame(numberOfPlayer, principal.getName());
        model.addAttribute("game", g);
        response.addCookie(new Cookie("gameId", g.getId() + ""));
        return "test";
    }

    @GetMapping("/game/join")
    public String joinGame(@RequestParam("gameId") int id, Principal principal, Model model, HttpServletResponse response) {
        gameService.joinPlayer(id, principal.getName());
        for (String player : gameService.getGame(id).getPlayers()) {
            messagingTemplate.convertAndSendToUser(player, "/queue/messages", gameService.getGame(id).getFullJson(player)); //send full
        }
        model.addAttribute("game", gameService.getGame(id));
        response.addCookie(new Cookie("gameId", id + ""));
        return "test";
    }

    @PostMapping("/game/leave")
    public String leaveGame(@RequestParam("gameId") int id, Principal principal, Model model, HttpServletRequest request) {
        Move message = new Move(principal.getName(), 0, 0, 0, MoveType.LEAVE_GAME.name(), null, " leave game");
        gameService.makeMove(message, id);
        for (String player : gameService.getGame(id).getPlayers()) {
            messagingTemplate.convertAndSendToUser(player, "/queue/messages", gameService.getGame(id).getLightWeightJson(player)); //send light json
        }
        return "redirect:/index";
    }

}
