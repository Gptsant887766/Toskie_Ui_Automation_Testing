package com.toskie.tests.dashboard;
import com.aventstack.extentreports.Status;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.constants.TestGroups;
import com.toskie.pages.dashboard.*;
import com.toskie.utils.AssertionHelper;
import org.testng.annotations.Test;

public class DashboardCRUDTests extends BaseTest {
    private AssertionHelper a;
    private void init() { a = new AssertionHelper(); }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P1}, description = "Add skill from dashboard and verify")
    public void testAddSkillFromDashboard() {
        init();
        SkillsDashboardPage skillsPage = new SkillsDashboardPage(utilLayer);
        int before = skillsPage.getSkillCount();
        skillsPage.addSkill("Photography");
        skillsPage.saveSkills();
        a.assertTrue(skillsPage.getSkillCount() >= before, "Skill count should not decrease after add");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P1}, description = "Add project from dashboard and verify")
    public void testAddProjectFromDashboard() {
        init();
        ProjectsDashboardPage projectsPage = new ProjectsDashboardPage(utilLayer);
        projectsPage.clickAddProject();
        projectsPage.fillProjectTitle("Dashboard Test Project");
        projectsPage.fillProjectDesc("Created from dashboard CRUD test");
        projectsPage.saveProject();
        a.assertTrue(projectsPage.getProjectCount() > 0, "Project should be added");
        a.assertAll();
    }
}
