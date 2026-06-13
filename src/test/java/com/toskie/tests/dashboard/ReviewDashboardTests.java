package com.toskie.tests.dashboard;
import com.aventstack.extentreports.Status;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.constants.TestGroups;
import com.toskie.pages.dashboard.ReviewDashboardPage;
import com.toskie.utils.AssertionHelper;
import com.toskie.utils_Layer.ReportManager;
import org.testng.annotations.Test;

public class ReviewDashboardTests extends BaseTest {
    private ReviewDashboardPage reviewPage;
    private AssertionHelper a;
    private void init() { reviewPage = new ReviewDashboardPage(utilLayer); a = new AssertionHelper(); }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P1}, description = "Average rating is displayed on dashboard")
    public void testAvgRatingVisible() {
        init();
        ReportManager.getTest().log(Status.INFO, "Checking average rating");
        a.assertNotEmpty(reviewPage.getAvgRating(), "Average rating should be shown");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2}, description = "Review count is displayed correctly")
    public void testReviewCountVisible() {
        init();
        a.assertNotEmpty(reviewPage.getTotalReviewCount(), "Review count should be displayed");
        a.assertAll();
    }
}

