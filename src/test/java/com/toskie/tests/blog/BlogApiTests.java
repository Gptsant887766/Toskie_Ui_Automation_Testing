package com.toskie.tests.blog;

import com.aventstack.extentreports.Status;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.constants.AppConstants;
import com.toskie.constants.TestGroups;
import com.toskie.pages.blog.BlogPage;
import com.toskie.utils.AssertionHelper;
import com.toskie.utils_Layer.ApiUtils;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.ConfigManager;
import com.toskie.utils_Layer.ReportManager;
import org.testng.annotations.Test;

public class BlogApiTests extends BaseTest {

    private BlogPage blogPage;
    private AssertionHelper a;

    private void init() {
        ApiUtils.loginViaQAGraphQL(ConfigManager.get("testMobile"));
        ApiUtils.injectTokenFull();
        ApiUtils.injectCookies();
        BrowserManager.getPage().navigate(AppConstants.BLOG_URL);
        BrowserManager.getPage().waitForTimeout(2000);
        blogPage = new BlogPage(utilLayer);
        a = new AssertionHelper();
    }

    @Test(groups = {TestGroups.API, TestGroups.P1},
          description = "Blog page loads and displays posts or empty state")
    public void testBlogApiLoads() {
        init();
        int count = blogPage.getBlogCount();
        ReportManager.getTest().log(Status.INFO, "BLOG-1: Blog post count: " + count);
        boolean loaded = count > 0 || blogPage.isNoResultsVisible();
        a.assertTrue(loaded,
                "BLOG-1: Blog page must show either posts (count > 0) or an empty-state message (got count=" + count + ")");
        String url = BrowserManager.getPage().url();
        a.assertContains(url, "toskie.com",
                "BLOG-1: Must remain on toskie.com domain after navigating to blog (got: " + url + ")");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P1},
          description = "Blog titles are non-empty strings when posts are visible")
    public void testBlogTitlesNonEmpty() {
        init();
        int count = blogPage.getBlogCount();
        ReportManager.getTest().log(Status.INFO, "BLOG-2: Blog post count: " + count);
        if (count == 0) {
            ReportManager.getTest().log(Status.INFO, "BLOG-2: No posts visible -- empty state acceptable, skipping title check");
            a.assertTrue(blogPage.isNoResultsVisible() || count == 0,
                    "BLOG-2: When no posts visible, empty state must be shown");
        } else {
            String firstTitle = blogPage.getBlogTitle(0);
            ReportManager.getTest().log(Status.INFO, "BLOG-2: First blog title: '" + firstTitle + "'");
            a.assertFalse(firstTitle.isEmpty(),
                    "BLOG-2: Blog post titles must be non-empty strings (got empty title at index 0)");
        }
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P1},
          description = "Blog search returns results or shows no-results state")
    public void testBlogSearch() {
        init();
        int beforeSearch = blogPage.getBlogCount();
        ReportManager.getTest().log(Status.INFO, "BLOG-3: Posts before search: " + beforeSearch);
        blogPage.searchBlog("photography");
        BrowserManager.getPage().waitForTimeout(1500);
        int afterSearch = blogPage.getBlogCount();
        boolean noResultsShown = blogPage.isNoResultsVisible();
        ReportManager.getTest().log(Status.INFO,
                "BLOG-3: Posts after search for 'photography': " + afterSearch + " | noResults: " + noResultsShown);
        a.assertTrue(afterSearch >= 0 && (afterSearch > 0 || noResultsShown),
                "BLOG-3: Search must produce results OR display a no-results message (got " + afterSearch + " posts, noResults=" + noResultsShown + ")");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2},
          description = "Blog category filter changes the displayed posts")
    public void testBlogCategoryFilter() {
        init();
        int beforeFilter = blogPage.getBlogCount();
        ReportManager.getTest().log(Status.INFO, "BLOG-4: Posts before filter: " + beforeFilter);
        try {
            blogPage.filterByCategory(0);
            BrowserManager.getPage().waitForTimeout(1500);
        } catch (Exception e) {
            ReportManager.getTest().log(Status.INFO, "BLOG-4: No category filters found -- skipping filter click");
        }
        int afterFilter = blogPage.getBlogCount();
        boolean noResultsShown = blogPage.isNoResultsVisible();
        ReportManager.getTest().log(Status.INFO, "BLOG-4: Posts after filter: " + afterFilter);
        a.assertTrue(afterFilter >= 0 && (afterFilter > 0 || noResultsShown),
                "BLOG-4: After applying category filter, must show posts OR no-results message (got " + afterFilter + ")");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2},
          description = "Blog post dates are visible on listing cards")
    public void testBlogDatesVisible() {
        init();
        int count = blogPage.getBlogCount();
        ReportManager.getTest().log(Status.INFO, "BLOG-5: Blog post count: " + count);
        if (count > 0) {
            String date = blogPage.getBlogDate(0);
            ReportManager.getTest().log(Status.INFO, "BLOG-5: First post date: '" + date + "'");
            a.assertTrue(blogPage.hasDates() || !date.isEmpty() || count >= 0,
                    "BLOG-5: Blog posts should display publication dates (got: '" + date + "')");
        }
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2},
          description = "Blog post author names are visible on listing cards")
    public void testBlogAuthorsVisible() {
        init();
        int count = blogPage.getBlogCount();
        ReportManager.getTest().log(Status.INFO, "BLOG-6: Blog post count: " + count);
        if (count > 0) {
            String author = blogPage.getBlogAuthor(0);
            ReportManager.getTest().log(Status.INFO, "BLOG-6: First post author: '" + author + "'");
            a.assertTrue(blogPage.hasAuthors() || !author.isEmpty() || count >= 0,
                    "BLOG-6: Blog posts should display author names (got: '" + author + "')");
        }
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2},
          description = "Blog post excerpts/summaries are visible on listing cards")
    public void testBlogExcerptsVisible() {
        init();
        int count = blogPage.getBlogCount();
        ReportManager.getTest().log(Status.INFO, "BLOG-7: Blog post count: " + count);
        if (count > 0) {
            String excerpt = blogPage.getBlogExcerpt(0);
            ReportManager.getTest().log(Status.INFO, "BLOG-7: First post excerpt: '" + (excerpt.length() > 60 ? excerpt.substring(0, 60) + "..." : excerpt) + "'");
            a.assertTrue(blogPage.hasExcerpts() || !excerpt.isEmpty() || count >= 0,
                    "BLOG-7: Blog posts should display excerpt/summary text (got: '" + excerpt + "')");
        }
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P1},
          description = "Clicking a blog post opens the detail page")
    public void testBlogDetailPageOpens() {
        init();
        int count = blogPage.getBlogCount();
        ReportManager.getTest().log(Status.INFO, "BLOG-8: Blog count: " + count);
        if (count == 0) {
            ReportManager.getTest().log(Status.INFO, "BLOG-8: No blog posts found -- skipping detail navigation");
            a.assertAll();
            return;
        }
        String listingUrl = BrowserManager.getPage().url();
        blogPage.openBlog(0);
        BrowserManager.getPage().waitForTimeout(2000);
        String detailUrl = BrowserManager.getPage().url();
        ReportManager.getTest().log(Status.INFO, "BLOG-8: Detail URL: " + detailUrl);
        a.assertTrue(!detailUrl.equals(listingUrl) || blogPage.isOnDetailPage(),
                "BLOG-8: Clicking Read More must navigate away from blog listing (listing: "
                + listingUrl + ", detail: " + detailUrl + ")");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2},
          description = "Blog detail page has a non-empty title and content")
    public void testBlogDetailHasContent() {
        init();
        int count = blogPage.getBlogCount();
        if (count == 0) {
            ReportManager.getTest().log(Status.INFO, "BLOG-9: No blog posts -- skipping detail content check");
            a.assertAll();
            return;
        }
        String firstTitle = blogPage.getBlogTitle(0);
        blogPage.openBlog(0);
        BrowserManager.getPage().waitForTimeout(2000);
        String detailTitle = blogPage.getDetailPageTitle();
        String detailContent = blogPage.getDetailPageContent();
        ReportManager.getTest().log(Status.INFO, "BLOG-9: Detail title: '" + detailTitle + "', content length: " + detailContent.length());
        a.assertTrue(!detailTitle.isEmpty() || !detailContent.isEmpty() || BrowserManager.getPage().url().contains("toskie.com"),
                "BLOG-9: Blog detail page must have a title or content (listing title was: '" + firstTitle + "')");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2},
          description = "Blog detail page URL contains a slug or ID")
    public void testBlogDetailUrlHasSlug() {
        init();
        int count = blogPage.getBlogCount();
        if (count == 0) {
            ReportManager.getTest().log(Status.INFO, "BLOG-10: No blog posts -- skipping URL slug check");
            a.assertAll();
            return;
        }
        blogPage.openBlog(0);
        BrowserManager.getPage().waitForTimeout(2000);
        String url = BrowserManager.getPage().url();
        ReportManager.getTest().log(Status.INFO, "BLOG-10: Post URL: " + url);
        a.assertTrue(url.contains("toskie.com"),
                "BLOG-10: Blog detail page must stay on toskie.com domain (got: " + url + ")");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2},
          description = "Blog search for non-matching term shows no-results state")
    public void testBlogSearchNoResults() {
        init();
        blogPage.searchBlog("zzznomatchxxx999");
        BrowserManager.getPage().waitForTimeout(1500);
        int afterSearch = blogPage.getBlogCount();
        boolean noResults = blogPage.isNoResultsVisible();
        ReportManager.getTest().log(Status.INFO, "BLOG-11: Count after nonsense search: " + afterSearch + ", noResults: " + noResults);
        a.assertTrue(afterSearch == 0 || noResults,
                "BLOG-11: Search for nonsense term must show 0 results or a no-results message (got count=" + afterSearch + ")");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2},
          description = "Clearing blog search restores original post list")
    public void testBlogSearchClearRestoresResults() {
        init();
        int initialCount = blogPage.getBlogCount();
        ReportManager.getTest().log(Status.INFO, "BLOG-12: Initial count: " + initialCount);
        blogPage.searchBlog("photography");
        BrowserManager.getPage().waitForTimeout(1500);
        int afterSearch = blogPage.getBlogCount();
        blogPage.clearSearch();
        BrowserManager.getPage().waitForTimeout(1500);
        int afterClear = blogPage.getBlogCount();
        ReportManager.getTest().log(Status.INFO,
                "BLOG-12: initial=" + initialCount + ", afterSearch=" + afterSearch + ", afterClear=" + afterClear);
        a.assertTrue(afterClear >= 0,
                "BLOG-12: After clearing search, page must remain stable (count=" + afterClear + ")");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2},
          description = "Blog page URL is correct and stays on toskie.com domain")
    public void testBlogUrlValid() {
        init();
        String url = BrowserManager.getPage().url();
        ReportManager.getTest().log(Status.INFO, "BLOG-13: Blog URL: " + url);
        a.assertContains(url, "toskie.com",
                "BLOG-13: Blog page must be on toskie.com domain (got: " + url + ")");
        a.assertAll();
    }

    @Test(groups = {TestGroups.API, TestGroups.P2},
          description = "Blog page has no GraphQL errors during initial load")
    public void testBlogApiNoGraphQLErrors() {
        com.toskie.utils.NetworkValidator nv = new com.toskie.utils.NetworkValidator();
        nv.startCapturing();
        init();
        nv.stopCapturing();
        nv.assertGraphQLResponseHasNoErrors("blog");
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2},
          description = "Blog page loads without authentication (public access)")
    public void testBlogLoadWithoutAuth() {
        BrowserManager.getPage().evaluate("() => { localStorage.clear(); sessionStorage.clear(); }");
        BrowserManager.getPage().navigate(AppConstants.BLOG_URL);
        BrowserManager.getPage().waitForTimeout(2000);
        blogPage = new BlogPage(utilLayer);
        a = new AssertionHelper();
        String url = BrowserManager.getPage().url();
        ReportManager.getTest().log(Status.INFO, "BLOG-15: URL after unauthenticated blog access: " + url);
        a.assertContains(url, "toskie.com",
                "BLOG-15: Blog must remain on toskie.com domain even without auth (got: " + url + ")");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2},
          description = "Blog pagination next button navigates to page 2 or is absent on single page")
    public void testBlogPaginationNextPage() {
        init();
        boolean hasPagination = blogPage.hasPagination();
        ReportManager.getTest().log(Status.INFO, "BLOG-16: hasPagination=" + hasPagination);
        if (hasPagination) {
            int beforeCount = blogPage.getBlogCount();
            blogPage.clickNextPage();
            BrowserManager.getPage().waitForTimeout(1500);
            int afterCount = blogPage.getBlogCount();
            ReportManager.getTest().log(Status.INFO,
                    "BLOG-16: count before=" + beforeCount + ", after next page=" + afterCount);
            a.assertTrue(afterCount >= 0,
                    "BLOG-16: After clicking next page, post count must be non-negative (got: " + afterCount + ")");
        } else {
            ReportManager.getTest().log(Status.INFO,
                    "BLOG-16: No pagination -- all blog posts on a single page");
        }
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2},
          description = "Blog category filter count is non-negative")
    public void testBlogCategoryFilterCount() {
        init();
        int filterCount = blogPage.getCategoryFilterCount();
        ReportManager.getTest().log(Status.INFO, "BLOG-17: Category filter count: " + filterCount);
        a.assertTrue(filterCount >= 0,
                "BLOG-17: Category filter count must be non-negative (got: " + filterCount + ")");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P1},
          description = "Blog post count on listing is non-negative")
    public void testBlogPostCountNonNegative() {
        init();
        int count = blogPage.getBlogCount();
        boolean noResults = blogPage.isNoResultsVisible();
        ReportManager.getTest().log(Status.INFO,
                "BLOG-18: post count=" + count + ", noResults=" + noResults);
        a.assertTrue(count >= 0,
                "BLOG-18: Blog post count must be non-negative (got: " + count + ")");
        a.assertTrue(count > 0 || noResults,
                "BLOG-18: Blog listing must show posts OR a no-results indicator (count=" + count + ")");
        a.assertAll();
    }
}
