package com.github.dkw87.honkaionstarrails.service.constant;


public class CombatOffsets {

    public static final String GAME_ASSEMBLY_MODULE = "GameAssembly.dll";

    public static final long BASE = 0x43AFD00L;

    public static final long IS_COMBAT_STARTING = BASE;
    public static final long IS_COMBAT_INITIALIZING = BASE - 0x1930L;
    public static final long IS_COMBAT_INITIALIZED = BASE - 0x1F58L;

    public static final long IS_COMBAT_PAUSED = BASE - 0x18F1L;
    public static final long IS_COMBAT_VIEW_OPEN = BASE - 0x18F2L;

    public static final long TURN_COUNTER = BASE + 0x2E94L;

    // not stable
    public static final long SKILLPOINTS_BASE = 0x4272180L;
    public static final int[] SKILLPOINTS_PTR_CHAIN = {
            0x318,
            0x20,
            0x18,
            0x190,
            0x10C,
            0
    };
    // might actually be more stable than original
    public static final long SKILLPOINTS_BACKUP_BASE = 0x4261C88L;
    public static final int[] SKILLPOINTS_BACKUP_CHAIN = {
            0x80,
            0x158,
            0x88,
            0x10,
            0x1C8,
            0x88,
            0x28,
            0
    };

    private CombatOffsets() {}

}
