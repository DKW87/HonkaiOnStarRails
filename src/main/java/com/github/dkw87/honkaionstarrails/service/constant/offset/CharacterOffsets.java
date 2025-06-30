package com.github.dkw87.honkaionstarrails.service.constant.offset;

import com.github.dkw87.honkaionstarrails.service.constant.MemoryConst;

/**
 * Character offsets which use the anchor as the base and navigate to their relative
 * position by adding/subtracting to that. Should ensure data is easily reaccessed
 * after a game update where the anchor changes.
 */
public class CharacterOffsets {

    private static final long ANCHOR = MemoryConst.ANCHOR;

    public static final long ACTIVE_CHAR_MAX_HEALTH = ANCHOR - 0x8768L;
    public static final long ACTIVE_CHAR_CURRENT_HEALTH = ANCHOR - 0x8768L;

    private CharacterOffsets() {}

}
