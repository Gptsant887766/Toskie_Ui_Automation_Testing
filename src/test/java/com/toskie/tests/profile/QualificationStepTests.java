package com.toskie.tests.profile;
import com.aventstack.extentreports.Status;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.constants.TestGroups;
import com.toskie.pages.profile.QualificationStepPage;
import com.toskie.utils.AssertionHelper;
import com.toskie.utils_Layer.ReportManager;
import org.testng.annotations.Test;

public class QualificationStepTests extends BaseTest {
    private QualificationStepPage qualPage;
    private AssertionHelper a;

    private void init() { qualPage = new QualificationStepPage(utilLayer); a = new AssertionHelper(); }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P1}, description = "Add valid qualification")
    public void testAddValidQualification() {
        init();
        ReportManager.getTest().log(Status.INFO, "Adding qualification");
        qualPage.addQualification("B.Tech", "IIT Delhi", "Computer Science", "2017", "2021");
        a.assertTrue(qualPage.isQualificationSaved(), "Qualification card should appear");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2}, description = "Add multiple qualifications")
    public void testAddMultipleQualifications() {
        init();
        qualPage.addQualification("B.Tech", "NIT", "CS", "2017", "2021");
        qualPage.addMoreQualification();
        qualPage.addQualification("MBA", "IIM", "Finance", "2021", "2023");
        a.assertTrue(qualPage.getQualificationCount() >= 2, "Should have 2+ qualifications");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P3}, description = "Delete qualification")
    public void testDeleteQualification() {
        init();
        qualPage.addQualification("B.Sc", "DU", "Physics", "2018", "2021");
        int before = qualPage.getQualificationCount();
        qualPage.deleteQualification(0);
        a.assertTrue(qualPage.getQualificationCount() < before, "Count should decrease");
        a.assertAll();
    }
}

