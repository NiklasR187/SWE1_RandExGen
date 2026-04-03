package com.randexgen.swe1_randexgen.service;

import com.randexgen.swe1_randexgen.datamodel.*;
import org.junit.jupiter.api.Test;

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

        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                PDFExporter.exportExam(exam, ExamType.REGULAR, new ArrayList<>(), null, ExportMode.EXAM)
        );

        assertEquals("Output-Datei darf nicht null sein.", ex.getMessage());
    }

    /**
     * Tests whether an exception is thrown when the export mode is null.
     */
    @Test
    void exportExam_shouldThrowException_whenExportModeIsNull() {
        Exam exam = new Exam();
        File file = new File("test.pdf");

        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                PDFExporter.exportExam(exam, ExamType.REGULAR, new ArrayList<>(), file, null)
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