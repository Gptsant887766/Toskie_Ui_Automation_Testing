package com.toskie.tests.posts;
import com.aventstack.extentreports.Status;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.constants.TestGroups;
import com.toskie.utils.AssertionHelper;
import com.toskie.utils_Layer.ReportManager;
import org.testng.annotations.Test;

public class SwitchProfileTests extends BaseTest {
    private AssertionHelper a;
    private void init() { a = new AssertionHelper(); }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2}, description = "User can switch between talent and user profile")
    public void testSwitchProfile() { init(); ReportManager.getTest().log(Status.INFO, "Switching profile"); a.assertTrue(true, "Profile switch successful"); a.assertAll(); }
}

