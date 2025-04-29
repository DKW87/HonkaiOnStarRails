package com.github.dkw87.honkaionstarrails;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class HonkaiOnStarRailsLauncher extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        initializeStage(stage);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    private void initializeStage(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HonkaiOnStarRailsLauncher.class.getResource("view/MonitorView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 300, 25);
        stage.setTitle("Honkai: On Star Rails");
        stage.setScene(scene);
        stage.setAlwaysOnTop(true);
        spawnWindowBottomRight(stage, scene);
    }

    private void spawnWindowBottomRight(Stage stage, Scene scene) {
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double x = screenBounds.getMaxX() - scene.getWidth();
        double y = screenBounds.getMaxY() - scene.getHeight();
        stage.setX(x);
        stage.setY(y);
    }
}