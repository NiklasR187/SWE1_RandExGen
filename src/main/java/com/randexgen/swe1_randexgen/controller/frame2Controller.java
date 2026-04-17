package com.randexgen.swe1_randexgen.controller;

import com.randexgen.swe1_randexgen.app.AppNavigator;
import com.randexgen.swe1_randexgen.datamodel.Chapter;
import com.randexgen.swe1_randexgen.datamodel.DifficultyLevel;
import com.randexgen.swe1_randexgen.datamodel.Exam;
import com.randexgen.swe1_randexgen.datamodel.ExamAppearance;
import com.randexgen.swe1_randexgen.datamodel.ExamType;
import com.randexgen.swe1_randexgen.datamodel.Subtask;
import com.randexgen.swe1_randexgen.datamodel.Variant;
import com.randexgen.swe1_randexgen.service.AppState;
import com.randexgen.swe1_randexgen.service.DataValidator;
import com.randexgen.swe1_randexgen.service.ScoreCalculator;
import com.randexgen.swe1_randexgen.service.XMLParser;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controller for the main XML editor view of the application.
 *
 * This controller manages loading, editing, saving, and navigating through
 * exams, chapters, subtasks, and variants inside the JavaFX user interface.
 */
public class frame2Controller {

    private final Map<String, Boolean> chapterExpandedState = new HashMap<>();
    private final Map<Chapter, Label> chapterWarningLabels = new HashMap<>();

    private File currentXmlFile;
    private Exam currentExam;
    private Label subtaskWarningLabel;
    private String defaultExpandStyle;
    private String defaultCollapseStyle;

    @FXML
    private BorderPane rootPane;

    @FXML
    private Button closeButton;

    @FXML
    private ScrollPane editorScrollPane;

    @FXML
    private HBox bottomActionBox;

    @FXML
    private Label saveStatusLabel;

    @FXML
    private VBox chapterOverviewBox;

    @FXML
    private Button expandAllButton;

    @FXML
    private Button collapseAllButton;

    @FXML
    private VBox editorContentBox;

    /**
     * Initializes the editor view after the FXML file has been loaded.
     *
     * The method resets stored expansion states and clears all major UI containers
     * so that the view starts in a clean and consistent state.
     */
    @FXML
    private void initialize() {
        chapterExpandedState.clear();

        if (chapterOverviewBox != null) {
            chapterOverviewBox.getChildren().clear();
        }
        if (editorContentBox != null) {
            editorContentBox.getChildren().clear();
        }
        if (bottomActionBox != null) {
            bottomActionBox.getChildren().clear();
        }

        defaultExpandStyle = expandAllButton.getStyle();
        defaultCollapseStyle = collapseAllButton.getStyle();

        refreshExpandCollapseButtonState();

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
        if (currentXmlFile == null) {
            handleSaveAs();
            return;
        }

        try {
            saveExamToFile(currentXmlFile);
            // UI/UX-Rule "Guidance": a short success message confirms that the save action worked as expected.
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
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save XML File As");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("XML Files", "*.xml")
            );

            if (currentXmlFile != null) {
                fileChooser.setInitialFileName(currentXmlFile.getName());
            } else {
                fileChooser.setInitialFileName("exam.xml");
            }

            Stage stage = (Stage) editorContentBox.getScene().getWindow();
            File selectedFile = fileChooser.showSaveDialog(stage);

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
    //UI/UX-Rule "Empathy": confirm potentially destructive actions to protect the user from accidental data loss.
    @FXML
    private void handleClose() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm");
        alert.setHeaderText("Go back to start page?");
        alert.setContentText("Your unsaved progress will be lost.");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            AppState.reset();
            AppNavigator.showStartView();
        }
    }

    /**
     * Saves the current exam object as XML into the given file.
     *
     * @param file the target file to which the exam is written
     * @throws Exception if writing to the file fails
     */
    private void saveExamToFile(File file) throws Exception {
        StringBuilder xml = new StringBuilder();

        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<exam id=\"").append(escapeXml(currentExam.getId())).append("\">\n");
        xml.append("    <title>").append(escapeXml(currentExam.getTitle())).append("</title>\n");
        xml.append("    <chapters>\n");

        for (Chapter chapter : currentExam.getChapters()) {
            xml.append("        <chapter id=\"").append(escapeXml(chapter.getId())).append("\">\n");
            xml.append("            <title>").append(escapeXml(chapter.getTitle())).append("</title>\n");
            xml.append("            <examAppearance>").append(chapter.getExamAppearance()).append("</examAppearance>\n");
            xml.append("            <selectedRegularScore>").append(chapter.getSelectedRegularScore()).append("</selectedRegularScore>\n");
            xml.append("            <subtasks>\n");

            for (Subtask subtask : chapter.getSubtasks()) {
                xml.append("                <subtask id=\"").append(escapeXml(subtask.getId())).append("\">\n");
                xml.append("                    <title>").append(escapeXml(subtask.getTitle())).append("</title>\n");
                xml.append("                    <score>").append(subtask.getScore()).append("</score>\n");
                xml.append("                    <difficultyLevel>").append(subtask.getDifficultyLevel()).append("</difficultyLevel>\n");
                xml.append("                    <examType>").append(subtask.getExamType()).append("</examType>\n");
                xml.append("                    <variants>\n");

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

        try (FileWriter writer = new FileWriter(file, false)) {
            writer.write(xml.toString());
        }
    }

    /**
     * Escapes special XML characters in a given string.
     *
     * @param text the text to escape
     * @return the escaped XML-safe text
     */
    private String escapeXml(String text) {
        if (text == null) {
            return "";
        }

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
     * @param xmlFile the XML file to load
     */
    public void loadXml(File xmlFile) {
        try (FileInputStream fis = new FileInputStream(xmlFile)) {
            XMLParser parser = new XMLParser();
            currentExam = parser.parse(fis);
            currentXmlFile = xmlFile;

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
     * @param exam the exam whose structure is displayed
     */
    private void buildLeftOverview(Exam exam) {
        chapterOverviewBox.getChildren().clear();

        HBox examRow = new HBox();
        examRow.setAlignment(Pos.CENTER_LEFT);
        examRow.setSpacing(8);
        examRow.setPadding(new Insets(8, 10, 8, 28));
        examRow.getStyleClass().add("nav-exam");

        Label examLabel = new Label(exam.getTitle());
        examLabel.getStyleClass().add("exam-label");
        examLabel.setWrapText(true);

        HBox.setHgrow(examLabel, Priority.ALWAYS);
        examLabel.setOnMouseClicked(e -> showExamDetails());

        examRow.getChildren().add(examLabel);
        chapterOverviewBox.getChildren().add(examRow);

        for (Chapter chapter : exam.getChapters()) {
            boolean expanded = chapterExpandedState.getOrDefault(chapter.getId(), true);

            HBox chapterRow = new HBox();
            chapterRow.setAlignment(Pos.CENTER_LEFT);
            chapterRow.setSpacing(6);
            chapterRow.setPadding(new Insets(6, 10, 6, 18));
            chapterRow.getStyleClass().add("nav-chapter");

            Label arrowLabel = new Label(expanded ? "▾" : "▸");
            arrowLabel.getStyleClass().add("nav-arrow");
            arrowLabel.setMinWidth(30);
            arrowLabel.setAlignment(Pos.CENTER);
            arrowLabel.setCursor(Cursor.HAND);

            Label chapterLabel = new Label(chapter.getTitle());
            chapterLabel.getStyleClass().add("chapter-label");
            chapterLabel.setWrapText(true);

            HBox.setHgrow(chapterLabel, Priority.ALWAYS);

            arrowLabel.setOnMouseClicked(e -> {
                boolean newState = !chapterExpandedState.getOrDefault(chapter.getId(), true);
                chapterExpandedState.put(chapter.getId(), newState);
                buildLeftOverview(currentExam);
            });

            chapterLabel.setOnMouseClicked(e -> showChapterDetails(chapter));

            chapterRow.getChildren().addAll(arrowLabel, chapterLabel);
            chapterOverviewBox.getChildren().add(chapterRow);

            if (expanded) {
                for (Subtask subtask : chapter.getSubtasks()) {
                    HBox subtaskRow = new HBox();
                    subtaskRow.setAlignment(Pos.CENTER_LEFT);
                    subtaskRow.setSpacing(6);
                    subtaskRow.setPadding(new Insets(4, 10, 4, 38));
                    subtaskRow.getStyleClass().add("nav-subtask");

                    Label subtaskLabel = new Label(subtask.getTitle());
                    subtaskLabel.getStyleClass().add("subtask-label");
                    subtaskLabel.setWrapText(true);

                    HBox.setHgrow(subtaskLabel, Priority.ALWAYS);
                    subtaskLabel.setOnMouseClicked(e -> showSubtaskDetails(subtask, chapter));

                    subtaskRow.getChildren().add(subtaskLabel);
                    chapterOverviewBox.getChildren().add(subtaskRow);
                }
            }
        }

        refreshExpandCollapseButtonState();
    }

    private void refreshExpandCollapseButtonState() {
        if (currentExam == null || currentExam.getChapters() == null || currentExam.getChapters().isEmpty()) {
            updateExpandCollapseButtonsDisabled();
            return;
        }

        boolean allExpanded = true;
        boolean allCollapsed = true;

        for (Chapter chapter : currentExam.getChapters()) {
            boolean expanded = chapterExpandedState.getOrDefault(chapter.getId(), true);

            if (expanded) {
                allCollapsed = false;
            } else {
                allExpanded = false;
            }
        }

        if (allExpanded) {
            updateExpandCollapseButtons(true);
        } else if (allCollapsed) {
            updateExpandCollapseButtons(false);
        } else {
            updateExpandCollapseButtonsNeutral();
        }
    }

    private void updateExpandCollapseButtonsDisabled() {
        expandAllButton.setDisable(true);
        collapseAllButton.setDisable(true);
        expandAllButton.setStyle(defaultExpandStyle);
        collapseAllButton.setStyle(defaultCollapseStyle);
    }

    private void updateExpandCollapseButtonsNeutral() {
        expandAllButton.setDisable(false);
        collapseAllButton.setDisable(false);
        expandAllButton.setStyle(defaultExpandStyle);
        collapseAllButton.setStyle(defaultCollapseStyle);
    }

    /**
     * Expands all chapters in the left overview.
     */
    @FXML
    private void handleExpandAll() {
        for (Chapter chapter : currentExam.getChapters()) {
            chapterExpandedState.put(chapter.getId(), true);
        }

        buildLeftOverview(currentExam);
        refreshExpandCollapseButtonState();
    }

    /**
     * Collapses all chapters in the left overview.
     */
    @FXML
    private void handleCollapseAll() {
        for (Chapter chapter : currentExam.getChapters()) {
            chapterExpandedState.put(chapter.getId(), false);
        }

        buildLeftOverview(currentExam);
        refreshExpandCollapseButtonState();
    }

    /**
     * Updates the visual state and interactivity of the expand/collapse buttons.
     *
     * @param expanded true if all chapters are expanded, false if all are collapsed
     */
    private void updateExpandCollapseButtons(boolean expanded) {
        if (expanded) {
            expandAllButton.setDisable(true);
            collapseAllButton.setDisable(false);
        } else {
            collapseAllButton.setDisable(true);
            expandAllButton.setDisable(false);
        }
    }

    /**
     * Replaces the bottom action bar with a single action button.
     *
     * @param text the label text of the button
     * @param action the action to execute when the button is clicked
     */
    private void setBottomActionButton(String text, Runnable action) {
        bottomActionBox.getChildren().clear();

        Button button = new Button(text);
        button.setPrefHeight(44);
        button.setPrefWidth(210);
        button.getStyleClass().add("bottom-action-button");

        SVGPath plusIcon = new SVGPath();
        plusIcon.setContent("M11 5h2v6h6v2h-6v6h-2v-6H5v-2h6V5Z");
        plusIcon.getStyleClass().add("action-icon");

        plusIcon.setScaleX(1.4);
        plusIcon.setScaleY(1.4);

        plusIcon.setPickOnBounds(true);

        button.setGraphic(plusIcon);
        button.setGraphicTextGap(10);

        button.setOnAction(e -> action.run());

        bottomActionBox.getChildren().add(button);
    }

    /**
     * Creates the editor row for the exam object.
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
        titleLabel.getStyleClass().add("strong-label");

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
        regularScoreValueLabel.getStyleClass().add("score-value-label");

        Label practiceScoreLabel = new Label("Practice Score:");
        practiceScoreLabel.setPrefWidth(80);

        Label practiceScoreValueLabel = new Label(
                String.valueOf(ScoreCalculator.calculatePracticeExamScore(exam.getChapters()))
        );
        practiceScoreValueLabel.setPrefWidth(50);
        practiceScoreValueLabel.getStyleClass().add("score-value-label");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        SVGPath editIcon = new SVGPath();
        editIcon.setContent("M4 16.5V20h3.5L17.8 9.7l-3.5-3.5L4 16.5ZM20.7 6.3a1 1 0 0 0 0-1.4l-1.6-1.6a1 1 0 0 0-1.4 0l-1.3 1.3 3.5 3.5 1.3-1.3Z");
        editIcon.getStyleClass().add("action-icon");
        editIcon.setScaleX(1.4);
        editIcon.setScaleY(1.4);
        editIcon.setMouseTransparent(true);

        StackPane editButton = new StackPane(editIcon);
        editButton.getStyleClass().add("icon-button");
        editButton.setPickOnBounds(true);
        Tooltip.install(editButton, new Tooltip("Rename exam"));

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

        editButton.setOnMouseClicked(e -> {
            e.consume();
            titleLabel.setVisible(false);
            titleLabel.setManaged(false);
            titleField.setVisible(true);
            titleField.setManaged(true);
            titleField.requestFocus();
            titleField.selectAll();
        });

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
        subtaskWarningLabel.getStyleClass().add("warning-label");
        subtaskWarningLabel.setPrefWidth(215);
        subtaskWarningLabel.setText(DataValidator.isSubtaskUsable(subtask) ? "" : "⚠ Invalid");

        Label titleLabel = new Label(subtask.getTitle());
        titleLabel.setPrefWidth(190);
        titleLabel.getStyleClass().add("strong-label");

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
            return newText.matches("\\d*(\\.([05])?)?") ? change : null;
        });
        scoreField.setTextFormatter(formatter);

        scoreField.setOnAction(e -> applyScoreChange(scoreField, subtask));
        scoreField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                applyScoreChange(scoreField, subtask);
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

        SVGPath editIcon = new SVGPath();
        editIcon.setContent("M4 16.5V20h3.5L17.8 9.7l-3.5-3.5L4 16.5ZM20.7 6.3a1 1 0 0 0 0-1.4l-1.6-1.6a1 1 0 0 0-1.4 0l-1.3 1.3 3.5 3.5 1.3-1.3Z");
        editIcon.getStyleClass().add("action-icon");
        editIcon.setScaleX(1.4);
        editIcon.setScaleY(1.4);
        editIcon.setMouseTransparent(true);

        StackPane editButton = new StackPane(editIcon);
        editButton.getStyleClass().add("icon-button");
        editButton.setPickOnBounds(true);
        Tooltip.install(editButton, new Tooltip("Rename subtask"));

        SVGPath deleteIcon = new SVGPath();
        deleteIcon.setContent("M8 6h8M10 6V4h4v2M7 6l1 12h8l1-12M11 9v6M13 9v6");
        deleteIcon.getStyleClass().add("delete-icon");
        deleteIcon.setScaleX(1.4);
        deleteIcon.setScaleY(1.4);
        deleteIcon.setMouseTransparent(true);

        StackPane deleteButton = new StackPane(deleteIcon);
        deleteButton.getStyleClass().add("icon-button");
        deleteButton.setPickOnBounds(true);
        Tooltip.install(deleteButton, new Tooltip("Delete Subtask"));

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

        editButton.setOnMouseClicked(e -> {
            e.consume();
            titleLabel.setVisible(false);
            titleLabel.setManaged(false);
            titleField.setVisible(true);
            titleField.setManaged(true);
            titleField.requestFocus();
            titleField.selectAll();
        });

        titleField.setOnAction(e -> saveSubtaskTitleChange(subtask, titleLabel, titleField));
        titleField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                saveSubtaskTitleChange(subtask, titleLabel, titleField);
            }
        });

        deleteButton.setOnMouseClicked(e -> {
            e.consume();

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Deletion");
            alert.setHeaderText("Delete Subtask?");
            alert.setContentText("Are you sure you want to delete this subtask? This action cannot be undone.");

            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                deleteSubtask(subtask, parentChapter);
            }
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
     * Applies a new score from the UI to the subtask.
     *
     * @param scoreField the score input field
     * @param subtask the related subtask
     */
    private void applyScoreChange(TextField scoreField, Subtask subtask) {
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

    /**
     * Displays the details of a selected subtask in the editor area.
     *
     * @param subtask the subtask to display
     * @param parentChapter the chapter containing the subtask
     */
    private void showSubtaskDetails(Subtask subtask, Chapter parentChapter) {
        editorContentBox.getChildren().clear();

        Label sectionTitle = new Label("Edit Subtask");
        sectionTitle.getStyleClass().add("section-title");

        editorContentBox.getChildren().add(sectionTitle);
        editorContentBox.getChildren().add(createSubtaskEditorRow(subtask, parentChapter));

        Label variantsTitle = new Label("Variants");
        variantsTitle.getStyleClass().add("subsection-title");
        editorContentBox.getChildren().add(variantsTitle);

        for (Variant variant : subtask.getVariants()) {
            editorContentBox.getChildren().add(createVariantRow(variant, subtask, parentChapter, subtaskWarningLabel));
        }

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
     * @param subtask the subtask whose variants are checked
     * @return the generated variant ID
     */
    private String generateVariantId(Subtask subtask) {
        int max = 0;

        for (Variant v : subtask.getVariants()) {
            String id = v.getId();

            if (id != null && id.startsWith("v")) {
                try {
                    int num = Integer.parseInt(id.substring(1));
                    if (num > max) {
                        max = num;
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }

        return "v" + (max + 1);
    }

    /**
     * Scrolls the editor view to the bottom.
     */
    private void scrollToBottom() {
        Platform.runLater(() -> editorScrollPane.setVvalue(1.0));
    }

    /**
     * Creates the editor box for a variant.
     *
     * @param variant the variant to display
     * @param parentSubtask the subtask containing the variant
     * @param parentChapter the chapter containing the subtask
     * @param subtaskWarningLabel warning label of the parent subtask
     * @return the configured variant editor box
     */
    private VBox createVariantRow(Variant variant, Subtask parentSubtask, Chapter parentChapter, Label subtaskWarningLabel) {
        VBox box = new VBox();
        box.setSpacing(8);
        box.setPadding(new Insets(10, 15, 10, 15));
        box.setFillWidth(true);
        box.getStyleClass().add("variant-box");

        Label titleLabel = new Label("Variant");
        titleLabel.getStyleClass().add("variant-title");

        Label badge = new Label(variant.getId());
        badge.getStyleClass().add("variant-badge");

        HBox titleBox = new HBox(titleLabel, badge);
        titleBox.setSpacing(8);
        titleBox.setAlignment(Pos.CENTER_LEFT);

        Label taskLabel = new Label("Question:");
        TextArea taskField = createVariantTextArea(variant.getTaskText());

        Label answerLabel = new Label("Answer:");
        TextArea answerField = createVariantTextArea(variant.getAnswerText());

        Label solutionLabel = new Label("Solution:");
        TextArea solutionField = createVariantTextArea(variant.getSolutionText());

        SVGPath deleteIcon = new SVGPath();
        deleteIcon.setContent("M8 6h8M10 6V4h4v2M7 6l1 12h8l1-12M11 9v6M13 9v6");
        deleteIcon.getStyleClass().add("delete-icon");
        deleteIcon.setScaleX(1.6);
        deleteIcon.setScaleY(1.6);
        deleteIcon.setMouseTransparent(true);

        StackPane deleteButton = new StackPane(deleteIcon);
        deleteButton.getStyleClass().add("icon-button");
        deleteButton.setPickOnBounds(true);
        Tooltip.install(deleteButton, new Tooltip("Delete Variant"));

        deleteButton.setOnMouseClicked(e -> {
            e.consume();

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Deletion");
            alert.setHeaderText("Delete Variant?");
            alert.setContentText("Are you sure you want to delete this variant? This action cannot be undone.");

            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                deleteVariant(variant, parentSubtask, parentChapter);
            }
        });

        HBox topBar = new HBox();
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        topBar.getChildren().addAll(titleBox, spacer, deleteButton);

        taskField.setOnKeyReleased(e -> {
            variant.setTaskText(taskField.getText());
            subtaskWarningLabel.setText(DataValidator.isSubtaskUsable(parentSubtask) ? "" : "⚠ Invalid");
        });

        answerField.setOnKeyReleased(e -> variant.setAnswerText(answerField.getText()));
        solutionField.setOnKeyReleased(e -> variant.setSolutionText(solutionField.getText()));

        taskField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                variant.setTaskText(taskField.getText());
            }
        });
        answerField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                variant.setAnswerText(answerField.getText());
            }
        });
        solutionField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                variant.setSolutionText(solutionField.getText());
            }
        });

        box.getChildren().addAll(
                topBar,
                taskLabel, taskField,
                answerLabel, answerField,
                solutionLabel, solutionField
        );

        return box;
    }

    /**
     * Creates a styled text area used inside variant cards.
     *
     * @param text initial text
     * @return configured text area
     */
    private TextArea createVariantTextArea(String text) {
        TextArea area = new TextArea(text);
        area.setWrapText(true);
        area.setPrefRowCount(3);
        area.setMaxWidth(Double.MAX_VALUE);
        area.getStyleClass().add("modern-textarea");
        return area;
    }

    /**
     * Creates an icon with the standard hover animation used in the editor.
     *
     * @param resourcePath path to the image resource
     * @param width icon width
     * @param height icon height
     * @return configured image view
     */
    private ImageView createHoverIcon(String resourcePath, double width, double height) {
        ImageView icon = new ImageView(new Image(getClass().getResourceAsStream(resourcePath)));
        icon.setFitWidth(width);
        icon.setFitHeight(height);
        icon.setPreserveRatio(true);
        icon.setCursor(Cursor.HAND);
        icon.setPickOnBounds(true);

        icon.setOnMouseEntered(e -> {
            icon.setOpacity(0.6);
            icon.setScaleX(1.1);
            icon.setScaleY(1.1);
        });

        icon.setOnMouseExited(e -> {
            icon.setOpacity(1.0);
            icon.setScaleX(1.0);
            icon.setScaleY(1.0);
        });

        return icon;
    }

    /**
     * Deletes a chapter from the current exam and refreshes the editor.
     *
     * @param chapter the chapter to delete
     */
    private void deleteChapter(Chapter chapter) {
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

        if (!newTitle.isEmpty()) {
            subtask.setTitle(newTitle);
            titleLabel.setText(newTitle);
            buildLeftOverview(currentExam);
        }

        titleField.setVisible(false);
        titleField.setManaged(false);
        titleLabel.setVisible(true);
        titleLabel.setManaged(true);
        AppState.setCurrentExam(currentExam);
    }

    /**
     * Sets the current exam and XML file and refreshes the editor view.
     *
     * @param exam the exam to display
     * @param xmlFile the XML file associated with the exam
     */
    public void setExamData(Exam exam, File xmlFile) {
        this.currentExam = exam;
        this.currentXmlFile = xmlFile;

        AppState.setCurrentExam(currentExam);
        AppState.setCurrentXmlFile(currentXmlFile);

        refreshWholeView();
    }

    /**
     * Rebuilds the complete editor view.
     */
    private void refreshWholeView() {
        chapterOverviewBox.getChildren().clear();
        editorContentBox.getChildren().clear();
        bottomActionBox.getChildren().clear();

        if (currentExam == null) {
            return;
        }

        buildLeftOverview(currentExam);
        showExamDetails();
    }

    /**
     * Creates the editor row for a chapter.
     *
     * @param chapter the chapter to display
     * @return the configured chapter editor row
     */
    private HBox createChapterRow(Chapter chapter) {
        Label warningLabel = new Label();
        warningLabel.getStyleClass().add("warning-label");
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
        titleLabel.getStyleClass().add("strong-label");

        TextField titleField = new TextField(chapter.getTitle());
        titleField.setPrefWidth(220);
        titleField.setVisible(false);
        titleField.setManaged(false);

        Label regularScoreLabel = new Label("Regular Score:");
        regularScoreLabel.setPrefWidth(80);

        ComboBox<Double> regularScoreBox = new ComboBox<>();
        regularScoreBox.setPrefWidth(110);
        List<Double> scores = scoreCalculator.calculatePossibleRegularScores(chapter);

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
        practiceScoreValueLabel.getStyleClass().add("score-value-label");

        Label appearanceLabel = new Label("Appearance:");
        appearanceLabel.setPrefWidth(70);

        ComboBox<ExamAppearance> appearanceBox = new ComboBox<>();
        appearanceBox.getItems().addAll(ExamAppearance.values());
        appearanceBox.setValue(chapter.getExamAppearance());
        appearanceBox.setPrefWidth(120);

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

        SVGPath editIcon = new SVGPath();
        editIcon.setContent("M4 16.5V20h3.5L17.8 9.7l-3.5-3.5L4 16.5ZM20.7 6.3a1 1 0 0 0 0-1.4l-1.6-1.6a1 1 0 0 0-1.4 0l-1.3 1.3 3.5 3.5 1.3-1.3Z");
        editIcon.getStyleClass().add("action-icon");
        editIcon.setScaleX(1.4);
        editIcon.setScaleY(1.4);
        editIcon.setMouseTransparent(true);

        StackPane editButton = new StackPane(editIcon);
        editButton.getStyleClass().add("icon-button");
        editButton.setPickOnBounds(true);
        Tooltip.install(editButton, new Tooltip("Rename chapter"));

        SVGPath deleteIcon = new SVGPath();
        deleteIcon.setContent("M8 6h8M10 6V4h4v2M7 6l1 12h8l1-12M11 9v6M13 9v6");
        deleteIcon.getStyleClass().add("delete-icon");
        deleteIcon.setScaleX(1.4);
        deleteIcon.setScaleY(1.4);
        deleteIcon.setMouseTransparent(true);

        StackPane deleteButton = new StackPane(deleteIcon);
        deleteButton.getStyleClass().add("icon-button");
        deleteButton.setPickOnBounds(true);
        Tooltip.install(deleteButton, new Tooltip("Delete Chapter"));

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

        editButton.setOnMouseClicked(e -> {
            e.consume();
            titleLabel.setVisible(false);
            titleLabel.setManaged(false);
            titleField.setVisible(true);
            titleField.setManaged(true);
            titleField.requestFocus();
            titleField.selectAll();
        });

        titleField.setOnAction(e -> saveChapterTitleChange(chapter, titleLabel, titleField));
        titleField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                saveChapterTitleChange(chapter, titleLabel, titleField);
            }
        });

        deleteButton.setOnMouseClicked(e -> {
            e.consume();

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Deletion");
            alert.setHeaderText("Delete Chapter?");
            alert.setContentText("Are you sure you want to delete this chapter? This action cannot be undone.");

            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                deleteChapter(chapter);
            }
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

        if (!newTitle.isEmpty()) {
            chapter.setTitle(newTitle);
            titleLabel.setText(newTitle);
            buildLeftOverview(currentExam);
        }

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

        if (!newTitle.isEmpty()) {
            exam.setTitle(newTitle);
            titleLabel.setText(newTitle);
            buildLeftOverview(currentExam);
        }

        titleField.setVisible(false);
        titleField.setManaged(false);
        titleLabel.setVisible(true);
        titleLabel.setManaged(true);
        AppState.setCurrentExam(currentExam);
    }

    /**
     * Displays the exam details inside the editor area.
     */
    private void showExamDetails() {
        editorContentBox.getChildren().clear();

        Label sectionTitle = new Label("Edit Exam");
        sectionTitle.getStyleClass().add("section-title");

        editorContentBox.getChildren().add(sectionTitle);
        editorContentBox.getChildren().add(createExamRow(currentExam));

        Label chaptersTitle = new Label("Chapters");
        chaptersTitle.getStyleClass().add("subsection-title");
        editorContentBox.getChildren().add(chaptersTitle);

        for (Chapter chapter : currentExam.getChapters()) {
            editorContentBox.getChildren().add(createChapterRow(chapter));
        }

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
     * @param chapter the chapter to display
     */
    private void showChapterDetails(Chapter chapter) {
        editorContentBox.getChildren().clear();

        Label sectionTitle = new Label("Edit Chapter");
        sectionTitle.getStyleClass().add("section-title");

        editorContentBox.getChildren().add(sectionTitle);
        editorContentBox.getChildren().add(createChapterRow(chapter));

        Label subtasksTitle = new Label("Subtasks");
        subtasksTitle.getStyleClass().add("subsection-title");
        editorContentBox.getChildren().add(subtasksTitle);

        for (Subtask subtask : chapter.getSubtasks()) {
            editorContentBox.getChildren().add(createSubtaskEditorRow(subtask, chapter));
        }

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
     */
    @FXML
    private void switchToPDF() {
        try {
            AppState.setCurrentExam(currentExam);
            AppState.setCurrentXmlFile(currentXmlFile);
            AppNavigator.showPdf();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}