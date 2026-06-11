package com.toskie.utils;

import com.aventstack.extentreports.Status;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.ReportManager;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Captures Web Performance API metrics, Core Web Vitals, and custom timing.
 */
public class PerformanceUtils {

    // SLA thresholds (milliseconds)
    public static final long PAGE_LOAD_THRESHOLD      = 5000;
    public static final long DOM_CONTENT_LOADED       = 3000;
    public static final long API_RESPONSE_THRESHOLD   = 2000;
    public static final long FIRST_CONTENTFUL_PAINT   = 2500;
    public static final long LARGEST_CONTENTFUL_PAINT = 4000;
    public static final long TIME_TO_INTERACTIVE      = 5000;
    public static final long CUMULATIVE_LAYOUT_SHIFT  = 100; // CLS * 1000

    private final Map<String, Long> timers = new HashMap<>();

    // ─── Timer ────────────────────────────────────────────────────────────────

    public void startTimer(String name) {
        timers.put(name, System.currentTimeMillis());
        ReportManager.getTest().log(Status.INFO, "[Perf] Timer started: " + name);
    }

    public long stopTimer(String name) {
        Long start = timers.remove(name);
        if (start == null) return -1;
        long elapsed = System.currentTimeMillis() - start;
        ReportManager.getTest().log(Status.INFO,
            "[Perf] " + name + " elapsed: " + elapsed + "ms");
        return elapsed;
    }

    public long stopTimerAndAssert(String name, long thresholdMs) {
        long elapsed = stopTimer(name);
        if (elapsed <= thresholdMs) {
            ReportManager.getTest().log(Status.PASS,
                "[Perf] " + name + ": " + elapsed + "ms ≤ " + thresholdMs + "ms ✓");
        } else {
            ReportManager.getTest().log(Status.FAIL,
                "[Perf] " + name + ": " + elapsed + "ms > " + thresholdMs + "ms VIOLATION");
        }
        return elapsed;
    }

    // ─── Navigation Timing API ────────────────────────────────────────────────

    public JSONObject getNavigationTimings() {
        try {
            Object result = BrowserManager.getPage().evaluate(
                "() => JSON.stringify(performance.getEntriesByType('navigation')[0])");
            return new JSONObject(result.toString());
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "Could not retrieve navigation timings: " + e.getMessage());
            return new JSONObject();
        }
    }

    public long getDOMContentLoadedTime() {
        try {
            JSONObject timings = getNavigationTimings();
            return (long) (timings.getDouble("domContentLoadedEventEnd")
                - timings.getDouble("startTime"));
        } catch (Exception e) { return -1; }
    }

    public long getPageLoadTime() {
        try {
            JSONObject timings = getNavigationTimings();
            return (long) (timings.getDouble("loadEventEnd")
                - timings.getDouble("startTime"));
        } catch (Exception e) { return -1; }
    }

    public long getTimeToFirstByte() {
        try {
            JSONObject timings = getNavigationTimings();
            return (long) (timings.getDouble("responseStart")
                - timings.getDouble("requestStart"));
        } catch (Exception e) { return -1; }
    }

    // ─── Paint Timing (FCP / LCP) ─────────────────────────────────────────────

    public long getFirstContentfulPaint() {
        try {
            Object result = BrowserManager.getPage().evaluate(
                "() => { const e = performance.getEntriesByName('first-contentful-paint')[0];"
                + " return e ? e.startTime : -1; }");
            return result != null ? ((Number) result).longValue() : -1;
        } catch (Exception e) { return -1; }
    }

    public long getLargestContentfulPaint() {
        try {
            Object result = BrowserManager.getPage().evaluate(
                "() => new Promise(resolve => {"
                + " new PerformanceObserver(list => {"
                + "   const entries = list.getEntries();"
                + "   resolve(entries[entries.length-1].startTime);"
                + " }).observe({type:'largest-contentful-paint', buffered:true});"
                + " setTimeout(() => resolve(-1), 3000);"
                + "})");
            return result != null ? ((Number) result).longValue() : -1;
        } catch (Exception e) { return -1; }
    }

    public double getCumulativeLayoutShift() {
        try {
            Object result = BrowserManager.getPage().evaluate(
                "() => new Promise(resolve => {"
                + " let cls = 0;"
                + " new PerformanceObserver(list => {"
                + "   list.getEntries().forEach(e => cls += e.value);"
                + "   resolve(cls);"
                + " }).observe({type:'layout-shift', buffered:true});"
                + " setTimeout(() => resolve(cls), 2000);"
                + "})");
            return result != null ? ((Number) result).doubleValue() : -1;
        } catch (Exception e) { return -1; }
    }

    // ─── Resource timing ──────────────────────────────────────────────────────

    public Map<String, Long> getResourceTimings() {
        Map<String, Long> resources = new HashMap<>();
        try {
            Object result = BrowserManager.getPage().evaluate(
                "() => performance.getEntriesByType('resource').map(r => ({name: r.name, duration: r.duration}))");
            if (result instanceof java.util.List<?> list) {
                for (Object item : list) {
                    if (item instanceof Map<?, ?> m) {
                        resources.put(m.get("name").toString(),
                            ((Number) m.get("duration")).longValue());
                    }
                }
            }
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "Resource timings unavailable: " + e.getMessage());
        }
        return resources;
    }

    // ─── Memory usage ─────────────────────────────────────────────────────────

    public JSONObject getMemoryInfo() {
        try {
            Object result = BrowserManager.getPage().evaluate(
                "() => performance.memory ? JSON.stringify(performance.memory) : null");
            return result != null ? new JSONObject(result.toString()) : new JSONObject();
        } catch (Exception e) {
            return new JSONObject();
        }
    }

    // ─── Full performance report ──────────────────────────────────────────────

    public void logFullPerformanceReport(String pageName) {
        ReportManager.getTest().log(Status.INFO,
            "═══ Performance Report: " + pageName + " ═══");

        long fcp    = getFirstContentfulPaint();
        long load   = getPageLoadTime();
        long dom    = getDOMContentLoadedTime();
        long ttfb   = getTimeToFirstByte();
        double cls  = getCumulativeLayoutShift();

        logMetric("First Contentful Paint", fcp, FIRST_CONTENTFUL_PAINT);
        logMetric("Page Load Time",         load, PAGE_LOAD_THRESHOLD);
        logMetric("DOM Content Loaded",     dom,  DOM_CONTENT_LOADED);
        logMetric("Time to First Byte",     ttfb, 600);

        if (cls >= 0) {
            boolean goodCLS = cls < 0.1;
            ReportManager.getTest().log(goodCLS ? Status.PASS : Status.FAIL,
                "CLS: " + String.format("%.4f", cls) + " (threshold: < 0.1)");
        }
    }

    private void logMetric(String name, long value, long threshold) {
        if (value < 0) {
            ReportManager.getTest().log(Status.WARNING, name + ": N/A");
            return;
        }
        Status s = value <= threshold ? Status.PASS : Status.FAIL;
        ReportManager.getTest().log(s,
            name + ": " + value + "ms (threshold: " + threshold + "ms)");
    }

    // ─── Intercept & measure API calls ────────────────────────────────────────

    public long measureAPICall(String urlFragment, Runnable action) {
        final long[] responseTime = {-1};

        BrowserManager.getPage().onResponse(resp -> {
            if (resp.url().contains(urlFragment)) {
                double start = resp.request().timing().requestStart;
                double end   = resp.request().timing().responseEnd;
                responseTime[0] = (long) (end - start);
            }
        });

        startTimer("api_" + urlFragment);
        action.run();
        BrowserManager.getPage().waitForTimeout(3000);
        long wallTime = stopTimer("api_" + urlFragment);

        long actual = responseTime[0] > 0 ? responseTime[0] : wallTime;
        ReportManager.getTest().log(Status.INFO,
            "API " + urlFragment + " response time: " + actual + "ms");
        return actual;
    }
}
