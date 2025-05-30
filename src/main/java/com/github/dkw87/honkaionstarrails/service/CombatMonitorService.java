package com.github.dkw87.honkaionstarrails.service;

import com.github.dkw87.honkaionstarrails.service.constant.CombatOffsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

public class CombatMonitorService {

    public static final AtomicBoolean isInCombat = new AtomicBoolean(false);
    public static final AtomicBoolean isCombatPaused = new AtomicBoolean(false);
    public static final AtomicBoolean isCombatViewOpen = new AtomicBoolean(false);

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
        if (isInCombat.get()) {
            isCombatPaused();
            isCombatViewOpen();
        }
        return isInCombat.get();
    }

    private void logCombatState() {
        if (isInCombat.get() != lastInCombat ||
            isCombatPaused.get() != lastCombatPaused ||
            isCombatViewOpen.get() != lastCombatViewOpen) {
            LOGGER.info("CombatState info: isInCombat - {}, isCombatPaused - {}, isCombatViewOpen - {}",
                    isInCombat.get(), isCombatPaused.get(), isCombatViewOpen.get());
            lastInCombat = isInCombat.get();
            lastCombatPaused = isCombatPaused.get();
            lastCombatViewOpen = isCombatViewOpen.get();
        }
    }

    public void isInCombat() {
        Long gameAssemblyModule = getModuleBaseAddress(CombatOffsets.GAME_ASSEMBLY_MODULE);
        if (moduleNotFound(gameAssemblyModule)) return;

        byte combatStart = memoryReadingService.readByteFromAddress(gameAssemblyModule + CombatOffsets.COMBAT_START);
        byte combatReady = memoryReadingService.readByteFromAddress(gameAssemblyModule + CombatOffsets.COMBAT_READY);

        isInCombat.set(combatStart > 0
                        && combatReady > 0);
    }

    public void isCombatPaused() {
        Long gameAssemblyModule = getModuleBaseAddress(CombatOffsets.GAME_ASSEMBLY_MODULE);
        if (moduleNotFound(gameAssemblyModule)) return;

        byte combatPaused1 = memoryReadingService.readByteFromAddress(gameAssemblyModule + CombatOffsets.COMBAT_PAUSED_1);
        byte combatPaused2 = memoryReadingService.readByteFromAddress(gameAssemblyModule + CombatOffsets.COMBAT_PAUSED_2);

        isCombatPaused.set(combatPaused1 > 0
                            && combatPaused2 > 0);
    }

    public void isCombatViewOpen() {
        Long gameAssemblyModule = getModuleBaseAddress(CombatOffsets.GAME_ASSEMBLY_MODULE);
        if (moduleNotFound(gameAssemblyModule)) return;

        byte combatViewActivated = memoryReadingService.readByteFromAddress(gameAssemblyModule + CombatOffsets.COMBAT_VIEW_ACTIVATED);
        byte combatViewReady1 = memoryReadingService.readByteFromAddress(gameAssemblyModule + CombatOffsets.COMBAT_VIEW_READY_1);
        byte combatViewReady2 = memoryReadingService.readByteFromAddress(gameAssemblyModule + CombatOffsets.COMBAT_VIEW_READY_2);

        isCombatViewOpen.set(combatViewActivated >0
                && combatViewReady1 > 0
                && combatViewReady2 > 0);
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
