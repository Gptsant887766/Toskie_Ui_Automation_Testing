package com.toskie.components;
import com.microsoft.playwright.*;
import com.toskie.utils_Layer.*;

public class FilterComponent {
    private final UtilLayer<?> util;
    private final Locator filterBtn, categoryFilter, locationFilter, skillFilter, applyBtn, clearBtn, filterPanel;

    public FilterComponent(UtilLayer<?> util) {
        this.util = util;
        Page page = BrowserManager.getPage();
        filterBtn      = page.locator("//button[contains(.,'Filter')] | [class*='filter-btn']").first();
        filterPanel    = page.locator("[class*='filter-panel'], [class*='filter-drawer']").first();
        categoryFilter = page.locator("[class*='category-filter'], select[name*='category']").first();
        locationFilter = page.locator("[class*='location-filter'], input[placeholder*='location' i]").first();
        skillFilter    = page.locator("[class*='skill-filter'], [placeholder*='skill' i]").first();
        applyBtn       = page.locator("//button[normalize-space()='Apply'] | //button[normalize-space()='Apply Filters']").first();
        clearBtn       = page.locator("//button[normalize-space()='Clear'] | //button[normalize-space()='Reset']").first();
    }

    public void openFilters()   { util.click(filterBtn, "Open Filters"); }
    public void applyFilters()  { util.click(applyBtn, "Apply Filters"); }
    public void clearFilters()  { util.click(clearBtn, "Clear Filters"); }
    public boolean isPanelOpen(){ try { return filterPanel.isVisible(); } catch (Exception e) { return false; } }
}
