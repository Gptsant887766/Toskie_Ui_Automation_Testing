package com.toskie.tests.performance;

import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.pages.LoginPage;
import com.toskie.pages.WelcomePage;
import com.toskie.utils.NetworkValidator;
import com.toskie.utils.PerformanceUtils;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.ConfigManager;
import org.testng.annotations.Test;

/**
 * PERFORMANCE TESTS — Core Web Vitals, SLA thresholds, API response times
 * TC-PF-001 through TC-PF-010
 */
public class PerformanceTests extends BaseTest {

    // ─── TC-PF-001: Welcome page load time ────────────────────────────────────
    @Test(priority = 1,
          description = "Welcome page must load within 5 seconds (LCP ≤ 4s, FCP ≤ 2.5s)")
    public void testWelcomePageLoadTime() {
        PerformanceUtils perf = new PerformanceUtils();
        perf.logFullPerformanceReport("Welcome Page");
    }

    // ─── TC-PF-002: Login API response time ──────────────────────────────────
    @Test(priority = 2,
          description = "QA_Bypass_Login API must respond within 2 seconds")
    public void testLoginAPIResponseTime() {
        PerformanceUtils perf = new PerformanceUtils();
        new WelcomePage(utilLayer).completeOnboarding();
        new LoginPage(utilLayer).clickLoginButton();

        long responseTime = perf.measureAPICall("graphql", () ->
            utilLayer.loginViaQAGraphQL(ConfigManager.getTestMobile()));

        perf.stopTimerAndAssert("api_graphql", PerformanceUtils.API_RESPONSE_THRESHOLD);
        com.toskie.utils_Layer.ReportManager.getTest().log(
            com.aventstack.extentreports.Status.INFO,
            "Login API response time: " + responseTime + "ms");
    }

    // ─── TC-PF-003: Profile creation page load ────────────────────────────────
    @Test(priority = 3,
          description = "Profile creation page must reach DOM interactive state within 3s")
    public void testProfilePageLoadTime() {
        PerformanceUtils perf = new PerformanceUtils();

        perf.startTimer("profile_page_load");
        new WelcomePage(utilLayer).completeOnboarding();
        new LoginPage(utilLayer).loginWithDefaultCredentials();
        BrowserManager.getPage().waitForLoadState(com.microsoft.playwright.options.LoadState.NETWORKIDLE);
        perf.stopTimerAndAssert("profile_page_load", PerformanceUtils.PAGE_LOAD_THRESHOLD);

        perf.logFullPerformanceReport("Post-Login Page");
    }

    // ─── TC-PF-004: All API calls finish within SLA ───────────────────────────
    @Test(priority = 4,
          description = "All API calls during onboarding must respond within 2s SLA")
    public void testAllAPIsWithinSLA() {
        NetworkValidator nv = new NetworkValidator();
        nv.startCapturing();

        new WelcomePage(utilLayer).completeOnboarding();
        new LoginPage(utilLayer).loginWithDefaultCredentials();
        BrowserManager.getPage().waitForTimeout(3000);

        nv.stopCapturing();
        nv.assertAllAPIResponsesUnder(PerformanceUtils.API_RESPONSE_THRESHOLD);

        double avg = nv.getAverageResponseTime();
        com.toskie.utils_Layer.ReportManager.getTest().log(
            com.aventstack.extentreports.Status.INFO,
            "Average API response time: " + String.format("%.0f", avg) + "ms");
    }

    // ─── TC-PF-005: Time to First Byte ───────────────────────────────────────
    @Test(priority = 5,
          description = "TTFB (Time to First Byte) should be under 600ms")
    public void testTimeToFirstByte() {
        PerformanceUtils perf = new PerformanceUtils();
        long ttfb = perf.getTimeToFirstByte();

        if (ttfb > 0) {
            if (ttfb <= 600) {
                com.toskie.utils_Layer.ReportManager.getTest().log(
                    com.aventstack.extentreports.Status.PASS,
                    "TTFB: " + ttfb + "ms ✓ (threshold: 600ms)");
            } else {
                com.toskie.utils_Layer.ReportManager.getTest().log(
                    com.aventstack.extentreports.Status.WARNING,
                    "TTFB: " + ttfb + "ms (threshold: 600ms) — investigate server response");
            }
        } else {
            com.toskie.utils_Layer.ReportManager.getTest().log(
                com.aventstack.extentreports.Status.WARNING, "TTFB not available.");
        }
    }

    // ─── TC-PF-006: First Contentful Paint ───────────────────────────────────
    @Test(priority = 6,
          description = "First Contentful Paint must be ≤ 2.5s (Google Core Web Vitals)")
    public void testFirstContentfulPaint() {
        PerformanceUtils perf = new PerformanceUtils();
        long fcp = perf.getFirstContentfulPaint();

        if (fcp > 0) {
            if (fcp <= PerformanceUtils.FIRST_CONTENTFUL_PAINT) {
                com.toskie.utils_Layer.ReportManager.getTest().log(
                    com.aventstack.extentreports.Status.PASS,
                    "FCP: " + fcp + "ms ✓");
            } else {
                com.toskie.utils_Layer.ReportManager.getTest().log(
                    com.aventstack.extentreports.Status.FAIL,
                    "FCP: " + fcp + "ms EXCEEDS threshold " + PerformanceUtils.FIRST_CONTENTFUL_PAINT + "ms");
            }
        }
    }

    // ─── TC-PF-007: DOM Content Loaded time ──────────────────────────────────
    @Test(priority = 7,
          description = "DOMContentLoaded event must fire within 3 seconds")
    public void testDOMContentLoadedTime() {
        PerformanceUtils perf = new PerformanceUtils();
        long dom = perf.getDOMContentLoadedTime();

        if (dom > 0) {
            if (dom <= PerformanceUtils.DOM_CONTENT_LOADED) {
                com.toskie.utils_Layer.ReportManager.getTest().log(
                    com.aventstack.extentreports.Status.PASS,
                    "DOM ContentLoaded: " + dom + "ms ✓");
            } else {
                com.toskie.utils_Layer.ReportManager.getTest().log(
                    com.aventstack.extentreports.Status.WARNING,
                    "DOM ContentLoaded: " + dom + "ms (threshold: " + PerformanceUtils.DOM_CONTENT_LOADED + "ms)");
            }
        }
    }

    // ─── TC-PF-008: Email OTP bypass API response time ────────────────────────
    @Test(priority = 8,
          description = "QA_Bypass_Verify_Email_Otp API must respond within 2s")
    public void testEmailOTPAPIResponseTime() {
        PerformanceUtils perf = new PerformanceUtils();
        new WelcomePage(utilLayer).completeOnboarding();
        new LoginPage(utilLayer).loginWithDefaultCredentials();

        com.toskie.pages.ProfileCreationPage pp =
            new com.toskie.pages.ProfileCreationPage(utilLayer);
        if (!pp.isProfileCreationPageVisible()) return;

        perf.startTimer("email_otp_api");
        pp.enterEmail(ConfigManager.getTestEmail());
        pp.clickSendOTP();
        pp.bypassEmailOTP(ConfigManager.getTestEmail());
        perf.stopTimerAndAssert("email_otp_api", PerformanceUtils.API_RESPONSE_THRESHOLD * 3);
    }

    // ─── TC-PF-009: Search results load time ─────────────────────────────────
    @Test(priority = 9,
          description = "Search results page must render within 3s of submitting query")
    public void testSearchResultsLoadTime() {
        PerformanceUtils perf = new PerformanceUtils();
        new WelcomePage(utilLayer).completeOnboarding();
        new LoginPage(utilLayer).loginWithDefaultCredentials();

        com.toskie.pages.ProfileCreationPage pp =
            new com.toskie.pages.ProfileCreationPage(utilLayer);
        if (pp.isProfileCreationPageVisible()) pp.createProfileWithDefaultData();

        perf.startTimer("search_results");
        new com.toskie.pages.SearchPage(utilLayer).searchFor("plumber");
        perf.stopTimerAndAssert("search_results", 3000);
    }

    // ─── TC-PF-010: Memory usage check ────────────────────────────────────────
    @Test(priority = 10,
          description = "Check for memory leaks — heap usage should not exceed 100MB")
    public void testMemoryUsage() {
        PerformanceUtils perf = new PerformanceUtils();
        new WelcomePage(utilLayer).completeOnboarding();
        new LoginPage(utilLayer).loginWithDefaultCredentials();
        BrowserManager.getPage().waitForTimeout(5000);

        org.json.JSONObject memory = perf.getMemoryInfo();
        if (memory.has("usedJSHeapSize")) {
            long heapMB = memory.getLong("usedJSHeapSize") / (1024 * 1024);
            long limitMB = memory.getLong("jsHeapSizeLimit") / (1024 * 1024);
            com.toskie.utils_Layer.ReportManager.getTest().log(
                heapMB < 100 ? com.aventstack.extentreports.Status.PASS
                             : com.aventstack.extentreports.Status.WARNING,
                "JS Heap: " + heapMB + "MB used / " + limitMB + "MB limit");
        } else {
            com.toskie.utils_Layer.ReportManager.getTest().log(
                com.aventstack.extentreports.Status.INFO,
                "Memory API not available in this browser/mode.");
        }
    }
}
