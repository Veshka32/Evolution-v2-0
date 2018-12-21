package com.controller;

import com.game.Game;
import com.game.Move;
import com.services.implementations.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;

@Controller
public class GameController {
    private static final String QUEUE = "/queue/messages";
    private static final String GAME_ID = "gameId";

    @Autowired
    GameService gameService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void handleSessionConnect(SessionConnectedEvent event) {

        Game g = gameService.joinGame(event.getUser().getName());
        if (g == null) return;
        for (String player : g.getPlayers()) {
            if (player.equals(event.getUser().getName())) {
                messagingTemplate.convertAndSendToUser(player, QUEUE, g.getFullJson(player)); //send full json
            } else messagingTemplate.convertAndSendToUser(player, QUEUE, g.getLightWeightJson(player)); //sen light json
        }
    }

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        Game g = gameService.leaveGame(event.getUser().getName());
        for (String player : g.getPlayers()) {
            messagingTemplate.convertAndSendToUser(player, QUEUE, g.getLightWeightJson(player)); //send light json
        }
    }

    @MessageMapping("/chat/{gameId}")
    public void getMessage(@DestinationVariable(GAME_ID) int id, Move message, Principal principal) {
        message.setPlayer(principal.getName());
        gameService.makeMove(message, id);
        for (String player : gameService.getGame(id).getPlayers()) {
            messagingTemplate.convertAndSendToUser(player, QUEUE, gameService.getGame(id).getLightWeightJson(player)); //send light json
        }
    }

    @PostMapping("/game/create")
    public String createGame(@RequestParam("number") int numberOfPlayer, Principal principal) {
        gameService.createGame(numberOfPlayer, principal.getName());
        return "redirect:/game";
    }

    @GetMapping("/game/join")
    public String joinGame(@RequestParam(GAME_ID) int id, Principal principal) {
        gameService.joinGame(id, principal.getName());
        for (String player : gameService.getGame(id).getPlayers()) {
            messagingTemplate.convertAndSendToUser(player, QUEUE, gameService.getGame(id).getFullJson(player)); //send full
        }
        return "redirect:/game";
    }

    @PostMapping("/game/leave")
    public String leaveGame(@RequestParam(GAME_ID) int id, Principal principal) {
        Game g = gameService.leaveGame(principal.getName());
        for (String player : g.getPlayers()) {
            messagingTemplate.convertAndSendToUser(player, QUEUE, g.getLightWeightJson(player)); //send light json
        }
        return "redirect:/index";
    }

    @GetMapping("/game")
    public String gamePage(Principal principal, HttpServletResponse response) {
        Game g = gameService.getGame(principal.getName());
        if (g == null) return "redirect:/index";
        response.addCookie(new Cookie(GAME_ID, g.getId() + ""));
        return "game";
    }
}
