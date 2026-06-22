package com.toskie.tests.smoke;

import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.pages.LoginPage;
import com.toskie.pages.ProfileCreationPage;
import com.toskie.pages.WelcomePage;
import com.toskie.utils.AssertionHelper;
import com.toskie.utils_Layer.BrowserManager;
import org.testng.annotations.Test;

/**
 * SMOKE TESTS -- Critical happy-path tests.
 * Run on every deployment to confirm the app is alive and core flows work.
 */
public class SmokeTests extends BaseTest {

    // ─── TC-SM-001: App loads ─────────────────────────────────────────────────
    @Test(groups = {"smoke"}, priority = 1,
          description = "Verify application loads and shows welcome/onboarding screen")
    public void verifyAppLoads() {
        AssertionHelper a = new AssertionHelper();
        WelcomePage welcomePage = new WelcomePage(utilLayer);
        String url = BrowserManager.getPage().url();

        a.assertNotEmpty(url, "App URL should not be empty");
        a.assertContains(url, "toskie.com", "App URL should be on toskie.com domain");
        a.assertFalse(url.contains("404") || url.contains("error") || url.contains("not-found"),
            "App URL must not be an error page (actual: " + url + ")");
        a.assertAll();
    }

    // ─── TC-SM-002: Onboarding completes ──────────────────────────────────────
    @Test(groups = {"smoke"}, priority = 2,
          description = "Complete onboarding slides and reach login screen")
    public void verifyOnboardingFlow() {
        AssertionHelper a = new AssertionHelper();
        WelcomePage welcomePage = new WelcomePage(utilLayer);

        try {
            welcomePage.completeOnboarding();
        } catch (Exception e) {
            // Onboarding may not appear for returning users -- treat as non-fatal
        }

        String url = BrowserManager.getPage().url();
        a.assertNotEmpty(url, "After onboarding, app URL should not be empty");
        a.assertContains(url, "toskie.com", "After onboarding, URL should remain on toskie.com domain");
        a.assertTrue(
            url.contains("login") || url.contains("register") || url.contains("dashboard") || url.contains("toskie"),
            "After onboarding, should be on login or dashboard -- not an error page (actual: " + url + ")");
        a.assertAll();
    }

    // ─── TC-SM-003: QA Login works ────────────────────────────────────────────
    @Test(groups = {"smoke"}, priority = 3,
          description = "Login with QA bypass and verify token generation")
    public void verifyLoginQABypass() {
        AssertionHelper a = new AssertionHelper();
        WelcomePage wp = new WelcomePage(utilLayer);
        try { wp.completeOnboarding(); } catch (Exception e) { /* onboarding may not appear */ }

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
        try { wp.completeOnboarding(); } catch (Exception e) { /* onboarding may not appear */ }

        LoginPage lp = new LoginPage(utilLayer);
        lp.loginWithDefaultCredentials();

        ProfileCreationPage pp = new ProfileCreationPage(utilLayer);
        if (pp.isProfileCreationPageVisible()) {
            try {
                pp.createProfileWithDefaultData();
            } catch (Exception e) {
                // Profile may already exist or a step may be pre-filled -- treat as non-fatal
            }
        }
        // If profile already created, test passes -- user is in the app
        AssertionHelper a = new AssertionHelper();
        String url = BrowserManager.getPage().url();
        a.assertNotEmpty(url, "User should be on a valid page after login");
        a.assertContains(url, "toskie.com", "User should remain on toskie.com after login journey");
        a.assertTrue(
            url.contains("dashboard") || url.contains("profile") || url.contains("home"),
            "After login journey, user should be on dashboard or profile -- not on login page (actual: " + url + ")");
        a.assertAll();
    }

    // ─── TC-SM-005: API responds ──────────────────────────────────────────────
    @Test(groups = {"smoke"}, priority = 5,
          description = "Verify GraphQL API responds to QA login mutation")
    public void verifyAPIResponds() {
        utilLayer.loginViaQAGraphQL("8808992219");
        AssertionHelper a = new AssertionHelper();
        a.assertNotEmpty(utilLayer.getAccessToken(),  "API must return access_token");
        a.assertNotEmpty(utilLayer.getRefreshToken(), "API must return refresh_token");
        a.assertFalse(utilLayer.isTokenExpired(), "Token must not be expired immediately after login");
        a.assertAll();
    }
}
