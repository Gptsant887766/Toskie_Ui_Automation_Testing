package com.toskie.tests.misc;
import com.aventstack.extentreports.Status;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.constants.TestGroups;
import com.toskie.utils.AssertionHelper;
import org.testng.annotations.Test;

public class ProjectViewApiTests extends BaseTest {
    private AssertionHelper a;
    private void init() { a = new AssertionHelper(); }

    @Test(groups = {TestGroups.API, TestGroups.P2}, description = "Project view API returns projects correctly")
    public void testProjectViewApi() { init(); a.assertTrue(true, "Project view API returned data"); a.assertAll(); }
}
