package com.toskie.tests.misc;
import com.aventstack.extentreports.Status;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.constants.TestGroups;
import com.toskie.utils.AssertionHelper;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.ReportManager;
import org.testng.annotations.Test;

public class PrivacyPolicyTests extends BaseTest {
    private AssertionHelper a;
    private void init() { a = new AssertionHelper(); }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P3}, description = "Privacy policy page loads correctly")
    public void testPrivacyPolicyLoads() { init(); ReportManager.getTest().log(Status.INFO, "Privacy policy page"); a.assertContains(BrowserManager.getPage().url(), "toskie.com", "Privacy policy page test -- should be on toskie.com"); a.assertAll(); }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P3}, description = "Terms and conditions page loads")
    public void testTermsLoads() { init(); a.assertContains(BrowserManager.getPage().url(), "toskie.com", "Terms page test -- should be on toskie.com"); a.assertAll(); }
}
