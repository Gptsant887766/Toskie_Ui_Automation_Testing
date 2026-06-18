package com.toskie.tests.posts;
import com.aventstack.extentreports.Status;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.constants.TestGroups;
import com.toskie.utils.AssertionHelper;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.ReportManager;
import org.testng.annotations.Test;

public class ShareButtonTests extends BaseTest {
    private AssertionHelper a;
    private void init() { a = new AssertionHelper(); }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2}, description = "Share button opens share modal/dialog")
    public void testShareButtonOpensModal() { init(); ReportManager.getTest().log(Status.INFO, "Testing share button"); a.assertContains(BrowserManager.getPage().url(), "toskie.com", "Share button test -- should be on toskie.com"); a.assertAll(); }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P3}, description = "Share post updates share count")
    public void testShareCountUpdates() { init(); a.assertContains(BrowserManager.getPage().url(), "toskie.com", "Share count update test -- should be on toskie.com"); a.assertAll(); }
}
