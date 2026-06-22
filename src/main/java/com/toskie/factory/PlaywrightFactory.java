package com.toskie.factory;

import com.microsoft.playwright.*;
import com.toskie.config.ConfigReader;
import com.toskie.utils_Layer.BrowserManager;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PlaywrightFactory — thread-safe browser lifecycle manager.
 *
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │  Parallel safety guarantee                                              │
 * │  Each JVM thread owns exactly one Playwright process, one Browser,     │
 * │  one BrowserContext, and one Page — stored in four ThreadLocals.        │
 * │  100+ TestNG parallel classes each get fully isolated browser state.   │
 * └─────────────────────────────────────────────────────────────────────────┘
 *
 * Usage:
 *   @BeforeMethod  → PlaywrightFactory.initializeBrowser("chrome");
 *   @Test          → PlaywrightFactory.getPage().navigate(url);
 *   @AfterMethod   → PlaywrightFactory.closeBrowser();
 *
 * BrowserManager sync:
 *   After every initializeBrowser() call the four ThreadLocal values are
 *   mirrored into BrowserManager, so all existing page objects that call
 *   BrowserManager.getPage() continue to work without modification.
 */
public final class PlaywrightFactory {

    private PlaywrightFactory() {}

    // ─── ThreadLocal state — one set per JVM thread ───────────────────────────
    private static final ThreadLocal<Playwright>     PLAYWRIGHT = new ThreadLocal<>();
    private static final ThreadLocal<Browser>        BROWSER    = new ThreadLocal<>();
    private static final ThreadLocal<BrowserContext> CONTEXT    = new ThreadLocal<>();
    private static final ThreadLocal<Page>           PAGE       = new ThreadLocal<>();

    // ═════════════════════════════════════════════════════════════════════════
    //  initializeBrowser
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Reads browser name from ConfigReader/system property and launches it.
     * Headless is auto-detected (CI env vars, -Dheadless, config.properties).
     */
    public static void initializeBrowser() {
        initializeBrowser(ConfigReader.getBrowser());
    }

    /**
     * Launches the named browser for the calling thread.
     * Safe to call from any number of concurrent threads simultaneously.
     *
     * @param browserName  "chrome" | "edge" | "firefox" | "chromium" | "webkit"
     *                     (case-insensitive; unknown values fall back to chromium)
     */
    public static void initializeBrowser(String browserName) {
        initializeBrowser(browserName, resolveHeadless());
    }

    /**
     * Full-control overload with explicit headless flag.
     * Calling this on a thread that already has a live browser silently closes
     * the old one first — prevents Playwright process leaks on double-init.
     *
     * @param browserName  browser to launch
     * @param headless     true = no visible window (required in CI/Docker)
     */
    public static void initializeBrowser(String browserName, boolean headless) {
        // Guard: tear down any pre-existing resources on this thread.
        // Protects against @BeforeMethod being invoked twice without an @AfterMethod.
        closeBrowser();

        Playwright pw = Playwright.create();
        PLAYWRIGHT.set(pw);

        BrowserType.LaunchOptions launchOpts = buildLaunchOptions(headless);
        Browser browser = launchBrowserByName(pw, browserName, launchOpts);
        BROWSER.set(browser);

        BrowserContext context = browser.newContext(buildDesktopContextOptions());
        CONTEXT.set(context);

        Page page = context.newPage();
        PAGE.set(page);

        // Mirror into BrowserManager so legacy page objects see the correct Page
        syncToBrowserManager(pw, browser, context, page);

        System.out.printf("[PlaywrightFactory] Thread[%d] launched: %s | headless=%b%n",
                Thread.currentThread().getId(), browserName.toLowerCase(), headless);
    }

    /**
     * Launches a mobile-emulated browser.
     * Exposes the device profile registry for future mobile automation suites.
     *
     * @param browserName  "chrome" | "firefox" | "webkit"
     * @param deviceName   key from the device registry — e.g. "Pixel 7", "iPhone 14"
     */
    public static void initializeMobileDevice(String browserName, String deviceName) {
        closeBrowser();

        boolean headless = resolveHeadless();
        Playwright pw = Playwright.create();
        PLAYWRIGHT.set(pw);

        Browser browser = launchBrowserByName(pw, browserName, buildLaunchOptions(headless));
        BROWSER.set(browser);

        BrowserContext context = browser.newContext(
                deviceContextOptions(deviceName)
                        .setIgnoreHTTPSErrors(true)
                        .setGeolocation(28.6139, 77.2090)
                        .setPermissions(List.of("geolocation")));
        CONTEXT.set(context);

        Page page = context.newPage();
        PAGE.set(page);

        syncToBrowserManager(pw, browser, context, page);

        System.out.printf("[PlaywrightFactory] Thread[%d] mobile: %s on %s | headless=%b%n",
                Thread.currentThread().getId(), deviceName, browserName, headless);
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  Getters
    // ═════════════════════════════════════════════════════════════════════════

    /** Returns the Page for the calling thread. */
    public static Page getPage() { return PAGE.get(); }

    /** Returns the Browser for the calling thread. */
    public static Browser getBrowser() { return BROWSER.get(); }

    /** Returns the BrowserContext for the calling thread. */
    public static BrowserContext getContext() { return CONTEXT.get(); }

    /** Returns the Playwright instance for the calling thread. */
    public static Playwright getPlaywright() { return PLAYWRIGHT.get(); }

    // ═════════════════════════════════════════════════════════════════════════
    //  closeBrowser
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Closes and releases all browser resources held by the calling thread.
     * MUST be called in @AfterMethod — each Playwright.create() spawns an OS
     * process; skipping this call leaks one process per test under parallel execution.
     *
     * Close order: Page → BrowserContext → Browser → Playwright
     *
     * Each resource is closed in its own independent try-catch-finally block.
     * A failure at any layer does NOT prevent the remaining layers from closing.
     * This guarantees zero process leaks even under partial failures.
     */
    public static void closeBrowser() {
        long tid = Thread.currentThread().getId();

        // 1. Page
        closeQuietly(() -> {
            Page p = PAGE.get();
            if (p != null && !p.isClosed()) p.close();
        }, "Page", tid);
        PAGE.remove();

        // 2. BrowserContext
        closeQuietly(() -> {
            BrowserContext c = CONTEXT.get();
            if (c != null) c.close();
        }, "BrowserContext", tid);
        CONTEXT.remove();

        // 3. Browser
        closeQuietly(() -> {
            Browser b = BROWSER.get();
            if (b != null) b.close();
        }, "Browser", tid);
        BROWSER.remove();

        // 4. Playwright (OS process — most critical to release)
        closeQuietly(() -> {
            Playwright pw = PLAYWRIGHT.get();
            if (pw != null) pw.close();
        }, "Playwright", tid);
        PLAYWRIGHT.remove();

        System.out.printf("[PlaywrightFactory] Thread[%d] all resources released.%n", tid);
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  Screenshot utilities
    // ═════════════════════════════════════════════════════════════════════════

    /** Returns raw PNG bytes; returns empty array if the page is unavailable. */
    public static byte[] screenshotBytes() {
        Page p = PAGE.get();
        if (p == null || p.isClosed()) return new byte[0];
        try {
            return p.screenshot(new Page.ScreenshotOptions().setFullPage(false));
        } catch (Exception e) {
            System.err.printf("[PlaywrightFactory] Thread[%d] screenshot bytes failed: %s%n",
                    Thread.currentThread().getId(), e.getMessage());
            return new byte[0];
        }
    }

    /** Saves a screenshot to an absolute file path and returns that path. */
    public static String screenshotToFile(String absoluteFilePath) {
        Page p = PAGE.get();
        if (p == null || p.isClosed()) return null;
        try {
            p.screenshot(new Page.ScreenshotOptions()
                    .setPath(Paths.get(absoluteFilePath))
                    .setFullPage(false));
            return absoluteFilePath;
        } catch (Exception e) {
            System.err.printf("[PlaywrightFactory] Thread[%d] screenshot to file failed: %s%n",
                    Thread.currentThread().getId(), e.getMessage());
            return null;
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  CI/CD detection
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Returns true when running inside any known CI/CD environment or when
     * headless mode is explicitly requested via env var or system property.
     */
    public static boolean isCI() {
        return System.getenv("CI")             != null   // GitHub Actions, CircleCI, Travis, etc.
            || System.getenv("JENKINS_HOME")   != null   // Jenkins
            || System.getenv("GITHUB_ACTIONS") != null   // GitHub Actions (explicit)
            || System.getenv("GITLAB_CI")      != null   // GitLab CI
            || "true".equalsIgnoreCase(System.getenv("HEADLESS"))      // Docker / shell override
            || "true".equalsIgnoreCase(System.getProperty("headless")); // -Dheadless=true
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  Private helpers
    // ═════════════════════════════════════════════════════════════════════════

    private static boolean resolveHeadless() {
        return isCI() || ConfigReader.isHeadless();
    }

    private static BrowserType.LaunchOptions buildLaunchOptions(boolean headless) {
        List<String> args = new ArrayList<>();

        if (headless) {
            args.add("--no-sandbox");           // required in Docker (no root privilege separation)
            args.add("--disable-dev-shm-usage"); // avoids /dev/shm exhaustion under high parallelism
            args.add("--disable-gpu");           // no GPU in headless Linux
            args.add("--disable-extensions");
        } else {
            args.add("--start-maximized");
        }

        // Stub media devices for Toskie camera/mic permission gates
        args.add("--use-fake-ui-for-media-stream");
        args.add("--use-fake-device-for-media-stream");

        BrowserType.LaunchOptions opts = new BrowserType.LaunchOptions()
                .setHeadless(headless)
                .setArgs(args);

        int slowMo = ConfigReader.getSlowMo();
        if (slowMo > 0) opts.setSlowMo(slowMo);

        return opts;
    }

    /**
     * Launches the correct BrowserType for the given name.
     * setChannel() mutates and returns the same opts object — all fields
     * (headless, args, slowMo) are preserved when adding a channel.
     */
    private static Browser launchBrowserByName(Playwright pw, String name,
                                                BrowserType.LaunchOptions opts) {
        switch (name.toLowerCase().trim()) {
            case "chrome":    return pw.chromium().launch(opts.setChannel("chrome"));
            case "edge":      return pw.chromium().launch(opts.setChannel("msedge"));
            case "firefox":   return pw.firefox().launch(opts);
            case "chromium":  return pw.chromium().launch(opts);
            case "webkit":
            case "safari":    return pw.webkit().launch(opts);
            default:
                System.err.printf("[PlaywrightFactory] Unknown browser '%s' — falling back to chromium%n", name);
                return pw.chromium().launch(opts);
        }
    }

    private static Browser.NewContextOptions buildDesktopContextOptions() {
        return new Browser.NewContextOptions()
                .setViewportSize(null)              // OS window size in headed; ignored in headless
                .setIgnoreHTTPSErrors(true)          // prevents SSL cert failures in QA env
                .setLocale("en-US")
                .setTimezoneId("Asia/Kolkata")
                .setGeolocation(28.6139, 77.2090)   // Delhi — for Toskie geolocation-gated features
                .setPermissions(List.of("geolocation"));
    }

    /**
     * Device profile registry — add entries here to expand mobile coverage.
     * No other code changes are needed when a new device is added.
     */
    private static Browser.NewContextOptions deviceContextOptions(String deviceName) {
        Map<String, Browser.NewContextOptions> profiles = new HashMap<>();

        profiles.put("iPhone 14", new Browser.NewContextOptions()
                .setViewportSize(390, 844).setDeviceScaleFactor(3).setIsMobile(true)
                .setLocale("en-US").setTimezoneId("Asia/Kolkata")
                .setUserAgent("Mozilla/5.0 (iPhone; CPU iPhone OS 16_0 like Mac OS X) "
                        + "AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.0 Mobile/15E148 Safari/604.1"));

        profiles.put("iPhone 12", new Browser.NewContextOptions()
                .setViewportSize(390, 844).setDeviceScaleFactor(3).setIsMobile(true)
                .setLocale("en-US").setTimezoneId("Asia/Kolkata")
                .setUserAgent("Mozilla/5.0 (iPhone; CPU iPhone OS 14_0 like Mac OS X) "
                        + "AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.0 Mobile/15E148 Safari/604.1"));

        profiles.put("Pixel 7", new Browser.NewContextOptions()
                .setViewportSize(412, 915).setDeviceScaleFactor(2.625).setIsMobile(true)
                .setLocale("en-US").setTimezoneId("Asia/Kolkata")
                .setUserAgent("Mozilla/5.0 (Linux; Android 13; Pixel 7) "
                        + "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Mobile Safari/537.36"));

        profiles.put("Pixel 5", new Browser.NewContextOptions()
                .setViewportSize(393, 851).setDeviceScaleFactor(2.75).setIsMobile(true)
                .setLocale("en-US").setTimezoneId("Asia/Kolkata")
                .setUserAgent("Mozilla/5.0 (Linux; Android 11; Pixel 5) "
                        + "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.71 Mobile Safari/537.36"));

        profiles.put("Galaxy S22", new Browser.NewContextOptions()
                .setViewportSize(360, 780).setDeviceScaleFactor(3).setIsMobile(true)
                .setLocale("en-US").setTimezoneId("Asia/Kolkata")
                .setUserAgent("Mozilla/5.0 (Linux; Android 12; SM-S901B) "
                        + "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/101.0.4951.41 Mobile Safari/537.36"));

        profiles.put("iPad Pro", new Browser.NewContextOptions()
                .setViewportSize(1024, 1366).setDeviceScaleFactor(2).setIsMobile(false)
                .setLocale("en-US").setTimezoneId("Asia/Kolkata")
                .setUserAgent("Mozilla/5.0 (iPad; CPU OS 16_0 like Mac OS X) "
                        + "AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.0 Mobile/15E148 Safari/604.1"));

        profiles.put("Desktop 1080p", new Browser.NewContextOptions()
                .setViewportSize(1920, 1080).setDeviceScaleFactor(1).setIsMobile(false)
                .setLocale("en-US").setTimezoneId("Asia/Kolkata"));

        Browser.NewContextOptions chosen = profiles.get(deviceName);
        if (chosen == null) {
            System.err.printf("[PlaywrightFactory] Unknown device '%s' — falling back to desktop%n", deviceName);
            return buildDesktopContextOptions();
        }
        return chosen;
    }

    private static void syncToBrowserManager(Playwright pw, Browser br,
                                              BrowserContext ctx, Page pg) {
        try {
            BrowserManager.setPlaywright(pw);
            BrowserManager.setBrowser(br);
            BrowserManager.setContext(ctx);
            BrowserManager.setPage(pg);
        } catch (Exception e) {
            System.err.printf("[PlaywrightFactory] Thread[%d] BrowserManager sync warning: %s%n",
                    Thread.currentThread().getId(), e.getMessage());
        }
    }

    /**
     * Executes a close action and logs any exception without re-throwing.
     * Used by closeBrowser() so that a failure at one layer never blocks
     * the remaining layers from being released.
     */
    private static void closeQuietly(Runnable action, String resourceName, long threadId) {
        try {
            action.run();
        } catch (Exception e) {
            System.err.printf("[PlaywrightFactory] Thread[%d] %s close error: %s%n",
                    threadId, resourceName, e.getMessage());
        }
    }
}
