package com.toskie.tests.ai;
import com.aventstack.extentreports.Status;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.constants.TestGroups;
import com.toskie.pages.profile.BioStepPage;
import com.toskie.utils.AssertionHelper;
import org.testng.annotations.Test;

public class AIBioTests extends BaseTest {
    private BioStepPage bioPage;
    private AssertionHelper a;
    private void init() { bioPage = new BioStepPage(utilLayer); a = new AssertionHelper(); }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P1}, description = "AI bio generation button is visible")
    public void testAIBioButtonVisible() { init(); a.assertTrue(bioPage.isAIGenerateBtnVisible(), "AI generate button should be visible"); a.assertAll(); }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P1}, description = "AI generates bio content on click")
    public void testAIGeneratesBio() {
        init();
        bioPage.clickAIGenerate();
        a.assertTrue(bioPage.isBioGenerated(), "AI should generate bio content");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2}, description = "Apply AI suggestion fills bio textarea")
    public void testApplyAISuggestion() {
        init();
        bioPage.clickAIGenerate();
        bioPage.applyAISuggestion();
        a.assertNotEmpty(bioPage.getBioText(), "Bio textarea should be filled with AI content");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P3}, description = "Clear bio after AI generation empties textarea")
    public void testClearBioAfterAI() {
        init();
        bioPage.clickAIGenerate();
        bioPage.applyAISuggestion();
        bioPage.clearBio();
        a.assertTrue(bioPage.getBioText().isEmpty(), "Bio should be empty after clear");
        a.assertAll();
    }
}
