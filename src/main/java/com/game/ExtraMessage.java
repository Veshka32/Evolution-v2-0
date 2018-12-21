package com.game;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.game.constants.Property;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY) //skip empty collections
public class ExtraMessage {
    private String playerOnAttack;
    private int predator;
    private String playerUnderAttack;
    private Property type;
    private List<Integer> victims;

    public ExtraMessage(String name, int id, String name1, Property type) {
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

    public List<Integer> getVictims() {
        return victims;
    }
}
