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

    public static volatile boolean isInCombat;
    public static volatile boolean isCombatPaused;
    public static volatile boolean isCombatViewOpen;

    private static final Logger LOGGER = LoggerFactory.getLogger(CombatMonitorService.class);

    @Getter
    private final MemoryReadingService memoryReadingService;
    private final CombatData combatData;

    public CombatMonitorService() {
        LOGGER.info("Initializing CombatMonitorService...");
        this.memoryReadingService = MemoryReadingService.getInstance();
        this.combatData = CombatData.getInstance();
    }

    public boolean runMonitor() {
        if (isInCombat()) {
            // TODO need to fix these methods, see below
            isCombatPaused();
            isCombatViewOpen();
        }
        return isInCombat();
    }

    public boolean isInCombat() {
        boolean inCombat = GameMemoryConst.IN_COMBAT.readFromMemory().equals(1);
        combatData.setInCombat(inCombat);
        return combatData.isInCombat();
    }

    public void isCombatPaused() {
//        Long gameAssemblyModule = getModuleBaseAddress(MemoryConst.GAME_ASSEMBLY_MODULE);
//        if (moduleNotFound(gameAssemblyModule)) return;
//
//        byte isPaused = memoryReadingService.readByteFromAddress(gameAssemblyModule + CombatOffsets.IS_COMBAT_PAUSED);
//
//        isCombatPaused = (isPaused == 1);
    }

    public void isCombatViewOpen() {
//        Long gameAssemblyModule = getModuleBaseAddress(MemoryConst.GAME_ASSEMBLY_MODULE);
//        if (moduleNotFound(gameAssemblyModule)) return;
//
//        byte isOpen = memoryReadingService.readByteFromAddress(gameAssemblyModule + CombatOffsets.IS_COMBAT_VIEW_OPEN);
//
//        isCombatViewOpen = (isOpen == 1);
    }

}
