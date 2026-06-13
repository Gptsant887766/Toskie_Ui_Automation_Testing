package com.toskie.tests.dashboard;
import com.aventstack.extentreports.Status;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.constants.TestGroups;
import com.toskie.pages.dashboard.AboutDashboardPage;
import com.toskie.utils.AssertionHelper;
import org.testng.annotations.Test;

public class DashboardAboutTests extends BaseTest {
    private AboutDashboardPage aboutPage;
    private AssertionHelper a;
    private void init() { aboutPage = new AboutDashboardPage(utilLayer); a = new AssertionHelper(); }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P1}, description = "Bio text is visible in About section")
    public void testBioVisible() { init(); a.assertTrue(aboutPage.isBioVisible(), "Bio text should be displayed"); a.assertAll(); }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P1}, description = "Edit bio and save updates successfully")
    public void testEditAndSaveBio() {
        init();
        aboutPage.clickEditBio();
        aboutPage.updateBio("Updated bio for testing - " + System.currentTimeMillis());
        aboutPage.saveBio();
        a.assertTrue(aboutPage.isBioVisible(), "Bio should still be visible after save");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2}, description = "Cancel edit restores original bio")
    public void testCancelEditBio() {
        init();
        String original = aboutPage.getBioText();
        aboutPage.clickEditBio();
        aboutPage.updateBio("This should not be saved");
        aboutPage.cancelEdit();
        a.assertTrue(aboutPage.isBioVisible(), "Bio should be visible after cancel");
        a.assertAll();
    }
}
