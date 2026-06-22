package com.toskie.BaseTest_Layer;

import java.lang.reflect.Method;

import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import com.aventstack.extentreports.Status;
import com.microsoft.playwright.options.LoadState;
import com.toskie.utils_Layer.ConfigManager;
import com.toskie.utils_Layer.ReportManager;
import com.toskie.utils_Layer.UtilLayer;
import com.toskie.utils_Layer.WaitManager;

public class BaseTest {

    protected UtilLayer<?> utilLayer;

    // Read from config.properties -- still overridable via -Dbrowser=firefox
    protected String browser = System.getProperty("browser", ConfigManager.getBrowser());
    protected String baseUrl  = System.getProperty("baseUrl",  ConfigManager.getBaseUrl());

    // ─── Suite setup ─────────────────────────────────────────────────────────
    @BeforeSuite(alwaysRun = true)
    public synchronized void beforeSuite() {
        utilLayer = UtilLayer.getInstance();
        utilLayer.resetSuiteData();
        ReportManager.createExtentReport();
        utilLayer.markSuiteStart();
        System.out.println("===== REPORT STARTED =====");
    }

    // ─── Per-test setup ───────────────────────────────────────────────────────
    @BeforeMethod(alwaysRun = true)
    public void setUp(Method method, ITestContext context) {
        // Allow <parameter name="browser" value="firefox"/> in suite XML to override config/system-prop
        String xmlBrowser = context.getCurrentXmlTest().getParameter("browser");
        if (xmlBrowser != null && !xmlBrowser.isEmpty()) {
            browser = xmlBrowser;
        }

        utilLayer = UtilLayer.getInstance();
        utilLayer.markTestStart();
        System.out.println("===== TEST START : " + method.getName() + " [" + browser + "] =====");

        utilLayer.resetOnlyLogs();
        utilLayer.createTest(method.getName());

        // Tag each test with its suite name and module block so ExtentReport groups by suite
        String suiteName = context.getSuite().getName();
        String moduleName = context.getCurrentXmlTest().getName();
        ReportManager.getTest().assignCategory(suiteName);
        if (moduleName != null && !moduleName.equals(suiteName)) {
            ReportManager.getTest().assignCategory(moduleName);
        }

        // Retry up to 3 times with delay — handles transient DNS / network errors
        // (ERR_NAME_NOT_RESOLVED, ERR_NETWORK_CHANGED, ERR_CONNECTION_RESET)
        int maxSetupAttempts = 3;
        Exception lastSetupError = null;
        for (int attempt = 1; attempt <= maxSetupAttempts; attempt++) {
            try {
                utilLayer.launchBrowser(browser);
                utilLayer.openUrl(baseUrl);
                WaitManager.waitForPageLoad(LoadState.DOMCONTENTLOADED);
                lastSetupError = null;
                break;
            } catch (Exception e) {
                lastSetupError = e;
                System.out.printf("[BaseTest] @BeforeMethod attempt %d/%d failed: %s%n",
                        attempt, maxSetupAttempts, e.getMessage());
                ReportManager.getTest().log(Status.WARNING,
                        "Browser setup attempt " + attempt + "/" + maxSetupAttempts + " failed: " + e.getMessage());
                try { utilLayer.tearDown(); } catch (Exception ignored) {}
                if (attempt < maxSetupAttempts) {
                    // Wait 3 seconds to let DNS / network recover before next attempt
                    try { Thread.sleep(3000); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
                }
            }
        }
        if (lastSetupError != null) {
            throw new RuntimeException("Browser setup failed after " + maxSetupAttempts + " attempts", lastSetupError);
        }

        ReportManager.getTest().log(Status.INFO, "Application opened: " + baseUrl);
        System.out.println("Application opened: " + baseUrl);
    }

    // ─── Per-test teardown ────────────────────────────────────────────────────
    @AfterMethod(alwaysRun = true)
    public void tearDownMethod(ITestResult result) {
        try {
            String screenshotPath = utilLayer.captureScreenshot(result.getName(), result);
            utilLayer.updateTestResult(result, screenshotPath);

            switch (result.getStatus()) {
                case ITestResult.SUCCESS:
                    ReportManager.getTest().log(Status.PASS, "TEST PASSED: " + result.getName());
                    System.out.println("PASSED: " + result.getName());
                    break;
                case ITestResult.FAILURE:
                    ReportManager.getTest().log(Status.FAIL, "TEST FAILED: " + result.getName());
                    ReportManager.getTest().fail(result.getThrowable());
                    System.out.println("FAILED: " + result.getName());
                    break;
                case ITestResult.SKIP:
                    ReportManager.getTest().log(Status.SKIP, "TEST SKIPPED: " + result.getName());
                    System.out.println("SKIPPED: " + result.getName());
                    break;
            }
        } catch (Exception e) {
            System.out.println("AfterMethod issue: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (utilLayer != null) utilLayer.tearDown();
        }
    }

    // ─── Suite teardown ───────────────────────────────────────────────────────
    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
        System.out.println("===== FINAL REPORT START =====");
        if (utilLayer != null) {
            utilLayer.generateFinalReports();
        } else {
            System.err.println("[BaseTest] REPORT SKIPPED -- utilLayer is null. Check @BeforeSuite for errors.");
        }
        System.out.println("===== FINAL REPORT END =====");
    }
}
