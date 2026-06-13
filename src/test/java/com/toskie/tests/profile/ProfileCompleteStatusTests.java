package com.toskie.tests.profile;
import com.aventstack.extentreports.Status;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.constants.TestGroups;
import com.toskie.pages.dashboard.DashboardPage;
import com.toskie.utils.AssertionHelper;
import com.toskie.utils_Layer.ReportManager;
import org.testng.annotations.Test;

public class ProfileCompleteStatusTests extends BaseTest {
    private DashboardPage dashPage;
    private AssertionHelper a;

    private void init() { dashPage = new DashboardPage(utilLayer); a = new AssertionHelper(); }

    @Test(groups = {TestGroups.SMOKE, TestGroups.P0}, description = "Profile completion percentage is displayed")
    public void testCompletionPercentageVisible() {
        init();
        ReportManager.getTest().log(Status.INFO, "Checking profile completion %");
        String pct = dashPage.getCompletionPercentage();
        a.assertNotEmpty(pct, "Completion percentage should be shown");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P1}, description = "All profile sections are visible on dashboard")
    public void testAllSectionsVisible() {
        init();
        a.assertTrue(dashPage.isAboutSectionVisible(), "About section visible");
        a.assertTrue(dashPage.isGallerySectionVisible(), "Gallery section visible");
        a.assertTrue(dashPage.isSkillsSectionVisible(), "Skills section visible");
        a.assertTrue(dashPage.isProjectsSectionVisible(), "Projects section visible");
        a.assertAll();
    }
}

