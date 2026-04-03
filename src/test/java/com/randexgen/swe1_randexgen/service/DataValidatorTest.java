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
 * Test class for {@link DataValidator}.
 *
 * This class verifies the validation logic for subtasks, chapters, and exams.
 * It checks valid and invalid cases for usability, chapter validity,
 * exam validity, chapter counting, and warning texts.
 */
class DataValidatorTest {

    /**
     * Tests whether a subtask is usable when it contains at least one variant
     * with non-empty task text.
     */
    @Test
    void isSubtaskUsable_shouldReturnTrue_whenVariantContainsTaskText() {
        // Arrange
        Subtask subtask = new Subtask();
        Variant variant = new Variant();
        variant.setTaskText("Solve the equation");

        subtask.setVariants(new ArrayList<>(List.of(variant)));

        // Act
        boolean result = DataValidator.isSubtaskUsable(subtask);

        // Assert
        assertTrue(result);
    }

    /**
     * Tests whether a subtask is unusable when it has no variants.
     */
    @Test
    void isSubtaskUsable_shouldReturnFalse_whenVariantsAreMissing() {
        // Arrange
        Subtask subtask = new Subtask();
        subtask.setVariants(new ArrayList<>());

        // Act
        boolean result = DataValidator.isSubtaskUsable(subtask);

        // Assert
        assertFalse(result);
    }

    /**
     * Tests whether a subtask is unusable when all variants contain only blank task text.
     */
    @Test
    void isSubtaskUsable_shouldReturnFalse_whenAllTaskTextsAreBlank() {
        // Arrange
        Subtask subtask = new Subtask();

        Variant variant1 = new Variant();
        variant1.setTaskText("   ");

        Variant variant2 = new Variant();
        variant2.setTaskText(null);

        subtask.setVariants(new ArrayList<>(List.of(variant1, variant2)));

        // Act
        boolean result = DataValidator.isSubtaskUsable(subtask);

        // Assert
        assertFalse(result);
    }

    /**
     * Tests whether valid subtasks are correctly filtered by difficulty and exam type.
     */
    @Test
    void getValidSubtasksByDifficulty_shouldReturnOnlyUsableMatchingSubtasks() {
        // Arrange
        Chapter chapter = new Chapter();
        chapter.setSubtasks(new ArrayList<>());

        Subtask validEasyPractice = createUsableSubtask("s1", 2.0, DifficultyLevel.EASY, ExamType.PRACTICE);
        Subtask invalidEasyPractice = createUnusableSubtask("s2", 2.0, DifficultyLevel.EASY, ExamType.PRACTICE);
        Subtask mediumPractice = createUsableSubtask("s3", 3.0, DifficultyLevel.MEDIUM, ExamType.PRACTICE);

        chapter.getSubtasks().add(validEasyPractice);
        chapter.getSubtasks().add(invalidEasyPractice);
        chapter.getSubtasks().add(mediumPractice);

        // Act
        List<Subtask> result = DataValidator.getValidSubtasksByDifficulty(
                chapter, ExamType.PRACTICE, DifficultyLevel.EASY
        );

        // Assert
        assertEquals(1, result.size());
        assertEquals("s1", result.get(0).getId());
    }

    /**
     * Tests whether a chapter is valid when it has at least one usable subtask
     * for each difficulty level and a balanced distribution.
     */
    @Test
    void isChapterValid_shouldReturnTrue_forValidChapter() {
        // Arrange
        Chapter chapter = createValidChapterForType(ExamType.PRACTICE);

        // Act
        boolean result = DataValidator.isChapterValid(chapter, ExamType.PRACTICE);

        // Assert
        assertTrue(result);
    }

    /**
     * Tests whether a chapter is invalid when one difficulty level is missing.
     */
    @Test
    void isChapterValid_shouldReturnFalse_whenOneDifficultyIsMissing() {
        // Arrange
        Chapter chapter = new Chapter();
        chapter.setSubtasks(new ArrayList<>());
        chapter.getSubtasks().add(createUsableSubtask("s1", 2.0, DifficultyLevel.EASY, ExamType.PRACTICE));
        chapter.getSubtasks().add(createUsableSubtask("s2", 3.0, DifficultyLevel.MEDIUM, ExamType.PRACTICE));

        // Act
        boolean result = DataValidator.isChapterValid(chapter, ExamType.PRACTICE);

        // Assert
        assertFalse(result);
    }

    /**
     * Tests whether a chapter is invalid when the difficulty distribution is unbalanced.
     */
    @Test
    void isChapterValid_shouldReturnFalse_whenDistributionIsInvalid() {
        // Arrange
        Chapter chapter = new Chapter();
        chapter.setSubtasks(new ArrayList<>());

        chapter.getSubtasks().add(createUsableSubtask("e1", 1.0, DifficultyLevel.EASY, ExamType.REGULAR));
        chapter.getSubtasks().add(createUsableSubtask("e2", 1.0, DifficultyLevel.EASY, ExamType.REGULAR));
        chapter.getSubtasks().add(createUsableSubtask("e3", 1.0, DifficultyLevel.EASY, ExamType.REGULAR));
        chapter.getSubtasks().add(createUsableSubtask("e4", 1.0, DifficultyLevel.EASY, ExamType.REGULAR));

        chapter.getSubtasks().add(createUsableSubtask("m1", 1.0, DifficultyLevel.MEDIUM, ExamType.REGULAR));
        chapter.getSubtasks().add(createUsableSubtask("h1", 1.0, DifficultyLevel.HARD, ExamType.REGULAR));

        // easy=4, medium=1, hard=1 -> invalid because max-min = 3
        // Act
        boolean result = DataValidator.isChapterValid(chapter, ExamType.REGULAR);

        // Assert
        assertFalse(result);
    }

    /**
     * Tests whether an exam is valid when it has at least four included valid chapters.
     */
    @Test
    void isExamValid_shouldReturnTrue_whenAtLeastFourIncludedChaptersAreValid() {
        // Arrange
        Exam exam = new Exam();
        exam.setChapters(new ArrayList<>(List.of(
                createIncludedValidChapter(ExamType.PRACTICE),
                createIncludedValidChapter(ExamType.PRACTICE),
                createIncludedValidChapter(ExamType.PRACTICE),
                createIncludedValidChapter(ExamType.PRACTICE)
        )));

        // Act
        boolean result = DataValidator.isExamValid(exam, ExamType.PRACTICE);

        // Assert
        assertTrue(result);
    }

    /**
     * Tests whether an exam is invalid when fewer than four chapters are included.
     */
    @Test
    void isExamValid_shouldReturnFalse_whenLessThanFourIncludedChaptersExist() {
        // Arrange
        Exam exam = new Exam();
        exam.setChapters(new ArrayList<>(List.of(
                createIncludedValidChapter(ExamType.REGULAR),
                createIncludedValidChapter(ExamType.REGULAR),
                createIncludedValidChapter(ExamType.REGULAR)
        )));

        // Act
        boolean result = DataValidator.isExamValid(exam, ExamType.REGULAR);

        // Assert
        assertFalse(result);
    }

    /**
     * Tests whether an exam is invalid when one included chapter is invalid.
     */
    @Test
    void isExamValid_shouldReturnFalse_whenOneIncludedChapterIsInvalid() {
        // Arrange
        Chapter valid1 = createIncludedValidChapter(ExamType.PRACTICE);
        Chapter valid2 = createIncludedValidChapter(ExamType.PRACTICE);
        Chapter valid3 = createIncludedValidChapter(ExamType.PRACTICE);

        Chapter invalid = new Chapter();
        invalid.setExamAppearance(ExamAppearance.INCLUDE);
        invalid.setSubtasks(new ArrayList<>());

        Exam exam = new Exam();
        exam.setChapters(new ArrayList<>(List.of(valid1, valid2, valid3, invalid)));

        // Act
        boolean result = DataValidator.isExamValid(exam, ExamType.PRACTICE);

        // Assert
        assertFalse(result);
    }

    /**
     * Tests whether the included chapter count is calculated correctly.
     */
    @Test
    void getIncludedChapterCount_shouldCountOnlyIncludedChapters() {
        // Arrange
        Chapter included1 = createIncludedValidChapter(ExamType.PRACTICE);
        Chapter included2 = createIncludedValidChapter(ExamType.PRACTICE);

        Chapter excluded = createIncludedValidChapter(ExamType.PRACTICE);
        excluded.setExamAppearance(ExamAppearance.EXCLUDE);

        Exam exam = new Exam();
        exam.setChapters(new ArrayList<>(List.of(included1, included2, excluded)));

        // Act
        int result = DataValidator.getIncludedChapterCount(exam);

        // Assert
        assertEquals(2, result);
    }

    /**
     * Tests whether the correct warning text is returned
     * when a chapter is invalid for both exam types.
     */
    @Test
    void getWarningText_shouldReturnBothInvalidWarning_whenChapterIsInvalidForBothTypes() {
        // Arrange
        Chapter chapter = new Chapter();
        chapter.setSubtasks(new ArrayList<>());

        // Act
        String warning = DataValidator.getWarningText(chapter);

        // Assert
        assertEquals("⚠ Invalid for Practice + Regular Exam", warning);
    }

    /**
     * Tests whether the exam warning text reports too few included chapters.
     */
    @Test
    void getExamWarningText_shouldReturnMinimumChapterWarning_whenLessThanFourIncludedChaptersExist() {
        // Arrange
        Exam exam = new Exam();
        exam.setChapters(new ArrayList<>(List.of(
                createIncludedValidChapter(ExamType.PRACTICE),
                createIncludedValidChapter(ExamType.PRACTICE),
                createIncludedValidChapter(ExamType.PRACTICE)
        )));

        // Act
        String warning = DataValidator.getExamWarningText(exam);

        // Assert
        assertEquals("⚠ At least 4 included chapters are required", warning);
    }

    /**
     * Tests whether an empty warning text is returned for an exam
     * that is valid for both REGULAR and PRACTICE exam generation.
     */
    @Test
    void getExamWarningText_shouldReturnEmptyString_whenExamIsValid() {
        // Arrange
        Exam exam = new Exam();
        exam.setChapters(new ArrayList<>(List.of(
                createIncludedChapterValidForBothExamTypes(),
                createIncludedChapterValidForBothExamTypes(),
                createIncludedChapterValidForBothExamTypes(),
                createIncludedChapterValidForBothExamTypes()
        )));

        // Act
        String warning = DataValidator.getExamWarningText(exam);

        // Assert
        assertEquals("", warning);
    }

    /**
     * Creates a valid included chapter for a single exam type.
     *
     * @param examType the exam type for which the chapter should be valid
     * @return a valid included chapter
     */
    private Chapter createIncludedValidChapter(ExamType examType) {
        Chapter chapter = createValidChapterForType(examType);
        chapter.setExamAppearance(ExamAppearance.INCLUDE);
        return chapter;
    }

    /**
     * Creates a valid chapter for a single exam type
     * with one usable subtask per difficulty level.
     *
     * @param examType the exam type to use for the subtasks
     * @return a valid chapter
     */
    private Chapter createValidChapterForType(ExamType examType) {
        Chapter chapter = new Chapter();
        chapter.setSubtasks(new ArrayList<>());

        chapter.getSubtasks().add(createUsableSubtask("s1", 2.0, DifficultyLevel.EASY, examType));
        chapter.getSubtasks().add(createUsableSubtask("s2", 3.0, DifficultyLevel.MEDIUM, examType));
        chapter.getSubtasks().add(createUsableSubtask("s3", 4.0, DifficultyLevel.HARD, examType));

        return chapter;
    }

    /**
     * Creates a valid included chapter that is valid
     * for both REGULAR and PRACTICE exam types.
     *
     * @return a valid included chapter for both exam types
     */
    private Chapter createIncludedChapterValidForBothExamTypes() {
        Chapter chapter = new Chapter();
        chapter.setExamAppearance(ExamAppearance.INCLUDE);
        chapter.setSubtasks(new ArrayList<>());

        chapter.getSubtasks().add(createUsableSubtask("e-p", 2.0, DifficultyLevel.EASY, ExamType.PRACTICE));
        chapter.getSubtasks().add(createUsableSubtask("m-p", 3.0, DifficultyLevel.MEDIUM, ExamType.PRACTICE));
        chapter.getSubtasks().add(createUsableSubtask("h-p", 4.0, DifficultyLevel.HARD, ExamType.PRACTICE));

        chapter.getSubtasks().add(createUsableSubtask("e-r", 2.0, DifficultyLevel.EASY, ExamType.REGULAR));
        chapter.getSubtasks().add(createUsableSubtask("m-r", 3.0, DifficultyLevel.MEDIUM, ExamType.REGULAR));
        chapter.getSubtasks().add(createUsableSubtask("h-r", 4.0, DifficultyLevel.HARD, ExamType.REGULAR));

        return chapter;
    }

    /**
     * Creates a usable subtask with one variant containing task text.
     *
     * @param id the subtask ID
     * @param score the score of the subtask
     * @param difficulty the difficulty level
     * @param examType the exam type
     * @return a usable subtask
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
        variant.setTaskText("Task text");
        variant.setAnswerText("Answer");
        variant.setSolutionText("Solution");

        subtask.setVariants(new ArrayList<>(List.of(variant)));
        return subtask;
    }

    /**
     * Creates an unusable subtask with blank variant text.
     *
     * @param id the subtask ID
     * @param score the score of the subtask
     * @param difficulty the difficulty level
     * @param examType the exam type
     * @return an unusable subtask
     */
    private Subtask createUnusableSubtask(String id, double score, DifficultyLevel difficulty, ExamType examType) {
        Subtask subtask = new Subtask();
        subtask.setId(id);
        subtask.setTitle("Subtask " + id);
        subtask.setScore(score);
        subtask.setDifficultyLevel(difficulty);
        subtask.setExamType(examType);

        Variant variant = new Variant();
        variant.setId("v-" + id);
        variant.setTaskText("   ");

        subtask.setVariants(new ArrayList<>(List.of(variant)));
        return subtask;
    }
}