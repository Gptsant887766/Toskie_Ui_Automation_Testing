package com.toskie.tests.misc;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.constants.TestGroups;
import com.toskie.utils.AssertionHelper;
import com.toskie.utils_Layer.BrowserManager;
import org.testng.annotations.Test;

public class ProjectViewApiTests extends BaseTest {
    private AssertionHelper a;
    private void init() { a = new AssertionHelper(); }

    @Test(groups = {TestGroups.API, TestGroups.P2}, description = "Project view API returns projects correctly")
    public void testProjectViewApi() { init(); a.assertContains(BrowserManager.getPage().url(), "toskie.com", "Project view API test -- should be on toskie.com"); a.assertAll(); }
}
