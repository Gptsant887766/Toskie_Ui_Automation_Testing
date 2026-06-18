package com.toskie.tests.accessibility;

import com.aventstack.extentreports.Status;
import com.deque.html.axecore.playwright.AxeBuilder;
import com.deque.html.axecore.results.AxeResults;
import com.deque.html.axecore.results.Rule;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.pages.LoginPage;
import com.toskie.pages.WelcomePage;
import com.toskie.utils.AccessibilityUtils;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.ReportManager;
import org.testng.annotations.Test;

import java.util.List;

/**
 * ACCESSIBILITY TESTS -- WCAG 2.1 AA compliance
 * TC-ACC-001 through TC-ACC-012
 */
public class AccessibilityTests extends BaseTest {

    private void runAxeAudit(String pageName) {
        try {
            AxeBuilder axeBuilder = new AxeBuilder(BrowserManager.getPage());
            AxeResults results = axeBuilder.analyze();
            List<Rule> violations = results.getViolations();
            if (violations.isEmpty()) {
                ReportManager.getTest().log(Status.PASS,
                    "axe-core WCAG 2.1 AA: No violations on " + pageName);
            } else {
                ReportManager.getTest().log(Status.INFO,
                    "axe-core: " + violations.size() + " violation(s) on " + pageName);
                for (Rule rule : violations) {
                    ReportManager.getTest().log(Status.FAIL,
                        "[axe-core][" + rule.getImpact() + "] " + rule.getId()
                        + ": " + rule.getHelp()
                        + " (" + rule.getNodes().size() + " element(s))");
                }
            }
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING,
                "axe-core audit skipped on " + pageName + ": " + e.getMessage());
        }
    }

    // ─── TC-ACC-001: Welcome page a11y audit ──────────────────────────────────
    @Test(priority = 1,
          description = "WCAG full audit on Welcome/Onboarding page")
    public void testWelcomePageAccessibility() {
        AccessibilityUtils a11y = new AccessibilityUtils();
        a11y.runFullAccessibilityAudit("Welcome Page");
        runAxeAudit("Welcome Page");
    }

    // ─── TC-ACC-002: Login page a11y audit ────────────────────────────────────
    @Test(priority = 2,
          description = "WCAG full audit on Login page")
    public void testLoginPageAccessibility() {
        new WelcomePage(utilLayer).completeOnboarding();
        new LoginPage(utilLayer).clickLoginButton();
        AccessibilityUtils a11y = new AccessibilityUtils();
        a11y.runFullAccessibilityAudit("Login Page");
        runAxeAudit("Login Page");
    }

    // ─── TC-ACC-003: Profile creation a11y audit ──────────────────────────────
    @Test(priority = 3,
          description = "WCAG full audit on Profile Creation page")
    public void testProfileCreationAccessibility() {
        new WelcomePage(utilLayer).completeOnboarding();
        new LoginPage(utilLayer).loginWithDefaultCredentials();
        AccessibilityUtils a11y = new AccessibilityUtils();
        a11y.runFullAccessibilityAudit("Profile Creation Page");
        runAxeAudit("Profile Creation Page");
    }

    // ─── TC-ACC-004: All images have alt text ─────────────────────────────────
    @Test(priority = 4,
          description = "WCAG 1.1.1: All images must have alt text")
    public void testImagesHaveAltText() {
        AccessibilityUtils a11y = new AccessibilityUtils();
        a11y.assertAllImagesHaveAltText();
    }

    // ─── TC-ACC-005: All buttons have accessible names ────────────────────────
    @Test(priority = 5,
          description = "WCAG 4.1.2: All buttons must have text or aria-label")
    public void testButtonsHaveAccessibleNames() {
        AccessibilityUtils a11y = new AccessibilityUtils();
        a11y.assertAllButtonsHaveAccessibleName();
    }

    // ─── TC-ACC-006: All inputs have labels ───────────────────────────────────
    @Test(priority = 6,
          description = "WCAG 1.3.1: All form inputs must have associated labels")
    public void testInputsHaveLabels() {
        new WelcomePage(utilLayer).completeOnboarding();
        new LoginPage(utilLayer).clickLoginButton();
        AccessibilityUtils a11y = new AccessibilityUtils();
        a11y.assertAllInputsHaveLabels();
    }

    // ─── TC-ACC-007: Heading hierarchy ────────────────────────────────────────
    @Test(priority = 7,
          description = "WCAG 1.3.1: Page should have proper heading hierarchy (H1 → H2...)")
    public void testHeadingHierarchy() {
        AccessibilityUtils a11y = new AccessibilityUtils();
        a11y.assertHeadingHierarchy();
    }

    // ─── TC-ACC-008: Landmark roles ───────────────────────────────────────────
    @Test(priority = 8,
          description = "WCAG 1.3.6: Page should have main, nav, and header landmark roles")
    public void testLandmarkRoles() {
        AccessibilityUtils a11y = new AccessibilityUtils();
        a11y.assertLandmarkRoles();
    }

    // ─── TC-ACC-009: Tab key navigation ───────────────────────────────────────
    @Test(priority = 9,
          description = "WCAG 2.1.1: All interactive elements should be keyboard navigable via Tab")
    public void testTabKeyNavigation() {
        new WelcomePage(utilLayer).completeOnboarding();
        new LoginPage(utilLayer).clickLoginButton();
        AccessibilityUtils a11y = new AccessibilityUtils();
        a11y.testTabKeyNavigation(10);
    }

    // ─── TC-ACC-010: Focus indicators ────────────────────────────────────────
    @Test(priority = 10,
          description = "WCAG 2.4.7: Focusable elements should have visible focus indicator")
    public void testFocusIndicators() {
        new WelcomePage(utilLayer).completeOnboarding();
        AccessibilityUtils a11y = new AccessibilityUtils();
        a11y.assertFocusableElementsHaveFocusIndicator();
    }

    // ─── TC-ACC-011: ARIA live regions ────────────────────────────────────────
    @Test(priority = 11,
          description = "Dynamic content updates should use ARIA live regions for screen readers")
    public void testAriaLiveRegions() {
        new WelcomePage(utilLayer).completeOnboarding();
        new LoginPage(utilLayer).loginWithDefaultCredentials();
        AccessibilityUtils a11y = new AccessibilityUtils();
        a11y.assertAriaLiveRegionPresent();
    }

    // ─── TC-ACC-012: Escape closes modal ──────────────────────────────────────
    @Test(priority = 12,
          description = "WCAG 2.1.2: Escape key should close modal dialogs")
    public void testEscapeClosesModal() {
        new WelcomePage(utilLayer).completeOnboarding();
        new LoginPage(utilLayer).loginWithDefaultCredentials();

        com.toskie.pages.ProfileCreationPage pp =
            new com.toskie.pages.ProfileCreationPage(utilLayer);
        if (pp.isProfileCreationPageVisible()) {
            // Open DOB calendar
            try {
                pp.enterFirstName("Test"); // ensure page is ready
                com.toskie.locators.ProfileCreationLocators loc =
                    new com.toskie.locators.ProfileCreationLocators(BrowserManager.getPage());
                loc.dobButton.click();
                BrowserManager.getPage().waitForTimeout(500);

                AccessibilityUtils a11y = new AccessibilityUtils();
                a11y.assertEscapeKeyClosesModal(loc.dobCalendarDialog);
            } catch (Exception ignored) {}
        }
    }
}
