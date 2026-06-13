package com.toskie.locators;
import com.microsoft.playwright.*;

public class ExperienceStepPageLocators {
    public final Locator companyInput, roleInput, startDateInput, endDateInput, currentJobCheck,
            descriptionInput, addMoreBtn, experienceCards, deleteCardBtns, saveBtn, nextBtn,
            prevBtn, noExperienceMsg,
            addButton, titleInput, startDate, currentJobCheckbox, endDate, saveButton,
            editButtons, deleteButtons, list, skipButton;

    public ExperienceStepPageLocators(Page page) {
        companyInput   = page.locator("input[name*='company' i], input[placeholder*='Company' i]").first();
        roleInput      = page.locator("input[name*='role' i], input[placeholder*='Role' i], input[placeholder*='Designation' i]").first();
        startDateInput = page.locator("input[name*='startDate' i], input[placeholder*='Start Date' i]").first();
        endDateInput   = page.locator("input[name*='endDate' i], input[placeholder*='End Date' i]").first();
        currentJobCheck= page.locator("input[type='checkbox'][name*='current' i]").first();
        descriptionInput= page.locator("textarea[name*='description' i], textarea[placeholder*='description' i]").first();
        addMoreBtn     = page.locator("//button[contains(.,'Add More')] | //button[contains(.,'Add Experience')]").first();
        experienceCards= page.locator("[class*='experience-card'], [class*='exp-item']");
        deleteCardBtns = page.locator("[class*='experience-card'] [class*='delete'], [class*='remove-exp']");
        saveBtn        = page.locator("//button[normalize-space()='Save']").first();
        nextBtn        = page.locator("//button[normalize-space()='Next'] | //button[contains(.,'Continue')]").first();
        prevBtn        = page.locator("//button[normalize-space()='Back']").first();
        noExperienceMsg  = page.locator("[class*='empty'], [class*='no-experience']").first();
        addButton        = addMoreBtn;
        titleInput       = roleInput;
        startDate        = startDateInput;
        currentJobCheckbox = currentJobCheck;
        endDate          = endDateInput;
        saveButton       = saveBtn;
        editButtons      = page.locator("[class*='experience-card'] [class*='edit'], [class*='edit-exp']");
        deleteButtons    = deleteCardBtns;
        list             = experienceCards;
        skipButton       = page.locator("//button[normalize-space()='Skip'] | //button[contains(.,'Skip')]").first();
    }
}
