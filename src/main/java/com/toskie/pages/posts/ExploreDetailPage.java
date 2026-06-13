package com.toskie.pages.posts;

import com.aventstack.extentreports.Status;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.ReportManager;
import com.toskie.utils_Layer.UtilLayer;

public class ExploreDetailPage {

    private final UtilLayer<?> util;

    public ExploreDetailPage(UtilLayer<?> util) {
        this.util = util;
    }

    public boolean isLoaded() {
        try {
            return BrowserManager.getPage()
                    .locator("[data-testid='explore-detail'], .explore-detail, .post-detail-view, .post-detail")
                    .isVisible();
        } catch (Exception e) {
            return false;
        }
    }

    public String getContent() {
        ReportManager.getTest().log(Status.INFO, "Getting explore detail content");
        try {
            return BrowserManager.getPage()
                    .locator("[data-testid='post-content'], .post-content, .post-text, .explore-content")
                    .first().textContent().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public void clickBack() {
        ReportManager.getTest().log(Status.INFO, "Clicking Back on explore detail");
        try {
            BrowserManager.getPage()
                    .locator("button[aria-label='Back'], button:has-text('Back'), [data-testid='back-btn'], .back-button")
                    .first().click();
            BrowserManager.getPage().waitForTimeout(1000);
        } catch (Exception e) {
            BrowserManager.getPage().goBack();
        }
    }

    public String getAuthorName() {
        ReportManager.getTest().log(Status.INFO, "Getting author name on explore detail");
        try {
            return BrowserManager.getPage()
                    .locator("[data-testid='author-name'], .author-name, .post-author, .creator-name")
                    .first().textContent().trim();
        } catch (Exception e) {
            return "";
        }
    }
}
