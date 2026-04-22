package com.randexgen.swe1_randexgen.service;

import com.randexgen.swe1_randexgen.datamodel.Chapter;
import com.randexgen.swe1_randexgen.datamodel.Subtask;
import com.randexgen.swe1_randexgen.datamodel.Variant;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the GeneratedTask class.
 *
 * These tests verify that the constructor, getters and toString method
 * work correctly.
 */
class GeneratedTaskTest {

    /**
     * Tests whether the constructor correctly assigns
     * the given chapter, subtask and variant.
     */
    @Test
    void constructor_shouldSetAllFieldsCorrectly() {
        Chapter chapter = new Chapter();
        Subtask subtask = new Subtask();
        Variant variant = new Variant();

        GeneratedTask task = new GeneratedTask(chapter, subtask, variant);

        assertSame(chapter, task.getChapter());
        assertSame(subtask, task.getSubtask());
        assertSame(variant, task.getVariant());
    }

    /**
     * Tests whether the getters return exactly the objects
     * that were passed into the constructor.
     */
    @Test
    void getters_shouldReturnValues() {
        Chapter chapter = new Chapter();
        Subtask subtask = new Subtask();
        Variant variant = new Variant();

        GeneratedTask task = new GeneratedTask(chapter, subtask, variant);

        assertSame(chapter, task.getChapter());
        assertSame(subtask, task.getSubtask());
        assertSame(variant, task.getVariant());
    }

    /**
     * Tests whether the toString method returns a non-null string.
     */
    @Test
    void toString_shouldNotBeNull() {
        GeneratedTask task = new GeneratedTask(
                new Chapter(),
                new Subtask(),
                new Variant()
        );

        assertNotNull(task.toString());
    }
}