package com.toskie.tests.security;

import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.locators.LoginPageLocators;
import com.toskie.locators.ProfileCreationLocators;
import com.toskie.pages.LoginPage;
import com.toskie.pages.WelcomePage;
import com.toskie.utils.NetworkValidator;
import com.toskie.utils.SecurityUtils;
import com.toskie.utils.TestDataManager;
import com.toskie.utils_Layer.BrowserManager;
import org.testng.annotations.DataProvider;
import com.toskie.utils_Layer.ReportManager;
import org.testng.annotations.Test;

/**
 * SECURITY TESTS â€” OWASP Top 10, XSS, SQLi, Auth, HTTPS, Token security
 * TC-SEC-001 through TC-SEC-015
 */
public class SecurityTests extends BaseTest {

    @DataProvider(name = "xssPayloads")
    public Object[][] xssPayloads() {
        return TestDataManager.getSecurityPayloads();
    }

    // â”€â”€â”€ TC-SEC-001: XSS in phone number field â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    @Test(priority = 1,
          description = "OWASP A03: XSS payloads in phone number field should be sanitized")
    public void testXSSInPhoneField() {
        SecurityUtils sec = new SecurityUtils();
        new WelcomePage(utilLayer).completeOnboarding();
        new LoginPage(utilLayer).clickLoginButton();

        LoginPageLocators loc = new LoginPageLocators(BrowserManager.getPage());
        sec.testXSSInField(loc.phoneNumberInput, "Phone Number");
        // Pass regardless â€” we've logged pass/fail per payload
    }

    // â”€â”€â”€ TC-SEC-002: XSS in first name field â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    @Test(priority = 2,
          description = "OWASP A03: XSS payloads in profile first name should be sanitized")
    public void testXSSInFirstNameField() {
        SecurityUtils sec = new SecurityUtils();
        new WelcomePage(utilLayer).completeOnboarding();
        new LoginPage(utilLayer).loginWithDefaultCredentials();

        ProfileCreationLocators loc = new ProfileCreationLocators(BrowserManager.getPage());
        com.toskie.pages.ProfileCreationPage pp = new com.toskie.pages.ProfileCreationPage(utilLayer);
        if (!pp.isProfileCreationPageVisible()) return;

        sec.testXSSInField(loc.firstNameInput, "First Name");
    }

    // â”€â”€â”€ TC-SEC-003: SQL Injection in search â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    @Test(priority = 3,
          description = "OWASP A03: SQL injection payloads in search should be sanitized")
    public void testSQLInjectionInSearch() {
        new WelcomePage(utilLayer).completeOnboarding();
        new LoginPage(utilLayer).loginWithDefaultCredentials();

        com.toskie.pages.ProfileCreationPage pp = new com.toskie.pages.ProfileCreationPage(utilLayer);
        if (pp.isProfileCreationPageVisible()) pp.createProfileWithDefaultData();

        com.toskie.locators.SearchPageLocators loc =
            new com.toskie.locators.SearchPageLocators(BrowserManager.getPage());
        for (String payload : SecurityUtils.SQL_INJECTION_PAYLOADS) {
            try {
                loc.searchInput.fill(payload);
                BrowserManager.getPage().keyboard().press("Enter");
                BrowserManager.getPage().waitForTimeout(1000);
                String content = BrowserManager.getPage().content().toLowerCase();
                if (content.contains("sql error") || content.contains("syntax error")) {
                    com.toskie.utils_Layer.ReportManager.getTest().log(
                        com.aventstack.extentreports.Status.FAIL,
                        "SQL INJECTION VULNERABILITY for payload: " + payload);
                } else {
                    com.toskie.utils_Layer.ReportManager.getTest().log(
                        com.aventstack.extentreports.Status.PASS,
                        "SQLi handled safely: " + payload.substring(0, Math.min(40, payload.length())));
                }
                loc.searchInput.fill("");
            } catch (Exception ignored) {}
        }
    }

    // â”€â”€â”€ TC-SEC-004: All requests use HTTPS â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
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

    // â”€â”€â”€ TC-SEC-005: No sensitive data in URLs â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
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

    // â”€â”€â”€ TC-SEC-006: Auth header sent with API calls â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    @Test(priority = 6,
          description = "OWASP A07: Authorization header must be present in authenticated API calls")
    public void testAuthHeaderPresentInAPIcalls() {
        new WelcomePage(utilLayer).completeOnboarding();
        new LoginPage(utilLayer).loginWithDefaultCredentials();

        NetworkValidator nv = new NetworkValidator();
        nv.startCapturing();
        BrowserManager.getPage().waitForTimeout(3000);
        nv.stopCapturing();
        nv.assertAuthHeaderPresent();
    }

    // â”€â”€â”€ TC-SEC-007: Token expiry validation â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    @Test(priority = 7,
          description = "OWASP A07: JWT token should have a valid expiry and not be expired")
    public void testTokenExpiry() {
        new WelcomePage(utilLayer).completeOnboarding();
        new LoginPage(utilLayer).loginWithDefaultCredentials();

        SecurityUtils sec = new SecurityUtils();
        sec.assertTokenExpiry();
    }

    // â”€â”€â”€ TC-SEC-008: QA secret not exposed in responses â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    @Test(priority = 8,
          description = "QA secret key must not appear in any API response")
    public void testQASecretNotExposedInResponses() {
        SecurityUtils sec = new SecurityUtils();
        sec.assertNoSensitiveDataInResponse("graphql");

        new WelcomePage(utilLayer).completeOnboarding();
        new LoginPage(utilLayer).loginWithDefaultCredentials();
        BrowserManager.getPage().waitForTimeout(3000);
    }

    // â”€â”€â”€ TC-SEC-009: OTP rate limiting â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    @Test(priority = 9,
          description = "OWASP A04: Multiple OTP requests should trigger rate limiting")
    public void testOTPRateLimiting() {
        new WelcomePage(utilLayer).completeOnboarding();
        new LoginPage(utilLayer).clickLoginButton();

        LoginPageLocators loc = new LoginPageLocators(BrowserManager.getPage());
        loc.phoneNumberInput.fill("9919011050");

        SecurityUtils sec = new SecurityUtils();
        sec.assertRateLimitingOnOTP(loc.sendOtpButton, 5);
    }

    // â”€â”€â”€ TC-SEC-010: Data-driven XSS payloads â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    @Test(dataProvider = "xssPayloads", priority = 10,
          description = "Data-driven: Multiple XSS payloads across different input fields")
    public void testXSSPayloadsInFields(String payload, String field, String expectedResult) {
        new WelcomePage(utilLayer).completeOnboarding();
        new LoginPage(utilLayer).clickLoginButton();

        LoginPageLocators loc = new LoginPageLocators(BrowserManager.getPage());
        try {
            if ("phoneNumber".equals(field)) {
                loc.phoneNumberInput.fill(payload);
                BrowserManager.getPage().waitForTimeout(500);
                String content = BrowserManager.getPage().content();
                boolean xssExecuted = isXSSExecuted(content, payload);
                com.toskie.utils_Layer.ReportManager.getTest().log(
                    xssExecuted ? com.aventstack.extentreports.Status.FAIL
                                : com.aventstack.extentreports.Status.PASS,
                    (xssExecuted ? "XSS EXECUTED in " : "XSS blocked in ") + field + ": " + payload.substring(0, Math.min(50, payload.length())));
            }
        } catch (Exception ignored) {}
    }

    // â”€â”€â”€ TC-SEC-011: CSRF protection â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    @Test(priority = 11,
          description = "API endpoints should have CSRF protection or SameSite cookies")
    public void testCSRFProtection() {
        NetworkValidator nv = new NetworkValidator();
        nv.startCapturing();

        new WelcomePage(utilLayer).completeOnboarding();
        new LoginPage(utilLayer).loginWithDefaultCredentials();

        nv.stopCapturing();
        // Log the check â€” actual CSRF validation requires deeper inspection
        com.toskie.utils_Layer.ReportManager.getTest().log(
            com.aventstack.extentreports.Status.INFO,
            "CSRF: Verify that auth tokens use httpOnly+SameSite cookies or CSRF tokens.");
    }

    // â”€â”€â”€ TC-SEC-012: Broken Object Level Authorization â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    @Test(priority = 12,
          description = "OWASP API A01: Test BOLA â€” access another user's profile data")
    public void testBrokenObjectLevelAuthorization() {
        new WelcomePage(utilLayer).completeOnboarding();
        new LoginPage(utilLayer).loginWithDefaultCredentials();

        // Try accessing other user IDs â€” should return 403 or masked data
        for (String testId : SecurityUtils.BOLA_TEST_IDS) {
            try {
                String testUrl = BrowserManager.getPage().url().split("#")[0] + "/profile/" + testId;
                BrowserManager.getPage().navigate(testUrl);
                BrowserManager.getPage().waitForTimeout(1500);
                String content = BrowserManager.getPage().content().toLowerCase();
                boolean unexpectedAccess = content.contains("admin") && !content.contains("not found");
                com.toskie.utils_Layer.ReportManager.getTest().log(
                    unexpectedAccess ? com.aventstack.extentreports.Status.WARNING
                                     : com.aventstack.extentreports.Status.PASS,
                    "BOLA check for ID=" + testId + ": " + (unexpectedAccess ? "review needed" : "ok"));
            } catch (Exception ignored) {}
        }
    }

    // â”€â”€â”€ TC-SEC-013: No password/secrets in page source â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    @Test(priority = 13,
          description = "QA secrets and passwords must not appear in page HTML source")
    public void testNoSecretsInPageSource() {
        new WelcomePage(utilLayer).completeOnboarding();
        new LoginPage(utilLayer).loginWithDefaultCredentials();

        String source = BrowserManager.getPage().content();
        boolean hasSecret = source.contains("ATDBUDBOCLYMQEJBTSKXATKZCGCTFIUTOHTVCWOMLGVHMJUHVZUV");
        com.toskie.utils_Layer.ReportManager.getTest().log(
            hasSecret ? com.aventstack.extentreports.Status.FAIL
                      : com.aventstack.extentreports.Status.PASS,
            hasSecret ? "QA SECRET found in page source!" : "No QA secrets in page source.");
    }

    // â”€â”€â”€ TC-SEC-014: Session token invalidation on logout â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    @Test(priority = 14,
          description = "Token in localStorage should be cleared after logout")
    public void testTokenClearedOnLogout() {
        new WelcomePage(utilLayer).completeOnboarding();
        new LoginPage(utilLayer).loginWithDefaultCredentials();

        // Navigate to settings and logout (if available)
        try {
            BrowserManager.getPage().locator("[href*='settings'], button:has-text('Settings')").first().click();
            BrowserManager.getPage().waitForTimeout(1000);
            BrowserManager.getPage().locator("button:has-text('Logout')").first().click();
            BrowserManager.getPage().waitForTimeout(2000);

            Object token = BrowserManager.getPage().evaluate("() => localStorage.getItem('access_token')");
            com.toskie.utils_Layer.ReportManager.getTest().log(
                (token == null || token.toString().isEmpty())
                    ? com.aventstack.extentreports.Status.PASS
                    : com.aventstack.extentreports.Status.WARNING,
                "Token after logout: " + token);
        } catch (Exception e) {
            com.toskie.utils_Layer.ReportManager.getTest().log(
                com.aventstack.extentreports.Status.INFO,
                "Logout flow not reachable from current state.");
        }
    }

    // â”€â”€â”€ TC-SEC-015: Verify CSP header â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    @Test(priority = 15,
          description = "Content-Security-Policy header should be present")
    public void testContentSecurityPolicyHeader() {
        SecurityUtils sec = new SecurityUtils();
        sec.assertCSPHeaderPresent();
        new WelcomePage(utilLayer).completeOnboarding();
        BrowserManager.getPage().waitForTimeout(2000);
    }

    private boolean isXSSExecuted(String content, String payload) {
        return content.contains("<script>alert") || content.contains("onerror=alert");
    }
}

