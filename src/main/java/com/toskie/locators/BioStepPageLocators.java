package com.toskie.locators;
import com.microsoft.playwright.*;

public class BioStepPageLocators {
    public final Locator bioTextarea, charCount, aiGenerateBtn, clearBtn, saveBtn, nextBtn,
            prevBtn, bioError, aiSuggestionContainer, applySuggestionBtn,
            aiGenerateButton, aiSpinner, regenerateButton, charCounter, saveButton, mandatoryError;

    public BioStepPageLocators(Page page) {
        bioTextarea          = page.locator("textarea[name*='bio' i], textarea[placeholder*='bio' i], textarea[placeholder*='about' i]").first();
        charCount            = page.locator("[class*='char-count'], [class*='character-count']").first();
        aiGenerateBtn        = page.locator("//button[contains(.,'AI')] | //button[contains(.,'Generate')]").first();
        clearBtn             = page.locator("//button[normalize-space()='Clear'] | //button[contains(.,'Clear Bio')]").first();
        saveBtn              = page.locator("//button[normalize-space()='Save']").first();
        nextBtn              = page.locator("//button[normalize-space()='Next'] | //button[contains(.,'Continue')]").first();
        prevBtn              = page.locator("//button[normalize-space()='Back']").first();
        bioError             = page.locator("[class*='error']:near(textarea)").first();
        aiSuggestionContainer= page.locator("[class*='ai-suggestion'], [class*='suggestion-box']").first();
        applySuggestionBtn   = page.locator("//button[contains(.,'Apply')] | //button[contains(.,'Use')]").first();
        aiGenerateButton     = aiGenerateBtn;
        aiSpinner            = page.locator("[class*='ai-spinner'], [class*='loading'][class*='ai'], [data-testid*='ai-load']").first();
        regenerateButton     = page.locator("//button[contains(.,'Regenerate')] | //button[contains(.,'Refresh')]").first();
        charCounter          = charCount;
        saveButton           = saveBtn;
        mandatoryError       = page.locator("[class*='error'][class*='mandatory'], [class*='required-error'], [class*='bio-error']").first();
    }
}
