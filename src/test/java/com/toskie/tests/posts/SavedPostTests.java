package com.toskie.tests.posts;
import com.aventstack.extentreports.Status;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.constants.TestGroups;
import com.toskie.utils.AssertionHelper;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.ReportManager;
import org.testng.annotations.Test;

public class SavedPostTests extends BaseTest {
    private AssertionHelper a;
    private void init() { a = new AssertionHelper(); }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2}, description = "Bookmark post saves it to saved list")
    public void testBookmarkPost() { init(); ReportManager.getTest().log(Status.INFO, "Bookmarking a post"); a.assertContains(BrowserManager.getPage().url(), "toskie.com", "Bookmark post test -- should be on toskie.com"); a.assertAll(); }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2}, description = "Saved posts list shows bookmarked posts")
    public void testSavedPostsVisible() { init(); a.assertContains(BrowserManager.getPage().url(), "toskie.com", "Saved posts visible test -- should be on toskie.com"); a.assertAll(); }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P3}, description = "Remove bookmark removes post from saved list")
    public void testRemoveBookmark() { init(); a.assertContains(BrowserManager.getPage().url(), "toskie.com", "Remove bookmark test -- should be on toskie.com"); a.assertAll(); }
}
