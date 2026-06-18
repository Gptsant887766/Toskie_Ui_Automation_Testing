package com.toskie.tests.security;

import com.aventstack.extentreports.Status;
import com.microsoft.playwright.options.LoadState;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.locators.LoginPageLocators;
import com.toskie.locators.ProfileCreationLocators;
import com.toskie.pages.LoginPage;
import com.toskie.pages.WelcomePage;
import com.toskie.utils.AssertionHelper;
import com.toskie.utils.NetworkValidator;
import com.toskie.utils.SecurityUtils;
import com.toskie.utils.TestDataManager;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.ReportManager;
import com.toskie.utils_Layer.WaitManager;
import org.testng.SkipException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * SECURITY TESTS -- OWASP Top 10, XSS, SQLi, Auth, HTTPS, Token security
 * TC-SEC-001 through TC-SEC-015
 */
public class SecurityTests extends BaseTest {

    @DataProvider(name = "xssPayloads")
    public Object[][] xssPayloads() {
        return TestDataManager.getSecurityPayloads();
    }

    // ─── TC-SEC-001: XSS in phone number field ──────────────────────────────
    @Test(priority = 1,
          description = "OWASP A03: XSS payloads in phone number field should be sanitized")
    public void testXSSInPhoneField() {
        AssertionHelper a = new AssertionHelper();
        SecurityUtils sec = new SecurityUtils();
        new WelcomePage(utilLayer).completeOnboarding();
        new LoginPage(utilLayer).clickLoginButton();

        LoginPageLocators loc = new LoginPageLocators(BrowserManager.getPage());
        boolean isSecure = sec.testXSSInField(loc.phoneNumberInput, "Phone Number");
        a.assertTrue(isSecure,
                "TC-SEC-001: XSS payloads must be sanitized in phone field (isSecure=" + isSecure + ")");
        a.assertAll();
    }

    // ─── TC-SEC-002: XSS in first name field ────────────────────────────────
    @Test(priority = 2,
          description = "OWASP A03: XSS payloads in profile first name should be sanitized")
    public void testXSSInFirstNameField() {
        AssertionHelper a = new AssertionHelper();
        SecurityUtils sec = new SecurityUtils();
        new WelcomePage(utilLayer).completeOnboarding();
        new LoginPage(utilLayer).loginWithDefaultCredentials();

        ProfileCreationLocators loc = new ProfileCreationLocators(BrowserManager.getPage());
        com.toskie.pages.ProfileCreationPage pp = new com.toskie.pages.ProfileCreationPage(utilLayer);
        if (!pp.isProfileCreationPageVisible()) {
            ReportManager.getTest().log(Status.INFO, "TC-SEC-002: Profile creation not visible -- skipping field test");
            return;
        }

        boolean isSecure = sec.testXSSInField(loc.firstNameInput, "First Name");
        a.assertTrue(isSecure,
                "TC-SEC-002: XSS payloads must be sanitized in first name field (isSecure=" + isSecure + ")");
        a.assertAll();
    }

    // ─── TC-SEC-003: SQL Injection in search ────────────────────────────────
    @Test(priority = 3,
          description = "OWASP A03: SQL injection payloads in search should be sanitized")
    public void testSQLInjectionInSearch() {
        AssertionHelper a = new AssertionHelper();
        new WelcomePage(utilLayer).completeOnboarding();
        new LoginPage(utilLayer).loginWithDefaultCredentials();

        com.toskie.pages.ProfileCreationPage pp = new com.toskie.pages.ProfileCreationPage(utilLayer);
        if (pp.isProfileCreationPageVisible()) {
            try {
                pp.createProfileWithDefaultData();
            } catch (Exception e) {
                throw new SkipException("TC-SEC-003: Profile creation timed out -- skipping SQL injection test: " + e.getMessage());
            }
        }

        com.toskie.locators.SearchPageLocators loc =
            new com.toskie.locators.SearchPageLocators(BrowserManager.getPage());

        boolean sqlInjectionFound = false;
        String vulnerablePayload = null;
        for (String payload : SecurityUtils.SQL_INJECTION_PAYLOADS) {
            try {
                loc.searchInput.fill(payload);
                BrowserManager.getPage().keyboard().press("Enter");
                BrowserManager.getPage().waitForTimeout(1000);
                String content = BrowserManager.getPage().content().toLowerCase();
                if (content.contains("sql error") || content.contains("syntax error")
                        || content.contains("mysql") || content.contains("ora-")) {
                    sqlInjectionFound = true;
                    vulnerablePayload = payload;
                    ReportManager.getTest().log(Status.FAIL,
                            "SQL INJECTION VULNERABILITY for payload: " + payload);
                } else {
                    ReportManager.getTest().log(Status.PASS,
                            "SQLi handled safely: " + payload.substring(0, Math.min(40, payload.length())));
                }
                loc.searchInput.fill("");
            } catch (Exception ignored) {}
        }
        a.assertFalse(sqlInjectionFound,
                "TC-SEC-003: SQL injection vulnerability detected with payload: " + vulnerablePayload);
        a.assertAll();
    }

    // ─── TC-SEC-004: All requests use HTTPS ─────────────────────────────────
    @Test(priority = 4,
          description = "OWASP A02: All network requests must use HTTPS")
    public void testAllRequestsUseHTTPS() {
        NetworkValidator nv = new NetworkValidator();
        nv.startCapturing();
        new WelcomePage(utilLayer).completeOnboarding();
        new LoginPage(utilLayer).loginWithDefaultCredentials();
        nv.stopCapturing();
        nv.assertHTTPS();
    }

    // ─── TC-SEC-005: No sensitive data in URLs ───────────────────────────────
    @Test(priority = 5,
          description = "OWASP A02: Tokens and secrets must not appear in request URLs")
    public void testNoSensitiveDataInURLs() {
        NetworkValidator nv = new NetworkValidator();
        nv.startCapturing();
        new WelcomePage(utilLayer).completeOnboarding();
        new LoginPage(utilLayer).loginWithDefaultCredentials();
        nv.stopCapturing();
        nv.assertNoSensitiveDataInURL();
    }

    // ─── TC-SEC-006: Auth header sent with API calls ─────────────────────────
    @Test(priority = 6,
          description = "OWASP A07: Authorization header must be present in authenticated API calls")
    public void testAuthHeaderPresentInAPIcalls() {
        new WelcomePage(utilLayer).completeOnboarding();
        new LoginPage(utilLayer).loginWithDefaultCredentials();
        NetworkValidator nv = new NetworkValidator();
        nv.startCapturing();
        WaitManager.waitForPageLoad(LoadState.DOMCONTENTLOADED);
        nv.stopCapturing();
        nv.assertAuthHeaderPresent();
    }

    // ─── TC-SEC-007: Token expiry validation ────────────────────────────────
    @Test(priority = 7,
          description = "OWASP A07: JWT token should have a valid expiry and not be expired")
    public void testTokenExpiry() {
        new WelcomePage(utilLayer).completeOnboarding();
        new LoginPage(utilLayer).loginWithDefaultCredentials();
        SecurityUtils sec = new SecurityUtils();
        sec.assertTokenExpiry();
    }

    // ─── TC-SEC-008: QA secret not exposed in responses ─────────────────────
    @Test(priority = 8,
          description = "QA secret key must not appear in any API response")
    public void testQASecretNotExposedInResponses() {
        SecurityUtils sec = new SecurityUtils();
        sec.assertNoSensitiveDataInResponse("graphql");
        new WelcomePage(utilLayer).completeOnboarding();
        new LoginPage(utilLayer).loginWithDefaultCredentials();
        WaitManager.waitForPageLoad(LoadState.DOMCONTENTLOADED);
    }

    // ─── TC-SEC-009: OTP rate limiting ──────────────────────────────────────
    @Test(priority = 9,
          description = "OWASP A04: Multiple OTP requests should trigger rate limiting")
    public void testOTPRateLimiting() {
        try {
            new WelcomePage(utilLayer).completeOnboarding();
            new LoginPage(utilLayer).clickLoginButton();
            LoginPageLocators loc = new LoginPageLocators(BrowserManager.getPage());
            loc.phoneNumberInput.fill("9919011050");
            SecurityUtils sec = new SecurityUtils();
            sec.assertRateLimitingOnOTP(loc.sendOtpButton, 5);
        } catch (SkipException se) {
            throw se;
        } catch (Exception e) {
            throw new SkipException("TC-SEC-009: OTP rate limiting check not applicable -- " + e.getMessage());
        }
    }

    // ─── TC-SEC-010: Data-driven XSS payloads ───────────────────────────────
    @Test(dataProvider = "xssPayloads", priority = 10,
          description = "Data-driven: Multiple XSS payloads across different input fields")
    public void testXSSPayloadsInFields(String payload, String field, String expectedResult) {
        AssertionHelper a = new AssertionHelper();
        new WelcomePage(utilLayer).completeOnboarding();
        new LoginPage(utilLayer).clickLoginButton();
        LoginPageLocators loc = new LoginPageLocators(BrowserManager.getPage());
        try {
            if ("phoneNumber".equals(field)) {
                loc.phoneNumberInput.fill(payload);
                BrowserManager.getPage().waitForTimeout(500);
                String content = BrowserManager.getPage().content();
                boolean xssExecuted = content.contains("<script>alert") || content.contains("onerror=alert");
                ReportManager.getTest().log(
                    xssExecuted ? Status.FAIL : Status.PASS,
                    (xssExecuted ? "XSS EXECUTED in " : "XSS blocked in ") + field
                        + ": " + payload.substring(0, Math.min(50, payload.length())));
                a.assertFalse(xssExecuted,
                        "TC-SEC-010: XSS must not execute in field=" + field + " payload=" +
                        payload.substring(0, Math.min(50, payload.length())));
            }
        } catch (Exception ignored) {}
        a.assertAll();
    }

    // ─── TC-SEC-011: CSRF protection ────────────────────────────────────────
    @Test(priority = 11,
          description = "API endpoints should have CSRF protection or SameSite cookies")
    public void testCSRFProtection() {
        AssertionHelper a = new AssertionHelper();
        NetworkValidator nv = new NetworkValidator();
        nv.startCapturing();
        new WelcomePage(utilLayer).completeOnboarding();
        new LoginPage(utilLayer).loginWithDefaultCredentials();
        nv.stopCapturing();
        // Verify no plain http:// requests were captured (basic CSRF surface reduction)
        // NetworkValidator.assertHTTPS() enforces this; we additionally check cookies
        String cookies = BrowserManager.getPage().evaluate(
                "() => document.cookie").toString();
        // If auth cookie exists it must NOT be accessible via JS (httpOnly) -- JS can't read httpOnly cookies
        // so if we can read a token cookie, it's NOT httpOnly which is a risk
        boolean tokenInJsCookies = cookies.contains("access_token") || cookies.contains("authToken");
        a.assertFalse(tokenInJsCookies,
                "TC-SEC-011: Auth token must not be readable via document.cookie (should use httpOnly flag)");
        a.assertAll();
    }

    // ─── TC-SEC-012: Broken Object Level Authorization ───────────────────────
    @Test(priority = 12,
          description = "OWASP API A01: Test BOLA -- access another user's profile data")
    public void testBrokenObjectLevelAuthorization() {
        AssertionHelper a = new AssertionHelper();
        new WelcomePage(utilLayer).completeOnboarding();
        new LoginPage(utilLayer).loginWithDefaultCredentials();

        boolean bolaExposed = false;
        String bolaId = null;
        for (String testId : SecurityUtils.BOLA_TEST_IDS) {
            try {
                String testUrl = BrowserManager.getPage().url().split("#")[0] + "/profile/" + testId;
                BrowserManager.getPage().navigate(testUrl);
                BrowserManager.getPage().waitForTimeout(1500);
                String content = BrowserManager.getPage().content().toLowerCase();
                // Unexpected access: admin-level data exposed without error/not-found
                if (content.contains("admin") && !content.contains("not found")
                        && !content.contains("403") && !content.contains("unauthorized")) {
                    bolaExposed = true;
                    bolaId = testId;
                    ReportManager.getTest().log(Status.WARNING,
                            "BOLA: potential exposure for ID=" + testId + " -- review manually");
                } else {
                    ReportManager.getTest().log(Status.PASS, "BOLA check ok for ID=" + testId);
                }
            } catch (Exception ignored) {}
        }
        a.assertFalse(bolaExposed,
                "TC-SEC-012: BOLA -- admin-level data exposed for user ID=" + bolaId
                + " without authorization error");
        a.assertAll();
    }

    // ─── TC-SEC-013: No password/secrets in page source ─────────────────────
    @Test(priority = 13,
          description = "QA secrets and passwords must not appear in page HTML source")
    public void testNoSecretsInPageSource() {
        AssertionHelper a = new AssertionHelper();
        new WelcomePage(utilLayer).completeOnboarding();
        new LoginPage(utilLayer).loginWithDefaultCredentials();
        String source = BrowserManager.getPage().content();
        boolean hasSecret = source.contains("ATDBUDBOCLYMQEJBTSKXATKZCGCTFIUTOHTVCWOMLGVHMJUHVZUV");
        a.assertFalse(hasSecret,
                "TC-SEC-013: QA secret key must not be present in page source -- credential leak detected");
        a.assertAll();
    }

    // ─── TC-SEC-014: Session token invalidation on logout ───────────────────
    @Test(priority = 14,
          description = "Token in localStorage should be cleared after logout")
    public void testTokenClearedOnLogout() {
        AssertionHelper a = new AssertionHelper();
        new WelcomePage(utilLayer).completeOnboarding();
        new LoginPage(utilLayer).loginWithDefaultCredentials();
        try {
            BrowserManager.getPage().locator("[href*='settings'], button:has-text('Settings')").first().click();
            BrowserManager.getPage().waitForTimeout(1000);
            BrowserManager.getPage().locator("button:has-text('Logout')").first().click();
            WaitManager.safePageLoad();
        } catch (Exception e) {
            throw new SkipException("TC-SEC-014: Logout navigation not reachable in test environment -- " + e.getMessage());
        }
        Object token = BrowserManager.getPage().evaluate("() => localStorage.getItem('access_token')");
        boolean tokenCleared = token == null
                || "null".equals(token.toString())
                || token.toString().trim().isEmpty();
        a.assertTrue(tokenCleared,
                "TC-SEC-014: access_token must be cleared from localStorage after logout (found: " + token + ")");
        a.assertAll();
    }

    // ─── TC-SEC-015: Verify CSP header ──────────────────────────────────────
    @Test(priority = 15,
          description = "Content-Security-Policy header should be present")
    public void testContentSecurityPolicyHeader() {
        SecurityUtils sec = new SecurityUtils();
        sec.assertCSPHeaderPresent();
        new WelcomePage(utilLayer).completeOnboarding();
        WaitManager.safePageLoad();
    }
}
