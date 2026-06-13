package com.toskie.pages.activity;
import com.aventstack.extentreports.Status;
import com.microsoft.playwright.*;
import com.toskie.utils_Layer.*;
import java.util.*;

public class ActivityHistoryPage {
    private final UtilLayer<?> util;
    private final Locator activityItems, activityTypes, activityDates, filterDropdown,
            loadMoreBtn, noActivityMsg, searchInput;

    public ActivityHistoryPage(UtilLayer<?> util) {
        this.util  = util;
        Page page  = BrowserManager.getPage();
        activityItems  = page.locator("[class*='activity-item'], [class*='history-item']");
        activityTypes  = page.locator("[class*='activity-type'], [class*='activity-label']");
        activityDates  = page.locator("[class*='activity-date'], [class*='time-stamp']");
        filterDropdown = page.locator("select[name*='filter'], [class*='activity-filter']").first();
        loadMoreBtn    = page.locator("//button[contains(.,'Load More')] | //button[contains(.,'Show More')]").first();
        noActivityMsg  = page.locator("[class*='empty'], [class*='no-activity']").first();
        searchInput    = page.locator("input[placeholder*='search activity' i]").first();
    }

    public int getActivityCount()          { return (int) activityItems.count(); }
    public boolean isNoActivityVisible()   { try { return noActivityMsg.isVisible(); } catch (Exception e) { return false; } }
    public void clickLoadMore()            { util.click(loadMoreBtn, "Load More Activities"); }
    public String getActivityType(int idx) { try { return activityTypes.nth(idx).textContent().trim(); } catch (Exception e) { return ""; } }
    public List<String> getAllActivityTypes() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < activityTypes.count(); i++) list.add(activityTypes.nth(i).textContent().trim());
        return list;
    }
    public void searchActivity(String q)   { util.fill(searchInput, q, "Activity Search"); }
}
