package com.toskie.tests.posts;
import com.aventstack.extentreports.Status;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.constants.TestGroups;
import com.toskie.pages.posts.PostDetailPage;
import com.toskie.utils.AssertionHelper;
import org.testng.annotations.Test;

public class PostDetailTests extends BaseTest {
    private PostDetailPage detailPage;
    private AssertionHelper a;
    private void init() { detailPage = new PostDetailPage(utilLayer); a = new AssertionHelper(); }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P1}, description = "Post detail page opens with full content")
    public void testPostDetailOpens() { init(); a.assertTrue(detailPage.isPostContentVisible(), "Post detail should show content"); a.assertAll(); }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2}, description = "Comments are visible on post detail page")
    public void testCommentsVisible() { init(); a.assertTrue(detailPage.isCommentsVisible(), "Comments should be visible"); a.assertAll(); }
}
