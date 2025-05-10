package com.github.dkw87.honkaionstarrails.service.win32interface;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.Psapi;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;

public interface PsapiExtended extends Psapi {
    PsapiExtended INSTANCE = Native.load("psapi", PsapiExtended.class);
    int GetModuleBaseName(WinNT.HANDLE hProcess, WinDef.HMODULE hModule, char[] lpBaseName, int nSize);
}