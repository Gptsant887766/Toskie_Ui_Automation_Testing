package com.toskie.tests.dashboard;
import com.aventstack.extentreports.Status;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.constants.TestGroups;
import com.toskie.pages.dashboard.DashboardPage;
import com.toskie.utils.AssertionHelper;
import com.toskie.utils_Layer.ReportManager;
import org.testng.annotations.Test;

public class TalentProfileApiTests extends BaseTest {
    private DashboardPage dash;
    private AssertionHelper a;
    private void init() { dash = new DashboardPage(utilLayer); a = new AssertionHelper(); }

    @Test(groups = {TestGroups.API, TestGroups.P0}, description = "Talent profile data loaded via API correctly")
    public void testProfileDataLoaded() {
        init();
        ReportManager.getTest().log(Status.INFO, "Verifying profile data from API");
        a.assertNotEmpty(dash.getProfileName(), "Profile name from API should not be empty");
        a.assertAll();
    }

    @Test(groups = {TestGroups.API, TestGroups.P1}, description = "Profile view count is retrieved correctly")
    public void testProfileViewCount() {
        init();
        String count = dash.getProfileViewsCount();
        a.assertNotEmpty(count, "View count should not be empty");
        a.assertAll();
    }
}

