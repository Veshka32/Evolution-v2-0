package com.model.game.constants;

public enum Constants {
    START_NUMBER_OF_CARDS(6),
    START_CARD_INDEX(1),
    NUMBER_OF_EXTRA_CARD(1),
    FOOD(0),
    TOTAL_CARD_NUMBER(84),
    MIN_NUMBER_OF_PLAYERS(2),
    PARASITE_HUNGRITY(2),
    START_HUNGRITY(1),
    MAX_NUMBER_PF_PLAYERS(4);

    private final int id;

    Constants(int id) {
        this.id = id;
    }

    public int getValue() {
        return id;
    }

    public int maxFoodFor(int i) {
        switch (i) {
            case 2:
                return 8; //max points of one dice + 2
            case 3:
                return 12; //max points of two dice
            case 4:
                return 14; //max points of to dice + 2
        }
        throw new IllegalArgumentException("Wrong number of player");
    }

    public int minFoodFor(int i) {
        switch (i) {
            case 2:
                return 3; //min points of one dice +2
            case 3:
                return 2; //min points of two dice
            case 4:
                return 4; //min points of two dice + 2
        }
        throw new IllegalArgumentException("Wrong number of player");
    }
}
