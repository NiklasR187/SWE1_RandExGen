package com.randexgen.swe1_randexgen.app;

import com.randexgen.swe1_randexgen.controller.PdfviewerController;
import com.randexgen.swe1_randexgen.controller.frame2Controller;
import com.randexgen.swe1_randexgen.service.AppState;
import com.randexgen.swe1_randexgen.service.ExamGenerator;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

/**
 * MainShellController is the central controller of the application.
 *
 * It manages a single stable main scene (the shell) and dynamically
 * swaps the content displayed inside the contentHost.
 * This avoids reloading entire scenes and improves performance
 * while preserving application state.
 *
 * The controller enables navigation between:
 * - Hello View (start screen)
 * - Frame2 View (XML editor)
 * - PDF View (preview/export)
 */
public class MainShellController {

    private static final String DARK_MODE_CLASS = "dark-mode";

    /** Stores whether dark mode is currently enabled */
    private boolean darkModeEnabled = false;

    /** Root container of the shell */
    @FXML
    private BorderPane shellRoot;

    /** Container where the current view is displayed */
    @FXML
    private StackPane contentHost;

    /** Tab representing the XML view */
    @FXML
    private Pane xmlTab;

    /** Tab representing the PDF view */
    @FXML
    private Pane pdfTab;

    /** Button for toggling light/dark mode */
    @FXML
    private Button themeToggleButton;

    /**
     * Initializes the controller.
     * This method is automatically called by JavaFX after loading the FXML.
     * It registers the shell and loads the start view.
     */
    @FXML
    private void initialize() {
        AppNavigator.registerShell(this);
        applyDarkModeState();
        showHelloView();
    }

    /**
     * Toggles dark mode for the entire shell and the currently displayed content.
     */
    @FXML
    private void toggleDarkMode() {
        darkModeEnabled = !darkModeEnabled;
        applyDarkModeState();
    }

    /**
     * Applies the current dark mode state to the shell and the loaded content.
     */
    private void applyDarkModeState() {
        applyDarkModeToNode(shellRoot);

        if (themeToggleButton != null) {
            themeToggleButton.setText(darkModeEnabled ? "☀" : "🌙");
        }

        if (contentHost != null && !contentHost.getChildren().isEmpty()) {
            Node currentContent = contentHost.getChildren().get(0);
            applyDarkModeToNode(currentContent);
        }
    }

    /**
     * Applies or removes the dark-mode style class on a node.
     *
     * @param node the node to update
     */
    private void applyDarkModeToNode(Node node) {
        if (node == null) {
            return;
        }

        if (darkModeEnabled) {
            if (!node.getStyleClass().contains(DARK_MODE_CLASS)) {
                node.getStyleClass().add(DARK_MODE_CLASS);
            }
        } else {
            node.getStyleClass().remove(DARK_MODE_CLASS);
        }
    }

    /**
     * Loads the Hello View (start screen).
     */
    public void showHelloView() {
        loadContent(
                "/com/randexgen/swe1_randexgen/hello-view.fxml",
                "XML",
                controller -> {
                    // No additional setup required
                }
        );
    }

    /**
     * Opens the Frame2 View and loads an XML file.
     *
     * @param xmlFile the XML file to load
     */
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

    /**
     * Opens the Frame2 View with an existing exam and XML file.
     *
     * @param exam the current exam object
     * @param xmlFile the associated XML file
     */
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

    /**
     * Loads the PDF view for previewing or exporting the exam.
     */
    public void showPdfView() {
        loadContent(
                "/com/randexgen/swe1_randexgen/pdfviewer.fxml",
                "PDF",
                controller -> {
                    if (controller instanceof PdfviewerController) {
                        // No additional setup required at the moment
                    }
                }
        );
    }

    /**
     * Handles clicks on the XML tab.
     *
     * If no exam is loaded, the Hello View is shown.
     * If an exam was already generated, a confirmation dialog appears
     * to warn the user about losing unsaved progress.
     */
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

    /**
     * Handles clicks on the PDF tab.
     *
     * If no exam is loaded, a warning dialog is shown.
     * Otherwise, the PDF view is opened.
     */
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

    /**
     * Loads a new FXML view into the contentHost.
     *
     * @param fxmlPath path to the FXML file
     * @param activeTab which tab should be styled as active ("XML" or "PDF")
     * @param controllerConsumer function to configure the loaded controller
     */
    private void loadContent(String fxmlPath, String activeTab, ControllerConsumer controllerConsumer) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Remove top area of inner layouts because the shell already provides tabs
            if (root instanceof BorderPane borderPane) {
                borderPane.setTop(null);
            }

            // Apply dark mode to the newly loaded content if needed
            applyDarkModeToNode(root);

            contentHost.getChildren().setAll(root);
            updateTabStyles(activeTab);

            Object controller = loader.getController();
            controllerConsumer.accept(controller);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the visual style of the tabs depending on the active view.
     *
     * @param activeTab "XML" or "PDF"
     */
    private void updateTabStyles(String activeTab) {
        xmlTab.getStyleClass().removeAll("shell-tab-active", "shell-tab-inactive");
        pdfTab.getStyleClass().removeAll("shell-tab-active", "shell-tab-inactive");

        if ("PDF".equals(activeTab)) {
            xmlTab.getStyleClass().add("shell-tab-inactive");
            pdfTab.getStyleClass().add("shell-tab-active");
        } else {
            xmlTab.getStyleClass().add("shell-tab-active");
            pdfTab.getStyleClass().add("shell-tab-inactive");
        }
    }

    /**
     * Functional interface used to configure controllers after loading an FXML file.
     */
    @FunctionalInterface
    private interface ControllerConsumer {
        void accept(Object controller);
    }
}