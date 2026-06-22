package com.toskie.constants;

/**
 * Single source of truth for all framework-level constants.
 * Replaces magic numbers and scattered string literals across the codebase.
 */
public final class FrameworkConstants {

    private FrameworkConstants() {}

    // ─── Browser names ────────────────────────────────────────────────────────
    public static final String CHROME   = "chrome";
    public static final String EDGE     = "edge";
    public static final String FIREFOX  = "firefox";
    public static final String WEBKIT   = "webkit";
    public static final String CHROMIUM = "chromium";

    // ─── Timeouts (milliseconds) ──────────────────────────────────────────────
    public static final int TIMEOUT_DEFAULT    = 30_000;
    public static final int TIMEOUT_ELEMENT    = 10_000;
    public static final int TIMEOUT_PAGE_LOAD  = 15_000;
    public static final int TIMEOUT_NAVIGATION = 15_000;
    public static final int TIMEOUT_API        = 20_000;
    public static final int TIMEOUT_TOAST      = 10_000;
    public static final int TIMEOUT_DIALOG     =  5_000;
    public static final int TIMEOUT_SHORT      =  3_000;
    public static final int TIMEOUT_ANIMATION  =  2_000;

    // ─── Retry settings ───────────────────────────────────────────────────────
    public static final int  MAX_RETRY_ATTEMPTS  = 3;
    public static final int  RETRY_INITIAL_DELAY = 500;
    public static final long RETRY_MAX_DELAY     = 5_000L;

    // ─── File output paths ────────────────────────────────────────────────────
    private static final String ROOT = System.getProperty("user.dir") + "/";
    public static final String SCREENSHOTS_DIR    = ROOT + "SnapShots/";
    public static final String REPORTS_HTML       = ROOT + "Reports/HTML/";
    public static final String REPORTS_PDF        = ROOT + "Reports/PDF/";
    public static final String REPORTS_CHARTS     = ROOT + "Reports/Charts/";
    public static final String TEST_DATA_DIR      = ROOT + "src/test/resources/testdata/";
    public static final String ALLURE_RESULTS_DIR = System.getProperty(
            "allure.results.directory", ROOT + "target/allure-results/");

    // ─── Report metadata ─────────────────────────────────────────────────────
    public static final String REPORT_TITLE   = "Toskie Web Automation Report";
    public static final String REPORT_NAME    = "Toskie QA Dashboard";
    public static final String PROJECT_NAME   = "Toskie Web Automation";
    public static final String FRAMEWORK_VER  = "Playwright 1.53.0 + Java 21 + TestNG 7.9.0";

    // ─── Mobile device profile keys ───────────────────────────────────────────
    public static final String DEVICE_IPHONE_14  = "iPhone 14";
    public static final String DEVICE_IPHONE_12  = "iPhone 12";
    public static final String DEVICE_PIXEL_7    = "Pixel 7";
    public static final String DEVICE_PIXEL_5    = "Pixel 5";
    public static final String DEVICE_GALAXY_S22 = "Galaxy S22";
    public static final String DEVICE_IPAD_PRO   = "iPad Pro";
    public static final String DEVICE_DESKTOP    = "Desktop 1080p";

    // ─── Test group tags (mirrors TestGroups.java, available without instantiation) ─
    public static final String GROUP_SMOKE      = "smoke";
    public static final String GROUP_REGRESSION = "regression";
    public static final String GROUP_API        = "api";
    public static final String GROUP_E2E        = "e2e";
    public static final String GROUP_SECURITY   = "security";
    public static final String GROUP_PERF       = "performance";
    public static final String GROUP_P0         = "p0";
    public static final String GROUP_P1         = "p1";
    public static final String GROUP_P2         = "p2";
    public static final String GROUP_P3         = "p3";

    // ─── Environment labels ───────────────────────────────────────────────────
    public static final String ENV_DEV     = "dev";
    public static final String ENV_QA      = "qa";
    public static final String ENV_STAGING = "staging";
    public static final String ENV_PROD    = "prod";

    // ─── CI/CD environment variable names ────────────────────────────────────
    public static final String CI_ENV_VAR      = "CI";
    public static final String HEADLESS_ENV_VAR = "HEADLESS";
    public static final String BROWSER_ENV_VAR  = "BROWSER";
}
