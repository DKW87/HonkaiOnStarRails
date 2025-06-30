package com.github.dkw87.honkaionstarrails.service.constant.offset;

import com.github.dkw87.honkaionstarrails.service.constant.MemoryConst;

/**
 * Combat offsets which use the anchor as the base and navigate to their relative
 * position by adding/subtracting to that. Should ensure data is easily reaccessed
 * after a game update where the anchor changes.
 */
public class CombatOffsets {

    private static final long ANCHOR = MemoryConst.ANCHOR;

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
