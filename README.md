# Honkai On Star Rails 

## ⚠️ DISCLAIMER ⚠️
**USE THIS SOFTWARE AT YOUR OWN RISK**. This application reads game memory and simulates key inputs, which may violate the Terms of Service of Honkai: Star Rail. Using this software could potentially result in your account being banned. I take NO RESPONSIBILITY for any consequences resulting from the use of this software.

<p align="center">
  <img src="src/main/resources/com/github/dkw87/honkaionstarrails/image/hosr_logo.png" alt="HOSR Logo">
</p>

## Overview

Honkai On Star Rails is an automation assistant for Honkai: Star Rail that aims to provide a better automated combat experience. The goal is to have custom rules for each character for granular decision-making and no longer use abilities that don't make sense for the situation. This application is built using Java and JavaFX with JNA for memory access and JNativeHook for keyboard input simulation. Because of this, this application requires Administrator privileges to run properly. 


### This application is currently able to

- detect when the game is running and in focus;
- monitor combat state through reading the game's memory;
- identify when combat begins, pauses, or when the real-time combat viewer is open;
- automatically adjust polling rates based on game state for efficiency;
- able to send keypresses.

### Features to be implemented

- the full GUI for the end-user;
- modular combat rules service;
- combat execution service;
- set teams in GUI;
- set rules on character or team level;
- tbd.

### Nice to haves

- Damage meter.

### How to help
Searching for static addresses or reliable pointer chains to read data from is very time-consuming. I'd be happy with any being provided! Please refer [here](https://github.com/DKW87/HonkaiOnStarRails/blob/main/src/main/java/com/github/dkw87/honkaionstarrails/service/constant/CombatOffsets.java) for currently available addresses.    

### Application requires
- **administrator privileges** in order for it to work;
- Windows OS;
- the game [Honkai: Star Rail](https://hsr.hoyoverse.com/en-us/).

### Compiling requires
- [Java LTS 21](https://adoptium.net/temurin/releases/)
- [JavaFX LTS 21](https://gluonhq.com/products/javafx/openjfx-21-release-notes/)