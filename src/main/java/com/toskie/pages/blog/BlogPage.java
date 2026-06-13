package com.toskie.pages.blog;
import com.aventstack.extentreports.Status;
import com.microsoft.playwright.*;
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
    public void searchBlog(String q)      { util.fill(searchInput, q, "Blog Search"); searchInput.press("Enter"); }
    public void filterByCategory(int idx) { categoryFilters.nth(idx).click(); }
    public void openBlog(int idx)         { readMoreBtns.nth(idx).click(); }
    public boolean isNoResultsVisible()   { try { return noResultsMsg.isVisible(); } catch (Exception e) { return false; } }
    public List<String> getAllTitles() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < blogTitles.count(); i++) list.add(blogTitles.nth(i).textContent().trim());
        return list;
    }
}
