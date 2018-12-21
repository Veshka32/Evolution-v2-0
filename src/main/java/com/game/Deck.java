package com.game;

import com.game.constants.Constants;
import com.game.constants.Property;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    private final List<Card> cards = new ArrayList<>(Constants.TOTAL_CARD_NUMBER.getValue());

    public Deck() {
        int startId = Constants.START_CARD_INDEX.getValue();
        for (int i = 0; i < 4; i++) {
            cards.add(new Card(startId++, Property.SYMBIOSIS));
            cards.add(new Card(startId++, Property.PIRACY));
            cards.add(new Card(startId++, Property.GRAZING));
            cards.add(new Card(startId++, Property.TAIL_LOSS, Property.DELETE_PROPERTY));
            cards.add(new Card(startId++, Property.HIBERNATION));
            cards.add(new Card(startId++, Property.POISONOUS));
            cards.add(new Card(startId++, Property.COMMUNICATION, Property.PREDATOR));
            cards.add(new Card(startId++, Property.SCAVENGER));
            cards.add(new Card(startId++, Property.RUNNING));
            cards.add(new Card(startId++, Property.MIMICRY));
            cards.add(new Card(startId++, Property.CAMOUFLAGE));
            cards.add(new Card(startId++, Property.BURROWING));
            cards.add(new Card(startId++, Property.SHARP_VISION));
            cards.add(new Card(startId++, Property.PARASITE, Property.PREDATOR));
            cards.add(new Card(startId++, Property.PARASITE, Property.FAT));
            cards.add(new Card(startId++, Property.COOPERATION, Property.PREDATOR));
            cards.add(new Card(startId++, Property.COOPERATION, Property.FAT));
            cards.add(new Card(startId++, Property.BIG, Property.PREDATOR));
            cards.add(new Card(startId++, Property.BIG, Property.FAT));
            cards.add(new Card(startId++, Property.SWIMMING));
            cards.add(new Card(startId++, Property.SWIMMING));
        }
    }

    public List<Card> getCards() {
        Collections.shuffle(cards);
        return new ArrayList<>(cards);
    }
}