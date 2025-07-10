package com.github.dkw87.honkaionstarrails.service.constant;

import com.github.dkw87.honkaionstarrails.model.GameMemoryData;
import com.github.dkw87.honkaionstarrails.model.enumeration.MemoryType;

/**
 * GameAssembly.dll contains all data we read from the game and the anchor is where we hook into it.
 */
public class MemoryConst {

    public static final String GAME_ASSEMBLY_MODULE = "GameAssembly.dll";
    public static final long ANCHOR = 0x43B4588L;

    // shortened enum
    private static final MemoryType TYPE_BYTE = MemoryType.BYTE;
    private static final MemoryType TYPE_INT = MemoryType.INT;
    private static final MemoryType TYPE_LONG = MemoryType.LONG;

    // offsets
    private static final Long IN_COMBAT_OFFSET = 0x4C2EBA0L;

    // chains
    private static final Integer[] NULL_PTR_CHAIN = null;

    // GameMemoryData
    public static final GameMemoryData IN_COMBAT =
            new GameMemoryData(IN_COMBAT_OFFSET, NULL_PTR_CHAIN, TYPE_BYTE);


    private MemoryConst() {}

}
