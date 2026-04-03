package com.randexgen.swe1_randexgen.service;

import com.randexgen.swe1_randexgen.datamodel.Chapter;
import com.randexgen.swe1_randexgen.datamodel.DifficultyLevel;
import com.randexgen.swe1_randexgen.datamodel.Exam;
import com.randexgen.swe1_randexgen.datamodel.ExamAppearance;
import com.randexgen.swe1_randexgen.datamodel.ExamType;
import com.randexgen.swe1_randexgen.datamodel.Subtask;
import com.randexgen.swe1_randexgen.datamodel.Variant;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ExamGenerator}.
 *
 * This class verifies the main generation behavior for practice and regular exams.
 * The focus is on:
 * <ul>
 *     <li>handling invalid input</li>
 *     <li>ignoring excluded chapters</li>
 *     <li>aborting generation for invalid included chapters</li>
 *     <li>generating tasks for valid practice exams</li>
 *     <li>generating tasks for valid regular exams with matching target scores</li>
 * </ul>
 */
class ExamGeneratorTest {

    /**
     * Tests whether generation returns an empty list
     * if the exam object is null.
     */
    @Test
    void generateExam_shouldReturnEmptyList_whenExamIsNull() {
        // Act
        List<GeneratedTask> result = ExamGenerator.generateExam(null, ExamType.PRACTICE);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /**
     * Tests whether generation returns an empty list
     * if the exam type is null.
     */
    @Test
    void generateExam_shouldReturnEmptyList_whenExamTypeIsNull() {
        // Arrange
        Exam exam = new Exam();

        // Act
        List<GeneratedTask> result = ExamGenerator.generateExam(exam, null);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /**
     * Tests whether a valid practice exam is generated correctly.
     *
     * A valid included chapter with one usable practice subtask
     * per difficulty should lead to exactly three generated tasks.
     */
    @Test
    void generateExam_shouldGeneratePracticeExam_whenInputIsValid() {
        // Arrange
        Exam exam = new Exam();
        Chapter chapter = createValidPracticeChapter();
        exam.setChapters(new ArrayList<>(List.of(chapter)));

        // Act
        List<GeneratedTask> result = ExamGenerator.generateExam(exam, ExamType.PRACTICE);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());

        for (GeneratedTask task : result) {
            assertNotNull(task.getChapter());
            assertNotNull(task.getSubtask());
            assertNotNull(task.getVariant());
            assertEquals(chapter, task.getChapter());
            assertEquals(ExamType.PRACTICE, task.getSubtask().getExamType());
            assertNotNull(task.getVariant().getTaskText());
            assertFalse(task.getVariant().getTaskText().trim().isEmpty());
        }
    }

    /**
     * Tests whether excluded chapters are ignored during practice exam generation.
     *
     * Only tasks from included chapters should appear in the result.
     */
    @Test
    void generateExam_shouldIgnoreExcludedChapters_whenGeneratingPracticeExam() {
        // Arrange
        Exam exam = new Exam();

        Chapter includedChapter = createValidPracticeChapter();
        includedChapter.setId("included");

        Chapter excludedChapter = createValidPracticeChapter();
        excludedChapter.setId("excluded");
        excludedChapter.setExamAppearance(ExamAppearance.EXCLUDE);

        exam.setChapters(new ArrayList<>(List.of(includedChapter, excludedChapter)));

        // Act
        List<GeneratedTask> result = ExamGenerator.generateExam(exam, ExamType.PRACTICE);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());

        for (GeneratedTask task : result) {
            assertEquals("included", task.getChapter().getId());
        }
    }

    /**
     * Tests whether generation aborts with an empty list
     * if an included practice chapter is invalid.
     */
    @Test
    void generateExam_shouldReturnEmptyList_whenIncludedPracticeChapterIsInvalid() {
        // Arrange
        Exam exam = new Exam();

        Chapter invalidChapter = new Chapter();
        invalidChapter.setId("invalid");
        invalidChapter.setTitle("Invalid Chapter");
        invalidChapter.setExamAppearance(ExamAppearance.INCLUDE);
        invalidChapter.setSubtasks(new ArrayList<>());

        exam.setChapters(new ArrayList<>(List.of(invalidChapter)));

        // Act
        List<GeneratedTask> result = ExamGenerator.generateExam(exam, ExamType.PRACTICE);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /**
     * Tests whether a valid regular exam is generated correctly.
     *
     * A chapter with one valid regular subtask per difficulty and a matching
     * selected regular score should produce exactly three tasks.
     */
    @Test
    void generateExam_shouldGenerateRegularExam_whenInputIsValid() {
        // Arrange
        Exam exam = new Exam();
        Chapter chapter = createValidRegularChapter();
        exam.setChapters(new ArrayList<>(List.of(chapter)));

        // Act
        List<GeneratedTask> result = ExamGenerator.generateExam(exam, ExamType.REGULAR);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());

        double totalScore = 0;
        for (GeneratedTask task : result) {
            assertNotNull(task.getChapter());
            assertNotNull(task.getSubtask());
            assertNotNull(task.getVariant());
            assertEquals(chapter, task.getChapter());
            assertEquals(ExamType.REGULAR, task.getSubtask().getExamType());

            totalScore += task.getSubtask().getScore();
        }

        assertEquals(9.0, totalScore);
    }

    /**
     * Tests whether regular exam generation returns an empty list
     * if the selected regular score cannot be matched.
     */
    @Test
    void generateExam_shouldReturnEmptyList_whenRegularTargetScoreCannotBeReached() {
        // Arrange
        Exam exam = new Exam();
        Chapter chapter = createValidRegularChapter();

        // Set an impossible score for the given subtasks
        chapter.setSelectedRegularScore(100.0);

        exam.setChapters(new ArrayList<>(List.of(chapter)));

        // Act
        List<GeneratedTask> result = ExamGenerator.generateExam(exam, ExamType.REGULAR);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /**
     * Tests whether generation returns an empty list
     * if an included regular chapter is invalid.
     */
    @Test
    void generateExam_shouldReturnEmptyList_whenIncludedRegularChapterIsInvalid() {
        // Arrange
        Exam exam = new Exam();

        Chapter invalidChapter = new Chapter();
        invalidChapter.setId("invalid");
        invalidChapter.setTitle("Invalid Chapter");
        invalidChapter.setExamAppearance(ExamAppearance.INCLUDE);
        invalidChapter.setSelectedRegularScore(5.0);
        invalidChapter.setSubtasks(new ArrayList<>());

        exam.setChapters(new ArrayList<>(List.of(invalidChapter)));

        // Act
        List<GeneratedTask> result = ExamGenerator.generateExam(exam, ExamType.REGULAR);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /**
     * Creates a valid practice chapter with one usable subtask
     * for each difficulty level.
     *
     * @return a valid chapter for practice exam generation
     */
    private Chapter createValidPracticeChapter() {
        Chapter chapter = new Chapter();
        chapter.setId("practice-chapter");
        chapter.setTitle("Practice Chapter");
        chapter.setExamAppearance(ExamAppearance.INCLUDE);
        chapter.setSubtasks(new ArrayList<>());

        chapter.getSubtasks().add(createUsableSubtask("p1", 2.0, DifficultyLevel.EASY, ExamType.PRACTICE));
        chapter.getSubtasks().add(createUsableSubtask("p2", 3.0, DifficultyLevel.MEDIUM, ExamType.PRACTICE));
        chapter.getSubtasks().add(createUsableSubtask("p3", 4.0, DifficultyLevel.HARD, ExamType.PRACTICE));

        return chapter;
    }

    /**
     * Creates a valid regular chapter with one usable subtask
     * for each difficulty level and a matching selected regular score.
     *
     * @return a valid chapter for regular exam generation
     */
    private Chapter createValidRegularChapter() {
        Chapter chapter = new Chapter();
        chapter.setId("regular-chapter");
        chapter.setTitle("Regular Chapter");
        chapter.setExamAppearance(ExamAppearance.INCLUDE);
        chapter.setSelectedRegularScore(9.0);
        chapter.setSubtasks(new ArrayList<>());

        chapter.getSubtasks().add(createUsableSubtask("r1", 2.0, DifficultyLevel.EASY, ExamType.REGULAR));
        chapter.getSubtasks().add(createUsableSubtask("r2", 3.0, DifficultyLevel.MEDIUM, ExamType.REGULAR));
        chapter.getSubtasks().add(createUsableSubtask("r3", 4.0, DifficultyLevel.HARD, ExamType.REGULAR));

        return chapter;
    }

    /**
     * Creates a usable subtask with one valid variant.
     *
     * The created subtask contains the minimum required data
     * to be usable during exam generation.
     *
     * @param id the subtask ID
     * @param score the score of the subtask
     * @param difficulty the difficulty level
     * @param examType the exam type
     * @return a fully initialized usable subtask
     */
    private Subtask createUsableSubtask(String id, double score, DifficultyLevel difficulty, ExamType examType) {
        Subtask subtask = new Subtask();
        subtask.setId(id);
        subtask.setTitle("Subtask " + id);
        subtask.setScore(score);
        subtask.setDifficultyLevel(difficulty);
        subtask.setExamType(examType);

        Variant variant = new Variant();
        variant.setId("v-" + id);
        variant.setTaskText("Task text for " + id);
        variant.setAnswerText("Answer text");
        variant.setSolutionText("Solution text");

        subtask.setVariants(new ArrayList<>(List.of(variant)));

        return subtask;
    }
}