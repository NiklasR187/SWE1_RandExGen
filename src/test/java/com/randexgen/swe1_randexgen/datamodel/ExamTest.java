package com.randexgen.swe1_randexgen.datamodel;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Exam.
 */
class ExamTest {

    /**
     * Tests whether the constructor correctly assigns all fields.
     */
    @Test
    void constructor_shouldSetAllFieldsCorrectly() {
        List<Chapter> chapters = new ArrayList<>();
        chapters.add(new Chapter());

        Exam exam = new Exam("e1", "Exam Title", chapters);

        assertEquals("e1", exam.getId());
        assertEquals("Exam Title", exam.getTitle());
        assertEquals(chapters, exam.getChapters());
    }

    /**
     * Tests whether toString returns the title.
     */
    @Test
    void toString_shouldReturnTitle() {
        Exam exam = new Exam("e2", "My Exam", new ArrayList<>());

        assertEquals("My Exam", exam.toString());
    }

    /**
     * Tests whether setters and getters work correctly.
     */
    @Test
    void settersAndGetters_shouldWorkCorrectly() {
        Exam exam = new Exam();
        List<Chapter> chapters = new ArrayList<>();
        chapters.add(new Chapter());

        exam.setId("e3");
        exam.setTitle("Another Exam");
        exam.setChapters(chapters);

        assertEquals("e3", exam.getId());
        assertEquals("Another Exam", exam.getTitle());
        assertEquals(chapters, exam.getChapters());
    }
}