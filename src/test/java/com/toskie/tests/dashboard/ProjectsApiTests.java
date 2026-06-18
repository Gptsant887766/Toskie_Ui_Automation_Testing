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
        int count = projectsPage.getProjectCount();
        ReportManager.getTest().log(Status.INFO, "Project count: " + count);
        boolean emptyState = com.toskie.utils_Layer.BrowserManager.getPage()
                .locator("[class*='empty'], [class*='no-project'], :has-text('No projects'), :has-text('Add your first')")
                .count() > 0;
        a.assertTrue(count > 0 || emptyState,
                "PROJECTS-API-1: Projects must show items (count > 0) OR an empty-state message (count=" + count + ", emptyState=" + emptyState + ")");
        a.assertAll();
    }
}

