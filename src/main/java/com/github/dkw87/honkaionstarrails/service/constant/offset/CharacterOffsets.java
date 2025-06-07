package com.github.dkw87.honkaionstarrails.service.constant.offset;

import com.github.dkw87.honkaionstarrails.service.constant.MemoryConst;

public class CharacterOffsets {

    private static final long ANCHOR = MemoryConst.ANCHOR;

    public static final long ACTIVE_CHAR_MAX_HEALTH = ANCHOR - 0x8768L;
    public static final long ACTIVE_CHAR_CURRENT_HEALTH = ANCHOR - 0x8768L;

    private CharacterOffsets() {}

}
