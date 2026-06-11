package com.toskie.BaseTest_Layer;

import java.lang.reflect.Method;

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

    // Read from config.properties — still overridable via -Dbrowser=firefox
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
    public void setUp(Method method) {
        utilLayer = UtilLayer.getInstance();
        utilLayer.markTestStart();
        System.out.println("===== TEST START : " + method.getName() + " =====");

        utilLayer.resetOnlyLogs();
        utilLayer.createTest(method.getName());
        utilLayer.launchBrowser(browser);

        utilLayer.openUrl(baseUrl);
        WaitManager.waitForPageLoad(LoadState.DOMCONTENTLOADED);

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
            System.err.println("[BaseTest] REPORT SKIPPED — utilLayer is null. Check @BeforeSuite for errors.");
        }
        System.out.println("===== FINAL REPORT END =====");
    }
}
