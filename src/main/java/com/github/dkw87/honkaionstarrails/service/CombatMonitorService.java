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

        byte isCombatStarting = memoryReadingService.readByteFromAddress(gameAssemblyModule + CombatOffsets.IS_COMBAT_STARTING);
        byte isCombatInitializing = memoryReadingService.readByteFromAddress(gameAssemblyModule + CombatOffsets.IS_COMBAT_INITIALIZING);
        byte isCombatInitialized = memoryReadingService.readByteFromAddress(gameAssemblyModule + CombatOffsets.IS_COMBAT_INITIALIZED);

        isInCombat.set(isCombatStarting > 0
                        && isCombatInitializing > 0
                        && isCombatInitialized > 0);
    }

    public void isCombatPaused() {
        Long gameAssemblyModule = getModuleBaseAddress(CombatOffsets.GAME_ASSEMBLY_MODULE);
        if (moduleNotFound(gameAssemblyModule)) return;

        byte isPaused = memoryReadingService.readByteFromAddress(gameAssemblyModule + CombatOffsets.IS_COMBAT_PAUSED);

        isCombatPaused.set(isPaused > 0);
    }

    public void isCombatViewOpen() {
        Long gameAssemblyModule = getModuleBaseAddress(CombatOffsets.GAME_ASSEMBLY_MODULE);
        if (moduleNotFound(gameAssemblyModule)) return;

        byte isOpen = memoryReadingService.readByteFromAddress(gameAssemblyModule + CombatOffsets.IS_COMBAT_VIEW_OPEN);

        isCombatViewOpen.set(isOpen > 0);
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
