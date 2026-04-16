package com.randexgen.swe1_randexgen.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Main entry point of the JavaFX application.
 */
public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                HelloApplication.class.getResource("/com/randexgen/swe1_randexgen/MainShell.fxml")
        );

        Scene scene = new Scene(loader.load());

        stage.setTitle("RandExGen");
        stage.getIcons().add(new Image(
                HelloApplication.class.getResourceAsStream("/images/applogo.png")
        ));

        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}