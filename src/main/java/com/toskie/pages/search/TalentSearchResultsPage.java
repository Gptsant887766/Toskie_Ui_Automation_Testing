package com.toskie.pages.search;

import com.aventstack.extentreports.Status;
import com.microsoft.playwright.options.LoadState;
import com.toskie.locators.TalentSearchPageLocators;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.ReportManager;
import com.toskie.utils_Layer.UtilLayer;

public class TalentSearchResultsPage {

    private final UtilLayer<?> util;
    private final TalentSearchPageLocators loc;

    public TalentSearchResultsPage(UtilLayer<?> util) {
        this.util = util;
        this.loc  = new TalentSearchPageLocators(BrowserManager.getPage());
    }

    public String getCardName(int index) {
        ReportManager.getTest().log(Status.INFO, "Getting card name at index: " + index);
        try {
            return loc.talentCards.nth(index)
                    .locator("[class*='name'], h3, h4, .talent-name")
                    .first().textContent().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public String getCardSkill(int index) {
        ReportManager.getTest().log(Status.INFO, "Getting card skill at index: " + index);
        try {
            return loc.talentCards.nth(index)
                    .locator("[class*='skill'], .skill-tag, .talent-skill")
                    .first().textContent().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public String getCardLocation(int index) {
        ReportManager.getTest().log(Status.INFO, "Getting card location at index: " + index);
        try {
            return loc.talentCards.nth(index)
                    .locator("[class*='location'], .talent-location, [data-testid='card-location']")
                    .first().textContent().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public String getCardRating(int index) {
        ReportManager.getTest().log(Status.INFO, "Getting card rating at index: " + index);
        try {
            return loc.talentCards.nth(index)
                    .locator("[class*='rating'], .star-rating, [data-testid='card-rating']")
                    .first().textContent().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public void clickCard(int index) {
        ReportManager.getTest().log(Status.INFO, "Clicking result card at index: " + index);
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

    public int getCardCount() {
        ReportManager.getTest().log(Status.INFO, "Getting result card count");
        try {
            return loc.talentCards.count();
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean isResultsLoaded() {
        try {
            return loc.resultsContainer.isVisible() || loc.talentCards.count() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public int getResultCount()        { return getCardCount(); }
    public boolean isNoResultsVisible(){ try { return loc.noResultsMsg.isVisible(); } catch (Exception e) { return false; } }
    public boolean isPaginationVisible(){ try { return loc.paginationNext.isVisible(); } catch (Exception e) { return false; } }
    public void clickNextPage()        { try { loc.paginationNext.click(); BrowserManager.getPage().waitForTimeout(1500); } catch (Exception ignored) {} }
    public int getCurrentPage()        {
        try {
            String active = BrowserManager.getPage().locator("[class*='page-active'], [aria-current='page']").textContent().trim();
            return Integer.parseInt(active);
        } catch (Exception e) { return 1; }
    }
}
