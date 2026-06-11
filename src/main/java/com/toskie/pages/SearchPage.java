package com.toskie.pages;

import com.aventstack.extentreports.Status;
import com.toskie.locators.SearchPageLocators;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.ReportManager;
import com.toskie.utils_Layer.UtilLayer;
import com.toskie.utils_Layer.WaitManager;

public class SearchPage {

    private final UtilLayer<?> util;
    private final SearchPageLocators loc;

    public SearchPage(UtilLayer<?> util) {
        this.util = util;
        this.loc  = new SearchPageLocators(BrowserManager.getPage());
    }

    public void enterSearchQuery(String query) {
        util.fill(loc.searchInput, query, "Search Input");
        ReportManager.getTest().log(Status.INFO, "Search query: " + query);
    }

    public void submitSearch() {
        util.click(loc.searchSubmitButton, "Search Submit");
        WaitManager.safePageLoad();
        BrowserManager.getPage().waitForTimeout(2000);
    }

    public void searchFor(String query) {
        enterSearchQuery(query);
        BrowserManager.getPage().keyboard().press("Enter");
        WaitManager.safePageLoad();
        BrowserManager.getPage().waitForTimeout(2000);
        ReportManager.getTest().log(Status.INFO, "Searched: " + query);
    }

    public void clearSearch() {
        util.click(loc.searchClearButton, "Clear Search");
        BrowserManager.getPage().waitForTimeout(500);
    }

    public void applySortBy(String sortOption) {
        util.selectByText(loc.sortByDropdown, sortOption);
        BrowserManager.getPage().waitForTimeout(1500);
        ReportManager.getTest().log(Status.INFO, "Sort by: " + sortOption);
    }

    public void openFilterPanel() {
        try {
            util.click(loc.applyFiltersButton, "Open Filter Panel");
        } catch (Exception e) {
            BrowserManager.getPage().locator("button:has-text('Filter')").click();
        }
        BrowserManager.getPage().waitForTimeout(800);
    }

    public void setPriceRange(String min, String max) {
        util.fill(loc.priceMinInput, min, "Min Price");
        util.fill(loc.priceMaxInput, max, "Max Price");
    }

    public void applyFilters() {
        util.click(loc.applyFiltersButton, "Apply Filters");
        BrowserManager.getPage().waitForTimeout(2000);
    }

    public void clearFilters() {
        util.click(loc.clearFiltersButton, "Clear Filters");
        BrowserManager.getPage().waitForTimeout(1500);
    }

    public int getResultCount() {
        return loc.searchResultCards.count();
    }

    public String getResultCountText() {
        try { return loc.searchResultCount.textContent().trim(); }
        catch (Exception e) { return "0"; }
    }

    public void clickFirstResult() {
        loc.searchResultCards.first().click();
        BrowserManager.getPage().waitForTimeout(1500);
    }

    public void clickResultAtIndex(int index) {
        loc.searchResultCards.nth(index).click();
        BrowserManager.getPage().waitForTimeout(1500);
    }

    public boolean isNoResultsVisible() {
        try { return loc.noResultsMessage.isVisible(); }
        catch (Exception e) { return false; }
    }

    public boolean isSearchSuggestionVisible() {
        try { return loc.searchSuggestions.isVisible(); }
        catch (Exception e) { return false; }
    }

    public boolean isResultsLoaded() {
        try {
            BrowserManager.getPage().waitForTimeout(2000);
            return loc.searchResultCards.count() > 0 || loc.noResultsMessage.isVisible();
        } catch (Exception e) {
            return false;
        }
    }
}
