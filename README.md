# Honkai On Star Rails 

## ⚠️ PLEASE READ THIS DISCLAIMER ⚠️
**USE THIS APPLICATION AT YOUR OWN RISK**. This application automates gameplay by reading game memory and simulating keyboard inputs, which may violate the Terms of Service of Honkai: Star Rail. Using this application could potentially result in your account being suspended or permanently banned. Always be cautious using this application with newer versions of the game. I take NO RESPONSIBILITY for any consequences resulting from the use of this application.      

<p align="center">
  <img src="src/main/resources/com/github/dkw87/honkaionstarrails/image/hosr_logo.png" alt="HOSR Logo">
</p>

## Overview

"Honkai On Star Rails" is an automation assistant for Honkai: Star Rail that aims to provide a better automated combat experience than the game provides natively. The goal is to have custom rules for each character/team for granular decision-making and no longer use abilities that don't make sense given the situation. This application is built using Java and JavaFX with JNA for memory access and JNativeHook for keyboard input simulation. Because of this, **this application requires Administrator privileges to run properly**. 


### This application is currently able to

- detect when the game is running and in focus;
- monitor combat state through reading the game's memory;
- identify when combat begins, pauses, or when the real-time combat viewer is open;
- automatically adjust polling rates based on game state for efficiency;
- able to send keypresses.

### Features to be implemented for release version 1.0

- modular combat rules service;
- combat execution service;
- data collection service;
- full GUI for the end-user;
- save user created data and configuration;
- GUI switching automatically between monitor and editor;
- create team(s) in GUI;
- create combat rules on character or team level in GUI;
- able to set different combat rules for different teams in GUI;
- possible beta release ahead of release version 1.0.

### Future features which may or may not happen

- GUI translations;
- damage meter;
- export logs;
- export user profile(s) to cloud;
- TBD.

### Application requires
- **administrator privileges** in order for it to work;
- Windows OS;
- the game [Honkai: Star Rail](https://hsr.hoyoverse.com/en-us/).

### Compiling requires
- [Java LTS 21](https://adoptium.net/temurin/releases/);
- [JavaFX LTS 21](https://gluonhq.com/products/javafx/openjfx-21-release-notes/);
- Create a folder in root "tesseract-data" and put [tessdata_fast](https://github.com/tesseract-ocr/tessdata_fast) here;
- run your IDE with **administrator privileges**.
