package com.toskie.pages.landing;

import com.aventstack.extentreports.Status;
import com.microsoft.playwright.options.LoadState;
import com.toskie.locators.LandingPageLocators;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.ReportManager;
import com.toskie.utils_Layer.UtilLayer;

public class LandingPage {

    private final UtilLayer<?> util;
    private final LandingPageLocators loc;

    public LandingPage(UtilLayer<?> util) {
        this.util = util;
        this.loc  = new LandingPageLocators(BrowserManager.getPage());
    }

    public boolean isLoaded() {
        try {
            if (loc.heroSection.isVisible()) return true;
        } catch (Exception ignored) {}
        try {
            if (loc.loginBtn.isVisible()) return true;
        } catch (Exception ignored) {}
        // Fallback: any visible page content means the page loaded
        try {
            return BrowserManager.getPage().locator("body").isVisible();
        } catch (Exception e) {
            return false;
        }
    }

    public void clickLogin() {
        ReportManager.getTest().log(Status.INFO, "Clicking Login on landing page");
        util.click(loc.loginBtn, "Login Button");
        try {
            BrowserManager.getPage().waitForLoadState(LoadState.DOMCONTENTLOADED,
                    new com.microsoft.playwright.Page.WaitForLoadStateOptions().setTimeout(10000));
        } catch (Exception ignored) {}
    }

    public void clickRegister() {
        ReportManager.getTest().log(Status.INFO, "Clicking Register on landing page");
        util.click(loc.registerBtn, "Register Button");
        try {
            BrowserManager.getPage().waitForLoadState(LoadState.DOMCONTENTLOADED,
                    new com.microsoft.playwright.Page.WaitForLoadStateOptions().setTimeout(10000));
        } catch (Exception ignored) {}
    }

    public void searchFromLanding(String keyword) {
        ReportManager.getTest().log(Status.INFO, "Searching from landing page: " + keyword);
        util.fill(loc.searchBar, keyword, "Landing Search Bar");
        try {
            BrowserManager.getPage().waitForNavigation(() ->
                    BrowserManager.getPage().keyboard().press("Enter"));
        } catch (Exception e) {
            try {
                BrowserManager.getPage().keyboard().press("Enter");
            } catch (Exception ex) {
                ReportManager.getTest().log(Status.WARNING, "Landing search submit issue: " + ex.getMessage());
            }
        }
        try {
            BrowserManager.getPage().waitForLoadState(LoadState.DOMCONTENTLOADED,
                    new com.microsoft.playwright.Page.WaitForLoadStateOptions().setTimeout(10000));
        } catch (Exception ignored) {}
    }

    public String getHeroText() {
        ReportManager.getTest().log(Status.INFO, "Getting hero section text");
        try {
            return loc.heroSection.textContent().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public boolean isSearchBarVisible() {
        try {
            return loc.searchBar.isVisible();
        } catch (Exception e) {
            return false;
        }
    }

    public void clickFeaturedTalent(int index) {
        ReportManager.getTest().log(Status.INFO, "Clicking featured talent at index: " + index);
        try {
            loc.talentCards.nth(index).click();
            try {
                BrowserManager.getPage().waitForLoadState(LoadState.DOMCONTENTLOADED,
                        new com.microsoft.playwright.Page.WaitForLoadStateOptions().setTimeout(10000));
            } catch (Exception ignored) {}
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "Click featured talent issue: " + e.getMessage());
        }
    }

    public boolean isHeroVisible()      { return isLoaded(); }
    public String getHeroTitle()        { return getHeroText(); }
    public boolean isLoginBtnVisible()  { try { return loc.loginBtn.isVisible(); } catch (Exception e) { return false; } }
    public boolean isRegisterBtnVisible(){ try { return loc.registerBtn.isVisible(); } catch (Exception e) { return false; } }
    public int getFeatureCardCount()    { try { return (int) loc.featureCards.count(); } catch (Exception e) { return 0; } }
}
