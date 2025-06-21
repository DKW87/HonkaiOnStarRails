package com.github.dkw87.honkaionstarrails.service;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.ptr.IntByReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameMonitorService {

    public static volatile WinDef.HWND gameWindow = null;

    public static volatile boolean gameIsRunning;
    public static volatile boolean gameIsFocused;

    private static final String HSR_WINDOW_TITLE = "Honkai: Star Rail";
    private static final String HSR_WINDOW_CLASS = "UnityWndClass";
    private static final Logger LOGGER = LoggerFactory.getLogger(GameMonitorService.class);

    public boolean isGameRunning() {
        gameWindow = User32.INSTANCE.FindWindow(HSR_WINDOW_CLASS, HSR_WINDOW_TITLE);
        gameIsRunning = (gameWindow != null);
        return gameIsRunning;
    }

    public boolean isGameFocused() {
        WinDef.HWND focusWindow = User32.INSTANCE.GetForegroundWindow();

        IntByReference gamePid = new IntByReference();
        IntByReference focusPid = new IntByReference();

        User32.INSTANCE.GetWindowThreadProcessId(gameWindow, gamePid);
        User32.INSTANCE.GetWindowThreadProcessId(focusWindow, focusPid);

        gameIsFocused = (focusPid.getValue() == gamePid.getValue());

        return gameIsFocused;
    }

}
