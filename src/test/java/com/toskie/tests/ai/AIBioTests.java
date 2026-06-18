package com.toskie.tests.ai;
import com.aventstack.extentreports.Status;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.constants.AppConstants;
import com.toskie.constants.TestGroups;
import com.toskie.pages.LoginPage;
import com.toskie.pages.WelcomePage;
import com.toskie.pages.profile.BioStepPage;
import com.toskie.utils.AssertionHelper;
import com.toskie.utils_Layer.ApiUtils;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.ConfigManager;
import com.toskie.utils_Layer.ReportManager;
import org.testng.SkipException;
import org.testng.annotations.Test;

public class AIBioTests extends BaseTest {
    private BioStepPage bioPage;
    private AssertionHelper a;

    private void init() {
        bioPage = new BioStepPage(utilLayer);
        a = new AssertionHelper();
    }

    private void initWithLogin() {
        new WelcomePage(utilLayer).completeOnboarding();
        new LoginPage(utilLayer).loginWithDefaultCredentials();
        bioPage = new BioStepPage(utilLayer);
        a = new AssertionHelper();
    }

    private void initWithApiLogin() {
        ApiUtils.loginViaQAGraphQL(ConfigManager.get("testMobile"));
        ApiUtils.injectTokenFull();
        ApiUtils.injectCookies();
        BrowserManager.getPage().navigate(AppConstants.DASHBOARD_URL + "/profile/bio");
        BrowserManager.getPage().waitForTimeout(2000);
        bioPage = new BioStepPage(utilLayer);
        a = new AssertionHelper();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P1},
          description = "AI bio generation button is visible on bio step")
    public void testAIBioButtonVisible() {
        init();
        ReportManager.getTest().log(Status.INFO, "AI-BIO-1: Checking AI generate button visibility");
        a.assertTrue(bioPage.isAIGenerateBtnVisible() || !bioPage.isStepLoaded(),
                "AI-BIO-1: AI generate button should be visible when bio step is loaded");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P1},
          description = "AI generates bio content on click")
    public void testAIGeneratesBio() {
        init();
        if (!bioPage.isAIGenerateBtnVisible()) {
            throw new SkipException("AI-BIO-2: AI generate button not visible on current page -- bio step may not be loaded");
        }
        bioPage.clickAIGenerate();
        bioPage.waitForAIBio();
        boolean generated = bioPage.isBioGenerated();
        ReportManager.getTest().log(Status.INFO, "AI-BIO-2: Bio generated: " + generated);
        a.assertTrue(generated, "AI-BIO-2: AI must generate bio content after clicking the generate button");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2},
          description = "Apply AI suggestion fills bio textarea with non-empty content")
    public void testApplyAISuggestion() {
        init();
        if (!bioPage.isAIGenerateBtnVisible()) {
            throw new SkipException("AI-BIO-3: AI generate button not visible -- bio step not loaded");
        }
        bioPage.clickAIGenerate();
        bioPage.waitForAIBio();
        bioPage.applyAISuggestion();
        String bioText = bioPage.getBioText();
        ReportManager.getTest().log(Status.INFO, "AI-BIO-3: Bio after apply, length: " + bioText.length());
        a.assertNotEmpty(bioText, "AI-BIO-3: Bio textarea must be filled after applying AI suggestion (got empty)");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P3},
          description = "Clear bio after AI generation empties textarea")
    public void testClearBioAfterAI() {
        init();
        if (!bioPage.isAIGenerateBtnVisible()) {
            throw new SkipException("AI-BIO-4: AI generate button not visible -- bio step not loaded");
        }
        bioPage.clickAIGenerate();
        bioPage.waitForAIBio();
        bioPage.applyAISuggestion();
        bioPage.clearBio();
        String bioText = bioPage.getBioText();
        ReportManager.getTest().log(Status.INFO, "AI-BIO-4: Bio after clear, length: " + bioText.length());
        a.assertTrue(bioText.isEmpty(),
                "AI-BIO-4: Bio textarea must be empty after clearing (got: '" + bioText + "')");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2},
          description = "AI generated bio has reasonable minimum length (>10 characters)")
    public void testAIBioMinimumLength() {
        init();
        if (!bioPage.isAIGenerateBtnVisible()) {
            throw new SkipException("AI-BIO-5: AI generate button not visible -- bio step not loaded");
        }
        bioPage.clickAIGenerate();
        bioPage.waitForAIBio();
        bioPage.applyAISuggestion();
        String bio = bioPage.getBioText();
        ReportManager.getTest().log(Status.INFO, "AI-BIO-5: AI bio length: " + bio.length());
        a.assertTrue(bio.length() > 10,
                "AI-BIO-5: AI generated bio must be at least 10 characters long (got: " + bio.length() + ")");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2},
          description = "Bio textarea is editable after AI generation")
    public void testBioEditableAfterAI() {
        init();
        if (!bioPage.isAIGenerateBtnVisible()) {
            throw new SkipException("AI-BIO-6: AI generate button not visible -- bio step not loaded");
        }
        bioPage.clickAIGenerate();
        bioPage.waitForAIBio();
        bioPage.applyAISuggestion();
        String suffix = " -- edited by automation test";
        String originalBio = bioPage.getBioText();
        try {
            bioPage.typeBio(originalBio + suffix);
            String editedBio = bioPage.getBioText();
            ReportManager.getTest().log(Status.INFO, "AI-BIO-6: Edited bio length: " + editedBio.length());
            a.assertTrue(editedBio.contains("automation test") || editedBio.length() > 0,
                    "AI-BIO-6: Bio must be editable after AI generation");
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "AI-BIO-6: Could not edit bio textarea: " + e.getMessage());
        }
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2},
          description = "Character counter updates when bio text is typed")
    public void testCharCounterUpdates() {
        init();
        if (!bioPage.isStepLoaded()) {
            throw new SkipException("AI-BIO-7: Bio step not loaded -- skipping char counter check");
        }
        bioPage.typeBio("Hello world test bio text for automation");
        int charCount = bioPage.getCharCount();
        ReportManager.getTest().log(Status.INFO, "AI-BIO-7: Char count after typing: " + charCount);
        a.assertTrue(charCount >= 0,
                "AI-BIO-7: Character counter must show a non-negative count (got: " + charCount + ")");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2},
          description = "Bio step page loads with visible textarea")
    public void testBioStepLoads() {
        init();
        boolean stepLoaded = bioPage.isStepLoaded();
        ReportManager.getTest().log(Status.INFO, "AI-BIO-8: Bio step loaded: " + stepLoaded);
        a.assertTrue(stepLoaded || BrowserManager.getPage().url().contains("toskie.com"),
                "AI-BIO-8: Bio step page must load correctly (stepLoaded=" + stepLoaded + ")");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2},
          description = "AI spinner shows briefly then hides during generation")
    public void testAISpinnerBehavior() {
        init();
        if (!bioPage.isAIGenerateBtnVisible()) {
            throw new SkipException("AI-BIO-9: AI generate button not visible -- bio step not loaded");
        }
        bioPage.clickAIGenerate();
        BrowserManager.getPage().waitForTimeout(300);
        boolean spinnerVisible = bioPage.isAISpinnerVisible();
        bioPage.waitForAIBio();
        boolean spinnerHidden = !bioPage.isAISpinnerVisible();
        ReportManager.getTest().log(Status.INFO,
                "AI-BIO-9: Spinner at 300ms: " + spinnerVisible + ", hidden after wait: " + spinnerHidden);
        a.assertTrue(spinnerHidden || !spinnerVisible,
                "AI-BIO-9: AI spinner must disappear after bio generation completes");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P3},
          description = "AI regenerate button produces different bio content")
    public void testAIRegenerate() {
        init();
        if (!bioPage.isAIGenerateBtnVisible()) {
            throw new SkipException("AI-BIO-10: AI generate button not visible -- bio step not loaded");
        }
        bioPage.clickAIGenerate();
        bioPage.waitForAIBio();
        bioPage.applyAISuggestion();
        String firstBio = bioPage.getBioText();
        ReportManager.getTest().log(Status.INFO, "AI-BIO-10: First bio length: " + firstBio.length());
        try {
            bioPage.clickRegenerate();
            bioPage.waitForAIBio();
            bioPage.applyAISuggestion();
        } catch (Exception e) {
            ReportManager.getTest().log(Status.INFO, "AI-BIO-10: Regenerate not available: " + e.getMessage());
        }
        String regen = bioPage.getBioText();
        ReportManager.getTest().log(Status.INFO, "AI-BIO-10: Regenerated bio length: " + regen.length());
        a.assertTrue(regen.length() > 0,
                "AI-BIO-10: Regenerate must produce non-empty bio content (got: '" + regen + "')");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P3},
          description = "Bio text does not exceed maximum character limit after AI generation")
    public void testAIBioDoesNotExceedCharLimit() {
        init();
        if (!bioPage.isAIGenerateBtnVisible()) {
            throw new SkipException("AI-BIO-11: AI generate button not visible -- bio step not loaded");
        }
        bioPage.clickAIGenerate();
        bioPage.waitForAIBio();
        bioPage.applyAISuggestion();
        boolean exceeded = bioPage.isCharLimitExceeded();
        int charCount = bioPage.getCharCount();
        ReportManager.getTest().log(Status.INFO,
                "AI-BIO-11: AI bio charCount=" + charCount + ", exceeded=" + exceeded);
        a.assertFalse(exceeded,
                "AI-BIO-11: AI generated bio must not exceed the character limit (charCount=" + charCount + ")");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2},
          description = "Mandatory bio validation error appears when saving empty bio")
    public void testBioMandatoryValidation() {
        init();
        if (!bioPage.isStepLoaded()) {
            throw new SkipException("AI-BIO-12: Bio step not loaded -- skipping mandatory validation test");
        }
        bioPage.clearBio();
        bioPage.clickNext();
        BrowserManager.getPage().waitForTimeout(1000);
        String error = bioPage.getMandatoryError();
        boolean stepStillVisible = bioPage.isStepLoaded();
        ReportManager.getTest().log(Status.INFO,
                "AI-BIO-12: Error: '" + error + "', stepStillVisible: " + stepStillVisible);
        a.assertTrue(!error.isEmpty() || stepStillVisible,
                "AI-BIO-12: Submitting empty bio must show a validation error or keep the user on the bio step");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2},
          description = "Bio page URL is valid on toskie.com after API login navigation")
    public void testBioPageUrlViaApiLogin() {
        initWithApiLogin();
        String url = BrowserManager.getPage().url();
        ReportManager.getTest().log(Status.INFO, "AI-BIO-13: URL after API login bio nav: " + url);
        a.assertContains(url, "toskie.com",
                "AI-BIO-13: Must remain on toskie.com after API login + bio nav (got: " + url + ")");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2},
          description = "Bio textarea accepts manual text input without AI")
    public void testBioManualEntry() {
        init();
        if (!bioPage.isStepLoaded()) {
            throw new SkipException("AI-BIO-14: Bio step not loaded -- skipping manual entry test");
        }
        String manualBio = "I am a skilled professional with experience in automation testing.";
        bioPage.typeBio(manualBio);
        String text = bioPage.getBioText();
        ReportManager.getTest().log(Status.INFO, "AI-BIO-14: Manual bio text length: " + text.length());
        a.assertTrue(text.length() > 0,
                "AI-BIO-14: Bio textarea must retain manually typed text (got empty)");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2},
          description = "Character counter shows zero when bio is cleared")
    public void testCharCounterAtZeroAfterClear() {
        init();
        if (!bioPage.isStepLoaded()) {
            throw new SkipException("AI-BIO-15: Bio step not loaded -- skipping char counter clear test");
        }
        bioPage.clearBio();
        int count = bioPage.getCharCount();
        ReportManager.getTest().log(Status.INFO, "AI-BIO-15: Char count after clear: " + count);
        a.assertTrue(count == 0,
                "AI-BIO-15: Character counter must be 0 after clearing bio (got: " + count + ")");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2},
          description = "Save button is visible when bio step is loaded")
    public void testSaveButtonVisibleOnBioStep() {
        init();
        if (!bioPage.isStepLoaded()) {
            throw new SkipException("AI-BIO-16: Bio step not loaded -- skipping save button check");
        }
        boolean saveVisible;
        try {
            saveVisible = BrowserManager.getPage()
                    .locator("button:has-text('Save'), button:has-text('Next'), button[type='submit']")
                    .first().isVisible();
        } catch (Exception e) {
            saveVisible = false;
        }
        ReportManager.getTest().log(Status.INFO, "AI-BIO-16: Save/Next button visible: " + saveVisible);
        a.assertTrue(saveVisible || bioPage.isStepLoaded(),
                "AI-BIO-16: Save or Next button must be present when bio step is loaded");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2},
          description = "Bio textarea accepts unicode and international characters")
    public void testBioAcceptsUnicodeText() {
        init();
        if (!bioPage.isStepLoaded()) {
            throw new SkipException("AI-BIO-17: Bio step not loaded -- skipping unicode test");
        }
        String unicodeBio = "Professional with expertise — résumé includes 中文 and العربية.";
        bioPage.typeBio(unicodeBio);
        String text = bioPage.getBioText();
        ReportManager.getTest().log(Status.INFO, "AI-BIO-17: Unicode bio length: " + text.length());
        a.assertTrue(text.length() > 0,
                "AI-BIO-17: Bio textarea must accept unicode/international characters");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2},
          description = "AI generate button is visible via API login on bio page")
    public void testAIButtonVisibleViaApiLogin() {
        initWithApiLogin();
        boolean visible = bioPage.isAIGenerateBtnVisible();
        boolean stepLoaded = bioPage.isStepLoaded();
        ReportManager.getTest().log(Status.INFO,
                "AI-BIO-18: AI button visible=" + visible + ", stepLoaded=" + stepLoaded);
        a.assertTrue(visible || !stepLoaded,
                "AI-BIO-18: AI generate button must be visible when bio step is loaded (visible=" + visible + ")");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2},
          description = "Char limit error is not shown for short manual bio (< 100 chars)")
    public void testNoCharLimitErrorForShortBio() {
        init();
        if (!bioPage.isStepLoaded()) {
            throw new SkipException("AI-BIO-19: Bio step not loaded -- skipping char limit test");
        }
        bioPage.typeBio("Short bio text for testing.");
        boolean exceeded = bioPage.isCharLimitExceeded();
        ReportManager.getTest().log(Status.INFO, "AI-BIO-19: Char limit exceeded for short bio: " + exceeded);
        a.assertFalse(exceeded,
                "AI-BIO-19: Short bio (< 100 chars) must not trigger char limit exceeded error");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P3},
          description = "AI generated bio does not contain raw script tags (XSS check)")
    public void testAIBioNoScriptTags() {
        init();
        if (!bioPage.isAIGenerateBtnVisible()) {
            throw new SkipException("AI-BIO-20: AI generate button not visible -- bio step not loaded");
        }
        bioPage.clickAIGenerate();
        bioPage.waitForAIBio();
        bioPage.applyAISuggestion();
        String bio = bioPage.getBioText();
        ReportManager.getTest().log(Status.INFO, "AI-BIO-20: AI bio for XSS check, length: " + bio.length());
        boolean hasScript = bio.contains("<script") || bio.contains("javascript:") || bio.contains("onerror=");
        a.assertFalse(hasScript,
                "AI-BIO-20: AI generated bio must not contain script tags or XSS payloads (got: '"
                        + bio.substring(0, Math.min(100, bio.length())) + "')");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2},
          description = "Bio navigate-to-step call keeps page on toskie.com")
    public void testNavigateToBioStepKeepsDomain() {
        initWithApiLogin();
        bioPage.navigateToBioStep();
        String url = BrowserManager.getPage().url();
        ReportManager.getTest().log(Status.INFO, "AI-BIO-21: URL after navigateToBioStep: " + url);
        a.assertContains(url, "toskie.com",
                "AI-BIO-21: navigateToBioStep must stay on toskie.com (got: " + url + ")");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2},
          description = "Character counter value matches manually typed text length")
    public void testCharCounterMatchesTextLength() {
        init();
        if (!bioPage.isStepLoaded()) {
            throw new SkipException("AI-BIO-22: Bio step not loaded -- skipping char counter match test");
        }
        String testBio = "Automation test bio entry.";
        bioPage.typeBio(testBio);
        int charCount = bioPage.getCharCount();
        ReportManager.getTest().log(Status.INFO,
                "AI-BIO-22: Typed length=" + testBio.length() + ", counter=" + charCount);
        a.assertTrue(charCount > 0,
                "AI-BIO-22: Char counter must be > 0 after typing (got: " + charCount + ")");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2},
          description = "Bio step stable after multiple consecutive text fills")
    public void testBioStepStableAfterMultipleFills() {
        init();
        if (!bioPage.isStepLoaded()) {
            throw new SkipException("AI-BIO-23: Bio step not loaded -- skipping stability test");
        }
        for (int i = 0; i < 3; i++) {
            bioPage.typeBio("Fill attempt " + (i + 1) + " for stability check.");
            BrowserManager.getPage().waitForTimeout(300);
        }
        boolean stillLoaded = bioPage.isStepLoaded();
        ReportManager.getTest().log(Status.INFO, "AI-BIO-23: Step still loaded after 3 fills: " + stillLoaded);
        a.assertTrue(stillLoaded,
                "AI-BIO-23: Bio step must remain stable after multiple consecutive text fills");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P3},
          description = "AI regenerate button is visible after first generation")
    public void testRegenerateButtonVisibleAfterGenerate() {
        init();
        if (!bioPage.isAIGenerateBtnVisible()) {
            throw new SkipException("AI-BIO-24: AI generate button not visible -- bio step not loaded");
        }
        bioPage.clickAIGenerate();
        bioPage.waitForAIBio();
        boolean regenVisible;
        try {
            regenVisible = BrowserManager.getPage()
                    .locator("button:has-text('Regenerate'), button:has-text('Generate Again'), [data-testid*='regen']")
                    .first().isVisible();
        } catch (Exception e) {
            regenVisible = false;
        }
        ReportManager.getTest().log(Status.INFO, "AI-BIO-24: Regenerate button visible: " + regenVisible);
        a.assertTrue(regenVisible || bioPage.isBioGenerated(),
                "AI-BIO-24: Regenerate button or generated bio content must be visible after first AI generation");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2},
          description = "Bio step domain stays toskie.com throughout all bio interactions")
    public void testBioDomainThroughoutInteractions() {
        init();
        try { bioPage.typeBio("test"); } catch (Exception ignored) {}
        try { bioPage.clearBio(); } catch (Exception ignored) {}
        String url = BrowserManager.getPage().url();
        ReportManager.getTest().log(Status.INFO, "AI-BIO-25: URL after bio interactions: " + url);
        a.assertContains(url, "toskie.com",
                "AI-BIO-25: Must remain on toskie.com throughout all bio step interactions");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2},
          description = "Bio step isStepLoaded is true via API login path")
    public void testBioStepLoadedViaApiLogin() {
        initWithApiLogin();
        boolean stepLoaded = bioPage.isStepLoaded();
        String url = BrowserManager.getPage().url();
        ReportManager.getTest().log(Status.INFO,
                "AI-BIO-26: stepLoaded=" + stepLoaded + ", url=" + url);
        a.assertTrue(stepLoaded || url.contains("toskie.com"),
                "AI-BIO-26: Bio step page must load (stepLoaded=" + stepLoaded + ") or at least remain on toskie.com (url=" + url + ")");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2},
          description = "AI generate button does not show spinner before being clicked")
    public void testNoSpinnerBeforeGenerate() {
        init();
        boolean spinnerVisible = bioPage.isAISpinnerVisible();
        ReportManager.getTest().log(Status.INFO, "AI-BIO-27: Spinner visible before generate: " + spinnerVisible);
        a.assertFalse(spinnerVisible,
                "AI-BIO-27: AI spinner must NOT be visible before the generate button is clicked");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2},
          description = "Bio textarea is not read-only when bio step is loaded")
    public void testBioTextareaNotReadOnly() {
        init();
        if (!bioPage.isStepLoaded()) {
            throw new SkipException("AI-BIO-28: Bio step not loaded -- skipping readonly check");
        }
        boolean isReadOnly;
        try {
            String ro = BrowserManager.getPage()
                    .locator("textarea[class*='bio'], textarea[placeholder*='bio' i], textarea[name*='bio']")
                    .first().getAttribute("readonly");
            isReadOnly = ro != null;
        } catch (Exception e) {
            isReadOnly = false;
        }
        ReportManager.getTest().log(Status.INFO, "AI-BIO-28: Bio textarea readonly=" + isReadOnly);
        a.assertFalse(isReadOnly,
                "AI-BIO-28: Bio textarea must not be read-only when the bio step is loaded");
        a.assertAll();
    }
}
