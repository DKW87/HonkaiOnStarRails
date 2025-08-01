package com.github.dkw87.honkaionstarrails;

import com.github.dkw87.honkaionstarrails.controller.MonitoringController;
import com.github.dkw87.honkaionstarrails.service.CleanupService;
import com.github.dkw87.honkaionstarrails.service.GameStateService;
import com.github.dkw87.honkaionstarrails.service.KeyInputService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;

public class HonkaiOnStarRailsLauncher extends Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(HonkaiOnStarRailsLauncher.class);

    private FXMLLoader fxmlLoader;
    private CleanupService cleanupService;

    @Override
    public void start(Stage stage) throws IOException {
        LOGGER.info("Starting Honkai: On Star Rails...");
        initializeStage(stage);
        registerCleanupService();
        stage.show();
        LOGGER.info("Honkai: On Star Rails Started");
    }

    @Override
    public void stop() {
        LOGGER.info("Shutting down Honkai: On Star Rails...");
        cleanupService.unregister();
    }

    public static void main(String[] args) {
        launch();
    }

    private void initializeStage(Stage stage) throws IOException {
        this.fxmlLoader = new FXMLLoader(HonkaiOnStarRailsLauncher.class.getResource("view/MonitorView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 300, 25);
        stage.setTitle("Honkai: On Star Rails");
        stage.setScene(scene);
        stage.setAlwaysOnTop(true);
        stage.getIcons().add(new Image(
                Objects.requireNonNull(HonkaiOnStarRailsLauncher.class.getResourceAsStream("image/hosr_logo.png"))
        ));
        spawnWindowBottomRight(stage, scene);
    }

    private void spawnWindowBottomRight(Stage stage, Scene scene) {
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double x = screenBounds.getMaxX() - scene.getWidth();
        double y = screenBounds.getMaxY() - scene.getHeight();
        stage.setX(x);
        stage.setY(y);
    }

    private void registerCleanupService() {
        MonitoringController controller = fxmlLoader.getController();
        GameStateService gameStateService = controller.getGameStateService();
        KeyInputService keyInputService = KeyInputService.getInstance();
        this.cleanupService = new CleanupService(gameStateService, keyInputService);
    }

}