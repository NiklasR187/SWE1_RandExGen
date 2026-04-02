package com.randexgen.swe1_randexgen.service;

import com.randexgen.swe1_randexgen.datamodel.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Exports generated exams into a PDF document using Apache PDFBox.
 *
 * This class is responsible for rendering the exam structure, task texts,
 * answer boxes, and optional solution content into a formatted PDF file.
 */
public class PDFExporter {

    private static final float MARGIN = 50;
    private static final float START_Y = 800;
    private static final float BOTTOM_MARGIN = 60;
    private static final float LINE_HEIGHT = 16;
    private static final float MAX_WIDTH = PDRectangle.A4.getWidth() - 2 * MARGIN;

    private static final PDFont FONT_REGULAR =
            new PDType1Font(Standard14Fonts.FontName.HELVETICA);
    private static final PDFont FONT_BOLD =
            new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);

    private static final int FONT_SIZE_REGULAR = 12;
    private static final int FONT_SIZE_BOLD = 13;
    private static final int FONT_SIZE_TITLE = 16;

    /**
     * Exports the given generated exam tasks to a PDF file.
     *
     * The method validates the input, creates a PDF document, writes the exam
     * header information, and renders all generated tasks including answer or solution boxes.
     *
     * @param exam the exam containing the general exam information
     * @param examType the type of the exam to export
     * @param generatedTasks the list of generated tasks to include in the PDF
     * @param outputFile the target file to which the PDF is saved
     * @param exportMode determines whether answers or solutions are exported
     * @throws IOException if an error occurs while writing the PDF file
     */
    public static void exportExam(Exam exam,
                                  ExamType examType,
                                  List<GeneratedTask> generatedTasks,
                                  File outputFile,
                                  ExportMode exportMode) throws IOException {

        // Validate all required input parameters before starting the export
        if (exam == null) {
            throw new IllegalArgumentException("Exam darf nicht null sein.");
        }

        if (examType == null) {
            throw new IllegalArgumentException("ExamType darf nicht null sein.");
        }

        if (generatedTasks == null || generatedTasks.isEmpty()) {
            throw new IllegalArgumentException("Es gibt keine generierten Aufgaben zum Exportieren.");
        }

        if (outputFile == null) {
            throw new IllegalArgumentException("Output-Datei darf nicht null sein.");
        }

        if (exportMode == null) {
            throw new IllegalArgumentException("ExportMode darf nicht null sein.");
        }

        // Create the PDF document and initialize the first page
        try (PDDocument document = new PDDocument()) {
            PageState state = createNewPage(document);

            // Write the document title and general export metadata
            String title = safeText(exam.getTitle(), "Generated Exam");
            state = writeLine(document, state, title, FONT_BOLD, FONT_SIZE_TITLE);
            state = writeEmptyLine(document, state);
            state = writeLine(document, state, "Exam Type: " + examType, FONT_REGULAR, FONT_SIZE_REGULAR);
            state = writeLine(document, state,
                    exportMode == ExportMode.SOLUTION ? "Document Type: Solution" : "Document Type: Exam",
                    FONT_REGULAR, FONT_SIZE_REGULAR);
            state = writeEmptyLine(document, state);
            state = writeEmptyLine(document, state);

            String lastChapterId = null;
            int taskNumber = 1;

            // Render all generated tasks in the order they were created
            for (GeneratedTask task : generatedTasks) {
                if (task == null) {
                    continue;
                }

                Chapter chapter = task.getChapter();
                Subtask subtask = task.getSubtask();
                Variant variant = task.getVariant();

                // Skip incomplete task entries that cannot be rendered properly
                if (chapter == null || subtask == null) {
                    continue;
                }

                String currentChapterId = chapter.getId();
                String currentChapterTitle = safeText(chapter.getTitle(), "Ohne Kapitel");

                // Detect whether a new chapter heading must be written
                boolean isNewChapter;
                if (lastChapterId == null) {
                    isNewChapter = true;
                } else if (currentChapterId != null) {
                    isNewChapter = !currentChapterId.equals(lastChapterId);
                } else {
                    isNewChapter = true;
                }

                // Ensure enough space so that the chapter heading and first task stay together
                if (isNewChapter) {
                    float chapterStartHeight = estimateChapterStartHeight(task, exportMode);
                    state = ensureSpace(document, state, chapterStartHeight);

                    state = writeEmptyLine(document, state);
                    state = writeLine(document, state, "Kapitel: " + currentChapterTitle, FONT_BOLD, FONT_SIZE_BOLD);
                    state = writeEmptyLine(document, state);

                    lastChapterId = currentChapterId;
                }

                // Build and write the task header with title and score
                String subtaskTitle = safeText(subtask.getTitle(), "Ohne Titel");
                String taskHeader = "Aufgabe " + taskNumber + ": " + subtaskTitle +
                        " (" + subtask.getScore() + " P)";

                state = writeLine(document, state, taskHeader, FONT_BOLD, FONT_SIZE_BOLD);

                // Write the actual task text if the selected variant contains one
                String taskText = safeText(variant != null ? variant.getTaskText() : "", "");
                if (!taskText.isBlank()) {
                    state = writeParagraph(document, state, taskText, FONT_REGULAR, FONT_SIZE_REGULAR);
                }

                state = writeEmptyLine(document, state);

                // Determine whether the answer box contains answer text or solution text
                String boxText = "";
                if (variant != null) {
                    if (exportMode == ExportMode.SOLUTION) {
                        boxText = safeText(variant.getSolutionText(), "");
                    } else {
                        boxText = safeText(variant.getAnswerText(), "");
                    }
                }

                // Draw the answer box and continue with the next task afterwards
                state = drawAnswerBox(document, state, boxText);
                state = writeEmptyLine(document, state);

                taskNumber++;
            }

            // Close the final page stream and save the document to disk
            closePageState(state);
            document.save(outputFile);
        }
    }

    /**
     * Creates a new PDF page and initializes its text content stream.
     *
     * The method sets the default font and starting position for subsequent
     * text output and returns the corresponding page state.
     *
     * @param document the PDF document to which the page is added
     * @return the initialized page state for the new page
     * @throws IOException if the page or content stream cannot be created
     */
    private static PageState createNewPage(PDDocument document) throws IOException {
        // Create a new A4 page and add it to the document
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        // Open a text content stream and place the cursor at the initial writing position
        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        contentStream.beginText();
        contentStream.setFont(FONT_REGULAR, FONT_SIZE_REGULAR);
        contentStream.newLineAtOffset(MARGIN, START_Y);

        return new PageState(page, contentStream, START_Y);
    }

    /**
     * Closes the content stream of the current page state.
     *
     * This method safely ends the current text section and closes the
     * underlying content stream if a valid page state exists.
     *
     * @param state the page state to close
     * @throws IOException if the content stream cannot be closed
     */
    private static void closePageState(PageState state) throws IOException {
        // Only close the stream if a valid page state is available
        if (state != null && state.contentStream != null) {
            state.contentStream.endText();
            state.contentStream.close();
        }
    }

    /**
     * Starts a new page after closing the previous page state.
     *
     * This method is used when the current page no longer has enough space
     * for the next content block.
     *
     * @param document the PDF document in which the new page is created
     * @param oldState the current page state that should be closed
     * @return the initialized state of the new page
     * @throws IOException if page handling fails
     */
    private static PageState startNewPage(PDDocument document, PageState oldState) throws IOException {
        // Finish the current page and continue writing on a fresh page
        closePageState(oldState);
        return createNewPage(document);
    }

    /**
     * Ensures that enough vertical space is available on the current page.
     *
     * If the required height would exceed the bottom margin, a new page is
     * started automatically and its state is returned.
     *
     * @param document the PDF document being written
     * @param state the current page state
     * @param neededHeight the vertical height required for the next content block
     * @return the current or newly created page state
     * @throws IOException if a new page must be created and fails
     */
    private static PageState ensureSpace(PDDocument document, PageState state, float neededHeight) throws IOException {
        // Start a new page if the remaining vertical space is insufficient
        if (state.y - neededHeight < BOTTOM_MARGIN) {
            return startNewPage(document, state);
        }
        return state;
    }

    /**
     * Writes a single line of text to the current page.
     *
     * The method ensures enough space, applies the given font settings,
     * writes the text, and updates the vertical writing position.
     *
     * @param document the PDF document being written
     * @param state the current page state
     * @param text the text to write
     * @param font the font used for rendering
     * @param fontSize the size of the font
     * @return the updated page state after writing the line
     * @throws IOException if writing to the content stream fails
     */
    private static PageState writeLine(PDDocument document,
                                       PageState state,
                                       String text,
                                       PDFont font,
                                       int fontSize) throws IOException {

        // Ensure that the next line still fits on the current page
        state = ensureSpace(document, state, LINE_HEIGHT);

        // Write the text line and move the cursor down by one line
        state.contentStream.setFont(font, fontSize);
        state.contentStream.showText(safeText(text, ""));
        state.contentStream.newLineAtOffset(0, -LINE_HEIGHT);
        state.y -= LINE_HEIGHT;

        return state;
    }

    /**
     * Writes an empty line to create vertical spacing.
     *
     * This method delegates to the normal line-writing logic using
     * an empty string and the default regular font.
     *
     * @param document the PDF document being written
     * @param state the current page state
     * @return the updated page state
     * @throws IOException if writing fails
     */
    private static PageState writeEmptyLine(PDDocument document, PageState state) throws IOException {
        return writeLine(document, state, "", FONT_REGULAR, FONT_SIZE_REGULAR);
    }

    /**
     * Writes a multi-line paragraph with automatic line wrapping.
     *
     * The text is first split into wrapped lines and then written line by line
     * using the normal line output logic.
     *
     * @param document the PDF document being written
     * @param state the current page state
     * @param text the paragraph text to write
     * @param font the font used for rendering
     * @param fontSize the font size used for rendering
     * @return the updated page state after writing the paragraph
     * @throws IOException if writing fails
     */
    private static PageState writeParagraph(PDDocument document,
                                            PageState state,
                                            String text,
                                            PDFont font,
                                            int fontSize) throws IOException {

        // Split the paragraph into lines that fit the configured maximum width
        List<String> lines = wrapText(text, font, fontSize, MAX_WIDTH);

        // Write all wrapped lines one after another
        for (String line : lines) {
            state = writeLine(document, state, line, font, fontSize);
        }

        return state;
    }

    /**
     * Wraps a text into multiple lines that fit within the given width.
     *
     * The method preserves paragraph breaks and builds new lines word by word
     * based on the measured text width of the selected font.
     *
     * @param text the text to wrap
     * @param font the font used for width calculation
     * @param fontSize the size of the font
     * @param maxWidth the maximum allowed line width
     * @return a list of wrapped lines
     * @throws IOException if the text width cannot be measured
     */
    private static List<String> wrapText(String text,
                                         PDFont font,
                                         int fontSize,
                                         float maxWidth) throws IOException {

        List<String> lines = new ArrayList<>();

        // Return a single empty line for empty or blank text input
        if (text == null || text.isBlank()) {
            lines.add("");
            return lines;
        }

        // Normalize line endings and split the text into logical paragraphs
        String normalizedText = text.replace("\r", "");
        String[] paragraphs = normalizedText.split("\n");

        for (String paragraph : paragraphs) {
            if (paragraph.isBlank()) {
                lines.add("");
                continue;
            }

            // Build each line word by word until the maximum width is reached
            String[] words = paragraph.trim().split("\\s+");
            StringBuilder currentLine = new StringBuilder();

            for (String word : words) {
                String testLine;

                if (currentLine.length() == 0) {
                    testLine = word;
                } else {
                    testLine = currentLine + " " + word;
                }

                // Measure the current test line to decide whether it still fits
                float textWidth = font.getStringWidth(testLine) / 1000f * fontSize;

                if (textWidth <= maxWidth) {
                    currentLine = new StringBuilder(testLine);
                } else {
                    // If the line would overflow, finalize the current line and start a new one
                    if (currentLine.length() > 0) {
                        lines.add(currentLine.toString());
                        currentLine = new StringBuilder(word);
                    } else {
                        lines.add(word);
                    }
                }
            }

            // Add the remaining text of the current paragraph as the last line
            if (currentLine.length() > 0) {
                lines.add(currentLine.toString());
            }
        }

        return lines;
    }

    /**
     * Draws an answer box and optionally writes text inside it.
     *
     * Depending on the export mode, the box may contain answer text or solution text.
     * The method also updates the page cursor position after the box is rendered.
     *
     * @param document the PDF document being written
     * @param state the current page state
     * @param boxText the text to display inside the answer box
     * @return the updated page state after drawing the box
     * @throws IOException if drawing or text output fails
     */
    private static PageState drawAnswerBox(PDDocument document,
                                           PageState state,
                                           String boxText) throws IOException {

        // Prepare the box text and calculate the required box dimensions
        String safeBoxText = safeText(boxText, "").trim();

        float boxHeight = estimateAnswerBoxHeight(safeBoxText);
        float padding = 10;
        float textWidth = MAX_WIDTH - 2 * padding;

        List<String> wrappedLines = wrapText(safeBoxText, FONT_REGULAR, FONT_SIZE_REGULAR, textWidth);

        // Ensure that the full box including spacing still fits on the page
        state = ensureSpace(document, state, boxHeight + 10);

        // End the current text block because drawing operations follow next
        state.contentStream.endText();

        float topY = state.y;
        float bottomY = topY - boxHeight;

        // Draw the rectangular answer box
        state.contentStream.addRect(MARGIN, bottomY, MAX_WIDTH, boxHeight);
        state.contentStream.stroke();

        // Write text inside the box if the box is not empty
        if (!safeBoxText.isBlank()) {
            state.contentStream.beginText();
            state.contentStream.setFont(FONT_REGULAR, FONT_SIZE_REGULAR);
            state.contentStream.newLineAtOffset(MARGIN + padding, topY - padding - FONT_SIZE_REGULAR);

            for (String line : wrappedLines) {
                state.contentStream.showText(line);
                state.contentStream.newLineAtOffset(0, -LINE_HEIGHT);
            }

            state.contentStream.endText();
        }

        // Start a new text section below the box for subsequent content
        state.contentStream.beginText();
        state.contentStream.setFont(FONT_REGULAR, FONT_SIZE_REGULAR);
        state.contentStream.newLineAtOffset(MARGIN, bottomY - 15);

        state.y = bottomY - 15;

        return state;
    }

    /**
     * Estimates the height required for an answer box.
     *
     * The method ensures a minimum height and increases it when the contained
     * text requires more vertical space after line wrapping.
     *
     * @param text the text that may be placed inside the answer box
     * @return the estimated height of the box
     * @throws IOException if text wrapping fails
     */
    private static float estimateAnswerBoxHeight(String text) throws IOException {
        String safeText = safeText(text, "").trim();

        float padding = 10;
        float minBoxHeight = 80;
        float textWidth = MAX_WIDTH - 2 * padding;

        // Return the minimum height for empty answer boxes
        if (safeText.isBlank()) {
            return minBoxHeight;
        }

        // Estimate the text height based on the number of wrapped lines
        List<String> wrappedLines = wrapText(safeText, FONT_REGULAR, FONT_SIZE_REGULAR, textWidth);
        float textHeight = wrappedLines.size() * LINE_HEIGHT;

        return Math.max(minBoxHeight, textHeight + 2 * padding);
    }

    /**
     * Estimates the height needed for starting a new chapter block.
     *
     * This is used to ensure that a chapter heading and the first task of that
     * chapter are not split awkwardly across two pages.
     *
     * @param task the first generated task of the chapter
     * @param exportMode determines whether answers or solutions are considered
     * @return the estimated vertical height required
     * @throws IOException if text measurement fails
     */
    private static float estimateChapterStartHeight(GeneratedTask task, ExportMode exportMode) throws IOException {
        float height = 0;

        // Reserve the fixed line heights for heading and spacing elements
        height += LINE_HEIGHT; // Leerzeile
        height += LINE_HEIGHT; // Kapitelüberschrift
        height += LINE_HEIGHT; // Leerzeile
        height += LINE_HEIGHT; // Aufgabenüberschrift

        Variant variant = task.getVariant();

        // Estimate the wrapped height of the task description text
        String taskText = safeText(variant != null ? variant.getTaskText() : "", "");
        List<String> wrappedTaskLines = wrapText(taskText, FONT_REGULAR, FONT_SIZE_REGULAR, MAX_WIDTH);
        height += wrappedTaskLines.size() * LINE_HEIGHT;

        // Determine which box content is relevant for height calculation
        String boxText = "";
        if (variant != null) {
            if (exportMode == ExportMode.SOLUTION) {
                boxText = safeText(variant.getSolutionText(), "");
            } else {
                boxText = safeText(variant.getAnswerText(), "");
            }
        }

        // Add space for the answer box and surrounding margins
        height += LINE_HEIGHT; // Abstand vor Box
        height += estimateAnswerBoxHeight(boxText);
        height += LINE_HEIGHT; // Abstand nach Box

        return height;
    }

    /**
     * Returns the given text or a fallback value if the text is null.
     *
     * This helper method prevents null values from being written directly
     * into the PDF output.
     *
     * @param text the text to check
     * @param fallback the fallback value if the text is null
     * @return the original text or the fallback value
     */
    private static String safeText(String text, String fallback) {
        if (text == null) {
            return fallback;
        }
        return text;
    }

    /**
     * Stores the current writing state of a PDF page.
     *
     * The page state keeps a reference to the current page, its content stream,
     * and the current vertical writing position.
     */
    private static class PageState {
        private final PDPage page;
        private final PDPageContentStream contentStream;
        private float y;

        /**
         * Creates a new page state for the current PDF page.
         *
         * @param page the current PDF page
         * @param contentStream the active content stream of the page
         * @param y the current vertical writing position
         */
        public PageState(PDPage page, PDPageContentStream contentStream, float y) {
            this.page = page;
            this.contentStream = contentStream;
            this.y = y;
        }
    }
}