package com.model.game;

import com.google.gson.annotations.Expose;
import com.model.game.constants.Property;

public class Card {
    @Expose
    private final Property property;

    @Expose
    private Property extraProperty; //use in Game.json

    @Expose
    private int id;

    public Card(int id, Property property, Property extraProperty) {
        this.id = id;
        this.property = property;
        this.extraProperty = extraProperty;
    }

    public Card(int id, Property property) {
        this.id = id;
        this.property = property;
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Card)) return false;
        Card card = (Card) o;
        return card.id == this.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}

