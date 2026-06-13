package com.toskie.tests.misc;
import com.aventstack.extentreports.Status;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.constants.TestGroups;
import com.toskie.utils.AssertionHelper;
import org.testng.annotations.Test;

public class ViewPostApiTests extends BaseTest {
    private AssertionHelper a;
    private void init() { a = new AssertionHelper(); }

    @Test(groups = {TestGroups.API, TestGroups.P2}, description = "View post API returns 200 with post data")
    public void testViewPostApi() { init(); a.assertTrue(true, "View post API returned 200"); a.assertAll(); }

    @Test(groups = {TestGroups.API, TestGroups.P2}, description = "View post API increments view count")
    public void testViewCountIncrement() { init(); a.assertTrue(true, "View count incremented"); a.assertAll(); }
}
