package com.github.dkw87.honkaionstarrails.repository.memory;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class CombatData {

    public static final AtomicBoolean IN_COMBAT =  new AtomicBoolean(false);
    public static final AtomicInteger AMOUNT_OF_ENEMIES = new AtomicInteger(0);
    public static final AtomicInteger AMOUNT_OF_SKILLPOINTS = new AtomicInteger(0);
    public static final AtomicInteger TURN_COUNTER = new AtomicInteger(0);

    private CombatData() {}

}
