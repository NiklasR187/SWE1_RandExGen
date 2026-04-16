package com.randexgen.swe1_randexgen.controller;

import com.randexgen.swe1_randexgen.app.AppNavigator;
import com.randexgen.swe1_randexgen.service.AppState;
import com.randexgen.swe1_randexgen.service.XMLFileValidator;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import java.io.File;
import java.io.FileWriter;

/**
 * Controller for the start view of the application.
 *
 * This controller handles the initial user actions such as creating
 * a new XML file, selecting an existing XML file, and navigating to the next view.
 */
public class helloViewController {

    @FXML
    private Label welcomeText;

    /**
     * Handles the click on the PDF area in the start view.
     *
     * If no exam is currently loaded, a warning dialog is shown
     * to inform the user that the PDF view cannot be opened yet.
     */
    @FXML
    private void handlePdfClick() {
        // Show a warning if no exam is currently available in the application state
        if (AppState.getCurrentExam() == null) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Exam Loaded");
            alert.setHeaderText("No exam available");

            alert.setContentText("Please load or create an XML exam before accessing the PDF view.");

            alert.showAndWait();
        }
    }

    /**
     * Opens a file chooser so the user can select an XML file.
     *
     * If a file is selected, it is validated and opened.
     * Otherwise, a message is shown in the UI.
     */
    @FXML
    protected void chooseFile() {
        // Create a file chooser that only allows XML files
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select XML File");

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("XML Files", "*.xml")
        );

        // Open the dialog in the current application window
        Stage stage = (Stage) welcomeText.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        // Open the selected file or update the label if the dialog was cancelled
        if (selectedFile != null) {
            openXmlFile(selectedFile);
        } else {
            welcomeText.setText("No file selected");
        }
    }

    /**
     * Displays an error dialog for invalid XML input.
     *
     * @param message the error message to display
     */
    private void showError(String message) {
        // Show a dialog containing the validation error message
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("XML Error");
        alert.setHeaderText("Invalid XML File");
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Opens the second application view and loads the selected XML file.
     *
     * The method switches the current scene to Frame2 and passes
     * the XML file to the corresponding controller.
     *
     * @param xmlFile the XML file to load in the next view
     */
    private void openFrame2(File xmlFile) {
        AppNavigator.showEditor(xmlFile);
    }

    /**
     * Validates and opens the selected XML file.
     *
     * If the file is valid, the next view is opened.
     * Otherwise, an error dialog is displayed.
     *
     * @param xmlFile the XML file to validate and open
     */
    private void openXmlFile(File xmlFile) {
        try {
            // Validate the XML structure before opening the file
            XMLFileValidator.validate(xmlFile);

            // Update the UI and continue with the next view
            welcomeText.setText("Selected: " + xmlFile.getName());
            System.out.println(xmlFile.getAbsolutePath());

            openFrame2(xmlFile);

        } catch (Exception e) {
            // Show an error if the selected XML file is invalid
            showError("Invalid XML file:\n" + e.getMessage());
            welcomeText.setText("Invalid XML file");
        }
    }

    /**
     * Creates a new XML file using a predefined template.
     *
     * After the file has been created successfully, it is opened
     * and loaded into the next application view.
     */
    @FXML
    protected void createXmlFile() {
        // Open a save dialog for creating a new XML file
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save XML File");

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("XML Files", "*.xml")
        );

        fileChooser.setInitialFileName("exam.xml");

        Stage stage = (Stage) welcomeText.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                // Write a basic XML exam template into the selected file
                writer.write("""
                    <?xml version="1.0" encoding="UTF-8"?>
                    <exam id="exam-001">
                        <title>Neue Klausur</title>
                        <totalScore>0</totalScore>
                        <chapters>
                        </chapters>
                    </exam>
                    """);
            } catch (Exception e) {
                // Display an error message if file creation fails
                e.printStackTrace();
                welcomeText.setText("Error creating file");
                return;
            }

            // Open the newly created XML file directly afterwards
            openXmlFile(file);

        } else {
            welcomeText.setText("Save cancelled");
        }
    }
}