package com.game;

import java.util.ArrayList;
import java.util.List;

public class GameDTO {
    private int id;
    private int numberOfPlayers;
    private List<String> players = new ArrayList<>();

    public GameDTO(Game game) {
        this.id = game.getId();
        this.numberOfPlayers = game.getNumberOfPlayers();
    }

    public int getId() {
        return id;
    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public List<String> getPlayers() {
        return players;
    }
}
