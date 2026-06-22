package com.toskie.listeners;

import com.microsoft.playwright.Page;
import com.toskie.constants.FrameworkConstants;
import com.toskie.factory.PlaywrightFactory;
import com.toskie.utils_Layer.BrowserManager;

import java.io.File;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Thread-safe screenshot utility shared across all listeners.
 *
 * Page resolution order:
 *   1. PlaywrightFactory.getPage()  — enterprise tests (com.toskie.base.BaseTest)
 *   2. BrowserManager.getPage()     — legacy tests (BaseTest_Layer.BaseTest, all 823 methods)
 *
 * Both sources are ThreadLocal so resolution is always per-thread correct
 * under parallel="classes" execution with 10 concurrent threads.
 *
 * Callers receive an absolute file path on success, null if no page is
 * available or if the capture throws. Callers must not assume a non-null
 * return — they should guard with a null-check before attaching to the report.
 */
public final class ScreenshotManager {

    private ScreenshotManager() {}

    private static final String TS_FORMAT = "dd_MMM_yyyy_HH_mm_ss_SSS";

    // ─── Public API ───────────────────────────────────────────────────────────

    public static String captureOnFailure(String testName) {
        return capture(testName, "FAIL");
    }

    public static String captureOnSkip(String testName) {
        return capture(testName, "SKIP");
    }

    public static String captureOnTimeout(String testName) {
        return capture(testName, "TIMEOUT");
    }

    /**
     * Captures a full-page screenshot and saves it under SnapShots/.
     *
     * @param testName  used in the file name (sanitised — special chars replaced)
     * @param label     suffix tag: FAIL | SKIP | TIMEOUT | MANUAL
     * @return absolute path of the saved PNG, or null on any error
     */
    public static String capture(String testName, String label) {
        Page page = resolvePage();
        if (page == null || page.isClosed()) return null;

        try {
            String dir  = FrameworkConstants.SCREENSHOTS_DIR;
            new File(dir).mkdirs();

            String ts   = new SimpleDateFormat(TS_FORMAT).format(new Date());
            String safe = sanitiseName(testName);
            String path = dir + safe + "_" + label + "_" + ts + ".png";

            page.screenshot(new Page.ScreenshotOptions()
                    .setPath(Paths.get(path))
                    .setFullPage(false));

            return path;

        } catch (Exception e) {
            System.err.printf("[ScreenshotManager] Thread[%d] %s capture failed: %s%n",
                    Thread.currentThread().getId(), label, e.getMessage());
            return null;
        }
    }

    /**
     * Converts an absolute screenshot path to the relative form expected by
     * ExtentReports when the report is under Reports/HTML/ and screenshots
     * are under SnapShots/.
     *
     * Reports/HTML/report.html  →  ../../SnapShots/testName_FAIL_ts.png
     */
    public static String toReportRelativePath(String absolutePath) {
        if (absolutePath == null) return null;
        return "../../SnapShots/" + new File(absolutePath).getName();
    }

    // ─── Private helpers ──────────────────────────────────────────────────────

    /**
     * Resolves the live Page for the calling thread.
     * Tries enterprise ThreadLocal first; falls back to legacy ThreadLocal.
     * Returns null (and does NOT throw) if neither has a live page.
     */
    private static Page resolvePage() {
        try {
            Page p = PlaywrightFactory.getPage();
            if (p != null && !p.isClosed()) return p;
        } catch (Exception ignored) {}

        try {
            Page p = BrowserManager.getPage();
            if (p != null && !p.isClosed()) return p;
        } catch (Exception ignored) {}

        return null;
    }

    /** Replaces characters that are illegal in Windows/Linux file names. */
    private static String sanitiseName(String name) {
        if (name == null || name.isBlank()) return "test";
        return name.replaceAll("[^a-zA-Z0-9_\\-.]", "_")
                   .replaceAll("_{2,}", "_")
                   .substring(0, Math.min(name.length(), 100));
    }
}
