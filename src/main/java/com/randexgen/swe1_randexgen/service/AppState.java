package com.randexgen.swe1_randexgen.service;

import com.randexgen.swe1_randexgen.datamodel.Exam;

import java.io.File;

/**
 * Stores the current application state.
 *
 * This class keeps track of the currently loaded exam and the associated XML file,
 * allowing different parts of the application to access shared state information.
 */
public class AppState {

    private static Exam currentExam;
    private static File currentXmlFile;

    /**
     * Returns the currently loaded exam.
     *
     * @return the current exam, or null if none is loaded
     */
    public static Exam getCurrentExam() {
        return currentExam;
    }

    /**
     * Sets the currently loaded exam.
     *
     * @param currentExam the exam to store in the application state
     */
    public static void setCurrentExam(Exam currentExam) {
        // Update the global reference to the currently active exam
        AppState.currentExam = currentExam;
    }

    /**
     * Returns the XML file associated with the current exam.
     *
     * @return the current XML file, or null if none is set
     */
    public static File getCurrentXmlFile() {
        return currentXmlFile;
    }

    /**
     * Sets the XML file associated with the current exam.
     *
     * @param currentXmlFile the XML file to store
     */
    public static void setCurrentXmlFile(File currentXmlFile) {
        // Store the reference to the currently used XML file
        AppState.currentXmlFile = currentXmlFile;
    }

    /**
     * Resets the application state.
     *
     * This method clears the current exam and XML file,
     * for example when returning to the start screen.
     */
    public static void reset() {
        // Clear all stored state values
        currentExam = null;
        currentXmlFile = null;
    }
}