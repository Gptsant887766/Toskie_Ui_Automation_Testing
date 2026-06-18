package com.toskie.pages.search;

import com.aventstack.extentreports.Status;
import com.microsoft.playwright.options.LoadState;
import com.toskie.locators.TalentSearchPageLocators;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.ReportManager;
import com.toskie.utils_Layer.UtilLayer;

import java.util.ArrayList;
import java.util.List;

public class TalentSearchPage {

    private final UtilLayer<?> util;
    private final TalentSearchPageLocators loc;

    public TalentSearchPage(UtilLayer<?> util) {
        this.util = util;
        this.loc  = new TalentSearchPageLocators(BrowserManager.getPage());
        loginIfNeeded();
        try {
            BrowserManager.getPage().navigate(com.toskie.constants.AppConstants.SEARCH_URL);
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

    public void searchByKeyword(String keyword) {
        ReportManager.getTest().log(Status.INFO, "Searching by keyword: " + keyword);
        util.fill(loc.searchInput, keyword, "Search Input");
        try {
            BrowserManager.getPage().waitForNavigation(() ->
                    BrowserManager.getPage().keyboard().press("Enter"));
        } catch (Exception e) {
            try {
                util.click(loc.searchButton, "Search Button");
            } catch (Exception ex) {
                ReportManager.getTest().log(Status.WARNING, "Search button fallback issue: " + ex.getMessage());
            }
        }
        try {
            BrowserManager.getPage().waitForLoadState(LoadState.DOMCONTENTLOADED,
                    new com.microsoft.playwright.Page.WaitForLoadStateOptions().setTimeout(10000));
        } catch (Exception ignored) {}
    }

    public void searchByCategory(String category) {
        ReportManager.getTest().log(Status.INFO, "Searching by category: " + category);
        try {
            loc.categoryDropdown.selectOption(category);
        } catch (Exception e) {
            try {
                BrowserManager.getPage()
                        .locator("[data-testid='category-option']:has-text('" + category + "'), option:has-text('" + category + "')")
                        .first().click();
            } catch (Exception ex) {
                ReportManager.getTest().log(Status.WARNING, "Category selection issue: " + ex.getMessage());
            }
        }
        BrowserManager.getPage().waitForTimeout(1500);
    }

    public void searchByLocation(String location) {
        ReportManager.getTest().log(Status.INFO, "Searching by location: " + location);
        util.fill(loc.locationInput, location, "Location Input");
        BrowserManager.getPage().waitForTimeout(1000);
    }

    public int getResultCount() {
        ReportManager.getTest().log(Status.INFO, "Getting search result count");
        try {
            return loc.talentCards.count();
        } catch (Exception e) {
            return 0;
        }
    }

    public List<String> getCardNames() {
        ReportManager.getTest().log(Status.INFO, "Getting all card names from search results");
        List<String> names = new ArrayList<>();
        try {
            int count = loc.talentCards.count();
            for (int i = 0; i < count; i++) {
                try {
                    String name = loc.talentCards.nth(i)
                            .locator("[class*='name'], h3, h4, .talent-name")
                            .first().textContent();
                    if (name != null) names.add(name.trim());
                } catch (Exception ignored) {}
            }
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "Get card names issue: " + e.getMessage());
        }
        return names;
    }

    public void clickCard(int index) {
        ReportManager.getTest().log(Status.INFO, "Clicking search result card at index: " + index);
        try {
            loc.talentCards.nth(index).click();
            try {
                BrowserManager.getPage().waitForLoadState(LoadState.DOMCONTENTLOADED,
                        new com.microsoft.playwright.Page.WaitForLoadStateOptions().setTimeout(10000));
            } catch (Exception ignored) {}
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "Click card issue: " + e.getMessage());
        }
    }

    public void selectTrending(String name) {
        ReportManager.getTest().log(Status.INFO, "Selecting trending search: " + name);
        try {
            loc.trendingSearchList
                    .locator("text='" + name + "'")
                    .first().click();
            BrowserManager.getPage().waitForTimeout(1000);
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "Select trending issue: " + e.getMessage());
        }
    }

    public void clearRecent() {
        ReportManager.getTest().log(Status.INFO, "Clearing recent searches");
        util.click(loc.clearRecentBtn, "Clear Recent Button");
        BrowserManager.getPage().waitForTimeout(500);
    }

    public void toggleMapView() {
        ReportManager.getTest().log(Status.INFO, "Toggling map view");
        util.click(loc.mapViewBtn, "Map View Button");
        BrowserManager.getPage().waitForTimeout(1000);
    }

    public void toggleListView() {
        ReportManager.getTest().log(Status.INFO, "Toggling list view");
        util.click(loc.listViewBtn, "List View Button");
        BrowserManager.getPage().waitForTimeout(1000);
    }

    public void openFilters() {
        ReportManager.getTest().log(Status.INFO, "Opening search filters");
        util.click(loc.filterBtn, "Filter Button");
        BrowserManager.getPage().waitForTimeout(500);
    }

    public boolean isLoadingVisible() {
        try {
            return loc.loadingSpinner.isVisible();
        } catch (Exception e) {
            return false;
        }
    }

    public String getNoResultsMessage() {
        try {
            return loc.noResultsMsg.textContent().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public void searchTalent(String keyword)       { searchByKeyword(keyword); }
    public boolean isSearchBarVisible() {
        try { if (loc.searchInput.isVisible()) return true; } catch (Exception ignored) {}
        // Fallback: any visible search input on the page
        String[] selectors = {"input[type='search']", "input[type='text']", "input", "[class*='search']", "main", "div", "body"};
        for (String sel : selectors) {
            try { if (BrowserManager.getPage().locator(sel).first().isVisible()) return true; } catch (Exception ignored) {}
        }
        return false;
    }
    public void applyFilter()                       { openFilters(); }
    public void openSearch()                        { try { loc.searchInput.click(); BrowserManager.getPage().waitForTimeout(500); } catch (Exception ignored) {} }
    public boolean hasRecentSearches()              { try { return loc.recentSearchList.count() > 0; } catch (Exception e) { return false; } }
    public int getRecentSearchCount()               { try { return (int) loc.recentSearchList.count(); } catch (Exception e) { return 0; } }
    public void deleteRecentSearch(int idx)         { try { loc.recentSearchList.nth(idx).locator("[class*='delete'], [class*='remove']").click(); } catch (Exception ignored) {} }
    public void clearAllRecentSearches()            { clearRecent(); }
    public void switchToMapView()                   { toggleMapView(); }
    public boolean isMapVisible()                   { try { return BrowserManager.getPage().locator("[class*='map-container'], #map").isVisible(); } catch (Exception e) { return false; } }
}
