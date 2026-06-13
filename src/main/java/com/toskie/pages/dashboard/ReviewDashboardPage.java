package com.toskie.pages.dashboard;
import com.microsoft.playwright.*;
import com.toskie.utils_Layer.*;
import java.util.*;

public class ReviewDashboardPage {
    private final UtilLayer<?> util;
    private final Locator reviewCards, reviewRatings, reviewAuthors, reviewTexts, avgRating,
            totalReviewCount, replyButtons, replyInput, submitReplyBtn, noReviewMsg;

    public ReviewDashboardPage(UtilLayer<?> util) {
        this.util = util;
        Page page = BrowserManager.getPage();
        reviewCards      = page.locator("[class*='review-card'], [class*='testimonial-card']");
        reviewRatings    = page.locator("[class*='review-card'] [class*='rating']");
        reviewAuthors    = page.locator("[class*='review-author'], [class*='reviewer-name']");
        reviewTexts      = page.locator("[class*='review-text'], [class*='review-content']");
        avgRating        = page.locator("[class*='avg-rating'], [class*='average-rating']").first();
        totalReviewCount = page.locator("[class*='review-count'], [class*='total-reviews']").first();
        replyButtons     = page.locator("//button[contains(.,'Reply')]");
        replyInput       = page.locator("textarea[placeholder*='reply' i], input[placeholder*='reply' i]").first();
        submitReplyBtn   = page.locator("//button[normalize-space()='Submit'] | //button[contains(.,'Send Reply')]").first();
        noReviewMsg      = page.locator("[class*='empty'], [class*='no-review']").first();
    }

    public int getReviewCount()         { return (int) reviewCards.count(); }
    public String getAvgRating()        { try { return avgRating.textContent().trim(); } catch (Exception e) { return "0"; } }
    public String getTotalReviewCount() { try { return totalReviewCount.textContent().trim(); } catch (Exception e) { return "0"; } }
    public boolean isEmptyState()       { try { return noReviewMsg.isVisible(); } catch (Exception e) { return false; } }
    public void replyToReview(int idx, String reply) {
        replyButtons.nth(idx).click(); util.fill(replyInput, reply, "Reply Input"); util.click(submitReplyBtn, "Submit Reply");
    }
    public List<String> getReviewTexts() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < reviewTexts.count(); i++) list.add(reviewTexts.nth(i).textContent().trim());
        return list;
    }
}
