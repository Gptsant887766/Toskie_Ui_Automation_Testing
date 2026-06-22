package com.toskie.base;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import com.toskie.annotations.TestInfo;
import com.toskie.config.ConfigReader;
import com.toskie.constants.FrameworkConstants;
import com.toskie.factory.PlaywrightFactory;
import com.toskie.reports.ExtentManager;
import com.toskie.reports.ReportUtil;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Enterprise base class for all migrated and new test classes.
 *
 * Features:
 *   - PlaywrightFactory ThreadLocal browser lifecycle (CI/Docker headless aware)
 *   - Screenshot-on-failure → SnapShots/ → attached to both Extent and Allure
 *   - @TestInfo annotation support — module, owner, tcId, severity stamped automatically
 *   - Browser launch retry (flaky CI resilience)
 *   - XML-parameter browser override: {@code <parameter name="browser" value="firefox"/>}
 *   - Listener registration via @Listeners so IDE test runs also fire listeners
 *
 * Migration from legacy BaseTest_Layer.BaseTest:
 *   1. Change extends to com.toskie.base.BaseTest
 *   2. Remove utilLayer usage — use page objects directly with PlaywrightFactory
 *   3. Replace ReportManager.getTest() → ReportUtil.step() / ReportUtil.pass()
 */
@Listeners({
    com.toskie.listeners.ExecutionListener.class,
    com.toskie.listeners.SuiteListener.class,
    com.toskie.listeners.TestListener.class,
    io.qameta.allure.testng.AllureTestNg.class
})
public abstract class BaseTest {

    // Subclasses can read these to build URLs or make assertions
    protected String browser = ConfigReader.getBrowser();
    protected String baseUrl  = ConfigReader.getBaseUrl();

    // ─── Suite lifecycle ──────────────────────────────────────────────────────

    @BeforeSuite(alwaysRun = true)
    public synchronized void suiteSetup() {
        ExtentManager.init();
        System.out.println("[BaseTest] Suite started — browser=" + browser.toUpperCase()
                + " | url=" + baseUrl + " | CI=" + PlaywrightFactory.isCI());
    }

    @AfterSuite(alwaysRun = true)
    public synchronized void suiteTeardown() {
        ExtentManager.flush();
        System.out.println("[BaseTest] Suite complete.");
    }

    // ─── Per-test lifecycle ───────────────────────────────────────────────────

    @BeforeMethod(alwaysRun = true)
    public void testSetup(Method method, ITestContext context) {
        // Allow suite XML to override the browser per test group
        String xmlBrowser = context.getCurrentXmlTest().getParameter("browser");
        if (xmlBrowser != null && !xmlBrowser.isBlank()) {
            browser = xmlBrowser.trim();
        }

        String testLabel = method.getDeclaringClass().getSimpleName() + "." + method.getName();
        ExtentTest test = ExtentManager.createTest(testLabel);
        test.log(Status.INFO, "Browser: " + browser.toUpperCase()
                + " | URL: " + baseUrl
                + " | Headless: " + (PlaywrightFactory.isCI() || ConfigReader.isHeadless())
                + " | Thread: " + Thread.currentThread().getId());

        // Apply @TestInfo metadata to both Extent and Allure if present
        TestInfo info = method.getAnnotation(TestInfo.class);
        if (info != null) {
            if (!info.module().isBlank())      ReportUtil.setModule(info.module());
            if (!info.owner().isBlank())       ReportUtil.setAuthor(info.owner());
            if (!info.tcId().isBlank())        ReportUtil.logKeyValue("TC-ID", info.tcId());
            if (!info.description().isBlank()) ReportUtil.setDescription(info.description());
            ReportUtil.setSeverity(
                io.qameta.allure.SeverityLevel.valueOf(info.severity().toUpperCase()));
        }

        System.out.printf("===== START : %s.%s [%s] =====%n",
                method.getDeclaringClass().getSimpleName(), method.getName(), browser.toUpperCase());

        launchBrowserWithRetry(browser);

        if (!baseUrl.isBlank()) {
            navigateSilently(baseUrl);
        }
    }

    @AfterMethod(alwaysRun = true)
    public void testTeardown(ITestResult result) {
        try {
            String screenshotPath = null;
            Page pg = PlaywrightFactory.getPage();

            if (result.getStatus() == ITestResult.FAILURE && pg != null && !pg.isClosed()) {
                screenshotPath = captureFailureScreenshot(result.getName());
            }

            updateReport(result, screenshotPath);

            System.out.printf("===== END : %s [%s] =====%n",
                    result.getName(), statusLabel(result.getStatus()));

        } catch (Exception e) {
            System.err.println("[BaseTest] @AfterMethod error: " + e.getMessage());
        } finally {
            PlaywrightFactory.closeBrowser();
        }
    }

    // ─── Protected utilities for test subclasses ──────────────────────────────

    protected Page getPage() {
        return PlaywrightFactory.getPage();
    }

    protected void navigateTo(String url) {
        navigateSilently(url);
    }

    protected void log(Status status, String message) {
        ExtentTest test = ExtentManager.getTest();
        if (test != null) test.log(status, message);
    }

    protected void logPass(String msg)    { log(Status.PASS,    msg); }
    protected void logFail(String msg)    { log(Status.FAIL,    msg); }
    protected void logInfo(String msg)    { log(Status.INFO,    msg); }
    protected void logWarning(String msg) { log(Status.WARNING, msg); }

    // ─── Private helpers ──────────────────────────────────────────────────────

    private void launchBrowserWithRetry(String browserName) {
        try {
            PlaywrightFactory.initializeBrowser(browserName);
        } catch (Exception firstAttempt) {
            System.err.println("[BaseTest] First browser launch failed: "
                    + firstAttempt.getMessage() + " — retrying once...");
            PlaywrightFactory.closeBrowser();
            try {
                PlaywrightFactory.initializeBrowser(browserName);
            } catch (Exception retryFailure) {
                log(Status.FAIL, "Browser launch failed after retry: " + retryFailure.getMessage());
                throw new RuntimeException("[BaseTest] Browser launch failed: " + browserName, retryFailure);
            }
        }
    }

    private void navigateSilently(String url) {
        try {
            PlaywrightFactory.getPage().navigate(url,
                    new Page.NavigateOptions().setTimeout(FrameworkConstants.TIMEOUT_NAVIGATION));
            PlaywrightFactory.getPage().waitForLoadState(LoadState.DOMCONTENTLOADED);
            log(Status.INFO, "Opened: " + url);
        } catch (Exception e) {
            log(Status.WARNING, "Navigation issue — " + url + ": " + e.getMessage());
        }
    }

    private String captureFailureScreenshot(String testName) {
        try {
            new File(FrameworkConstants.SCREENSHOTS_DIR).mkdirs();
            String ts   = new SimpleDateFormat("dd_MMM_yyyy_HH_mm_ss").format(new Date());
            String path = FrameworkConstants.SCREENSHOTS_DIR + testName + "_FAIL_" + ts + ".png";
            PlaywrightFactory.getPage().screenshot(
                    new Page.ScreenshotOptions().setPath(Paths.get(path)).setFullPage(false));
            return path;
        } catch (Exception e) {
            System.err.println("[BaseTest] Screenshot failed: " + e.getMessage());
            return null;
        }
    }

    private void updateReport(ITestResult result, String screenshotPath) {
        ExtentTest test = ExtentManager.getTest();
        if (test == null) return;

        long durationMs = result.getEndMillis() - result.getStartMillis();

        switch (result.getStatus()) {
            case ITestResult.SUCCESS:
                test.log(Status.PASS, "PASSED in " + durationMs + "ms");
                break;
            case ITestResult.FAILURE:
                test.log(Status.FAIL, "FAILED in " + durationMs + "ms");
                if (result.getThrowable() != null) test.fail(result.getThrowable());
                if (screenshotPath != null) {
                    try {
                        test.addScreenCaptureFromPath(
                                "../../SnapShots/" + new File(screenshotPath).getName());
                    } catch (Exception ignored) {}
                }
                break;
            case ITestResult.SKIP:
                test.log(Status.SKIP, "SKIPPED — " + result.getName());
                break;
        }
    }

    private static String statusLabel(int status) {
        switch (status) {
            case ITestResult.SUCCESS: return "PASSED";
            case ITestResult.FAILURE: return "FAILED";
            case ITestResult.SKIP:    return "SKIPPED";
            default:                  return "UNKNOWN";
        }
    }
}
