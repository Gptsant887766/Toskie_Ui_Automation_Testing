package com.toskie.locators;
import com.microsoft.playwright.*;

public class QualificationStepPageLocators {
    public final Locator degreeInput, institutionInput, fieldInput, startYearInput, endYearInput,
            addMoreBtn, qualificationCards, deleteCardBtns, saveBtn, nextBtn, prevBtn, noQualMsg,
            addButton, yearInput, fieldOfStudyInput, saveButton, editButtons, deleteButtons, list, skipButton;

    public QualificationStepPageLocators(Page page) {
        degreeInput       = page.locator("input[name*='degree' i], input[placeholder*='Degree' i]").first();
        institutionInput  = page.locator("input[name*='institution' i], input[placeholder*='Institute' i], input[placeholder*='College' i]").first();
        fieldInput        = page.locator("input[name*='field' i], input[placeholder*='Field of Study' i]").first();
        startYearInput    = page.locator("input[name*='startYear' i], select[name*='startYear' i]").first();
        endYearInput      = page.locator("input[name*='endYear' i], select[name*='endYear' i]").first();
        addMoreBtn        = page.locator("//button[contains(.,'Add More')] | //button[contains(.,'Add Qualification')]").first();
        qualificationCards= page.locator("[class*='qualification-card'], [class*='edu-item']");
        deleteCardBtns    = page.locator("[class*='qualification-card'] [class*='delete']");
        saveBtn           = page.locator("//button[normalize-space()='Save']").first();
        nextBtn           = page.locator("//button[normalize-space()='Next'] | //button[contains(.,'Continue')]").first();
        prevBtn           = page.locator("//button[normalize-space()='Back']").first();
        noQualMsg         = page.locator("[class*='empty'], [class*='no-qualification']").first();
        addButton         = addMoreBtn;
        yearInput         = endYearInput;
        fieldOfStudyInput = fieldInput;
        saveButton        = saveBtn;
        editButtons       = page.locator("[class*='qualification-card'] [class*='edit'], [class*='edit-qual']");
        deleteButtons     = deleteCardBtns;
        list              = qualificationCards;
        skipButton        = page.locator("//button[normalize-space()='Skip'] | //button[contains(.,'Skip')]").first();
    }
}
