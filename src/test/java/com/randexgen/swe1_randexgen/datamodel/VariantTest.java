package com.randexgen.swe1_randexgen.datamodel;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Variant.
 */
class VariantTest {

    /**
     * Tests whether the full constructor correctly assigns all fields.
     */
    @Test
    void constructor_shouldSetAllFieldsCorrectly() {
        Variant variant = new Variant("v1", "Task text", "Answer text", "Solution text");

        assertEquals("v1", variant.getId());
        assertEquals("Task text", variant.getTaskText());
        assertEquals("Answer text", variant.getAnswerText());
        assertEquals("Solution text", variant.getSolutionText());
    }

    /**
     * Tests whether toString returns the expected text format.
     */
    @Test
    void toString_shouldContainIdAndTaskText() {
        Variant variant = new Variant("v1", "Task text", "Answer text", "Solution text");

        String result = variant.toString();

        assertEquals("Variant{id='v1', taskText='Task text'}", result);
    }

    /**
     * Tests whether setters and getters work correctly.
     */
    @Test
    void settersAndGetters_shouldWorkCorrectly() {
        Variant variant = new Variant();

        variant.setId("v2");
        variant.setTaskText("New task");
        variant.setAnswerText("New answer");
        variant.setSolutionText("New solution");

        assertEquals("v2", variant.getId());
        assertEquals("New task", variant.getTaskText());
        assertEquals("New answer", variant.getAnswerText());
        assertEquals("New solution", variant.getSolutionText());
    }
}