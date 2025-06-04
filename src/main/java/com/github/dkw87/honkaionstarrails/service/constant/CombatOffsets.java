package com.github.dkw87.honkaionstarrails.service.constant;


public class CombatOffsets {

    public static final String GAME_ASSEMBLY_MODULE = "GameAssembly.dll";
    public static final long ANCHOR = 0x43B4588L;

    // need to be updated start
    public static final long IS_COMBAT_STARTING = ANCHOR;
    public static final long IS_COMBAT_INITIALIZING = ANCHOR - 0x1930L;
    public static final long IS_COMBAT_INITIALIZED = ANCHOR - 0x1F58L;

    public static final long IS_COMBAT_PAUSED = ANCHOR - 0x18F1L;
    public static final long IS_COMBAT_VIEW_OPEN = ANCHOR - 0x18F2L;
    // need to be updated end

    public static final long TURN_COUNTER = ANCHOR + 0x1FB50L;

    public static final long SKILLPOINTS = ANCHOR - 0x8768L;
    public static final int[] SKILLPOINTS_PTR_CHAIN = {
            0xF80,
            0x88,
            0x278,
            0x38,
            0x18,
            0x10,
            0x28,
            0
    };

    public static final long AMOUNT_OF_ENEMIES = ANCHOR - 0x8768L;
    public static final int[] AMOUNT_OF_ENEMIES_PTR_CHAIN = {
            0xF80,
            0x88,
            0x278,
            0x48,
            0x38,
            0x54,
            0
    };

    private CombatOffsets() {}

}
