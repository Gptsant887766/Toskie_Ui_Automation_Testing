package com.toskie.listeners;

import org.testng.IExecutionListener;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * JVM-level execution listener — the outermost boundary of a TestNG run.
 *
 * Fires ONCE when the JVM begins executing any test suite, and ONCE when
 * the entire run finishes (after all ISuiteListener.onFinish calls).
 * In master.xml (suite-of-suites), this still fires only once.
 *
 * Responsibilities:
 *   - Print startup banner with full environment snapshot (OS, Java, heap,
 *     CPUs, hostname, CI/CD environment, timestamp)
 *   - Print final execution summary across ALL suites (total P/F/S, rate,
 *     wall-clock time)
 *
 * Cross-suite totals are accumulated by SuiteListener into the static
 * AtomicIntegers below, which this class reads in onExecutionFinish().
 *
 * Registration in suite XML:
 *   <listener class-name="com.toskie.listeners.ExecutionListener"/>
 *
 * Thread safety: all mutable state is AtomicInteger / AtomicLong.
 */
public class ExecutionListener implements IExecutionListener {

    // ─── Cross-suite accumulators (written by SuiteListener, read here) ───────
    static final AtomicInteger TOTAL_PASSED  = new AtomicInteger(0);
    static final AtomicInteger TOTAL_FAILED  = new AtomicInteger(0);
    static final AtomicInteger TOTAL_SKIPPED = new AtomicInteger(0);
    static final AtomicLong    RUN_START_MS  = new AtomicLong(0);

    private static final String BORDER  = "═".repeat(74);
    private static final String DIVIDER = "─".repeat(74);

    // ═════════════════════════════════════════════════════════════════════════
    //  onExecutionStart — fires once before any suite begins
    // ═════════════════════════════════════════════════════════════════════════

    @Override
    public void onExecutionStart() {
        RUN_START_MS.set(System.currentTimeMillis());

        System.out.println();
        System.out.println(BORDER);
        System.out.printf("  %-20s %s%n", "TOSKIE AUTOMATION",  "Framework v2.0 — Enterprise Edition");
        System.out.printf("  %-20s %s%n", "Execution started:", timestamp());
        System.out.println(DIVIDER);

        // ── Host and OS ──────────────────────────────────────────────────
        System.out.printf("  %-20s %s%n", "Host:",
                resolveHostname());
        System.out.printf("  %-20s %s %s (%s)%n", "OS:",
                System.getProperty("os.name"),
                System.getProperty("os.arch"),
                System.getProperty("os.version"));

        // ── JVM ─────────────────────────────────────────────────────────
        System.out.printf("  %-20s %s  [%s]%n", "Java:",
                System.getProperty("java.version"),
                System.getProperty("java.vendor"));
        System.out.printf("  %-20s %d MB max  /  %d MB free%n", "JVM Heap:",
                Runtime.getRuntime().maxMemory()   / (1024 * 1024),
                Runtime.getRuntime().freeMemory()  / (1024 * 1024));
        System.out.printf("  %-20s %d cores%n", "CPUs:",
                Runtime.getRuntime().availableProcessors());

        // ── Workspace ────────────────────────────────────────────────────
        System.out.printf("  %-20s %s%n", "Work Dir:",
                System.getProperty("user.dir"));

        // ── CI/CD ────────────────────────────────────────────────────────
        System.out.printf("  %-20s %s%n", "Environment:", detectCiLabel());
        String buildId = System.getenv("BUILD_NUMBER");
        if (buildId != null) {
            System.out.printf("  %-20s %s%n", "Build Number:", buildId);
        }
        String gitBranch = System.getenv("BRANCH_NAME");
        if (gitBranch == null) gitBranch = System.getenv("GITHUB_REF_NAME");
        if (gitBranch != null) {
            System.out.printf("  %-20s %s%n", "Branch:", gitBranch);
        }

        System.out.println(BORDER);
        System.out.println();
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  onExecutionFinish — fires once after all suites complete
    // ═════════════════════════════════════════════════════════════════════════

    @Override
    public void onExecutionFinish() {
        long wallMs  = System.currentTimeMillis() - RUN_START_MS.get();
        int  passed  = TOTAL_PASSED.get();
        int  failed  = TOTAL_FAILED.get();
        int  skipped = TOTAL_SKIPPED.get();
        int  total   = passed + failed + skipped;
        double rate  = total > 0 ? (passed * 100.0 / total) : 0.0;

        // ASCII pass/fail bar (40 chars wide)
        int barWidth = 40;
        int passBar  = total > 0 ? (int) Math.round(passed  * barWidth / (double) total) : 0;
        int failBar  = total > 0 ? (int) Math.round(failed  * barWidth / (double) total) : 0;
        int skipBar  = barWidth - passBar - failBar;
        String bar = "█".repeat(Math.max(0, passBar))
                   + "▓".repeat(Math.max(0, failBar))
                   + "░".repeat(Math.max(0, skipBar));

        System.out.println();
        System.out.println(BORDER);
        System.out.printf("  %-20s %s%n", "EXECUTION COMPLETE:", timestamp());
        System.out.println(DIVIDER);
        System.out.printf("  %-20s %d%n",    "Total Tests:",   total);
        System.out.printf("  %-20s %d%n",    "✓ Passed:",      passed);
        System.out.printf("  %-20s %d%n",    "✗ Failed:",      failed);
        System.out.printf("  %-20s %d%n",    "⊘ Skipped:",     skipped);
        System.out.printf("  %-20s %.1f%%%n", "Pass Rate:",     rate);
        System.out.printf("  %-20s %s%n",    "Wall Time:",     formatDuration(wallMs));
        System.out.println(DIVIDER);
        System.out.printf("  [%s] %s%n", bar,
                String.format("P:%-4d F:%-4d S:%-4d", passed, failed, skipped));
        System.out.println(BORDER);
        System.out.println();

        // ── Exit hint for CI ─────────────────────────────────────────────
        if (failed > 0) {
            System.out.printf("[ExecutionListener] ⚠  %d test(s) FAILED — "
                    + "check Reports/HTML/ for screenshot attachments.%n", failed);
        } else if (total > 0) {
            System.out.println("[ExecutionListener] ✓ All tests passed.");
        }
    }

    // ─── Shared utilities (used by SuiteListener and TestListener) ───────────

    static String formatDuration(long ms) {
        if (ms < 1_000)        return ms + "ms";
        if (ms < 60_000)       return String.format("%.1fs", ms / 1_000.0);
        long min = ms / 60_000;
        long sec = (ms % 60_000) / 1_000;
        if (min < 60)          return String.format("%dm %02ds", min, sec);
        long hr = min / 60;
        long remMin = min % 60;
        return String.format("%dh %02dm", hr, remMin);
    }

    static String timestamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    // ─── Private helpers ──────────────────────────────────────────────────────

    private static String resolveHostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "unknown";
        }
    }

    private static String detectCiLabel() {
        if (System.getenv("GITHUB_ACTIONS") != null) return "GitHub Actions  [CI=true]";
        if (System.getenv("JENKINS_HOME")   != null) return "Jenkins         [CI=true]";
        if (System.getenv("GITLAB_CI")      != null) return "GitLab CI       [CI=true]";
        if (System.getenv("CIRCLECI")       != null) return "CircleCI        [CI=true]";
        if (System.getenv("BITBUCKET_BUILD_NUMBER") != null) return "Bitbucket       [CI=true]";
        if (System.getenv("CI")             != null) return "Generic CI      [CI=true]";
        if ("true".equalsIgnoreCase(System.getenv("HEADLESS")))
            return "Headless (forced via env)";
        return "Local Development";
    }
}
