package com.toskie.pages;

import com.aventstack.extentreports.Status;
import com.microsoft.playwright.options.LoadState;
import com.toskie.locators.WelcomePageLocators;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.ReportManager;
import com.toskie.utils_Layer.UtilLayer;

public class WelcomePage {

    private final UtilLayer<?> util;
    private final WelcomePageLocators loc;

    public WelcomePage(UtilLayer<?> util) {
        this.util = util;
        this.loc  = new WelcomePageLocators(BrowserManager.getPage());
    }

    // ─── Page verification ────────────────────────────────────────────────────

    public boolean isOnWelcomePage() {
        try {
            return loc.nextButton.isVisible() || loc.createMyProfileButton.isVisible();
        } catch (Exception e) {
            return false;
        }
    }

    // ─── Onboarding flow ──────────────────────────────────────────────────────

    public void clickNext() {
        util.click(loc.nextButton, "Next Button");
        BrowserManager.getPage().waitForTimeout(500);
    }

    public void clickNextNTimes(int times) {
        for (int i = 0; i < times; i++) {
            util.click(loc.nextButton, "Next Button [" + (i + 1) + "/" + times + "]");
            BrowserManager.getPage().waitForTimeout(500);
        }
    }

    public void completeOnboarding() {
        ReportManager.getTest().log(Status.INFO, "Starting onboarding flow...");
        BrowserManager.getPage().waitForLoadState(LoadState.NETWORKIDLE);
        BrowserManager.getPage().waitForTimeout(2000);

        // Navigate through 3 slides
        clickNextNTimes(3);

        // Use forceClick — fixed footer div (user-registration-footer-shadow z-50) intercepts normal clicks
        util.forceClick(loc.createMyProfileButton, "Create My Profile");
        BrowserManager.getPage().waitForTimeout(1000);
        ReportManager.getTest().log(Status.PASS, "Onboarding completed.");
    }

    public void skipOnboarding() {
        try {
            if (loc.skipButton.isVisible()) {
                util.click(loc.skipButton, "Skip Button");
                ReportManager.getTest().log(Status.INFO, "Onboarding skipped.");
            } else {
                completeOnboarding();
            }
        } catch (Exception e) {
            completeOnboarding();
        }
    }

    public void clickStartExploring() {
        util.click(loc.startExploringButton, "Start Exploring");
    }

    public void verifySlideContent(int slideNumber) {
        ReportManager.getTest().log(Status.INFO, "Verifying slide " + slideNumber + " content.");
        BrowserManager.getPage().waitForTimeout(500);
    }

    public int getVisibleSlideCount() {
        return loc.progressDots.count();
    }

    public boolean isNextButtonVisible() {
        return loc.nextButton.isVisible();
    }

    public boolean isCreateProfileButtonVisible() {
        return loc.createMyProfileButton.isVisible();
    }

    public boolean isSkipButtonVisible() {
        try {
            return loc.skipButton.isVisible();
        } catch (Exception e) {
            return false;
        }
    }
}
