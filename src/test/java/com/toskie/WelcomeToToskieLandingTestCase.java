package com.toskie;

import com.toskie.AuthenticationPages.Page.WelcomeToToskieLandingPage;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.utils_Layer.BrowserManager;
import org.testng.Assert;
import org.testng.annotations.Test;

public class WelcomeToToskieLandingTestCase extends BaseTest {

    @Test(description = "Verify welcome landing page navigation flow")
    public void verifyWelcomeToToskieLanding() {
        WelcomeToToskieLandingPage welcomePage = new WelcomeToToskieLandingPage(utilLayer);

        Assert.assertTrue(welcomePage.isWelcomePageLoaded(),
            "Welcome/onboarding page must be visible before navigating through slides");

        welcomePage.validateWelcomeToToskieLandingPage();

        // The app uses same-URL SPA state rendering — clicking the final CTA replaces the
        // welcome screen in-place without a URL change, so assertNotEquals on URLs is wrong.
        // The reliable indicator of forward progress is that the slide Next button is gone.
        Assert.assertFalse(welcomePage.isNextButtonStillVisible(),
            "Next button must not be visible after completing the onboarding flow");
        Assert.assertFalse(BrowserManager.getPage().url().isEmpty(),
            "URL must not be empty after completing the onboarding flow");
    }
}
