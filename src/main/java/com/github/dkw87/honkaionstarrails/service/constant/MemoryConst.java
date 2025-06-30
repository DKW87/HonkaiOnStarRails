package com.github.dkw87.honkaionstarrails.service.constant;

/**
 * GameAssembly.dll contains all data we read from the game and the anchor is where we hook into it.
 */
public class MemoryConst {

    public static final String GAME_ASSEMBLY_MODULE = "GameAssembly.dll";
    public static final long ANCHOR = 0x43B4588L;

    private MemoryConst() {}

}
