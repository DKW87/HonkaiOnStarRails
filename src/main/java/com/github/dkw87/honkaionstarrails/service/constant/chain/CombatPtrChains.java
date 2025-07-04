package com.github.dkw87.honkaionstarrails.service.constant.chain;

/**
 * The necessary steps to take in order to navigate to the data we need
 */
public class CombatPtrChains {

    //flags
    public static final int[] IN_COMBAT = { 0x50, 0x90, 0x8, 0x48, 0x58, 0x0, 0x24 };

    public static final int[] AMOUNT_OF_ENEMIES = { 0xF80, 0x88, 0x278, 0x48, 0x38, 0x54 };
    public static final int[] SKILLPOINTS = { 0xF80, 0x88, 0x278, 0x38, 0x18, 0x10, 0x28 };
    public static final int[] TURN_COUNTER = { 0x90, 0x10, 0xC8, 0x0, 0x18, 0x20, 0x20 };

    private CombatPtrChains() {}

}
