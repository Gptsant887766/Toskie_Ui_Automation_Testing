package com.toskie.listeners;

import com.toskie.config.ConfigReader;
import com.toskie.factory.PlaywrightFactory;
import com.toskie.reports.AllureManager;
import com.toskie.reports.ExtentManager;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ISuiteResult;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

/**
 * Suite-level listener — fires at the boundary of each TestNG &lt;suite&gt; block.
 *
 * In master.xml (suite-of-suites), this fires once per referenced sub-suite
 * (regression, api, e2e, performance) because TestNG treats each as an
 * independent suite execution.
 *
 * Responsibilities:
 *   - Initialize ExtentReports once at suite start (idempotent call)
 *   - Print a structured environment block (config, browser, CI/CD, parallel
 *     settings, target URL, retry config)
 *   - Print a module-level summary table when the suite finishes
 *   - Accumulate pass/fail/skip counts into ExecutionListener's cross-suite
 *     totals for the final JVM-level summary
 *   - Flush ExtentReports at suite finish
 *
 * Registration in suite XML:
 *   <listener class-name="com.toskie.listeners.SuiteListener"/>
 *
 * Thread safety:
 *   ISuiteListener callbacks fire sequentially (not in parallel) so no
 *   locking is needed in onStart/onFinish beyond what ExtentManager provides.
 */
public class SuiteListener implements ISuiteListener {

    private static final String BORDER  = "═".repeat(74);
    private static final String DIVIDER = "─".repeat(74);

    // Per-suite wall-clock timer (accessed only from the suite's thread)
    private final AtomicLong suiteStartMs = new AtomicLong(0);

    // ═════════════════════════════════════════════════════════════════════════
    //  onStart
    // ═════════════════════════════════════════════════════════════════════════

    @Override
    public void onStart(ISuite suite) {
        suiteStartMs.set(System.currentTimeMillis());

        // Idempotent — safe even if multiple sub-suites call this
        ExtentManager.init();

        // Write Allure environment.properties + categories.json once per run
        AllureManager.writeEnvironment();
        AllureManager.writeCategories();

        System.out.println();
        System.out.println(BORDER);
        System.out.printf("  SUITE START : %s%n", suite.getName());
        System.out.printf("  Started at  : %s%n", ExecutionListener.timestamp());
        System.out.println(DIVIDER);
        printEnvironmentBlock(suite);
        System.out.println(BORDER);
        System.out.println();
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  onFinish
    // ═════════════════════════════════════════════════════════════════════════

    @Override
    public void onFinish(ISuite suite) {
        long wallMs = System.currentTimeMillis() - suiteStartMs.get();

        // ── Tally results across all <test> blocks in this suite ─────────
        Map<String, ISuiteResult> results = suite.getResults();
        int totalPassed = 0, totalFailed = 0, totalSkipped = 0;

        System.out.println();
        System.out.println(BORDER);
        System.out.printf("  SUITE FINISH : %s%n", suite.getName());
        System.out.printf("  Finished at  : %s%n", ExecutionListener.timestamp());
        System.out.printf("  Wall time    : %s%n", ExecutionListener.formatDuration(wallMs));
        System.out.println(DIVIDER);

        // Per-<test> module breakdown table
        System.out.printf("  %-34s  %6s  %6s  %6s  %7s%n",
                "Test Block", "Total", "Pass", "Fail", "Skip");
        System.out.println("  " + "─".repeat(68));

        for (Map.Entry<String, ISuiteResult> entry : results.entrySet()) {
            String name    = entry.getKey();
            ISuiteResult r = entry.getValue();

            int p = r.getTestContext().getPassedTests().getAllResults().size();
            int f = r.getTestContext().getFailedTests().getAllResults().size();
            int s = r.getTestContext().getSkippedTests().getAllResults().size();
            int t = p + f + s;

            totalPassed  += p;
            totalFailed  += f;
            totalSkipped += s;

            String status = f > 0 ? "✗" : (s > 0 ? "⊘" : "✓");
            System.out.printf("  %s %-32s  %6d  %6d  %6d  %6d%n",
                    status, truncate(name, 32), t, p, f, s);
        }

        int suiteTotal = totalPassed + totalFailed + totalSkipped;
        double suiteRate = suiteTotal > 0 ? (totalPassed * 100.0 / suiteTotal) : 0.0;

        System.out.println("  " + "─".repeat(68));
        System.out.printf("  %-34s  %6d  %6d  %6d  %6d%n",
                "TOTAL", suiteTotal, totalPassed, totalFailed, totalSkipped);
        System.out.printf("  Pass Rate: %.1f%%%n", suiteRate);
        System.out.println(BORDER);
        System.out.println();

        // ── Propagate to JVM-level totals in ExecutionListener ───────────
        ExecutionListener.TOTAL_PASSED.addAndGet(totalPassed);
        ExecutionListener.TOTAL_FAILED.addAndGet(totalFailed);
        ExecutionListener.TOTAL_SKIPPED.addAndGet(totalSkipped);

        // ── Flush the HTML report ────────────────────────────────────────
        ExtentManager.flush();

        if (totalFailed > 0) {
            System.out.printf("[SuiteListener] ⚠ Suite '%s' had %d failure(s). "
                    + "Report: %s%n%n",
                    suite.getName(), totalFailed, ExtentManager.getReportPath());
        } else {
            System.out.printf("[SuiteListener] ✓ Suite '%s' passed. "
                    + "Report: %s%n%n",
                    suite.getName(), ExtentManager.getReportPath());
        }
    }

    // ─── Private helpers ──────────────────────────────────────────────────────

    private void printEnvironmentBlock(ISuite suite) {
        // ── Framework config ─────────────────────────────────────────────
        System.out.printf("  %-24s %s%n", "Browser:",
                safeGet(ConfigReader::getBrowser).toUpperCase());
        System.out.printf("  %-24s %s%n", "Headless:",
                String.valueOf(ConfigReader.isHeadless() || PlaywrightFactory.isCI()));
        System.out.printf("  %-24s %s%n", "Environment:",
                safeGet(ConfigReader::getEnvironment).toUpperCase());
        System.out.printf("  %-24s %s%n", "Base URL:",
                blankOr(ConfigReader.getBaseUrl()));
        System.out.printf("  %-24s %s%n", "API URL:",
                blankOr(ConfigReader.getApiUrl()));

        // ── Retry ────────────────────────────────────────────────────────
        System.out.printf("  %-24s %d attempts%n", "Max Retries:",
                ConfigReader.getMaxRetries());
        System.out.printf("  %-24s %d ms%n", "Slow-Mo:",
                ConfigReader.getSlowMo());

        // ── TestNG suite settings ────────────────────────────────────────
        String parallel = suite.getXmlSuite().getParallel() != null
                ? suite.getXmlSuite().getParallel().toString()
                : "none";
        System.out.printf("  %-24s %s%n", "Parallel Mode:", parallel);
        System.out.printf("  %-24s %d%n", "Thread Count:",
                suite.getXmlSuite().getThreadCount());
        System.out.printf("  %-24s %s%n", "Suite XML:",
                suite.getXmlSuite().getFileName());
        System.out.printf("  %-24s %d test blocks%n", "Test Blocks:",
                suite.getXmlSuite().getTests().size());
    }

    private static String safeGet(Supplier<String> supplier) {
        try {
            String v = supplier.get();
            return v != null ? v : "";
        } catch (Exception e) {
            return "(error)";
        }
    }

    private static String blankOr(String v) {
        return (v != null && !v.isBlank()) ? v : "(not configured)";
    }

    private static String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max - 1) + "…";
    }
}
