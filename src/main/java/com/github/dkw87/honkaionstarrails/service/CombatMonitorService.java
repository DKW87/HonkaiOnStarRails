package com.github.dkw87.honkaionstarrails.service;

import com.github.dkw87.honkaionstarrails.service.constant.offset.CombatOffsets;
import com.github.dkw87.honkaionstarrails.service.constant.chain.CombatPtrChains;
import com.github.dkw87.honkaionstarrails.service.constant.MemoryConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CombatMonitorService {

    public static volatile boolean isInCombat;
    public static volatile boolean isCombatPaused;
    public static volatile boolean isCombatViewOpen;

    private static final Logger LOGGER = LoggerFactory.getLogger(CombatMonitorService.class);

    private final MemoryReadingService memoryReadingService;

    private boolean lastInCombat;
    private boolean lastCombatPaused;
    private boolean lastCombatViewOpen;

    public CombatMonitorService() {
        LOGGER.info("Initializing CombatMonitorService...");
        this.memoryReadingService = new MemoryReadingService();
    }

    public boolean runMonitor() {
        isInCombat();
        logCombatState();
        if (isInCombat) {
            isCombatPaused();
            isCombatViewOpen();
        }
        return isInCombat;
    }

    private void logCombatState() {
        if (isInCombat != lastInCombat ||
            isCombatPaused != lastCombatPaused ||
            isCombatViewOpen != lastCombatViewOpen) {
            LOGGER.info("CombatState info: isInCombat - {}, isCombatPaused - {}, isCombatViewOpen - {}",
                    isInCombat, isCombatPaused, isCombatViewOpen);
            lastInCombat = isInCombat;
            lastCombatPaused = isCombatPaused;
            lastCombatViewOpen = isCombatViewOpen;
        }
    }

    public void isInCombat() {
        Long gameAssemblyModule = getModuleBaseAddress(MemoryConst.GAME_ASSEMBLY_MODULE);
        if (moduleNotFound(gameAssemblyModule)) return;

        long address = memoryReadingService.readLongFromAddress(gameAssemblyModule + CombatOffsets.IN_COMBAT);
        int inCombat = memoryReadingService.followPTRChain(address, CombatPtrChains.IN_COMBAT);

        isInCombat = (inCombat == 1);
    }

    public void isCombatPaused() {
        Long gameAssemblyModule = getModuleBaseAddress(MemoryConst.GAME_ASSEMBLY_MODULE);
        if (moduleNotFound(gameAssemblyModule)) return;

        byte isPaused = memoryReadingService.readByteFromAddress(gameAssemblyModule + CombatOffsets.IS_COMBAT_PAUSED);

        isCombatPaused = (isPaused == 1);
    }

    public void isCombatViewOpen() {
        Long gameAssemblyModule = getModuleBaseAddress(MemoryConst.GAME_ASSEMBLY_MODULE);
        if (moduleNotFound(gameAssemblyModule)) return;

        byte isOpen = memoryReadingService.readByteFromAddress(gameAssemblyModule + CombatOffsets.IS_COMBAT_VIEW_OPEN);

        isCombatViewOpen = (isOpen == 1);
    }

    public MemoryReadingService getMemoryReadingService() {
        return memoryReadingService;
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
