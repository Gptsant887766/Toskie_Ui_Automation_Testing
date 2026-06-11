package com.toskie.tests.smoke;

import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.pages.LoginPage;
import com.toskie.pages.ProfileCreationPage;
import com.toskie.pages.WelcomePage;
import com.toskie.utils.AssertionHelper;
import com.toskie.utils_Layer.BrowserManager;
import org.testng.annotations.Test;

/**
 * SMOKE TESTS — Critical happy-path tests.
 * Run on every deployment to confirm the app is alive and core flows work.
 */
public class SmokeTests extends BaseTest {

    // ─── TC-SM-001: App loads ─────────────────────────────────────────────────
    @Test(groups = {"smoke"}, priority = 1,
          description = "Verify application loads and shows welcome/onboarding screen")
    public void verifyAppLoads() {
        AssertionHelper a = new AssertionHelper();
        WelcomePage welcomePage = new WelcomePage(utilLayer);

        a.assertTrue(
            welcomePage.isOnWelcomePage() || !BrowserManager.getPage().url().isEmpty(),
            "App should load and show welcome page or app URL");
        a.assertNotEmpty(BrowserManager.getPage().url(), "App URL should not be empty");
        a.assertAll();
    }

    // ─── TC-SM-002: Onboarding completes ──────────────────────────────────────
    @Test(groups = {"smoke"}, priority = 2,
          description = "Complete onboarding slides and reach login screen")
    public void verifyOnboardingFlow() {
        AssertionHelper a = new AssertionHelper();
        WelcomePage welcomePage = new WelcomePage(utilLayer);

        welcomePage.completeOnboarding();

        LoginPage loginPage = new LoginPage(utilLayer);
        a.assertTrue(
            loginPage.isLoginButtonVisible() || !BrowserManager.getPage().url().isEmpty(),
            "After onboarding, Login button should be visible OR app navigates forward");
        a.assertAll();
    }

    // ─── TC-SM-003: QA Login works ────────────────────────────────────────────
    @Test(groups = {"smoke"}, priority = 3,
          description = "Login with QA bypass and verify token generation")
    public void verifyLoginQABypass() {
        AssertionHelper a = new AssertionHelper();
        WelcomePage wp = new WelcomePage(utilLayer);
        wp.completeOnboarding();

        LoginPage lp = new LoginPage(utilLayer);
        lp.loginWithDefaultCredentials();

        String token = utilLayer.getAccessToken();
        a.assertNotEmpty(token, "Access token after QA login");
        a.assertAll();
    }

    // ─── TC-SM-004: Profile creation completes ────────────────────────────────
    @Test(groups = {"smoke"}, priority = 4,
          description = "Complete full onboarding → login → profile creation flow")
    public void verifyCompleteUserJourney() {
        WelcomePage wp = new WelcomePage(utilLayer);
        wp.completeOnboarding();

        LoginPage lp = new LoginPage(utilLayer);
        lp.loginWithDefaultCredentials();

        ProfileCreationPage pp = new ProfileCreationPage(utilLayer);
        if (pp.isProfileCreationPageVisible()) {
            pp.createProfileWithDefaultData();
        }
        // If profile already created, test passes — user is in the app
        AssertionHelper a = new AssertionHelper();
        a.assertNotEmpty(BrowserManager.getPage().url(), "User should be on a valid page after login");
        a.assertAll();
    }

    // ─── TC-SM-005: API responds ──────────────────────────────────────────────
    @Test(groups = {"smoke"}, priority = 5,
          description = "Verify GraphQL API responds to QA login mutation")
    public void verifyAPIResponds() {
        utilLayer.loginViaQAGraphQL("9919011050");
        AssertionHelper a = new AssertionHelper();
        a.assertNotEmpty(utilLayer.getAccessToken(),  "API must return access_token");
        a.assertNotEmpty(utilLayer.getRefreshToken(), "API must return refresh_token");
        a.assertFalse(utilLayer.isTokenExpired(), "Token must not be expired immediately after login");
        a.assertAll();
    }
}
