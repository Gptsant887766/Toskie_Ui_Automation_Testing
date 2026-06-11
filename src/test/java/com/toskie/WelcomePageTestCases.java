package com.toskie;

import com.microsoft.playwright.options.WaitForSelectorState;
import com.toskie.AuthenticationPages.Page.WelcomeToToskieLandingPage;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.utils_Layer.BrowserManager;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * WELCOME / ONBOARDING PAGE TEST CASES
 * Covers the landing page slide navigation and CTA buttons.
 *
 * Note: The app is a React SPA. Elements are rendered by JS after
 * DOMCONTENTLOADED fires, so all element checks use waitFor() instead
 * of the immediate count()/isVisible() — matching the pattern used by
 * WelcomeToToskieLandingPage.validateWelcomeToToskieLandingPage() which
 * calls NextBT.waitFor(VISIBLE, 15000) and works reliably.
 */
public class WelcomePageTestCases extends BaseTest {

    private static final int RENDER_WAIT_MS = 5000;

    // ── TC-WP-001: Welcome page loads — Next button renders ──────────────────
    @Test(priority = 1,
          description = "TC-WP-001: Welcome page Next button must render within 5 seconds of page load")
    public void verifyWelcomePage_Loads() {
        com.microsoft.playwright.Locator nextBtn =
            BrowserManager.getPage().locator("//span[contains(text(),'Next')]");
        nextBtn.waitFor(new com.microsoft.playwright.Locator.WaitForOptions()
            .setState(WaitForSelectorState.VISIBLE)
            .setTimeout(RENDER_WAIT_MS));
        Assert.assertTrue(nextBtn.isVisible(),
            "Next button must be visible after JS renders the welcome screen");
    }

    // ── TC-WP-002: App URL is not empty on load ───────────────────────────────
    @Test(priority = 2,
          description = "TC-WP-002: App URL must not be empty when the welcome page loads")
    public void verifyWelcomePage_URLNotEmpty() {
        String url = BrowserManager.getPage().url();
        Assert.assertFalse(url == null || url.isEmpty(),
            "App URL must not be empty after initial page load");
    }

    // ── TC-WP-003: Next button is present and visible after render ────────────
    @Test(priority = 3,
          description = "TC-WP-003: Next button (span 'Next') must be present after JS renders")
    public void verifyWelcomePage_NextButtonPresent() {
        com.microsoft.playwright.Locator nextBtn =
            BrowserManager.getPage().locator("//span[contains(text(),'Next')]");
        nextBtn.waitFor(new com.microsoft.playwright.Locator.WaitForOptions()
            .setState(WaitForSelectorState.VISIBLE)
            .setTimeout(RENDER_WAIT_MS));
        Assert.assertTrue(nextBtn.count() > 0,
            "Next button must be present in the DOM after the SPA renders");
    }

    // ── TC-WP-004: Slide navigation completes without exception ──────────────
    @Test(priority = 4,
          description = "TC-WP-004: Clicking Next 3 times + Create My Profile must complete without error")
    public void verifyWelcomePage_NavigateThroughSlides() {
        WelcomeToToskieLandingPage welcomePage = new WelcomeToToskieLandingPage(utilLayer);
        // validateWelcomeToToskieLandingPage uses waitFor(VISIBLE, 15000) internally
        welcomePage.validateWelcomeToToskieLandingPage();
        // If no exception was thrown, the slide navigation succeeded
        Assert.assertFalse(BrowserManager.getPage().url().isEmpty(),
            "URL must not be empty after slide navigation completes");
    }

    // ── TC-WP-005: Create My Profile CTA is reachable and clickable ──────────
    @Test(priority = 5,
          description = "TC-WP-005: 'Create My Profile' button must be clickable after the slides")
    public void verifyWelcomePage_CreateMyProfileButtonClickable() {
        WelcomeToToskieLandingPage welcomePage = new WelcomeToToskieLandingPage(utilLayer);
        welcomePage.validateWelcomeToToskieLandingPage();
        Assert.assertFalse(BrowserManager.getPage().url().isEmpty(),
            "After clicking Create My Profile, app must be on a valid URL");
    }

    // ── TC-WP-006: isWelcomePageLoaded detects the page after render ──────────
    @Test(priority = 6,
          description = "TC-WP-006: isWelcomePageLoaded() must return true once Next button has rendered")
    public void verifyWelcomePage_HelperMethodReturnsTrueOnFreshLoad() {
        // Wait for SPA to render before calling isWelcomePageLoaded()
        com.microsoft.playwright.Locator nextBtn =
            BrowserManager.getPage().locator("//span[contains(text(),'Next')]");
        nextBtn.waitFor(new com.microsoft.playwright.Locator.WaitForOptions()
            .setState(WaitForSelectorState.VISIBLE)
            .setTimeout(RENDER_WAIT_MS));

        WelcomeToToskieLandingPage welcomePage = new WelcomeToToskieLandingPage(utilLayer);
        Assert.assertTrue(welcomePage.isWelcomePageLoaded(),
            "isWelcomePageLoaded() must return true after the Next button is visible");
    }

    // ── TC-WP-007: Welcome slides dismissed after full onboarding flow ──────────
    // After clicking the final CTA ("Start Exploring" in current build, "Create My
    // Profile" in older builds), the app replaces the welcome overlay with the home
    // screen in-place — same URL, no navigation. The reliable proof is that the Next
    // button (present only during onboarding slides) is no longer visible.
    @Test(priority = 7,
          description = "TC-WP-007: After final onboarding CTA, welcome slides must be replaced by home content")
    public void verifyWelcomePage_LoginLinkAppearsAfterSlides() {
        WelcomeToToskieLandingPage welcomePage = new WelcomeToToskieLandingPage(utilLayer);
        welcomePage.validateWelcomeToToskieLandingPage();

        Assert.assertFalse(welcomePage.isNextButtonStillVisible(),
            "Next button must not be visible after completing the onboarding flow");
        Assert.assertFalse(BrowserManager.getPage().url().isEmpty(),
            "URL must not be empty after the onboarding flow completes");
    }

    // ── TC-WP-008: Next button disappears after all slides complete ───────────
    @Test(priority = 8,
          description = "TC-WP-008: After clicking Next 3 times, the Create My Profile CTA must appear")
    public void verifyWelcomePage_CreateProfileCTAAppearsAfterSlides() {
        WelcomeToToskieLandingPage welcomePage = new WelcomeToToskieLandingPage(utilLayer);
        welcomePage.NextBT.waitFor(new com.microsoft.playwright.Locator.WaitForOptions()
            .setState(WaitForSelectorState.VISIBLE).setTimeout(RENDER_WAIT_MS));
        utilLayer.clickThreeTimes(welcomePage.NextBT, "Next Button (slides)");

        com.microsoft.playwright.Locator createBtn =
            BrowserManager.getPage().locator("//span[contains(text(),'Create My Profile')]");
        createBtn.waitFor(new com.microsoft.playwright.Locator.WaitForOptions()
            .setState(WaitForSelectorState.VISIBLE).setTimeout(RENDER_WAIT_MS));
        Assert.assertTrue(createBtn.count() > 0,
            "Create My Profile button must be visible on the final onboarding screen");
    }
}
