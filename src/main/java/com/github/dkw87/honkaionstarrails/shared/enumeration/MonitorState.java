package com.github.dkw87.honkaionstarrails.shared.enumeration;

public enum MonitorState {
    NOT_FOUND("Game is not running or was not found"),
    FOUND("Game is running"),
    IN_FOCUS("Game is in focus"),
    IDLE("Idle. No combat sequence found"),
    EXECUTING("Executing combat rules");

    private final String labelText;

    MonitorState(String labelText) {
        this.labelText = labelText;
    }

    public String getLabelText() {
        return labelText;
    }
}
