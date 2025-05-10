package com.github.dkw87.honkaionstarrails.service.constant;


public class CombatOffsets {

    public static final String GAME_ASSEMBLY_MODULE = "GameAssembly.dll";

    public static final long COMBAT_START = 0x426C1D0L;
    public static final long COMBAT_READY = 0x4264EE8L;

    public static final long SKILLPOINTS_BASE = 0x4272180L;
    public static final int[] SKILLPOINTS_OFFSETS = {
            0x318,
            0x20,
            0x18,
            0x190,
            0x10C,
            0
    };

    private CombatOffsets() {}

}
