package com.game;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.game.constants.Phase;

import java.util.Collection;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_DEFAULT) //skip empty collections
public class GameDTO2 {

    private String winners;

    private Boolean lastRound;
    private ExtraMessage extraMessage;
    private Integer id;
    private Phase phase;
    private Collection<Player> players;
    private List<String> playersNames;
    private Boolean status;

    public List<String> getPlayersNames() {
        return playersNames;
    }

    private String playerOnMove;
    private Integer food;
    private String log;
    private String error;
    private Integer[] deleteAnimal;
    private Animal[] changedAnimal;
    private Card[] newCards;
    private Integer deletedCard;
    //player specific properties
    private String player;
    private List<Card> cards;

    public void setPlayersNames(List<String> playersNames) {
        this.playersNames = playersNames;
    }

    public Card[] getNewCards() {
        return newCards;
    }

    public void setNewCards(Card[] newCards) {
        this.newCards = newCards;
    }

    public Integer getDeletedCard() {
        return deletedCard;
    }

    public void setDeletedCard(Integer deletedCard) {
        this.deletedCard = deletedCard;
    }

    public Integer[] getDeleteAnimal() {
        return deleteAnimal;
    }

    public void setDeleteAnimal(Integer[] deleteAnimal) {
        this.deleteAnimal = deleteAnimal;
    }

    public Animal[] getChangedAnimal() {
        return changedAnimal;
    }

    public void setChangedAnimal(Animal[] changedAnimal) {
        this.changedAnimal = changedAnimal;
    }

    public String getError() {

        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getWinners() {
        return winners;
    }

    public void setWinners(String winners) {
        this.winners = winners;
    }

    public Boolean isLastRound() {
        return lastRound;
    }

    public void setLastRound(boolean lastRound) {
        this.lastRound = lastRound;
    }

    public ExtraMessage getExtraMessage() {
        return extraMessage;
    }

    public void setExtraMessage(ExtraMessage extraMessage) {
        this.extraMessage = extraMessage;
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Phase getPhase() {
        return phase;
    }

    public void setPhase(Phase phase) {
        this.phase = phase;
    }

    public Collection<Player> getPlayers() {
        return players;
    }

    public void setPlayers(Collection<Player> players) {
        this.players = players;
    }

    public String getPlayerOnMove() {
        return playerOnMove;
    }

    public void setPlayerOnMove(String playerOnMove) {
        this.playerOnMove = playerOnMove;
    }

    public Integer getFood() {
        return food;
    }

    public void setFood(int food) {
        this.food = food;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public Boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
