package com.toskie.utils_Layer;

/**
 * Centralized retry and timeout configuration.
 * Load values from config.properties or use defaults.
 */
public class RetryConfig {

    // ─── Timeout Constants (milliseconds) ──────────────────────────────────────
    public static final int DEFAULT_TIMEOUT = 30000;
    public static final int ELEMENT_TIMEOUT = getConfigInt("element.timeout", 10000);
    public static final int PAGE_LOAD_TIMEOUT = getConfigInt("page.load.timeout", 15000);
    public static final int NAVIGATION_TIMEOUT = getConfigInt("navigation.timeout", 15000);
    public static final int API_TIMEOUT = getConfigInt("api.timeout", 20000);
    public static final int SCROLL_TIMEOUT = getConfigInt("scroll.timeout", 5000);
    public static final int DIALOG_TIMEOUT = getConfigInt("dialog.timeout", 5000);
    public static final int TOAST_TIMEOUT = getConfigInt("toast.timeout", 10000);

    // ─── Retry Configuration ──────────────────────────────────────────────────
    public static final int MAX_RETRIES = getConfigInt("max.retries", 3);
    public static final int INITIAL_DELAY = getConfigInt("initial.delay", 500);
    public static final double BACKOFF_FACTOR = getConfigDouble("backoff.factor", 1.5);
    public static final int MAX_BACKOFF_DELAY = getConfigInt("max.backoff.delay", 5000);

    // ─── Scroll Configuration ─────────────────────────────────────────────────
    public static final int SCROLL_MAX_ATTEMPTS = getConfigInt("scroll.max.attempts", 10);
    public static final int SCROLL_DELAY = getConfigInt("scroll.delay", 300);
    public static final int SCROLL_DISTANCE = getConfigInt("scroll.distance", 200);

    // ─── Polling Configuration ────────────────────────────────────────────────
    public static final int POLL_INTERVAL = getConfigInt("poll.interval", 100);
    public static final long POLL_MAX_DURATION = getConfigInt("poll.max.duration", 30000);

    // ─── Feature Flags ────────────────────────────────────────────────────────
    public static final boolean ENABLE_RETRY = getConfigBoolean("enable.retry", true);
    public static final boolean ENABLE_LOGGING = getConfigBoolean("enable.logging", true);
    public static final boolean ENABLE_SCREENSHOTS_ON_FAILURE = getConfigBoolean("screenshots.on.failure", true);

    /**
     * Get integer configuration value from properties or use default.
     */
    private static int getConfigInt(String key, int defaultValue) {
        try {
            String value = ConfigManager.get(key);
            return value != null && !value.isEmpty() ? Integer.parseInt(value) : defaultValue;
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Get double configuration value from properties or use default.
     */
    private static double getConfigDouble(String key, double defaultValue) {
        try {
            String value = ConfigManager.get(key);
            return value != null && !value.isEmpty() ? Double.parseDouble(value) : defaultValue;
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Get boolean configuration value from properties or use default.
     */
    private static boolean getConfigBoolean(String key, boolean defaultValue) {
        try {
            String value = ConfigManager.get(key);
            if (value == null || value.isEmpty()) return defaultValue;
            return value.equalsIgnoreCase("true") || value.equalsIgnoreCase("yes");
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Get timeout value for operation, never exceeding DEFAULT_TIMEOUT.
     */
    public static int getTimeout(int suggestedTimeout) {
        return Math.min(suggestedTimeout, DEFAULT_TIMEOUT);
    }

    /**
     * Calculate exponential backoff delay with max cap.
     */
    public static int calculateBackoffDelay(int initialDelay, int attempt) {
        int delay = (int) (initialDelay * Math.pow(BACKOFF_FACTOR, attempt));
        return Math.min(delay, MAX_BACKOFF_DELAY);
    }

    /**
     * Log all configuration values at startup.
     */
    public static void logConfiguration() {
        System.out.println("===== RETRY CONFIGURATION =====");
        System.out.println("DEFAULT_TIMEOUT: " + DEFAULT_TIMEOUT + "ms");
        System.out.println("ELEMENT_TIMEOUT: " + ELEMENT_TIMEOUT + "ms");
        System.out.println("PAGE_LOAD_TIMEOUT: " + PAGE_LOAD_TIMEOUT + "ms");
        System.out.println("NAVIGATION_TIMEOUT: " + NAVIGATION_TIMEOUT + "ms");
        System.out.println("MAX_RETRIES: " + MAX_RETRIES);
        System.out.println("INITIAL_DELAY: " + INITIAL_DELAY + "ms");
        System.out.println("BACKOFF_FACTOR: " + BACKOFF_FACTOR);
        System.out.println("ENABLE_RETRY: " + ENABLE_RETRY);
        System.out.println("================================");
    }
}
