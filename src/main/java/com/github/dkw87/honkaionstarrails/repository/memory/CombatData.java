package com.github.dkw87.honkaionstarrails.repository.memory;

import lombok.Getter;
import lombok.Setter;

import java.awt.image.BufferedImage;

@Getter
@Setter
public class CombatData {

    private volatile boolean inCombat;
    private volatile int currentWave;
    private volatile int totalWaves;
    private volatile int amountOfEnemies;
    private volatile int currentSkillpoints;
    private volatile int turn;
    private volatile BufferedImage currentTurnImage;

    // character specific
    private volatile int charOneCurrentHealth;
    private volatile int charTwoCurrentHealth;
    private volatile int charThreeCurrentHealth;
    private volatile int charFourCurrentHealth;

    private CombatData() {}

    public static CombatData getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final CombatData INSTANCE = new CombatData();
    }

}
