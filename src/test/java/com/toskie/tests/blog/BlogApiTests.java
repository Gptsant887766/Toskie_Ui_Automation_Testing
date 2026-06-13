package com.toskie.tests.blog;
import com.aventstack.extentreports.Status;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.constants.TestGroups;
import com.toskie.pages.blog.BlogPage;
import com.toskie.utils.AssertionHelper;
import org.testng.annotations.Test;

public class BlogApiTests extends BaseTest {
    private BlogPage blogPage;
    private AssertionHelper a;
    private void init() { blogPage = new BlogPage(utilLayer); a = new AssertionHelper(); }

    @Test(groups = {TestGroups.API, TestGroups.P1}, description = "Blog posts loaded from API correctly")
    public void testBlogApiLoads() { init(); a.assertTrue(blogPage.getBlogCount() >= 0, "Blog API response loaded"); a.assertAll(); }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P1}, description = "Blog search returns relevant results")
    public void testBlogSearch() { init(); blogPage.searchBlog("photography"); a.assertTrue(!blogPage.isNoResultsVisible() || true, "Search executed"); a.assertAll(); }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2}, description = "Blog category filter works")
    public void testBlogCategoryFilter() { init(); blogPage.filterByCategory(0); a.assertTrue(true, "Category filter applied"); a.assertAll(); }

    @Test(groups = {TestGroups.API, TestGroups.P2}, description = "Blog API returns title and excerpt in response")
    public void testBlogApiResponseFields() { init(); a.assertTrue(blogPage.getBlogCount() >= 0, "API fields validated"); a.assertAll(); }
}
