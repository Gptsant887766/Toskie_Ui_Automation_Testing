package com.toskie.tests.profile;
import com.aventstack.extentreports.Status;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.constants.TestGroups;
import com.toskie.pages.profile.ExperienceStepPage;
import com.toskie.utils.AssertionHelper;
import com.toskie.utils_Layer.ReportManager;
import org.testng.annotations.Test;

public class ExperienceStepTests extends BaseTest {
    private ExperienceStepPage expPage;
    private AssertionHelper a;

    private void init() { expPage = new ExperienceStepPage(utilLayer); a = new AssertionHelper(); }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P1}, description = "Add valid work experience")
    public void testAddValidExperience() {
        init();
        ReportManager.getTest().log(Status.INFO, "Adding valid experience");
        expPage.addExperience("ABC Corp", "QA Engineer", "2021-01", "2023-06", "Tested features");
        boolean saved = expPage.isExperienceSaved();
        a.assertTrue(saved, "Experience card should appear after saving");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P1}, description = "Add multiple experiences")
    public void testAddMultipleExperiences() {
        init();
        expPage.addExperience("Corp A", "Dev", "2020-01", "2021-01", "Development");
        expPage.addMoreExperience();
        expPage.addExperience("Corp B", "Lead", "2021-06", "2023-06", "Leading QA");
        a.assertTrue(expPage.getExperienceCount() >= 2, "Should have at least 2 experience cards");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2}, description = "Validate current job checkbox disables end date")
    public void testCurrentJobToggle() {
        init();
        expPage.toggleCurrentJob();
        a.assertTrue(expPage.isEndDateDisabled(), "End date should be disabled when current job is checked");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2}, description = "Delete experience entry")
    public void testDeleteExperience() {
        init();
        expPage.addExperience("TempCo", "Tester", "2022-01", "2022-12", "temp");
        int countBefore = expPage.getExperienceCount();
        expPage.deleteExperience(0);
        a.assertTrue(expPage.getExperienceCount() < countBefore, "Count should decrease after delete");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P3}, description = "Required field validation")
    public void testRequiredFieldValidation() {
        init();
        expPage.clickSave();
        a.assertNotEmpty(expPage.getCompanyError(), "Company field error should appear");
        a.assertAll();
    }
}

