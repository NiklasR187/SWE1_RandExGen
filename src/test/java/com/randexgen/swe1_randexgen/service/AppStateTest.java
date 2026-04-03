package com.randexgen.swe1_randexgen.service;

import com.randexgen.swe1_randexgen.datamodel.Exam;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link AppState}.
 *
 * Verifies correct storing, retrieving, and resetting of global state.
 */
class AppStateTest {

    /**
     * Tests whether setting and getting the current exam works correctly.
     */
    @Test
    void setAndGetCurrentExam_shouldWorkCorrectly() {
        // Arrange
        Exam exam = new Exam();

        // Act
        AppState.setCurrentExam(exam);

        // Assert
        assertSame(exam, AppState.getCurrentExam());
    }

    /**
     * Tests whether setting and getting the XML file works correctly.
     */
    @Test
    void setAndGetCurrentXmlFile_shouldWorkCorrectly() {
        // Arrange
        File file = new File("test.xml");

        // Act
        AppState.setCurrentXmlFile(file);

        // Assert
        assertSame(file, AppState.getCurrentXmlFile());
    }

    /**
     * Tests whether reset clears both stored values.
     */
    @Test
    void reset_shouldClearState() {
        // Arrange
        AppState.setCurrentExam(new Exam());
        AppState.setCurrentXmlFile(new File("test.xml"));

        // Act
        AppState.reset();

        // Assert
        assertNull(AppState.getCurrentExam());
        assertNull(AppState.getCurrentXmlFile());
    }
}