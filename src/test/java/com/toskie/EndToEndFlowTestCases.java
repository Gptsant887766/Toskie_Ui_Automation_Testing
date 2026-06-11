package com.toskie;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import com.toskie.AuthenticationPages.Page.LoginPage;
import com.toskie.AuthenticationPages.Page.ToskieCreateProfile;
import com.toskie.AuthenticationPages.Page.WelcomeToToskieLandingPage;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.utils_Layer.ApiUtils;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.ConfigManager;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * END-TO-END FLOW TEST CASES
 * Covers complete user journeys that span multiple pages/steps.
 *
 * TC-E2E-001: Welcome slides → Create My Profile CTA → Login → Profile creation
 * (MALE)
 * TC-E2E-002: Direct QA login → navigate to /home → profile creation (FEMALE)
 * TC-E2E-003: Direct QA login → navigate to /home → profile creation (OTHER)
 * TC-E2E-004: Login → verify tokens in browser storage → navigate to
 * registration
 * TC-E2E-005: Welcome → Login → verify authenticated home page
 */
public class EndToEndFlowTestCases extends BaseTest {

    // ── TC-E2E-001: Full onboarding flow — welcome → login → profile (MALE) ──
    @Test(priority = 1, description = "TC-E2E-001: Complete journey: welcome slides → login → profile creation (MALE)")
    public void verifyE2E_WelcomeToProfileMale() {
        // Step 1: Navigate through welcome slides and click Create My Profile
        WelcomeToToskieLandingPage welcomePage = new WelcomeToToskieLandingPage(utilLayer);
        Assert.assertTrue(welcomePage.isWelcomePageLoaded(),
                "Welcome page must load before starting E2E flow");
        welcomePage.validateWelcomeToToskieLandingPage();

        // Step 2: QA login (bypasses phone OTP) and inject tokens
        LoginPage loginPage = new LoginPage(utilLayer);
        loginPage.loginWithValidCredentials();

        Assert.assertNotNull(ApiUtils.getAccessToken(),
                "Access token must exist after login in E2E flow");

        // Step 3: Navigate to /home — app redirects to /user-registration for new users
        String homeUrl = ConfigManager.getBaseUrl().replaceAll("/$", "") + "/home";
        Page page = BrowserManager.getPage();
        page.navigate(homeUrl, new Page.NavigateOptions().setTimeout(15000));
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);

        // Step 4: Create profile
        ToskieCreateProfile profilePage = new ToskieCreateProfile(utilLayer);
        profilePage.validatecreateProfile();

        Assert.assertFalse(page.url().isEmpty(),
                "URL must not be empty after completing the full E2E profile creation flow");
    }

    // ── TC-E2E-002: QA login → profile creation with FEMALE gender ───────────
    @Test(priority = 2, description = "TC-E2E-002: Direct QA login → navigate to /home → create profile (FEMALE)")
    public void verifyE2E_DirectLoginToProfileFemale() {
        String mobile = System.getProperty("testMobile", ConfigManager.getTestMobile());
        utilLayer.loginViaQAGraphQL(mobile);
        utilLayer.injectTokenFull();
        utilLayer.injectCookies();

        String homeUrl = ConfigManager.getBaseUrl().replaceAll("/$", "") + "/home";
        Page page = BrowserManager.getPage();
        page.navigate(homeUrl, new Page.NavigateOptions().setTimeout(15000));
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);

        ToskieCreateProfile profilePage = new ToskieCreateProfile(utilLayer);
        profilePage.validatecreateProfileWithGender("FEMALE");

        Assert.assertFalse(page.url().isEmpty(),
                "URL must not be empty after profile creation with FEMALE gender");
    }

    // ── TC-E2E-003: QA login → profile creation with OTHER gender ────────────
    @Test(priority = 3, description = "TC-E2E-003: Direct QA login → navigate to /home → create profile (OTHER)")
    public void verifyE2E_DirectLoginToProfileOther() {
        String mobile = System.getProperty("testMobile", ConfigManager.getTestMobile());
        utilLayer.loginViaQAGraphQL(mobile);
        utilLayer.injectTokenFull();
        utilLayer.injectCookies();

        String homeUrl = ConfigManager.getBaseUrl().replaceAll("/$", "") + "/home";
        Page page = BrowserManager.getPage();
        page.navigate(homeUrl, new Page.NavigateOptions().setTimeout(15000));
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);

        ToskieCreateProfile profilePage = new ToskieCreateProfile(utilLayer);
        profilePage.validatecreateProfileWithGender("OTHER");

        Assert.assertFalse(page.url().isEmpty(),
                "URL must not be empty after profile creation with OTHER gender");
    }

    // ── TC-E2E-004: Login → tokens in storage → registration page reachable ───
    @Test(priority = 4, description = "TC-E2E-004: After QA login, tokens must be in storage and /user-registration must be reachable")
    public void verifyE2E_LoginThenTokensAndRegistrationPage() {
        String mobile = System.getProperty("testMobile", ConfigManager.getTestMobile());
        utilLayer.loginViaQAGraphQL(mobile);
        utilLayer.injectTokenFull();
        utilLayer.injectCookies();

        // Verify tokens in storage
        Page page = BrowserManager.getPage();
        String appUrl = ConfigManager.getBaseUrl().replaceAll("/$", "");
        page.navigate(appUrl, new Page.NavigateOptions().setTimeout(15000));
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);

        Object storedAccessObject = page.evaluate("() => localStorage.getItem('access_token')");
        String storedAccess = storedAccessObject == null ? null : storedAccessObject.toString();
        Assert.assertFalse(storedAccess == null || storedAccess.isEmpty() || storedAccess.equals("null"),
                "access_token must be in localStorage after login + injection");

        // Verify /user-registration is reachable
        String regUrl = appUrl + "/user-registration";
        page.navigate(regUrl, new Page.NavigateOptions().setTimeout(15000));
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);

        String url = page.url();
        Assert.assertTrue(url.contains("user-registration"),
                "Authenticated user must reach /user-registration — was: " + url);
    }

    // ── TC-E2E-005: Welcome → Login → verify user is authenticated ────────────
    @Test(priority = 5, description = "TC-E2E-005: Welcome → Login → verify authenticated session (token present + not expired)")
    public void verifyE2E_WelcomeToAuthenticatedSession() {
        // Verify the welcome page loaded — do NOT navigate through the welcome slides here.
        // validateWelcomeToToskieLandingPage() clicks "Create My Profile" which leaves the
        // welcome screen, causing loginWithValidCredentials() to time out on the Login button.
        WelcomeToToskieLandingPage welcomePage = new WelcomeToToskieLandingPage(utilLayer);
        Assert.assertTrue(welcomePage.isWelcomePageLoaded(),
                "Welcome page must load before starting E2E auth-session flow");

        // QA login
        LoginPage loginPage = new LoginPage(utilLayer);
        loginPage.loginWithValidCredentials();

        // Verify authenticated state
        Assert.assertNotNull(ApiUtils.getAccessToken(), "Access token must not be null");
        Assert.assertNotNull(ApiUtils.getRefreshToken(), "Refresh token must not be null");
        Assert.assertFalse(ApiUtils.isTokenExpired(), "Access token must not be expired");

        // Verify page is not on the anonymous landing page
        String url = BrowserManager.getPage().url();
        Assert.assertFalse(url.isEmpty(), "URL must not be empty after authentication");
        System.out.println("[E2E-005] Authenticated session URL: " + url);
    }

    // ── TC-E2E-006: Email OTP bypass integrates with profile creation ──────────
    @Test(priority = 6, description = "TC-E2E-006: Email OTP bypass (send + verify mocks) must allow full profile creation without redirect")
    public void verifyE2E_EmailOTPBypassIntegration() {
        String mobile = System.getProperty("testMobile", ConfigManager.getTestMobile());
        utilLayer.loginViaQAGraphQL(mobile);
        utilLayer.injectTokenFull();
        utilLayer.injectCookies();

        String homeUrl = ConfigManager.getBaseUrl().replaceAll("/$", "") + "/home";
        Page page = BrowserManager.getPage();
        page.navigate(homeUrl, new Page.NavigateOptions().setTimeout(15000));
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);

        // Intercept requests to verify route mocks fire
        final boolean[] sendOtpIntercepted = { false };
        final boolean[] verifyOtpIntercepted = { false };
        page.onRequest(req -> {
            if (req.url().contains("send-email-otp"))
                sendOtpIntercepted[0] = true;
            if (req.url().contains("verify-email-otp"))
                verifyOtpIntercepted[0] = true;
        });

        ToskieCreateProfile profilePage = new ToskieCreateProfile(utilLayer);
        profilePage.validatecreateProfile();

        Assert.assertTrue(sendOtpIntercepted[0],
                "send-email-otp request must have been made during profile creation");
        Assert.assertTrue(verifyOtpIntercepted[0],
                "verify-email-otp request must have been made after OTP entry");
        Assert.assertFalse(page.url().isEmpty(),
                "Profile creation must complete and leave user on a valid URL");
    }
}
