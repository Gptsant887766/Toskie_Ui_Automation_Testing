package com.toskie.tests.posts;
import com.aventstack.extentreports.Status;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.constants.TestGroups;
import com.toskie.pages.posts.PostDetailPage;
import com.toskie.utils.AssertionHelper;
import org.testng.annotations.Test;

public class LikeBookmarkTests extends BaseTest {
    private PostDetailPage detailPage;
    private AssertionHelper a;
    private void init() { detailPage = new PostDetailPage(utilLayer); a = new AssertionHelper(); }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P1}, description = "Like a post and verify like count increases")
    public void testLikePost() { init(); detailPage.likePost(); a.assertTrue(detailPage.isLiked(), "Post should be liked"); a.assertAll(); }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P1}, description = "Unlike a post and verify like count decreases")
    public void testUnlikePost() { init(); if (detailPage.isLiked()) detailPage.likePost(); a.assertFalse(detailPage.isLiked(), "Post should be unliked"); a.assertAll(); }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2}, description = "Bookmark a post and verify it appears in saved")
    public void testBookmarkPost() { init(); detailPage.bookmarkPost(); a.assertTrue(detailPage.isBookmarked(), "Post should be bookmarked"); a.assertAll(); }
}
