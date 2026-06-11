package com.toskie.locators;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class SearchPageLocators {

    public final Locator searchInput;
    public final Locator searchSubmitButton;
    public final Locator searchClearButton;
    public final Locator searchSuggestions;
    public final Locator recentSearches;
    public final Locator clearRecentSearches;
    public final Locator popularSearches;
    public final Locator searchResultCount;
    public final Locator searchResultCards;
    public final Locator noResultsMessage;
    public final Locator searchSpinner;

    // ─── Filters ──────────────────────────────────────────────────────────────
    public final Locator filterPanel;
    public final Locator categoryFilter;
    public final Locator locationFilter;
    public final Locator ratingFilter;
    public final Locator ratingFilterStars;
    public final Locator priceMinInput;
    public final Locator priceMaxInput;
    public final Locator availabilityFilter;
    public final Locator genderFilter;
    public final Locator sortByDropdown;
    public final Locator sortByRelevance;
    public final Locator sortByRating;
    public final Locator sortByPrice;
    public final Locator sortByDistance;
    public final Locator applyFiltersButton;
    public final Locator clearFiltersButton;
    public final Locator activeFiltersCount;

    // ─── Result Cards ─────────────────────────────────────────────────────────
    public final Locator resultCardName;
    public final Locator resultCardCategory;
    public final Locator resultCardRating;
    public final Locator resultCardReviewCount;
    public final Locator resultCardLocation;
    public final Locator resultCardDistance;
    public final Locator resultCardPrice;
    public final Locator resultCardAvailability;
    public final Locator resultCardViewButton;

    // ─── Pagination ───────────────────────────────────────────────────────────
    public final Locator paginationContainer;
    public final Locator nextPageButton;
    public final Locator prevPageButton;
    public final Locator pageNumbers;

    public SearchPageLocators(Page page) {
        searchInput        = page.locator("input[type='search'], input[placeholder*='search' i], [class*='search-input'] input");
        searchSubmitButton = page.locator("button[type='submit'], [class*='search-btn'], [aria-label='search']");
        searchClearButton  = page.locator("button[aria-label='clear'], [class*='clear-search'], input+button");
        searchSuggestions  = page.locator("[class*='suggestions'], [class*='autocomplete'], [role='listbox']");
        recentSearches     = page.locator("[class*='recent-searches'], [class*='search-history']");
        clearRecentSearches= page.locator("button:has-text('Clear'), [class*='clear-history']");
        popularSearches    = page.locator("[class*='popular-searches'], [class*='trending']");
        searchResultCount  = page.locator("[class*='result-count'], [class*='total-results'], p:has-text('results')");
        searchResultCards  = page.locator("[class*='result-card'], [class*='search-result']");
        noResultsMessage   = page.locator("[class*='no-results'], [class*='empty-search'], p:has-text('No results')");
        searchSpinner      = page.locator("[class*='search-loading'], [class*='spinner']");

        // Filters
        filterPanel        = page.locator("[class*='filter-panel'], [class*='filter-drawer'], [class*='filter-modal']");
        categoryFilter     = page.locator("[class*='category-filter'], [name='category'], select:has([value*='category'])");
        locationFilter     = page.locator("[class*='location-filter'], input[placeholder*='location' i]");
        ratingFilter       = page.locator("[class*='rating-filter']");
        ratingFilterStars  = page.locator("[class*='rating-filter'] [class*='star'], [aria-label*='star']");
        priceMinInput      = page.locator("input[name='minPrice'], input[placeholder='Min'], [class*='price-min']");
        priceMaxInput      = page.locator("input[name='maxPrice'], input[placeholder='Max'], [class*='price-max']");
        availabilityFilter = page.locator("[class*='availability-filter'], [name='availability']");
        genderFilter       = page.locator("[class*='gender-filter'], [name='gender']");
        sortByDropdown     = page.locator("select[name='sortBy'], [class*='sort-dropdown'], [aria-label*='sort' i]");
        sortByRelevance    = page.locator("option:has-text('Relevance'), [class*='sort']:has-text('Relevant')");
        sortByRating       = page.locator("option:has-text('Rating'), [class*='sort']:has-text('Rating')");
        sortByPrice        = page.locator("option:has-text('Price'), [class*='sort']:has-text('Price')");
        sortByDistance     = page.locator("option:has-text('Distance'), [class*='sort']:has-text('Nearby')");
        applyFiltersButton = page.locator("button:has-text('Apply'), button:has-text('Search')");
        clearFiltersButton = page.locator("button:has-text('Clear'), button:has-text('Reset All')");
        activeFiltersCount = page.locator("[class*='filter-count'], [class*='active-filters']");

        // Result card details
        resultCardName        = page.locator("[class*='result-card'] [class*='name']");
        resultCardCategory    = page.locator("[class*='result-card'] [class*='category']");
        resultCardRating      = page.locator("[class*='result-card'] [class*='rating']");
        resultCardReviewCount = page.locator("[class*='result-card'] [class*='review-count']");
        resultCardLocation    = page.locator("[class*='result-card'] [class*='location']");
        resultCardDistance    = page.locator("[class*='result-card'] [class*='distance']");
        resultCardPrice       = page.locator("[class*='result-card'] [class*='price']");
        resultCardAvailability= page.locator("[class*='result-card'] [class*='available'], [class*='result-card'] [class*='busy']");
        resultCardViewButton  = page.locator("[class*='result-card'] button:has-text('View'), [class*='result-card'] a:has-text('View')");

        // Pagination
        paginationContainer = page.locator("[class*='pagination'], nav[aria-label*='pagination' i]");
        nextPageButton      = page.locator("button[aria-label='Next page'], [class*='pagination-next']");
        prevPageButton      = page.locator("button[aria-label='Previous page'], [class*='pagination-prev']");
        pageNumbers         = page.locator("[class*='page-number'], [aria-label*='Page ']");
    }
}
