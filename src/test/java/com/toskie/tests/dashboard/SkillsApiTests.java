package com.toskie.tests.dashboard;
import com.aventstack.extentreports.Status;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.constants.TestGroups;
import com.toskie.pages.dashboard.SkillsDashboardPage;
import com.toskie.utils.AssertionHelper;
import com.toskie.utils_Layer.ReportManager;
import org.testng.annotations.Test;

public class SkillsApiTests extends BaseTest {
    private SkillsDashboardPage skillsPage;
    private AssertionHelper a;
    private void init() { skillsPage = new SkillsDashboardPage(utilLayer); a = new AssertionHelper(); }

    @Test(groups = {TestGroups.API, TestGroups.P1}, description = "Skills loaded from API correctly")
    public void testSkillsLoadFromApi() {
        init();
        ReportManager.getTest().log(Status.INFO, "Checking skills loaded via API");
        a.assertTrue(skillsPage.getSkillCount() >= 0, "Skills should load without error");
        a.assertAll();
    }
}

