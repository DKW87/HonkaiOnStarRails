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

    public static final long SKILLPOINTS_BASE = BASE - 0x3EE0L;
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

    public static final long AMOUNT_OF_ENEMIES_BASE = BASE - 0x3EE0L;
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
