package com.randexgen.swe1_randexgen.datamodel;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a chapter within an exam.
 *
 * A chapter contains multiple subtasks and defines whether it is included
 * in exam generation as well as the selected score for regular exams.
 */
public class Chapter {

    private String id;
    private String title;
    private ExamAppearance examAppearance;
    private List<Subtask> subtasks = new ArrayList<>();
    private double selectedRegularScore;

    /**
     * Creates an empty chapter.
     */
    public Chapter() {
    }

    /**
     * Creates a chapter with all required fields.
     *
     * @param id the unique identifier of the chapter
     * @param title the title of the chapter
     * @param examAppearance defines whether the chapter is included in the exam
     * @param subtasks the list of subtasks belonging to the chapter
     */
    public Chapter(String id, String title, ExamAppearance examAppearance, List<Subtask> subtasks) {
        this.id = id;
        this.title = title;
        this.examAppearance = examAppearance;
        this.subtasks = subtasks;
    }

    /**
     * Returns all subtasks of this chapter for a specific exam type.
     *
     * @param examType the exam type to filter by
     * @return a list of subtasks matching the given exam type
     */
    public List<Subtask> getSubtasksForExamType(ExamType examType) {
        List<Subtask> result = new ArrayList<>();

        // Filter all subtasks that belong to the given exam type
        for (Subtask subtask : subtasks) {
            if (subtask.getExamType() == examType) {
                result.add(subtask);
            }
        }

        return result;
    }

    /**
     * Returns all subtasks of this chapter filtered by exam type and difficulty.
     *
     * @param examType the exam type to filter by
     * @param difficultyLevel the difficulty level to filter by
     * @return a list of matching subtasks
     */
    public List<Subtask> getSubtasksByDifficulty(ExamType examType, DifficultyLevel difficultyLevel) {
        List<Subtask> result = new ArrayList<>();

        // Filter subtasks that match both exam type and difficulty level
        for (Subtask subtask : subtasks) {
            if (subtask.getExamType() == examType &&
                    subtask.getDifficultyLevel() == difficultyLevel) {
                result.add(subtask);
            }
        }

        return result;
    }

    /**
     * Returns the identifier of the chapter.
     *
     * @return the chapter ID
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the title of the chapter.
     *
     * @return the chapter title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns whether the chapter is included in the exam.
     *
     * @return the exam appearance setting
     */
    public ExamAppearance getExamAppearance() {
        return examAppearance;
    }

    /**
     * Returns all subtasks of the chapter.
     *
     * @return the list of subtasks
     */
    public List<Subtask> getSubtasks() {
        return subtasks;
    }

    /**
     * Returns the selected score for regular exams.
     *
     * @return the selected regular exam score
     */
    public double getSelectedRegularScore() {
        return selectedRegularScore;
    }

    /**
     * Sets the identifier of the chapter.
     *
     * @param id the chapter ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Sets the title of the chapter.
     *
     * @param title the chapter title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Sets whether the chapter is included in the exam.
     *
     * @param examAppearance the exam appearance setting
     */
    public void setExamAppearance(ExamAppearance examAppearance) {
        this.examAppearance = examAppearance;
    }

    /**
     * Sets the subtasks of the chapter.
     *
     * @param subtasks the list of subtasks
     */
    public void setSubtasks(List<Subtask> subtasks) {
        this.subtasks = subtasks;
    }

    /**
     * Sets the selected score for regular exams.
     *
     * @param selectedRegularScore the selected score
     */
    public void setSelectedRegularScore(double selectedRegularScore) {
        this.selectedRegularScore = selectedRegularScore;
    }

    /**
     * Returns a string representation of the chapter.
     *
     * The title is returned to simplify display in UI components.
     *
     * @return the chapter title
     */
    @Override
    public String toString() {
        return title;
    }
}