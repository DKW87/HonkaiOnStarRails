package com.github.dkw87.honkaionstarrails.repository.memory;

public class CombatData {

    private volatile boolean inCombat;
    private volatile int amountOfEnemies;
    private volatile int currentSkillpoints;
    private volatile int turn;

    private CombatData() {}

    // getters
    public boolean getInCombat() {
        return inCombat;
    }

    public int getAmountOfEnemies() {
        return amountOfEnemies;
    }

    public int getCurrentSkillpoints() {
        return currentSkillpoints;
    }

    public int getTurn() {
        return turn;
    }

    // setters
    public void setInCombat(boolean inCombat) {
        this.inCombat = inCombat;
    }

    public void setAmountOfEnemies(int amountOfEnemies) {
        this.amountOfEnemies = amountOfEnemies;
    }

    public void setCurrentSkillpoints(int currentSkillpoints) {
        this.currentSkillpoints = currentSkillpoints;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

}
