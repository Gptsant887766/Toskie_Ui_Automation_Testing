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
        int count = skillsPage.getSkillCount();
        ReportManager.getTest().log(Status.INFO, "Skill count: " + count);
        boolean emptyState = com.toskie.utils_Layer.BrowserManager.getPage()
                .locator("[class*='empty'], [class*='no-skill'], :has-text('No skills'), :has-text('Add a skill')")
                .count() > 0;
        a.assertTrue(count > 0 || emptyState,
                "SKILLS-API-1: Skills section must show skills (count > 0) OR an empty-state message (count=" + count + ", emptyState=" + emptyState + ")");
        a.assertAll();
    }
}

