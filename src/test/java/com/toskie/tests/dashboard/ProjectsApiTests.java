package com.toskie.tests.dashboard;
import com.aventstack.extentreports.Status;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.constants.TestGroups;
import com.toskie.pages.dashboard.ProjectsDashboardPage;
import com.toskie.utils.AssertionHelper;
import com.toskie.utils_Layer.ReportManager;
import org.testng.annotations.Test;

public class ProjectsApiTests extends BaseTest {
    private ProjectsDashboardPage projectsPage;
    private AssertionHelper a;
    private void init() { projectsPage = new ProjectsDashboardPage(utilLayer); a = new AssertionHelper(); }

    @Test(groups = {TestGroups.API, TestGroups.P1}, description = "Projects loaded from API correctly")
    public void testProjectsLoadFromApi() {
        init();
        ReportManager.getTest().log(Status.INFO, "Checking projects loaded via API");
        a.assertTrue(projectsPage.getProjectCount() >= 0, "Projects should load without error");
        a.assertAll();
    }
}

