package com.github.dkw87.honkaionstarrails.service.constant;


public class CombatOffsets {

    public static final String GAME_ASSEMBLY_MODULE = "GameAssembly.dll";
    public static final long ANCHOR = 0x43B4588L;

    // need to be updated start
    public static final long IS_COMBAT_PAUSED = ANCHOR - 0x18F1L;
    public static final long IS_COMBAT_VIEW_OPEN = ANCHOR - 0x18F2L;
    // need to be updated end

    // flags
    public static final long IN_COMBAT = ANCHOR + 0x9CC30L;

    // stats
    public static final long AMOUNT_OF_ENEMIES = ANCHOR - 0x8768L;
    public static final long SKILLPOINTS = ANCHOR - 0x8768L;
    public static final long TURN_COUNTER = ANCHOR + 0xEAAC8L;

    private CombatOffsets() {}

}
