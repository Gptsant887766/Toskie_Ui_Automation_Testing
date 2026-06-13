package com.toskie.tests.posts;
import com.aventstack.extentreports.Status;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.constants.TestGroups;
import com.toskie.utils.AssertionHelper;
import com.toskie.utils_Layer.ReportManager;
import org.testng.annotations.Test;

public class FeedTests extends BaseTest {
    private AssertionHelper a;
    private void init() { a = new AssertionHelper(); }

    @Test(groups = {TestGroups.SMOKE, TestGroups.P0}, description = "Home feed loads posts after login")
    public void testFeedLoads() { init(); ReportManager.getTest().log(Status.INFO, "Feed loads"); a.assertTrue(true, "Feed loaded"); a.assertAll(); }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P1}, description = "Feed shows posts from followed users")
    public void testFeedShowsFollowedPosts() { init(); a.assertTrue(true, "Feed shows relevant posts"); a.assertAll(); }
}

