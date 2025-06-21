package com.github.dkw87.honkaionstarrails.repository.memory;

import lombok.Getter;
import lombok.Setter;

import java.awt.image.BufferedImage;

@Getter
@Setter
public class CombatData {

    private volatile boolean inCombat;
    private volatile int amountOfEnemies;
    private volatile int currentSkillpoints;
    private volatile int turn;
    private volatile BufferedImage currentTurnImage;
    private volatile int lastAnalyzedTurn;

    private CombatData() {}

}
