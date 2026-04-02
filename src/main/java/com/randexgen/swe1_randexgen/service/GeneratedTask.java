package com.randexgen.swe1_randexgen.service;

import com.randexgen.swe1_randexgen.datamodel.Chapter;
import com.randexgen.swe1_randexgen.datamodel.Subtask;
import com.randexgen.swe1_randexgen.datamodel.Variant;

/**
 * Represents a generated exam task consisting of a chapter, subtask, and variant.
 *
 * This class is used to store the final task selection that will later be
 * displayed in the application or exported to a PDF document.
 */
public class GeneratedTask {

    private final Chapter chapter;
    private final Subtask subtask;
    private final Variant variant;

    /**
     * Creates a new generated task with its associated chapter, subtask, and variant.
     *
     * @param chapter the chapter from which the task originates
     * @param subtask the selected subtask
     * @param variant the selected variant of the subtask
     */
    public GeneratedTask(Chapter chapter, Subtask subtask, Variant variant) {
        // Store the references of the generated task components
        this.chapter = chapter;
        this.subtask = subtask;
        this.variant = variant;
    }

    /**
     * Returns the chapter to which this generated task belongs.
     *
     * @return the associated chapter
     */
    public Chapter getChapter() {
        return chapter;
    }

    /**
     * Returns the selected subtask of this generated task.
     *
     * @return the associated subtask
     */
    public Subtask getSubtask() {
        return subtask;
    }

    /**
     * Returns the selected variant of this generated task.
     *
     * @return the associated variant
     */
    public Variant getVariant() {
        return variant;
    }
}