package com.randexgen.swe1_randexgen.app;

import com.randexgen.swe1_randexgen.datamodel.Exam;
import java.io.File;

/**
 * Simple static navigator that forwards navigation requests
 * to the single MainShellController instance.
 */
public final class AppNavigator {

    private static MainShellController shellController;

    private AppNavigator() {
    }

    public static void registerShell(MainShellController controller) {
        shellController = controller;
    }

    public static void showStartView() {
        if (shellController != null) {
            shellController.showHelloView();
        }
    }

    public static void showEditor(File xmlFile) {
        if (shellController != null) {
            shellController.showFrame2View(xmlFile);
        }
    }

    public static void showEditor(Exam exam, File xmlFile) {
        if (shellController != null) {
            shellController.showFrame2View(exam, xmlFile);
        }
    }

    public static void showPdf() {
        if (shellController != null) {
            shellController.showPdfView();
        }
    }
}