package com.randexgen.swe1_randexgen.service;

import com.randexgen.swe1_randexgen.datamodel.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Provides methods to calculate scores for practice and regular exams.
 *
 * This class contains logic for summing up scores of valid subtasks,
 * filtering chapters based on their state, and computing all possible
 * score combinations for regular exams.
 */
public class ScoreCalculator {

    /**
     * Calculates the total score of a chapter for a practice exam.
     *
     * The method sums up all valid subtasks of the given chapter,
     * grouped by difficulty level, for the PRACTICE exam type.
     *
     * @param chapter the chapter for which the score is calculated
     * @return the total score of all valid subtasks in the chapter
     */
    public static double calculatePracticeScore(Chapter chapter) {
        double totalScore = 0;

        // Iterate over all difficulty levels to include every valid subtask
        for (DifficultyLevel difficulty : DifficultyLevel.values()) {
            List<Subtask> subtasks = DataValidator.getValidSubtasksByDifficulty(
                    chapter,
                    ExamType.PRACTICE,
                    difficulty
            );

            // Sum up the score of each valid subtask
            for (Subtask subtask : subtasks) {
                totalScore += subtask.getScore();
            }
        }

        return totalScore;
    }

    /**
     * Calculates the total score of a regular exam based on selected chapters.
     *
     * Only chapters that are marked as INCLUDED and are valid for the REGULAR
     * exam type are considered. The selected regular score of each chapter is added.
     *
     * @param chapters the list of chapters to evaluate
     * @return the total score of the regular exam
     */
    public static double calculateRegularExamScore(List<Chapter> chapters) {
        double total = 0;

        for (Chapter chapter : chapters) {

            // Skip chapters that are not included in the exam
            if (chapter.getExamAppearance() != ExamAppearance.INCLUDE) {
                continue;
            }

            // Skip chapters that are not valid for a regular exam
            if (!DataValidator.isChapterValid(chapter, ExamType.REGULAR)) {
                continue;
            }

            // Add the selected score for the current chapter
            total += chapter.getSelectedRegularScore();
        }

        return total;
    }

    /**
     * Calculates the total score of a practice exam based on selected chapters.
     *
     * Only chapters that are included and valid for the PRACTICE exam type
     * are considered. The score of each chapter is calculated individually.
     *
     * @param chapters the list of chapters to evaluate
     * @return the total score of the practice exam
     */
    public static double calculatePracticeExamScore(List<Chapter> chapters) {
        double total = 0;

        for (Chapter chapter : chapters) {

            // Skip chapters that are not included in the exam
            if (chapter.getExamAppearance() != ExamAppearance.INCLUDE) {
                continue;
            }

            // Skip chapters that are not valid for a practice exam
            if (!DataValidator.isChapterValid(chapter, ExamType.PRACTICE)) {
                continue;
            }

            // Add the calculated practice score of the chapter
            total += calculatePracticeScore(chapter);
        }

        return total;
    }


    /**
     * Calculates all possible total scores for a regular exam chapter.
     *
     * The method generates all valid combinations of subtasks across difficulty
     * levels and ensures that the distribution difference between difficulties
     * does not exceed a threshold.
     *
     * @param chapter the chapter for which possible scores are calculated
     * @return a sorted list of all possible total scores
     */
    public static List<Double> calculatePossibleRegularScores(Chapter chapter) {
        // Retrieve valid subtasks grouped by difficulty
        List<Subtask> easy = DataValidator.getValidSubtasksByDifficulty(
                chapter, ExamType.REGULAR, DifficultyLevel.EASY
        );
        List<Subtask> medium = DataValidator.getValidSubtasksByDifficulty(
                chapter, ExamType.REGULAR, DifficultyLevel.MEDIUM
        );
        List<Subtask> hard = DataValidator.getValidSubtasksByDifficulty(
                chapter, ExamType.REGULAR, DifficultyLevel.HARD
        );

        Set<Double> possibleScores = new HashSet<>();

        // If any difficulty level is missing, no valid combinations exist
        if (easy.isEmpty() || medium.isEmpty() || hard.isEmpty()) {
            return new ArrayList<>();
        }

        // Iterate over all possible counts of subtasks per difficulty
        for (int e = 1; e <= easy.size(); e++) {
            for (int m = 1; m <= medium.size(); m++) {
                for (int h = 1; h <= hard.size(); h++) {

                    // Ensure a balanced distribution between difficulties
                    int min = Math.min(e, Math.min(m, h));
                    int max = Math.max(e, Math.max(m, h));

                    if (max - min <= 2) {
                        collectPossibleScores(possibleScores, easy, medium, hard, e, m, h);
                    }
                }
            }
        }

        // Convert the result set into a sorted list
        List<Double> sortedScores = new ArrayList<>(possibleScores);
        sortedScores.sort(Double::compareTo);
        return sortedScores;
    }

    /**
     * Combines all possible score sums from different difficulty levels.
     *
     * The method generates all combinations of score sums for easy, medium,
     * and hard subtasks and adds the total values to the result set.
     */
    private static void collectPossibleScores(Set<Double> possibleScores,
                                              List<Subtask> easy,
                                              List<Subtask> medium,
                                              List<Subtask> hard,
                                              int easyCount,
                                              int mediumCount,
                                              int hardCount) {

        // Generate all possible score sums for each difficulty group
        List<Double> easySums = getAllScoreSums(easy, easyCount);
        List<Double> mediumSums = getAllScoreSums(medium, mediumCount);
        List<Double> hardSums = getAllScoreSums(hard, hardCount);

        // Combine all sums to form total possible scores
        for (double easySum : easySums) {
            for (double mediumSum : mediumSums) {
                for (double hardSum : hardSums) {
                    possibleScores.add(easySum + mediumSum + hardSum);
                }
            }
        }
    }

    /**
     * Generates all possible score sums for selecting a fixed number of subtasks.
     *
     * This method uses recursion to compute all combinations of subtasks
     * and their corresponding score sums.
     */
    private static List<Double> getAllScoreSums(List<Subtask> subtasks, int count) {
        List<Double> sums = new ArrayList<>();

        // Start recursive combination generation
        collectScoreSumsRecursive(subtasks, count, 0, 0, sums);

        return sums;
    }

    /**
     * Recursively computes all possible score sums for combinations of subtasks.
     *
     * The method selects subtasks step by step and accumulates their scores
     * until the required number of elements is reached.
     */
    private static void collectScoreSumsRecursive(List<Subtask> subtasks,
                                                  int remaining,
                                                  int index,
                                                  double currentSum,
                                                  List<Double> sums) {

        // Base case: required number of subtasks selected
        if (remaining == 0) {
            sums.add(currentSum);
            return;
        }

        // Stop if no more subtasks are available
        if (index >= subtasks.size()) {
            return;
        }

        // Iterate over remaining subtasks and build combinations recursively
        for (int i = index; i < subtasks.size(); i++) {
            collectScoreSumsRecursive(
                    subtasks,
                    remaining - 1,
                    i + 1,
                    currentSum + subtasks.get(i).getScore(),
                    sums
            );
        }
    }
}