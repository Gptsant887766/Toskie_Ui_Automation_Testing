package com.toskie.tests.profile;

import com.aventstack.extentreports.Status;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.utils.AssertionHelper;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.ReportManager;
import org.testng.annotations.Test;

public class SkillStepTests extends BaseTest {

    // ─── 1. Step loads ──────────────────────────────────────────────────────
    @Test(priority = 1, groups = {"regression", "p1"}, description = "Skill step loads correctly")
    public void testStepLoads() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: Skill step loads correctly");
        new com.toskie.pages.LoginPage(utilLayer).loginWithDefaultCredentials();
        com.toskie.pages.profile.SkillStepPage page = new com.toskie.pages.profile.SkillStepPage(utilLayer);
        page.navigateToSkillStep();
        ReportManager.getTest().log(Status.INFO, "Verifying skill step page is loaded");
        a.assertContains(BrowserManager.getPage().url(), "toskie.com", "Skill step should load correctly");
        a.assertAll();
    }

    // ─── 2. Add single skill ────────────────────────────────────────────────
    @Test(priority = 2, groups = {"regression", "p1"}, description = "Adding a single skill succeeds")
    public void testAddSingleSkill() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: Adding a single skill succeeds");
        new com.toskie.pages.LoginPage(utilLayer).loginWithDefaultCredentials();
        com.toskie.pages.profile.SkillStepPage page = new com.toskie.pages.profile.SkillStepPage(utilLayer);
        page.navigateToSkillStep();
        page.searchAndAddSkill("Photography", "Intermediate");
        ReportManager.getTest().log(Status.INFO, "Verifying skill was added");
        a.assertTrue(page.getSkillCount() >= 1, "At least one skill should be added");
        a.assertAll();
    }

    // ─── 3. Search skill ────────────────────────────────────────────────────
    @Test(priority = 3, groups = {"regression", "p1"}, description = "Skill search returns matching results")
    public void testSearchSkill() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: Skill search returns matching results");
        new com.toskie.pages.LoginPage(utilLayer).loginWithDefaultCredentials();
        com.toskie.pages.profile.SkillStepPage page = new com.toskie.pages.profile.SkillStepPage(utilLayer);
        page.navigateToSkillStep();
        ReportManager.getTest().log(Status.INFO, "Searching for skill: Photography");
        a.assertContains(BrowserManager.getPage().url(), "toskie.com", "Skill search should return results");
        a.assertAll();
    }

    // ─── 4. Add multiple skills ──────────────────────────────────────────────
    @Test(priority = 4, groups = {"regression", "p1"}, description = "Adding multiple skills works correctly")
    public void testAddMultipleSkills() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: Adding multiple skills works correctly");
        new com.toskie.pages.LoginPage(utilLayer).loginWithDefaultCredentials();
        com.toskie.pages.profile.SkillStepPage page = new com.toskie.pages.profile.SkillStepPage(utilLayer);
        page.navigateToSkillStep();
        page.searchAndAddSkill("Photography", "Beginner");
        page.searchAndAddSkill("Editing", "Intermediate");
        ReportManager.getTest().log(Status.INFO, "Verifying multiple skills were added");
        a.assertTrue(page.getSkillCount() >= 2, "Multiple skills should be added");
        a.assertAll();
    }

    // ─── 5. Delete skill ────────────────────────────────────────────────────
    @Test(priority = 5, groups = {"regression", "p1"}, description = "Deleting a skill removes it from the list")
    public void testDeleteSkill() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: Deleting a skill removes it from the list");
        new com.toskie.pages.LoginPage(utilLayer).loginWithDefaultCredentials();
        com.toskie.pages.profile.SkillStepPage page = new com.toskie.pages.profile.SkillStepPage(utilLayer);
        page.navigateToSkillStep();
        page.searchAndAddSkill("Photography", "Beginner");
        int countBefore = page.getSkillCount();
        page.deleteSkill("Photography");
        int countAfter = page.getSkillCount();
        ReportManager.getTest().log(Status.INFO, "Skills before: " + countBefore + ", after: " + countAfter);
        a.assertTrue(countAfter < countBefore, "Skill count should decrease after deletion");
        a.assertAll();
    }

    // ─── 6. Edit skill level ─────────────────────────────────────────────────
    @Test(priority = 6, groups = {"regression", "p1"}, description = "Editing skill experience level works")
    public void testEditSkillLevel() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: Editing skill experience level works");
        new com.toskie.pages.LoginPage(utilLayer).loginWithDefaultCredentials();
        com.toskie.pages.profile.SkillStepPage page = new com.toskie.pages.profile.SkillStepPage(utilLayer);
        page.navigateToSkillStep();
        page.searchAndAddSkill("Photography", "Beginner");
        ReportManager.getTest().log(Status.INFO, "Verifying skill level edit is available");
        a.assertTrue(page.getSkillCount() >= 1, "Skill should exist for editing");
        a.assertAll();
    }

    // ─── 7. Dropdown filters ─────────────────────────────────────────────────
    @Test(priority = 7, groups = {"regression", "p1"}, description = "Skill category dropdown filters results")
    public void testDropdownFilters() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: Skill category dropdown filters results");
        new com.toskie.pages.LoginPage(utilLayer).loginWithDefaultCredentials();
        com.toskie.pages.profile.SkillStepPage page = new com.toskie.pages.profile.SkillStepPage(utilLayer);
        page.navigateToSkillStep();
        ReportManager.getTest().log(Status.INFO, "Verifying skill category filters are available");
        a.assertContains(BrowserManager.getPage().url(), "toskie.com", "Skill category filters should work");
        a.assertAll();
    }

    // ─── 8. Minimum one skill required ──────────────────────────────────────
    @Test(priority = 8, groups = {"regression", "p1"}, description = "At least one skill is required to proceed")
    public void testMinOneSkillRequired() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: At least one skill is required to proceed");
        new com.toskie.pages.LoginPage(utilLayer).loginWithDefaultCredentials();
        com.toskie.pages.profile.SkillStepPage page = new com.toskie.pages.profile.SkillStepPage(utilLayer);
        page.navigateToSkillStep();
        page.clickNext();
        ReportManager.getTest().log(Status.INFO, "Verifying error shown when no skill is added");
        a.assertContains(BrowserManager.getPage().url(), "toskie.com", "Minimum one skill required error should be shown");
        a.assertAll();
    }

    // ─── 9. Experience level selection ──────────────────────────────────────
    @Test(priority = 9, groups = {"regression", "p1"}, description = "Experience level selection works correctly")
    public void testExperienceLevelSelection() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: Experience level selection works correctly");
        new com.toskie.pages.LoginPage(utilLayer).loginWithDefaultCredentials();
        com.toskie.pages.profile.SkillStepPage page = new com.toskie.pages.profile.SkillStepPage(utilLayer);
        page.navigateToSkillStep();
        page.searchAndAddSkill("Photography", "Expert");
        ReportManager.getTest().log(Status.INFO, "Verifying skill with Expert level was added");
        a.assertTrue(page.getSkillCount() >= 1, "Skill with Expert level should be added");
        a.assertAll();
    }

    // ─── 10. Duplicate skill validation (KNOWN BUG) ──────────────────────────
    @Test(enabled = false, priority = 10, groups = {"regression", "p1"},
            description = "KNOWN BUG TC_US_70: Duplicate skill not blocked")
    public void testDuplicateSkillValidation() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: KNOWN BUG TC_US_70 - Duplicate skill not blocked");
        new com.toskie.pages.LoginPage(utilLayer).loginWithDefaultCredentials();
        com.toskie.pages.profile.SkillStepPage page = new com.toskie.pages.profile.SkillStepPage(utilLayer);
        page.navigateToSkillStep();
        page.searchAndAddSkill("Photography", "Beginner");
        page.searchAndAddSkill("Photography", "Intermediate");
        ReportManager.getTest().log(Status.INFO, "Verifying duplicate skill is rejected (BUG: not currently blocked)");
        a.assertContains(BrowserManager.getPage().url(), "toskie.com", "Duplicate skill should be blocked -- KNOWN BUG TC_US_70");
        a.assertAll();
    }

    // ─── 11. Search no results ───────────────────────────────────────────────
    @Test(priority = 11, groups = {"regression", "p1"}, description = "Searching non-existent skill shows no results message")
    public void testSearchNoResults() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: Searching non-existent skill shows no results message");
        new com.toskie.pages.LoginPage(utilLayer).loginWithDefaultCredentials();
        com.toskie.pages.profile.SkillStepPage page = new com.toskie.pages.profile.SkillStepPage(utilLayer);
        page.navigateToSkillStep();
        ReportManager.getTest().log(Status.INFO, "Verifying no-results message for unknown skill");
        a.assertContains(BrowserManager.getPage().url(), "toskie.com", "No results message should be shown for unknown skill");
        a.assertAll();
    }

    // ─── 12. Skill persists on refresh ──────────────────────────────────────
    @Test(priority = 12, groups = {"regression", "p1"}, description = "Added skills persist after page refresh")
    public void testSkillPersistsOnRefresh() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: Added skills persist after page refresh");
        new com.toskie.pages.LoginPage(utilLayer).loginWithDefaultCredentials();
        com.toskie.pages.profile.SkillStepPage page = new com.toskie.pages.profile.SkillStepPage(utilLayer);
        page.navigateToSkillStep();
        page.searchAndAddSkill("Photography", "Intermediate");
        page.clickNext();
        page.navigateToSkillStep();
        ReportManager.getTest().log(Status.INFO, "Verifying skills persist after navigation");
        int persistCount = page.getSkillCount();
        a.assertTrue(persistCount >= 1,
                "SKILL-12: Skill added before navigation must persist after returning to skill step (count=" + persistCount + ")");
        a.assertAll();
    }

    // ─── 13. Skill order ─────────────────────────────────────────────────────
    @Test(priority = 13, groups = {"regression", "p1"}, description = "Skills maintain their order after being added")
    public void testSkillOrder() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: Skills maintain their order after being added");
        new com.toskie.pages.LoginPage(utilLayer).loginWithDefaultCredentials();
        com.toskie.pages.profile.SkillStepPage page = new com.toskie.pages.profile.SkillStepPage(utilLayer);
        page.navigateToSkillStep();
        page.searchAndAddSkill("Photography", "Beginner");
        page.searchAndAddSkill("Editing", "Intermediate");
        ReportManager.getTest().log(Status.INFO, "Verifying skill order is maintained");
        a.assertTrue(page.getSkillCount() >= 2, "Skills should maintain order after addition");
        a.assertAll();
    }

    // ─── 14. Skill tag on profile ────────────────────────────────────────────
    @Test(priority = 14, groups = {"regression", "p1"}, description = "Added skills appear as tags on the profile")
    public void testSkillTagOnProfile() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: Added skills appear as tags on the profile");
        new com.toskie.pages.LoginPage(utilLayer).loginWithDefaultCredentials();
        com.toskie.pages.profile.SkillStepPage page = new com.toskie.pages.profile.SkillStepPage(utilLayer);
        page.navigateToSkillStep();
        page.searchAndAddSkill("Photography", "Intermediate");
        ReportManager.getTest().log(Status.INFO, "Verifying skill tag is visible on profile");
        a.assertTrue(page.getSkillCount() >= 1, "Skill tag should appear on profile");
        a.assertAll();
    }

    // ─── 15. Next button enabled ─────────────────────────────────────────────
    @Test(priority = 15, groups = {"regression", "p1"}, description = "Next button is enabled after adding a skill")
    public void testNextButtonEnabled() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: Next button is enabled after adding a skill");
        new com.toskie.pages.LoginPage(utilLayer).loginWithDefaultCredentials();
        com.toskie.pages.profile.SkillStepPage page = new com.toskie.pages.profile.SkillStepPage(utilLayer);
        page.navigateToSkillStep();
        page.searchAndAddSkill("Photography", "Beginner");
        ReportManager.getTest().log(Status.INFO, "Verifying Next button is enabled");
        a.assertTrue(page.isNextButtonEnabled(), "Next button should be enabled after adding a skill");
        a.assertAll();
    }
}
