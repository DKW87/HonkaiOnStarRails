# Honkai On Star Rails (HOSR)

## ⚠️ WARNING ⚠️

**USE THIS SOFTWARE AT YOUR OWN RISK**. This tool reads game memory and simulates key inputs, which may violate the Terms of Service of Honkai: Star Rail. Using this software could potentially result in your account being banned. The developer takes NO RESPONSIBILITY for any consequences resulting from the use of this software.

![HOSR Logo](src/main/resources/com/github/dkw87/honkaionstarrails/image/hosr_logo.png)

## Overview

Honkai On Star Rails (HOSR) is a lightweight automation assistant for Honkai: Star Rail that monitors and detects combat states through memory reading. The application runs in the background with a minimal UI that displays the current state of the game and automation.

### Features

- Detects when the game is running and in focus
- Monitors combat state through memory reading
- Identifies when combat begins, pauses, or when the combat view is open
- Runs with minimal system impact
- Automatically adjusts polling rates based on game state for efficiency
- Small overlay UI that stays in the bottom right corner

This tool is built using Java and JavaFX with JNA for memory access and JNativeHook for keyboard input simulation. The project follows a service-oriented architecture with clean separation between game monitoring, memory reading, and input services.

Currently, the combat automation execution service is under development, but the foundation for detecting all necessary game states is implemented.