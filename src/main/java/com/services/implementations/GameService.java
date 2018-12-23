package com.services.implementations;

import com.game.*;
import com.game.constants.Constants;
import com.game.constants.MoveType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class GameService {
    private static final String QUEUE = "/queue/messages";
    private static final String GAME_ID = "gameId";
    private final AtomicInteger nextId = new AtomicInteger(1);
    Map<Integer, Game> games = new ConcurrentHashMap<>();
    private Deck deck = new Deck();

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void handleSubscribeEvent(SessionSubscribeEvent event) {
        Optional<Game> game = games.values().stream()
                .filter(g -> g.hasPlayer(event.getUser().getName()))
                .findFirst();
        game.ifPresent(g -> joinGame(g.getId(), event.getUser().getName()));
    }

    public void joinGame(int gameId, String login) {
        if (!games.containsKey(gameId) || login == null) throw new IllegalArgumentException();

        Game game = games.get(gameId);
        if (game.hasPlayer(login)) game.playerBack(login);
        else {
            game.addPlayer(login);
            if (game.isFull()) {
                game.setCardList(deck.getCards());
                game.start();
            }
        }

        for (String player : game.getPlayers()) {
            GameDTO2 dto = game.getFullJson(player);
            messagingTemplate.convertAndSendToUser(player, QUEUE, dto); //send full json
        }
        game.refresh();
    }

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        Optional<Integer> id = getGame(event.getUser().getName());
        if (!id.isPresent()) return;
        Game g = games.get(id.get());
        leaveGame(event.getUser().getName(), id.get());
        for (String player : g.getPlayers()) {
            if (!player.equals(event.getUser().getName())) {
                GameDTO2 dto = g.getFullJson(player);
                messagingTemplate.convertAndSendToUser(player, QUEUE, dto); //send light json
            }
        }
        g.refresh();
    }

    @MessageMapping("/chat/{gameId}")
    public void getMessage(@DestinationVariable(GAME_ID) int id, Move message, Principal principal) {
        message.setPlayer(principal.getName());
        makeMove(message, id);
        Game g = games.get(id);
        for (String player : g.getPlayers()) {
            GameDTO2 dto = g.getFullJson(player);
            messagingTemplate.convertAndSendToUser(player, QUEUE, dto); //send light json
        }
        g.refresh();
    }

    public List<GameDTO> getGamesToJoin(String login) {
        //get all the games those are not full and doesn't contain this player
        return games.values().stream().filter(game -> !game.onProgress() && !game.hasPlayer(login)).map(GameDTO::new).collect(Collectors.toList());
    }

    public Optional<GameDTO> getCurrentGame(String login) {
        return games.values().stream().filter(g -> g.hasPlayer(login)).findFirst().map(GameDTO::new);
    }

    public void leaveGame(String login, int gameId) {
        Optional<Game> g = Optional.ofNullable(games.get(gameId));
        if (!g.isPresent()) return;
        Game game = g.get();
        Move message = new Move(0, 0, 0, MoveType.LEAVE_GAME.name(), null, " leave game");
        message.setPlayer(login);
        game.makeMove(message);
        for (String player : game.getPlayers()) {
            GameDTO2 dto = game.getFullJson(player);
            messagingTemplate.convertAndSendToUser(player, QUEUE, dto); //send light json
        }
        game.refresh();
    }

    public Optional<Integer> getGame(String login) {
        return games.values().stream().filter(g -> g.hasPlayer(login)).findFirst().map(Game::getId);
    }

    public Game createGame(int numberOfPlayers, String login) {
        if (login == null || numberOfPlayers < Constants.MIN_NUMBER_OF_PLAYERS.getValue() || numberOfPlayers > Constants.MAX_NUMBER_PF_PLAYERS.getValue())
            throw new IllegalArgumentException();
        Game g = new Game(nextId.getAndIncrement(), numberOfPlayers, login);
        games.putIfAbsent(g.getId(), g);
        return g;
    }

    public List<GameDTO> getAllGames() {
        return games.values().stream().map(GameDTO::new).collect(Collectors.toList());
    }

    @Scheduled(cron = "0 0 0/1 1/1 * ? *") //every hour
    public void remove() {
        games.values().removeIf(g -> g.isLeft() || g.isEnd()); //safety removing from map
    }

    public void makeMove(Move message, int id) {
        games.get(id).makeMove(message);
    }

}