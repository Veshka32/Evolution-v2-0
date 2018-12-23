package com.game;

import java.util.Collection;

public class GameDTO {
    private int id;
    private int numberOfPlayers;
    private Collection<String> players;

    public GameDTO(Game game) {
        this.id = game.getId();
        this.numberOfPlayers = game.getNumberOfPlayers();
        this.players = game.getPlayers();
    }

    public int getId() {
        return id;
    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public Collection<String> getPlayers() {
        return players;
    }
}
