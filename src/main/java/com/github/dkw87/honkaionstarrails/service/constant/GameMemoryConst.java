package com.github.dkw87.honkaionstarrails.service.constant;

import com.github.dkw87.honkaionstarrails.model.GameMemoryData;
import com.github.dkw87.honkaionstarrails.model.enumeration.MemoryType;

/**
 * GameAssembly.dll contains all data we read from the game and the anchor is where we hook into it.
 */
public class GameMemoryConst {

    public static final String GAME_ASSEMBLY_MODULE = "GameAssembly.dll";
    public static final long ANCHOR = 0x43B4588L;

    // shortened enum
    private static final MemoryType TYPE_BYTE = MemoryType.BYTE;
    private static final MemoryType TYPE_INT = MemoryType.INT;
    private static final MemoryType TYPE_LONG = MemoryType.LONG;

    public static final GameMemoryData STARTED_COMBAT =
            new  GameMemoryData(OffsetConst.STARTED_COMBAT, PointerConst.NO_PTR, TYPE_BYTE);
    public static final GameMemoryData IN_COMBAT =
            new GameMemoryData(OffsetConst.IN_COMBAT, PointerConst.NO_PTR, TYPE_BYTE);
    public static final GameMemoryData IS_COMBAT_VIEW_OPEN =
            new GameMemoryData(OffsetConst.IS_COMBAT_VIEW_OPEN, PointerConst.NO_PTR, TYPE_BYTE);
    public static final GameMemoryData IS_COMBAT_PAUSED =
            new GameMemoryData(OffsetConst.IS_COMBAT_PAUSED, PointerConst.NO_PTR, TYPE_BYTE);
    public static final GameMemoryData CURRENT_TURN =
            new GameMemoryData(OffsetConst.CURRENT_TURN, PointerConst.NO_PTR, TYPE_BYTE);
    public static final GameMemoryData SKILL_POINTS =
            new GameMemoryData(OffsetConst.SKILL_POINTS, PointerConst.SKILL_POINTS, TYPE_BYTE);

    private GameMemoryConst() {}

}
