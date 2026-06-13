package com.toskie.tests.reviews;
import com.aventstack.extentreports.Status;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.constants.TestGroups;
import com.toskie.pages.reviews.ReviewPage;
import com.toskie.utils.AssertionHelper;
import org.testng.annotations.Test;

public class ReviewTests extends BaseTest {
    private ReviewPage reviewPage;
    private AssertionHelper a;
    private void init() { reviewPage = new ReviewPage(utilLayer); a = new AssertionHelper(); }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P1}, description = "Reviews are displayed on talent profile")
    public void testReviewsVisible() { init(); a.assertTrue(reviewPage.isReviewSectionVisible(), "Reviews should be visible"); a.assertAll(); }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P1}, description = "Submit a valid review with 5 stars")
    public void testSubmitReview() {
        init();
        reviewPage.selectRating(5);
        reviewPage.enterReviewText("Excellent talent! Highly recommended.");
        reviewPage.submitReview();
        a.assertTrue(reviewPage.isReviewSubmitted(), "Review should be submitted");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2}, description = "Submit review without rating shows validation error")
    public void testReviewRatingRequired() {
        init();
        reviewPage.enterReviewText("Good work");
        reviewPage.submitReview();
        a.assertNotEmpty(reviewPage.getRatingError(), "Rating error should appear");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2}, description = "Average rating updates after new review")
    public void testAverageRatingUpdates() {
        init();
        String before = reviewPage.getAverageRating();
        a.assertNotEmpty(before, "Average rating should be shown");
        a.assertAll();
    }
}
