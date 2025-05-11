package com.github.dkw87.honkaionstarrails.service;

import com.github.dkw87.honkaionstarrails.service.constant.CombatOffsets;

import java.util.concurrent.atomic.AtomicBoolean;

public class CombatMonitorService {

    public static final AtomicBoolean isInCombat = new AtomicBoolean(false);
    public static final AtomicBoolean isCombatPaused = new AtomicBoolean(false);
    public static final AtomicBoolean isCombatViewOpen = new AtomicBoolean(false);

    private final MemoryReadingService memoryReadingService;

    public CombatMonitorService() {
        this.memoryReadingService = new MemoryReadingService();
        memoryReadingService.initialize();
    }

    public boolean runMonitor() {
        isInCombat();
        if (isInCombat.get()) {
            isCombatPaused();
            isCombatViewOpen();
        }
        System.out.printf("inCombat: %s, paused: %s, combatView: %s\n", isInCombat.get(), isCombatPaused.get(), isCombatViewOpen.get());
        return isInCombat.get();
    }

    public boolean isInCombat() {
        Long gameAssemblyModule = getModuleBaseAddress(CombatOffsets.GAME_ASSEMBLY_MODULE);
        if (moduleNotFound(gameAssemblyModule)) return false;

        byte combatStart = memoryReadingService.readByteFromAddress(gameAssemblyModule + CombatOffsets.COMBAT_START);
        byte combatReady = memoryReadingService.readByteFromAddress(gameAssemblyModule + CombatOffsets.COMBAT_READY);

        isInCombat.set(combatStart > 0
                        && combatReady > 0);
        return isInCombat.get();
    }

    public boolean isCombatPaused() {
        Long gameAssemblyModule = getModuleBaseAddress(CombatOffsets.GAME_ASSEMBLY_MODULE);
        if (moduleNotFound(gameAssemblyModule)) return false;

        byte combatPaused1 = memoryReadingService.readByteFromAddress(gameAssemblyModule + CombatOffsets.COMBAT_PAUSED_1);
        byte combatPaused2 = memoryReadingService.readByteFromAddress(gameAssemblyModule + CombatOffsets.COMBAT_PAUSED_2);

        isCombatPaused.set(combatPaused1 > 0
                            && combatPaused2 > 0);
        return isCombatPaused.get();
    }

    public boolean isCombatViewOpen() {
        Long gameAssemblyModule = getModuleBaseAddress(CombatOffsets.GAME_ASSEMBLY_MODULE);
        if (moduleNotFound(gameAssemblyModule)) return false;

        byte combatViewActivated = memoryReadingService.readByteFromAddress(gameAssemblyModule + CombatOffsets.COMBAT_VIEW_ACTIVATED);
        byte combatViewReady1 = memoryReadingService.readByteFromAddress(gameAssemblyModule + CombatOffsets.COMBAT_VIEW_READY_1);
        byte combatViewReady2 = memoryReadingService.readByteFromAddress(gameAssemblyModule + CombatOffsets.COMBAT_VIEW_READY_2);

        isCombatViewOpen.set(combatViewActivated >0
                && combatViewReady1 > 0
                && combatViewReady2 > 0);
        return isCombatViewOpen.get();
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
        if (notFound) System.out.println("Module base address was not found");
        return notFound;
    }

}
