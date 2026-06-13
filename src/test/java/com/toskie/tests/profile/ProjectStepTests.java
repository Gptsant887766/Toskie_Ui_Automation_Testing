package com.toskie.tests.profile;
import com.aventstack.extentreports.Status;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.constants.TestGroups;
import com.toskie.pages.profile.ProjectStepPage;
import com.toskie.utils.AssertionHelper;
import com.toskie.utils_Layer.ReportManager;
import org.testng.annotations.Test;

public class ProjectStepTests extends BaseTest {
    private ProjectStepPage projectPage;
    private AssertionHelper a;

    private void init() { projectPage = new ProjectStepPage(utilLayer); a = new AssertionHelper(); }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P1}, description = "Add valid project")
    public void testAddValidProject() {
        init();
        ReportManager.getTest().log(Status.INFO, "Adding project");
        projectPage.addProject("Toskie Automation", "UI automation framework", "https://github.com/test");
        a.assertTrue(projectPage.isProjectSaved(), "Project card should appear after save");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2}, description = "Add project with invalid URL")
    public void testAddProjectInvalidUrl() {
        init();
        projectPage.addProject("Test Project", "A project", "not-a-url");
        a.assertNotEmpty(projectPage.getUrlError(), "URL validation error should show");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P3}, description = "Delete project")
    public void testDeleteProject() {
        init();
        projectPage.addProject("Delete Me", "temp", "https://example.com");
        int before = projectPage.getProjectCount();
        projectPage.deleteProject(0);
        a.assertTrue(projectPage.getProjectCount() < before, "Count should decrease after delete");
        a.assertAll();
    }
}

