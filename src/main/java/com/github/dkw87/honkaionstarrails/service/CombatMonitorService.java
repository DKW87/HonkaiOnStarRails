package com.github.dkw87.honkaionstarrails.service;

import com.github.dkw87.honkaionstarrails.repository.memory.CombatData;
import com.github.dkw87.honkaionstarrails.service.constant.offset.CombatOffsets;
import com.github.dkw87.honkaionstarrails.service.constant.chain.CombatPtrChains;
import com.github.dkw87.honkaionstarrails.service.constant.MemoryConst;
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
        Long gameAssemblyModule = getModuleBaseAddress(MemoryConst.GAME_ASSEMBLY_MODULE);
        if (moduleNotFound(gameAssemblyModule)) return false;

        long address = memoryReadingService.readLongFromAddress(gameAssemblyModule + CombatOffsets.IN_COMBAT);
        int inCombat = memoryReadingService.followPTRChain(address, CombatPtrChains.IN_COMBAT);

        combatData.setInCombat(inCombat == 1);
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

    private Long getModuleBaseAddress(String module) {
        if (!memoryReadingService.isInitialized()) return -1L;
        Long moduleBase = memoryReadingService.getModuleBaseAddresses(module);
        if (!memoryReadingService.gameModuleExists(moduleBase)) return -1L;
        return moduleBase;
    }

    private boolean moduleNotFound(Long module) {
        boolean notFound = (module == -1L);
        if (notFound) LOGGER.warn("Module base address was not found");
        return notFound;
    }

}
