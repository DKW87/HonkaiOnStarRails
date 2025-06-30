package com.github.dkw87.honkaionstarrails.service.constant.chain;

/**
 * The necessary steps to take in order to navigate to the data we need
 */
public class CharacterPtrChains {

    public static final int[] ACTIVE_CHAR_MAX_HEALTH = { 0xF80, 0xA0, 0x18, 0x278, 0x20, 0xC0, 0x28, 0x64 };
    public static final int[] ACTIVE_CHAR_CURRENT_HEALTH = { 0xF80, 0x90, 0xA0, 0x18, 0x278, 0x20, 0xC0, 0x70, 0x64 };

    private CharacterPtrChains() {}
}
