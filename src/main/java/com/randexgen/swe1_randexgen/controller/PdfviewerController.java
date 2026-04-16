package com.randexgen.swe1_randexgen.controller;

import com.randexgen.swe1_randexgen.app.AppNavigator;
import com.randexgen.swe1_randexgen.datamodel.Exam;
import com.randexgen.swe1_randexgen.datamodel.ExamType;
import com.randexgen.swe1_randexgen.service.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import java.io.File;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.Optional;
import javafx.scene.layout.Region;
import java.util.List;
import javafx.scene.layout.VBox;


/**
 * Controller for the PDF preview view of the application.
 *
 * This controller handles exam generation, preview rendering, exporting PDFs,
 * and switching back to the XML editor view.
 */
public class PdfviewerController {

    @FXML
    private Pane xmlPane;

    private Exam currentExam;

    @FXML
    private Button saveAsFolderButton;

    private List<GeneratedTask> currentGeneratedExam;
    private ExamType currentGeneratedExamType;

    @FXML
    private Text examStatusText;

    @FXML
    private VBox previewPagesBox;

    @FXML
    private ComboBox<ExamType> examTypeComboBox;

    /**
     * Initializes the PDF view after the FXML file has been loaded.
     *
     * The method fills the exam type combo box with available values
     * and shows an empty preview page as the initial state.
     */
    @FXML
    private void initialize() {
        // Populate the combo box with supported exam types
        examTypeComboBox.getItems().addAll(ExamType.REGULAR, ExamType.PRACTICE);
        examTypeComboBox.setValue(ExamType.REGULAR);

        // Show the default preview before an exam has been generated
        showGeneratedExamInPreview(null);

        Platform.runLater(() -> {
            Stage stage = (Stage) examStatusText.getScene().getWindow();

            stage.setOnCloseRequest(event -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirm");
                alert.setHeaderText("Close application?");
                alert.setContentText("Your unsaved progress will be lost.");

                Optional<ButtonType> result = alert.showAndWait();

                if (result.isEmpty() || result.get() != ButtonType.OK) {
                    event.consume();
                }
            });
        });
    }

    /**
     * Switches back to the XML editor view after user confirmation.
     *
     * The currently generated exam preview is discarded and the existing exam
     * data is passed back to the XML view controller.
     */
    @FXML
    private void switchToXML() {
        AppNavigator.showEditor(AppState.getCurrentExam(), AppState.getCurrentXmlFile());
    }

    /**
     * Generates an exam based on the selected exam type.
     *
     * The method validates the current exam, calls the exam generator,
     * and updates the preview area with the generated result.
     */
    @FXML
    private void handleGenerateExam() {
        ExamType selectedExamType = examTypeComboBox.getValue();

        // Ensure that an exam type has been selected by the user
        if (selectedExamType == null) {
            examStatusText.setText("⚠ Please select an exam type.");
            return;
        }

        Exam currentExam = AppState.getCurrentExam();

        // Stop if no exam is currently loaded
        if (currentExam == null) {
            examStatusText.setText("⚠ No exam loaded.");
            return;
        }

        // Validate the current exam before attempting generation
        if (!DataValidator.isExamValid(currentExam, selectedExamType)) {
            examStatusText.setText(DataValidator.getExamWarningText(currentExam));
            return;
        }

        // Generate the exam tasks for the selected type
        List<GeneratedTask> generatedTasks = ExamGenerator.generateExam(currentExam, selectedExamType);

        // Show an error if generation did not produce any tasks
        if (generatedTasks.isEmpty()) {
            examStatusText.setText("⚠ Exam could not be generated.");
            return;
        }

        // Store the current generated exam and update the preview
        this.currentGeneratedExam = generatedTasks;
        this.currentGeneratedExamType = selectedExamType;

        examStatusText.setText("✅ Exam generated successfully.");
        showGeneratedExamInPreview(generatedTasks);
    }

    /**
     * Exports the generated exam and its solution as PDF files into a selected folder.
     *
     * The method creates two files, one for the exam and one for the solution,
     * and uses the PDF exporter service for both outputs.
     */
    @FXML
    private void handleSaveAsFolder() {
        // Ensure that an exam has already been generated before exporting
        if (currentGeneratedExam == null || currentGeneratedExam.isEmpty()) {
            examStatusText.setText("⚠ Please generate an exam first.");
            return;
        }

        Exam currentExam = AppState.getCurrentExam();
        if (currentExam == null) {
            examStatusText.setText("⚠ No exam loaded.");
            return;
        }

        // Open a directory chooser so the user can select the export target folder
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select export folder");

        Stage stage = (Stage) saveAsFolderButton.getScene().getWindow();
        File selectedDirectory = directoryChooser.showDialog(stage);

        if (selectedDirectory == null) {
            return;
        }

        // Build safe file names based on the exam title
        String examTitle = currentExam.getTitle() != null ? currentExam.getTitle() : "exam";
        String safeFileName = examTitle
                .replaceAll("[^a-zA-Z0-9\\-_ ]", "")
                .replaceAll("\\s+", "_");

        File examFile = new File(selectedDirectory, safeFileName + ".pdf");
        File solutionFile = new File(selectedDirectory, safeFileName + "_LSG.pdf");

        try {
            // Export the generated exam PDF without solutions
            PDFExporter.exportExam(
                    currentExam,
                    currentGeneratedExamType,
                    currentGeneratedExam,
                    examFile,
                    ExportMode.EXAM
            );

            // Export the corresponding solution PDF
            PDFExporter.exportExam(
                    currentExam,
                    currentGeneratedExamType,
                    currentGeneratedExam,
                    solutionFile,
                    ExportMode.SOLUTION
            );

            examStatusText.setText("✅ Exam and solution exported successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            examStatusText.setText("⚠ Error while exporting files.");
        }
    }

    /**
     * Creates a styled preview box for displaying answer text.
     *
     * The height of the text area is adjusted dynamically so that the preview
     * resembles the layout of the exported PDF as closely as possible.
     *
     * @param answerText the answer text to display inside the preview box
     * @return a configured read-only text area
     */
    private TextArea createAnswerPreviewBox(String answerText) {
        String safeAnswerText = answerText == null ? "" : answerText.trim();

        // Create a non-editable text area used as a visual answer box
        TextArea answerBox = new TextArea();
        answerBox.setEditable(false);
        answerBox.setWrapText(true);
        answerBox.setText(safeAnswerText);

        // Apply a fixed width so all preview boxes are aligned consistently
        answerBox.setPrefWidth(680);
        answerBox.setMaxWidth(680);
        answerBox.setMinWidth(680);

        // Apply styling to make the preview box resemble a PDF answer field
        answerBox.setStyle(
                "-fx-control-inner-background: white;" +
                        "-fx-background-color: white;" +
                        "-fx-border-color: #444444;" +
                        "-fx-border-width: 1;" +
                        "-fx-font-size: 13px;" +
                        "-fx-padding: 8;"
        );

        // Calculate and apply the required box height based on the answer text
        double minHeight = 90;
        double calculatedHeight = calculateAnswerBoxHeight(safeAnswerText);
        double finalHeight = Math.max(minHeight, calculatedHeight);

        answerBox.setPrefHeight(finalHeight);
        answerBox.setMinHeight(finalHeight);
        answerBox.setMaxHeight(finalHeight);

        return answerBox;
    }

    /**
     * Estimates the required height for an answer preview box.
     *
     * The calculation is based on a simple character-per-line approximation
     * to keep the preview readable and visually stable.
     *
     * @param text the answer text to evaluate
     * @return the estimated height of the preview box
     */
    private double calculateAnswerBoxHeight(String text) {
        // Return a default minimum height for empty answer boxes
        if (text == null || text.isBlank()) {
            return 90;
        }

        // Estimate the number of lines and convert it into a visual box height
        int charsPerLine = 85;
        int lineCount = (int) Math.ceil((double) text.length() / charsPerLine);

        return 30 + (lineCount * 18);
    }

    /**
     * Displays the generated exam inside the preview area.
     *
     * If no generated tasks are available, a placeholder page is shown.
     * Otherwise, the method renders a PDF-like preview of all chapters and tasks.
     *
     * @param generatedTasks the generated tasks to display in the preview
     */
    private void showGeneratedExamInPreview(List<GeneratedTask> generatedTasks) {
        // Clear all previous preview content before rendering the new state
        previewPagesBox.getChildren().clear();

        // Show a placeholder preview if no exam has been generated yet
        if (generatedTasks == null || generatedTasks.isEmpty()) {
            VBox emptyPage = createPdfLikePage();

            Text title = new Text("No PDF generated yet");
            title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

            Text subtitle = new Text("Select an exam type and click Generate Exam.");
            subtitle.setStyle("-fx-font-size: 14px; -fx-fill: #666666;");

            emptyPage.getChildren().addAll(title, subtitle);
            previewPagesBox.getChildren().add(emptyPage);
            return;
        }

        // Create the main preview page that visually imitates an A4 PDF layout
        VBox page = createPdfLikePage();

        this.currentExam = AppState.getCurrentExam();
        Text documentTitle = new Text(
                currentExam != null ? currentExam.getTitle() : "Generated Exam"
        );

        documentTitle.setStyle(
                "-fx-font-size: 25px;" +
                        "-fx-font-weight: bold;"
        );

        Text examTypeText = new Text("Exam Type: " + currentGeneratedExamType);
        examTypeText.setStyle("-fx-font-size: 14px; -fx-fill: #444444;");

        Region spacer = new Region();
        spacer.setMinHeight(10);

        page.getChildren().addAll(documentTitle, examTypeText, spacer);

        String lastChapter = null;
        int taskNumber = 1;

        // Render all generated tasks grouped by chapter
        for (GeneratedTask task : generatedTasks) {
            String chapterTitle = task.getChapter().getTitle();

            // Insert a new chapter heading whenever the chapter changes
            if (lastChapter == null || !lastChapter.equals(chapterTitle)) {
                Text chapterText = new Text(chapterTitle);
                chapterText.setStyle(
                        "-fx-font-size: 18px;" +
                                "-fx-font-weight: bold;"
                );

                Region chapterSpacer = new Region();
                chapterSpacer.setMinHeight(12);

                page.getChildren().addAll(chapterSpacer, chapterText);
                lastChapter = chapterTitle;
            }

            // Build the task header and task description for the current generated task
            Text taskHeader = new Text(
                    taskNumber + ". " +
                            task.getSubtask().getTitle() +
                            " (" + task.getSubtask().getScore() + " P)"
            );
            taskHeader.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");

            Text taskText = new Text(task.getVariant().getTaskText());
            taskText.setWrappingWidth(680);
            taskText.setStyle("-fx-font-size: 14px; -fx-fill: #111111;");

            // Create the preview box for the corresponding answer text
            TextArea answerBox = createAnswerPreviewBox(task.getVariant().getAnswerText());

            Region taskSpacer = new Region();
            taskSpacer.setMinHeight(8);

            Region answerSpacer = new Region();
            answerSpacer.setMinHeight(8);

            page.getChildren().addAll(
                    taskSpacer,
                    taskHeader,
                    taskText,
                    answerSpacer,
                    answerBox
            );

            taskNumber++;
        }

        // Add the finished preview page to the preview container
        previewPagesBox.getChildren().add(page);
    }

    /**
     * Creates a visual container that imitates a PDF page.
     *
     * The returned VBox is styled to resemble an A4 page on screen
     * and is used as the main container for the generated exam preview.
     *
     * @return a styled VBox representing a PDF-like page
     */
    private VBox createPdfLikePage() {
        // Create a page container with an A4-like size and visual styling
        VBox page = new VBox();
        page.setPrefWidth(794);   // ungefähr A4-Proportion auf Bildschirm
        page.setMaxWidth(794);
        page.setMinWidth(794);
        page.setSpacing(12);
        page.setStyle(
                "-fx-background-color: white;" +
                        "-fx-padding: 50 55 50 55;" +
                        "-fx-border-color: #cfcfcf;" +
                        "-fx-border-width: 1;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.18), 18, 0.15, 0, 3);"
        );
        return page;
    }
}