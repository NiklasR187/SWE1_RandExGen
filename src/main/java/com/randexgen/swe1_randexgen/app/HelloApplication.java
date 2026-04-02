package com.randexgen.swe1_randexgen.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Main entry point of the JavaFX application.
 *
 * This class starts the application, loads the initial FXML view,
 * and configures the primary stage.
 */
public class HelloApplication extends Application {

    /**
     * Starts the JavaFX application and shows the main window.
     *
     * The method loads the start view, sets the application title and icon,
     * and displays the primary stage in maximized mode.
     *
     * @param stage the primary stage of the JavaFX application
     * @throws Exception if the FXML file or resources cannot be loaded
     */
    @Override
    public void start(Stage stage) throws Exception {

        // Load the initial FXML file of the application
        FXMLLoader loader = new FXMLLoader(
                HelloApplication.class.getResource("/com/randexgen/swe1_randexgen/hello-view.fxml")
        );

        Scene scene = new Scene(loader.load());

        // Configure the main application window
        stage.setTitle("RandExGen");
        stage.setMaximized(true);

        // Load and set the application icon
        stage.getIcons().add(new Image(
                HelloApplication.class.getResourceAsStream("/images/applogo.png")
        ));

        // Show the loaded scene in the primary stage
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Launches the JavaFX application.
     *
     * @param args command-line arguments passed to the application
     */
    public static void main(String[] args) {
        launch();
    }
}