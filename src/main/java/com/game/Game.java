package com.game;


import com.game.constants.Constants;
import com.game.constants.Phase;
import com.game.constants.Property;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class Game {
    private int id;
    private int numberOfPlayers;
    private String winners; //game has winners=>game end=>never saved
    private String lastLogMessage;
    private Set<Animal> changedAnimals = new HashSet<>();
    private Set<Integer> deletedAnimalsId = new HashSet<>();
    private int animalID;
    private int round;
    private int playerOnMove;
    private String error;
    private StringBuilder log = new StringBuilder();
    private List<Card> cardList;
    private Map<String, Player> players = new HashMap<>();
    private List<String> playersOrder = new ArrayList<>();
    private ExtraMessage extraMessage;
    private Phase phase = Phase.START;
    private int food;
    private GameDTO2 dto = new GameDTO2();

    public Game(int id, int numberOfPlayers, String login) {
        this.id = id;
        this.numberOfPlayers = numberOfPlayers;
        dto.setId(id);
        addPlayer(login);
    }

    public int getId() {
        return id;
    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Game)) return false;
        return ((Game) o).id == this.id;
    }

    @Override
    public String toString() {
        return "game #" + id;
    }

    public Set<String> getPlayers() {
        return players.keySet();
    }

    public void addPlayer(String login) {
        Player player = new Player(login, players.size());
        players.put(login, player);
        lastLogMessage = login + " joined game at " + new Date() + "\n";
        log.append(lastLogMessage);
    }

    public boolean isFull() {
        return players.size() == numberOfPlayers;
    }

    public void start() {
        if (!phase.equals(Phase.START)) return;
        animalID = Constants.START_CARD_INDEX.getValue();
        resetPlayersOrder();
        players.forEach((k, v) -> addCardsOnStart(v));
        playerOnMove = 0;
        phase = Phase.EVOLUTION;
    }

    public void refresh() {
        error = null;
        changedAnimals.clear();
        deletedAnimalsId.clear();
        //dto.clear();
    }

    public GameDTO2 getFullJson(String name) {
        if (error != null) {
            if (playersOrder.get(playerOnMove).equals(name)) {
                dto.setError(error);
                return dto;
            } else return null;
        }
        dto.setPhase(phase);
        dto.setPlayers(players.values());
        dto.setFood(food); //in phase?
        dto.setId(id);
        dto.setLog(log.toString());//???

        if (extraMessage != null)
            dto.setExtraMessage(extraMessage);

        if (phase.equals(Phase.END)) dto.setWinners(winners);
        if (round == -1) dto.setLastRound(true); //else null

        //these json fields are player name sensitive
        dto.setPlayer(name);
        dto.setCards(players.get(name).getCards());
        if (!playersOrder.isEmpty() && playersOrder.get(playerOnMove).equals(name))
            dto.setStatus(true); //else default false
        return dto;
    }

    public GameDTO2 getLightWeightJson(String name) {

        if (error != null) {
            if (playersOrder.get(playerOnMove).equals(name)) {
                dto.setError(error);
                return dto;
            } else return null;
        }

        dto.setPhase(phase);
        if (!deletedAnimalsId.isEmpty())
            dto.setDeleteAnimal(deletedAnimalsId.toArray(new Integer[]{}));
        if (!changedAnimals.isEmpty())
            dto.setChangedAnimal(changedAnimals.toArray(new Animal[]{}));
        dto.setLog(lastLogMessage);

        if (phase.equals(Phase.EVOLUTION)) {
            if (players.get(name).hasNewCards())
                dto.setNewCards(players.get(name).getNewCards().toArray(new Card[]{}));
            else if (players.get(name).hasDeletedCard())
                dto.setDeletedCard(players.get(name).getDeletedCard());
        }

        if (phase.equals(Phase.FEED)) dto.setFood(food);

        if (!playersOrder.isEmpty() && playersOrder.get(playerOnMove).equals(name))
            dto.setStatus(true); //else default false

        if (extraMessage != null)
            dto.setExtraMessage(extraMessage);

        if (phase.equals(Phase.END)) dto.setWinners(winners);
        if (round == -1) dto.setLastRound(true); //else null
        return dto;
    }

    public void playerBack(String name) {
        players.get(name).backToGame();
    }

    public boolean isEnd() {
        return phase.equals(Phase.END);
    }

    public boolean onProgress() {
        return !phase.equals(Phase.START);
    }

    public boolean hasPlayer(String name) {
        return players.containsKey(name);
    }

    public void setCardList(List<Card> cardList) {
        if (this.cardList == null) this.cardList = cardList;
    }

    public boolean isLeft() {
        return players.values().stream().allMatch(Player::doLeaveGame);
    }

    public void makeMove(Move move) {

        lastLogMessage = "\n" + move.getPlayer() + " " + move.getLog() + " at " + new Date();
        log.append(lastLogMessage);
        switch (move.getMove()) {
            case SAVE_GAME:
                return;
            case END_PHASE:
                playerEndsPhase(move.getPlayer());
                return;
            case LEAVE_GAME: //only log updates
                players.get(move.getPlayer()).leaveGame();
                return;
        }
        try {
            switch (phase) {
                case EVOLUTION:
                    EvolutionPhase.processMove(this, move);
                    break;
                case FEED:
                    FeedPhase.processMove(this, move);
                    break;
                case END:
                    break;
            }
        } catch (GameException e) {
            error = e.getMessage();
        }
    }

    void deleteFood() {
        food--;
    }

    void playTailLoss(Animal predator, Animal victim) {
        List<Integer> victims = new ArrayList<>(victim.getId());
        extraMessage = new ExtraMessage(predator.getOwner().getName(), predator.getId(), victim.getOwner().getName(), Property.TAIL_LOSS);
        extraMessage.setVictims(victims);
    }

    void afterTailLoss() {
        String pl = extraMessage.getPlayerOnAttack();
        playerOnMove = playersOrder.indexOf(pl);
        extraMessage = null;
    }

    void playMimicry(Animal predator, Animal victim, List<Integer> list) {
        extraMessage = new ExtraMessage(predator.getOwner().getName(), predator.getId(), victim.getOwner().getName(), Property.MIMICRY);
        extraMessage.setVictims(list);
    }

    void afterMimicry() {
        String pl = extraMessage.getPlayerOnAttack();
        playerOnMove = playersOrder.indexOf(pl);
        extraMessage = null;
    }

    void switchPlayerOnMove() {
        // circular array
        playerOnMove = (playerOnMove + 1) % playersOrder.size();
    }

    int getFood() {
        return food;
    }

    void feedScavenger(String name) {
        List<String> scavengerOwners = new ArrayList<>(players.keySet());
        int start = 0;
        for (int i = 0; i < scavengerOwners.size(); i++) {
            if (scavengerOwners.get(i).equals(name)) {
                start = i;
                break;
            }
        }

        for (int i = start; i < scavengerOwners.size() + start; i++) {
            int k = i % scavengerOwners.size(); //circular array
            Player player = players.get(scavengerOwners.get(k));
            if (player.feedScavenger())
                break;
        }
    }

    void addLogMessage(String... s) {
        StringBuilder sb = new StringBuilder(log);
        for (String str : s)
            sb.append(str);

        lastLogMessage = sb.toString();
        log.append(lastLogMessage);
    }

    void makeAnimal(Move move) {
        Player player = players.get(move.getPlayer());
        Animal animal = new Animal(animalID++, player);
        animal.setObserver(this);
        changedAnimals.add(animal);
        player.deleteCard(move.getCardId());
        player.addAnimal(animal);
    }

    void updateChanges(Animal animal, String type) {
        if ("change".equals(type)) changedAnimals.add(animal);
        else if ("delete".equals(type)) {
            animal.deleteObserver();
            deletedAnimalsId.add(animal.getId());
        }
    }

    Animal getAnimal(int id) {
        for (Player player : players.values())
            if (player.hasAnimal(id))
                return player.getAnimal(id);
        throw new IllegalArgumentException();
    }

    Player getPlayer(String name) {
        return players.get(name);
    }

    List<String> getPlayersOrder() {
        return playersOrder;
    }

    Phase getPhase() {
        return phase;
    }

    int getRound() {
        return round;
    }

    ExtraMessage getExtraMessage() {
        return extraMessage;
    }


    private void resetPlayersOrder() {
        playersOrder = new ArrayList<>(players.keySet());
        playersOrder.sort(Comparator.comparingInt(s -> players.get(s).getOrder())); //important to keep order of players
    }

    private void playerEndsPhase(String name) {
        playersOrder.remove(name);
        if (playersOrder.isEmpty())
            goToNextPhase();
        else switchPlayerOnMove();
    }

    private void goToNextPhase() {
        switch (phase) {
            case EVOLUTION:
                food = ThreadLocalRandom.current().nextInt(Constants.FOOD.minFoodFor(numberOfPlayers), Constants.FOOD.maxFoodFor(numberOfPlayers) + 1);
                phase = Phase.FEED;
                break;
            case FEED:
                if (cardList.isEmpty()) endGame();
                else {
                    players.values().forEach(player -> {
                        player.animalsDie();
                        player.resetFields();
                        player.setRequiredCards();
                    });
                    giveCards();
                    phase = Phase.EVOLUTION;
                    round++;
                }

                //last round
                if (cardList.isEmpty()) round = -1;
                break;
        }
        resetPlayersOrder();
        //circular array; each round starts by next player
        playerOnMove = round % players.size();
    }

    private void endGame() {
        phase = Phase.END;
        List<Player> sorted = new ArrayList<>(players.values());
        sorted.sort(Comparator.comparing(Player::getPoints).thenComparing(Player::getUsedCards).reversed());
        winners = sorted.stream().map(Player::pointsToString).collect(Collectors.joining("\n"));
    }

    private void giveCards() {

        while (!cardList.isEmpty()) {
            int flag = players.size();
            for (Player player : players.values()) {
                if (player.getRequiredCards() == 0) flag--;
                else
                    player.addCard(cardList.remove(cardList.size() - 1));
                if (cardList.isEmpty()) return;
            }
            if (flag == 0) return;
        }
    }

    private void addCardsOnStart(Player player) {
        for (int i = 0; i < Constants.START_NUMBER_OF_CARDS.getValue(); i++)
            player.addCard(cardList.remove(cardList.size() - 1));

    }
}
