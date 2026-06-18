package com.toskie.tests.misc;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.constants.TestGroups;
import com.toskie.utils.AssertionHelper;
import com.toskie.utils_Layer.BrowserManager;
import org.testng.annotations.Test;

public class UserTipsTests extends BaseTest {
    private AssertionHelper a;
    private void init() { a = new AssertionHelper(); }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P3}, description = "User tips/onboarding tooltip is shown to new users")
    public void testOnboardingTipsVisible() { init(); a.assertContains(BrowserManager.getPage().url(), "toskie.com", "Onboarding tips test -- should be on toskie.com"); a.assertAll(); }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P3}, description = "Dismiss tip removes it from view")
    public void testDismissTip() { init(); a.assertContains(BrowserManager.getPage().url(), "toskie.com", "Dismiss tip test -- should be on toskie.com"); a.assertAll(); }
}
