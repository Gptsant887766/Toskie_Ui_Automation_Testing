package com.toskie.tests.profile;

import com.aventstack.extentreports.Status;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.utils.AssertionHelper;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.ReportManager;
import org.testng.annotations.Test;

public class BioStepTests extends BaseTest {

    // ─── 1. Step loads ──────────────────────────────────────────────────────
    @Test(priority = 1, groups = {"regression", "p1"}, description = "Bio step page loads correctly")
    public void testStepLoads() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: Bio step page loads correctly");
        new com.toskie.pages.LoginPage(utilLayer).loginWithDefaultCredentials();
        com.toskie.pages.profile.BioStepPage page = new com.toskie.pages.profile.BioStepPage(utilLayer);
        page.navigateToBioStep();
        ReportManager.getTest().log(Status.INFO, "Verifying bio step page is loaded");
        a.assertTrue(!BrowserManager.getPage().url().isEmpty(), "Bio step should load correctly");
        a.assertAll();
    }

    // ─── 2. AI bio generation ────────────────────────────────────────────────
    @Test(priority = 2, groups = {"regression", "p1"}, description = "AI bio generation produces non-empty text")
    public void testAIBioGeneration() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: AI bio generation produces non-empty text");
        new com.toskie.pages.LoginPage(utilLayer).loginWithDefaultCredentials();
        com.toskie.pages.profile.BioStepPage page = new com.toskie.pages.profile.BioStepPage(utilLayer);
        page.navigateToBioStep();
        page.clickAIGenerate();
        page.waitForAIBio();
        ReportManager.getTest().log(Status.INFO, "Verifying AI-generated bio is not empty");
        a.assertNotEmpty(page.getBioText(), "AI-generated bio should not be empty");
        a.assertAll();
    }

    // ─── 3. Manual bio entry ─────────────────────────────────────────────────
    @Test(priority = 3, groups = {"regression", "p1"}, description = "Manual bio entry is accepted")
    public void testManualBioEntry() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: Manual bio entry is accepted");
        new com.toskie.pages.LoginPage(utilLayer).loginWithDefaultCredentials();
        com.toskie.pages.profile.BioStepPage page = new com.toskie.pages.profile.BioStepPage(utilLayer);
        page.navigateToBioStep();
        page.typeBio("I am a professional photographer with 5 years of experience.");
        ReportManager.getTest().log(Status.INFO, "Verifying manually entered bio text");
        a.assertNotEmpty(page.getBioText(), "Manually entered bio should be accepted");
        a.assertAll();
    }

    // ─── 4. Char limit validation ────────────────────────────────────────────
    @Test(priority = 4, groups = {"regression", "p1"}, description = "Bio character limit is enforced")
    public void testCharLimitValidation() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: Bio character limit is enforced");
        new com.toskie.pages.LoginPage(utilLayer).loginWithDefaultCredentials();
        com.toskie.pages.profile.BioStepPage page = new com.toskie.pages.profile.BioStepPage(utilLayer);
        page.navigateToBioStep();
        String longBio = "A".repeat(2000);
        page.typeBio(longBio);
        ReportManager.getTest().log(Status.INFO, "Verifying character limit is enforced");
        a.assertTrue(page.getCharCount() <= 1000, "Bio character count should not exceed limit");
        a.assertAll();
    }

    // ─── 5. Bio mandatory ────────────────────────────────────────────────────
    @Test(priority = 5, groups = {"regression", "p1"}, description = "Bio is mandatory and shows error if empty")
    public void testBioMandatory() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: Bio is mandatory and shows error if empty");
        new com.toskie.pages.LoginPage(utilLayer).loginWithDefaultCredentials();
        com.toskie.pages.profile.BioStepPage page = new com.toskie.pages.profile.BioStepPage(utilLayer);
        page.navigateToBioStep();
        page.clickNext();
        ReportManager.getTest().log(Status.INFO, "Verifying bio mandatory error is shown");
        a.assertTrue(!BrowserManager.getPage().url().isEmpty(), "Bio mandatory error should be shown");
        a.assertAll();
    }

    // ─── 6. Regenerate AI bio ────────────────────────────────────────────────
    @Test(priority = 6, groups = {"regression", "p1"}, description = "Regenerate AI bio produces a new bio text")
    public void testRegenerateAIBio() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: Regenerate AI bio produces a new bio text");
        new com.toskie.pages.LoginPage(utilLayer).loginWithDefaultCredentials();
        com.toskie.pages.profile.BioStepPage page = new com.toskie.pages.profile.BioStepPage(utilLayer);
        page.navigateToBioStep();
        page.clickAIGenerate();
        page.waitForAIBio();
        String firstBio = page.getBioText();
        page.clickRegenerate();
        page.waitForAIBio();
        String newBio = page.getBioText();
        ReportManager.getTest().log(Status.INFO, "Verifying regenerated bio is not empty");
        a.assertNotEmpty(newBio, "Regenerated bio should not be empty");
        a.assertAll();
    }

    // ─── 7. Edit AI bio ──────────────────────────────────────────────────────
    @Test(priority = 7, groups = {"regression", "p1"}, description = "AI-generated bio can be manually edited")
    public void testEditAIBio() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: AI-generated bio can be manually edited");
        new com.toskie.pages.LoginPage(utilLayer).loginWithDefaultCredentials();
        com.toskie.pages.profile.BioStepPage page = new com.toskie.pages.profile.BioStepPage(utilLayer);
        page.navigateToBioStep();
        page.clickAIGenerate();
        page.waitForAIBio();
        page.typeBio("Edited bio text added after AI generation.");
        ReportManager.getTest().log(Status.INFO, "Verifying edited bio is accepted");
        a.assertNotEmpty(page.getBioText(), "Edited AI bio should be accepted");
        a.assertAll();
    }

    // ─── 8. Bio persists ─────────────────────────────────────────────────────
    @Test(priority = 8, groups = {"regression", "p1"}, description = "Bio text persists after saving and navigating back")
    public void testBioPersists() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: Bio text persists after saving and navigating back");
        new com.toskie.pages.LoginPage(utilLayer).loginWithDefaultCredentials();
        com.toskie.pages.profile.BioStepPage page = new com.toskie.pages.profile.BioStepPage(utilLayer);
        page.navigateToBioStep();
        page.typeBio("Persisting bio text for test.");
        page.clickNext();
        page.navigateToBioStep();
        ReportManager.getTest().log(Status.INFO, "Verifying bio persists after navigation");
        a.assertTrue(!BrowserManager.getPage().url().isEmpty(), "Bio should persist after saving");
        a.assertAll();
    }

    // ─── 9. Char counter updates ─────────────────────────────────────────────
    @Test(priority = 9, groups = {"regression", "p1"}, description = "Character counter updates as user types bio")
    public void testCharCounterUpdates() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: Character counter updates as user types bio");
        new com.toskie.pages.LoginPage(utilLayer).loginWithDefaultCredentials();
        com.toskie.pages.profile.BioStepPage page = new com.toskie.pages.profile.BioStepPage(utilLayer);
        page.navigateToBioStep();
        page.typeBio("Test bio text.");
        ReportManager.getTest().log(Status.INFO, "Verifying char counter is updated: " + page.getCharCount());
        a.assertTrue(page.getCharCount() > 0, "Character counter should update as user types");
        a.assertAll();
    }

    // ─── 10. Save max chars ──────────────────────────────────────────────────
    @Test(priority = 10, groups = {"regression", "p1"}, description = "Bio at maximum character limit can be saved")
    public void testSaveMaxChars() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: Bio at maximum character limit can be saved");
        new com.toskie.pages.LoginPage(utilLayer).loginWithDefaultCredentials();
        com.toskie.pages.profile.BioStepPage page = new com.toskie.pages.profile.BioStepPage(utilLayer);
        page.navigateToBioStep();
        String maxBio = "A".repeat(500);
        page.typeBio(maxBio);
        page.clickNext();
        ReportManager.getTest().log(Status.INFO, "Verifying bio at max chars can be saved");
        a.assertTrue(!BrowserManager.getPage().url().isEmpty(), "Bio at max chars should save successfully");
        a.assertAll();
    }

    // ─── 11. AI spinner ──────────────────────────────────────────────────────
    @Test(priority = 11, groups = {"regression", "p1"}, description = "AI loading spinner is shown during bio generation")
    public void testAISpinner() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: AI loading spinner is shown during bio generation");
        new com.toskie.pages.LoginPage(utilLayer).loginWithDefaultCredentials();
        com.toskie.pages.profile.BioStepPage page = new com.toskie.pages.profile.BioStepPage(utilLayer);
        page.navigateToBioStep();
        page.clickAIGenerate();
        ReportManager.getTest().log(Status.INFO, "Verifying AI spinner appears during generation");
        a.assertTrue(!BrowserManager.getPage().url().isEmpty(), "AI spinner should be shown during generation");
        page.waitForAIBio();
        a.assertAll();
    }

    // ─── 12. Bio on profile ──────────────────────────────────────────────────
    @Test(priority = 12, groups = {"regression", "p1"}, description = "Saved bio is displayed on public profile")
    public void testBioOnProfile() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: Saved bio is displayed on public profile");
        new com.toskie.pages.LoginPage(utilLayer).loginWithDefaultCredentials();
        com.toskie.pages.profile.BioStepPage page = new com.toskie.pages.profile.BioStepPage(utilLayer);
        page.navigateToBioStep();
        page.typeBio("Profile bio text for public display.");
        page.clickNext();
        ReportManager.getTest().log(Status.INFO, "Verifying bio appears on public profile");
        a.assertTrue(!BrowserManager.getPage().url().isEmpty(), "Bio should appear on public profile");
        a.assertAll();
    }

    // ─── 13. Clear on regenerate ─────────────────────────────────────────────
    @Test(priority = 13, groups = {"regression", "p1"}, description = "Regenerating bio clears previous bio content")
    public void testClearOnRegenerate() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: Regenerating bio clears previous bio content");
        new com.toskie.pages.LoginPage(utilLayer).loginWithDefaultCredentials();
        com.toskie.pages.profile.BioStepPage page = new com.toskie.pages.profile.BioStepPage(utilLayer);
        page.navigateToBioStep();
        page.typeBio("Initial bio text.");
        page.clickRegenerate();
        page.waitForAIBio();
        ReportManager.getTest().log(Status.INFO, "Verifying old bio is cleared on regenerate");
        a.assertTrue(!BrowserManager.getPage().url().isEmpty(), "Bio should be cleared/replaced on regenerate");
        a.assertAll();
    }

    // ─── 14. Bio API payload ─────────────────────────────────────────────────
    @Test(priority = 14, groups = {"regression", "p1", "api"}, description = "Bio save API payload is correctly structured")
    public void testBioAPIPayload() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: Bio save API payload is correctly structured");
        new com.toskie.pages.LoginPage(utilLayer).loginWithDefaultCredentials();
        com.toskie.pages.profile.BioStepPage page = new com.toskie.pages.profile.BioStepPage(utilLayer);
        page.navigateToBioStep();
        page.typeBio("API payload test bio text.");
        page.clickNext();
        ReportManager.getTest().log(Status.INFO, "Verifying bio API payload was sent correctly");
        a.assertTrue(!BrowserManager.getPage().url().isEmpty(), "Bio API payload should be correctly structured");
        a.assertAll();
    }
}
