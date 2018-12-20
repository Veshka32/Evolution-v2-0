package com.model;


import com.model.game.Game;

import java.util.ArrayList;
import java.util.List;

public class GameDTO {
    private int id;
    private int numberOfPlayers;
    private String message;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void addPlayer(String pl) {
        players.add(pl);
    }

    public List<String> getPlayers() {
        return players;
    }
}
