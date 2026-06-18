package com.toskie.utils_Layer;

import com.aventstack.extentreports.Status;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;

/**
 * Centralized wait strategy with exponential backoff and intelligent polling.
 * Replaces hardcoded Thread.sleep() and waitForTimeout() with condition-based waits.
 */
public class WaitManager {

    private static final int DEFAULT_TIMEOUT = RetryConfig.DEFAULT_TIMEOUT;

    /**
     * Wait for element to be visible with exponential backoff retry.
     */
    public static void waitForElementVisible(Locator locator, String elementName, int maxRetries, int initialDelay) {
        int delay = initialDelay;
        WaitTimeoutException lastException = null;

        for (int attempt = 0; attempt < maxRetries; attempt++) {
            try {
                locator.waitFor(
                    new Locator.WaitForOptions()
                        .setState(WaitForSelectorState.VISIBLE)
                        .setTimeout(5000));
                logSuccess(elementName, attempt);
                return;
            } catch (Exception e) {
                lastException = new WaitTimeoutException("Wait failed for: " + elementName, e);
                if (attempt < maxRetries - 1) {
                    logRetry(elementName, attempt, delay);
                    sleep(delay);
                    delay = (int) (delay * RetryConfig.BACKOFF_FACTOR);
                }
            }
        }

        String msg = "Element not visible after " + maxRetries + " retries: " + elementName;
        if (ReportManager.getTest() != null) {
            ReportManager.getTest().log(Status.FAIL, msg);
        }
        throw new WaitTimeoutException(msg, lastException);
    }

    /**
     * Wait for element visible with default retry strategy.
     */
    public static void waitForElementVisible(Locator locator, String elementName) {
        waitForElementVisible(locator, elementName, 3, 500);
    }

    /**
     * Poll for a boolean condition with exponential backoff.
     */
    public static void waitForCondition(ConditionChecker checker, int maxAttempts, int initialDelay) {
        int delay = initialDelay;
        Exception lastException = null;

        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            try {
                if (checker.check()) {
                    logConditionMet(attempt);
                    return;
                }
            } catch (Exception e) {
                lastException = e;
            }

            if (attempt < maxAttempts - 1) {
                sleep(delay);
                delay = (int) (delay * RetryConfig.BACKOFF_FACTOR);
            }
        }

        String msg = "Condition not met after " + maxAttempts + " attempts";
        if (ReportManager.getTest() != null) {
            ReportManager.getTest().log(Status.FAIL, msg);
        }
        throw new WaitTimeoutException(msg, lastException);
    }

    /**
     * Wait for navigation with URL pattern validation.
     * Waits for both navigation to complete and URL to match pattern.
     */
    public static void waitForNavigationWithURL(String urlPattern, int timeout) {
        try {
            BrowserManager.getPage().waitForURL(
                java.util.regex.Pattern.compile(".*" + urlPattern + ".*"),
                new com.microsoft.playwright.Page.WaitForURLOptions().setTimeout(timeout));

            logSuccess("Navigation to URL pattern: " + urlPattern, 0);
        } catch (Exception e) {
            String msg = "Navigation wait failed for URL pattern: " + urlPattern;
            if (ReportManager.getTest() != null) {
                ReportManager.getTest().log(Status.FAIL, msg);
            }
            throw new WaitTimeoutException(msg, e);
        }
    }

    /**
     * Wait for page to reach specific LoadState with intelligent retry.
     */
    public static void waitForPageLoad(LoadState state, int maxRetries, int initialDelay) {
        int delay = initialDelay;
        WaitTimeoutException lastException = null;

        for (int attempt = 0; attempt < maxRetries; attempt++) {
            try {
                BrowserManager.getPage().waitForLoadState(state);
                logSuccess("Page load state: " + state.name(), attempt);
                return;
            } catch (Exception e) {
                lastException = new WaitTimeoutException("Page load failed for state: " + state, e);
                if (attempt < maxRetries - 1) {
                    logRetry("Page load (" + state.name() + ")", attempt, delay);
                    sleep(delay);
                    delay = (int) (delay * RetryConfig.BACKOFF_FACTOR);
                }
            }
        }

        String msg = "Page load timeout for state " + state + " after " + maxRetries + " retries";
        if (ReportManager.getTest() != null) {
            ReportManager.getTest().log(Status.FAIL, msg);
        }
        throw new WaitTimeoutException(msg, lastException);
    }

    /**
     * Wait for page load with default retry strategy.
     */
    public static void waitForPageLoad(LoadState state) {
        waitForPageLoad(state, 2, 1000);
    }

    /**
     * Safe page load: tries NETWORKIDLE for 5 s then silently continues.
     * Use after login -- the app keeps WebSocket/polling connections open so
     * NETWORKIDLE never fires, but the page is interactive after DOM is loaded.
     */
    public static void safePageLoad() {
        try {
            BrowserManager.getPage().waitForLoadState(
                    LoadState.NETWORKIDLE,
                    new com.microsoft.playwright.Page.WaitForLoadStateOptions().setTimeout(5000));
        } catch (Exception e) {
            // NETWORKIDLE timed out -- app has live connections; proceed anyway
            try {
                BrowserManager.getPage().waitForLoadState(
                        LoadState.LOAD,
                        new com.microsoft.playwright.Page.WaitForLoadStateOptions().setTimeout(5000));
            } catch (Exception ignored) {}
        }
    }

    /**
     * Wait for text to appear in element.
     */
    public static void waitForTextInElement(Locator locator, String expectedText, int timeout) {
        try {
            locator.waitFor(
                new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.VISIBLE)
                    .setTimeout(timeout));

            BrowserManager.getPage().waitForFunction(
                "() => document.body.innerText.includes('" + expectedText + "')",
                null);

            logSuccess("Text found: " + expectedText, 0);
        } catch (Exception e) {
            String msg = "Text not found after " + timeout + "ms: " + expectedText;
            if (ReportManager.getTest() != null) {
                ReportManager.getTest().log(Status.FAIL, msg);
            }
            throw new WaitTimeoutException(msg, e);
        }
    }

    /**
     * Wait for multiple elements to be visible.
     */
    public static void waitForAllVisible(java.util.List<Locator> locators, int timeout) {
        long endTime = System.currentTimeMillis() + timeout;

        for (Locator locator : locators) {
            int remainingTime = (int) (endTime - System.currentTimeMillis());
            if (remainingTime <= 0) {
                throw new WaitTimeoutException("Timeout waiting for all elements to be visible");
            }

            locator.waitFor(
                new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.VISIBLE)
                    .setTimeout(remainingTime));
        }
    }

    /**
     * Execute action with retry on stale element or transient failure.
     */
    public static void executeWithRetry(Runnable action, int maxRetries, int initialDelay) {
        int delay = initialDelay;
        WaitTimeoutException lastException = null;

        for (int attempt = 0; attempt < maxRetries; attempt++) {
            try {
                action.run();
                logSuccess("Action executed", attempt);
                return;
            } catch (Exception e) {
                lastException = new WaitTimeoutException("Action failed", e);
                if (attempt < maxRetries - 1) {
                    logRetry("Action retry", attempt, delay);
                    sleep(delay);
                    delay = (int) (delay * RetryConfig.BACKOFF_FACTOR);
                }
            }
        }

        String msg = "Action failed after " + maxRetries + " retries";
        if (ReportManager.getTest() != null) {
            ReportManager.getTest().log(Status.FAIL, msg);
        }
        throw lastException;
    }

    /**
     * Smart wait: Wait for element, scroll if needed, wait again.
     */
    public static void waitForElementWithScroll(Locator locator, String elementName, int maxAttempts) {
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            try {
                if (locator.isVisible()) {
                    logSuccess("Element visible: " + elementName, attempt);
                    return;
                }
            } catch (Exception ignored) {}

            if (attempt < maxAttempts - 1) {
                try {
                    BrowserManager.getPage().evaluate("window.scrollBy(0, 200)");
                    sleep(300);
                } catch (Exception e) {
                    logRetry("Scroll for element: " + elementName, attempt, 300);
                }
            }
        }

        throw new WaitTimeoutException("Element not found after scroll attempts: " + elementName);
    }

    /**
     * Functional interface for condition checking.
     */
    @FunctionalInterface
    public interface ConditionChecker {
        boolean check() throws Exception;
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private static void logRetry(String operation, int attempt, int delay) {
        String msg = "[WaitManager] Retry attempt " + (attempt + 1) + " for: " + operation +
                     " (waiting " + delay + "ms)";
        System.out.println(msg);
        if (ReportManager.getTest() != null) {
            ReportManager.getTest().log(Status.INFO, msg);
        }
    }

    private static void logSuccess(String operation, int attempt) {
        String msg = "[WaitManager] " + operation + (attempt > 0 ? " (succeeded on attempt " + (attempt + 1) + ")" : "");
        if (attempt > 0) {
            System.out.println(msg);
            if (ReportManager.getTest() != null) {
                ReportManager.getTest().log(Status.INFO, msg);
            }
        }
    }

    private static void logConditionMet(int attempt) {
        if (attempt > 0) {
            String msg = "[WaitManager] Condition met (attempt " + (attempt + 1) + ")";
            System.out.println(msg);
            if (ReportManager.getTest() != null) {
                ReportManager.getTest().log(Status.INFO, msg);
            }
        }
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new WaitTimeoutException("Sleep interrupted", e);
        }
    }

    /**
     * Exception wrapper for Playwright operations.
     */
    public static class WaitTimeoutException extends RuntimeException {
        public WaitTimeoutException(String message) {
            super(message);
        }

        public WaitTimeoutException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
