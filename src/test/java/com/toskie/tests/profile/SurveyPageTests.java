package com.toskie.tests.profile;
import com.aventstack.extentreports.Status;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.constants.TestGroups;
import com.toskie.pages.profile.SurveyPage;
import com.toskie.utils.AssertionHelper;
import com.toskie.utils_Layer.ReportManager;
import org.testng.annotations.Test;

public class SurveyPageTests extends BaseTest {
    private SurveyPage surveyPage;
    private AssertionHelper a;

    private void init() { surveyPage = new SurveyPage(utilLayer); a = new AssertionHelper(); }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P1}, description = "Survey page loads with questions")
    public void testSurveyLoads() {
        init();
        ReportManager.getTest().log(Status.INFO, "Survey page loaded");
        a.assertTrue(surveyPage.isSurveyVisible(), "Survey should be visible");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2}, description = "Submit survey with all answers")
    public void testSubmitSurvey() {
        init();
        surveyPage.answerFirstQuestion();
        surveyPage.submitSurvey();
        a.assertTrue(surveyPage.isSubmitSuccessful(), "Survey submission should succeed");
        a.assertAll();
    }
}

