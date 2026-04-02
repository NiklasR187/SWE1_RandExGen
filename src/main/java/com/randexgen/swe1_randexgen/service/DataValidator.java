package com.randexgen.swe1_randexgen.service;

import com.randexgen.swe1_randexgen.datamodel.DifficultyLevel;
import com.randexgen.swe1_randexgen.datamodel.Exam;
import com.randexgen.swe1_randexgen.datamodel.ExamAppearance;
import com.randexgen.swe1_randexgen.datamodel.ExamType;
import com.randexgen.swe1_randexgen.datamodel.Chapter;
import com.randexgen.swe1_randexgen.datamodel.Subtask;
import com.randexgen.swe1_randexgen.datamodel.Variant;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides validation logic for exam data structures.
 *
 * This class ensures that chapters and exams meet the required constraints,
 * such as valid difficulty distribution, usable subtasks, and minimum chapter count.
 */
public class DataValidator {

    /**
     * Checks whether a chapter is valid for a given exam type.
     *
     * A chapter is considered valid if it contains at least one usable subtask
     * for each difficulty level and the distribution between them is balanced.
     *
     * @param chapter the chapter to validate
     * @param examType the exam type (PRACTICE or REGULAR)
     * @return true if the chapter is valid, otherwise false
     */
    public static boolean isChapterValid(Chapter chapter, ExamType examType) {
        // Retrieve valid subtasks grouped by difficulty level
        List<Subtask> easy = getValidSubtasksByDifficulty(chapter, examType, DifficultyLevel.EASY);
        List<Subtask> medium = getValidSubtasksByDifficulty(chapter, examType, DifficultyLevel.MEDIUM);
        List<Subtask> hard = getValidSubtasksByDifficulty(chapter, examType, DifficultyLevel.HARD);

        // A valid chapter must contain at least one subtask per difficulty
        if (easy.isEmpty() || medium.isEmpty() || hard.isEmpty()) {
            return false;
        }

        // Check whether the distribution between difficulties is balanced
        return hasValidDistribution(easy.size(), medium.size(), hard.size());
    }

    /**
     * Checks whether an entire exam is valid for a given exam type.
     *
     * The exam must contain at least four included chapters, and each included
     * chapter must be valid for the selected exam type.
     *
     * @param exam the exam to validate
     * @param examType the exam type (PRACTICE or REGULAR)
     * @return true if the exam is valid, otherwise false
     */
    public static boolean isExamValid(Exam exam, ExamType examType) {
        // Reject invalid or incomplete exam structures
        if (exam == null || exam.getChapters() == null) {
            return false;
        }

        int includedChapterCount = 0;

        // Validate all included chapters
        for (Chapter chapter : exam.getChapters()) {
            if (chapter == null) {
                continue;
            }

            // Only consider chapters marked as included
            if (chapter.getExamAppearance() != ExamAppearance.INCLUDE) {
                continue;
            }

            includedChapterCount++;

            // Stop if any included chapter is invalid
            if (!isChapterValid(chapter, examType)) {
                return false;
            }
        }

        // At least four valid included chapters are required
        return includedChapterCount >= 4;
    }

    /**
     * Returns a warning message describing the validity of a chapter.
     *
     * The message indicates whether the chapter is invalid for practice,
     * regular, or both exam types.
     *
     * @param chapter the chapter to evaluate
     * @return a warning message or an empty string if the chapter is valid
     */
    public static String getWarningText(Chapter chapter) {
        boolean regularValid = isChapterValid(chapter, ExamType.REGULAR);
        boolean practiceValid = isChapterValid(chapter, ExamType.PRACTICE);

        if (!regularValid && !practiceValid) {
            return "⚠ Invalid for Practice + Regular Exam";
        } else if (!regularValid) {
            return "⚠ Invalid for Regular Exam";
        } else if (!practiceValid) {
            return "⚠ Invalid for Practice Exam";
        } else {
            return "";
        }
    }

    /**
     * Returns a warning message describing the validity of an exam.
     *
     * The message indicates whether the exam fulfills the minimum requirements
     * and whether it is valid for practice and/or regular exam generation.
     *
     * @param exam the exam to evaluate
     * @return a warning message or an empty string if the exam is valid
     */
    public static String getExamWarningText(Exam exam) {
        boolean regularValid = isExamValid(exam, ExamType.REGULAR);
        boolean practiceValid = isExamValid(exam, ExamType.PRACTICE);

        int includedCount = getIncludedChapterCount(exam);

        // Ensure that the minimum number of chapters is satisfied
        if (includedCount < 4) {
            return "⚠ At least 4 included chapters are required";
        }

        // Return specific warnings depending on validity
        if (!regularValid && !practiceValid) {
            return "⚠ Exam invalid for Practice + Regular Exam";
        } else if (!regularValid) {
            return "⚠ Exam invalid for Regular Exam";
        } else if (!practiceValid) {
            return "⚠ Exam invalid for Practice Exam";
        } else {
            return "";
        }
    }

    /**
     * Counts the number of chapters marked as included in an exam.
     *
     * @param exam the exam to evaluate
     * @return the number of included chapters
     */
    public static int getIncludedChapterCount(Exam exam) {
        // Return zero if the exam structure is incomplete
        if (exam == null || exam.getChapters() == null) {
            return 0;
        }

        int count = 0;

        // Count all chapters marked as INCLUDE
        for (Chapter chapter : exam.getChapters()) {
            if (chapter != null && chapter.getExamAppearance() == ExamAppearance.INCLUDE) {
                count++;
            }
        }

        return count;
    }

    /**
     * Returns all usable subtasks of a chapter for a given difficulty level.
     *
     * A subtask is considered usable if it contains at least one valid variant
     * with non-empty task text.
     *
     * @param chapter the chapter containing the subtasks
     * @param examType the exam type (PRACTICE or REGULAR)
     * @param difficultyLevel the difficulty level to filter by
     * @return a list of valid subtasks
     */
    public static List<Subtask> getValidSubtasksByDifficulty(Chapter chapter,
                                                             ExamType examType,
                                                             DifficultyLevel difficultyLevel) {
        List<Subtask> result = new ArrayList<>();

        // Filter all subtasks by usability
        for (Subtask subtask : chapter.getSubtasksByDifficulty(examType, difficultyLevel)) {
            if (isSubtaskUsable(subtask)) {
                result.add(subtask);
            }
        }

        return result;
    }

    /**
     * Checks whether a subtask can be used for exam generation.
     *
     * A subtask is usable if it contains at least one variant with
     * a non-empty task text.
     *
     * @param subtask the subtask to evaluate
     * @return true if the subtask is usable, otherwise false
     */
    public static boolean isSubtaskUsable(Subtask subtask) {
        // Reject null subtasks or subtasks without variants
        if (subtask == null) {
            return false;
        }

        if (subtask.getVariants() == null || subtask.getVariants().isEmpty()) {
            return false;
        }

        // Check whether at least one variant contains valid task text
        for (Variant variant : subtask.getVariants()) {
            if (variant == null) {
                continue;
            }

            String taskText = variant.getTaskText();

            if (!isBlank(taskText)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks whether the distribution of subtasks across difficulty levels is valid.
     *
     * The distribution is considered valid if the difference between the maximum
     * and minimum number of subtasks does not exceed two.
     *
     * @param easyCount number of easy subtasks
     * @param mediumCount number of medium subtasks
     * @param hardCount number of hard subtasks
     * @return true if the distribution is valid, otherwise false
     */
    private static boolean hasValidDistribution(int easyCount, int mediumCount, int hardCount) {
        int min = Math.min(easyCount, Math.min(mediumCount, hardCount));
        int max = Math.max(easyCount, Math.max(mediumCount, hardCount));

        return (max - min) <= 2;
    }

    /**
     * Checks whether a string is null, empty, or consists only of whitespace.
     *
     * @param text the text to check
     * @return true if the text is blank, otherwise false
     */
    private static boolean isBlank(String text) {
        return text == null || text.trim().isEmpty();
    }
}