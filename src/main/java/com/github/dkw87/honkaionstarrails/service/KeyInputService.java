package com.github.dkw87.honkaionstarrails.service;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

public class KeyInputService {

    public void initialize() {
        try {
            // disable JNativeHook lib logging
            Logger.getLogger(GlobalScreen.class.getPackage().getName()).setLevel(Level.OFF);

            GlobalScreen.registerNativeHook();
            System.out.println("JNativeHook initialized successfully");
        } catch (NativeHookException e) {
            System.err.println("Error initializing JNativeHook: " + e.getMessage());
        }
    }

    public void unregister() {
        try {
            GlobalScreen.unregisterNativeHook();
        } catch (NativeHookException e) {
            System.err.println("Error unregistering JNativeHook: " + e.getMessage());
        }
    }

    public void pressKey(int keyCode) {
        try {
            NativeKeyEvent pressEvent = new NativeKeyEvent(
                    NativeKeyEvent.NATIVE_KEY_PRESSED,
                    0,
                    keyCode,
                    keyCode,
                    NativeKeyEvent.CHAR_UNDEFINED
            );
            GlobalScreen.postNativeEvent(pressEvent);

            pressDelay();

            NativeKeyEvent releaseEvent = new NativeKeyEvent(
                    NativeKeyEvent.NATIVE_KEY_RELEASED,
                    0,
                    keyCode,
                    keyCode,
                    NativeKeyEvent.CHAR_UNDEFINED
            );
            GlobalScreen.postNativeEvent(releaseEvent);

        } catch (Exception e) {
            System.err.println("Error posting key event: " + e.getMessage());
        }
    }

    private void pressDelay() {
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}