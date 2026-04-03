package com.randexgen.swe1_randexgen.service;

import com.randexgen.swe1_randexgen.datamodel.Chapter;
import com.randexgen.swe1_randexgen.datamodel.DifficultyLevel;
import com.randexgen.swe1_randexgen.datamodel.Exam;
import com.randexgen.swe1_randexgen.datamodel.ExamAppearance;
import com.randexgen.swe1_randexgen.datamodel.ExamType;
import com.randexgen.swe1_randexgen.datamodel.Subtask;
import com.randexgen.swe1_randexgen.datamodel.Variant;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link XMLParser}.
 *
 * This class verifies that XML input is correctly parsed into the internal
 * exam data model. It tests valid XML structures, optional fields,
 * and invalid XML input.
 */
class XMLParserTest {

    /**
     * Tests whether a complete and valid XML exam is parsed correctly.
     *
     * This includes the exam itself, one chapter, one subtask,
     * and one variant with all relevant properties.
     *
     * @throws Exception if parsing unexpectedly fails
     */
    @Test
    void parse_shouldParseCompleteExamCorrectly() throws Exception {
        // Arrange: create a valid XML input with all important fields
        String xml = """
                <exam id="exam1">
                    <title>Test Exam</title>
                    <chapters>
                        <chapter id="c1">
                            <title>Chapter 1</title>
                            <examAppearance>INCLUDE</examAppearance>
                            <selectedRegularScore>12.5</selectedRegularScore>
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

        XMLParser parser = new XMLParser();
        InputStream inputStream = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));

        // Act: parse the XML
        Exam exam = parser.parse(inputStream);

        // Assert: verify exam data
        assertNotNull(exam);
        assertEquals("exam1", exam.getId());
        assertEquals("Test Exam", exam.getTitle());
        assertNotNull(exam.getChapters());
        assertEquals(1, exam.getChapters().size());

        // Assert: verify chapter data
        Chapter chapter = exam.getChapters().get(0);
        assertEquals("c1", chapter.getId());
        assertEquals("Chapter 1", chapter.getTitle());
        assertEquals(ExamAppearance.INCLUDE, chapter.getExamAppearance());
        assertEquals(12.5, chapter.getSelectedRegularScore());

        assertNotNull(chapter.getSubtasks());
        assertEquals(1, chapter.getSubtasks().size());

        // Assert: verify subtask data
        Subtask subtask = chapter.getSubtasks().get(0);
        assertEquals("s1", subtask.getId());
        assertEquals("Subtask 1", subtask.getTitle());
        assertEquals(5.0, subtask.getScore());
        assertEquals(DifficultyLevel.EASY, subtask.getDifficultyLevel());
        assertEquals(ExamType.REGULAR, subtask.getExamType());

        assertNotNull(subtask.getVariants());
        assertEquals(1, subtask.getVariants().size());

        // Assert: verify variant data
        Variant variant = subtask.getVariants().get(0);
        assertEquals("v1", variant.getId());
        assertEquals("Task 1", variant.getTaskText());
        assertEquals("Answer 1", variant.getAnswerText());
        assertEquals("Solution 1", variant.getSolutionText());
    }

    /**
     * Tests whether the parser handles a missing optional
     * selectedRegularScore tag correctly.
     *
     * The parsing should still succeed without throwing an exception.
     *
     * @throws Exception if parsing unexpectedly fails
     */
    @Test
    void parse_shouldUseDefaultValueWhenSelectedRegularScoreIsMissing() throws Exception {
        // Arrange: create XML without selectedRegularScore
        String xml = """
                <exam id="exam2">
                    <title>Exam Without Regular Score</title>
                    <chapters>
                        <chapter id="c1">
                            <title>Chapter 1</title>
                            <examAppearance>EXCLUDE</examAppearance>
                            <subtasks>
                                <subtask id="s1">
                                    <title>Subtask 1</title>
                                    <score>3.0</score>
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

        XMLParser parser = new XMLParser();
        InputStream inputStream = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));

        // Act
        Exam exam = parser.parse(inputStream);

        // Assert
        assertNotNull(exam);
        assertEquals(1, exam.getChapters().size());

        Chapter chapter = exam.getChapters().get(0);
        assertEquals("c1", chapter.getId());
        assertEquals(ExamAppearance.EXCLUDE, chapter.getExamAppearance());

        // Default value for a primitive double is usually 0.0
        assertEquals(0.0, chapter.getSelectedRegularScore());
    }

    /**
     * Tests whether invalid XML input causes the parser to throw an exception.
     *
     * @throws Exception not thrown directly by the test method,
     *                   but checked via assertThrows
     */
    @Test
    void parse_shouldThrowExceptionForInvalidXml() {
        // Arrange: create broken XML with missing closing tags
        String invalidXml = """
                <exam id="exam1">
                    <title>Broken Exam</title>
                    <chapters>
                        <chapter id="c1">
                            <title>Chapter 1</title>
                    </chapters>
                </exam>
                """;

        XMLParser parser = new XMLParser();
        InputStream inputStream = new ByteArrayInputStream(invalidXml.getBytes(StandardCharsets.UTF_8));

        // Act + Assert
        assertThrows(Exception.class, () -> parser.parse(inputStream));
    }

    /**
     * Tests whether multiple variants inside one subtask
     * are parsed completely and in the correct order.
     *
     * @throws Exception if parsing unexpectedly fails
     */
    @Test
    void parse_shouldParseMultipleVariantsCorrectly() throws Exception {
        // Arrange: create XML with two variants in one subtask
        String xml = """
                <exam id="exam3">
                    <title>Variant Test</title>
                    <chapters>
                        <chapter id="c1">
                            <title>Chapter 1</title>
                            <examAppearance>INCLUDE</examAppearance>
                            <subtasks>
                                <subtask id="s1">
                                    <title>Subtask 1</title>
                                    <score>4.0</score>
                                    <difficultyLevel>HARD</difficultyLevel>
                                    <examType>REGULAR</examType>
                                    <variants>
                                        <variant id="v1">
                                            <taskText>Task 1</taskText>
                                            <answerText>Answer 1</answerText>
                                            <solutionText>Solution 1</solutionText>
                                        </variant>
                                        <variant id="v2">
                                            <taskText>Task 2</taskText>
                                            <answerText>Answer 2</answerText>
                                            <solutionText>Solution 2</solutionText>
                                        </variant>
                                    </variants>
                                </subtask>
                            </subtasks>
                        </chapter>
                    </chapters>
                </exam>
                """;

        XMLParser parser = new XMLParser();
        InputStream inputStream = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));

        // Act
        Exam exam = parser.parse(inputStream);

        // Assert
        Subtask subtask = exam.getChapters().get(0).getSubtasks().get(0);
        assertEquals(2, subtask.getVariants().size());

        assertEquals("v1", subtask.getVariants().get(0).getId());
        assertEquals("v2", subtask.getVariants().get(1).getId());
    }
}