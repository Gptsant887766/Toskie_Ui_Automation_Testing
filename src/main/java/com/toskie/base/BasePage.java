package com.toskie.base;

import com.aventstack.extentreports.Status;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.toskie.constants.FrameworkConstants;
import com.toskie.factory.PlaywrightFactory;
import com.toskie.reports.ExtentManager;

import java.io.File;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Abstract base for all enterprise page objects.
 *
 * Contract every page must fulfil:
 *   - isLoaded() — verify the page rendered correctly after navigation
 *
 * Compatibility: page objects can still extend BasePage even while also using
 * UtilLayer helpers — both call BrowserManager.getPage() under the hood, which
 * PlaywrightFactory keeps in sync.
 */
public abstract class BasePage {

    protected final Page page;

    /** Default constructor — resolves Page from PlaywrightFactory (thread-safe). */
    protected BasePage() {
        this.page = PlaywrightFactory.getPage();
    }

    /** Constructor for explicit page injection (useful in tests that manage pages directly). */
    protected BasePage(Page page) {
        this.page = page;
    }

    // ─── Contract ─────────────────────────────────────────────────────────────

    /**
     * Returns true if the page has loaded successfully and its primary landmark
     * element is visible. Each page object provides its own implementation.
     */
    public abstract boolean isLoaded();

    // ─── Navigation ───────────────────────────────────────────────────────────

    protected void navigate(String url) {
        try {
            page.navigate(url, new Page.NavigateOptions()
                    .setTimeout(FrameworkConstants.TIMEOUT_NAVIGATION));
            page.waitForLoadState(LoadState.DOMCONTENTLOADED);
            log(Status.INFO, "Navigated to: " + url);
        } catch (Exception e) {
            log(Status.WARNING, "Navigation issue — " + url + ": " + e.getMessage());
        }
    }

    protected String currentUrl()  { return page.url(); }
    protected String currentTitle(){ return page.title(); }

    protected void goBack() {
        page.goBack();
        log(Status.INFO, "Browser back");
    }

    protected void reload() {
        page.reload();
        log(Status.INFO, "Page reloaded");
    }

    // ─── Click ────────────────────────────────────────────────────────────────

    protected void click(Locator locator, String name) {
        waitVisible(locator, name);
        locator.click();
        log(Status.INFO, "Clicked: " + name);
    }

    protected void clickIfVisible(Locator locator, String name) {
        try {
            if (locator.isVisible()) {
                locator.click();
                log(Status.INFO, "Clicked (optional): " + name);
            }
        } catch (Exception ignored) {}
    }

    protected void forceClick(Locator locator, String name) {
        locator.click(new Locator.ClickOptions().setForce(true));
        log(Status.INFO, "Force-clicked: " + name);
    }

    // ─── Input ────────────────────────────────────────────────────────────────

    protected void fill(Locator locator, String value, String name) {
        waitVisible(locator, name);
        locator.fill("");
        locator.fill(value);
        log(Status.INFO, "Filled " + name + ": '" + value + "'");
    }

    protected void clear(Locator locator, String name) {
        locator.fill("");
        log(Status.INFO, "Cleared: " + name);
    }

    protected void pressKey(String key) {
        page.keyboard().press(key);
        log(Status.INFO, "Key: " + key);
    }

    protected void pressEnterOn(Locator locator) {
        locator.press("Enter");
    }

    // ─── State ────────────────────────────────────────────────────────────────

    protected boolean isVisible(Locator locator) {
        try { return locator.isVisible(); } catch (Exception e) { return false; }
    }

    protected boolean isEnabled(Locator locator) {
        try { return locator.isEnabled(); } catch (Exception e) { return false; }
    }

    protected String getText(Locator locator) {
        try { return locator.textContent().trim(); } catch (Exception e) { return ""; }
    }

    protected String getInputValue(Locator locator) {
        try { return locator.inputValue().trim(); } catch (Exception e) { return ""; }
    }

    protected int count(Locator locator) {
        try { return locator.count(); } catch (Exception e) { return 0; }
    }

    // ─── Waits ────────────────────────────────────────────────────────────────

    protected void waitVisible(Locator locator, String name) {
        waitVisible(locator, name, FrameworkConstants.TIMEOUT_ELEMENT);
    }

    protected void waitVisible(Locator locator, String name, int timeoutMs) {
        try {
            locator.waitFor(new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.VISIBLE)
                    .setTimeout(timeoutMs));
        } catch (Exception e) {
            log(Status.WARNING, "'" + name + "' not visible within " + timeoutMs + "ms");
        }
    }

    protected void waitHidden(Locator locator, String name) {
        try {
            locator.waitFor(new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.HIDDEN)
                    .setTimeout(FrameworkConstants.TIMEOUT_ELEMENT));
        } catch (Exception ignored) {}
    }

    protected void waitForPageLoad() {
        try {
            page.waitForLoadState(LoadState.DOMCONTENTLOADED,
                    new Page.WaitForLoadStateOptions()
                            .setTimeout(FrameworkConstants.TIMEOUT_PAGE_LOAD));
        } catch (Exception ignored) {}
    }

    protected void waitForUrl(String urlFragment) {
        try {
            page.waitForURL("**" + urlFragment + "**",
                    new Page.WaitForURLOptions()
                            .setTimeout(FrameworkConstants.TIMEOUT_NAVIGATION));
        } catch (Exception e) {
            log(Status.WARNING, "URL fragment not matched: " + urlFragment);
        }
    }

    protected void pause(int millis) {
        try { Thread.sleep(millis); }
        catch (InterruptedException ex) { Thread.currentThread().interrupt(); }
    }

    // ─── Scroll ───────────────────────────────────────────────────────────────

    protected void scrollTo(Locator locator) {
        try { locator.scrollIntoViewIfNeeded(); } catch (Exception ignored) {}
    }

    protected void scrollDown(int pixels) {
        page.mouse().wheel(0, pixels);
    }

    // ─── Screenshot ───────────────────────────────────────────────────────────

    protected String screenshotBase64() {
        try {
            byte[] bytes = page.screenshot(new Page.ScreenshotOptions().setFullPage(false));
            return java.util.Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) { return ""; }
    }

    protected String screenshotToFile(String testName) {
        try {
            new File(FrameworkConstants.SCREENSHOTS_DIR).mkdirs();
            String ts   = new SimpleDateFormat("dd_MMM_yyyy_HH_mm_ss").format(new Date());
            String path = FrameworkConstants.SCREENSHOTS_DIR + testName + "_" + ts + ".png";
            page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(path)).setFullPage(false));
            return path;
        } catch (Exception e) {
            log(Status.WARNING, "Screenshot failed: " + e.getMessage());
            return null;
        }
    }

    // ─── Report logging ───────────────────────────────────────────────────────

    protected void log(Status status, String message) {
        var test = ExtentManager.getTest();
        if (test != null) test.log(status, message);
    }

    protected void logPass(String msg)    { log(Status.PASS,    msg); }
    protected void logFail(String msg)    { log(Status.FAIL,    msg); }
    protected void logInfo(String msg)    { log(Status.INFO,    msg); }
    protected void logWarning(String msg) { log(Status.WARNING, msg); }
}
