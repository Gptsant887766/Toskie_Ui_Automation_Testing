package com.toskie;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.toskie.AuthenticationPages.Page.LoginPage;
import com.toskie.AuthenticationPages.Page.WelcomeToToskieLandingPage;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.utils_Layer.ApiUtils;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.ConfigManager;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * LOGIN PAGE TEST CASES
 * Covers QA-bypass login, token validation, storage injection, and navigation.
 * All tests use the QA_Bypass_Login GraphQL mutation (no real OTP required).
 */
public class LoginPageTestCases extends BaseTest {

    private String testMobile() {
        return System.getProperty("testMobile", ConfigManager.getTestMobile());
    }

    // ── TC-LG-001: QA login returns access token ──────────────────────────────
    @Test(priority = 1,
          description = "TC-LG-001: QA_Bypass_Login must return a non-empty access token")
    public void verifyLogin_ReturnsAccessToken() {
        utilLayer.loginViaQAGraphQL(testMobile());
        String token = ApiUtils.getAccessToken();
        Assert.assertNotNull(token,  "Access token must not be null after QA login");
        Assert.assertFalse(token.isEmpty(), "Access token must not be empty");
    }

    // ── TC-LG-002: QA login returns refresh token ─────────────────────────────
    @Test(priority = 2,
          description = "TC-LG-002: QA_Bypass_Login must return a non-empty refresh token")
    public void verifyLogin_ReturnsRefreshToken() {
        utilLayer.loginViaQAGraphQL(testMobile());
        String token = ApiUtils.getRefreshToken();
        Assert.assertNotNull(token, "Refresh token must not be null after QA login");
        Assert.assertFalse(token.isEmpty(), "Refresh token must not be empty");
    }

    // ── TC-LG-003: Access token has valid JWT structure (3 parts) ─────────────
    @Test(priority = 3,
          description = "TC-LG-003: Access token must be a valid JWT with 3 base64 parts")
    public void verifyLogin_TokenIsValidJWT() {
        utilLayer.loginViaQAGraphQL(testMobile());
        String token = ApiUtils.getAccessToken();
        Assert.assertNotNull(token, "Token must not be null");
        String[] parts = token.split("\\.");
        Assert.assertEquals(parts.length, 3,
            "JWT token must have exactly 3 parts (header.payload.signature) — was: " + token);
    }

    // ── TC-LG-004: Token is not expired immediately after login ───────────────
    @Test(priority = 4,
          description = "TC-LG-004: Access token must not be expired immediately after issuance")
    public void verifyLogin_TokenNotExpiredAfterLogin() {
        utilLayer.loginViaQAGraphQL(testMobile());
        Assert.assertFalse(ApiUtils.isTokenExpired(),
            "Freshly issued access token must not be expired");
    }

    // ── TC-LG-005: Token injected into localStorage ───────────────────────────
    @Test(priority = 5,
          description = "TC-LG-005: injectTokenFull() must store access_token in localStorage")
    public void verifyLogin_TokenInjectedToLocalStorage() {
        utilLayer.loginViaQAGraphQL(testMobile());
        utilLayer.injectTokenFull();

        String stored = BrowserManager.getPage()
            .evaluate("() => localStorage.getItem('access_token')")
            .toString();
        Assert.assertFalse(stored == null || stored.isEmpty() || stored.equals("null"),
            "access_token must be present in localStorage after injectTokenFull()");
    }

    // ── TC-LG-006: Token injected into sessionStorage ─────────────────────────
    @Test(priority = 6,
          description = "TC-LG-006: injectTokenFull() must store access_token in sessionStorage")
    public void verifyLogin_TokenInjectedToSessionStorage() {
        utilLayer.loginViaQAGraphQL(testMobile());
        utilLayer.injectTokenFull();

        String stored = BrowserManager.getPage()
            .evaluate("() => sessionStorage.getItem('access_token')")
            .toString();
        Assert.assertFalse(stored == null || stored.isEmpty() || stored.equals("null"),
            "access_token must be present in sessionStorage after injectTokenFull()");
    }

    // ── TC-LG-007: Navigate to /home succeeds after token injection ───────────
    @Test(priority = 7,
          description = "TC-LG-007: After login + token injection, /home must load without auth error")
    public void verifyLogin_NavigateToHomeSucceeds() {
        utilLayer.loginViaQAGraphQL(testMobile());
        utilLayer.injectTokenFull();
        utilLayer.injectCookies();

        String homeUrl = ConfigManager.getBaseUrl().replaceAll("/$", "") + "/home";
        Page page = BrowserManager.getPage();
        page.navigate(homeUrl, new Page.NavigateOptions().setTimeout(15000));
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);

        String url = page.url();
        Assert.assertFalse(url.isEmpty(), "URL must not be empty after navigating to /home");
        Assert.assertFalse(url.contains("non-loggedin-profile") && !url.contains("user-registration"),
            "Authenticated user must not land on non-loggedin-profile — was: " + url);
    }

    // ── TC-LG-008: LoginPage.loginWithValidCredentials() completes ────────────
    // The "Login" span lives on the phone-entry page reached AFTER clicking
    // "Create My Profile" — so the full welcome flow must run before loginWithValidCredentials().
    @Test(priority = 8,
          description = "TC-LG-008: After full welcome flow (Next×3 + Create My Profile), loginWithValidCredentials() must complete and return a token")
    public void verifyLoginPage_LoginWithValidCredentials() {
        WelcomeToToskieLandingPage welcomePage = new WelcomeToToskieLandingPage(utilLayer);
        welcomePage.validateWelcomeToToskieLandingPage(); // Next×3 + Create My Profile

        LoginPage loginPage = new LoginPage(utilLayer);
        loginPage.loginWithValidCredentials();

        Assert.assertNotNull(ApiUtils.getAccessToken(),
            "loginWithValidCredentials() must produce a valid access token");
    }

    // ── TC-LG-009: Welcome slides dismissed after final onboarding CTA ──────────
    // Previously the final CTA was "Create My Profile" which navigated to a
    // phone-entry page with a "Login" span. The current build shows "Start Exploring"
    // which replaces the welcome overlay with the home screen in-place (same URL,
    // no "Login" span). The reliable proof of forward progress is that the Next
    // button — present only during onboarding — is no longer visible.
    @Test(priority = 9,
          description = "TC-LG-009: After final onboarding CTA, welcome slides must be dismissed (Next button gone)")
    public void verifyLoginPage_LoginButtonVisibleAfterSlides() {
        WelcomeToToskieLandingPage welcomePage = new WelcomeToToskieLandingPage(utilLayer);
        welcomePage.validateWelcomeToToskieLandingPage();

        Assert.assertFalse(welcomePage.isNextButtonStillVisible(),
            "Next button must not be visible after the onboarding flow completes");
        Assert.assertFalse(BrowserManager.getPage().url().isEmpty(),
            "URL must not be empty after the onboarding flow completes");
    }

    // ── TC-LG-010: Data-driven — login with multiple mobile numbers ───────────
    @DataProvider(name = "multipleUsers", parallel = false)
    public Object[][] multipleUsers() {
        return new Object[][]{
            {"9919011050", "primary_qa_account"},
            // Add additional QA mobile numbers below as new accounts are created:
            // {"9876543210", "secondary_qa_account"},
        };
    }

    @Test(dataProvider = "multipleUsers", priority = 10,
          description = "TC-LG-010: Data-driven — each QA mobile number must produce a valid token")
    public void verifyLogin_MultipleUsers(String mobile, String label) {
        System.out.println("[LoginPageTestCases] mobile=" + mobile + " label=" + label);
        utilLayer.loginViaQAGraphQL(mobile);

        String token = ApiUtils.getAccessToken();
        Assert.assertNotNull(token,  "Access token must not be null for mobile=" + mobile);
        Assert.assertFalse(token.isEmpty(), "Access token must not be empty for mobile=" + mobile);
        Assert.assertFalse(ApiUtils.isTokenExpired(), "Token must be fresh for mobile=" + mobile);
    }

    // ── TC-LG-011: Consecutive logins produce non-null tokens ────────────────
    @Test(priority = 11,
          description = "TC-LG-011: Two consecutive QA logins must each return a non-null access token")
    public void verifyLogin_ConsecutiveLoginsReturnTokens() {
        utilLayer.loginViaQAGraphQL(testMobile());
        String token1 = ApiUtils.getAccessToken();
        Assert.assertNotNull(token1, "First login token must not be null");

        utilLayer.loginViaQAGraphQL(testMobile());
        String token2 = ApiUtils.getAccessToken();
        Assert.assertNotNull(token2, "Second login token must not be null");
    }

    // ── TC-LG-012: Both tokens differ between login and browser storage ────────
    @Test(priority = 12,
          description = "TC-LG-012: Token from ApiUtils and localStorage must match after injection")
    public void verifyLogin_StoredTokenMatchesApiToken() {
        utilLayer.loginViaQAGraphQL(testMobile());
        utilLayer.injectTokenFull();

        String apiToken = ApiUtils.getAccessToken();
        String storedToken = BrowserManager.getPage()
            .evaluate("() => localStorage.getItem('access_token')")
            .toString();

        Assert.assertEquals(storedToken, apiToken,
            "Token in localStorage must match the token returned by ApiUtils");
    }
}
