package com.github.dkw87.honkaionstarrails.service;

import com.github.dkw87.honkaionstarrails.shared.enumeration.MonitorState;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.ptr.IntByReference;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class MonitoringService {

    private static final String HSR_WINDOW_TITLE = "Honkai: Star Rail";
    private static final int RECHECK_PERIOD = 500;

    private final Label monitorLabel;

    private ScheduledService<MonitorState> monitoringService;

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
        monitoringService = new ScheduledService<>() {

            @Override
            protected Task<MonitorState> createTask() {
                return new Task<>() {
                    @Override
                    protected MonitorState call() {
                        WinDef.HWND gameWindow = User32.INSTANCE.FindWindow(null, HSR_WINDOW_TITLE);

                        if (gameWindow == null) {
                            return MonitorState.NOT_FOUND;
                        }

                        WinDef.HWND focusWindow = User32.INSTANCE.GetForegroundWindow();

                        IntByReference gamePid = new IntByReference();
                        IntByReference focusPid = new IntByReference();

                        User32.INSTANCE.GetWindowThreadProcessId(gameWindow, gamePid);
                        User32.INSTANCE.GetWindowThreadProcessId(focusWindow, focusPid);

                        if (focusPid.getValue() == gamePid.getValue()) {
                            return MonitorState.IDLE;
                        } else {
                            return MonitorState.FOUND;
                        }
                    }
                };
            }
        };

        monitoringService.setPeriod(Duration.millis(RECHECK_PERIOD));

        monitoringService.setOnSucceeded(event -> {
            MonitorState monitorStatus = monitoringService.getValue();
            updateLabel(monitorStatus);
        });
    }

    private void updateLabel(MonitorState state) {
        monitorLabel.setText(state.getLabelText());
        monitorLabel.setStyle(state.getLabelStyle());
    }


}
