package com.toskie.utils_Layer;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.*;
import org.testng.ITestResult;

import java.io.File;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * Core utility class — action, wait, verify, and scroll helpers.
 *
 * Browser state  → BrowserManager  (ThreadLocal Page/Browser/Context)
 * Config         → ConfigManager   (config.properties)
 * Reporting      → ReportManager   (ExtentReports, PDF, charts)
 * API / GraphQL  → ApiUtils        (login, OTP, token injection)
 */
public class UtilLayer<RouteHandler> {

    // ─── Singleton ────────────────────────────────────────────────────────────
    private static volatile UtilLayer<?> instance;

    public static UtilLayer<?> getInstance() {
        if (instance == null) {
            synchronized (UtilLayer.class) {
                if (instance == null) instance = new UtilLayer<>();
            }
        }
        return instance;
    }

    // ─── Suite-level counters (atomic for parallel safety) ───────────────────
    public final AtomicInteger passed  = new AtomicInteger(0);
    public final AtomicInteger failed  = new AtomicInteger(0);
    public final AtomicInteger skipped = new AtomicInteger(0);

    // ─── Suite / per-test timing ─────────────────────────────────────────────
    private long suiteStartTime = 0;
    private final ThreadLocal<Long> testStartTime = ThreadLocal.withInitial(() -> 0L);

    public final List<String[]> testResults   = Collections.synchronizedList(new ArrayList<>());
    public final List<String>   screenshotPaths = Collections.synchronizedList(new ArrayList<>());
    public final List<String>   stepLogs        = Collections.synchronizedList(new ArrayList<>());

    // ─── Delegate: browser ───────────────────────────────────────────────────
    /** Thread-safe: returns the Page for the calling thread. */
    public static Page getPage() { return BrowserManager.getPage(); }

    public void launchBrowser(String browserName) {
        try {
            BrowserManager.launchBrowser(browserName);
            ReportManager.getTest().log(Status.PASS, "Browser launched: " + browserName);
        } catch (Exception e) {
            if (ReportManager.getTest() != null)
                ReportManager.getTest().log(Status.FAIL, "Browser launch failed: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void tearDown() {
        try {
            BrowserManager.tearDown();
            if (ReportManager.getTest() != null)
                ReportManager.getTest().log(Status.INFO, "Browser closed.");
        } catch (Exception e) {
            System.out.println("[UtilLayer] TearDown issue: " + e.getMessage());
        }
    }

    public Page launchDevice(String deviceName) {
        try {
            Page p = BrowserManager.launchDevice(deviceName);
            ReportManager.getTest().log(Status.INFO, "Launched device: " + deviceName);
            return p;
        } catch (Exception e) {
            ReportManager.getTest().log(Status.FAIL, "Device launch failed: " + e.getMessage());
            throw e;
        }
    }

    // ─── Delegate: report ────────────────────────────────────────────────────
    public static ExtentTest getTest() { return ReportManager.getTest(); }

    public void createTest(String testName) {
        ReportManager.createTestEntry(testName);
    }

    public void createExtentReport() {
        ReportManager.createExtentReport();
    }

    public void flushReport() {
        ReportManager.flush();
        new File(System.getProperty("user.dir") + "/Reports/PDF").mkdirs();
        new File(System.getProperty("user.dir") + "/Reports/Charts").mkdirs();
        new File(System.getProperty("user.dir") + "/SnapShots").mkdirs();
    }

    public void generateFinalReports() {
        ReportManager.generateFinalReports(
                passed.get(), failed.get(), skipped.get(),
                testResults, suiteStartTime, getSuiteElapsedMs());
    }

    public void generatePieChart() {
        ReportManager.generatePieChart(passed.get(), failed.get(), skipped.get());
    }

    // ─── Delegate: API ───────────────────────────────────────────────────────
    public void loginViaQAGraphQL(String mobile)   { ApiUtils.loginViaQAGraphQL(mobile); }
    public void bypassEmailOTP(String email)        { ApiUtils.bypassEmailOTP(email); }
    public void verifyEmailViaQAGraphQL(String email){ ApiUtils.verifyEmailViaQAGraphQL(email); }
    public void injectTokenFull()                   { ApiUtils.injectTokenFull(); }
    public void injectCookies()                     { ApiUtils.injectCookies(); }
    public String getAccessToken()                  { return ApiUtils.getAccessToken(); }
    public String getRefreshToken()                 { return ApiUtils.getRefreshToken(); }
    public boolean isTokenExpired()                 { return ApiUtils.isTokenExpired(); }
    public String getDynamicOTP(String mobile)      { return ApiUtils.getDynamicOTP(mobile); }
    public String captureOTPFromGraphQL(Runnable a) { return ApiUtils.captureOTPFromGraphQL(a); }
    public void enableEmailOTPBypass(String email)  { ApiUtils.enableEmailOTPBypass(email); }

    // ─── Suite lifecycle ─────────────────────────────────────────────────────
    public void resetSuiteData() {
        passed.set(0); failed.set(0); skipped.set(0);
        testResults.clear();
        suiteStartTime = 0;
    }

    public void resetOnlyLogs() {
        stepLogs.clear();
        screenshotPaths.clear();
    }

    public void markSuiteStart()    { suiteStartTime = System.currentTimeMillis(); }
    public void markTestStart()     { testStartTime.set(System.currentTimeMillis()); }
    public long getSuiteStartTime() { return suiteStartTime; }
    public long getSuiteElapsedMs() { return System.currentTimeMillis() - suiteStartTime; }

    public void updateTestResult(ITestResult result, String screenshotPath) {
        long   durationMs = System.currentTimeMillis() - testStartTime.get();
        String className  = result.getTestClass().getRealClass().getSimpleName();
        String status;
        switch (result.getStatus()) {
            case ITestResult.SUCCESS: passed.incrementAndGet();  status = "PASS";    break;
            case ITestResult.FAILURE: failed.incrementAndGet();  status = "FAIL";    break;
            default:                  skipped.incrementAndGet(); status = "SKIPPED"; break;
        }
        String errorMsg = "";
        if (result.getThrowable() != null) {
            String msg = result.getThrowable().getMessage();
            errorMsg = (msg != null) ? msg : result.getThrowable().getClass().getSimpleName();
            if (errorMsg.length() > 300) errorMsg = errorMsg.substring(0, 300) + "...";
        }
        testResults.add(new String[]{
            className,
            result.getName(),
            status,
            errorMsg,
            screenshotPath != null ? screenshotPath : "",
            String.valueOf(durationMs)
        });
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  ACTION HELPERS
    // ═════════════════════════════════════════════════════════════════════════

    public void click(Locator locator, String name) {
        try {
            locator.click();
            getTest().log(Status.INFO, "Clicked: " + name);
        } catch (PlaywrightException e) {
            getTest().log(Status.FAIL, "Click failed: " + name + " | " + e.getMessage());
            throw e;
        }
    }

    public void forceClick(Locator locator, String name) {
        try {
            locator.click(new Locator.ClickOptions().setForce(true));
            getTest().log(Status.INFO, "Force-clicked: " + name);
        } catch (PlaywrightException e) {
            getTest().log(Status.FAIL, "Force-click failed: " + name + " | " + e.getMessage());
            throw e;
        }
    }

    public void multipleClick(Locator locator, String name, int count) {
        try {
            for (int i = 1; i <= count; i++) {
                locator.click();
                getTest().log(Status.INFO, "Clicked " + name + " — count " + i);
            }
        } catch (PlaywrightException e) {
            getTest().log(Status.FAIL, "multipleClick failed: " + name + " | " + e.getMessage());
            throw e;
        }
    }

    public void clickThreeTimes(Locator locator, String name) {
        multipleClick(locator, name, 3);
    }

    public void clickAllElements(Locator locator, String elementName) {
        try {
            int count = locator.count();
            getTest().log(Status.INFO, elementName + " count: " + count);
            for (int i = 0; i < count; i++) {
                Locator el = locator.nth(i);
                el.waitFor(new Locator.WaitForOptions()
                        .setState(WaitForSelectorState.VISIBLE).setTimeout(15000));
                el.scrollIntoViewIfNeeded();
                el.click();
                BrowserManager.getPage().waitForTimeout(500);
                getTest().log(Status.INFO, elementName + " clicked index: " + i);
            }
        } catch (PlaywrightException e) {
            getTest().log(Status.FAIL, "clickAllElements failed: " + elementName + " | " + e.getMessage());
            throw e;
        }
    }

    public void clickAndReturn(Locator locator, String elementName) {
        try {
            locator.waitFor(new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.VISIBLE).setTimeout(15000));
            locator.click();
            BrowserManager.getPage().waitForLoadState();
            getTest().log(Status.INFO, "Clicked: " + elementName);
        } catch (PlaywrightException e) {
            getTest().log(Status.FAIL, "clickAndReturn failed: " + elementName + " | " + e.getMessage());
            throw e;
        }
    }

    public void clickAndReturn(List<Locator> elements) {
        if (elements == null || elements.isEmpty()) return;
        for (Locator el : elements) {
            try {
                String name = el.textContent().trim();
                if (name.isEmpty()) name = el.toString();
                el.click();
                BrowserManager.getPage().waitForLoadState();
                sleep(1000);
                BrowserManager.getPage().goBack();
                BrowserManager.getPage().waitForLoadState();
                sleep(1000);
                getTest().log(Status.PASS, "Click-and-return: " + name);
            } catch (PlaywrightException e) {
                getTest().log(Status.FAIL, "click-and-return error: " + e.getMessage());
            }
        }
    }

    public void doubleClick(Locator locator) {
        try {
            locator.dblclick();
            getTest().log(Status.INFO, "Double-clicked element.");
        } catch (PlaywrightException e) {
            getTest().log(Status.FAIL, "Double-click failed: " + e.getMessage());
            throw e;
        }
    }

    public void clickIfEnabled(Locator locator) {
        try {
            if (locator.isEnabled()) {
                locator.click();
                getTest().log(Status.PASS, "Clicked (enabled).");
            } else {
                getTest().log(Status.FAIL, "Element disabled, skip click.");
            }
        } catch (PlaywrightException e) {
            getTest().log(Status.FAIL, "clickIfEnabled error: " + e.getMessage());
            throw e;
        }
    }

    public void closeDialogOverlay() {
        try {
            BrowserManager.getPage().keyboard().press("Escape");
            BrowserManager.getPage().waitForTimeout(300);
            getTest().log(Status.INFO, "Dialog closed with Escape.");
        } catch (PlaywrightException e) {
            getTest().log(Status.WARNING, "closeDialogOverlay failed: " + e.getMessage());
        }
    }

    // ─── Input ────────────────────────────────────────────────────────────────
    public void fill(Locator locator, String value, String elementName) {
        try {
            locator.waitFor(new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.VISIBLE).setTimeout(60000));
            locator.scrollIntoViewIfNeeded();
            locator.fill("");
            locator.fill(value);
            getTest().log(Status.PASS, elementName + " filled: '" + value + "'");
        } catch (PlaywrightException e) {
            getTest().log(Status.FAIL, elementName + " fill failed: " + e.getMessage());
            throw e;
        }
    }

    public void inputValue(Locator locator, String value) {
        try {
            locator.fill(value);
            getTest().log(Status.INFO, "Input value: '" + value + "'");
        } catch (PlaywrightException e) {
            getTest().log(Status.FAIL, "inputValue failed: " + e.getMessage());
            throw e;
        }
    }

    public void typeValue(Locator locator, String value) {
        try {
            locator.waitFor(new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
            locator.scrollIntoViewIfNeeded();
            locator.fill(value);
            locator.press("Tab");
            getTest().log(Status.INFO, "Typed: " + value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void waitAndTypeInput(Locator locator, String value) {
        try {
            locator.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
            if (locator.isEnabled()) {
                locator.fill(value);
                locator.press("Tab");
                getTest().log(Status.INFO, "waitAndTypeInput: '" + value + "'");
            } else {
                getTest().log(Status.FAIL, "Input disabled for value: " + value);
            }
        } catch (PlaywrightException e) {
            getTest().log(Status.FAIL, "waitAndTypeInput error: " + e.getMessage());
            throw e;
        }
    }

    public void clickAndInputValue(Locator locator, String value) {
        try {
            locator.click();
            locator.fill(value);
            getTest().log(Status.INFO, "Clicked and filled: '" + value + "'");
        } catch (PlaywrightException e) {
            getTest().log(Status.FAIL, "clickAndInputValue error: " + e.getMessage());
            throw e;
        }
    }

    public void clear(Locator locator, String elementName) {
        try {
            locator.waitFor(new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.VISIBLE).setTimeout(5000));
            locator.fill("");
            getTest().log(Status.PASS, elementName + " cleared.");
        } catch (PlaywrightException e) {
            getTest().log(Status.FAIL, elementName + " clear failed: " + e.getMessage());
            throw e;
        }
    }

    public void clear(Locator locator) {
        try {
            locator.fill("");
            getTest().log(Status.INFO, "Field cleared.");
        } catch (PlaywrightException e) {
            getTest().log(Status.FAIL, "clear failed: " + e.getMessage());
            throw e;
        }
    }

    public void uploadFile(Locator locator, String filePath) {
        try {
            locator.click();
            locator.setInputFiles(Paths.get(filePath));
            getTest().log(Status.INFO, "File uploaded: " + filePath);
        } catch (PlaywrightException e) {
            getTest().log(Status.FAIL, "uploadFile failed: " + e.getMessage());
            throw e;
        }
    }

    public void jsInputValueMethod(Locator locator, String value) {
        try {
            locator.evaluate("(el, val) => el.value = val", value);
            getTest().log(Status.INFO, "JS set value: '" + value + "'");
        } catch (PlaywrightException e) {
            getTest().log(Status.FAIL, "jsInputValueMethod failed: " + e.getMessage());
            throw e;
        }
    }

    // ─── Keyboard / Mouse ────────────────────────────────────────────────────
    public void hover(Locator locator, String name) {
        try {
            locator.hover();
            getTest().log(Status.INFO, "Hovered: " + name);
        } catch (PlaywrightException e) {
            getTest().log(Status.FAIL, "hover failed: " + name + " | " + e.getMessage());
            throw e;
        }
    }

    public void mouseOver(Locator element, String elementName) {
        try {
            element.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
            element.hover();
            getTest().log(Status.PASS, "Mouse over: " + elementName);
        } catch (PlaywrightException e) {
            getTest().log(Status.FAIL, "mouseOver failed: " + elementName + " | " + e.getMessage());
            throw e;
        }
    }

    public void dragAndDrop(Locator source, Locator target) {
        try {
            source.dragTo(target);
            getTest().log(Status.INFO, "Drag-and-drop performed.");
        } catch (PlaywrightException e) {
            getTest().log(Status.FAIL, "dragAndDrop failed: " + e.getMessage());
            throw e;
        }
    }

    public void pressKey(String key) {
        try {
            BrowserManager.getPage().keyboard().press(key);
            getTest().log(Status.INFO, "Key pressed: " + key);
        } catch (PlaywrightException e) {
            getTest().log(Status.FAIL, "pressKey failed: " + e.getMessage());
            throw e;
        }
    }

    public void clickWithEnter(Locator locator, String elementName) {
        try {
            locator.press("Enter");
            getTest().log(Status.INFO, "Enter on: " + elementName);
        } catch (PlaywrightException e) {
            getTest().log(Status.FAIL, "clickWithEnter failed: " + e.getMessage());
            throw e;
        }
    }

    public void mouseClick(int x, int y) {
        try {
            BrowserManager.getPage().mouse().click(x, y);
            getTest().log(Status.INFO, "Mouse click at (" + x + "," + y + ")");
        } catch (PlaywrightException e) {
            getTest().log(Status.FAIL, "mouseClick failed: " + e.getMessage());
            throw e;
        }
    }

    public void mouseMove(int x, int y) {
        try {
            BrowserManager.getPage().mouse().move(x, y);
            getTest().log(Status.INFO, "Mouse move to (" + x + "," + y + ")");
        } catch (PlaywrightException e) {
            getTest().log(Status.FAIL, "mouseMove failed: " + e.getMessage());
            throw e;
        }
    }

    // ─── Dropdowns ───────────────────────────────────────────────────────────
    public void selectByText(Locator selectLocator, String text) {
        try {
            selectLocator.selectOption(new SelectOption().setLabel(text));
            getTest().log(Status.INFO, "Selected by text: " + text);
        } catch (PlaywrightException e) {
            getTest().log(Status.FAIL, "selectByText failed: " + e.getMessage());
            throw e;
        }
    }

    public void selectByIndex(Locator selectLocator, int index) {
        try {
            selectLocator.selectOption(new SelectOption().setIndex(index));
            getTest().log(Status.INFO, "Selected by index: " + index);
        } catch (PlaywrightException e) {
            getTest().log(Status.FAIL, "selectByIndex failed: " + e.getMessage());
            throw e;
        }
    }

    public void selectByValue(Locator selectLocator, String value) {
        try {
            selectLocator.selectOption(new SelectOption().setValue(value));
            getTest().log(Status.INFO, "Selected by value: " + value);
        } catch (PlaywrightException e) {
            getTest().log(Status.FAIL, "selectByValue failed: " + e.getMessage());
            throw e;
        }
    }

    @SuppressWarnings("unchecked")
    public void selectByTextContains(Locator selectLocator, String partial) {
        try {
            List<String> opts = (List<String>) selectLocator
                    .evaluate("s => Array.from(s.options).map(o => o.textContent)");
            String found = opts.stream().filter(o -> o.contains(partial)).findFirst().orElse(null);
            if (found != null) {
                selectLocator.selectOption(new SelectOption().setLabel(found));
                getTest().log(Status.INFO, "Selected (contains): " + found);
            } else {
                throw new PlaywrightException("No option containing: " + partial);
            }
        } catch (PlaywrightException e) {
            getTest().log(Status.FAIL, "selectByTextContains failed: " + e.getMessage());
            throw e;
        }
    }

    @SuppressWarnings("unchecked")
    public List<String> getAllSelectedOptions(Locator selectLocator) {
        try {
            List<String> selected = (List<String>) selectLocator.evaluate(
                    "s => Array.from(s.options).filter(o=>o.selected).map(o=>o.textContent)");
            getTest().log(Status.INFO, "All selected options retrieved.");
            return selected;
        } catch (PlaywrightException e) {
            getTest().log(Status.FAIL, "getAllSelectedOptions failed: " + e.getMessage());
            throw e;
        }
    }

    public String getDropdownSelectedText(Locator selectLocator) {
        try {
            String text = (String) selectLocator
                    .evaluate("s => s.options[s.selectedIndex].textContent");
            getTest().log(Status.INFO, "Selected text: " + text);
            return text;
        } catch (PlaywrightException e) {
            getTest().log(Status.FAIL, "getDropdownSelectedText failed: " + e.getMessage());
            throw e;
        }
    }

    public List<String> getTextAllOptionsDropdown(Locator selectLocator) {
        try {
            List<String> opts = (List<String>) selectLocator
                    .evaluate("s => Array.from(s.options).map(o=>o.textContent)");
            getTest().log(Status.INFO, "All dropdown options retrieved.");
            return opts;
        } catch (PlaywrightException e) {
            getTest().log(Status.FAIL, "getTextAllOptionsDropdown failed: " + e.getMessage());
            throw e;
        }
    }

    public int getAllOptionCount(Locator selectLocator) {
        try {
            int count = selectLocator.locator("option").count();
            getTest().log(Status.INFO, "Option count: " + count);
            return count;
        } catch (PlaywrightException e) {
            getTest().log(Status.FAIL, "getAllOptionCount failed: " + e.getMessage());
            throw e;
        }
    }

    // ─── Checkboxes ───────────────────────────────────────────────────────────
    public void checkAll(List<Locator> checkboxes, String name) {
        try {
            for (int i = 0; i < checkboxes.size(); i++) {
                Locator cb = checkboxes.get(i);
                if (!cb.isChecked()) { cb.check(); getTest().log(Status.PASS, name + "[" + i + "] checked."); }
                else getTest().log(Status.INFO, name + "[" + i + "] already checked.");
            }
        } catch (PlaywrightException e) {
            getTest().log(Status.FAIL, "checkAll failed: " + e.getMessage());
            throw e;
        }
    }

    public void uncheckAll(List<Locator> checkboxes, String name) {
        try {
            for (int i = 0; i < checkboxes.size(); i++) {
                Locator cb = checkboxes.get(i);
                if (cb.isChecked()) { cb.uncheck(); getTest().log(Status.PASS, name + "[" + i + "] unchecked."); }
                else getTest().log(Status.INFO, name + "[" + i + "] already unchecked.");
            }
        } catch (PlaywrightException e) {
            getTest().log(Status.FAIL, "uncheckAll failed: " + e.getMessage());
            throw e;
        }
    }

    public void selectRandomCheckbox(List<Locator> checkboxes) {
        try {
            if (checkboxes.isEmpty()) { getTest().log(Status.FAIL, "No checkboxes to select."); return; }
            int idx = new Random().nextInt(checkboxes.size());
            Locator cb = checkboxes.get(idx);
            if (!cb.isChecked()) { cb.check(); getTest().log(Status.PASS, "Random checkbox[" + idx + "] selected."); }
        } catch (PlaywrightException e) {
            getTest().log(Status.FAIL, "selectRandomCheckbox failed: " + e.getMessage());
            throw e;
        }
    }

    public void selectRandomMultipleCheckboxes(List<Locator> checkboxes, int maxSelections) {
        try {
            Set<Integer> selected = new HashSet<>();
            while (selected.size() < Math.min(maxSelections, checkboxes.size())) {
                int idx = new Random().nextInt(checkboxes.size());
                if (!selected.contains(idx)) {
                    selected.add(idx);
                    Locator cb = checkboxes.get(idx);
                    if (!cb.isChecked()) cb.check();
                    getTest().log(Status.PASS, "Random checkbox[" + idx + "] selected.");
                }
            }
        } catch (PlaywrightException e) {
            getTest().log(Status.FAIL, "selectRandomMultipleCheckboxes failed: " + e.getMessage());
            throw e;
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  NAVIGATION
    // ═════════════════════════════════════════════════════════════════════════

    public void openUrl(String url) {
        try {
            BrowserManager.getPage().navigate(url, new Page.NavigateOptions().setTimeout(60000));
            BrowserManager.getPage().waitForLoadState(LoadState.DOMCONTENTLOADED);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void refreshPage() {
        try {
            BrowserManager.getPage().reload();
            getTest().log(Status.INFO, "Page refreshed.");
        } catch (PlaywrightException e) {
            getTest().log(Status.FAIL, "refreshPage failed: " + e.getMessage());
            throw e;
        }
    }

    public void navigateBack() {
        try {
            BrowserManager.getPage().goBack();
            getTest().log(Status.INFO, "Navigated back.");
        } catch (PlaywrightException e) {
            getTest().log(Status.FAIL, "navigateBack failed: " + e.getMessage());
            throw e;
        }
    }

    public void navigateForward() {
        try {
            BrowserManager.getPage().goForward();
            getTest().log(Status.INFO, "Navigated forward.");
        } catch (PlaywrightException e) {
            getTest().log(Status.FAIL, "navigateForward failed: " + e.getMessage());
            throw e;
        }
    }

    public String getCurrentURL() {
        try {
            String url = BrowserManager.getPage().url();
            getTest().log(Status.INFO, "Current URL: " + url);
            return url;
        } catch (PlaywrightException e) {
            getTest().log(Status.FAIL, "getCurrentURL failed: " + e.getMessage());
            throw e;
        }
    }

    public String getCurrentTitle() {
        try {
            String title = BrowserManager.getPage().title();
            getTest().log(Status.INFO, "Page title: " + title);
            return title;
        } catch (PlaywrightException e) {
            getTest().log(Status.FAIL, "getCurrentTitle failed: " + e.getMessage());
            throw e;
        }
    }

    public void maximizeWindow() {
        try {
            BrowserManager.getPage().setViewportSize(1920, 1080);
            getTest().log(Status.INFO, "Viewport set to 1920x1080.");
        } catch (PlaywrightException e) {
            getTest().log(Status.FAIL, "maximizeWindow failed: " + e.getMessage());
            throw e;
        }
    }

    public void closeWindow() {
        try {
            Page p = BrowserManager.getPage();
            if (p != null) p.close();
            getTest().log(Status.INFO, "Page closed.");
        } catch (PlaywrightException e) {
            getTest().log(Status.FAIL, "closeWindow failed: " + e.getMessage());
            throw e;
        }
    }

    public void closeAllWindow() {
        try {
            BrowserManager.getPage().close();
            getTest().log(Status.INFO, "All pages closed.");
        } catch (PlaywrightException e) {
            getTest().log(Status.FAIL, "closeAllWindow failed: " + e.getMessage());
        }
    }

    public void switchToWindowByTitle(String title) {
        try {
            for (Page p : BrowserManager.getContext().pages()) {
                if (p.title().equals(title)) {
                    BrowserManager.setPage(p);
                    getTest().log(Status.INFO, "Switched to window: " + title);
                    return;
                }
            }
            throw new PlaywrightException("No window with title: " + title);
        } catch (PlaywrightException e) {
            getTest().log(Status.FAIL, "switchToWindowByTitle failed: " + e.getMessage());
            throw e;
        }
    }

    public Page waitForPopupClick(Locator trigger, String name) {
        try {
            Page popup = BrowserManager.getPage().waitForPopup(() -> trigger.click());
            getTest().log(Status.INFO, "Popup opened after: " + name + " | URL: " + popup.url());
            return popup;
        } catch (PlaywrightException e) {
            getTest().log(Status.FAIL, "waitForPopupClick failed: " + e.getMessage());
            throw e;
        }
    }

    public Page switchToPopupIfOpened() {
        try {
            for (Page p : BrowserManager.getPage().context().pages()) {
                if (!p.equals(BrowserManager.getPage())) {
                    getTest().log(Status.INFO, "Popup switched: " + p.url());
                    return p;
                }
            }
            getTest().log(Status.WARNING, "No popup found.");
            return null;
        } catch (PlaywrightException e) {
            getTest().log(Status.FAIL, "switchToPopupIfOpened failed: " + e.getMessage());
            throw e;
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  WAITS
    // ═════════════════════════════════════════════════════════════════════════

    public void waitForPageLoad() {
        BrowserManager.getPage().waitForLoadState();
    }

    public void waitForLoadState(LoadState state, int timeout) {
        try {
            BrowserManager.getPage().waitForLoadState(state,
                    new Page.WaitForLoadStateOptions().setTimeout(timeout));
            getTest().log(Status.PASS, "Load state reached: " + state);
        } catch (PlaywrightException e) {
            getTest().log(Status.FAIL, "waitForLoadState failed: " + e.getMessage());
            throw e;
        }
    }

    public void waitForElementVisible(String selector, int timeout) {
        try {
            BrowserManager.getPage().waitForSelector(selector,
                    new Page.WaitForSelectorOptions().setTimeout(timeout)
                            .setState(WaitForSelectorState.VISIBLE));
            getTest().log(Status.PASS, "Element visible: " + selector);
        } catch (PlaywrightException e) {
            getTest().log(Status.FAIL, "waitForElementVisible failed: " + selector);
            throw e;
        }
    }

    public void waitForElementVisible(Locator locator, String elementName) {
        waitForElementVisible(locator, elementName, 10000);
    }

    public void waitForElementVisible(Locator locator, String elementName, int timeoutMs) {
        try {
            locator.waitFor(new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.VISIBLE).setTimeout(timeoutMs));
            getTest().log(Status.PASS, elementName + " visible.");
        } catch (PlaywrightException e) {
            getTest().log(Status.FAIL, elementName + " NOT visible within " + timeoutMs + "ms: " + e.getMessage());
            throw e;
        }
    }

    public void waitForTextInElement(String selector, String expectedText, int timeout) {
        try {
            BrowserManager.getPage().waitForFunction(
                    "selector => document.querySelector(selector)?.textContent.includes('" + expectedText + "')",
                    selector,
                    new Page.WaitForFunctionOptions().setTimeout(timeout));
            getTest().log(Status.PASS, "Text '" + expectedText + "' found in: " + selector);
        } catch (PlaywrightException e) {
            getTest().log(Status.FAIL, "waitForTextInElement failed: " + e.getMessage());
            throw e;
        }
    }

    public void waitForURL(String urlPart) {
        try {
            BrowserManager.getPage().waitForURL(urlPart);
            getTest().log(Status.INFO, "URL matched: " + urlPart);
        } catch (PlaywrightException e) {
            getTest().log(Status.FAIL, "waitForURL failed: " + e.getMessage());
            throw e;
        }
    }

    public void waitForSelector(String selector) {
        try {
            BrowserManager.getPage().waitForSelector(selector);
            getTest().log(Status.INFO, "Selector ready: " + selector);
        } catch (PlaywrightException e) {
            getTest().log(Status.FAIL, "waitForSelector failed: " + selector);
            throw e;
        }
    }

    public void waitForLocatorVisible(Locator locator) {
        try {
            locator.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
            getTest().log(Status.INFO, "Locator visible.");
        } catch (PlaywrightException e) {
            getTest().log(Status.FAIL, "waitForLocatorVisible failed: " + e.getMessage());
            throw e;
        }
    }

    public void sleep(int millis) {
        try {
            Thread.sleep(millis);
            if (ReportManager.getTest() != null)
                ReportManager.getTest().log(Status.INFO, "Slept " + millis + "ms.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  VERIFICATIONS
    // ═════════════════════════════════════════════════════════════════════════

    public void verifyElementVisible(Locator locator, String elementName) {
        try {
            locator.waitFor(new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.VISIBLE).setTimeout(60000));
            getTest().log(locator.isVisible() ? Status.PASS : Status.FAIL,
                    elementName + (locator.isVisible() ? " is visible." : " is NOT visible."));
        } catch (PlaywrightException e) {
            getTest().log(Status.FAIL, elementName + " not visible: " + e.getMessage());
            throw e;
        }
    }

    public void verifyText(Locator locator, String expectedText, String elementName) {
        try {
            locator.waitFor(new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.VISIBLE).setTimeout(5000));
            String actual = locator.textContent().trim();
            getTest().log(actual.equals(expectedText) ? Status.PASS : Status.FAIL,
                    elementName + " | expected='" + expectedText + "' actual='" + actual + "'");
        } catch (PlaywrightException e) {
            getTest().log(Status.FAIL, elementName + " verifyText error: " + e.getMessage());
            throw e;
        }
    }

    public void verifyTitle(String expectedTitle) {
        try {
            String actual = BrowserManager.getPage().title();
            getTest().log(actual.equals(expectedTitle) ? Status.PASS : Status.FAIL,
                    "Title | expected='" + expectedTitle + "' actual='" + actual + "'");
        } catch (PlaywrightException e) {
            getTest().log(Status.FAIL, "verifyTitle error: " + e.getMessage());
            throw e;
        }
    }

    public void verifyTextContains(String actualValue, String expectedContains) {
        getTest().log(actualValue.contains(expectedContains) ? Status.PASS : Status.FAIL,
                "'" + actualValue + "' " + (actualValue.contains(expectedContains) ? "contains" : "does NOT contain")
                        + " '" + expectedContains + "'");
    }

    public void verifyDisplayed(Locator locator) {
        try {
            boolean vis = locator.isVisible();
            getTest().log(vis ? Status.PASS : Status.FAIL, vis ? "Element VISIBLE." : "Element NOT VISIBLE.");
        } catch (PlaywrightException e) {
            getTest().log(Status.FAIL, "verifyDisplayed error: " + e.getMessage());
            throw e;
        }
    }

    public void verifyDisplayedByList(List<Locator> locators) {
        if (locators == null || locators.isEmpty()) return;
        for (Locator l : locators) {
            try { verifyDisplayed(l); }
            catch (PlaywrightException e) { getTest().log(Status.INFO, "List verify error: " + e.getMessage()); }
        }
    }

    public void verifyEnabled(Locator locator) {
        try {
            boolean en = locator.isEnabled();
            getTest().log(en ? Status.PASS : Status.FAIL, en ? "ENABLED." : "DISABLED.");
        } catch (PlaywrightException e) {
            getTest().log(Status.FAIL, "verifyEnabled error: " + e.getMessage());
            throw e;
        }
    }

    public void verifyDisabled(Locator locator) {
        try {
            boolean dis = !locator.isEnabled();
            getTest().log(dis ? Status.PASS : Status.FAIL, dis ? "DISABLED." : "ENABLED (expected disabled).");
        } catch (PlaywrightException e) {
            getTest().log(Status.FAIL, "verifyDisabled error: " + e.getMessage());
            throw e;
        }
    }

    public void verifySelected(Locator locator) {
        try {
            boolean sel = locator.isChecked();
            getTest().log(sel ? Status.PASS : Status.FAIL, sel ? "SELECTED." : "NOT SELECTED.");
        } catch (PlaywrightException e) {
            getTest().log(Status.FAIL, "verifySelected error: " + e.getMessage());
            throw e;
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  SCROLL
    // ═════════════════════════════════════════════════════════════════════════

    public void scrollToElement(Locator locator) {
        try {
            locator.scrollIntoViewIfNeeded();
            getTest().log(Status.INFO, "Scrolled to element.");
        } catch (PlaywrightException e) {
            getTest().log(Status.FAIL, "scrollToElement failed: " + e.getMessage());
            throw e;
        }
    }

    public void scrollIntoView(Locator locator, String elementName) {
        try {
            locator.waitFor(new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.ATTACHED).setTimeout(15000));
            locator.scrollIntoViewIfNeeded();
            BrowserManager.getPage().waitForTimeout(500);
            getTest().log(Status.PASS, elementName + " scrolled into view.");
        } catch (PlaywrightException e) {
            getTest().log(Status.FAIL, "scrollIntoView failed: " + e.getMessage());
            throw e;
        }
    }

    public void scrollUntilElementVisible(String selector, int maxScrolls) {
        Locator element = BrowserManager.getPage().locator(selector);
        for (int i = 0; i < maxScrolls; i++) {
            if (element.count() > 0 && element.first().isVisible()) {
                getTest().log(Status.PASS, "Element visible after scroll: " + selector);
                return;
            }
            BrowserManager.getPage().mouse().wheel(0, 1000);
            BrowserManager.getPage().waitForTimeout(1000);
        }
        getTest().log(Status.FAIL, "Element not found after scrolling: " + selector);
        throw new RuntimeException("Element not found: " + selector);
    }

    public void scrollUntilVisible(Locator locator, String elementName) {
        try {
            for (int i = 0; i < 10; i++) {
                if (locator.count() > 0 && locator.first().isVisible()) {
                    locator.first().scrollIntoViewIfNeeded();
                    getTest().log(Status.PASS, elementName + " visible after scroll.");
                    return;
                }
                BrowserManager.getPage().mouse().wheel(0, 1500);
                BrowserManager.getPage().waitForTimeout(800);
            }
            throw new RuntimeException("Element not found: " + elementName);
        } catch (Exception e) {
            getTest().log(Status.FAIL, "scrollUntilVisible failed: " + e.getMessage());
            throw e;
        }
    }

    public void smartScrollToElement(Locator locator, String elementName) {
        try {
            locator.waitFor(new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.ATTACHED).setTimeout(10000));
            for (int i = 0; i < 15; i++) {
                if (locator.isVisible()) {
                    locator.scrollIntoViewIfNeeded();
                    getTest().log(Status.PASS, elementName + " visible after smart scroll.");
                    return;
                }
                BrowserManager.getPage().mouse().wheel(0, 1200);
                BrowserManager.getPage().waitForTimeout(500);
            }
            locator.scrollIntoViewIfNeeded();
            if (!locator.isVisible())
                throw new RuntimeException("Not visible after scrolling: " + elementName);
            getTest().log(Status.PASS, elementName + " visible after force scroll.");
        } catch (Exception e) {
            getTest().log(Status.FAIL, "smartScrollToElement failed: " + e.getMessage());
            throw e;
        }
    }

    public void scrollToBottom() {
        int prev = 0, curr = 1;
        while (prev != curr) {
            prev = Integer.parseInt(
                    BrowserManager.getPage().evaluate("document.body.scrollHeight").toString());
            BrowserManager.getPage().mouse().wheel(0, 2000);
            BrowserManager.getPage().waitForTimeout(1000);
            curr = Integer.parseInt(
                    BrowserManager.getPage().evaluate("document.body.scrollHeight").toString());
        }
        getTest().log(Status.INFO, "Scrolled to bottom.");
    }

    public void scrollHorizontally(String locator, int pixels) {
        BrowserManager.getPage().locator(locator).evaluate("(el, px) => el.scrollBy(px, 0)", pixels);
        getTest().log(Status.INFO, "Scrolled horizontally: " + pixels + "px");
    }

    public void scrollHorizontallyUntilVisible(String containerLocator, String elementLocator) {
        Locator container = BrowserManager.getPage().locator(containerLocator);
        Locator element   = BrowserManager.getPage().locator(elementLocator);
        for (int i = 0; i < 10; i++) {
            if (element.count() > 0 && element.first().isVisible()) {
                getTest().log(Status.PASS, "Element visible after horizontal scroll.");
                return;
            }
            container.evaluate("(el) => el.scrollBy(400, 0)");
            BrowserManager.getPage().waitForTimeout(1000);
        }
        throw new RuntimeException("Element not found after horizontal scroll.");
    }

    public void scrollAndClick(String selector) {
        scrollUntilElementVisible(selector, 10);
        Locator el = BrowserManager.getPage().locator(selector);
        el.scrollIntoViewIfNeeded();
        el.click();
        getTest().log(Status.PASS, "Scrolled and clicked: " + selector);
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  ELEMENT INFO
    // ═════════════════════════════════════════════════════════════════════════

    public String getText(Locator locator) {
        try {
            String text = locator.textContent();
            getTest().log(Status.INFO, "Text: '" + text + "'");
            return text;
        } catch (PlaywrightException e) {
            getTest().log(Status.FAIL, "getText failed: " + e.getMessage());
            throw e;
        }
    }

    public String getAttribute(Locator locator, String attributeName) {
        try {
            String attr = locator.getAttribute(attributeName);
            getTest().log(Status.INFO, attributeName + "='" + attr + "'");
            return attr;
        } catch (PlaywrightException e) {
            getTest().log(Status.FAIL, "getAttribute failed: " + e.getMessage());
            throw e;
        }
    }

    public int[] getElementSize(String selector) {
        try {
            BoundingBox box = BrowserManager.getPage().locator(selector).boundingBox();
            if (box != null) {
                int[] size = {(int) box.width, (int) box.height};
                getTest().log(Status.INFO, selector + " size: " + size[0] + "x" + size[1]);
                return size;
            }
            return new int[]{0, 0};
        } catch (PlaywrightException e) {
            getTest().log(Status.FAIL, "getElementSize failed: " + e.getMessage());
            throw e;
        }
    }

    public int[] getElementLocation(String selector) {
        try {
            BoundingBox box = BrowserManager.getPage().locator(selector).boundingBox();
            if (box != null) {
                int[] loc = {(int) box.x, (int) box.y};
                getTest().log(Status.INFO, selector + " loc: (" + loc[0] + "," + loc[1] + ")");
                return loc;
            }
            return new int[]{0, 0};
        } catch (PlaywrightException e) {
            getTest().log(Status.FAIL, "getElementLocation failed: " + e.getMessage());
            throw e;
        }
    }

    public Locator getByText(String text) {
        try {
            Locator loc = BrowserManager.getPage().getByText(text);
            getTest().log(Status.INFO, "Located by text: '" + text + "'");
            return loc;
        } catch (PlaywrightException e) {
            getTest().log(Status.FAIL, "getByText failed: " + e.getMessage());
            throw e;
        }
    }

    public Locator getNth(String selector, int index) {
        try {
            Locator loc = BrowserManager.getPage().locator(selector).nth(index);
            getTest().log(Status.INFO, "nth[" + index + "] for: " + selector);
            return loc;
        } catch (PlaywrightException e) {
            getTest().log(Status.FAIL, "getNth failed: " + e.getMessage());
            throw e;
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  FRAMES / DIALOGS
    // ═════════════════════════════════════════════════════════════════════════

    public Frame switchToFrame(String frameSelector) {
        try {
            Frame frame = BrowserManager.getPage().frame(frameSelector);
            if (frame == null) {
                frame = BrowserManager.getPage().frames().stream()
                    .filter(f -> frameSelector.equals(f.name())
                        || (f.url() != null && f.url().contains(frameSelector)))
                    .findFirst().orElse(null);
            }
            if (frame != null) {
                getTest().log(Status.INFO, "Switched to frame: " + frameSelector);
                return frame;
            }
            throw new PlaywrightException("Frame not found: " + frameSelector);
        } catch (PlaywrightException e) {
            getTest().log(Status.FAIL, "switchToFrame failed: " + e.getMessage());
            throw e;
        }
    }

    public void switchToMainFrame() {
        getTest().log(Status.INFO, "Main frame context active.");
    }

    public void alertAccept() {
        try {
            BrowserManager.getPage().onceDialog(dialog -> {
                getTest().log(Status.INFO, "Alert accepted: '" + dialog.message() + "'");
                dialog.accept();
            });
            getTest().log(Status.INFO, "Alert accept listener set.");
        } catch (PlaywrightException e) {
            getTest().log(Status.FAIL, "alertAccept failed: " + e.getMessage());
            throw e;
        }
    }

    public void alertDismiss() {
        try {
            BrowserManager.getPage().onceDialog(dialog -> {
                getTest().log(Status.INFO, "Alert dismissed: '" + dialog.message() + "'");
                dialog.dismiss();
            });
            getTest().log(Status.INFO, "Alert dismiss listener set.");
        } catch (PlaywrightException e) {
            getTest().log(Status.FAIL, "alertDismiss failed: " + e.getMessage());
            throw e;
        }
    }

    public void handleInitialPopup(Locator allowBtn, Locator skipBtn) {
        try {
            BrowserManager.getPage().waitForTimeout(1500);
            if (allowBtn != null && allowBtn.count() > 0 && allowBtn.first().isVisible()) {
                allowBtn.first().click();
                getTest().log(Status.INFO, "Popup: Allow clicked.");
                return;
            }
            if (skipBtn != null && skipBtn.count() > 0 && skipBtn.first().isVisible()) {
                skipBtn.first().click();
                getTest().log(Status.INFO, "Popup: Skip clicked.");
                return;
            }
            getTest().log(Status.INFO, "No popup found.");
        } catch (Exception e) {
            getTest().log(Status.WARNING, "handleInitialPopup error: " + e.getMessage());
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  TRACING / MOCKING
    // ═════════════════════════════════════════════════════════════════════════

    public void startTracing() {
        try {
            BrowserManager.getContext().tracing()
                    .start(new Tracing.StartOptions().setScreenshots(true).setSnapshots(true));
            getTest().log(Status.INFO, "Tracing started.");
        } catch (PlaywrightException e) {
            getTest().log(Status.FAIL, "startTracing failed: " + e.getMessage());
            throw e;
        }
    }

    public void stopTracing(String path) {
        try {
            BrowserManager.getContext().tracing()
                    .stop(new Tracing.StopOptions().setPath(Paths.get(path)));
            getTest().log(Status.INFO, "Tracing saved: " + path);
        } catch (PlaywrightException e) {
            getTest().log(Status.FAIL, "stopTracing failed: " + e.getMessage());
            throw e;
        }
    }

    public void mockRoute(String urlPattern, Consumer<Route> handler) {
        try {
            BrowserManager.getPage().route(urlPattern, handler);
            getTest().log(Status.INFO, "Mock route added: " + urlPattern);
        } catch (PlaywrightException e) {
            getTest().log(Status.FAIL, "mockRoute failed: " + e.getMessage());
            throw e;
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  DATE PICKER
    // ═════════════════════════════════════════════════════════════════════════

    public void selectRandom18PlusDOB(Locator calendarInput) {
        try {
            LocalDate dob   = getRandom18PlusDOB();
            String year     = String.valueOf(dob.getYear());
            String day      = String.valueOf(dob.getDayOfMonth());
            String month    = dob.getMonth().name().charAt(0)
                    + dob.getMonth().name().substring(1).toLowerCase();
            Page page = BrowserManager.getPage();

            click(calendarInput, "Open Calendar");
            page.waitForTimeout(500);

            Locator yearHeader = page.locator("//button[contains(@class,'heading-5')]");
            yearHeader.waitFor();
            yearHeader.click();
            page.waitForTimeout(500);

            for (int i = 0; i < 25; i++) {
                Locator yearLoc = page.locator("//button[text()='" + year + "']");
                if (yearLoc.count() > 0 && yearLoc.first().isVisible()) { yearLoc.first().click(); break; }
                Locator prev = page.locator("(//button[.//svg])[1]");
                if (prev.count() > 0) prev.first().click();
                page.waitForTimeout(300);
            }

            page.locator("//button[text()='" + month + "']").waitFor();
            page.locator("//button[text()='" + month + "']").click();
            page.waitForTimeout(300);
            page.locator("//button[text()='" + day + "']").waitFor();
            page.locator("//button[text()='" + day + "']").click();
            page.waitForTimeout(300);
            click(page.locator("//span[contains(text(),'Save')]"), "Save DOB");
            System.out.println("[UtilLayer] DOB selected: " + dob);
        } catch (Exception e) {
            System.out.println("[UtilLayer] DOB selection failed: " + e.getMessage());
            throw new RuntimeException("DOB selection failed", e);
        }
    }

    public static LocalDate getRandom18PlusDOB() {
        LocalDate maxDate = LocalDate.now().minusYears(18);
        int maxYear = maxDate.getYear();
        int minYear = maxYear - 32;
        int year    = minYear + new Random().nextInt(maxYear - minYear);
        int month   = 1 + new Random().nextInt(12);
        int day     = 1 + new Random().nextInt(28);
        return LocalDate.of(year, month, day);
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  SCREENSHOT
    // ═════════════════════════════════════════════════════════════════════════

    public String captureScreenshot(String imageName, ITestResult result) {
        DateFormat df = new SimpleDateFormat("MM_dd_yyyy_HH_mm_ss_a");
        String ts = df.format(new Date());

        String base = System.getProperty("user.dir");
        new File(base + "/SnapShots").mkdirs();
        String path = base + "/SnapShots/" + imageName + "_" + ts + ".png";

        try {
            Page page = BrowserManager.getPage();
            page.screenshot(new Page.ScreenshotOptions()
                    .setPath(Paths.get(path)).setFullPage(true));

            if (result.getStatus() == ITestResult.FAILURE) {
                getTest().fail("Test Failed");
                getTest().addScreenCaptureFromPath(path);
            } else {
                getTest().info("Screenshot captured.");
                getTest().addScreenCaptureFromPath(path);
            }
            return path;
        } catch (PlaywrightException e) {
            getTest().fail("Screenshot failed: " + e.getMessage());
            return null;
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  MISC (Calendar, OTP UI entry, etc.)
    // ═════════════════════════════════════════════════════════════════════════

    public void enterOTP(Locator otpField, String otp) {
        try {
            otpField.waitFor(new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.VISIBLE).setTimeout(5000));
            otpField.fill(otp);
            getTest().log(Status.PASS, "OTP entered: " + otp);
        } catch (PlaywrightException e) {
            getTest().log(Status.FAIL, "enterOTP failed: " + e.getMessage());
            throw e;
        }
    }

    public void handleGraphQLOTPFlow(Locator otpField, Locator submitBtn, Runnable clickAction) {
        try {
            String otp = captureOTPFromGraphQL(clickAction);
            enterOTP(otpField, otp);
            click(submitBtn, "Submit OTP");
        } catch (Exception e) {
            getTest().log(Status.FAIL, "OTP flow failed.");
            throw e;
        }
    }

    public void selectDateFromCalendar(Locator calendarLocator,
                                        Locator dateElementsLocator, String expectedDate) {
        try {
            calendarLocator.click();
            getTest().log(Status.INFO, "Calendar opened.");
            boolean found = false;
            for (Locator dt : dateElementsLocator.all()) {
                if (dt.textContent().trim().equalsIgnoreCase(expectedDate)) {
                    dt.click();
                    getTest().log(Status.PASS, "Date selected: " + expectedDate);
                    found = true;
                    break;
                }
            }
            if (!found) getTest().log(Status.WARNING, "Date '" + expectedDate + "' not found.");
        } catch (PlaywrightException e) {
            getTest().log(Status.FAIL, "selectDateFromCalendar failed: " + e.getMessage());
            throw e;
        }
    }
}
