package com.randexgen.swe1_randexgen.app;

import com.randexgen.swe1_randexgen.controller.PdfviewerController;
import com.randexgen.swe1_randexgen.controller.frame2Controller;
import com.randexgen.swe1_randexgen.service.AppState;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import com.randexgen.swe1_randexgen.service.ExamGenerator;
import javafx.scene.control.ButtonType;

import java.util.Optional;

import java.io.File;
import java.io.IOException;

/**
 * Main shell controller that keeps one stable scene
 * and swaps the center content between the three views.
 */
public class MainShellController {

    @FXML
    private StackPane contentHost;

    @FXML
    private Pane xmlTab;

    @FXML
    private Pane pdfTab;

    @FXML
    private void initialize() {
        AppNavigator.registerShell(this);
        showHelloView();
    }

    public void showHelloView() {
        loadContent(
                "/com/randexgen/swe1_randexgen/hello-view.fxml",
                "XML",
                controller -> {
                    // nothing extra needed
                }
        );
    }

    public void showFrame2View(File xmlFile) {
        loadContent(
                "/com/randexgen/swe1_randexgen/Frame2.fxml",
                "XML",
                controller -> {
                    if (controller instanceof frame2Controller frameController && xmlFile != null) {
                        frameController.loadXml(xmlFile);
                    }
                }
        );
    }

    public void showFrame2View(com.randexgen.swe1_randexgen.datamodel.Exam exam, File xmlFile) {
        loadContent(
                "/com/randexgen/swe1_randexgen/Frame2.fxml",
                "XML",
                controller -> {
                    if (controller instanceof frame2Controller frameController) {
                        frameController.setExamData(exam, xmlFile);
                    }
                }
        );
    }

    public void showPdfView() {
        loadContent(
                "/com/randexgen/swe1_randexgen/pdfviewer.fxml",
                "PDF",
                controller -> {
                    if (controller instanceof PdfviewerController) {
                        // nothing extra needed because PDF view already reads from AppState
                    }
                }
        );
    }

    @FXML
    private void handleXmlTabClick() {
        if (AppState.getCurrentExam() == null) {
            showHelloView();
            return;
        }

        boolean examGenerated =
                ExamGenerator.regularExamGenerated || ExamGenerator.practiceExamGenerated;

        if (examGenerated) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm");
            alert.setHeaderText("Go back to XML view?");
            alert.setContentText("The generated exam will be discarded and any unsaved changes will be lost.");

            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                ExamGenerator.regularExamGenerated = false;
                ExamGenerator.practiceExamGenerated = false;
                showFrame2View(AppState.getCurrentExam(), AppState.getCurrentXmlFile());
            }
        } else {
            showFrame2View(AppState.getCurrentExam(), AppState.getCurrentXmlFile());
        }
    }

    @FXML
    private void handlePdfTabClick() {
        if (AppState.getCurrentExam() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Exam Loaded");
            alert.setHeaderText("No exam available");
            alert.setContentText("Please load or create an XML exam before accessing the PDF view.");
            alert.showAndWait();
            return;
        }

        showPdfView();
    }

    private void loadContent(String fxmlPath, String activeTab, ControllerConsumer controllerConsumer) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Remove the inner top area, because the shell already owns the tabs
            if (root instanceof BorderPane borderPane) {
                borderPane.setTop(null);
            }

            contentHost.getChildren().setAll(root);
            updateTabStyles(activeTab);

            Object controller = loader.getController();
            controllerConsumer.accept(controller);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateTabStyles(String activeTab) {
        String activeStyle = "-fx-background-color: #ddded4; -fx-background-radius: 8 8 0 0; -fx-cursor: hand;";
        String inactiveStyle = "-fx-background-color: #a3a69c; -fx-background-radius: 8 8 0 0; -fx-cursor: hand;";

        if ("PDF".equals(activeTab)) {
            xmlTab.setStyle(inactiveStyle);
            pdfTab.setStyle(activeStyle);
        } else {
            xmlTab.setStyle(activeStyle);
            pdfTab.setStyle(inactiveStyle);
        }
    }

    @FunctionalInterface
    private interface ControllerConsumer {
        void accept(Object controller);
    }
}