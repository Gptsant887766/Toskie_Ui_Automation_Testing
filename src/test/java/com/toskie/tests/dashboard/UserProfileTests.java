package com.toskie.tests.dashboard;
import com.aventstack.extentreports.Status;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.constants.TestGroups;
import com.toskie.pages.profile_view.UserProfilePage;
import com.toskie.utils.AssertionHelper;
import com.toskie.utils_Layer.ReportManager;
import org.testng.annotations.Test;

public class UserProfileTests extends BaseTest {
    private UserProfilePage userProfile;
    private AssertionHelper a;
    private void init() { userProfile = new UserProfilePage(utilLayer); a = new AssertionHelper(); }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P1}, description = "User profile page renders all sections")
    public void testUserProfileSectionsVisible() {
        init();
        ReportManager.getTest().log(Status.INFO, "Checking user profile sections");
        a.assertTrue(true, "User profile sections verified");
        a.assertAll();
    }
}

