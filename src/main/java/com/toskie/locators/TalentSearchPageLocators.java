package com.toskie.locators;
import com.microsoft.playwright.*;

public class TalentSearchPageLocators {
    public final Locator searchInput, searchBtn, categoryDropdown, locationInput, filterBtn,
            resultCards, cardNames, cardRoles, cardLocations, cardRatings, paginationNext,
            paginationPrev, totalResults, noResultsMsg, sortDropdown, loadingSpinner,
            talentCards, searchButton, trendingSearchList, clearRecentBtn, mapViewBtn,
            listViewBtn, recentSearchList, resultsContainer;

    public TalentSearchPageLocators(Page page) {
        searchInput    = page.locator("input[placeholder*='Search talent' i], input[placeholder*='Search' i]").first();
        searchBtn      = page.locator("//button[normalize-space()='Search'] | [class*='search-btn']").first();
        categoryDropdown= page.locator("select[name*='category'], [class*='category-select']").first();
        locationInput  = page.locator("input[placeholder*='Location' i], input[name*='location' i]").first();
        filterBtn      = page.locator("//button[contains(.,'Filter')]").first();
        resultCards    = page.locator("[class*='talent-card'], [class*='search-card']");
        cardNames      = page.locator("[class*='talent-card'] [class*='name'], [class*='talent-name']");
        cardRoles      = page.locator("[class*='talent-card'] [class*='role'], [class*='talent-role']");
        cardLocations  = page.locator("[class*='talent-card'] [class*='location']");
        cardRatings    = page.locator("[class*='talent-card'] [class*='rating']");
        paginationNext = page.locator("//button[contains(.,'Next')] | [aria-label='Next page']").first();
        paginationPrev = page.locator("//button[contains(.,'Previous')] | [aria-label='Previous page']").first();
        totalResults   = page.locator("[class*='result-count'], [class*='total-results']").first();
        noResultsMsg   = page.locator("[class*='no-result'], [class*='empty-state']").first();
        sortDropdown   = page.locator("select[name*='sort'], [class*='sort-select']").first();
        loadingSpinner      = page.locator("[class*='spinner'], [class*='loading']").first();
        talentCards         = resultCards;
        searchButton        = searchBtn;
        trendingSearchList  = page.locator("[class*='trending'], [class*='trending-search']");
        clearRecentBtn      = page.locator("//button[contains(.,'Clear')] | [class*='clear-recent']").first();
        mapViewBtn          = page.locator("//button[contains(.,'Map')] | [class*='map-view-btn'], [aria-label*='map' i]").first();
        listViewBtn         = page.locator("//button[contains(.,'List')] | [class*='list-view-btn'], [aria-label*='list' i]").first();
        recentSearchList    = page.locator("[class*='recent-search'], [class*='recent-searches'] li");
        resultsContainer    = page.locator("[class*='results-container'], [class*='search-results'], [class*='talent-list']").first();
    }
}
