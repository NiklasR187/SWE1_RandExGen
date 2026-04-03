package com.randexgen.swe1_randexgen.service;

import com.randexgen.swe1_randexgen.datamodel.Chapter;
import com.randexgen.swe1_randexgen.datamodel.DifficultyLevel;
import com.randexgen.swe1_randexgen.datamodel.ExamAppearance;
import com.randexgen.swe1_randexgen.datamodel.ExamType;
import com.randexgen.swe1_randexgen.datamodel.Subtask;
import com.randexgen.swe1_randexgen.datamodel.Variant;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ScoreCalculator}.
 *
 * This class verifies the score calculation logic for practice exams,
 * regular exams, and the generation of possible regular score combinations.
 */
class ScoreCalculatorTest {

    /**
     * Tests whether the practice score of a chapter is calculated correctly
     * by summing all valid PRACTICE subtasks.
     */
    @Test
    void calculatePracticeScore_shouldReturnCorrectSumForValidPracticeSubtasks() {
        // Arrange: create a valid chapter with three practice subtasks
        Chapter chapter = new Chapter();
        chapter.setTitle("Chapter 1");
        chapter.setExamAppearance(ExamAppearance.INCLUDE);
        chapter.setSubtasks(new ArrayList<>());

        chapter.getSubtasks().add(createUsableSubtask("s1", 2.0, DifficultyLevel.EASY, ExamType.PRACTICE));
        chapter.getSubtasks().add(createUsableSubtask("s2", 3.5, DifficultyLevel.MEDIUM, ExamType.PRACTICE));
        chapter.getSubtasks().add(createUsableSubtask("s3", 4.0, DifficultyLevel.HARD, ExamType.PRACTICE));

        // Act
        double result = ScoreCalculator.calculatePracticeScore(chapter);

        // Assert
        assertEquals(9.5, result);
    }

    /**
     * Tests whether the regular exam score only includes chapters
     * that are marked as INCLUDE and valid for REGULAR exams.
     */
    @Test
    void calculateRegularExamScore_shouldOnlyCountIncludedAndValidRegularChapters() {
        // Arrange: valid included chapter
        Chapter includedValid = createValidRegularChapter();
        includedValid.setExamAppearance(ExamAppearance.INCLUDE);
        includedValid.setSelectedRegularScore(10.5);

        // Arrange: excluded chapter should not count
        Chapter excludedValid = createValidRegularChapter();
        excludedValid.setExamAppearance(ExamAppearance.EXCLUDE);
        excludedValid.setSelectedRegularScore(8.0);

        // Arrange: invalid regular chapter should not count
        Chapter includedInvalid = new Chapter();
        includedInvalid.setExamAppearance(ExamAppearance.INCLUDE);
        includedInvalid.setSelectedRegularScore(99.0);
        includedInvalid.setSubtasks(new ArrayList<>()); // invalid because no valid subtasks

        List<Chapter> chapters = List.of(includedValid, excludedValid, includedInvalid);

        // Act
        double result = ScoreCalculator.calculateRegularExamScore(chapters);

        // Assert
        assertEquals(10.5, result);
    }

    /**
     * Tests whether the practice exam score only includes chapters
     * that are marked as INCLUDE and valid for PRACTICE exams.
     */
    @Test
    void calculatePracticeExamScore_shouldOnlyCountIncludedAndValidPracticeChapters() {
        // Arrange: valid included chapter for practice
        Chapter includedValid = new Chapter();
        includedValid.setExamAppearance(ExamAppearance.INCLUDE);
        includedValid.setSubtasks(new ArrayList<>());
        includedValid.getSubtasks().add(createUsableSubtask("s1", 2.0, DifficultyLevel.EASY, ExamType.PRACTICE));
        includedValid.getSubtasks().add(createUsableSubtask("s2", 3.0, DifficultyLevel.MEDIUM, ExamType.PRACTICE));
        includedValid.getSubtasks().add(createUsableSubtask("s3", 4.0, DifficultyLevel.HARD, ExamType.PRACTICE));

        // Arrange: excluded valid chapter should not count
        Chapter excludedValid = new Chapter();
        excludedValid.setExamAppearance(ExamAppearance.EXCLUDE);
        excludedValid.setSubtasks(new ArrayList<>());
        excludedValid.getSubtasks().add(createUsableSubtask("s4", 10.0, DifficultyLevel.EASY, ExamType.PRACTICE));
        excludedValid.getSubtasks().add(createUsableSubtask("s5", 10.0, DifficultyLevel.MEDIUM, ExamType.PRACTICE));
        excludedValid.getSubtasks().add(createUsableSubtask("s6", 10.0, DifficultyLevel.HARD, ExamType.PRACTICE));

        // Arrange: invalid chapter should not count
        Chapter includedInvalid = new Chapter();
        includedInvalid.setExamAppearance(ExamAppearance.INCLUDE);
        includedInvalid.setSubtasks(new ArrayList<>());

        List<Chapter> chapters = List.of(includedValid, excludedValid, includedInvalid);

        // Act
        double result = ScoreCalculator.calculatePracticeExamScore(chapters);

        // Assert
        assertEquals(9.0, result);
    }

    /**
     * Tests whether no regular score combinations are returned
     * if one of the required difficulty levels is missing.
     */
    @Test
    void calculatePossibleRegularScores_shouldReturnEmptyListWhenOneDifficultyIsMissing() {
        // Arrange: create a chapter without HARD subtasks
        Chapter chapter = new Chapter();
        chapter.setExamAppearance(ExamAppearance.INCLUDE);
        chapter.setSubtasks(new ArrayList<>());

        chapter.getSubtasks().add(createUsableSubtask("s1", 2.0, DifficultyLevel.EASY, ExamType.REGULAR));
        chapter.getSubtasks().add(createUsableSubtask("s2", 3.0, DifficultyLevel.MEDIUM, ExamType.REGULAR));

        // Act
        List<Double> scores = ScoreCalculator.calculatePossibleRegularScores(chapter);

        // Assert
        assertTrue(scores.isEmpty());
    }

    /**
     * Tests whether valid regular score combinations are generated correctly
     * for a chapter with one usable subtask in each difficulty level.
     */
    @Test
    void calculatePossibleRegularScores_shouldReturnCorrectScoreForSimpleValidCombination() {
        // Arrange: one valid REGULAR subtask per difficulty
        Chapter chapter = new Chapter();
        chapter.setExamAppearance(ExamAppearance.INCLUDE);
        chapter.setSubtasks(new ArrayList<>());

        chapter.getSubtasks().add(createUsableSubtask("s1", 2.0, DifficultyLevel.EASY, ExamType.REGULAR));
        chapter.getSubtasks().add(createUsableSubtask("s2", 3.0, DifficultyLevel.MEDIUM, ExamType.REGULAR));
        chapter.getSubtasks().add(createUsableSubtask("s3", 4.0, DifficultyLevel.HARD, ExamType.REGULAR));

        // Act
        List<Double> scores = ScoreCalculator.calculatePossibleRegularScores(chapter);

        // Assert
        assertEquals(1, scores.size());
        assertEquals(9.0, scores.get(0));
    }

    /**
     * Tests whether the returned possible regular scores are sorted in ascending order.
     */
    @Test
    void calculatePossibleRegularScores_shouldReturnSortedScores() {
        // Arrange: create multiple valid combinations
        Chapter chapter = new Chapter();
        chapter.setExamAppearance(ExamAppearance.INCLUDE);
        chapter.setSubtasks(new ArrayList<>());

        chapter.getSubtasks().add(createUsableSubtask("e1", 1.0, DifficultyLevel.EASY, ExamType.REGULAR));
        chapter.getSubtasks().add(createUsableSubtask("e2", 2.0, DifficultyLevel.EASY, ExamType.REGULAR));

        chapter.getSubtasks().add(createUsableSubtask("m1", 3.0, DifficultyLevel.MEDIUM, ExamType.REGULAR));
        chapter.getSubtasks().add(createUsableSubtask("m2", 4.0, DifficultyLevel.MEDIUM, ExamType.REGULAR));

        chapter.getSubtasks().add(createUsableSubtask("h1", 5.0, DifficultyLevel.HARD, ExamType.REGULAR));
        chapter.getSubtasks().add(createUsableSubtask("h2", 6.0, DifficultyLevel.HARD, ExamType.REGULAR));

        // Act
        List<Double> scores = ScoreCalculator.calculatePossibleRegularScores(chapter);

        // Assert
        assertFalse(scores.isEmpty());

        for (int i = 0; i < scores.size() - 1; i++) {
            assertTrue(scores.get(i) <= scores.get(i + 1));
        }
    }

    /**
     * Creates a valid regular chapter with one usable subtask
     * for each required difficulty level.
     *
     * @return a valid chapter for REGULAR exam calculations
     */
    private Chapter createValidRegularChapter() {
        Chapter chapter = new Chapter();
        chapter.setExamAppearance(ExamAppearance.INCLUDE);
        chapter.setSubtasks(new ArrayList<>());

        chapter.getSubtasks().add(createUsableSubtask("s1", 2.0, DifficultyLevel.EASY, ExamType.REGULAR));
        chapter.getSubtasks().add(createUsableSubtask("s2", 3.0, DifficultyLevel.MEDIUM, ExamType.REGULAR));
        chapter.getSubtasks().add(createUsableSubtask("s3", 4.0, DifficultyLevel.HARD, ExamType.REGULAR));

        return chapter;
    }

    /**
     * Creates a usable subtask with one valid variant.
     *
     * The created subtask can be used in score-related tests
     * because it contains the minimum valid data.
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
        variant.setTaskText("Task");
        variant.setAnswerText("Answer");
        variant.setSolutionText("Solution");

        subtask.setVariants(new ArrayList<>(List.of(variant)));
        return subtask;
    }
}