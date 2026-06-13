package com.toskie.components;
import com.microsoft.playwright.*;
import com.toskie.utils_Layer.*;
import java.util.*;

public class SearchComponent {
    private final UtilLayer<?> util;
    private final Locator searchInput, searchBtn, clearBtn, suggestions, recentSearches, trendingSearches;

    public SearchComponent(UtilLayer<?> util) {
        this.util = util;
        Page page = BrowserManager.getPage();
        searchInput     = page.locator("input[placeholder*='search' i], input[type='search']").first();
        searchBtn       = page.locator("//button[contains(.,'Search')] | [class*='search-btn']").first();
        clearBtn        = page.locator("[class*='clear-search'], [aria-label='clear search']").first();
        suggestions     = page.locator("[class*='suggestion'], [class*='autocomplete-item']");
        recentSearches  = page.locator("[class*='recent-search'] li, [class*='recent'] [class*='item']");
        trendingSearches= page.locator("[class*='trending'] li, [class*='trending-search']");
    }

    public void search(String keyword) { util.fill(searchInput, keyword, "Search Input"); searchInput.press("Enter"); }
    public void clearSearch()          { util.click(clearBtn, "Clear Search"); }
    public int getSuggestionCount()    { return (int) suggestions.count(); }
    public void selectSuggestion(int i){ suggestions.nth(i).click(); }
    public List<String> getTrending()  {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < trendingSearches.count(); i++) list.add(trendingSearches.nth(i).textContent().trim());
        return list;
    }
}
