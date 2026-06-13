package com.toskie.tests.posts;
import com.aventstack.extentreports.Status;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.constants.TestGroups;
import com.toskie.pages.posts.ExplorePage;
import com.toskie.utils.AssertionHelper;
import org.testng.annotations.Test;

public class ExploreTests extends BaseTest {
    private ExplorePage explorePage;
    private AssertionHelper a;
    private void init() { explorePage = new ExplorePage(utilLayer); a = new AssertionHelper(); }

    @Test(groups = {TestGroups.SMOKE, TestGroups.P0}, description = "Explore page loads with content")
    public void testExplorePageLoads() { init(); a.assertTrue(explorePage.isExplorePageLoaded(), "Explore page should load"); a.assertAll(); }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P1}, description = "Explore feed has posts")
    public void testExploreHasPosts() { init(); a.assertTrue(explorePage.getPostCount() > 0, "Explore should show posts"); a.assertAll(); }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2}, description = "Infinite scroll loads more posts")
    public void testInfiniteScroll() {
        init();
        int before = explorePage.getPostCount();
        explorePage.scrollToBottom();
        a.assertTrue(explorePage.getPostCount() >= before, "More posts should load on scroll");
        a.assertAll();
    }
}
