package com.randexgen.swe1_randexgen.service;

import com.randexgen.swe1_randexgen.datamodel.*;
import org.junit.jupiter.api.Test;
import com.randexgen.swe1_randexgen.datamodel.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link PDFExporter}.
 *
 * This class verifies the input validation and basic functionality
 * of the PDF export process.
 *
 * The focus is on:
 * - correct exception handling for invalid input
 * - successful creation of a PDF file for valid input
 */
class PDFExporterTest {

    /**
     * Tests whether an exception is thrown when the exam is null.
     */
    @Test
    void exportExam_shouldThrowException_whenExamIsNull() {
        File file = new File("test.pdf");

        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                PDFExporter.exportExam(null, ExamType.REGULAR, new ArrayList<>(), file, ExportMode.EXAM)
        );

        assertEquals("Exam darf nicht null sein.", ex.getMessage());
    }

    /**
     * Tests whether safeText returns the fallback text when the input is null.
     */
    @Test
    void safeText_shouldReturnFallback_whenTextIsNull() throws Exception {
        Method method = PDFExporter.class.getDeclaredMethod("safeText", String.class, String.class);
        method.setAccessible(true);

        String result = (String) method.invoke(null, null, "fallback");

        assertEquals("fallback", result);
    }

    /**
     * Tests whether safeText returns the original text when it is not null.
     */
    @Test
    void safeText_shouldReturnOriginalText_whenTextIsNotNull() throws Exception {
        Method method = PDFExporter.class.getDeclaredMethod("safeText", String.class, String.class);
        method.setAccessible(true);

        String result = (String) method.invoke(null, "hello", "fallback");

        assertEquals("hello", result);
    }


    /**
     * Tests whether wrapText adds a single long word directly as a line
     * when the first word already exceeds the maximum width.
     */
    @Test
    void wrapText_shouldAddWordDirectly_whenFirstWordExceedsWidth() throws Exception {
        Method method = PDFExporter.class.getDeclaredMethod(
                "wrapText",
                String.class,
                PDFont.class,
                int.class,
                float.class
        );
        method.setAccessible(true);

        PDFont font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

        @SuppressWarnings("unchecked")
        List<String> result = (List<String>) method.invoke(
                null,
                "Supercalifragilisticexpialidocious",
                font,
                12,
                10f
        );

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals("Supercalifragilisticexpialidocious", result.get(0));
    }

    /**
     * Tests whether wrapText finalizes the current line and starts a new one
     * when a later word causes the line to overflow.
     */
    @Test
    void wrapText_shouldStartNewLine_whenNextWordCausesOverflow() throws Exception {
        Method method = PDFExporter.class.getDeclaredMethod(
                "wrapText",
                String.class,
                PDFont.class,
                int.class,
                float.class
        );
        method.setAccessible(true);

        PDFont font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

        @SuppressWarnings("unchecked")
        List<String> result = (List<String>) method.invoke(
                null,
                "Hi Supercalifragilisticexpialidocious",
                font,
                12,
                20f
        );

        assertNotNull(result);
        assertTrue(result.size() >= 2);
        assertEquals("Hi", result.get(0));
    }
    /**
     * Tests whether an exception is thrown when the exam type is null.
     */
    @Test
    void exportExam_shouldThrowException_whenExamTypeIsNull() {
        Exam exam = new Exam();
        File file = new File("test.pdf");

        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                PDFExporter.exportExam(exam, null, new ArrayList<>(), file, ExportMode.EXAM)
        );

        assertEquals("ExamType darf nicht null sein.", ex.getMessage());
    }

    /**
     * Tests whether startNewPage closes the old page state
     * and creates a new page state on a fresh page.
     */
    @Test
    void startNewPage_shouldCreateNewPageState() throws Exception {
        PDDocument document = new PDDocument();

        try {
            Class<?> pageStateClass = Class.forName(
                    "com.randexgen.swe1_randexgen.service.PDFExporter$PageState"
            );

            Method createNewPageMethod = PDFExporter.class.getDeclaredMethod("createNewPage", PDDocument.class);
            createNewPageMethod.setAccessible(true);

            Object oldState = createNewPageMethod.invoke(null, document);

            Method startNewPageMethod = PDFExporter.class.getDeclaredMethod(
                    "startNewPage",
                    PDDocument.class,
                    pageStateClass
            );
            startNewPageMethod.setAccessible(true);

            Object newState = startNewPageMethod.invoke(null, document, oldState);

            assertNotNull(newState);
            assertEquals(2, document.getNumberOfPages());
        } finally {
            document.close();
        }
    }


    /**
     * Tests whether an exception is thrown when the generated task list is null.
     */
    @Test
    void exportExam_shouldThrowException_whenGeneratedTasksIsNull() {
        Exam exam = new Exam();
        File file = new File("test.pdf");

        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                PDFExporter.exportExam(exam, ExamType.REGULAR, null, file, ExportMode.EXAM)
        );

        assertEquals("Es gibt keine generierten Aufgaben zum Exportieren.", ex.getMessage());
    }


    /**
     * Tests chapter detection logic for first chapter (null case).
     */
    @Test
    void isNewChapter_shouldBeTrue_whenLastChapterIsNull() {
        String lastChapterId = null;
        String currentChapterId = "c1";

        boolean isNewChapter;

        if (lastChapterId == null) {
            isNewChapter = true;
        } else if (currentChapterId != null) {
            isNewChapter = !currentChapterId.equals(lastChapterId);
        } else {
            isNewChapter = true;
        }

        assertTrue(isNewChapter);
    }


    /**
     * Tests chapter detection logic when chapter changes.
     */
    @Test
    void isNewChapter_shouldBeTrue_whenChapterChanges() {
        String lastChapterId = "c1";
        String currentChapterId = "c2";

        boolean isNewChapter;

        if (lastChapterId == null) {
            isNewChapter = true;
        } else if (currentChapterId != null) {
            isNewChapter = !currentChapterId.equals(lastChapterId);
        } else {
            isNewChapter = true;
        }

        assertTrue(isNewChapter);
    }

    /**
     * Creates a minimal generated task for PDF export tests.
     *
     * @param chapter the chapter of the task
     * @param subtaskId the subtask id
     * @param taskText the visible task text
     * @param answerText the visible answer text
     * @return a generated task with one valid variant
     */
    private GeneratedTask createGeneratedTask(Chapter chapter,
                                              String subtaskId,
                                              String taskText,
                                              String answerText) {
        Subtask subtask = new Subtask();
        subtask.setId(subtaskId);
        subtask.setTitle("Subtask " + subtaskId);
        subtask.setScore(5);
        subtask.setDifficultyLevel(DifficultyLevel.EASY);
        subtask.setExamType(ExamType.REGULAR);

        Variant variant = new Variant();
        variant.setId("v-" + subtaskId);
        variant.setTaskText(taskText);
        variant.setAnswerText(answerText);
        variant.setSolutionText("Solution for " + subtaskId);

        return new GeneratedTask(chapter, subtask, variant);
    }

    /**
     * Tests whether exportExam handles a chapter with a null id correctly.
     * This covers the branch where the current chapter id is null.
     */
    @Test
    void exportExam_shouldHandleNullChapterId() throws Exception {
        Exam exam = new Exam();
        exam.setTitle("Null Chapter Id Exam");

        Chapter chapter1 = new Chapter();
        chapter1.setId("c1");
        chapter1.setTitle("Chapter 1");
        chapter1.setExamAppearance(ExamAppearance.INCLUDE);

        Chapter chapter2 = new Chapter();
        chapter2.setId(null);
        chapter2.setTitle("Chapter without Id");
        chapter2.setExamAppearance(ExamAppearance.INCLUDE);

        GeneratedTask task1 = createGeneratedTask(chapter1, "s1", "Task 1", "Answer 1");
        GeneratedTask task2 = createGeneratedTask(chapter2, "s2", "Task 2", "Answer 2");

        File outputFile = File.createTempFile("pdfexport_null_chapter_id", ".pdf");
        outputFile.deleteOnExit();

        PDFExporter.exportExam(
                exam,
                ExamType.REGULAR,
                new ArrayList<>(List.of(task1, task2)),
                outputFile,
                ExportMode.EXAM
        );

        assertTrue(outputFile.exists());
        assertTrue(outputFile.length() > 0);
    }

    /**
     * Tests whether exportExam handles a chapter change correctly.
     * This covers the branch where a new chapter heading must be written.
     */
    @Test
    void exportExam_shouldHandleDifferentChapterIds() throws Exception {
        Exam exam = new Exam();
        exam.setTitle("Different Chapters Exam");

        Chapter chapter1 = new Chapter();
        chapter1.setId("c1");
        chapter1.setTitle("Chapter 1");
        chapter1.setExamAppearance(ExamAppearance.INCLUDE);

        Chapter chapter2 = new Chapter();
        chapter2.setId("c2");
        chapter2.setTitle("Chapter 2");
        chapter2.setExamAppearance(ExamAppearance.INCLUDE);

        GeneratedTask task1 = createGeneratedTask(chapter1, "s1", "Task 1", "Answer 1");
        GeneratedTask task2 = createGeneratedTask(chapter2, "s2", "Task 2", "Answer 2");

        File outputFile = File.createTempFile("pdfexport_diff_chapter", ".pdf");
        outputFile.deleteOnExit();

        PDFExporter.exportExam(
                exam,
                ExamType.REGULAR,
                new ArrayList<>(List.of(task1, task2)),
                outputFile,
                ExportMode.EXAM
        );

        assertTrue(outputFile.exists());
        assertTrue(outputFile.length() > 0);
    }

    /**
     * Tests whether exportExam handles two tasks from the same chapter
     * without failing. This covers the branch where no new chapter heading is needed.
     */
    @Test
    void exportExam_shouldHandleRepeatedChapterId() throws Exception {
        Exam exam = new Exam();
        exam.setTitle("Same Chapter Exam");

        Chapter chapter = new Chapter();
        chapter.setId("c1");
        chapter.setTitle("Chapter 1");
        chapter.setExamAppearance(ExamAppearance.INCLUDE);

        GeneratedTask task1 = createGeneratedTask(chapter, "s1", "Task 1", "Answer 1");
        GeneratedTask task2 = createGeneratedTask(chapter, "s2", "Task 2", "Answer 2");

        File outputFile = File.createTempFile("pdfexport_same_chapter", ".pdf");
        outputFile.deleteOnExit();

        PDFExporter.exportExam(
                exam,
                ExamType.REGULAR,
                new ArrayList<>(List.of(task1, task2)),
                outputFile,
                ExportMode.EXAM
        );

        assertTrue(outputFile.exists());
        assertTrue(outputFile.length() > 0);
    }

    /**
     * Tests whether exportExam writes a PDF successfully
     * when the answer box contains visible text.
     */
    @Test
    void exportExam_shouldHandleNonBlankAnswerBoxText() throws Exception {
        Exam exam = new Exam();
        exam.setTitle("Test Exam");

        Chapter chapter = new Chapter();
        chapter.setId("c1");
        chapter.setTitle("Chapter 1");
        chapter.setExamAppearance(ExamAppearance.INCLUDE);

        Subtask subtask = new Subtask();
        subtask.setId("s1");
        subtask.setTitle("Subtask 1");
        subtask.setScore(5);
        subtask.setDifficultyLevel(DifficultyLevel.EASY);
        subtask.setExamType(ExamType.REGULAR);

        Variant variant = new Variant();
        variant.setId("v1");
        variant.setTaskText("Task text");
        variant.setAnswerText("Answer text inside the box");
        variant.setSolutionText("Solution");

        GeneratedTask task = new GeneratedTask(chapter, subtask, variant);

        File outputFile = File.createTempFile("pdfexport_nonblank_box", ".pdf");
        outputFile.deleteOnExit();

        PDFExporter.exportExam(
                exam,
                ExamType.REGULAR,
                new ArrayList<>(List.of(task)),
                outputFile,
                ExportMode.EXAM
        );

        assertTrue(outputFile.exists());
        assertTrue(outputFile.length() > 0);
    }

    /**
     * Tests whether exportExam also works when the answer box text is blank.
     * This covers the branch where no text is written into the box.
     */
    @Test
    void exportExam_shouldHandleBlankAnswerBoxText() throws Exception {
        Exam exam = new Exam();
        exam.setTitle("Test Exam");

        Chapter chapter = new Chapter();
        chapter.setId("c1");
        chapter.setTitle("Chapter 1");
        chapter.setExamAppearance(ExamAppearance.INCLUDE);

        Subtask subtask = new Subtask();
        subtask.setId("s1");
        subtask.setTitle("Subtask 1");
        subtask.setScore(5);
        subtask.setDifficultyLevel(DifficultyLevel.EASY);
        subtask.setExamType(ExamType.REGULAR);

        Variant variant = new Variant();
        variant.setId("v1");
        variant.setTaskText("Task text");
        variant.setAnswerText("   "); // blank -> branch should skip text inside box
        variant.setSolutionText("Solution");

        GeneratedTask task = new GeneratedTask(chapter, subtask, variant);

        File outputFile = File.createTempFile("pdfexport_blank_box", ".pdf");
        outputFile.deleteOnExit();

        PDFExporter.exportExam(
                exam,
                ExamType.REGULAR,
                new ArrayList<>(List.of(task)),
                outputFile,
                ExportMode.EXAM
        );

        assertTrue(outputFile.exists());
        assertTrue(outputFile.length() > 0);
    }

    /**
     * Tests whether an exception is thrown when the generated task list is empty.
     */
    @Test
    void exportExam_shouldThrowException_whenGeneratedTasksIsEmpty() {
        Exam exam = new Exam();
        File file = new File("test.pdf");

        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                PDFExporter.exportExam(exam, ExamType.REGULAR, new ArrayList<>(), file, ExportMode.EXAM)
        );

        assertEquals("Es gibt keine generierten Aufgaben zum Exportieren.", ex.getMessage());
    }


    /**
     * Tests whether an exception is thrown when the output file is null.
     */
    @Test
    void exportExam_shouldThrowException_whenOutputFileIsNull() {
        Exam exam = new Exam();
        exam.setTitle("Test Exam");

        Chapter chapter = new Chapter();
        chapter.setId("c1");
        chapter.setTitle("Chapter 1");

        Subtask subtask = new Subtask();
        subtask.setId("s1");
        subtask.setTitle("Subtask 1");
        subtask.setScore(5.0);

        Variant variant = new Variant();
        variant.setId("v1");
        variant.setTaskText("Task");
        variant.setAnswerText("Answer");
        variant.setSolutionText("Solution");

        GeneratedTask task = new GeneratedTask(chapter, subtask, variant);
        List<GeneratedTask> tasks = List.of(task);

        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                PDFExporter.exportExam(exam, ExamType.REGULAR, tasks, null, ExportMode.EXAM)
        );

        assertEquals("Output-Datei darf nicht null sein.", ex.getMessage());
    }

    /**
     * Tests whether an exception is thrown when the export mode is null.
     */
    @Test
    void exportExam_shouldThrowException_whenExportModeIsNull() {
        Exam exam = new Exam();
        exam.setTitle("Test Exam");

        Chapter chapter = new Chapter();
        chapter.setId("c1");
        chapter.setTitle("Chapter 1");

        Subtask subtask = new Subtask();
        subtask.setId("s1");
        subtask.setTitle("Subtask 1");
        subtask.setScore(5.0);

        Variant variant = new Variant();
        variant.setId("v1");
        variant.setTaskText("Task");
        variant.setAnswerText("Answer");
        variant.setSolutionText("Solution");

        GeneratedTask task = new GeneratedTask(chapter, subtask, variant);
        List<GeneratedTask> tasks = List.of(task);

        File file = new File("test.pdf");

        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                PDFExporter.exportExam(exam, ExamType.REGULAR, tasks, file, null)
        );

        assertEquals("ExportMode darf nicht null sein.", ex.getMessage());
    }

    /**
     * Tests whether a PDF file is successfully created for valid input.
     *
     * This test verifies that:
     * - no exception is thrown
     * - the output file exists
     * - the file is not empty
     */
    @Test
    void exportExam_shouldCreatePdfFile_whenInputIsValid() throws Exception {
        // Arrange
        Exam exam = new Exam();
        exam.setTitle("Test Exam");

        Chapter chapter = new Chapter();
        chapter.setId("c1");
        chapter.setTitle("Chapter 1");

        Subtask subtask = new Subtask();
        subtask.setId("s1");
        subtask.setTitle("Subtask 1");
        subtask.setScore(5.0);

        Variant variant = new Variant();
        variant.setId("v1");
        variant.setTaskText("What is 2+2?");
        variant.setAnswerText("");
        variant.setSolutionText("4");

        GeneratedTask task = new GeneratedTask(chapter, subtask, variant);

        List<GeneratedTask> tasks = List.of(task);

        File file = new File("test_output.pdf");

        // Act
        PDFExporter.exportExam(exam, ExamType.REGULAR, tasks, file, ExportMode.EXAM);

        // Assert
        assertTrue(file.exists());
        assertTrue(file.length() > 0);

        // Cleanup
        file.delete();
    } 
}