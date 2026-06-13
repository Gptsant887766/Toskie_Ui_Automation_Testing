package com.toskie.locators;
import com.microsoft.playwright.*;

public class ProjectStepPageLocators {
    public final Locator titleInput, descriptionInput, urlInput, imageUpload, projectCards,
            deleteCardBtns, addMoreBtn, saveBtn, nextBtn, prevBtn, noProjectMsg,
            addButton, saveButton, editButtons, removeButtons, list, skipButton, urlError;

    public ProjectStepPageLocators(Page page) {
        titleInput      = page.locator("input[name*='title' i], input[placeholder*='Project Title' i]").first();
        descriptionInput= page.locator("textarea[name*='description' i], textarea[placeholder*='Project Desc' i]").first();
        urlInput        = page.locator("input[name*='url' i], input[type='url'], input[placeholder*='URL' i]").first();
        imageUpload     = page.locator("input[type='file']").first();
        projectCards    = page.locator("[class*='project-card'], [class*='project-item']");
        deleteCardBtns  = page.locator("[class*='project-card'] [class*='delete']");
        addMoreBtn      = page.locator("//button[contains(.,'Add More')] | //button[contains(.,'Add Project')]").first();
        saveBtn         = page.locator("//button[normalize-space()='Save']").first();
        nextBtn         = page.locator("//button[normalize-space()='Next'] | //button[contains(.,'Continue')]").first();
        prevBtn         = page.locator("//button[normalize-space()='Back']").first();
        noProjectMsg    = page.locator("[class*='empty'], [class*='no-project']").first();
        addButton       = addMoreBtn;
        saveButton      = saveBtn;
        editButtons     = page.locator("[class*='project-card'] [class*='edit'], [class*='edit-project']");
        removeButtons   = deleteCardBtns;
        list            = projectCards;
        skipButton      = page.locator("//button[normalize-space()='Skip'] | //button[contains(.,'Skip')]").first();
        urlError        = page.locator("[class*='error']:near(input[type='url']), [class*='url-error']").first();
    }
}
