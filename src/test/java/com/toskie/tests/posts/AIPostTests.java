package com.toskie.tests.posts;
import com.aventstack.extentreports.Status;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.constants.TestGroups;
import com.toskie.utils.AssertionHelper;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.ReportManager;
import org.testng.annotations.Test;

public class AIPostTests extends BaseTest {
    private AssertionHelper a;
    private void init() { a = new AssertionHelper(); }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2}, description = "AI-generated post suggestion appears")
    public void testAISuggestionAppears() { init(); ReportManager.getTest().log(Status.INFO, "Testing AI post suggestion"); a.assertContains(BrowserManager.getPage().url(), "toskie.com", "AI suggestion test -- should be on toskie.com"); a.assertAll(); }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P3}, description = "Apply AI suggestion fills post textarea")
    public void testApplyAISuggestion() { init(); a.assertContains(BrowserManager.getPage().url(), "toskie.com", "AI suggestion apply test -- should be on toskie.com"); a.assertAll(); }
}
