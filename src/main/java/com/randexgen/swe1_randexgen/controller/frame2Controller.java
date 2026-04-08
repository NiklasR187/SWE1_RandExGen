package com.randexgen.swe1_randexgen.controller;

import com.randexgen.swe1_randexgen.datamodel.*;
import com.randexgen.swe1_randexgen.service.AppState;
import com.randexgen.swe1_randexgen.service.DataValidator;
import com.randexgen.swe1_randexgen.service.ScoreCalculator;
import com.randexgen.swe1_randexgen.service.XMLParser;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.*;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.Optional;
import javafx.stage.FileChooser;
import java.io.FileWriter;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.scene.control.Label;
import java.util.List;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.File;
import javafx.scene.control.TextArea;
import javafx.scene.control.ScrollPane;
import javafx.util.Duration;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for the main XML editor view of the application.
 *
 * This controller manages loading, editing, saving, and navigating through
 * exams, chapters, subtasks, and variants inside the JavaFX user interface.
 */
public class frame2Controller {

    private Map<String, Boolean> chapterExpandedState = new HashMap<>();
    private Map<Chapter, Label> chapterWarningLabels = new HashMap<>();

    private File currentXmlFile;

    @FXML
    private Button closeButton;

    @FXML
    private ScrollPane editorScrollPane;

    @FXML
    private HBox bottomActionBox;

    @FXML
    private Label saveStatusLabel;

    @FXML
    private Pane pdfPane;

    @FXML
    private VBox chapterOverviewBox;

    @FXML
    private VBox editorContentBox;

    private Exam currentExam;

    private Label subtaskWarningLabel;

    /**
     * Initializes the editor view after the FXML file has been loaded.
     *
     * The method resets stored expansion states and clears all major UI containers
     * so that the view starts in a clean and consistent state.
     */
    @FXML
    private void initialize() {
        // Reset the chapter expansion state for a fresh editor view
        chapterExpandedState.clear();

        // Clear all main UI areas if they are already available
        if (chapterOverviewBox != null) {
            chapterOverviewBox.getChildren().clear();
        }
        if (editorContentBox != null) {
            editorContentBox.getChildren().clear();
        }
        if (bottomActionBox != null) {
            bottomActionBox.getChildren().clear();
        }
            Platform.runLater(() -> {
                Stage stage = (Stage) closeButton.getScene().getWindow();

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
     * Saves the currently edited exam to the existing XML file.
     *
     * If no file is currently associated with the exam, the save-as dialog
     * is opened instead.
     */
    @FXML
    private void handleSave() {
        // Redirect to "Save As" if no current file exists yet
        if (currentXmlFile == null) {
            handleSaveAs();
            return;
        }

        try {
            // Save the current exam state into the existing XML file
            saveExamToFile(currentXmlFile);
            saveStatusLabel.setText("Saved: " + currentXmlFile.getName() + " ✓");
            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(event -> saveStatusLabel.setText(""));
            pause.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens a file chooser and saves the current exam to a new XML file.
     *
     * The selected file becomes the new current XML file of the application.
     */
    @FXML
    private void handleSaveAs() {
        try {
            // Open a file chooser for saving the XML file under a custom name
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save XML File As");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("XML Files", "*.xml")
            );

            // Pre-fill the file name based on the current file or a default value
            if (currentXmlFile != null) {
                fileChooser.setInitialFileName(currentXmlFile.getName());
            } else {
                fileChooser.setInitialFileName("exam.xml");
            }

            Stage stage = (Stage) editorContentBox.getScene().getWindow();
            File selectedFile = fileChooser.showSaveDialog(stage);

            // Save the exam and update the current file reference
            if (selectedFile != null) {
                saveExamToFile(selectedFile);
                currentXmlFile = selectedFile;

                saveStatusLabel.setText("Saved: " + selectedFile.getName() + " ✓");

                PauseTransition pause = new PauseTransition(Duration.seconds(2));
                pause.setOnFinished(event -> saveStatusLabel.setText(""));
                pause.play();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns to the start view after user confirmation.
     *
     * The current application state is reset and all unsaved editor changes
     * are discarded.
     */
    @FXML
    private void handleClose() {
        // Ask the user for confirmation before leaving the editor
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm");
        alert.setHeaderText("Go back to start page?");
        alert.setContentText("Your unsaved progress will be lost.");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Reset the shared application state before returning to the start page
                AppState.reset();

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/randexgen/swe1_randexgen/hello-view.fxml"));
                Scene scene = new Scene(loader.load());

                Stage stage = (Stage) closeButton.getScene().getWindow();
                stage.setScene(scene);
                stage.show();

                // Refresh the maximized state after switching back to the start view
                Platform.runLater(() -> {
                    stage.setMaximized(false);
                    stage.setMaximized(true);
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Saves the current exam object as XML into the given file.
     *
     * The method manually builds the XML structure for the exam, including
     * all chapters, subtasks, and variants.
     *
     * @param file the target file to which the exam is written
     * @throws Exception if writing to the file fails
     */
    private void saveExamToFile(File file) throws Exception {
        StringBuilder xml = new StringBuilder();

        // Build the XML header and root exam element
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<exam id=\"").append(escapeXml(currentExam.getId())).append("\">\n");

        // Write the general exam information
        xml.append("    <title>").append(escapeXml(currentExam.getTitle())).append("</title>\n");
        xml.append("    <chapters>\n");

        // Serialize all chapters of the current exam
        for (Chapter chapter : currentExam.getChapters()) {
            xml.append("        <chapter id=\"").append(escapeXml(chapter.getId())).append("\">\n");
            xml.append("            <title>").append(escapeXml(chapter.getTitle())).append("</title>\n");
            xml.append("            <examAppearance>").append(chapter.getExamAppearance()).append("</examAppearance>\n");
            xml.append("            <selectedRegularScore>").append(chapter.getSelectedRegularScore()).append("</selectedRegularScore>\n");
            xml.append("            <subtasks>\n");

            // Serialize all subtasks of the current chapter
            for (Subtask subtask : chapter.getSubtasks()) {
                xml.append("                <subtask id=\"").append(escapeXml(subtask.getId())).append("\">\n");
                xml.append("                    <title>").append(escapeXml(subtask.getTitle())).append("</title>\n");
                xml.append("                    <score>").append(subtask.getScore()).append("</score>\n");
                xml.append("                    <difficultyLevel>").append(subtask.getDifficultyLevel()).append("</difficultyLevel>\n");
                xml.append("                    <examType>").append(subtask.getExamType()).append("</examType>\n");
                xml.append("                    <variants>\n");

                // Serialize all variants of the current subtask
                for (Variant variant : subtask.getVariants()) {
                    xml.append("                        <variant id=\"").append(escapeXml(variant.getId())).append("\">\n");
                    xml.append("                            <taskText>").append(escapeXml(variant.getTaskText())).append("</taskText>\n");
                    xml.append("                            <answerText>").append(escapeXml(variant.getAnswerText())).append("</answerText>\n");
                    xml.append("                            <solutionText>").append(escapeXml(variant.getSolutionText())).append("</solutionText>\n");
                    xml.append("                        </variant>\n");
                }

                xml.append("                    </variants>\n");
                xml.append("                </subtask>\n");
            }

            xml.append("            </subtasks>\n");
            xml.append("        </chapter>\n");
        }

        xml.append("    </chapters>\n");
        xml.append("</exam>\n");

        // Write the finished XML content to the target file
        try (FileWriter writer = new FileWriter(file, false)) {
            writer.write(xml.toString());
        }
    }

    /**
     * Escapes special XML characters in a given string.
     *
     * This prevents invalid XML output when user-entered text contains
     * reserved XML symbols.
     *
     * @param text the text to escape
     * @return the escaped XML-safe text
     */
    private String escapeXml(String text) {
        // Return an empty string if no text is provided
        if (text == null) {
            return "";
        }

        // Replace XML-sensitive characters with their escaped representations
        return text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    /**
     * Loads an exam from the given XML file and refreshes the editor view.
     *
     * The method parses the XML file, updates the shared application state,
     * and rebuilds the UI based on the loaded exam data.
     *
     * @param xmlFile the XML file to load
     */
    public void loadXml(File xmlFile) {
        try (FileInputStream fis = new FileInputStream(xmlFile)) {
            // Parse the XML file into the internal exam model
            XMLParser parser = new XMLParser();
            currentExam = parser.parse(fis);
            currentXmlFile = xmlFile;

            // Store the loaded exam data globally for other views
            AppState.setCurrentExam(currentExam);
            AppState.setCurrentXmlFile(currentXmlFile);

            refreshWholeView();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Builds the navigation overview on the left side of the editor.
     *
     * The overview contains the exam entry, all chapter entries, and
     * optionally the subtasks of expanded chapters.
     *
     * @param exam the exam whose structure is displayed
     */
    private void buildLeftOverview(Exam exam) {
        chapterOverviewBox.getChildren().clear();

        // Create the overview row for the exam itself
        HBox examRow = new HBox();
        examRow.setAlignment(Pos.CENTER_LEFT);
        examRow.setSpacing(8);
        examRow.setPadding(new Insets(8, 10, 8, 10));
        examRow.getStyleClass().add("nav-exam");

        Label examLabel = new Label(exam.getTitle());
        examLabel.setStyle("-fx-font-size: 15; -fx-font-weight: bold;");
        examLabel.setWrapText(true);
        examLabel.setCursor(Cursor.HAND);

        HBox.setHgrow(examLabel, Priority.ALWAYS);

        examLabel.setOnMouseClicked(e -> showExamDetails());

        examRow.getChildren().add(examLabel);
        chapterOverviewBox.getChildren().add(examRow);

        // Build overview rows for all chapters and optionally their subtasks
        for (Chapter chapter : exam.getChapters()) {

            boolean expanded = chapterExpandedState.getOrDefault(chapter.getId(), true);

            // Create the navigation row for one chapter
            HBox chapterRow = new HBox();
            chapterRow.setAlignment(Pos.CENTER_LEFT);
            chapterRow.setSpacing(6);
            chapterRow.setPadding(new Insets(6, 10, 6, 18));
            chapterRow.getStyleClass().add("nav-chapter");

            Label arrowLabel = new Label(expanded ? "▾" : "▸");
            arrowLabel.setStyle("-fx-font-size: 22;");
            arrowLabel.setMinWidth(30);
            arrowLabel.setAlignment(Pos.CENTER);
            arrowLabel.setCursor(Cursor.HAND);

            Label chapterLabel = new Label(chapter.getTitle());
            chapterLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");
            chapterLabel.setWrapText(true);
            chapterLabel.setCursor(Cursor.HAND);

            HBox.setHgrow(chapterLabel, Priority.ALWAYS);

            // Toggle the expanded state of the selected chapter
            arrowLabel.setOnMouseClicked(e -> {
                boolean newState = !chapterExpandedState.getOrDefault(chapter.getId(), true);
                chapterExpandedState.put(chapter.getId(), newState);
                buildLeftOverview(currentExam);
            });

            // Show the editor details for the selected chapter
            chapterLabel.setOnMouseClicked(e -> showChapterDetails(chapter));

            chapterRow.getChildren().addAll(arrowLabel, chapterLabel);
            chapterOverviewBox.getChildren().add(chapterRow);

            // Render subtask entries only if the chapter is expanded
            if (expanded) {
                for (Subtask subtask : chapter.getSubtasks()) {
                    HBox subtaskRow = new HBox();
                    subtaskRow.setAlignment(Pos.CENTER_LEFT);
                    subtaskRow.setSpacing(6);
                    subtaskRow.setPadding(new Insets(4, 10, 4, 38));
                    subtaskRow.getStyleClass().add("nav-subtask");

                    Label subtaskLabel = new Label(subtask.getTitle());
                    subtaskLabel.setWrapText(true);
                    subtaskLabel.setCursor(Cursor.HAND);

                    HBox.setHgrow(subtaskLabel, Priority.ALWAYS);

                    // Open the selected subtask in the editor area
                    subtaskLabel.setOnMouseClicked(e -> showSubtaskDetails(subtask, chapter));

                    subtaskRow.getChildren().add(subtaskLabel);
                    chapterOverviewBox.getChildren().add(subtaskRow);
                }
            }
        }
    }

    /**
     * Expands all chapters in the left overview.
     *
     * <p>This method iterates over all chapters of the current exam and sets their
     * expanded state to {@code true} in the {@code chapterExpandedState} map.
     * Afterwards, the left overview UI is rebuilt to reflect the updated state.</p>
     *
     * <p>Used when the user clicks the "Expand All" button.</p>
     */
    @FXML
    private void handleExpandAll() {
        // Set all chapters to expanded (true)
        for (Chapter chapter : currentExam.getChapters()) {
            chapterExpandedState.put(chapter.getId(), true);
        }

        // Rebuild the UI to apply the new states
        buildLeftOverview(currentExam);
    }

    /**
     * Collapses all chapters in the left overview.
     *
     * <p>This method iterates over all chapters of the current exam and sets their
     * expanded state to {@code false} in the {@code chapterExpandedState} map.
     * Afterwards, the left overview UI is rebuilt to reflect the updated state.</p>
     *
     * <p>Used when the user clicks the "Collapse All" button.</p>
     */
    @FXML
    private void handleCollapseAll() {
        // Set all chapters to collapsed (false)
        for (Chapter chapter : currentExam.getChapters()) {
            chapterExpandedState.put(chapter.getId(), false);
        }

        // Rebuild the UI to apply the new states
        buildLeftOverview(currentExam);
    }

    /**
     * Replaces the bottom action bar with a single action button.
     *
     * The created button is used for context-sensitive actions such as
     * adding chapters, subtasks, or variants.
     *
     * @param text the label text of the button
     * @param action the action to execute when the button is clicked
     */
    private void setBottomActionButton(String text, Runnable action) {
        bottomActionBox.getChildren().clear();

        // Create and style the context-sensitive action button
        Button button = new Button(text);
        button.setPrefHeight(44);
        button.setPrefWidth(210);
        button.setStyle(
                "-fx-background-color: #D3D3D3;" +
                        "-fx-background-radius: 8;" +
                        "-fx-text-fill: black;" +
                        "-fx-font-size: 14;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 8 14 8 14;"
        );

        // Add the plus icon used for creation actions
        ImageView plusIcon = new ImageView(
                new Image(getClass().getResourceAsStream("/images/Plus.png"))
        );
        plusIcon.setFitWidth(22);
        plusIcon.setFitHeight(22);
        plusIcon.setPreserveRatio(true);
        plusIcon.setCursor(Cursor.DEFAULT);

        button.setGraphic(plusIcon);
        button.setGraphicTextGap(10);
        button.setOnAction(e -> action.run());

        // Apply hover styling for better visual feedback
        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: #C8C8C8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-text-fill: black;" +
                        "-fx-font-size: 14;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 8 14 8 14;"
        ));

        button.setOnMouseExited(e -> button.setStyle(
                "-fx-background-color: #D3D3D3;" +
                        "-fx-background-radius: 8;" +
                        "-fx-text-fill: black;" +
                        "-fx-font-size: 14;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 8 14 8 14;"
        ));

        bottomActionBox.getChildren().add(button);
    }

    /**
     * Creates the editor row for the exam object.
     *
     * The row allows editing the exam title and displays the calculated
     * regular and practice scores of the whole exam.
     *
     * @param exam the exam to display
     * @return the configured editor row
     */
    private HBox createExamRow(Exam exam) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.setSpacing(10);
        row.setPadding(new Insets(10, 15, 10, 15));
        row.getStyleClass().add("chapter-row");
        row.setMaxWidth(Double.MAX_VALUE);

        Label titleLabel = new Label(exam.getTitle());
        titleLabel.setPrefWidth(190);
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

        TextField titleField = new TextField(exam.getTitle());
        titleField.setPrefWidth(220);
        titleField.setVisible(false);
        titleField.setManaged(false);

        Label regularScoreLabel = new Label("Regular Score:");
        regularScoreLabel.setPrefWidth(80);

        Label regularScoreValueLabel = new Label(
                String.valueOf(ScoreCalculator.calculateRegularExamScore(exam.getChapters()))
        );
        regularScoreValueLabel.setPrefWidth(110);
        regularScoreValueLabel.setStyle("-fx-font-weight: bold;");

        Label practiceScoreLabel = new Label("Practice Score:");
        practiceScoreLabel.setPrefWidth(80);

        Label practiceScoreValueLabel = new Label(
                String.valueOf(ScoreCalculator.calculatePracticeExamScore(exam.getChapters()))
        );
        practiceScoreValueLabel.setPrefWidth(50);
        practiceScoreValueLabel.setStyle("-fx-font-weight: bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Add edit icon for inline title editing
        ImageView editButton = new ImageView(new Image(getClass().getResourceAsStream("/images/edit.png")));
        editButton.setFitWidth(25);
        editButton.setFitHeight(25);
        editButton.setPreserveRatio(true);
        editButton.setCursor(Cursor.HAND);

        row.getChildren().addAll(
                titleLabel,
                titleField,
                regularScoreLabel,
                regularScoreValueLabel,
                practiceScoreLabel,
                practiceScoreValueLabel,
                spacer,
                editButton
        );

        // Switch from label mode to text field mode when editing starts
        editButton.setOnMouseClicked(e -> {
            e.consume();
            titleLabel.setVisible(false);
            titleLabel.setManaged(false);
            titleField.setVisible(true);
            titleField.setManaged(true);
            titleField.requestFocus();
            titleField.selectAll();
        });

        // Save the changed title when the field is confirmed or loses focus
        titleField.setOnAction(e -> saveExamTitleChange(exam, titleLabel, titleField));
        titleField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                saveExamTitleChange(exam, titleLabel, titleField);
            }
        });

        return row;
    }

    /**
     * Creates the editor row for a subtask.
     *
     * The row allows editing the subtask title, score, difficulty, and exam type,
     * and also provides actions for editing or deleting the subtask.
     *
     * @param subtask the subtask to display
     * @param parentChapter the chapter containing the subtask
     * @return the configured subtask editor row
     */
    private HBox createSubtaskEditorRow(Subtask subtask, Chapter parentChapter) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.setSpacing(10);
        row.setPadding(new Insets(10, 15, 10, 15));
        row.getStyleClass().add("subtask-row");
        row.setMaxWidth(Double.MAX_VALUE);

        Label subtaskWarningLabel = new Label();
        this.subtaskWarningLabel = subtaskWarningLabel;
        subtaskWarningLabel.setStyle("-fx-font-weight: bold;");
        subtaskWarningLabel.setPrefWidth(215);
        subtaskWarningLabel.setText(DataValidator.isSubtaskUsable(subtask) ? "" : "⚠ Invalid");

        Label titleLabel = new Label(subtask.getTitle());
        titleLabel.setPrefWidth(190);
        titleLabel.setStyle("-fx-font-weight: bold;");

        TextField titleField = new TextField(subtask.getTitle());
        titleField.setPrefWidth(220);
        titleField.setVisible(false);
        titleField.setManaged(false);

        Label scoreLabel = new Label("Score:");
        scoreLabel.setPrefWidth(45);

        TextField scoreField = new TextField(String.valueOf(subtask.getScore()));
        scoreField.setPrefWidth(85);

        TextFormatter<String> formatter = new TextFormatter<>(change -> {
            String newText = change.getControlNewText();

            if (newText.matches("\\d*(\\.([05])?)?")) {
                return change;
            }
            return null;
        });
        scoreField.setTextFormatter(formatter);

        // Update the score when the user confirms the input
        scoreField.setOnAction(e -> {
            try {
                double value = Double.parseDouble(scoreField.getText().trim());

                if (value < 0 || (value * 2) % 1 != 0) {
                    throw new NumberFormatException();
                }

                subtask.setScore(value);
            } catch (NumberFormatException ignored) {
                scoreField.setText(String.valueOf(subtask.getScore()));
            }
        });

        // Restore the previous score if the entered value is invalid
        scoreField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                try {
                    double value = Double.parseDouble(scoreField.getText().trim());

                    if (value < 0 || (value * 2) % 1 != 0) {
                        throw new NumberFormatException();
                    }

                    subtask.setScore(value);
                } catch (NumberFormatException ignored) {
                    scoreField.setText(String.valueOf(subtask.getScore()));
                }
            }
        });

        Label difficultyLabel = new Label("Difficulty:");
        difficultyLabel.setPrefWidth(60);

        ComboBox<DifficultyLevel> difficultyBox = new ComboBox<>();
        difficultyBox.getItems().addAll(DifficultyLevel.values());
        difficultyBox.setValue(subtask.getDifficultyLevel());
        difficultyBox.setPrefWidth(130);

        Label examTypeLabel = new Label("Exam Type:");
        examTypeLabel.setPrefWidth(70);

        ComboBox<ExamType> examTypeBox = new ComboBox<>();
        examTypeBox.getItems().addAll(ExamType.REGULAR, ExamType.PRACTICE);
        examTypeBox.setValue(subtask.getExamType());
        examTypeBox.setPrefWidth(120);

        // Update the subtask and chapter warning labels after relevant changes
        Runnable updateWarnings = () -> {
            subtaskWarningLabel.setText(DataValidator.isSubtaskUsable(subtask) ? "" : "⚠ Invalid");

            Label chapterWarningLabel = chapterWarningLabels.get(parentChapter);
            if (chapterWarningLabel != null) {
                chapterWarningLabel.setText(DataValidator.getWarningText(parentChapter));
            }
        };

        difficultyBox.setOnAction(e -> {
            subtask.setDifficultyLevel(difficultyBox.getValue());
            updateWarnings.run();
        });

        examTypeBox.setOnAction(e -> {
            subtask.setExamType(examTypeBox.getValue());
            updateWarnings.run();
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Create action icons for editing and deleting the subtask
        ImageView editButton = new ImageView(new Image(getClass().getResourceAsStream("/images/edit.png")));
        editButton.setFitWidth(25);
        editButton.setFitHeight(25);
        editButton.setPreserveRatio(true);
        editButton.setCursor(Cursor.HAND);

        ImageView deleteButton = new ImageView(new Image(getClass().getResourceAsStream("/images/Trash.png")));
        deleteButton.setFitWidth(40);
        deleteButton.setFitHeight(40);
        deleteButton.setPreserveRatio(true);
        deleteButton.setCursor(Cursor.HAND);

        row.getChildren().addAll(
                titleLabel,
                titleField,
                scoreLabel,
                scoreField,
                difficultyLabel,
                difficultyBox,
                examTypeLabel,
                examTypeBox,
                spacer,
                editButton,
                deleteButton
        );

        // Switch from label mode to editable text field mode
        editButton.setOnMouseClicked(e -> {
            e.consume();
            titleLabel.setVisible(false);
            titleLabel.setManaged(false);
            titleField.setVisible(true);
            titleField.setManaged(true);
            titleField.requestFocus();
            titleField.selectAll();
        });

        // Save the changed title when editing is completed
        titleField.setOnAction(e -> saveSubtaskTitleChange(subtask, titleLabel, titleField));
        titleField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                saveSubtaskTitleChange(subtask, titleLabel, titleField);
            }
        });

        // Delete the selected subtask from its parent chapter
        deleteButton.setOnMouseClicked(e -> {
            e.consume();
            deleteSubtask(subtask, parentChapter);
        });

        Region outerSpacer = new Region();
        HBox.setHgrow(outerSpacer, Priority.ALWAYS);

        HBox outerRow = new HBox();
        outerRow.setAlignment(Pos.CENTER_LEFT);
        outerRow.setSpacing(10);
        outerRow.setMaxWidth(Double.MAX_VALUE);

        HBox.setHgrow(row, Priority.ALWAYS);

        outerRow.getChildren().addAll(row, outerSpacer, subtaskWarningLabel);

        return outerRow;
    }

    /**
     * Displays the details of a selected subtask in the editor area.
     *
     * The method shows the subtask editor row and all belonging variants,
     * and configures the bottom action button for adding new variants.
     *
     * @param subtask the subtask to display
     * @param parentChapter the chapter containing the subtask
     */
    private void showSubtaskDetails(Subtask subtask, Chapter parentChapter) {
        editorContentBox.getChildren().clear();

        Label sectionTitle = new Label("Edit Subtask");
        sectionTitle.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        editorContentBox.getChildren().add(sectionTitle);
        editorContentBox.getChildren().add(createSubtaskEditorRow(subtask, parentChapter));

        Label variantsTitle = new Label("Variants");
        variantsTitle.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");
        editorContentBox.getChildren().add(variantsTitle);

        // Render all variants of the selected subtask
        for (Variant variant : subtask.getVariants()) {
            editorContentBox.getChildren().add(createVariantRow(variant, subtask, parentChapter, subtaskWarningLabel));
        }

        // Configure the bottom action button for creating a new variant
        setBottomActionButton("Add Variant", () -> {
            Variant newVariant = new Variant();

            newVariant.setId(generateVariantId(subtask));
            newVariant.setAnswerText("");
            newVariant.setSolutionText("");

            subtask.getVariants().add(newVariant);
            showSubtaskDetails(subtask, parentChapter);
            scrollToBottom();
        });
    }

    /**
     * Generates the next free variant ID for a subtask.
     *
     * The method scans all existing variant IDs of the subtask and returns
     * the next numeric ID in the sequence.
     *
     * @param subtask the subtask whose variants are checked
     * @return the generated variant ID
     */
    private String generateVariantId(Subtask subtask) {

        int max = 0;

        // Find the highest numeric suffix of all existing variant IDs
        for (Variant v : subtask.getVariants()) {
            String id = v.getId();

            if (id != null && id.startsWith("v")) {
                try {
                    int num = Integer.parseInt(id.substring(1));
                    if (num > max) max = num;
                } catch (NumberFormatException ignored) {}
            }
        }

        return "v" + (max + 1);
    }

    /**
     * Scrolls the editor view to the bottom.
     *
     * This is mainly used after creating new elements so the newly added
     * content becomes visible immediately.
     */
    private void scrollToBottom() {
        javafx.application.Platform.runLater(() -> editorScrollPane.setVvalue(1.0));
    }

    /**
     * Creates the editor box for a variant.
     *
     * The box allows editing the question, answer, and solution texts
     * and provides a delete action for removing the variant.
     *
     * @param variant the variant to display
     * @param parentSubtask the subtask containing the variant
     * @param parentChapter the chapter containing the subtask
     * @return the configured variant editor box
     */
    private VBox createVariantRow(Variant variant, Subtask parentSubtask, Chapter parentChapter, Label subtaskWarningLabel) {
        VBox box = new VBox();
        box.setSpacing(8);
        box.setPadding(new Insets(10, 15, 10, 15));
        box.setFillWidth(true);
        box.getStyleClass().add("variant-box");

        Label variantTitle = new Label("Variant " + variant.getId());
        variantTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

        Label taskLabel = new Label("Question:");
        TextArea taskField = new TextArea(variant.getTaskText());
        taskField.setWrapText(true);
        taskField.setPrefRowCount(3);
        taskField.setMaxWidth(Double.MAX_VALUE);
        taskField.setStyle(
                "-fx-background-radius: 8;" +
                        "-fx-border-radius: 8;" +
                        "-fx-border-color: #BDBDBD;" +
                        "-fx-background-color: white;"
        );

        Label answerLabel = new Label("Answer:");
        TextArea answerField = new TextArea(variant.getAnswerText());
        answerField.setWrapText(true);
        answerField.setPrefRowCount(3);
        answerField.setMaxWidth(Double.MAX_VALUE);
        answerField.setStyle(
                "-fx-background-radius: 8;" +
                        "-fx-border-radius: 8;" +
                        "-fx-border-color: #BDBDBD;" +
                        "-fx-background-color: white;"
        );

        Label solutionLabel = new Label("Solution:");
        TextArea solutionField = new TextArea(variant.getSolutionText());
        solutionField.setWrapText(true);
        solutionField.setPrefRowCount(3);
        solutionField.setMaxWidth(Double.MAX_VALUE);
        solutionField.setStyle(
                "-fx-background-radius: 8;" +
                        "-fx-border-radius: 8;" +
                        "-fx-border-color: #BDBDBD;" +
                        "-fx-background-color: white;"
        );

        // Create the delete icon for removing the current variant
        ImageView deleteButton = new ImageView(
                new Image(getClass().getResourceAsStream("/images/Trash.png"))
        );
        deleteButton.setFitWidth(40);
        deleteButton.setFitHeight(40);
        deleteButton.setPreserveRatio(true);
        deleteButton.setCursor(Cursor.HAND);

        deleteButton.setOnMouseEntered(e -> deleteButton.setOpacity(0.7));
        deleteButton.setOnMouseExited(e -> deleteButton.setOpacity(1.0));

        deleteButton.setOnMouseClicked(e -> {
            e.consume();
            deleteVariant(variant, parentSubtask, parentChapter);
        });

        HBox topBar = new HBox();
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        topBar.getChildren().addAll(variantTitle, spacer, deleteButton);

        // Keep the variant model synchronized with the text fields
        taskField.setOnKeyReleased(e -> {
            variant.setTaskText(taskField.getText());
            subtaskWarningLabel.setText(DataValidator.isSubtaskUsable(parentSubtask) ? "" : "⚠ Invalid");
        });
        answerField.setOnKeyReleased(e -> variant.setAnswerText(answerField.getText()));
        solutionField.setOnKeyReleased(e -> variant.setSolutionText(solutionField.getText()));

        taskField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) variant.setTaskText(taskField.getText());
        });
        answerField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) variant.setAnswerText(answerField.getText());
        });
        solutionField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) variant.setSolutionText(solutionField.getText());
        });

        box.getChildren().addAll(
                topBar,
                taskLabel, taskField,
                answerLabel, answerField,
                solutionLabel, solutionField
        );
        taskField.setMaxWidth(Double.MAX_VALUE);
        answerField.setMaxWidth(Double.MAX_VALUE);
        solutionField.setMaxWidth(Double.MAX_VALUE);
        box.setFillWidth(true);

        return box;
    }

    /**
     * Deletes a chapter from the current exam and refreshes the editor.
     *
     * @param chapter the chapter to delete
     */
    private void deleteChapter(Chapter chapter) {
        // Remove the chapter and rebuild the editor view afterwards
        currentExam.getChapters().remove(chapter);
        buildLeftOverview(currentExam);
        showExamDetails();
        AppState.setCurrentExam(currentExam);
    }

    /**
     * Deletes a subtask from its parent chapter and refreshes the editor.
     *
     * @param subtask the subtask to delete
     * @param parentChapter the chapter containing the subtask
     */
    private void deleteSubtask(Subtask subtask, Chapter parentChapter) {
        // Remove the subtask and reopen the parent chapter view
        parentChapter.getSubtasks().remove(subtask);
        buildLeftOverview(currentExam);
        showChapterDetails(parentChapter);
        AppState.setCurrentExam(currentExam);
    }

    /**
     * Deletes a variant from its parent subtask and refreshes the subtask view.
     *
     * @param variant the variant to delete
     * @param parentSubtask the subtask containing the variant
     * @param parentChapter the chapter containing the subtask
     */
    private void deleteVariant(Variant variant, Subtask parentSubtask, Chapter parentChapter) {
        // Remove the selected variant and reopen the subtask view
        parentSubtask.getVariants().remove(variant);
        showSubtaskDetails(parentSubtask, parentChapter);
        AppState.setCurrentExam(currentExam);
    }

    /**
     * Saves a changed subtask title and restores the normal display mode.
     *
     * @param subtask the edited subtask
     * @param titleLabel the label showing the current title
     * @param titleField the text field used for editing
     */
    private void saveSubtaskTitleChange(Subtask subtask, Label titleLabel, TextField titleField) {
        String newTitle = titleField.getText().trim();

        // Apply the new title only if the entered value is not empty
        if (!newTitle.isEmpty()) {
            subtask.setTitle(newTitle);
            titleLabel.setText(newTitle);
            buildLeftOverview(currentExam);
        }

        // Switch back from edit mode to normal label mode
        titleField.setVisible(false);
        titleField.setManaged(false);
        titleLabel.setVisible(true);
        titleLabel.setManaged(true);
        AppState.setCurrentExam(currentExam);
    }

    /**
     * Sets the current exam and XML file and refreshes the editor view.
     *
     * This method is used when switching back from another scene while
     * preserving the current exam data.
     *
     * @param exam the exam to display
     * @param xmlFile the XML file associated with the exam
     */
    public void setExamData(Exam exam, File xmlFile) {
        this.currentExam = exam;
        this.currentXmlFile = xmlFile;

        // Update the shared application state with the provided data
        AppState.setCurrentExam(currentExam);
        AppState.setCurrentXmlFile(currentXmlFile);

        refreshWholeView();
    }

    /**
     * Rebuilds the complete editor view.
     *
     * The method clears the overview, editor, and action areas and then
     * recreates them based on the current exam state.
     */
    private void refreshWholeView() {
        chapterOverviewBox.getChildren().clear();
        editorContentBox.getChildren().clear();
        bottomActionBox.getChildren().clear();

        // Stop if no exam is currently loaded
        if (currentExam == null) {
            return;
        }

        buildLeftOverview(currentExam);
        showExamDetails();
    }

    /**
     * Creates the editor row for a chapter.
     *
     * The row allows editing the chapter title, selected regular score,
     * appearance, and provides actions for editing or deleting the chapter.
     *
     * @param chapter the chapter to display
     * @return the configured chapter editor row
     */
    private HBox createChapterRow(Chapter chapter) {
        Label warningLabel = new Label();
        warningLabel.setStyle("-fx-font-weight: bold;");
        warningLabel.setPrefWidth(215);
        warningLabel.setText(DataValidator.getWarningText(chapter));
        chapterWarningLabels.put(chapter, warningLabel);

        ScoreCalculator scoreCalculator = new ScoreCalculator();

        HBox chapterRow = new HBox();
        chapterRow.setAlignment(Pos.CENTER_LEFT);
        chapterRow.setSpacing(10);
        chapterRow.setPadding(new Insets(10, 15, 10, 15));
        chapterRow.getStyleClass().add("chapter-row");
        chapterRow.setMaxWidth(Double.MAX_VALUE);

        Label titleLabel = new Label(chapter.getTitle());
        titleLabel.setPrefWidth(190);
        titleLabel.setStyle("-fx-font-weight: bold;");

        TextField titleField = new TextField(chapter.getTitle());
        titleField.setPrefWidth(220);
        titleField.setVisible(false);
        titleField.setManaged(false);

        Label regularScoreLabel = new Label("Regular Score:");
        regularScoreLabel.setPrefWidth(80);

        ComboBox<Double> regularScoreBox = new ComboBox<>();
        regularScoreBox.setPrefWidth(110);
        List<Double> scores = scoreCalculator.calculatePossibleRegularScores(chapter);

        // Configure the score selection depending on whether valid scores exist
        if (scores.isEmpty()) {
            regularScoreBox.getItems().setAll(0.0);
            regularScoreBox.setValue(0.0);
            regularScoreBox.setDisable(true);
            regularScoreBox.setStyle("-fx-opacity: 0.5;");
        } else {
            regularScoreBox.getItems().setAll(scores);
            regularScoreBox.setValue(scores.get(0));
            regularScoreBox.setDisable(false);
            regularScoreBox.setStyle("");
        }

        // Store the selected regular score inside the chapter model
        regularScoreBox.setOnAction(e -> {
            Double value = regularScoreBox.getValue();
            if (value != null) {
                chapter.setSelectedRegularScore(value);
            }
        });

        Label practiceScoreTitleLabel = new Label("Practice Score:");
        practiceScoreTitleLabel.setPrefWidth(80);

        Label practiceScoreValueLabel = new Label(String.valueOf(scoreCalculator.calculatePracticeScore(chapter)));
        practiceScoreValueLabel.setPrefWidth(50);
        practiceScoreValueLabel.setStyle("-fx-font-weight: bold;");

        Label appearanceLabel = new Label("Appearance:");
        appearanceLabel.setPrefWidth(70);

        ComboBox<ExamAppearance> appearanceBox = new ComboBox<>();
        appearanceBox.getItems().addAll(ExamAppearance.values());
        appearanceBox.setValue(chapter.getExamAppearance());
        appearanceBox.setPrefWidth(120);

        // Update chapter state, warnings, and score values after appearance changes
        appearanceBox.setOnAction(e -> {
            chapter.setExamAppearance(appearanceBox.getValue());

            warningLabel.setText(DataValidator.getWarningText(chapter));

            regularScoreBox.getItems().setAll(scoreCalculator.calculatePossibleRegularScores(chapter));
            if (!regularScoreBox.getItems().isEmpty()) {
                regularScoreBox.setValue(regularScoreBox.getItems().get(0));
            } else {
                regularScoreBox.setValue(null);
            }

            practiceScoreValueLabel.setText(String.valueOf(scoreCalculator.calculatePracticeScore(chapter)));
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Create action icons for editing and deleting the chapter
        ImageView editButton = new ImageView(new Image(getClass().getResourceAsStream("/images/edit.png")));
        editButton.setFitWidth(25);
        editButton.setFitHeight(25);
        editButton.setPreserveRatio(true);
        editButton.setCursor(Cursor.HAND);

        ImageView deleteButton = new ImageView(new Image(getClass().getResourceAsStream("/images/Trash.png")));
        deleteButton.setFitWidth(40);
        deleteButton.setFitHeight(40);
        deleteButton.setPreserveRatio(true);
        deleteButton.setCursor(Cursor.HAND);

        chapterRow.getChildren().addAll(
                titleLabel,
                titleField,
                regularScoreLabel,
                regularScoreBox,
                practiceScoreTitleLabel,
                practiceScoreValueLabel,
                appearanceLabel,
                appearanceBox,
                spacer,
                editButton,
                deleteButton
        );

        // Switch to title edit mode when the edit icon is clicked
        editButton.setOnMouseClicked(e -> {
            e.consume();
            titleLabel.setVisible(false);
            titleLabel.setManaged(false);
            titleField.setVisible(true);
            titleField.setManaged(true);
            titleField.requestFocus();
            titleField.selectAll();
        });

        // Save the changed title once editing is completed
        titleField.setOnAction(e -> saveChapterTitleChange(chapter, titleLabel, titleField));
        titleField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                saveChapterTitleChange(chapter, titleLabel, titleField);
            }
        });

        // Delete the selected chapter from the exam
        deleteButton.setOnMouseClicked(e -> {
            e.consume();
            deleteChapter(chapter);
        });

        Region outerSpacer = new Region();
        HBox.setHgrow(outerSpacer, Priority.ALWAYS);

        HBox outerRow = new HBox();
        outerRow.setAlignment(Pos.CENTER_LEFT);
        outerRow.setSpacing(10);
        outerRow.setMaxWidth(Double.MAX_VALUE);

        HBox.setHgrow(chapterRow, Priority.ALWAYS);

        outerRow.getChildren().addAll(chapterRow, outerSpacer, warningLabel);

        return outerRow;
    }

    /**
     * Saves a changed chapter title and restores the normal display mode.
     *
     * @param chapter the edited chapter
     * @param titleLabel the label showing the current title
     * @param titleField the text field used for editing
     */
    private void saveChapterTitleChange(Chapter chapter, Label titleLabel, TextField titleField) {
        String newTitle = titleField.getText().trim();

        // Apply the new title only if it is not empty
        if (!newTitle.isEmpty()) {
            chapter.setTitle(newTitle);
            titleLabel.setText(newTitle);
            buildLeftOverview(currentExam);
        }

        // Restore the normal view after title editing
        titleField.setVisible(false);
        titleField.setManaged(false);
        titleLabel.setVisible(true);
        titleLabel.setManaged(true);
        AppState.setCurrentExam(currentExam);
    }

    /**
     * Saves a changed exam title and restores the normal display mode.
     *
     * @param exam the edited exam
     * @param titleLabel the label showing the current title
     * @param titleField the text field used for editing
     */
    private void saveExamTitleChange(Exam exam, Label titleLabel, TextField titleField) {
        String newTitle = titleField.getText().trim();

        // Apply the new title only if it is not empty
        if (!newTitle.isEmpty()) {
            exam.setTitle(newTitle);
            titleLabel.setText(newTitle);
            buildLeftOverview(currentExam);
        }

        // Restore the normal view after title editing
        titleField.setVisible(false);
        titleField.setManaged(false);
        titleLabel.setVisible(true);
        titleLabel.setManaged(true);
        AppState.setCurrentExam(currentExam);
    }

    /**
     * Displays the exam details inside the editor area.
     *
     * The method shows the editable exam row, all chapters,
     * and configures the bottom action button for adding chapters.
     */
    private void showExamDetails() {
        editorContentBox.getChildren().clear();

        Label sectionTitle = new Label("Edit Exam");
        sectionTitle.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        editorContentBox.getChildren().add(sectionTitle);
        editorContentBox.getChildren().add(createExamRow(currentExam));

        Label chaptersTitle = new Label("Chapters");
        chaptersTitle.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");
        editorContentBox.getChildren().add(chaptersTitle);

        // Render all chapters of the current exam
        for (Chapter chapter : currentExam.getChapters()) {
            editorContentBox.getChildren().add(createChapterRow(chapter));
        }

        // Configure the bottom action button for creating a new chapter
        setBottomActionButton("Add Chapter", () -> {
            Chapter newChapter = new Chapter();
            newChapter.setId("ch-" + System.currentTimeMillis());
            newChapter.setTitle("New Chapter");
            newChapter.setExamAppearance(ExamAppearance.INCLUDE);

            currentExam.getChapters().add(newChapter);
            buildLeftOverview(currentExam);
            showExamDetails();
            scrollToBottom();
        });
    }

    /**
     * Displays the details of a selected chapter in the editor area.
     *
     * The method shows the editable chapter row, all its subtasks,
     * and configures the bottom action button for adding subtasks.
     *
     * @param chapter the chapter to display
     */
    private void showChapterDetails(Chapter chapter) {
        editorContentBox.getChildren().clear();

        Label sectionTitle = new Label("Edit Chapter");
        sectionTitle.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        editorContentBox.getChildren().add(sectionTitle);
        editorContentBox.getChildren().add(createChapterRow(chapter));

        Label subtasksTitle = new Label("Subtasks");
        subtasksTitle.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");
        editorContentBox.getChildren().add(subtasksTitle);

        // Render all subtasks of the selected chapter
        for (Subtask subtask : chapter.getSubtasks()) {
            editorContentBox.getChildren().add(createSubtaskEditorRow(subtask, chapter));
        }

        // Configure the bottom action button for creating a new subtask
        setBottomActionButton("Add Subtask", () -> {
            Subtask newSubtask = new Subtask();
            newSubtask.setId("st-" + System.currentTimeMillis());
            newSubtask.setTitle("New Subtask");
            newSubtask.setScore(0);
            newSubtask.setDifficultyLevel(DifficultyLevel.EASY);
            newSubtask.setExamType(ExamType.PRACTICE);

            chapter.getSubtasks().add(newSubtask);
            buildLeftOverview(currentExam);
            showChapterDetails(chapter);
            scrollToBottom();
        });
    }

    /**
     * Switches from the XML editor view to the PDF preview view.
     *
     * The current exam and file references are stored in the application state
     * so that the PDF view can access the same data.
     */
@FXML
private void switchToPDF() {
    try {
        AppState.setCurrentExam(currentExam);
        AppState.setCurrentXmlFile(currentXmlFile);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/randexgen/swe1_randexgen/pdfviewer.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);

        Stage stage = (Stage) pdfPane.getScene().getWindow();
        boolean wasMaximized = stage.isMaximized();

        stage.setResizable(true);
        stage.setScene(scene);
        stage.show();

        if (wasMaximized) {
            javafx.application.Platform.runLater(() -> stage.setMaximized(true));
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
}
}