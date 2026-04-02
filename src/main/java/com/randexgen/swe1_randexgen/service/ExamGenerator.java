package com.randexgen.swe1_randexgen.service;

import com.randexgen.swe1_randexgen.datamodel.ExamType;
import com.randexgen.swe1_randexgen.datamodel.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Generates practice and regular exams based on the available exam data.
 *
 * This class contains the core generation logic for selecting valid subtasks,
 * respecting difficulty distributions, and assigning random usable variants.
 */
public class ExamGenerator {

    private static final Random RANDOM = new Random();

    public static boolean regularExamGenerated = false;
    public static boolean practiceExamGenerated = false;


    /**
     * Generates an exam for the given exam type.
     *
     * The method first validates the input and then delegates the generation
     * process to the corresponding logic for practice or regular exams.
     *
     * @param exam the exam configuration containing chapters and subtasks
     * @param examType the type of exam to generate
     * @return a list of generated tasks, or an empty list if generation is not possible
     */
    public static List<GeneratedTask> generateExam(Exam exam, ExamType examType) {
        // Return an empty result if required input data is missing
        regularExamGenerated = true;
        if (exam == null || examType == null) {
            return new ArrayList<>();
        }

        // Stop generation if the exam is not valid for the selected type
        if (!DataValidator.isExamValid(exam, examType)) {
            return new ArrayList<>();
        }

        // Delegate to the matching generation strategy depending on the exam type
        if (examType == ExamType.PRACTICE) {
            return generatePracticeExam(exam);
        } else if (examType == ExamType.REGULAR) {
            return generateRegularExam(exam);
        }

        return new ArrayList<>();
    }

    /**
     * Generates a practice exam from all included and valid chapters.
     *
     * For practice exams, all valid subtasks of each included chapter are added,
     * grouped by difficulty level and combined with a random usable variant.
     *
     * @param exam the exam configuration
     * @return the generated list of practice tasks
     */
    private static List<GeneratedTask> generatePracticeExam(Exam exam) {
        practiceExamGenerated = true;
        List<GeneratedTask> result = new ArrayList<>();

        // Process all chapters of the exam one by one
        for (Chapter chapter : exam.getChapters()) {
            if (!isIncluded(chapter)) {
                continue;
            }

            // Abort generation if an included chapter is invalid for practice exams
            if (!DataValidator.isChapterValid(chapter, ExamType.PRACTICE)) {
                return new ArrayList<>();
            }

            // Add all valid subtasks of the included chapter for each difficulty level
            addAllSubtasks(result, chapter, ExamType.PRACTICE, DifficultyLevel.EASY);
            addAllSubtasks(result, chapter, ExamType.PRACTICE, DifficultyLevel.MEDIUM);
            addAllSubtasks(result, chapter, ExamType.PRACTICE, DifficultyLevel.HARD);
        }

        return result;
    }

    /**
     * Generates a regular exam from all included and valid chapters.
     *
     * For each included chapter, the method tries to find a valid task selection
     * whose total score matches the selected regular score of that chapter.
     *
     * @param exam the exam configuration
     * @return the generated list of regular exam tasks
     */
    private static List<GeneratedTask> generateRegularExam(Exam exam) {
        List<GeneratedTask> result = new ArrayList<>();

        // Process all included chapters that participate in the regular exam
        for (Chapter chapter : exam.getChapters()) {
            if (!isIncluded(chapter)) {
                continue;
            }

            // Abort generation if an included chapter is invalid for regular exams
            if (!DataValidator.isChapterValid(chapter, ExamType.REGULAR)) {
                return new ArrayList<>();
            }

            double targetScore = chapter.getSelectedRegularScore();

            // Generate a valid chapter selection that matches the configured target score
            ChapterSelection selection = generateRegularChapterSelection(chapter, targetScore);

            if (selection == null) {
                return new ArrayList<>();
            }

            result.addAll(selection.getTasks());
        }

        return result;
    }

    /**
     * Generates a valid task selection for one regular exam chapter.
     *
     * The method groups valid subtasks by difficulty, creates balanced combinations,
     * and searches for one whose total score matches the selected target score.
     *
     * @param chapter the chapter for which the selection is generated
     * @param targetScore the required total score of the chapter
     * @return the generated chapter selection, or null if none could be found
     */
    private static ChapterSelection generateRegularChapterSelection(Chapter chapter, double targetScore) {
        // Collect valid subtasks of the chapter grouped by difficulty
        List<Subtask> easy = new ArrayList<>(
                DataValidator.getValidSubtasksByDifficulty(chapter, ExamType.REGULAR, DifficultyLevel.EASY)
        );
        List<Subtask> medium = new ArrayList<>(
                DataValidator.getValidSubtasksByDifficulty(chapter, ExamType.REGULAR, DifficultyLevel.MEDIUM)
        );
        List<Subtask> hard = new ArrayList<>(
                DataValidator.getValidSubtasksByDifficulty(chapter, ExamType.REGULAR, DifficultyLevel.HARD)
        );

        // A valid chapter selection requires at least one subtask per difficulty
        if (easy.isEmpty() || medium.isEmpty() || hard.isEmpty()) {
            return null;
        }

        // Build all valid distributions of task counts across difficulty levels
        List<int[]> validCombinations = getValidDifficultyCombinations(easy.size(), medium.size(), hard.size());
        Collections.shuffle(validCombinations, RANDOM);

        // Try all valid difficulty distributions in random order
        for (int[] combination : validCombinations) {
            int easyCount = combination[0];
            int mediumCount = combination[1];
            int hardCount = combination[2];

            // Generate all subtask combinations for the chosen counts
            List<List<Subtask>> easyCombinations = getAllSubtaskCombinations(easy, easyCount);
            List<List<Subtask>> mediumCombinations = getAllSubtaskCombinations(medium, mediumCount);
            List<List<Subtask>> hardCombinations = getAllSubtaskCombinations(hard, hardCount);

            Collections.shuffle(easyCombinations, RANDOM);
            Collections.shuffle(mediumCombinations, RANDOM);
            Collections.shuffle(hardCombinations, RANDOM);

            // Combine the difficulty-specific selections and check their total score
            for (List<Subtask> easyList : easyCombinations) {
                for (List<Subtask> mediumList : mediumCombinations) {
                    for (List<Subtask> hardList : hardCombinations) {
                        int total =
                                getTotalScore(easyList) +
                                        getTotalScore(mediumList) +
                                        getTotalScore(hardList);

                        // Accept the first combination whose score matches the target score
                        if (total == targetScore) {
                            List<GeneratedTask> tasks = new ArrayList<>();
                            addGeneratedTasks(tasks, chapter, easyList);
                            addGeneratedTasks(tasks, chapter, mediumList);
                            addGeneratedTasks(tasks, chapter, hardList);

                            // Shuffle the final task order for a more natural exam layout
                            Collections.shuffle(tasks, RANDOM);
                            return new ChapterSelection(tasks);
                        }
                    }
                }
            }
        }

        return null;
    }

    /**
     * Generates all possible subtask combinations of a fixed size.
     *
     * This method delegates the actual combination building to a recursive helper
     * and returns the resulting list of combinations.
     *
     * @param pool the available subtasks
     * @param count the number of subtasks to select
     * @return a list of all possible combinations
     */
    private static List<List<Subtask>> getAllSubtaskCombinations(List<Subtask> pool, int count) {
        List<List<Subtask>> result = new ArrayList<>();
        collectAllCombinations(pool, count, 0, new ArrayList<>(), result);
        return result;
    }

    /**
     * Recursively collects all combinations of subtasks with the requested size.
     *
     * The method builds combinations step by step and stores every complete
     * selection in the result list.
     *
     * @param pool the available subtasks
     * @param remaining the number of subtasks still to select
     * @param index the current start index in the pool
     * @param current the current partial combination
     * @param result the list collecting all complete combinations
     */
    private static void collectAllCombinations(List<Subtask> pool,
                                               int remaining,
                                               int index,
                                               List<Subtask> current,
                                               List<List<Subtask>> result) {
        // Store the current combination once the requested number of subtasks is reached
        if (remaining == 0) {
            result.add(new ArrayList<>(current));
            return;
        }

        // Stop recursion if no more subtasks are available
        if (index >= pool.size()) {
            return;
        }

        // Extend the current combination with each remaining subtask
        for (int i = index; i < pool.size(); i++) {
            current.add(pool.get(i));
            collectAllCombinations(pool, remaining - 1, i + 1, current, result);
            current.remove(current.size() - 1);
        }
    }

    /**
     * Adds all valid subtasks of a given difficulty level to the generated result.
     *
     * Each subtask receives one random usable variant before it is converted
     * into a generated task object.
     *
     * @param result the result list to which the generated tasks are added
     * @param chapter the chapter from which the subtasks originate
     * @param examType the exam type for which valid subtasks are selected
     * @param difficultyLevel the difficulty level of the subtasks to add
     */
    private static void addAllSubtasks(List<GeneratedTask> result,
                                       Chapter chapter,
                                       ExamType examType,
                                       DifficultyLevel difficultyLevel) {
        // Retrieve all valid subtasks matching the selected difficulty
        List<Subtask> subtasks = DataValidator.getValidSubtasksByDifficulty(chapter, examType, difficultyLevel);

        // Convert each valid subtask into a generated task with a random usable variant
        for (Subtask subtask : subtasks) {
            Variant randomVariant = getRandomUsableVariant(subtask);

            if (randomVariant != null) {
                result.add(new GeneratedTask(chapter, subtask, randomVariant));
            }
        }
    }

    /**
     * Adds a list of subtasks as generated tasks to the result list.
     *
     * For each selected subtask, the method chooses one random usable variant
     * and creates a corresponding generated task object.
     *
     * @param result the result list to which tasks are added
     * @param chapter the chapter to which the subtasks belong
     * @param subtasks the selected subtasks to convert
     */
    private static void addGeneratedTasks(List<GeneratedTask> result,
                                          Chapter chapter,
                                          List<Subtask> subtasks) {
        // Create one generated task per selected subtask
        for (Subtask subtask : subtasks) {
            Variant variant = getRandomUsableVariant(subtask);

            if (variant != null) {
                result.add(new GeneratedTask(chapter, subtask, variant));
            }
        }
    }

    /**
     * Returns a random usable variant of the given subtask.
     *
     * A variant is considered usable if it exists and contains a non-empty task text.
     * If no usable variant exists, the method returns null.
     *
     * @param subtask the subtask whose variants are checked
     * @return a random usable variant, or null if none is available
     */
    private static Variant getRandomUsableVariant(Subtask subtask) {
        // Reject invalid subtasks or subtasks without variants
        if (subtask == null || subtask.getVariants() == null || subtask.getVariants().isEmpty()) {
            return null;
        }

        List<Variant> usableVariants = new ArrayList<>();

        // Collect only variants that contain actual task text
        for (Variant variant : subtask.getVariants()) {
            if (variant == null) {
                continue;
            }

            String taskText = variant.getTaskText();

            if (taskText != null && !taskText.trim().isEmpty()) {
                usableVariants.add(variant);
            }
        }

        // Return null if no valid variant can be used for generation
        if (usableVariants.isEmpty()) {
            return null;
        }

        // Choose one usable variant randomly
        return usableVariants.get(RANDOM.nextInt(usableVariants.size()));
    }

    /**
     * Builds all valid combinations of task counts across difficulty levels.
     *
     * A combination is valid if the difference between the maximum and minimum
     * number of subtasks per difficulty does not exceed two.
     *
     * @param easySize the maximum number of easy subtasks
     * @param mediumSize the maximum number of medium subtasks
     * @param hardSize the maximum number of hard subtasks
     * @return a list of valid difficulty count combinations
     */
    private static List<int[]> getValidDifficultyCombinations(int easySize, int mediumSize, int hardSize) {
        List<int[]> combinations = new ArrayList<>();

        // Test all possible counts per difficulty and keep only balanced ones
        for (int e = 1; e <= easySize; e++) {
            for (int m = 1; m <= mediumSize; m++) {
                for (int h = 1; h <= hardSize; h++) {
                    int min = Math.min(e, Math.min(m, h));
                    int max = Math.max(e, Math.max(m, h));

                    if (max - min <= 2) {
                        combinations.add(new int[]{e, m, h});
                    }
                }
            }
        }

        return combinations;
    }

    /**
     * Finds one random subtask combination with the requested size and score.
     *
     * The method first collects all matching combinations and then returns
     * one of them at random.
     *
     * @param pool the available subtasks
     * @param count the number of subtasks to select
     * @param targetScore the required total score
     * @return a random matching combination, or null if none exists
     */
    private static List<Subtask> findRandomSubtaskCombinationByScore(List<Subtask> pool,
                                                                     int count,
                                                                     int targetScore) {
        List<List<Subtask>> matches = new ArrayList<>();
        collectMatchingCombinations(pool, count, targetScore, 0, new ArrayList<>(), matches);

        // Return null if no matching combination was found
        if (matches.isEmpty()) {
            return null;
        }

        return matches.get(RANDOM.nextInt(matches.size()));
    }

    /**
     * Recursively collects all subtask combinations that match a target score.
     *
     * The method builds possible combinations step by step and stores only those
     * whose total score equals the requested target score.
     *
     * @param pool the available subtasks
     * @param remaining the number of subtasks still to select
     * @param targetScore the required total score
     * @param index the current start index in the pool
     * @param current the current partial combination
     * @param matches the list collecting all matching combinations
     */
    private static void collectMatchingCombinations(List<Subtask> pool,
                                                    int remaining,
                                                    int targetScore,
                                                    int index,
                                                    List<Subtask> current,
                                                    List<List<Subtask>> matches) {
        // Check the score once the requested number of subtasks has been selected
        if (remaining == 0) {
            if (getTotalScore(current) == targetScore) {
                matches.add(new ArrayList<>(current));
            }
            return;
        }

        // Stop recursion if there are no more subtasks left to consider
        if (index >= pool.size()) {
            return;
        }

        // Extend the current combination with each remaining subtask candidate
        for (int i = index; i < pool.size(); i++) {
            current.add(pool.get(i));
            collectMatchingCombinations(pool, remaining - 1, targetScore, i + 1, current, matches);
            current.remove(current.size() - 1);
        }
    }

    /**
     * Calculates the total score of a list of subtasks.
     *
     * @param subtasks the subtasks whose scores are summed up
     * @return the total score of the given subtasks
     */
    private static int getTotalScore(List<Subtask> subtasks) {
        int total = 0;

        // Sum up the score value of each subtask in the list
        for (Subtask subtask : subtasks) {
            total += subtask.getScore();
        }

        return total;
    }

    /**
     * Checks whether a chapter is included in exam generation.
     *
     * @param chapter the chapter to check
     * @return true if the chapter is not null and marked as INCLUDE
     */
    private static boolean isIncluded(Chapter chapter) {
        return chapter != null && chapter.getExamAppearance() == ExamAppearance.INCLUDE;
    }

    /**
     * Stores the generated task selection for a single chapter.
     *
     * This helper class is used to wrap the final task list of a chapter
     * during regular exam generation.
     */
    private static class ChapterSelection {
        private final List<GeneratedTask> tasks;

        /**
         * Creates a new chapter selection with the generated tasks.
         *
         * @param tasks the generated tasks of the chapter
         */
        public ChapterSelection(List<GeneratedTask> tasks) {
            this.tasks = tasks;
        }

        /**
         * Returns the generated tasks of the chapter.
         *
         * @return the generated task list
         */
        public List<GeneratedTask> getTasks() {
            return tasks;
        }
    }
}