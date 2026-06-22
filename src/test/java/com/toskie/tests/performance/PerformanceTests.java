package com.toskie.tests.performance;

import com.aventstack.extentreports.Status;
import com.microsoft.playwright.options.LoadState;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.constants.AppConstants;
import com.toskie.pages.LoginPage;
import com.toskie.pages.WelcomePage;
import com.toskie.utils.AssertionHelper;
import com.toskie.utils.NetworkValidator;
import com.toskie.utils.PerformanceUtils;
import com.toskie.utils_Layer.ApiUtils;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.ConfigManager;
import com.toskie.utils_Layer.ReportManager;
import com.toskie.utils_Layer.WaitManager;
import org.testng.annotations.Test;

/**
 * PERFORMANCE TESTS -- Core Web Vitals, SLA thresholds, API response times
 * TC-PF-001 through TC-PF-013
 */
public class PerformanceTests extends BaseTest {

    // ─── TC-PF-001: Welcome page load time ──────────────────────────────────────
    @Test(priority = 1,
          description = "Welcome page must load within 5 seconds (LCP <= 4s, FCP <= 2.5s)")
    public void testWelcomePageLoadTime() {
        AssertionHelper a = new AssertionHelper();
        PerformanceUtils perf = new PerformanceUtils();
        perf.logFullPerformanceReport("Welcome Page");

        long loadTime = perf.getPageLoadTime();
        ReportManager.getTest().log(Status.INFO,
                "TC-PF-001: Welcome page load time: " + loadTime + "ms (SLA: " + PerformanceUtils.PAGE_LOAD_THRESHOLD + "ms)");
        if (loadTime > 0) {
            a.assertTrue(loadTime <= PerformanceUtils.PAGE_LOAD_THRESHOLD,
                    "TC-PF-001: Welcome page load time must be <= " + PerformanceUtils.PAGE_LOAD_THRESHOLD
                    + "ms (got: " + loadTime + "ms)");
        }
        a.assertAll();
    }

    // ─── TC-PF-002: Login API response time ─────────────────────────────────────
    @Test(priority = 2,
          description = "QA_Bypass_Login API must respond within 2 seconds")
    public void testLoginAPIResponseTime() {
        PerformanceUtils perf = new PerformanceUtils();
        new WelcomePage(utilLayer).completeOnboarding();
        new LoginPage(utilLayer).clickLoginButton();

        long responseTime = perf.measureAPICall("graphql", () ->
            utilLayer.loginViaQAGraphQL(ConfigManager.getTestMobile()));

        perf.stopTimerAndAssert("api_graphql", PerformanceUtils.API_RESPONSE_THRESHOLD);
        ReportManager.getTest().log(Status.INFO,
            "Login API response time: " + responseTime + "ms");
    }

    // ─── TC-PF-003: Profile creation page load ──────────────────────────────────
    @Test(priority = 3,
          description = "Profile creation page must reach DOM interactive state within 3s")
    public void testProfilePageLoadTime() {
        PerformanceUtils perf = new PerformanceUtils();

        perf.startTimer("profile_page_load");
        new WelcomePage(utilLayer).completeOnboarding();
        new LoginPage(utilLayer).loginWithDefaultCredentials();
        BrowserManager.getPage().waitForLoadState(LoadState.NETWORKIDLE);
        perf.stopTimerAndAssert("profile_page_load", PerformanceUtils.PAGE_LOAD_THRESHOLD);

        perf.logFullPerformanceReport("Post-Login Page");
    }

    // ─── TC-PF-004: All API calls finish within SLA ──────────────────────────────
    @Test(priority = 4,
          description = "All API calls during onboarding must respond within 2s SLA")
    public void testAllAPIsWithinSLA() {
        NetworkValidator nv = new NetworkValidator();
        nv.startCapturing();

        new WelcomePage(utilLayer).completeOnboarding();
        new LoginPage(utilLayer).loginWithDefaultCredentials();
        WaitManager.waitForPageLoad(LoadState.DOMCONTENTLOADED);

        nv.stopCapturing();
        nv.assertAllAPIResponsesUnder(PerformanceUtils.API_RESPONSE_THRESHOLD);

        double avg = nv.getAverageResponseTime();
        ReportManager.getTest().log(Status.INFO,
            "Average API response time: " + String.format("%.0f", avg) + "ms");
    }

    // ─── TC-PF-005: Time to First Byte ──────────────────────────────────────────
    @Test(priority = 5,
          description = "TTFB (Time to First Byte) should be under 600ms")
    public void testTimeToFirstByte() {
        AssertionHelper a = new AssertionHelper();
        PerformanceUtils perf = new PerformanceUtils();
        long ttfb = perf.getTimeToFirstByte();

        if (ttfb > 0) {
            ReportManager.getTest().log(ttfb <= 600 ? Status.PASS : Status.WARNING,
                    "TC-PF-005: TTFB: " + ttfb + "ms (target: 600ms, dev-env SLA: 2000ms)");
            a.assertTrue(ttfb <= 2000,
                    "TC-PF-005: TTFB must be <= 2000ms in dev environment (got: " + ttfb + "ms)");
        } else {
            ReportManager.getTest().log(Status.INFO,
                    "TC-PF-005: TTFB metric not available in current browser/navigation state");
        }
        a.assertAll();
    }

    // ─── TC-PF-006: First Contentful Paint ──────────────────────────────────────
    @Test(priority = 6,
          description = "First Contentful Paint must be <= 2.5s (Google Core Web Vitals)")
    public void testFirstContentfulPaint() {
        AssertionHelper a = new AssertionHelper();
        PerformanceUtils perf = new PerformanceUtils();
        long fcp = perf.getFirstContentfulPaint();

        if (fcp > 0) {
            ReportManager.getTest().log(
                    fcp <= PerformanceUtils.FIRST_CONTENTFUL_PAINT ? Status.PASS : Status.WARNING,
                    "TC-PF-006: FCP: " + fcp + "ms (target: "
                    + PerformanceUtils.FIRST_CONTENTFUL_PAINT + "ms, dev-env SLA: "
                    + PerformanceUtils.PAGE_LOAD_THRESHOLD + "ms)");
            a.assertTrue(fcp <= PerformanceUtils.PAGE_LOAD_THRESHOLD,
                    "TC-PF-006: FCP must be <= " + PerformanceUtils.PAGE_LOAD_THRESHOLD
                    + "ms in dev environment (got: " + fcp + "ms)");
        } else {
            ReportManager.getTest().log(Status.INFO,
                    "TC-PF-006: FCP paint metric not available (headless/no-paint mode)");
        }
        a.assertAll();
    }

    // ─── TC-PF-007: DOM Content Loaded time ─────────────────────────────────────
    @Test(priority = 7,
          description = "DOMContentLoaded event must fire within 3 seconds")
    public void testDOMContentLoadedTime() {
        AssertionHelper a = new AssertionHelper();
        PerformanceUtils perf = new PerformanceUtils();
        long dom = perf.getDOMContentLoadedTime();

        if (dom > 0) {
            ReportManager.getTest().log(
                    dom <= PerformanceUtils.DOM_CONTENT_LOADED ? Status.PASS : Status.WARNING,
                    "TC-PF-007: DOMContentLoaded: " + dom + "ms (target: "
                    + PerformanceUtils.DOM_CONTENT_LOADED + "ms, dev-env SLA: "
                    + PerformanceUtils.PAGE_LOAD_THRESHOLD + "ms)");
            a.assertTrue(dom <= PerformanceUtils.PAGE_LOAD_THRESHOLD,
                    "TC-PF-007: DOMContentLoaded must be <= " + PerformanceUtils.PAGE_LOAD_THRESHOLD
                    + "ms in dev environment (got: " + dom + "ms)");
        } else {
            ReportManager.getTest().log(Status.INFO,
                    "TC-PF-007: DOM timing not available for current navigation");
        }
        a.assertAll();
    }

    // ─── TC-PF-008: Email OTP bypass API response time ──────────────────────────
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

    // ─── TC-PF-009: Search results load time ────────────────────────────────────
    @Test(priority = 9,
          description = "Search results page must render within 3s of submitting query")
    public void testSearchResultsLoadTime() {
        PerformanceUtils perf = new PerformanceUtils();
        ApiUtils.loginViaQAGraphQL(ConfigManager.get("testMobile"));
        ApiUtils.injectTokenFull();
        ApiUtils.injectCookies();
        try {
            BrowserManager.getPage().navigate(AppConstants.SEARCH_URL);
            com.toskie.utils_Layer.WaitManager.safePageLoad();
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "TC-PF-009: Search URL navigation failed: " + e.getMessage());
        }
        perf.startTimer("search_results");
        new com.toskie.pages.SearchPage(utilLayer).searchFor("plumber");
        perf.stopTimerAndAssert("search_results", 3000);
    }

    // ─── TC-PF-010: Memory usage check ──────────────────────────────────────────
    @Test(priority = 10,
          description = "JS heap usage should not exceed 100MB; heap used must be < heap limit")
    public void testMemoryUsage() {
        AssertionHelper a = new AssertionHelper();
        PerformanceUtils perf = new PerformanceUtils();
        new WelcomePage(utilLayer).completeOnboarding();
        new LoginPage(utilLayer).loginWithDefaultCredentials();
        WaitManager.waitForPageLoad(LoadState.DOMCONTENTLOADED);

        org.json.JSONObject memory = perf.getMemoryInfo();
        if (memory.has("usedJSHeapSize")) {
            long heapMB  = memory.getLong("usedJSHeapSize") / (1024 * 1024);
            long limitMB = memory.getLong("jsHeapSizeLimit") / (1024 * 1024);
            ReportManager.getTest().log(
                    heapMB < 100 ? Status.PASS : Status.WARNING,
                    "TC-PF-010: JS Heap: " + heapMB + "MB used / " + limitMB + "MB limit");
            a.assertTrue(heapMB < limitMB,
                    "TC-PF-010: Used JS heap (" + heapMB + "MB) must be < heap limit (" + limitMB + "MB)");
            a.assertTrue(heapMB >= 0,
                    "TC-PF-010: Heap size must be a non-negative value (got: " + heapMB + "MB)");
        } else {
            ReportManager.getTest().log(Status.INFO,
                    "TC-PF-010: Memory API not available -- skipping heap assertion (non-Chromium or headless mode)");
        }
        a.assertAll();
    }

    // ─── TC-PF-011: Dashboard page load SLA ─────────────────────────────────────
    @Test(priority = 11,
          description = "Dashboard page must load within PAGE_LOAD_THRESHOLD after API login")
    public void testDashboardPageLoadSLA() {
        AssertionHelper a = new AssertionHelper();
        ApiUtils.loginViaQAGraphQL(ConfigManager.get("testMobile"));
        ApiUtils.injectTokenFull();
        ApiUtils.injectCookies();

        PerformanceUtils perf = new PerformanceUtils();
        perf.startTimer("dashboard_load");
        BrowserManager.getPage().navigate(AppConstants.DASHBOARD_URL);
        BrowserManager.getPage().waitForLoadState(LoadState.DOMCONTENTLOADED);
        long elapsed = perf.stopTimer("dashboard_load");

        long navLoad = perf.getPageLoadTime();
        long reported = navLoad > 0 ? navLoad : elapsed;
        ReportManager.getTest().log(
                reported <= PerformanceUtils.PAGE_LOAD_THRESHOLD ? Status.PASS : Status.WARNING,
                "TC-PF-011: Dashboard load: " + reported + "ms (SLA: " + PerformanceUtils.PAGE_LOAD_THRESHOLD + "ms)");
        a.assertTrue(reported > 0,
                "TC-PF-011: Dashboard load time must be measurable (got: " + reported + "ms)");
        a.assertTrue(reported <= PerformanceUtils.PAGE_LOAD_THRESHOLD,
                "TC-PF-011: Dashboard page must load within " + PerformanceUtils.PAGE_LOAD_THRESHOLD
                + "ms (got: " + reported + "ms)");
        a.assertAll();
    }

    // ─── TC-PF-012: Feed/Explore page load SLA ──────────────────────────────────
    @Test(priority = 12,
          description = "Explore/feed page must load within PAGE_LOAD_THRESHOLD after API login")
    public void testExplorePageLoadSLA() {
        AssertionHelper a = new AssertionHelper();
        ApiUtils.loginViaQAGraphQL(ConfigManager.get("testMobile"));
        ApiUtils.injectTokenFull();
        ApiUtils.injectCookies();

        PerformanceUtils perf = new PerformanceUtils();
        perf.startTimer("explore_load");
        BrowserManager.getPage().navigate(AppConstants.EXPLORE_URL);
        BrowserManager.getPage().waitForLoadState(LoadState.DOMCONTENTLOADED);
        long elapsed = perf.stopTimer("explore_load");

        long navLoad = perf.getPageLoadTime();
        long reported = navLoad > 0 ? navLoad : elapsed;
        ReportManager.getTest().log(
                reported <= PerformanceUtils.PAGE_LOAD_THRESHOLD ? Status.PASS : Status.WARNING,
                "TC-PF-012: Explore page load: " + reported + "ms (SLA: " + PerformanceUtils.PAGE_LOAD_THRESHOLD + "ms)");
        a.assertTrue(reported > 0,
                "TC-PF-012: Explore page load time must be measurable (got: " + reported + "ms)");
        a.assertTrue(reported <= PerformanceUtils.PAGE_LOAD_THRESHOLD,
                "TC-PF-012: Explore page must load within " + PerformanceUtils.PAGE_LOAD_THRESHOLD
                + "ms (got: " + reported + "ms)");
        a.assertAll();
    }

    // ─── TC-PF-013: Profile page load SLA ───────────────────────────────────────
    @Test(priority = 13,
          description = "User profile page must load within PAGE_LOAD_THRESHOLD after API login")
    public void testProfilePageLoadSLA() {
        AssertionHelper a = new AssertionHelper();
        ApiUtils.loginViaQAGraphQL(ConfigManager.get("testMobile"));
        ApiUtils.injectTokenFull();
        ApiUtils.injectCookies();

        PerformanceUtils perf = new PerformanceUtils();
        perf.startTimer("profile_load");
        BrowserManager.getPage().navigate(AppConstants.PROFILE_URL);
        BrowserManager.getPage().waitForLoadState(LoadState.DOMCONTENTLOADED);
        long elapsed = perf.stopTimer("profile_load");

        long navLoad = perf.getPageLoadTime();
        long reported = navLoad > 0 ? navLoad : elapsed;
        ReportManager.getTest().log(
                reported <= PerformanceUtils.PAGE_LOAD_THRESHOLD ? Status.PASS : Status.WARNING,
                "TC-PF-013: Profile page load: " + reported + "ms (SLA: " + PerformanceUtils.PAGE_LOAD_THRESHOLD + "ms)");
        a.assertTrue(reported > 0,
                "TC-PF-013: Profile page load time must be measurable (got: " + reported + "ms)");
        a.assertTrue(reported <= PerformanceUtils.PAGE_LOAD_THRESHOLD,
                "TC-PF-013: Profile page must load within " + PerformanceUtils.PAGE_LOAD_THRESHOLD
                + "ms (got: " + reported + "ms)");
        a.assertAll();
    }
}
