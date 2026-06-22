package com.toskie.utils_Layer;

import com.microsoft.playwright.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages browser lifecycle with ThreadLocal state so that
 * parallel test methods each get their own Playwright/Browser/Page instance.
 */
public class BrowserManager {

    // ─── ThreadLocal state ───────────────────────────────────────────────────
    private static final ThreadLocal<Playwright>      pwHolder  = new ThreadLocal<>();
    private static final ThreadLocal<Browser>         brHolder  = new ThreadLocal<>();
    private static final ThreadLocal<BrowserContext>  ctxHolder = new ThreadLocal<>();
    private static final ThreadLocal<Page>            pgHolder  = new ThreadLocal<>();

    // ─── Getters ─────────────────────────────────────────────────────────────
    public static Playwright     getPlaywright() { return pwHolder.get(); }
    public static Browser        getBrowser()    { return brHolder.get(); }
    public static BrowserContext getContext()    { return ctxHolder.get(); }
    public static Page           getPage()       { return pgHolder.get(); }

    // ─── Setters — public so PlaywrightFactory can sync state ──────────────
    public static void setPlaywright(Playwright pw) { pwHolder.set(pw); }
    public static void setBrowser(Browser br)       { brHolder.set(br); }
    public static void setContext(BrowserContext c) { ctxHolder.set(c); }
    public static void setPage(Page p)              { pgHolder.set(p);  }

    // ─── Launch ───────────────────────────────────────────────────────────────
    /** Returns true when running inside any known CI/CD or Docker environment. */
    private static boolean isCI() {
        return System.getenv("CI")             != null
            || System.getenv("JENKINS_HOME")   != null
            || System.getenv("GITHUB_ACTIONS") != null
            || System.getenv("GITLAB_CI")      != null
            || "true".equalsIgnoreCase(System.getenv("HEADLESS"))
            || "true".equalsIgnoreCase(System.getProperty("headless"));
    }

    public static void launchBrowser(String browserName) {
        Playwright pw = Playwright.create();
        pwHolder.set(pw);

        boolean headless = ConfigManager.getHeadless() || isCI();

        List<String> args = new java.util.ArrayList<>();
        if (headless) {
            // Required for Chrome/Chromium in Docker/Linux CI environments
            args.add("--no-sandbox");
            args.add("--disable-dev-shm-usage");
            args.add("--disable-gpu");
            args.add("--disable-extensions");
        } else {
            args.add("--start-maximized");
        }
        args.add("--use-fake-ui-for-media-stream");
        args.add("--use-fake-device-for-media-stream");
        // Bypass system DNS for the test domain — prevents ERR_NAME_NOT_RESOLVED
        // when the home router DNS is overwhelmed by parallel thread DNS queries.
        // IP confirmed: dev.app.toskie.com → 3.6.102.54 (same as toskie-api.wasd.in)
        args.add("--host-resolver-rules=MAP dev.app.toskie.com 3.6.102.54," +
                 "MAP toskie-api.wasd.in 3.6.102.54");
        // Disable DNS prefetch to reduce unnecessary DNS load under parallel execution
        args.add("--dns-prefetch-disable");

        BrowserType.LaunchOptions opts = new BrowserType.LaunchOptions()
                .setHeadless(headless)
                .setArgs(args);

        Browser browser;
        switch (browserName.toLowerCase()) {
            case "chrome":
                browser = pw.chromium().launch(opts.setChannel("chrome"));
                break;
            case "edge":
                browser = pw.chromium().launch(opts.setChannel("msedge"));
                break;
            case "firefox":
                browser = pw.firefox().launch(opts);
                break;
            case "webkit":
                browser = pw.webkit().launch(opts);
                break;
            default:
                throw new RuntimeException("Unsupported browser: " + browserName);
        }
        brHolder.set(browser);

        BrowserContext ctx = browser.newContext(new Browser.NewContextOptions()
                .setViewportSize(null)
                .setGeolocation(28.6139, 77.2090)
                .setPermissions(List.of("geolocation")));
        ctxHolder.set(ctx);

        pgHolder.set(ctx.newPage());
    }

    // ─── Device Emulation ────────────────────────────────────────────────────
    public static Page launchDevice(String deviceName) {
        Map<String, Browser.NewContextOptions> devices = new HashMap<>();

        devices.put("Desktop", new Browser.NewContextOptions()
                .setViewportSize(1280, 720)
                .setUserAgent("desktop-automation"));

        devices.put("iPhone 12", new Browser.NewContextOptions()
                .setViewportSize(390, 844).setDeviceScaleFactor(3).setIsMobile(true)
                .setUserAgent("Mozilla/5.0 (iPhone; CPU iPhone OS 14_0 like Mac OS X) "
                        + "AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.0 Mobile/15E148 Safari/604.1"));

        devices.put("iPad (gen 7)", new Browser.NewContextOptions()
                .setViewportSize(810, 1080).setDeviceScaleFactor(2).setIsMobile(true)
                .setUserAgent("Mozilla/5.0 (iPad; CPU OS 13_0 like Mac OS X) "
                        + "AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0 Mobile/15E148 Safari/604.1"));

        devices.put("Pixel 5", new Browser.NewContextOptions()
                .setViewportSize(393, 851).setDeviceScaleFactor(2.75).setIsMobile(true)
                .setUserAgent("Mozilla/5.0 (Linux; Android 11; Pixel 5) "
                        + "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.71 Mobile Safari/537.36"));

        devices.put("Galaxy S9", new Browser.NewContextOptions()
                .setViewportSize(360, 740).setDeviceScaleFactor(4).setIsMobile(true)
                .setUserAgent("Mozilla/5.0 (Linux; Android 8.0; SM-G960F) "
                        + "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.71 Mobile Safari/537.36"));

        Browser.NewContextOptions ctxOpts = devices.get(deviceName);
        if (ctxOpts == null) throw new IllegalArgumentException("Unsupported device: " + deviceName);

        BrowserContext ctx = brHolder.get().newContext(ctxOpts);
        ctxHolder.set(ctx);
        Page p = ctx.newPage();
        pgHolder.set(p);
        return p;
    }

    // ─── Teardown ─────────────────────────────────────────────────────────────
    public static void tearDown() {
        try {
            Page p = pgHolder.get();
            if (p != null && !p.isClosed()) p.close();
            pgHolder.remove();

            BrowserContext ctx = ctxHolder.get();
            if (ctx != null) ctx.close();
            ctxHolder.remove();

            Browser br = brHolder.get();
            if (br != null) br.close();
            brHolder.remove();

            Playwright pw = pwHolder.get();
            if (pw != null) pw.close();
            pwHolder.remove();
        } catch (Exception e) {
            System.out.println("[BrowserManager] TearDown error: " + e.getMessage());
        }
    }
}
