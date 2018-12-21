package com.game;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.game.constants.Property;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Card {
    private final Property property;
    private Property extraProperty;
    private int id;

    @JsonCreator
    public Card(@JsonProperty("id") int id, @JsonProperty("property") Property property, @JsonProperty("extraProperty") Property extraProperty) {
        this.id = id;
        this.property = property;
        this.extraProperty = extraProperty;
    }

    @JsonCreator
    public Card(@JsonProperty("id") int id, @JsonProperty("property") Property property) {
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

    public Property getProperty() {
        return property;
    }

    public Property getExtraProperty() {
        return extraProperty;
    }
}

