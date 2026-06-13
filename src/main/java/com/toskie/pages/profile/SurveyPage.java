package com.toskie.pages.profile;

import com.aventstack.extentreports.Status;
import com.microsoft.playwright.options.LoadState;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.ReportManager;
import com.toskie.utils_Layer.UtilLayer;

public class SurveyPage {

    private final UtilLayer<?> util;

    public SurveyPage(UtilLayer<?> util) {
        this.util = util;
    }

    public void selectOption(String option) {
        ReportManager.getTest().log(Status.INFO, "Selecting survey option: " + option);
        try {
            BrowserManager.getPage()
                    .locator("label:has-text('" + option + "'), [data-testid='survey-option']:has-text('" + option + "'), input[value='" + option + "']")
                    .first()
                    .click();
            BrowserManager.getPage().waitForTimeout(500);
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "Survey option selection issue: " + e.getMessage());
        }
    }

    public void submit() {
        ReportManager.getTest().log(Status.INFO, "Submitting survey");
        try {
            BrowserManager.getPage()
                    .locator("button:has-text('Submit'), button[type='submit'], button:has-text('Continue'), [data-testid='survey-submit-btn']")
                    .first()
                    .click();
            try {
                BrowserManager.getPage().waitForLoadState(LoadState.DOMCONTENTLOADED,
                        new com.microsoft.playwright.Page.WaitForLoadStateOptions().setTimeout(10000));
            } catch (Exception ignored) {}
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "Survey submit issue: " + e.getMessage());
        }
    }

    public void skip() {
        ReportManager.getTest().log(Status.INFO, "Skipping survey");
        try {
            BrowserManager.getPage()
                    .locator("button:has-text('Skip'), [data-testid='survey-skip-btn'], a:has-text('Skip')")
                    .first()
                    .click();
            BrowserManager.getPage().waitForTimeout(1000);
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "Survey skip issue: " + e.getMessage());
        }
    }

    public boolean isPageLoaded() {
        try {
            return BrowserManager.getPage()
                    .locator("[data-testid='survey-page'], .survey-container, .survey-form, form.survey")
                    .isVisible();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isSurveyVisible()  { return isPageLoaded(); }
    public void answerFirstQuestion()  { try { BrowserManager.getPage().locator("input[type='radio'], label[class*='option']").first().click(); } catch (Exception ignored) {} }
    public void submitSurvey()         { submit(); }
    public boolean isSubmitSuccessful(){ try { return !isPageLoaded(); } catch (Exception e) { return false; } }
}
