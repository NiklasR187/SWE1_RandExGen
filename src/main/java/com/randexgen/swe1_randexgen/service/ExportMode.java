package com.randexgen.swe1_randexgen.service;

/**
 * Defines the export mode for PDF generation.
 *
 * Determines whether the exported document contains empty answer fields
 * for students or the corresponding solutions.
 */
public enum ExportMode {

    /**
     * Exports the exam with answer fields for students.
     */
    EXAM,

    /**
     * Exports the exam including the correct solutions.
     */
    SOLUTION
}