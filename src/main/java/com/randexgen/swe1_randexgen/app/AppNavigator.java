package com.randexgen.swe1_randexgen.app;

import com.randexgen.swe1_randexgen.datamodel.Exam;
import java.io.File;

/**
 * AppNavigator is a simple static navigation helper.
 *
 * It forwards navigation requests to a single instance of
 * MainShellController, which manages the currently displayed view.
 *
 * This allows other parts of the application to trigger navigation
 * without directly depending on the UI controller.
 */
public final class AppNavigator {

    /** Reference to the main shell controller (set once at startup) */
    private static MainShellController shellController;

    /**
     * Private constructor to prevent instantiation.
     * This class is intended to be used statically.
     */
    private AppNavigator() {
    }

    /**
     * Registers the main shell controller.
     * Must be called once during application initialization.
     *
     * @param controller the MainShellController instance
     */
    public static void registerShell(MainShellController controller) {
        shellController = controller;
    }

    /**
     * Navigates to the start view (Hello View).
     */
    public static void showStartView() {
        if (shellController != null) {
            shellController.showHelloView();
        }
    }

    /**
     * Opens the editor view and loads the given XML file.
     *
     * @param xmlFile the XML file to load
     */
    public static void showEditor(File xmlFile) {
        if (shellController != null) {
            shellController.showFrame2View(xmlFile);
        }
    }

    /**
     * Opens the editor view with an existing exam and XML file.
     *
     * @param exam    the current exam object
     * @param xmlFile the associated XML file
     */
    public static void showEditor(Exam exam, File xmlFile) {
        if (shellController != null) {
            shellController.showFrame2View(exam, xmlFile);
        }
    }

    /**
     * Navigates to the PDF view.
     */
    public static void showPdf() {
        if (shellController != null) {
            shellController.showPdfView();
        }
    }
}