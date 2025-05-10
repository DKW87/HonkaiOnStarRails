package com.github.dkw87.honkaionstarrails.service.enumeration;

public enum GameState {
    SHUTDOWN("Shutting down...", "-fx-text-fill: black;"),
    NOT_FOUND("Game is not running or was not detected", "-fx-text-fill: red;"),
    FOUND("Game is running, but not in focus", "-fx-text-fill: orange;"),
    IDLE("HOSR is waiting for combat to begin", "-fx-text-fill: green;"),
    EXECUTING("In combat: HOSR is executing combat rules...", "-fx-text-fill: blue;");

    private final String labelText;
    private final String labelStyle;

    GameState(String labelText, String labelStyle) {
        this.labelText = labelText;
        this.labelStyle = labelStyle;
    }

    public String getLabelText() {
        return labelText;
    }

    public String getLabelStyle() {
        return labelStyle;
    }

}
