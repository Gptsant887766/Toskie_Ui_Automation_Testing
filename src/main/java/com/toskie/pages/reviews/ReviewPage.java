package com.toskie.pages.reviews;

import com.aventstack.extentreports.Status;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.ReportManager;
import com.toskie.utils_Layer.UtilLayer;

public class ReviewPage {

    private final UtilLayer<?> util;

    public ReviewPage(UtilLayer<?> util) {
        this.util = util;
    }

    public boolean isLoaded() {
        try {
            return BrowserManager.getPage()
                    .locator("[data-testid='review-page'], .review-page, .reviews-section, .review-container")
                    .isVisible();
        } catch (Exception e) {
            return false;
        }
    }

    public void submitReview(int rating, String text) {
        ReportManager.getTest().log(Status.INFO, "Submitting review with rating: " + rating);
        try {
            BrowserManager.getPage()
                    .locator("[data-testid='star-" + rating + "'], .star:nth-child(" + rating + "), .rating-star:nth-child(" + rating + ")")
                    .first().click();
        } catch (Exception e) {
            try {
                BrowserManager.getPage()
                        .locator("[aria-label='" + rating + " star'], [data-rating='" + rating + "']")
                        .first().click();
            } catch (Exception ex) {
                ReportManager.getTest().log(Status.WARNING, "Star rating click issue: " + ex.getMessage());
            }
        }
        try {
            BrowserManager.getPage()
                    .locator("textarea[name='review'], textarea[placeholder*='review'], textarea[placeholder*='Review'], [data-testid='review-input']")
                    .first().fill(text);
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "Review text input issue: " + e.getMessage());
        }
        try {
            BrowserManager.getPage()
                    .locator("button:has-text('Submit'), button:has-text('Post Review'), [data-testid='submit-review-btn']")
                    .first().click();
            BrowserManager.getPage().waitForTimeout(1500);
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "Submit review issue: " + e.getMessage());
        }
    }

    public String getAverageRating() {
        ReportManager.getTest().log(Status.INFO, "Getting average rating");
        try {
            return BrowserManager.getPage()
                    .locator("[data-testid='average-rating'], .average-rating, .rating-value, .overall-rating")
                    .first().textContent().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public int getReviewCount() {
        ReportManager.getTest().log(Status.INFO, "Getting review count");
        try {
            return BrowserManager.getPage()
                    .locator("[data-testid='review-item'], .review-item, .review-card")
                    .count();
        } catch (Exception e) {
            return 0;
        }
    }

    private int pendingRating = 0;
    private String pendingText = "";
    public boolean isReviewSectionVisible() { return isLoaded(); }
    public void selectRating(int r)         { pendingRating = r; }
    public void enterReviewText(String t)   { pendingText = t; }
    public void submitReview()              { submitReview(pendingRating, pendingText); }
    public boolean isReviewSubmitted()      { try { return BrowserManager.getPage().locator("[class*='success'], [class*='thank-you']").isVisible(); } catch (Exception e) { return false; } }
    public String getRatingError()          { try { return BrowserManager.getPage().locator("[class*='error']:has-text('rating'), [class*='rating-error']").textContent().trim(); } catch (Exception e) { return ""; } }
}
