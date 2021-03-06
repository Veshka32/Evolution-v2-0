package com.game;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.game.constants.Constants;
import com.game.constants.Property;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY) //skip empty collections
public class Animal {
    @JsonIgnore
    private final Player owner;
    //include in json
    private final String ownerName;
    private final int id;
    private final List<Property> propertyList = new ArrayList<>();
    private final List<Integer> cooperateTo = new ArrayList<>();
    private final List<Integer> communicateTo = new ArrayList<>();
    private final List<Integer> symbiontFor = new ArrayList<>();
    private final List<Integer> symbiosisWith = new ArrayList<>();
    @JsonIgnore
    private boolean fed;
    @JsonIgnore
    private boolean poisoned;
    @JsonIgnore
    private int totalFatSupply;
    @JsonIgnore
    private Game observer;
    @JsonIgnore
    private boolean doPiracy;
    @JsonIgnore
    private boolean doGrazing;
    @JsonIgnore
    private int hibernationRound;
    @JsonIgnore
    private boolean attack;
    private int hungry = Constants.MIN_HUNGRY.getValue();

    public String getOwnerName() {
        return ownerName;
    }

    public int getCurrentFatSupply() {
        return currentFatSupply;
    }

    public void setCurrentFatSupply(int currentFatSupply) {
        this.currentFatSupply = currentFatSupply;
    }

    public int getHungry() {
        return hungry;
    }

    private int currentFatSupply;

    public void setHungry(int hungry) {
        this.hungry = hungry;
    }

    public List<Property> getPropertyList() {
        return propertyList;
    }

    public List<Integer> getCooperateTo() {
        return cooperateTo;
    }

    public List<Integer> getCommunicateTo() {
        return communicateTo;
    }

    public List<Integer> getSymbiontFor() {
        return symbiontFor;
    }

    public List<Integer> getSymbiosisWith() {
        return symbiosisWith;
    }

    public Animal(int id, Player player) {
        this.id = id;
        owner = player;
        ownerName = player.getName();
    }

    void setObserver(Game game) {
        observer = game;
    }

    void deleteObserver() {
        observer = null;
    }

    void notifyObserver(String type) {
        observer.updateChanges(this, type);
    }

    public int getId() {
        return id;
    }

    Player getOwner() {
        return owner;
    }

    void setHungry() {
        hungry = calculateHungry();
        observer.updateChanges(this, "change");
    }

    int calculateHungry() {
        int result = Constants.MIN_HUNGRY.getValue();
        if (hasProperty(Property.PREDATOR)) result++;
        if (hasProperty(Property.BIG)) result++;
        if (hasProperty(Property.PARASITE)) result += 2;
        return result;
    }

    void deleteFood() {
        hungry++;
        observer.updateChanges(this, "change");
    }

    void poison() {
        poisoned = true;
    }

    void setAttack(boolean bool) {
        attack = bool;
    }

    boolean doPiracy() {
        return doPiracy;
    }

    void setDoPiracy(boolean bool) {
        doPiracy = bool;
    }

    boolean doGrazing() {
        return doGrazing;
    }

    void setDoGrazing(boolean bool) {
        doGrazing = bool;
    }

    void hibernate(int round) throws GameException {
        if (round == -1)
            throw new GameException("You can't hibernate in last round");
        else if (round != 0 && round == hibernationRound) //can hibernate in 0 round
            throw new GameException("This animal is already in hibernation");
        else if (round - hibernationRound == 1)
            throw new GameException("You can't hibernate 2 rounds in a row");

        hibernationRound = round;
        hungry = 0;
        observer.updateChanges(this, "change");
    }

    void eatFat() throws GameException {
        if (currentFatSupply < 1) throw new GameException("You have no fat supply");
        if (hungry < 1) throw new GameException("Animal is fed");
        hungry--;
        currentFatSupply--;
        observer.updateChanges(this, "change");
    }

    void attack(Animal victim) throws GameException {
        //exceptions
        if (!hasProperty(Property.PREDATOR))
            throw new GameException("This animal is not a predator");

        if (attack)
            throw new GameException("This predator has been used");

        if (victim.hasProperty(Property.SWIMMING) && !hasProperty(Property.SWIMMING))
            throw new GameException("Not-swimming predator can't eat swimming animal");

        if (hasProperty(Property.SWIMMING) && !victim.hasProperty(Property.SWIMMING))
            throw new GameException("Swimming predator can't eat non-swimming animal");

        if (victim.hasProperty(Property.BIG) && !hasProperty(Property.BIG))
            throw new GameException("You can't eat such a big animal");

        if (!victim.symbiosisWith.isEmpty())
            throw new GameException("You can't eat this animal while its symbiont is alive");

        if (victim.hasProperty(Property.BURROWING) && victim.hungry == 0)
            throw new GameException("This animal is fed and in burrow");

        if (victim.hasProperty(Property.CAMOUFLAGE) && !hasProperty(Property.SHARP_VISION))
            throw new GameException("This animal is in camouflage");
    }

    void die() {
        for (int id : cooperateTo) {
            Animal animal = owner.getAnimal(id);
            animal.cooperateTo.remove(Integer.valueOf(this.id));
            if (animal.cooperateTo.isEmpty()) animal.propertyList.remove(Property.COOPERATION);
            observer.updateChanges(animal, "change");
            owner.increaseUsedCards();
        }

        for (int id : communicateTo) {
            Animal animal = owner.getAnimal(id);
            animal.communicateTo.remove(Integer.valueOf(this.id));
            if (animal.communicateTo.isEmpty()) animal.propertyList.remove(Property.COMMUNICATION);
            observer.updateChanges(animal, "change");
            owner.increaseUsedCards();
        }

        for (int id : symbiosisWith) {
            Animal animal = owner.getAnimal(id);
            animal.symbiontFor.remove(Integer.valueOf(this.id));
            if (animal.symbiontFor.isEmpty() && animal.symbiosisWith.isEmpty())
                animal.propertyList.remove(Property.SYMBIOSIS);
            observer.updateChanges(animal, "change");
            owner.increaseUsedCards();
        }

        for (int id : symbiontFor) {
            Animal animal = owner.getAnimal(id);
            animal.symbiosisWith.remove(Integer.valueOf(this.id));
            if (animal.symbiontFor.isEmpty() && animal.symbiosisWith.isEmpty())
                animal.propertyList.remove(Property.SYMBIOSIS);
            observer.updateChanges(animal, "change");
            owner.increaseUsedCards();
        }

        observer.updateChanges(this, "delete");
    }

    void addProperty(Property property) throws GameException {

        if (property.equals(Property.SCAVENGER) && propertyList.contains(Property.PREDATOR))
            throw new GameException("Predator cannot be a scavenger");

        else if (property.equals(Property.PREDATOR) && propertyList.contains(Property.SCAVENGER))
            throw new GameException("Scavenger cannot be a predator");

        else if (property.equals(Property.FAT))
            totalFatSupply++;

        else if (Property.isPropertyDouble(property)) {
            if (hasProperty(property)) return;  //put in list once, but if already has, do not throw exception
        } else if (propertyList.contains(property))
            throw new GameException("This animal already has property: " + property);

        propertyList.add(property);

        if (property.equals(Property.PREDATOR) || property.equals(Property.BIG))
            hungry++;

        if (property.equals(Property.PARASITE))
            hungry += 2;

        observer.updateChanges(this, "change");
    }

    void eatMeet(Player player, Game game) throws GameException {

        if (hungry == 0) {
            if (currentFatSupply == totalFatSupply) throw new GameException("This animal is fed!");
            else {
                currentFatSupply++;
                game.deleteFood();
                return;
            }
        }

        if (!checkSymbiosis(player)) throw new GameException("You should feed the symbiont first");

        fed = true;
        hungry--;
        game.deleteFood();

        cooperateTo.forEach(id -> player.getAnimal(id).eatFish(1));
        communicateTo.forEach(id -> player.getAnimal(id).eatExtraMeet(player, game));

        observer.updateChanges(this, "change");
    }

    void eatFish(int i) {
        if (hungry == 0 || fed || !(checkSymbiosis(owner)))
            return; //abort chain feeding if animal is fed, is already visited or can't get fish

        fed = true;
        if (hungry < i)
            i = 1; // if hungry==1 and fish==2, only one fish goes on
        hungry -= i;

        for (int id : cooperateTo) {
            owner.getAnimal(id).eatFish(i);
        }

        observer.updateChanges(this, "change");
    }

    boolean checkSymbiosis(Player player) { //return false if not all of the symbionts are fed;
        if (!symbiosisWith.isEmpty()) {
            for (int id : symbiosisWith) {
                if (player.getAnimal(id).hungry != 0)
                    return false;
            }
        }
        return true;
    }

    boolean notHungry() {
        return hungry == 0;
    }

    boolean isCommunicate(int id) {
        return communicateTo.contains(id);
    }

    boolean isCooperate(int id) {
        return cooperateTo.contains(id);
    }

    boolean isInSymbiosis(int id) {
        return symbiosisWith.contains(id);
    }

    boolean isSymbiontFor(int id) {
        return symbiontFor.contains(id);
    }

    void setCommunicateTo(int id) {
        communicateTo.add(id);
    }

    void setCooperateTo(int id) {
        cooperateTo.add(id);
    }

    void setSymbiosisWith(int id) {
        symbiosisWith.add(id);
    }

    void setSymbiontFor(int id) {
        symbiontFor.add(id);
    }

    boolean hasProperty(Property property) {
        return propertyList.contains(property);
    }

    void removeProperty(Property property) {
        switch (property) {
            case PARASITE:
                hungry -= Constants.PARASITE_HUNGRITY.getValue();
                propertyList.remove(property);
                break;
            case BIG:
                hungry--;
                propertyList.remove(property);
                break;
            case FAT:
                totalFatSupply--;
                if (currentFatSupply > totalFatSupply) currentFatSupply = totalFatSupply;
                propertyList.remove(property);
                break;
            case PREDATOR:
                hungry--;
                break;
            case COMMUNICATION:
                Animal partner = owner.getAnimal(communicateTo.get(0));
                communicateTo.remove(Integer.valueOf(partner.id));
                partner.communicateTo.remove(Integer.valueOf(this.id));
                if (communicateTo.isEmpty()) propertyList.remove(property);
                if (partner.communicateTo.isEmpty()) partner.propertyList.remove(property);
                observer.updateChanges(partner, "change");
                break;
            case COOPERATION:
                partner = owner.getAnimal(cooperateTo.get(0));
                cooperateTo.remove(Integer.valueOf(partner.id));
                partner.cooperateTo.remove(Integer.valueOf(this.id));
                if (cooperateTo.isEmpty()) propertyList.remove(property);
                if (partner.cooperateTo.isEmpty()) partner.propertyList.remove(property);
                observer.updateChanges(partner, "change");
                break;
            case SYMBIOSIS:
                if (!symbiosisWith.isEmpty()) {
                    partner = owner.getAnimal(symbiosisWith.get(0));
                    symbiosisWith.remove(Integer.valueOf(partner.id));
                    partner.symbiontFor.remove(Integer.valueOf(this.id));
                    observer.updateChanges(partner, "change");
                    if (partner.symbiosisWith.isEmpty() && partner.symbiontFor.isEmpty())
                        partner.propertyList.remove(property);
                } else if (!symbiontFor.isEmpty()) {
                    partner = owner.getAnimal(symbiontFor.get(0));
                    symbiontFor.remove(Integer.valueOf(partner.id));
                    partner.symbiosisWith.remove(Integer.valueOf(this.id));
                    observer.updateChanges(partner, "change");
                    if (partner.symbiosisWith.isEmpty() && partner.symbiontFor.isEmpty())
                        partner.propertyList.remove(property);
                }
                if (symbiosisWith.isEmpty() && symbiontFor.isEmpty()) propertyList.remove(property);
                break;
        }

        if (!Property.isPropertyDouble(property)) propertyList.remove(property);
        owner.increaseUsedCards();
        observer.updateChanges(this, "change");
    }

    private void eatExtraMeet(Player player, Game game) {
        if (game.getFood() == 0) return;
        if (hungry == 0 || fed || !(checkSymbiosis(owner)))
            return; //abort dfs if animal is fed, is already visited or can't get fish

        fed = true;
        hungry--;
        game.deleteFood();
        communicateTo.forEach(id -> player.getAnimal(id).eatExtraMeet(player, game));
        cooperateTo.forEach(id -> player.getAnimal(id).eatFish(1));

        observer.updateChanges(this, "change");
    }

    public boolean isFed() {

        return fed;
    }

    public void setFed(boolean fed) {
        this.fed = fed;
    }

    public boolean isPoisoned() {
        return poisoned;
    }

    public void setPoisoned(boolean poisoned) {
        this.poisoned = poisoned;
    }
}
