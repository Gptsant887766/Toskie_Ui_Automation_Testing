package com.toskie.AuthenticationPages.Page;

import com.toskie.AuthenticationPages.OrPages.page.WelcomeToToskieLandingPageOr;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.UtilLayer;

public class WelcomeToToskieLandingPage extends WelcomeToToskieLandingPageOr {

    private final UtilLayer<?> utilLayer;

    public WelcomeToToskieLandingPage(UtilLayer<?> utilLayer) {
        super(BrowserManager.getPage());
        this.utilLayer = utilLayer;
    }

    public void validateWelcomeToToskieLandingPage() {
        NextBT.waitFor(new com.microsoft.playwright.Locator.WaitForOptions()
            .setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE)
            .setTimeout(15000));
        utilLayer.clickThreeTimes(NextBT, "Next Button");

        String landingUrl = BrowserManager.getPage().url();

        // The final-slide CTA varies by app build:
        //   Current build  → "Start Exploring"  (visible, no overlay)
        //   Previous build → "Create My Profile" (has an overlay, needs force-click)
        // Try "Start Exploring" first (3-second grace); fall back to force-clicking
        // "Create My Profile" so older builds and both CTAs are handled.
        boolean startExploringVisible = false;
        try {
            StartExploring.waitFor(new com.microsoft.playwright.Locator.WaitForOptions()
                .setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE)
                .setTimeout(3000));
            startExploringVisible = true;
        } catch (Exception ignored) {}

        if (startExploringVisible) {
            utilLayer.click(StartExploring, "Start Exploring Button");
        } else {
            utilLayer.forceClick(CreateMYProfile, "Create My Profile Button");
        }

        // The app uses same-URL SPA state rendering — clicking the final CTA swaps React
        // components without changing the URL. waitForURL therefore never resolves.
        // Instead wait for the welcome-slide "Next" button to leave the DOM, which reliably
        // confirms the onboarding screen has been replaced by the next view.
        NextBT.waitFor(new com.microsoft.playwright.Locator.WaitForOptions()
            .setState(com.microsoft.playwright.options.WaitForSelectorState.HIDDEN)
            .setTimeout(10000));
        BrowserManager.getPage().waitForLoadState(com.microsoft.playwright.options.LoadState.DOMCONTENTLOADED);
    }

    public boolean isNextButtonStillVisible() {
        try {
            return NextBT.count() > 0 && NextBT.isVisible();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isWelcomePageLoaded() {
        try {
            NextBT.waitFor(new com.microsoft.playwright.Locator.WaitForOptions()
                .setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE)
                .setTimeout(10000));
            return true;
        } catch (Exception e) {
            try {
                StartExploring.waitFor(new com.microsoft.playwright.Locator.WaitForOptions()
                    .setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE)
                    .setTimeout(5000));
                return true;
            } catch (Exception ex) {
                return false;
            }
        }
    }
}
