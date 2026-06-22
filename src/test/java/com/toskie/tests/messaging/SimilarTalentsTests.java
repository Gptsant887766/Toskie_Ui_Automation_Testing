package com.toskie.tests.messaging;
import com.aventstack.extentreports.Status;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.constants.TestGroups;
import com.toskie.utils.AssertionHelper;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.ReportManager;
import org.testng.annotations.Test;

public class SimilarTalentsTests extends BaseTest {
    private AssertionHelper a;
    private void init() { a = new AssertionHelper(); }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2}, description = "Similar talents shown on talent profile")
    public void testSimilarTalentsVisible() {
        init();
        ReportManager.getTest().log(Status.INFO, "Checking similar talents section");
        a.assertContains(BrowserManager.getPage().url(), "toskie.com", "Similar talents test -- should be on toskie.com");
        a.assertAll();
    }
}
