package com.github.dkw87.honkaionstarrails.service.win32interface;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinDef;

public interface Kernel32Extended extends Kernel32 {
    Kernel32Extended INSTANCE = Native.load("kernel32", Kernel32Extended.class);
    WinDef.HMODULE GetModuleHandleA(String lpModuleName);
}