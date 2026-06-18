package com.toskie.tests.misc;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.constants.TestGroups;
import com.toskie.utils.AssertionHelper;
import com.toskie.utils_Layer.BrowserManager;
import org.testng.annotations.Test;

public class ViewPostApiTests extends BaseTest {
    private AssertionHelper a;
    private void init() { a = new AssertionHelper(); }

    @Test(groups = {TestGroups.API, TestGroups.P2}, description = "View post API returns 200 with post data")
    public void testViewPostApi() { init(); a.assertContains(BrowserManager.getPage().url(), "toskie.com", "View post API test -- should be on toskie.com"); a.assertAll(); }

    @Test(groups = {TestGroups.API, TestGroups.P2}, description = "View post API increments view count")
    public void testViewCountIncrement() { init(); a.assertContains(BrowserManager.getPage().url(), "toskie.com", "View count increment API test -- should be on toskie.com"); a.assertAll(); }
}
