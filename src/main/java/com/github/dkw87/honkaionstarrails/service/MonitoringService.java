package com.github.dkw87.honkaionstarrails.service;

import com.github.dkw87.honkaionstarrails.shared.enumeration.MonitorState;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class MonitoringService {

    private static final String HSR_WINDOW_TITLE = "Honkai: Star Rail";
    private static final int RECHECK_PERIOD = 200;

    private ScheduledService<MonitorState> monitoringService;
    private Label monitorLabel;

    public MonitoringService(Label statusLabel) {
        monitorLabel = statusLabel;
        startMonitoring();
    }

    public void start() {
        if (monitoringService != null) {
            monitoringService.restart();
        }
    }

    public void stop() {
        if (monitoringService != null) {
            monitoringService.cancel();
        }
    }

    private void startMonitoring() {
        monitoringService = new ScheduledService<MonitorState>() {

            @Override
            protected Task<MonitorState> createTask() {
                return new Task<MonitorState>() {
                    @Override
                    protected MonitorState call() {
                        WinDef.HWND hwnd = User32.INSTANCE.FindWindow(null, HSR_WINDOW_TITLE);
                        return hwnd != null ? MonitorState.FOUND : MonitorState.NOT_FOUND;
                    }
                };
            }
        };

        monitoringService.setPeriod(Duration.millis(RECHECK_PERIOD));

        monitoringService.setOnSucceeded(event -> {
            MonitorState monitorStatus = monitoringService.getValue();
            updateStatus(monitorStatus);
        });
    }

    private void updateStatus(MonitorState state) {
        System.out.println(state.getLabelText());
        switch (state) {
            case FOUND:
                monitorLabel.setText(state.name());
                monitorLabel.setStyle("-fx-text-fill: green;");
            case NOT_FOUND:
                monitorLabel.setText(state.name());
                monitorLabel.setStyle("-fx-text-fill: red;");
            default:
                monitorLabel.setText("Gamestate unknown");
        }
    }


}
