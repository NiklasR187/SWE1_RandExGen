package com.randexgen.swe1_randexgen.datamodel;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a subtask within a chapter.
 *
 * A subtask contains a title, score, difficulty level, exam type,
 * and a list of possible variants.
 */
public class Subtask {

    private String id;
    private String title;
    private double score;
    private DifficultyLevel difficultyLevel;
    private ExamType examType;
    private List<Variant> variants = new ArrayList<>();

    /**
     * Creates an empty subtask.
     */
    public Subtask() {
    }

    /**
     * Creates a subtask with all required fields.
     *
     * @param id the unique identifier of the subtask
     * @param title the title of the subtask
     * @param difficultyLevel the difficulty level of the subtask
     * @param examType the exam type the subtask belongs to
     * @param variants the list of variants for this subtask
     */
    public Subtask(String id, String title, DifficultyLevel difficultyLevel, ExamType examType, List<Variant> variants) {
        this.id = id;
        this.title = title;
        this.difficultyLevel = difficultyLevel;
        this.examType = examType;
        this.variants = variants;
    }

    /**
     * Returns the identifier of the subtask.
     *
     * @return the subtask ID
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the score of the subtask.
     *
     * @return the score value
     */
    public double getScore() {
        return score;
    }

    /**
     * Sets the score of the subtask.
     *
     * @param score the score value
     */
    public void setScore(double score) {
        this.score = score;
    }

    /**
     * Returns the title of the subtask.
     *
     * @return the subtask title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns the difficulty level of the subtask.
     *
     * @return the difficulty level
     */
    public DifficultyLevel getDifficultyLevel() {
        return difficultyLevel;
    }

    /**
     * Returns the exam type of the subtask.
     *
     * @return the exam type
     */
    public ExamType getExamType() {
        return examType;
    }

    /**
     * Returns all variants of the subtask.
     *
     * @return the list of variants
     */
    public List<Variant> getVariants() {
        return variants;
    }

    /**
     * Sets the identifier of the subtask.
     *
     * @param id the subtask ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Sets the title of the subtask.
     *
     * @param title the subtask title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Sets the difficulty level of the subtask.
     *
     * @param difficultyLevel the difficulty level
     */
    public void setDifficultyLevel(DifficultyLevel difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    /**
     * Sets the exam type of the subtask.
     *
     * @param examType the exam type
     */
    public void setExamType(ExamType examType) {
        this.examType = examType;
    }

    /**
     * Sets the variants of the subtask.
     *
     * @param variants the list of variants
     */
    public void setVariants(List<Variant> variants) {
        this.variants = variants;
    }

    /**
     * Returns a string representation of the subtask.
     *
     * The title is returned to simplify display in UI components.
     *
     * @return the subtask title
     */
    @Override
    public String toString() {
        return title;
    }
}