package com.toskie.pages.blog;
import com.aventstack.extentreports.Status;
import com.microsoft.playwright.*;
import com.toskie.constants.AppConstants;
import com.toskie.utils_Layer.*;
import java.util.*;

public class BlogPage {
    private final UtilLayer<?> util;
    private final Locator blogCards, blogTitles, blogExcerpts, blogDates, blogAuthors,
            categoryFilters, searchInput, paginationNext, paginationPrev, noResultsMsg, readMoreBtns;

    public BlogPage(UtilLayer<?> util) {
        this.util = util;
        Page page = BrowserManager.getPage();
        blogCards       = page.locator("[class*='blog-card'], [class*='blog-post']");
        blogTitles      = page.locator("[class*='blog-title'], [class*='blog-card'] h2, [class*='blog-card'] h3");
        blogExcerpts    = page.locator("[class*='blog-excerpt'], [class*='blog-summary']");
        blogDates       = page.locator("[class*='blog-date'], [class*='post-date']");
        blogAuthors     = page.locator("[class*='blog-author'], [class*='post-author']");
        categoryFilters = page.locator("[class*='category-filter'] button, [class*='blog-category']");
        searchInput     = page.locator("input[placeholder*='Search blog' i], input[placeholder*='search' i]").first();
        paginationNext  = page.locator("//button[contains(.,'Next')] | [aria-label='Next page']").first();
        paginationPrev  = page.locator("//button[contains(.,'Previous')] | [aria-label='Previous page']").first();
        noResultsMsg    = page.locator("[class*='no-result'], [class*='empty']").first();
        readMoreBtns    = page.locator("//a[contains(.,'Read More')] | //button[contains(.,'Read More')]");
    }

    public int getBlogCount()             { return (int) blogCards.count(); }
    public String getBlogTitle(int idx)   { try { return blogTitles.nth(idx).textContent().trim(); } catch (Exception e) { return ""; } }
    public String getBlogExcerpt(int idx) { try { return blogExcerpts.nth(idx).textContent().trim(); } catch (Exception e) { return ""; } }
    public String getBlogDate(int idx)    { try { return blogDates.nth(idx).textContent().trim(); } catch (Exception e) { return ""; } }
    public String getBlogAuthor(int idx)  { try { return blogAuthors.nth(idx).textContent().trim(); } catch (Exception e) { return ""; } }
    public boolean hasDates()             { try { return blogDates.count() > 0; } catch (Exception e) { return false; } }
    public boolean hasAuthors()           { try { return blogAuthors.count() > 0; } catch (Exception e) { return false; } }
    public boolean hasExcerpts()          { try { return blogExcerpts.count() > 0; } catch (Exception e) { return false; } }
    public boolean hasPagination()        { try { return paginationNext.isVisible(); } catch (Exception e) { return false; } }
    public void clickNextPage()           { try { paginationNext.click(); } catch (Exception e) { ReportManager.getTest().log(com.aventstack.extentreports.Status.INFO, "Pagination next not available: " + e.getMessage()); } }
    public void searchBlog(String q)      { util.fill(searchInput, q, "Blog Search"); searchInput.press("Enter"); }
    public void clearSearch()             { try { searchInput.clear(); searchInput.press("Enter"); } catch (Exception ignored) {} }
    public void filterByCategory(int idx) { categoryFilters.nth(idx).click(); }
    public int getCategoryFilterCount()   { try { return (int) categoryFilters.count(); } catch (Exception e) { return 0; } }
    public void openBlog(int idx)         { readMoreBtns.nth(idx).click(); }
    public boolean isNoResultsVisible()   { try { return noResultsMsg.isVisible(); } catch (Exception e) { return false; } }
    public boolean isOnDetailPage()       { String url = BrowserManager.getPage().url(); return url.contains("/blog/") && url.length() > AppConstants.BLOG_URL.length() + 1; }
    public String getDetailPageTitle()    { try { return BrowserManager.getPage().locator("h1, [class*='blog-detail-title'], article h1").first().textContent().trim(); } catch (Exception e) { return ""; } }
    public String getDetailPageContent()  { try { return BrowserManager.getPage().locator("[class*='blog-content'], [class*='post-content'], article").first().textContent().trim(); } catch (Exception e) { return ""; } }
    public List<String> getAllTitles() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < blogTitles.count(); i++) list.add(blogTitles.nth(i).textContent().trim());
        return list;
    }
}
