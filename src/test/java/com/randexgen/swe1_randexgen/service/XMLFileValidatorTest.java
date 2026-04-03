package com.randexgen.swe1_randexgen.service;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link XMLFileValidator}.
 *
 * This class verifies that XML exam files are validated correctly.
 * It checks valid files, missing required elements, invalid score values,
 * and structural errors in the XML hierarchy.
 */
class XMLFileValidatorTest {

    /**
     * Tests whether a completely valid XML exam file passes validation
     * without throwing an exception.
     *
     * @throws Exception if the temporary file cannot be created or written
     */
    @Test
    void validate_shouldAcceptValidXmlFile() throws Exception {
        // Arrange: create a valid XML file with the required structure
        String xml = """
                <exam id="exam1">
                    <title>Test Exam</title>
                    <chapters>
                        <chapter id="c1">
                            <title>Chapter 1</title>
                            <examAppearance>INCLUDE</examAppearance>
                            <selectedRegularScore>10.5</selectedRegularScore>
                            <subtasks>
                                <subtask id="s1">
                                    <title>Subtask 1</title>
                                    <score>5.0</score>
                                    <difficultyLevel>EASY</difficultyLevel>
                                    <examType>REGULAR</examType>
                                    <variants>
                                        <variant id="v1">
                                            <taskText>Task 1</taskText>
                                            <answerText>Answer 1</answerText>
                                            <solutionText>Solution 1</solutionText>
                                        </variant>
                                    </variants>
                                </subtask>
                            </subtasks>
                        </chapter>
                    </chapters>
                </exam>
                """;

        File file = createTempXmlFile(xml);

        // Act + Assert: validation should succeed without exception
        assertDoesNotThrow(() -> XMLFileValidator.validate(file));
    }

    /**
     * Tests whether validation fails if the root element is not {@code <exam>}.
     *
     * @throws Exception if the temporary file cannot be created or written
     */
    @Test
    void validate_shouldThrowExceptionWhenRootElementIsInvalid() throws Exception {
        // Arrange: create XML with an invalid root element
        String xml = """
                <test>
                    <title>Wrong Root</title>
                    <chapters></chapters>
                </test>
                """;

        File file = createTempXmlFile(xml);

        // Act + Assert
        Exception exception = assertThrows(Exception.class, () -> XMLFileValidator.validate(file));
        assertEquals("Root element must be <exam>", exception.getMessage());
    }

    /**
     * Tests whether validation fails if a required direct child element
     * is missing inside the root exam element.
     *
     * @throws Exception if the temporary file cannot be created or written
     */
    @Test
    void validate_shouldThrowExceptionWhenExamTitleIsMissing() throws Exception {
        // Arrange: create XML without the required exam title
        String xml = """
                <exam id="exam1">
                    <chapters>
                        <chapter id="c1">
                            <title>Chapter 1</title>
                            <examAppearance>INCLUDE</examAppearance>
                            <selectedRegularScore>5.0</selectedRegularScore>
                            <subtasks></subtasks>
                        </chapter>
                    </chapters>
                </exam>
                """;

        File file = createTempXmlFile(xml);

        // Act + Assert
        Exception exception = assertThrows(Exception.class, () -> XMLFileValidator.validate(file));
        assertEquals("Missing <title> inside <exam>", exception.getMessage());
    }

    /**
     * Tests whether validation fails if a chapter contains an invalid score value
     * for selectedRegularScore.
     *
     * The validator only accepts whole or half-number values that are non-negative.
     *
     * @throws Exception if the temporary file cannot be created or written
     */
    @Test
    void validate_shouldThrowExceptionWhenSelectedRegularScoreIsInvalid() throws Exception {
        // Arrange: create XML with an invalid quarter-step score
        String xml = """
                <exam id="exam1">
                    <title>Test Exam</title>
                    <chapters>
                        <chapter id="c1">
                            <title>Chapter 1</title>
                            <examAppearance>INCLUDE</examAppearance>
                            <selectedRegularScore>1.3</selectedRegularScore>
                            <subtasks>
                                <subtask id="s1">
                                    <title>Subtask 1</title>
                                    <score>2.0</score>
                                    <difficultyLevel>EASY</difficultyLevel>
                                    <examType>REGULAR</examType>
                                    <variants>
                                        <variant id="v1">
                                            <taskText>Task</taskText>
                                            <answerText>Answer</answerText>
                                            <solutionText>Solution</solutionText>
                                        </variant>
                                    </variants>
                                </subtask>
                            </subtasks>
                        </chapter>
                    </chapters>
                </exam>
                """;

        File file = createTempXmlFile(xml);

        // Act + Assert
        Exception exception = assertThrows(Exception.class, () -> XMLFileValidator.validate(file));
        assertEquals("Chapter selectedRegularScore must be a whole or half number", exception.getMessage());
    }

    /**
     * Tests whether validation fails if a subtask score is negative.
     *
     * @throws Exception if the temporary file cannot be created or written
     */
    @Test
    void validate_shouldThrowExceptionWhenSubtaskScoreIsNegative() throws Exception {
        // Arrange: create XML with a negative subtask score
        String xml = """
                <exam id="exam1">
                    <title>Test Exam</title>
                    <chapters>
                        <chapter id="c1">
                            <title>Chapter 1</title>
                            <examAppearance>INCLUDE</examAppearance>
                            <selectedRegularScore>5.0</selectedRegularScore>
                            <subtasks>
                                <subtask id="s1">
                                    <title>Subtask 1</title>
                                    <score>-2.0</score>
                                    <difficultyLevel>MEDIUM</difficultyLevel>
                                    <examType>PRACTICE</examType>
                                    <variants>
                                        <variant id="v1">
                                            <taskText>Task</taskText>
                                            <answerText>Answer</answerText>
                                            <solutionText>Solution</solutionText>
                                        </variant>
                                    </variants>
                                </subtask>
                            </subtasks>
                        </chapter>
                    </chapters>
                </exam>
                """;

        File file = createTempXmlFile(xml);

        // Act + Assert
        Exception exception = assertThrows(Exception.class, () -> XMLFileValidator.validate(file));
        assertEquals("Subtask must be zero or positive", exception.getMessage());
    }

    /**
     * Tests whether validation fails if an invalid element is placed
     * inside the {@code <subtasks>} container.
     *
     * @throws Exception if the temporary file cannot be created or written
     */
    @Test
    void validate_shouldThrowExceptionWhenInvalidElementExistsInsideSubtasks() throws Exception {
        // Arrange: create XML with an invalid child element inside subtasks
        String xml = """
                <exam id="exam1">
                    <title>Test Exam</title>
                    <chapters>
                        <chapter id="c1">
                            <title>Chapter 1</title>
                            <examAppearance>INCLUDE</examAppearance>
                            <selectedRegularScore>5.0</selectedRegularScore>
                            <subtasks>
                                <wrongTag>
                                    <title>Invalid</title>
                                </wrongTag>
                            </subtasks>
                        </chapter>
                    </chapters>
                </exam>
                """;

        File file = createTempXmlFile(xml);

        // Act + Assert
        Exception exception = assertThrows(Exception.class, () -> XMLFileValidator.validate(file));
        assertEquals("Invalid element inside <subtasks>", exception.getMessage());
    }

    /**
     * Tests whether validation fails if a required field
     * inside a variant is missing.
     *
     * @throws Exception if the temporary file cannot be created or written
     */
    @Test
    void validate_shouldThrowExceptionWhenVariantSolutionTextIsMissing() throws Exception {
        // Arrange: create XML with a missing solutionText element
        String xml = """
                <exam id="exam1">
                    <title>Test Exam</title>
                    <chapters>
                        <chapter id="c1">
                            <title>Chapter 1</title>
                            <examAppearance>INCLUDE</examAppearance>
                            <selectedRegularScore>5.0</selectedRegularScore>
                            <subtasks>
                                <subtask id="s1">
                                    <title>Subtask 1</title>
                                    <score>2.5</score>
                                    <difficultyLevel>HARD</difficultyLevel>
                                    <examType>REGULAR</examType>
                                    <variants>
                                        <variant id="v1">
                                            <taskText>Task</taskText>
                                            <answerText>Answer</answerText>
                                        </variant>
                                    </variants>
                                </subtask>
                            </subtasks>
                        </chapter>
                    </chapters>
                </exam>
                """;

        File file = createTempXmlFile(xml);

        // Act + Assert
        Exception exception = assertThrows(Exception.class, () -> XMLFileValidator.validate(file));
        assertEquals("Missing <solutionText> inside <variant>", exception.getMessage());
    }

    /**
     * Creates a temporary XML file with the given content.
     *
     * The file is marked for deletion when the JVM exits.
     *
     * @param xmlContent the XML content to write into the file
     * @return the created temporary XML file
     * @throws Exception if the file cannot be created or written
     */
    private File createTempXmlFile(String xmlContent) throws Exception {
        // Create a temporary file with XML extension
        File tempFile = File.createTempFile("xml-validator-test", ".xml");
        tempFile.deleteOnExit();

        // Write the XML content into the file
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write(xmlContent);
        }

        return tempFile;
    }
}