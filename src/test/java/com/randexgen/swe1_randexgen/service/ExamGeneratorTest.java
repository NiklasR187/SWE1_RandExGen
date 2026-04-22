package com.randexgen.swe1_randexgen.service;

import com.randexgen.swe1_randexgen.datamodel.Chapter;
import com.randexgen.swe1_randexgen.datamodel.DifficultyLevel;
import com.randexgen.swe1_randexgen.datamodel.Exam;
import com.randexgen.swe1_randexgen.datamodel.ExamAppearance;
import com.randexgen.swe1_randexgen.datamodel.ExamType;
import com.randexgen.swe1_randexgen.datamodel.Subtask;
import com.randexgen.swe1_randexgen.datamodel.Variant;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ExamGenerator}.
 *
 * This class verifies the main generation logic for practice and regular exams.
 * The tests focus on valid input, invalid input, excluded chapters,
 * and unreachable target scores.
 */
class ExamGeneratorTest {

    /**
     * Tests whether an empty list is returned when the exam is null.
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
     * when the exam is invalid for the selected exam type.
     */
    @Test
    void generateExam_shouldReturnEmptyList_whenExamIsInvalidForSelectedType() {
        Exam exam = new Exam();
        exam.setChapters(new ArrayList<>());

        List<GeneratedTask> result = ExamGenerator.generateExam(exam, ExamType.REGULAR);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /**
     * Tests whether the generator ignores excluded chapters
     * when generating a regular exam.
     */
    @Test
    void generateExam_shouldIgnoreExcludedChapters_whenGeneratingRegularExam() {
        Chapter included1 = createValidRegularChapter("included1");
        Chapter included2 = createValidRegularChapter("included2");
        Chapter included3 = createValidRegularChapter("included3");
        Chapter included4 = createValidRegularChapter("included4");

        Chapter excluded = createValidRegularChapter("excluded");
        excluded.setExamAppearance(ExamAppearance.EXCLUDE);

        Exam exam = new Exam();
        exam.setChapters(new ArrayList<>(List.of(
                included1, included2, included3, included4, excluded
        )));

        List<GeneratedTask> result = ExamGenerator.generateExam(exam, ExamType.REGULAR);

        assertNotNull(result);
        assertEquals(12, result.size());

        for (GeneratedTask task : result) {
            assertNotEquals("excluded", task.getChapter().getId());
        }
    }


    /**
     * Tests whether an empty list is returned when the exam type is null.
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
     * Tests whether collectMatchingCombinations adds one match
     * when a valid combination with the target score exists.
     */
    @Test
    void collectMatchingCombinations_shouldAddMatch_whenCombinationMatchesTargetScore() throws Exception {
        List<Subtask> pool = new ArrayList<>();
        pool.add(createUsableSubtask("s1", 2.0, DifficultyLevel.EASY, ExamType.REGULAR));
        pool.add(createUsableSubtask("s2", 3.0, DifficultyLevel.EASY, ExamType.REGULAR));
        pool.add(createUsableSubtask("s3", 5.0, DifficultyLevel.EASY, ExamType.REGULAR));

        List<Subtask> current = new ArrayList<>();
        List<List<Subtask>> matches = new ArrayList<>();

        Method method = ExamGenerator.class.getDeclaredMethod(
                "collectMatchingCombinations",
                List.class, int.class, int.class, int.class, List.class, List.class
        );
        method.setAccessible(true);

        // 2 subtasks, target score = 5 -> should find [2 + 3]
        method.invoke(null, pool, 2, 5, 0, current, matches);

        assertEquals(1, matches.size());
        assertEquals(2, matches.get(0).size());
    }

    /**
     * Tests whether collectMatchingCombinations adds no match
     * when no valid score combination exists.
     */
    @Test
    void collectMatchingCombinations_shouldAddNoMatch_whenNoCombinationMatchesTargetScore() throws Exception {
        List<Subtask> pool = new ArrayList<>();
        pool.add(createUsableSubtask("s1", 1.0, DifficultyLevel.EASY, ExamType.REGULAR));
        pool.add(createUsableSubtask("s2", 1.0, DifficultyLevel.EASY, ExamType.REGULAR));
        pool.add(createUsableSubtask("s3", 1.0, DifficultyLevel.EASY, ExamType.REGULAR));

        List<Subtask> current = new ArrayList<>();
        List<List<Subtask>> matches = new ArrayList<>();

        Method method = ExamGenerator.class.getDeclaredMethod(
                "collectMatchingCombinations",
                List.class, int.class, int.class, int.class, List.class, List.class
        );
        method.setAccessible(true);

        // No combination of 2 subtasks can reach score 5
        method.invoke(null, pool, 2, 5, 0, current, matches);

        assertTrue(matches.isEmpty());
    }

    /**
     * Tests whether collectMatchingCombinations stops correctly
     * when the index is already outside the pool.
     */
    @Test
    void collectMatchingCombinations_shouldStop_whenIndexIsOutsidePool() throws Exception {
        List<Subtask> pool = new ArrayList<>();
        pool.add(createUsableSubtask("s1", 2.0, DifficultyLevel.EASY, ExamType.REGULAR));

        List<Subtask> current = new ArrayList<>();
        List<List<Subtask>> matches = new ArrayList<>();

        Method method = ExamGenerator.class.getDeclaredMethod(
                "collectMatchingCombinations",
                List.class, int.class, int.class, int.class, List.class, List.class
        );
        method.setAccessible(true);

        // index == pool.size() -> should immediately return
        method.invoke(null, pool, 1, 2, pool.size(), current, matches);

        assertTrue(matches.isEmpty());
    }

    /**
     * Tests whether collectMatchingCombinations checks the current combination
     * immediately when remaining is zero.
     */
    @Test
    void collectMatchingCombinations_shouldCheckCurrentCombination_whenRemainingIsZero() throws Exception {
        List<Subtask> pool = new ArrayList<>();
        List<Subtask> current = new ArrayList<>();
        current.add(createUsableSubtask("s1", 4.0, DifficultyLevel.EASY, ExamType.REGULAR));

        List<List<Subtask>> matches = new ArrayList<>();

        Method method = ExamGenerator.class.getDeclaredMethod(
                "collectMatchingCombinations",
                List.class, int.class, int.class, int.class, List.class, List.class
        );
        method.setAccessible(true);

        // remaining == 0 -> method only checks current score
        method.invoke(null, pool, 0, 4, 0, current, matches);

        assertEquals(1, matches.size());
        assertEquals(1, matches.get(0).size());
    }

    /**
     * Tests whether a valid practice exam is generated correctly.
     *
     * Since the validator requires at least four included chapters,
     * this test creates four valid included chapters.
     * Each chapter contributes three usable practice subtasks,
     * resulting in twelve generated tasks in total.
     */
    @Test
    void generateExam_shouldGeneratePracticeExam_whenInputIsValid() {
        // Arrange
        Exam exam = new Exam();
        exam.setChapters(new ArrayList<>(List.of(
                createValidPracticeChapter("p1"),
                createValidPracticeChapter("p2"),
                createValidPracticeChapter("p3"),
                createValidPracticeChapter("p4")
        )));

        // Act
        List<GeneratedTask> result = ExamGenerator.generateExam(exam, ExamType.PRACTICE);

        // Assert
        assertNotNull(result);
        assertEquals(12, result.size());

        for (GeneratedTask task : result) {
            assertNotNull(task.getChapter());
            assertNotNull(task.getSubtask());
            assertNotNull(task.getVariant());
            assertEquals(ExamType.PRACTICE, task.getSubtask().getExamType());
            assertNotNull(task.getVariant().getTaskText());
            assertFalse(task.getVariant().getTaskText().trim().isEmpty());
        }
    }

    /**
     * Tests whether excluded chapters are ignored during practice exam generation.
     *
     * The exam still remains valid because it contains four included valid chapters.
     * The excluded chapter must not contribute any generated task.
     */
    @Test
    void generateExam_shouldIgnoreExcludedChapters_whenGeneratingPracticeExam() {
        // Arrange
        Chapter included1 = createValidPracticeChapter("included1");
        Chapter included2 = createValidPracticeChapter("included2");
        Chapter included3 = createValidPracticeChapter("included3");
        Chapter included4 = createValidPracticeChapter("included4");

        Chapter excluded = createValidPracticeChapter("excluded");
        excluded.setExamAppearance(ExamAppearance.EXCLUDE);

        Exam exam = new Exam();
        exam.setChapters(new ArrayList<>(List.of(
                included1, included2, included3, included4, excluded
        )));

        // Act
        List<GeneratedTask> result = ExamGenerator.generateExam(exam, ExamType.PRACTICE);

        // Assert
        assertNotNull(result);
        assertEquals(12, result.size());

        for (GeneratedTask task : result) {
            assertNotEquals("excluded", task.getChapter().getId());
        }
    }

    /**
     * Tests whether generation returns an empty list
     * when one included practice chapter is invalid.
     */
    @Test
    void generateExam_shouldReturnEmptyList_whenIncludedPracticeChapterIsInvalid() {
        // Arrange
        Chapter valid1 = createValidPracticeChapter("valid1");
        Chapter valid2 = createValidPracticeChapter("valid2");
        Chapter valid3 = createValidPracticeChapter("valid3");

        Chapter invalid = new Chapter();
        invalid.setId("invalid");
        invalid.setTitle("Invalid Chapter");
        invalid.setExamAppearance(ExamAppearance.INCLUDE);
        invalid.setSubtasks(new ArrayList<>());

        Exam exam = new Exam();
        exam.setChapters(new ArrayList<>(List.of(valid1, valid2, valid3, invalid)));

        // Act
        List<GeneratedTask> result = ExamGenerator.generateExam(exam, ExamType.PRACTICE);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /**
     * Tests whether a valid regular exam is generated correctly.
     *
     * Four included valid chapters are used so that the exam is valid.
     * Each chapter has one regular subtask per difficulty level and a matching
     * selected regular score of 9.0, resulting in three tasks per chapter.
     */
    @Test
    void generateExam_shouldGenerateRegularExam_whenInputIsValid() {
        // Arrange
        Exam exam = new Exam();
        exam.setChapters(new ArrayList<>(List.of(
                createValidRegularChapter("r1"),
                createValidRegularChapter("r2"),
                createValidRegularChapter("r3"),
                createValidRegularChapter("r4")
        )));

        // Act
        List<GeneratedTask> result = ExamGenerator.generateExam(exam, ExamType.REGULAR);

        // Assert
        assertNotNull(result);
        assertEquals(12, result.size());

        double totalScore = 0;
        for (GeneratedTask task : result) {
            assertNotNull(task.getChapter());
            assertNotNull(task.getSubtask());
            assertNotNull(task.getVariant());
            assertEquals(ExamType.REGULAR, task.getSubtask().getExamType());

            totalScore += task.getSubtask().getScore();
        }

        assertEquals(36.0, totalScore);
    }

    /**
     * Tests whether regular exam generation returns an empty list
     * when the selected regular score of the chapters cannot be reached.
     */
    @Test
    void generateExam_shouldReturnEmptyList_whenRegularTargetScoreCannotBeReached() {
        // Arrange
        Chapter c1 = createValidRegularChapter("r1");
        Chapter c2 = createValidRegularChapter("r2");
        Chapter c3 = createValidRegularChapter("r3");
        Chapter c4 = createValidRegularChapter("r4");

        c1.setSelectedRegularScore(100.0);
        c2.setSelectedRegularScore(100.0);
        c3.setSelectedRegularScore(100.0);
        c4.setSelectedRegularScore(100.0);

        Exam exam = new Exam();
        exam.setChapters(new ArrayList<>(List.of(c1, c2, c3, c4)));

        // Act
        List<GeneratedTask> result = ExamGenerator.generateExam(exam, ExamType.REGULAR);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /**
     * Tests whether generation returns an empty list
     * when one included regular chapter is invalid.
     */
    @Test
    void generateExam_shouldReturnEmptyList_whenIncludedRegularChapterIsInvalid() {
        // Arrange
        Chapter valid1 = createValidRegularChapter("valid1");
        Chapter valid2 = createValidRegularChapter("valid2");
        Chapter valid3 = createValidRegularChapter("valid3");

        Chapter invalid = new Chapter();
        invalid.setId("invalid");
        invalid.setTitle("Invalid Chapter");
        invalid.setExamAppearance(ExamAppearance.INCLUDE);
        invalid.setSelectedRegularScore(5.0);
        invalid.setSubtasks(new ArrayList<>());

        Exam exam = new Exam();
        exam.setChapters(new ArrayList<>(List.of(valid1, valid2, valid3, invalid)));

        // Act
        List<GeneratedTask> result = ExamGenerator.generateExam(exam, ExamType.REGULAR);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /**
     * Creates a valid included practice chapter with one usable subtask
     * for each difficulty level.
     *
     * @param id the chapter ID
     * @return a valid practice chapter
     */
    private Chapter createValidPracticeChapter(String id) {
        Chapter chapter = new Chapter();
        chapter.setId(id);
        chapter.setTitle("Practice Chapter " + id);
        chapter.setExamAppearance(ExamAppearance.INCLUDE);
        chapter.setSubtasks(new ArrayList<>());

        chapter.getSubtasks().add(createUsableSubtask(id + "-e", 2.0, DifficultyLevel.EASY, ExamType.PRACTICE));
        chapter.getSubtasks().add(createUsableSubtask(id + "-m", 3.0, DifficultyLevel.MEDIUM, ExamType.PRACTICE));
        chapter.getSubtasks().add(createUsableSubtask(id + "-h", 4.0, DifficultyLevel.HARD, ExamType.PRACTICE));

        return chapter;
    }

    /**
     * Creates a valid included regular chapter with one usable subtask
     * for each difficulty level and a matching target score.
     *
     * @param id the chapter ID
     * @return a valid regular chapter
     */
    private Chapter createValidRegularChapter(String id) {
        Chapter chapter = new Chapter();
        chapter.setId(id);
        chapter.setTitle("Regular Chapter " + id);
        chapter.setExamAppearance(ExamAppearance.INCLUDE);
        chapter.setSelectedRegularScore(9.0);
        chapter.setSubtasks(new ArrayList<>());

        chapter.getSubtasks().add(createUsableSubtask(id + "-e", 2.0, DifficultyLevel.EASY, ExamType.REGULAR));
        chapter.getSubtasks().add(createUsableSubtask(id + "-m", 3.0, DifficultyLevel.MEDIUM, ExamType.REGULAR));
        chapter.getSubtasks().add(createUsableSubtask(id + "-h", 4.0, DifficultyLevel.HARD, ExamType.REGULAR));

        return chapter;
    }

    /**
     * Creates a usable subtask with one valid variant.
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
        variant.setTaskText("Task text for " + id);
        variant.setAnswerText("Answer text");
        variant.setSolutionText("Solution text");

        subtask.setVariants(new ArrayList<>(List.of(variant)));
        return subtask;
    }
}