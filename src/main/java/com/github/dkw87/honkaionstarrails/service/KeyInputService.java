package com.github.dkw87.honkaionstarrails.service;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

public class KeyInputService {

    private static final Logger logger = Logger.getLogger(KeyInputService.class.getName());

    public void initialize() {
        try {
            // Disable logging
            Logger.getLogger(GlobalScreen.class.getPackage().getName()).setLevel(Level.OFF);

            // Register native hook
            GlobalScreen.registerNativeHook();
            logger.info("JNativeHook initialized successfully");
        } catch (NativeHookException e) {
            logger.severe("Error initializing JNativeHook: " + e.getMessage());
        }
    }

    public void unregister() {
        try {
            GlobalScreen.unregisterNativeHook();
            logger.info("JNativeHook unregistered successfully");
        } catch (NativeHookException e) {
            logger.severe("Error unregistering JNativeHook: " + e.getMessage());
        }
    }

    public void pressKey(int keyCode) {
        try {
            logger.info("Pressing key");
            NativeKeyEvent pressEvent = new NativeKeyEvent(
                    NativeKeyEvent.NATIVE_KEY_PRESSED,
                    0,  // modifiers
                    keyCode,
                    keyCode,
                    NativeKeyEvent.CHAR_UNDEFINED
            );
            GlobalScreen.postNativeEvent(pressEvent);

            pressDelay();

            NativeKeyEvent releaseEvent = new NativeKeyEvent(
                    NativeKeyEvent.NATIVE_KEY_RELEASED,
                    0,  // modifiers
                    keyCode,
                    keyCode,
                    NativeKeyEvent.CHAR_UNDEFINED
            );
            GlobalScreen.postNativeEvent(releaseEvent);

        } catch (Exception e) {
            logger.severe("Error posting key event: " + e.getMessage());
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
