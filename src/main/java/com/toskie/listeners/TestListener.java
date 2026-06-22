package com.toskie.listeners;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.microsoft.playwright.Page;
import com.toskie.config.ConfigReader;
import com.toskie.factory.PlaywrightFactory;
import com.toskie.reports.AllureManager;
import com.toskie.reports.ExtentManager;
import com.toskie.reports.ExtentTestManager;
import com.toskie.utils_Layer.BrowserManager;
import org.testng.IRetryAnalyzer;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.SkipException;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * Enterprise test-level listener — all eight cross-cutting features in one place.
 *
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │  Feature Matrix                                                         │
 * │  ① Screenshot on failure  — FAIL label, full page, attached to report  │
 * │  ② Screenshot on skip     — SKIP label, genuine skips only (not retry) │
 * │  ③ Structured logging     — every event logged to console + Extent     │
 * │  ④ Duration tracking      — wall-clock ms per test, logged at finish   │
 * │  ⑤ Environment capture    — browser, headless, URL, title at start     │
 * │  ⑥ Browser capture        — URL + page title snapshot at test start    │
 * │  ⑦ Failure reason capture — exception type, message, root cause,       │
 * │                              categorisation, top-5 stack frames         │
 * │  ⑧ Retry integration      — detects retry-skip vs genuine skip,        │
 * │                              logs attempt number, remaining retries     │
 * └─────────────────────────────────────────────────────────────────────────┘
 *
 * Thread safety:
 *   TestNG creates ONE instance of this listener for the entire run.
 *   All per-test state is carried in a ThreadLocal ({@link TestContext}) so
 *   10 concurrent test threads never see each other's data.
 *
 * Registration in suite XML:
 *   <listener class-name="com.toskie.listeners.TestListener"/>
 *
 * Interaction with BaseTest:
 *   - BaseTest.@BeforeMethod calls ExtentManager.createTest() first.
 *   - onTestStart() calls ExtentManager.createTest() ONLY if the node is
 *     absent — covers standalone test classes that don't extend BaseTest.
 *   - onTestSuccess/Failure/Skipped log AFTER BaseTest.@AfterMethod so the
 *     report node is still open when the listener writes its outcome lines.
 */
public class TestListener implements ITestListener {

    private static final String TAG = "[TestListener]";

    // ═════════════════════════════════════════════════════════════════════════
    //  ThreadLocal context — one per running thread
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Per-test metadata snapshot. Stored in ThreadLocal so parallel threads
     * carry their own independent context with zero synchronisation needed.
     */
    private static final class TestContext {
        final long   startMs;
        final String methodName;
        final String className;
        final String qualifiedName;
        final String[] groups;
        int    retryAttempt;
        String browserLine;   // "CHROME | headless=false | thread=12"
        String currentUrl;    // URL at test start
        String pageTitle;     // page title at test start

        TestContext(ITestResult result) {
            this.startMs       = System.currentTimeMillis();
            this.methodName    = result.getMethod().getMethodName();
            this.className     = result.getTestClass().getRealClass().getSimpleName();
            this.qualifiedName = className + "." + methodName;
            this.groups        = result.getMethod().getGroups();
            this.retryAttempt  = result.getMethod().getCurrentInvocationCount();
        }

        long elapsedMs() { return System.currentTimeMillis() - startMs; }
    }

    private static final ThreadLocal<TestContext> CTX = new ThreadLocal<>();

    // ═════════════════════════════════════════════════════════════════════════
    //  onTestStart  ─── Feature ③ ④ ⑤ ⑥
    // ═════════════════════════════════════════════════════════════════════════

    @Override
    public void onTestStart(ITestResult result) {
        // Build and store per-thread context
        TestContext ctx = new TestContext(result);
        CTX.set(ctx);

        // ── Feature ⑤ ⑥: browser + URL snapshot ────────────────────────
        ctx.browserLine = buildBrowserLine();
        ctx.currentUrl  = captureUrl();
        ctx.pageTitle   = captureTitle();

        // ── Extent node ─────────────────────────────────────────────────
        // BaseTest.@BeforeMethod usually creates this first.
        // If not (standalone class), we create it here as a fallback.
        ExtentTest test = ExtentManager.getTest();
        if (test == null) {
            String desc = result.getMethod().getDescription();
            test = (desc != null && !desc.isBlank())
                    ? ExtentManager.createTest(ctx.qualifiedName, desc)
                    : ExtentManager.createTest(ctx.qualifiedName);
        }

        // ── Feature ③: structured Extent logging ────────────────────────
        test.log(Status.INFO, hr());
        test.log(Status.INFO, "<b>▶ START</b>  " + ctx.qualifiedName);
        test.log(Status.INFO, fmt("Groups",   groupsLabel(ctx.groups)));
        test.log(Status.INFO, fmt("Browser",  ctx.browserLine));
        test.log(Status.INFO, fmt("URL",      ctx.currentUrl));
        test.log(Status.INFO, fmt("Title",    ctx.pageTitle));
        test.log(Status.INFO, fmt("Thread",   String.valueOf(Thread.currentThread().getId())));
        test.log(Status.INFO, fmt("Time",     new SimpleDateFormat("HH:mm:ss.SSS").format(new Date())));

        // ── Feature ⑧: retry context ────────────────────────────────────
        if (ctx.retryAttempt > 0) {
            test.log(Status.WARNING,
                    fmt("Retry", "attempt " + ctx.retryAttempt
                            + " / " + ConfigReader.getMaxRetries()));
        }

        // ── Allure: stamp epic / feature / story / severity / context ──────
        AllureManager.applyTestLabels(ctx.qualifiedName, ctx.groups, ctx.className);

        // ── Reset step counter for Extent numbered steps ─────────────────
        ExtentTestManager.resetStepCounter();

        // ── Feature ③: console ──────────────────────────────────────────
        console("▶", "START ", ctx.qualifiedName,
                ctx.retryAttempt > 0 ? "[retry #" + ctx.retryAttempt + "]" : "");
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  onTestSuccess  ─── Feature ③ ④ ⑧
    // ═════════════════════════════════════════════════════════════════════════

    @Override
    public void onTestSuccess(ITestResult result) {
        TestContext ctx = CTX.get();
        long durationMs = ctx != null ? ctx.elapsedMs()
                : (result.getEndMillis() - result.getStartMillis());
        int  attempt    = ctx != null ? ctx.retryAttempt : 0;
        String name     = ctx != null ? ctx.qualifiedName : qualifiedName(result);

        // ── Extent ──────────────────────────────────────────────────────
        ExtentTest test = ExtentManager.getTest();
        if (test != null) {
            String msg = attempt > 0
                    ? "✓ PASSED on retry #" + attempt
                    : "✓ PASSED";
            test.log(Status.PASS,
                    msg + "  —  " + ExecutionListener.formatDuration(durationMs));
            test.log(Status.PASS, hr());
        }

        // ── Console ──────────────────────────────────────────────────────
        console("✓", "PASS  ", name,
                "[" + ExecutionListener.formatDuration(durationMs) + "]"
                + (attempt > 0 ? " after retry #" + attempt : ""));

        CTX.remove();
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  onTestFailure  ─── Feature ① ③ ④ ⑦ ⑧
    // ═════════════════════════════════════════════════════════════════════════

    @Override
    public void onTestFailure(ITestResult result) {
        TestContext ctx = CTX.get();
        long durationMs = ctx != null ? ctx.elapsedMs()
                : (result.getEndMillis() - result.getStartMillis());
        String name     = ctx != null ? ctx.qualifiedName : qualifiedName(result);

        // ── Feature ⑦: failure reason analysis ──────────────────────────
        Throwable thrown = result.getThrowable();
        FailureInfo info = analyzeFailure(thrown);

        ExtentTest test = ExtentManager.getTest();
        if (test != null) {
            test.log(Status.FAIL,
                    "<b>✗ FAILED</b>  —  " + ExecutionListener.formatDuration(durationMs));
            test.log(Status.FAIL, fmt("Category",  info.category));
            test.log(Status.FAIL, fmt("Exception", info.exceptionType));
            test.log(Status.FAIL, fmt("Message",   info.message));
            if (info.rootCause != null && !info.rootCause.equals(info.message)) {
                test.log(Status.FAIL, fmt("Root Cause", info.rootCause));
            }
            test.log(Status.FAIL,
                    "<b>Stack Trace (top 5):</b><br/><pre>" + info.stackTop + "</pre>");

            // Attach the full throwable for Extent's collapsible stack panel
            if (thrown != null) {
                test.fail(thrown);
            }
        }

        // ── Feature ①: screenshot on failure ────────────────────────────
        String screenshotPath = ScreenshotManager.captureOnFailure(name);
        attachScreenshot(test, screenshotPath, "Failure screenshot");
        AllureManager.attachScreenshotFile("Failure Screenshot", screenshotPath);

        // ── Feature ⑧: retry feedback ────────────────────────────────────
        IRetryAnalyzer analyzer = result.getMethod().getRetryAnalyzer(result);
        if (analyzer != null && test != null) {
            int remaining = ConfigReader.getMaxRetries()
                    - (ctx != null ? ctx.retryAttempt : 0);
            if (remaining > 0) {
                test.log(Status.WARNING,
                        "↻ Queued for retry — " + remaining
                        + " attempt(s) remaining");
            } else {
                test.log(Status.WARNING,
                        "↻ No retries remaining — marking as final failure");
            }
        }

        if (test != null) test.log(Status.FAIL, hr());

        // ── Console ──────────────────────────────────────────────────────
        console("✗", "FAIL  ", name,
                "[" + ExecutionListener.formatDuration(durationMs) + "] "
                + info.exceptionType + ": " + truncate(info.message, 100));

        CTX.remove();
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  onTestSkipped  ─── Feature ② ③ ④ ⑧
    // ═════════════════════════════════════════════════════════════════════════

    @Override
    public void onTestSkipped(ITestResult result) {
        TestContext ctx = CTX.get();
        long durationMs = ctx != null ? ctx.elapsedMs()
                : (result.getEndMillis() - result.getStartMillis());
        String name     = ctx != null ? ctx.qualifiedName : qualifiedName(result);
        int    attempt  = ctx != null ? ctx.retryAttempt  : 0;

        ExtentTest test = ExtentManager.getTest();
        // ── Ensure there is a test node even if onTestStart ran for a prior attempt ──
        if (test == null) {
            test = ExtentManager.createTest(name);
        }

        boolean retrySkip = isRetrySkip(result);

        if (retrySkip) {
            // ── Feature ⑧: retry-induced skip — do NOT screenshot ────────
            // The failure screenshot was already taken in onTestFailure for
            // this attempt; taking another one here would be a duplicate.
            if (test != null) {
                test.log(Status.SKIP,
                        "↻ RETRY SKIP — attempt " + attempt
                        + " / " + ConfigReader.getMaxRetries()
                        + "  —  " + ExecutionListener.formatDuration(durationMs));
            }
            console("↻", "RETRY ", name,
                    "attempt " + attempt + "/" + ConfigReader.getMaxRetries());

        } else {
            // ── Genuine skip (enabled=false, dependency, SkipException) ──
            String reason = resolveSkipReason(result);

            if (test != null) {
                test.log(Status.SKIP,
                        "<b>⊘ SKIPPED</b>  —  " + ExecutionListener.formatDuration(durationMs));
                test.log(Status.SKIP, fmt("Reason", reason));
            }

            // ── Feature ②: screenshot on genuine skip ────────────────────
            // The page may still carry useful state (e.g. partial navigation
            // before a dependency check failed). Screenshot provides evidence.
            String screenshotPath = ScreenshotManager.captureOnSkip(name);
            attachScreenshot(test, screenshotPath, "Skip screenshot");
            AllureManager.attachScreenshotFile("Skip Screenshot", screenshotPath);

            if (test != null) test.log(Status.SKIP, hr());

            console("⊘", "SKIP  ", name,
                    "[" + ExecutionListener.formatDuration(durationMs) + "] " + reason);
        }

        CTX.remove();
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  onTestFailedWithTimeout  ─── Feature ① ③ ④ ⑦
    // ═════════════════════════════════════════════════════════════════════════

    @Override
    public void onTestFailedWithTimeout(ITestResult result) {
        TestContext ctx = CTX.get();
        long durationMs = ctx != null ? ctx.elapsedMs()
                : (result.getEndMillis() - result.getStartMillis());
        String name = ctx != null ? ctx.qualifiedName : qualifiedName(result);

        ExtentTest test = ExtentManager.getTest();
        if (test != null) {
            test.log(Status.FAIL,
                    "<b>⏱ TIMEOUT</b>  — test exceeded time limit  ["
                    + ExecutionListener.formatDuration(durationMs) + "]");
            test.log(Status.FAIL,
                    fmt("Category", "Test Timeout (@Test timeOut exceeded)"));
            if (result.getThrowable() != null) {
                test.fail(result.getThrowable());
            }
        }

        String screenshotPath = ScreenshotManager.captureOnTimeout(name + "_TIMEOUT");
        attachScreenshot(test, screenshotPath, "Timeout screenshot");

        console("⏱", "TIMEOUT", name, ExecutionListener.formatDuration(durationMs));
        CTX.remove();
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  onTestFailedButWithinSuccessPercentage  ─── Feature ③
    // ═════════════════════════════════════════════════════════════════════════

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        String name = qualifiedName(result);
        ExtentTest test = ExtentManager.getTest();
        if (test != null) {
            test.log(Status.WARNING,
                    "⚠ FAILED but within @Test(successPercentage) threshold: " + name);
        }
        console("⚠", "PARTIAL", name, "within successPercentage threshold");
        CTX.remove();
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  Context-level events  ─── Feature ③
    // ═════════════════════════════════════════════════════════════════════════

    @Override
    public void onStart(ITestContext context) {
        String parallel = context.getCurrentXmlTest().getParallel() != null
                ? context.getCurrentXmlTest().getParallel().toString()
                : "none";
        System.out.printf("%s  Context  START : %-40s  parallel=%s%n",
                TAG, context.getName(), parallel);
    }

    @Override
    public void onFinish(ITestContext context) {
        int p = context.getPassedTests().size();
        int f = context.getFailedTests().size();
        int s = context.getSkippedTests().size();
        int t = p + f + s;
        System.out.printf("%s  Context FINISH : %-40s  P=%-4d F=%-4d S=%-4d / %-4d  (%.0f%%)%n",
                TAG, context.getName(),
                p, f, s, t,
                t > 0 ? (p * 100.0 / t) : 0.0);
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  Failure analysis  ─── Feature ⑦
    // ═════════════════════════════════════════════════════════════════════════

    private static final class FailureInfo {
        final String category;
        final String exceptionType;
        final String message;
        final String rootCause;
        final String stackTop;

        FailureInfo(String category, String exceptionType,
                    String message, String rootCause, String stackTop) {
            this.category      = category;
            this.exceptionType = exceptionType;
            this.message       = message;
            this.rootCause     = rootCause;
            this.stackTop      = stackTop;
        }
    }

    private static FailureInfo analyzeFailure(Throwable t) {
        if (t == null) {
            return new FailureInfo("Unknown",
                    "NoException", "(no throwable attached)", null, "(no stack)");
        }

        String category    = categorise(t);
        String exType      = t.getClass().getSimpleName();
        String msg         = t.getMessage() != null
                ? truncate(t.getMessage(), 600) : "(no message)";
        String rootCauseMsg = null;
        String stackTop    = formatStackTop(t, 5);

        // ── Unwrap root cause chain ───────────────────────────────────────
        Throwable cause = t.getCause();
        int depth = 0;
        while (cause != null && cause != t && depth < 5) {
            rootCauseMsg = cause.getClass().getSimpleName() + ": "
                    + truncate(String.valueOf(cause.getMessage()), 300);
            cause = cause.getCause();
            depth++;
        }

        return new FailureInfo(category, exType, msg, rootCauseMsg, stackTop);
    }

    /**
     * Categorises an exception into a human-readable failure type.
     * Drives the "Category" field shown at the top of every failure card
     * in the Extent report — makes triage fast without reading stack traces.
     */
    private static String categorise(Throwable t) {
        if (t == null) return "Unknown";
        String fqn  = t.getClass().getName();
        String msg  = String.valueOf(t.getMessage()).toLowerCase();

        // ── Playwright-specific ──────────────────────────────────────────
        if (fqn.contains("playwright")) {
            if (msg.contains("timeout") || fqn.contains("Timeout"))
                return "Playwright — Element/Navigation Timeout";
            if (msg.contains("element") || msg.contains("locator"))
                return "Playwright — Element Not Found / Interaction Failure";
            if (msg.contains("net::") || msg.contains("navigation"))
                return "Playwright — Navigation / Network Error";
            return "Playwright — Browser Error";
        }

        // ── Java assertion ───────────────────────────────────────────────
        if (fqn.contains("AssertionError") || fqn.contains("AssertError")
                || fqn.contains("testng.Assert"))
            return "Assertion Failure";

        // ── TestNG ───────────────────────────────────────────────────────
        if (fqn.contains("testng.SkipException"))
            return "Intentional Skip";

        // ── Common runtime ───────────────────────────────────────────────
        if (fqn.contains("NullPointerException"))
            return "Null Reference — check test data or page object initialisation";
        if (fqn.contains("TimeoutException") || msg.contains("timed out"))
            return "Timeout — element or operation exceeded wait limit";
        if (fqn.contains("ElementNotInteractableException")
                || msg.contains("not interactable"))
            return "Element Not Interactable — hidden, disabled, or off-screen";
        if (fqn.contains("StaleElementReferenceException"))
            return "Stale Element — DOM changed between locate and interact";
        if (fqn.contains("IOException") || fqn.contains("SocketException"))
            return "Network / IO Error";
        if (fqn.contains("ClassCastException"))
            return "Type Cast Error — likely wrong page object or locator mismatch";

        return "Runtime Exception — " + t.getClass().getSimpleName();
    }

    private static String formatStackTop(Throwable t, int maxFrames) {
        StackTraceElement[] frames = t.getStackTrace();
        if (frames == null || frames.length == 0) return "(no stack trace)";

        StringBuilder sb = new StringBuilder();
        int limit = Math.min(frames.length, maxFrames);
        for (int i = 0; i < limit; i++) {
            sb.append("at ").append(frames[i]).append("\n");
        }
        if (frames.length > maxFrames) {
            sb.append("... ").append(frames.length - maxFrames).append(" more frames");
        }
        return sb.toString();
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  Retry detection  ─── Feature ⑧
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Distinguishes a retry-induced skip from a genuine skip.
     *
     * Heuristic:
     *   Retry skip   → retry analyzer is attached AND a throwable is present
     *                  (the throwable is the original failure that triggered retry)
     *   Genuine skip → enabled=false, dependency failure, or SkipException with
     *                  no cause (explicitly thrown to skip the test intentionally)
     *
     * This is the most reliable heuristic available in the TestNG 7.x API
     * without private-field reflection.
     */
    private static boolean isRetrySkip(ITestResult result) {
        IRetryAnalyzer analyzer = result.getMethod().getRetryAnalyzer(result);
        if (analyzer == null) return false;

        Throwable t = result.getThrowable();
        if (t == null) return false;

        // An explicitly thrown SkipException (no cause) is an intentional skip
        if (t instanceof SkipException && t.getCause() == null) return false;

        return true;
    }

    private static String resolveSkipReason(ITestResult result) {
        Throwable t = result.getThrowable();
        if (t instanceof SkipException) {
            String msg = t.getMessage();
            return "SkipException: " + (msg != null ? msg : "(no message)");
        }
        if (t != null) {
            return t.getClass().getSimpleName() + ": "
                    + truncate(String.valueOf(t.getMessage()), 200);
        }
        if (!result.getMethod().getEnabled()) {
            return "@Test(enabled=false)";
        }
        return "Dependency failure or configuration-level skip";
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  Browser capture helpers  ─── Feature ⑤ ⑥
    // ═════════════════════════════════════════════════════════════════════════

    private static String buildBrowserLine() {
        try {
            String browser  = ConfigReader.getBrowser().toUpperCase();
            boolean headless = ConfigReader.isHeadless() || PlaywrightFactory.isCI();
            return browser + " | headless=" + headless
                    + " | thread=" + Thread.currentThread().getId();
        } catch (Exception e) {
            return "UNKNOWN | thread=" + Thread.currentThread().getId();
        }
    }

    private static String captureUrl() {
        try {
            Page p = PlaywrightFactory.getPage();
            if (p != null && !p.isClosed()) return p.url();
            p = BrowserManager.getPage();
            if (p != null && !p.isClosed()) return p.url();
        } catch (Exception ignored) {}
        return "(no page open)";
    }

    private static String captureTitle() {
        try {
            Page p = PlaywrightFactory.getPage();
            if (p != null && !p.isClosed()) {
                String title = p.title();
                return (title != null && !title.isBlank()) ? title : "(untitled)";
            }
            p = BrowserManager.getPage();
            if (p != null && !p.isClosed()) {
                String title = p.title();
                return (title != null && !title.isBlank()) ? title : "(untitled)";
            }
        } catch (Exception ignored) {}
        return "(no page open)";
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  Formatting helpers
    // ═════════════════════════════════════════════════════════════════════════

    private static void attachScreenshot(ExtentTest test, String path, String label) {
        if (path == null || test == null) return;
        try {
            test.addScreenCaptureFromPath(ScreenshotManager.toReportRelativePath(path));
            test.log(Status.INFO, label + ": " + path);
        } catch (Exception e) {
            System.err.printf("%s  Screenshot attach failed [%s]: %s%n",
                    TAG, label, e.getMessage());
        }
    }

    /** Prints one line to stdout in a fixed-column format for easy grep. */
    private static void console(String icon, String verb, String name, String detail) {
        System.out.printf("%s  [T%-4d] %s %-8s  %-70s  %s%n",
                TAG,
                Thread.currentThread().getId(),
                icon, verb,
                truncate(name, 70),
                detail);
    }

    /** Labelled key-value pair formatted for Extent HTML cells. */
    private static String fmt(String key, String value) {
        return String.format("<b>%-14s</b>%s", key + ":", value);
    }

    /** Horizontal rule for visual separation inside a test card. */
    private static String hr() {
        return "─".repeat(60);
    }

    private static String qualifiedName(ITestResult result) {
        return result.getTestClass().getRealClass().getSimpleName()
                + "." + result.getMethod().getMethodName();
    }

    private static String groupsLabel(String[] groups) {
        return (groups == null || groups.length == 0)
                ? "(none)"
                : Arrays.toString(groups);
    }

    private static String truncate(String s, int maxLen) {
        if (s == null) return "(null)";
        return s.length() <= maxLen ? s : s.substring(0, maxLen - 3) + "...";
    }
}
