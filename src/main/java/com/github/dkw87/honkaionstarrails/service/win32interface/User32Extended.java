package com.github.dkw87.honkaionstarrails.service.win32interface;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.ptr.IntByReference;

public interface User32Extended extends User32 {
    User32Extended INSTANCE = Native.load("user32", User32Extended.class);
    int GetWindowThreadProcessId(WinDef.HWND hWnd, IntByReference lpdwProcessId);
}