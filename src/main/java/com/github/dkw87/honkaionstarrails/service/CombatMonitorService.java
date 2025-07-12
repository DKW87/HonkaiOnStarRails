package com.github.dkw87.honkaionstarrails.service;

import com.github.dkw87.honkaionstarrails.repository.memory.CombatData;
import com.github.dkw87.honkaionstarrails.service.constant.GameMemoryConst;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class is only executed by GameStateService to report whether
 * HSR is in combat, paused or combat view is open (effectively paused).
 * This will only run once the game is in focus. Uses MemoryReadingService
 * to read these flags.
 */
public class CombatMonitorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CombatMonitorService.class);

    @Getter
    private final MemoryReadingService memoryReadingService;
    private final CombatData combatData;

    public CombatMonitorService() {
        LOGGER.info("Initializing CombatMonitorService...");
        this.memoryReadingService = MemoryReadingService.getInstance();
        this.combatData = CombatData.getInstance();
    }

    public boolean isInCombat() {
        boolean startedCombat = GameMemoryConst.STARTED_COMBAT.readFromMemory().equals((byte) 1);
        boolean inCombat = GameMemoryConst.IN_COMBAT.readFromMemory().equals((byte) 1);

        combatData.setInCombat(startedCombat && inCombat);

        return combatData.isInCombat();
    }

    public boolean isCombatPaused() {
        boolean isCombatPaused = GameMemoryConst.IS_COMBAT_PAUSED.readFromMemory().equals((byte) 1);
        combatData.setCombatPaused(isCombatPaused);
        return combatData.isCombatPaused();
    }

    public boolean isCombatViewOpen() {
        boolean isCombatViewOpen = GameMemoryConst.IS_COMBAT_VIEW_OPEN.readFromMemory().equals((byte) 1);
        combatData.setCombatViewOpen(isCombatViewOpen);
        return combatData.isCombatViewOpen();
    }

}
