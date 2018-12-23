package com.game;

import com.game.constants.MoveType;
import com.game.constants.Property;

public class Move {
    private String player;

    public Move(int cardId, int animalId, int secondAnimalId, String move, String property, String log) {
        this.cardId = cardId;
        this.animalId = animalId;
        this.secondAnimalId = secondAnimalId;
        if (move != null) this.move = MoveType.valueOf(move);
        if (property != null) this.property = Property.valueOf(property);
        this.log = log;
    }

    public void setMove(MoveType move) {
        this.move = move;
    }
    private final int cardId;
    private final int animalId;
    private final int secondAnimalId;
    private final String log;
    private MoveType move;
    private Property property;

    public void setProperty(Property property) {
        this.property = property;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public MoveType getMove() {
        return move;
    }

    public String getPlayer() {
        return player;
    }

    public Property getProperty() {
        return property;
    }

    String getLog() {
        return log;
    }

    int getCardId() {
        return cardId;
    }

    int getAnimalId() {
        return animalId;
    }

    int getSecondAnimalId() {
        return secondAnimalId;
    }
}
