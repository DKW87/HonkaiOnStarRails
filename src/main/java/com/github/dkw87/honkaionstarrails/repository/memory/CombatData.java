package com.github.dkw87.honkaionstarrails.repository.memory;

public class CombatData {

    public static volatile boolean IN_COMBAT;
    public static volatile int AMOUNT_OF_ENEMIES;
    public static volatile int AMOUNT_OF_SKILLPOINTS;
    public static volatile int TURN_COUNTER;

    private CombatData() {}

}
