package com.services.implementations;

import com.model.GameDTO;
import com.model.game.Deck;
import com.model.game.Game;
import com.model.game.Move;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class GameService {
    private final AtomicInteger nextId = new AtomicInteger(1);
    Map<Integer, Game> games = new ConcurrentHashMap<>();

    private Deck deck = new Deck();

    public List<GameDTO> getGamesToJoin(String login) {
        //get all the games those are not full and doesn't contain this player
        return games.values().stream().filter(game -> !game.onProgress() && !game.hasPlayer(login)).map(GameDTO::new).collect(Collectors.toList());
    }

    public GameDTO getCurrentGame(String login) {
        return games.values().stream().filter(g -> g.hasPlayer(login)).findFirst().map(GameDTO::new).orElse(null);
    }


    public Game getGame(int id) {
        return games.get(id);
    }

    public Game createGame(int numberOfPlayers, String login) {
        // assert login!=null && numberOfPlayers>1 && numberOfPlayers<=4;
        Game g = new Game(nextId.getAndIncrement(), numberOfPlayers, login);
        games.putIfAbsent(g.getId(), g);
        return g;
    }

    public List<GameDTO> getAllGames() {
        return games.values().stream().map(GameDTO::new).collect(Collectors.toList());
    }

    public void joinPlayer(int gameId, String login) {
        //if (!games.containsKey(gameId) || login==null) throw new IllegalArgumentException();

        Game game = games.get(gameId);
        if (game.hasPlayer(login)) game.playerBack(login);
        else {
            game.addPlayer(login);
            if (game.isFull()) {
                game.setCardList(deck.getCards());
                game.start();
            }
        }
    }

    @Scheduled
    public void remove() {
        /**
         * TODO
         */
        games.values().removeIf(Game::isLeft); //safety removing from map
    }

    public void makeMove(Move message, int id) {
        Game game = getGame(id);
        game.makeMove(message);
    }

}