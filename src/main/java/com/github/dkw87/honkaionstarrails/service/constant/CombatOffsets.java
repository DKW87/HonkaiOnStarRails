package com.github.dkw87.honkaionstarrails.service.constant;


public class CombatOffsets {

    public static final String GAME_ASSEMBLY_MODULE = "GameAssembly.dll";

    public static final long COMBAT_START = 0x426C1D0L;
    public static final long COMBAT_READY = 0x4264EE8L;

    public static final long COMBAT_PAUSED_1 = 0x42659BDL;
    public static final long COMBAT_PAUSED_2 = 0x42659BCL;

    public static final long COMBAT_VIEW_ACTIVATED = 0x426B804L;
    public static final long COMBAT_VIEW_READY_1 = 0x42659C0L;
    public static final long COMBAT_VIEW_READY_2 = 0x426C960L;

    public static final long TURN_COUNTER = 0x426C670L;

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
