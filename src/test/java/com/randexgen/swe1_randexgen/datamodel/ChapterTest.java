package com.randexgen.swe1_randexgen.datamodel;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Chapter.
 */
class ChapterTest {

    /**
     * Tests whether the constructor correctly assigns all fields.
     */
    @Test
    void constructor_shouldSetAllFieldsCorrectly() {
        List<Subtask> subtasks = new ArrayList<>();
        subtasks.add(new Subtask());

        Chapter chapter = new Chapter(
                "c1",
                "Chapter Title",
                ExamAppearance.INCLUDE,
                subtasks
        );

        assertEquals("c1", chapter.getId());
        assertEquals("Chapter Title", chapter.getTitle());
        assertEquals(ExamAppearance.INCLUDE, chapter.getExamAppearance());
        assertEquals(subtasks, chapter.getSubtasks());
    }

    /**
     * Tests whether toString returns the title.
     */
    @Test
    void toString_shouldReturnTitle() {
        Chapter chapter = new Chapter(
                "c2",
                "My Chapter",
                ExamAppearance.EXCLUDE,
                new ArrayList<>()
        );

        assertEquals("My Chapter", chapter.toString());
    }

    /**
     * Tests whether getSubtasksForExamType returns only subtasks
     * with the requested exam type.
     */
    @Test
    void getSubtasksForExamType_shouldReturnOnlyMatchingSubtasks() {
        Subtask practiceSubtask = new Subtask(
                "s1",
                "Practice",
                DifficultyLevel.EASY,
                ExamType.PRACTICE,
                new ArrayList<>()
        );

        Subtask regularSubtask = new Subtask(
                "s2",
                "Regular",
                DifficultyLevel.MEDIUM,
                ExamType.REGULAR,
                new ArrayList<>()
        );

        List<Subtask> subtasks = new ArrayList<>();
        subtasks.add(practiceSubtask);
        subtasks.add(regularSubtask);

        Chapter chapter = new Chapter(
                "c3",
                "Filter Chapter",
                ExamAppearance.INCLUDE,
                subtasks
        );

        List<Subtask> result = chapter.getSubtasksForExamType(ExamType.PRACTICE);

        assertEquals(1, result.size());
        assertEquals("s1", result.get(0).getId());
        assertEquals(ExamType.PRACTICE, result.get(0).getExamType());
    }

    /**
     * Tests whether getSubtasksForExamType returns an empty list
     * when no subtask matches the requested exam type.
     */
    @Test
    void getSubtasksForExamType_shouldReturnEmptyList_whenNothingMatches() {
        Subtask regularSubtask = new Subtask(
                "s2",
                "Regular",
                DifficultyLevel.MEDIUM,
                ExamType.REGULAR,
                new ArrayList<>()
        );

        List<Subtask> subtasks = new ArrayList<>();
        subtasks.add(regularSubtask);

        Chapter chapter = new Chapter(
                "c4",
                "No Match Chapter",
                ExamAppearance.INCLUDE,
                subtasks
        );

        List<Subtask> result = chapter.getSubtasksForExamType(ExamType.PRACTICE);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /**
     * Tests whether setters and getters work correctly.
     */
    @Test
    void settersAndGetters_shouldWorkCorrectly() {
        Chapter chapter = new Chapter();
        List<Subtask> subtasks = new ArrayList<>();
        subtasks.add(new Subtask());

        chapter.setId("c5");
        chapter.setTitle("Another Chapter");
        chapter.setExamAppearance(ExamAppearance.INCLUDE);
        chapter.setSubtasks(subtasks);

        assertEquals("c5", chapter.getId());
        assertEquals("Another Chapter", chapter.getTitle());
        assertEquals(ExamAppearance.INCLUDE, chapter.getExamAppearance());
        assertEquals(subtasks, chapter.getSubtasks());
    }
}