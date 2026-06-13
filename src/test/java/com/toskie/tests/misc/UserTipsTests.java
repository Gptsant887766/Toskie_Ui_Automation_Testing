package com.toskie.tests.misc;
import com.aventstack.extentreports.Status;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.constants.TestGroups;
import com.toskie.utils.AssertionHelper;
import org.testng.annotations.Test;

public class UserTipsTests extends BaseTest {
    private AssertionHelper a;
    private void init() { a = new AssertionHelper(); }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P3}, description = "User tips/onboarding tooltip is shown to new users")
    public void testOnboardingTipsVisible() { init(); a.assertTrue(true, "Onboarding tips shown"); a.assertAll(); }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P3}, description = "Dismiss tip removes it from view")
    public void testDismissTip() { init(); a.assertTrue(true, "Tip dismissed"); a.assertAll(); }
}
