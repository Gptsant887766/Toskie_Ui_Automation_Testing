package com.toskie.tests.dashboard;
import com.aventstack.extentreports.Status;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.constants.TestGroups;
import com.toskie.pages.dashboard.DashboardPage;
import com.toskie.utils.AssertionHelper;
import org.testng.annotations.Test;

public class TalentProfileTests extends BaseTest {
    private DashboardPage dash;
    private AssertionHelper a;
    private void init() { dash = new DashboardPage(utilLayer); a = new AssertionHelper(); }

    @Test(groups = {TestGroups.SMOKE, TestGroups.P0}, description = "Profile name visible on dashboard")
    public void testProfileNameVisible() { init(); a.assertNotEmpty(dash.getProfileName(), "Profile name should be displayed"); a.assertAll(); }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P1}, description = "Profile photo is displayed")
    public void testProfilePhotoVisible() { init(); a.assertTrue(dash.isProfilePhotoVisible(), "Profile photo should show"); a.assertAll(); }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P1}, description = "Profile role/designation is visible")
    public void testProfileRoleVisible() { init(); a.assertNotEmpty(dash.getProfileRole(), "Role/designation should show"); a.assertAll(); }
}
