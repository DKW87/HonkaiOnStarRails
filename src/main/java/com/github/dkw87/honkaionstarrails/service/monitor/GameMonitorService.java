package com.github.dkw87.honkaionstarrails.service.monitor;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.ptr.IntByReference;

public class GameMonitorService {

    public static volatile WinDef.HWND gameWindow = null;

    private static final String HSR_WINDOW_TITLE = "Honkai: Star Rail";
    private static final String HSR_WINDOW_CLASS = "UnityWndClass";

    public boolean isGameRunning() {
        gameWindow = User32.INSTANCE.FindWindow(HSR_WINDOW_CLASS, HSR_WINDOW_TITLE);
        return gameWindow != null;
    }

    public boolean isGameFocused() {
        WinDef.HWND focusWindow = User32.INSTANCE.GetForegroundWindow();

        IntByReference gamePid = new IntByReference();
        IntByReference focusPid = new IntByReference();

        User32.INSTANCE.GetWindowThreadProcessId(gameWindow, gamePid);
        User32.INSTANCE.GetWindowThreadProcessId(focusWindow, focusPid);

        return focusPid.getValue() == gamePid.getValue();
    }

}
