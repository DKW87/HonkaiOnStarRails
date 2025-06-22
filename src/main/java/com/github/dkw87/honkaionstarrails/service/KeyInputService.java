package com.github.dkw87.honkaionstarrails.service;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeyInputService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KeyInputService.class);

    private KeyInputService() {}

    protected void initialize() {
        LOGGER.info("Initializing KeyInputService...");
        try {
            // disable JNativeHook lib logging
            java.util.logging.Logger.getLogger(GlobalScreen.class.getPackage().getName())
                    .setLevel(java.util.logging.Level.OFF);

            GlobalScreen.registerNativeHook();
        } catch (NativeHookException e) {
            LOGGER.error("KeyInputService failed to initialize", e);
        }
    }

    protected void unregister() {
        LOGGER.info("Unregistering KeyInputService...");
        try {
            GlobalScreen.unregisterNativeHook();
        } catch (NativeHookException e) {
            LOGGER.error("KeyInputService failed to unregister", e);
        }
    }

    protected void pressKey(int keyCode) {
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
            LOGGER.error("KeyInputService failed to post key event: {}", keyCode, e);
        }
    }

    protected void pressDelay() {
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static KeyInputService getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final KeyInputService INSTANCE = new KeyInputService();
    }

}