package com.toskie.pages.profile;

import com.aventstack.extentreports.Status;
import com.toskie.locators.BioStepPageLocators;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.ReportManager;
import com.toskie.utils_Layer.UtilLayer;

public class BioStepPage {

    private final UtilLayer<?> util;
    private final BioStepPageLocators loc;

    public BioStepPage(UtilLayer<?> util) {
        this.util = util;
        this.loc  = new BioStepPageLocators(BrowserManager.getPage());
    }

    public void typeBio(String text) {
        ReportManager.getTest().log(Status.INFO, "Typing bio text");
        util.fill(loc.bioTextarea, text, "Bio Textarea");
    }

    public void clickAIGenerate() {
        ReportManager.getTest().log(Status.INFO, "Clicking AI Generate for bio");
        util.click(loc.aiGenerateButton, "AI Generate Button");
    }

    public void waitForAIBio() {
        ReportManager.getTest().log(Status.INFO, "Waiting for AI bio generation");
        try {
            loc.aiSpinner.waitFor(new com.microsoft.playwright.Locator.WaitForOptions()
                    .setState(com.microsoft.playwright.options.WaitForSelectorState.HIDDEN)
                    .setTimeout(30000));
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "AI bio wait issue: " + e.getMessage());
        }
        BrowserManager.getPage().waitForTimeout(500);
    }

    public void clickRegenerate() {
        ReportManager.getTest().log(Status.INFO, "Clicking Regenerate bio");
        util.click(loc.regenerateButton, "Regenerate Button");
    }

    public String getBioText() {
        try {
            return loc.bioTextarea.inputValue().trim();
        } catch (Exception e) {
            try {
                return loc.bioTextarea.textContent().trim();
            } catch (Exception ex) {
                return "";
            }
        }
    }

    public int getCharCount() {
        ReportManager.getTest().log(Status.INFO, "Getting bio char count");
        try {
            String text = loc.charCounter.textContent().trim();
            // parse numbers like "120/500" or "120 characters"
            String digits = text.replaceAll("[^0-9].*", "").trim();
            return Integer.parseInt(digits);
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean isCharLimitExceeded() {
        try {
            String cls = loc.charCounter.getAttribute("class");
            return cls != null && (cls.contains("exceeded") || cls.contains("error") || cls.contains("limit"));
        } catch (Exception e) {
            return false;
        }
    }

    public void clickSave() {
        ReportManager.getTest().log(Status.INFO, "Clicking Save on Bio step");
        util.click(loc.saveButton, "Save Button");
        BrowserManager.getPage().waitForTimeout(1500);
    }

    public boolean isStepLoaded() {
        try {
            return loc.bioTextarea.isVisible();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isAISpinnerVisible() {
        try {
            return loc.aiSpinner.isVisible();
        } catch (Exception e) {
            return false;
        }
    }

    public String getMandatoryError() {
        try {
            return loc.mandatoryError.textContent().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public boolean isAIGenerateBtnVisible() { try { return loc.aiGenerateButton.isVisible(); } catch (Exception e) { return false; } }
    public boolean isBioGenerated()         { try { return !getBioText().isEmpty(); } catch (Exception e) { return false; } }
    public void applyAISuggestion()         { try { BrowserManager.getPage().locator("//button[contains(.,'Apply')] | //button[contains(.,'Use')]").first().click(); BrowserManager.getPage().waitForTimeout(500); } catch (Exception ignored) {} }
    public void clearBio()                  { try { loc.bioTextarea.fill(""); } catch (Exception ignored) {} }
    public String getCharLimitError()       { return isCharLimitExceeded() ? "Char limit exceeded" : ""; }
    public void navigateToBioStep()         { try { BrowserManager.getPage().navigate(BrowserManager.getPage().url().replaceAll("/profile.*", "/profile/bio")); BrowserManager.getPage().waitForTimeout(1500); } catch (Exception ignored) {} }
    public void clickNext()                 { try { BrowserManager.getPage().locator("button:has-text('Next'), button[type='submit']").first().click(); BrowserManager.getPage().waitForTimeout(1000); } catch (Exception e) { clickSave(); } }
}
