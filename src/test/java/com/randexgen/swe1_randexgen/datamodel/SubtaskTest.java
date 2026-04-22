package com.randexgen.swe1_randexgen.datamodel;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Subtask.
 */
class SubtaskTest {

    /**
     * Tests whether the constructor correctly assigns all fields.
     */
    @Test
    void constructor_shouldSetAllFieldsCorrectly() {
        List<Variant> variants = new ArrayList<>();
        variants.add(new Variant("v1", "Task", "Answer", "Solution"));

        Subtask subtask = new Subtask(
                "s1",
                "Subtask Title",
                DifficultyLevel.MEDIUM,
                ExamType.REGULAR,
                variants
        );

        assertEquals("s1", subtask.getId());
        assertEquals("Subtask Title", subtask.getTitle());
        assertEquals(DifficultyLevel.MEDIUM, subtask.getDifficultyLevel());
        assertEquals(ExamType.REGULAR, subtask.getExamType());
        assertEquals(variants, subtask.getVariants());
    }

    /**
     * Tests whether toString returns the title.
     */
    @Test
    void toString_shouldReturnTitle() {
        Subtask subtask = new Subtask(
                "s1",
                "My Subtask",
                DifficultyLevel.EASY,
                ExamType.PRACTICE,
                new ArrayList<>()
        );

        assertEquals("My Subtask", subtask.toString());
    }

    /**
     * Tests whether setters and getters work correctly.
     */
    @Test
    void settersAndGetters_shouldWorkCorrectly() {
        Subtask subtask = new Subtask();
        List<Variant> variants = new ArrayList<>();
        variants.add(new Variant());

        subtask.setId("s2");
        subtask.setTitle("Another Title");
        subtask.setDifficultyLevel(DifficultyLevel.HARD);
        subtask.setExamType(ExamType.PRACTICE);
        subtask.setVariants(variants);

        assertEquals("s2", subtask.getId());
        assertEquals("Another Title", subtask.getTitle());
        assertEquals(DifficultyLevel.HARD, subtask.getDifficultyLevel());
        assertEquals(ExamType.PRACTICE, subtask.getExamType());
        assertEquals(variants, subtask.getVariants());
    }
}