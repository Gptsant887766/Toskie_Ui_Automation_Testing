package com.toskie.pages;

import com.aventstack.extentreports.Status;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.LoadState;
import com.toskie.locators.HomePageLocators;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.ReportManager;
import com.toskie.utils_Layer.UtilLayer;
import com.toskie.utils_Layer.WaitManager;


public class HomePage {

    private final UtilLayer<?> util;
    private final HomePageLocators loc;

    public HomePage(UtilLayer<?> util) {
        this.util = util;
        this.loc  = new HomePageLocators(BrowserManager.getPage());
    }

    // ─── Page verification ────────────────────────────────────────────────────

    public boolean isOnHomePage() {
        try {
            WaitManager.safePageLoad();
            // DOM check: talent cards or search bar visible
            if (loc.talentCards.count() > 0 || loc.searchBar.isVisible()) return true;
            // Bottom nav check: indicates authenticated app state
            try {
                if (loc.bottomNavHome.isVisible() || loc.bottomNavSearch.isVisible()) return true;
            } catch (Exception ignored) {}
            // Auth-token check: if token in localStorage + not on auth/onboard page → home
            String url = BrowserManager.getPage().url();
            if (url.contains("/login") || url.contains("/auth") || url.contains("/onboard")
                    || url.contains("/welcome") || url.contains("/register")) return false;
            Object token = BrowserManager.getPage()
                    .evaluate("() => localStorage.getItem('access_token')");
            return token != null && !"null".equals(token.toString()) && !token.toString().isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public void waitForHomePageLoad() {
        try {
            WaitManager.waitForPageLoad(LoadState.DOMCONTENTLOADED);
            WaitManager.waitForCondition(() ->
                loc.talentCards.count() > 0 || loc.searchBar.isVisible(),
                3, 500);
            ReportManager.getTest().log(Status.INFO, "Home page loaded.");
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "Home page load check partially failed: " + e.getMessage());
        }
    }

    // ─── Navigation ──────────────────────────────────────────────────────────

    public void navigateToSearch() {
        util.click(loc.bottomNavSearch, "Search Nav");
        WaitManager.waitForElementVisible(loc.bottomNavSearch, "Search Nav after click");
    }

    public void navigateToChat() {
        util.click(loc.bottomNavChat, "Chat Nav");
        WaitManager.waitForElementVisible(loc.bottomNavChat, "Chat Nav after click");
    }

    public void navigateToProfile() {
        util.click(loc.bottomNavProfile, "Profile Nav");
        WaitManager.waitForElementVisible(loc.bottomNavProfile, "Profile Nav after click");
    }

    public void clickNotifications() {
        util.click(loc.notificationsIcon, "Notifications Icon");
        WaitManager.waitForElementVisible(loc.notificationsIcon, "Notifications icon after click");
    }

    // ─── Search ───────────────────────────────────────────────────────────────

    public void searchFor(String query) {
        util.click(loc.searchBar, "Search Bar");
        util.fill(loc.searchBar, query, "Search Input");
        try {
            BrowserManager.getPage().waitForNavigation(() ->
                BrowserManager.getPage().keyboard().press("Enter"));
        } catch (Exception ignored) {}
        WaitManager.waitForPageLoad(LoadState.DOMCONTENTLOADED);
        ReportManager.getTest().log(Status.INFO, "Searched for: " + query);
    }

    // ─── Filter ───────────────────────────────────────────────────────────────

    public void openFilters() {
        util.click(loc.filterButton, "Filter Button");
        WaitManager.waitForElementVisible(loc.filterButton, "Filter button after click");
    }

    public void applyFilters() {
        util.click(loc.filterApplyButton, "Apply Filters");
        WaitManager.waitForPageLoad(LoadState.DOMCONTENTLOADED);
    }

    public void resetFilters() {
        util.click(loc.filterResetButton, "Reset Filters");
        WaitManager.waitForElementVisible(loc.filterResetButton, "Filter reset button after click");
    }

    public void selectCategory(String categoryName) {
        try {
            Locator chip = BrowserManager.getPage()
                .locator("[class*='chip']:has-text('" + categoryName + "')");
            util.click(chip, "Category: " + categoryName);
            BrowserManager.getPage().waitForTimeout(1500);
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "Category not found: " + categoryName);
        }
    }

    // ─── Talent Cards ─────────────────────────────────────────────────────────

    public int getTalentCardCount() {
        return loc.talentCards.count();
    }

    public void clickFirstTalentCard() {
        util.click(loc.firstTalentCard, "First Talent Card");
        try {
            BrowserManager.getPage().waitForNavigation(() -> {});
        } catch (Exception ignored) {}
        WaitManager.waitForPageLoad(LoadState.DOMCONTENTLOADED);
    }

    public void clickViewProfile(int index) {
        Locator cards = loc.talentCards;
        if (index < cards.count()) {
            try {
                BrowserManager.getPage().waitForNavigation(() ->
                    cards.nth(index).click());
            } catch (Exception ignored) {}
            WaitManager.waitForPageLoad(LoadState.DOMCONTENTLOADED);
            ReportManager.getTest().log(Status.INFO, "Opened talent card index: " + index);
        }
    }

    public void clickBookOnCard(int index) {
        Locator bookBtns = loc.talentCardBookButton;
        if (index < bookBtns.count()) {
            bookBtns.nth(index).click();
            WaitManager.waitForElementVisible(bookBtns.nth(index), "Book button after click");
        }
    }

    public void saveTalent(int index) {
        Locator saveBtns = loc.talentCardSaveButton;
        if (index < saveBtns.count()) {
            saveBtns.nth(index).click();
            WaitManager.waitForElementVisible(saveBtns.nth(index), "Save button after click");
            ReportManager.getTest().log(Status.INFO, "Saved talent at index: " + index);
        }
    }

    public String getFirstCardName() {
        try { return loc.talentCardName.first().textContent().trim(); }
        catch (Exception e) { return ""; }
    }

    public String getFirstCardCategory() {
        try { return loc.talentCardCategory.first().textContent().trim(); }
        catch (Exception e) { return ""; }
    }

    // ─── Scroll / Pagination ──────────────────────────────────────────────────

    public void scrollDownToLoadMore() {
        util.scrollToBottom();
        WaitManager.waitForPageLoad(LoadState.DOMCONTENTLOADED);
    }

    public boolean hasMoreCardsAfterScroll(int previousCount) {
        scrollDownToLoadMore();
        return loc.talentCards.count() > previousCount;
    }

    // ─── State checks ─────────────────────────────────────────────────────────

    public boolean isEmptyStateVisible() {
        try { return loc.emptyStateMessage.isVisible(); }
        catch (Exception e) { return false; }
    }

    public boolean isLoadingSpinnerVisible() {
        try { return loc.loadingSpinner.isVisible(); }
        catch (Exception e) { return false; }
    }

    public boolean isNotificationBadgeVisible() {
        try { return loc.notificationBadge.isVisible(); }
        catch (Exception e) { return false; }
    }

    public boolean isProfileCompletionBannerVisible() {
        try { return loc.profileCompletionBanner.isVisible(); }
        catch (Exception e) { return false; }
    }

    public String getToastMessage() {
        try {
            loc.toastMessage.waitFor(
                new Locator.WaitForOptions().setTimeout(10000)
                    .setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE));
            return loc.toastMessage.textContent().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public void verifyHomePageElements() {
        util.verifyElementVisible(loc.searchBar,       "Search Bar");
        util.verifyElementVisible(loc.bottomNavHome,   "Bottom Nav: Home");
        util.verifyElementVisible(loc.bottomNavSearch, "Bottom Nav: Search");
        util.verifyElementVisible(loc.bottomNavChat,   "Bottom Nav: Chat");
        ReportManager.getTest().log(Status.PASS, "Home page key elements verified.");
    }
}
