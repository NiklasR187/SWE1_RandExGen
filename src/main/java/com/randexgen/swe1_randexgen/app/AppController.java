package com.randexgen.swe1_randexgen.app;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Simple controller for the example application view.
 *
 * This controller handles the button interaction and updates
 * the displayed welcome text in the UI.
 */
public class AppController {

    @FXML
    private Label welcomeText;

    /**
     * Handles the click on the hello button.
     *
     * The method updates the label text with a welcome message.
     */
    @FXML
    protected void onHelloButtonClick() {
        // Update the label with a predefined welcome message
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}