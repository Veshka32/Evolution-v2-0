package com.model.game;

import com.model.game.constants.Property;

import java.util.List;

class ExtraMessage {
    private String playerOnAttack;
    private int predator;
    private String playerUnderAttack;
    private Property type;
    private List<Integer> victims;

    ExtraMessage(String name, int id, String name1, Property type) {
        playerOnAttack = name;
        predator = id;
        playerUnderAttack = name1;
        this.type = type;
    }

    void setVictims(List<Integer> victims) {
        this.victims = victims;
    }

    public String getPlayerUnderAttack() {
        return playerUnderAttack;
    }

    public void setPlayerUnderAttack(String playerUnderAttack) {
        this.playerUnderAttack = playerUnderAttack;
    }

    public int getPredator() {
        return predator;
    }

    public void setPredator(int predator) {
        this.predator = predator;
    }

    public String getPlayerOnAttack() {
        return playerOnAttack;
    }

    public void setPlayerOnAttack(String playerOnAttack) {
        this.playerOnAttack = playerOnAttack;
    }

    public Property getType() {
        return type;
    }

    public void setType(Property type) {
        this.type = type;
    }

}
