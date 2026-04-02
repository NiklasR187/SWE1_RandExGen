package com.randexgen.swe1_randexgen.datamodel;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an exam consisting of multiple chapters.
 *
 * An exam contains a title, an identifier, and a list of chapters
 * that define the structure of the exam.
 */
public class Exam {

    private String id;
    private String title;
    private List<Chapter> chapters = new ArrayList<>();

    /**
     * Creates an empty exam.
     */
    public Exam() {
    }

    /**
     * Creates an exam with all required fields.
     *
     * @param id the unique identifier of the exam
     * @param title the title of the exam
     * @param chapters the list of chapters belonging to the exam
     */
    public Exam(String id, String title, List<Chapter> chapters) {
        this.id = id;
        this.title = title;
        this.chapters = chapters;
    }

    /**
     * Returns the identifier of the exam.
     *
     * @return the exam ID
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the title of the exam.
     *
     * @return the exam title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns all chapters of the exam.
     *
     * @return the list of chapters
     */
    public List<Chapter> getChapters() {
        return chapters;
    }

    /**
     * Sets the identifier of the exam.
     *
     * @param id the exam ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Sets the title of the exam.
     *
     * @param title the exam title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Sets the chapters of the exam.
     *
     * @param chapters the list of chapters
     */
    public void setChapters(List<Chapter> chapters) {
        this.chapters = chapters;
    }

    /**
     * Returns a string representation of the exam.
     *
     * The title is returned to simplify display in UI components.
     *
     * @return the exam title
     */
    @Override
    public String toString() {
        return title;
    }
}