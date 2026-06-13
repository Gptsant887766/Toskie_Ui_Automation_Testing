package com.toskie.pages.search;

import com.aventstack.extentreports.Status;
import com.microsoft.playwright.options.LoadState;
import com.toskie.locators.TalentSearchPageLocators;
import com.toskie.models.TalentCardData;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.ReportManager;
import com.toskie.utils_Layer.UtilLayer;

public class TalentSearchCardsPage {

    private final UtilLayer<?> util;
    private final TalentSearchPageLocators loc;

    public TalentSearchCardsPage(UtilLayer<?> util) {
        this.util = util;
        this.loc  = new TalentSearchPageLocators(BrowserManager.getPage());
    }

    public void verifyCardFields(int index) {
        ReportManager.getTest().log(Status.INFO, "Verifying card fields at index: " + index);
        try {
            String name = loc.talentCards.nth(index)
                    .locator("[class*='name'], h3, h4, .talent-name").first().textContent();
            ReportManager.getTest().log(Status.INFO, "Card name: " + (name != null ? name.trim() : "N/A"));
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "Card name not found at index " + index);
        }
        try {
            String skill = loc.talentCards.nth(index)
                    .locator("[class*='skill'], .skill-tag").first().textContent();
            ReportManager.getTest().log(Status.INFO, "Card skill: " + (skill != null ? skill.trim() : "N/A"));
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "Card skill not found at index " + index);
        }
        try {
            String location = loc.talentCards.nth(index)
                    .locator("[class*='location'], .talent-location").first().textContent();
            ReportManager.getTest().log(Status.INFO, "Card location: " + (location != null ? location.trim() : "N/A"));
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "Card location not found at index " + index);
        }
        try {
            String rating = loc.talentCards.nth(index)
                    .locator("[class*='rating'], .star-rating").first().textContent();
            ReportManager.getTest().log(Status.INFO, "Card rating: " + (rating != null ? rating.trim() : "N/A"));
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "Card rating not found at index " + index);
        }
    }

    public void clickCardConnect(int index) {
        ReportManager.getTest().log(Status.INFO, "Clicking connect on card at index: " + index);
        try {
            loc.talentCards.nth(index)
                    .locator("button:has-text('Connect'), button[aria-label='Connect'], [data-testid='connect-btn']")
                    .first().click();
            BrowserManager.getPage().waitForTimeout(1000);
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "Card connect click issue: " + e.getMessage());
        }
    }

    public boolean isCardImageLoaded(int index) {
        ReportManager.getTest().log(Status.INFO, "Checking if card image is loaded at index: " + index);
        try {
            com.microsoft.playwright.Locator img = loc.talentCards.nth(index)
                    .locator("img").first();
            String src = img.getAttribute("src");
            return src != null && !src.isEmpty() && img.isVisible();
        } catch (Exception e) {
            return false;
        }
    }

    public TalentCardData getCardDetails(int index) {
        ReportManager.getTest().log(Status.INFO, "Getting card details at index: " + index);
        String name = "";
        String skill = "";
        String location = "";
        String rating = "";
        String imageUrl = "";
        try {
            name = loc.talentCards.nth(index)
                    .locator("[class*='name'], h3, h4, .talent-name").first().textContent().trim();
        } catch (Exception ignored) {}
        try {
            skill = loc.talentCards.nth(index)
                    .locator("[class*='skill'], .skill-tag").first().textContent().trim();
        } catch (Exception ignored) {}
        try {
            location = loc.talentCards.nth(index)
                    .locator("[class*='location'], .talent-location").first().textContent().trim();
        } catch (Exception ignored) {}
        try {
            rating = loc.talentCards.nth(index)
                    .locator("[class*='rating'], .star-rating").first().textContent().trim();
        } catch (Exception ignored) {}
        try {
            imageUrl = loc.talentCards.nth(index)
                    .locator("img").first().getAttribute("src");
        } catch (Exception ignored) {}
        return TalentCardData.builder()
                .name(name)
                .skill(skill)
                .location(location)
                .rating(rating)
                .imageUrl(imageUrl)
                .build();
    }
}
