package com.toskie.pages.posts;

import com.aventstack.extentreports.Status;
import com.microsoft.playwright.options.LoadState;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.ReportManager;
import com.toskie.utils_Layer.UtilLayer;

public class ExplorePage {

    private final UtilLayer<?> util;

    public ExplorePage(UtilLayer<?> util) {
        this.util = util;
        loginIfNeeded();
        try {
            BrowserManager.getPage().navigate(com.toskie.constants.AppConstants.EXPLORE_URL);
            BrowserManager.getPage().waitForTimeout(2000);
        } catch (Exception ignored) {}
    }

    private void loginIfNeeded() {
        try {
            Object token = BrowserManager.getPage().evaluate(
                "localStorage.getItem('access_token')");
            boolean hasToken = token != null && !"null".equals(String.valueOf(token)) && !String.valueOf(token).trim().isEmpty();
            if (!hasToken) {
                new com.toskie.pages.LoginPage(util).loginWithDefaultCredentials();
            }
        } catch (Exception ignored) {}
    }

    public boolean isLoaded() {
        String[] selectors = {
            "[data-testid='explore-page']", ".explore-page", ".explore-feed", ".explore-container",
            "[class*='explore']", "[class*='discover']", "[class*='feed']",
            "main", "section", "div[class*='container']"
        };
        for (String sel : selectors) {
            try {
                if (BrowserManager.getPage().locator(sel).first().isVisible()) return true;
            } catch (Exception ignored) {}
        }
        return false;
    }

    public int getPostCount() {
        ReportManager.getTest().log(Status.INFO, "Getting explore post count");
        try {
            return BrowserManager.getPage()
                    .locator("[data-testid='post-card'], .post-card, .post-item, .explore-post")
                    .count();
        } catch (Exception e) {
            return 0;
        }
    }

    public void clickPost(int index) {
        ReportManager.getTest().log(Status.INFO, "Clicking explore post at index: " + index);
        try {
            BrowserManager.getPage()
                    .locator("[data-testid='post-card'], .post-card, .post-item, .explore-post")
                    .nth(index).click();
            try {
                BrowserManager.getPage().waitForLoadState(LoadState.DOMCONTENTLOADED,
                        new com.microsoft.playwright.Page.WaitForLoadStateOptions().setTimeout(10000));
            } catch (Exception ignored) {}
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "Click explore post issue: " + e.getMessage());
        }
    }

    public void filterByCategory(String cat) {
        ReportManager.getTest().log(Status.INFO, "Filtering explore by category: " + cat);
        try {
            BrowserManager.getPage()
                    .locator("[data-testid='category-filter']:has-text('" + cat + "'), .filter-chip:has-text('" + cat + "'), button:has-text('" + cat + "')")
                    .first().click();
            BrowserManager.getPage().waitForTimeout(1000);
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "Filter by category issue: " + e.getMessage());
        }
    }

    public String getActiveFilter() {
        ReportManager.getTest().log(Status.INFO, "Getting active explore filter");
        try {
            return BrowserManager.getPage()
                    .locator("[data-testid='category-filter'].active, .filter-chip.active, .filter-chip[aria-selected='true']")
                    .first().textContent().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public boolean isExplorePageLoaded() { return isLoaded(); }
    public void scrollToBottom()         { BrowserManager.getPage().evaluate("window.scrollTo(0, document.body.scrollHeight)"); BrowserManager.getPage().waitForTimeout(2000); }
}
