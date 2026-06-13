package com.toskie.pages.profile;

import com.aventstack.extentreports.Status;
import com.toskie.locators.ExperienceStepPageLocators;
import com.toskie.models.ExperienceData;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.ReportManager;
import com.toskie.utils_Layer.UtilLayer;

public class ExperienceStepPage {

    private final UtilLayer<?> util;
    private final ExperienceStepPageLocators loc;

    public ExperienceStepPage(UtilLayer<?> util) {
        this.util = util;
        this.loc  = new ExperienceStepPageLocators(BrowserManager.getPage());
    }

    public void addExperience(ExperienceData data) {
        ReportManager.getTest().log(Status.INFO, "Adding experience: " + data.getTitle());
        util.click(loc.addButton, "Add Experience Button");
        BrowserManager.getPage().waitForTimeout(500);
        util.fill(loc.titleInput, data.getTitle(), "Title Input");
        util.fill(loc.companyInput, data.getCompany(), "Company Input");
        util.fill(loc.startDate, data.getStartDate(), "Start Date");
        if (data.isCurrentJob()) {
            try {
                loc.currentJobCheckbox.check();
            } catch (Exception e) {
                util.click(loc.currentJobCheckbox, "Current Job Checkbox");
            }
        } else if (data.getEndDate() != null && !data.getEndDate().isEmpty()) {
            util.fill(loc.endDate, data.getEndDate(), "End Date");
        }
        if (data.getDescription() != null && !data.getDescription().isEmpty()) {
            util.fill(loc.descriptionInput, data.getDescription(), "Description");
        }
        util.click(loc.saveButton, "Save Experience");
        BrowserManager.getPage().waitForTimeout(1000);
    }

    public void editExperience(int index, ExperienceData data) {
        ReportManager.getTest().log(Status.INFO, "Editing experience at index: " + index);
        try {
            loc.editButtons.nth(index).click();
            BrowserManager.getPage().waitForTimeout(500);
            util.fill(loc.titleInput, data.getTitle(), "Title Input");
            util.fill(loc.companyInput, data.getCompany(), "Company Input");
            util.fill(loc.startDate, data.getStartDate(), "Start Date");
            if (!data.isCurrentJob() && data.getEndDate() != null && !data.getEndDate().isEmpty()) {
                util.fill(loc.endDate, data.getEndDate(), "End Date");
            }
            if (data.getDescription() != null && !data.getDescription().isEmpty()) {
                util.fill(loc.descriptionInput, data.getDescription(), "Description");
            }
            util.click(loc.saveButton, "Save Experience Edit");
            BrowserManager.getPage().waitForTimeout(1000);
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "Edit experience issue: " + e.getMessage());
        }
    }

    public void deleteExperience(int index) {
        ReportManager.getTest().log(Status.INFO, "Deleting experience at index: " + index);
        try {
            loc.deleteButtons.nth(index).click();
            BrowserManager.getPage().waitForTimeout(1000);
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "Delete experience issue: " + e.getMessage());
        }
    }

    public int getCount() {
        ReportManager.getTest().log(Status.INFO, "Getting experience count");
        try {
            return loc.list.count();
        } catch (Exception e) {
            return 0;
        }
    }

    public void clickSkip() {
        ReportManager.getTest().log(Status.INFO, "Clicking Skip on Experience step");
        util.click(loc.skipButton, "Skip Button");
        BrowserManager.getPage().waitForTimeout(1000);
    }

    public void clickNext() {
        ReportManager.getTest().log(Status.INFO, "Clicking Next on Experience step");
        try {
            BrowserManager.getPage()
                    .locator("button:has-text('Next'), [data-testid='next-btn']")
                    .click();
            BrowserManager.getPage().waitForTimeout(1000);
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "Next button issue: " + e.getMessage());
        }
    }

    public boolean isCurrentJobToggleEnabled() {
        try {
            return loc.currentJobCheckbox.isChecked();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isEndDateDisabled() {
        try {
            return loc.endDate.isDisabled();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isStepLoaded() {
        try {
            return loc.addButton.isVisible();
        } catch (Exception e) {
            return false;
        }
    }

    public void addExperience(String company, String role, String startDate, String endDate, String desc) {
        addExperience(ExperienceData.builder().company(company).title(role).startDate(startDate).endDate(endDate).description(desc).build());
    }
    public int getExperienceCount()  { return getCount(); }
    public boolean isExperienceSaved(){ return getCount() > 0; }
    public void toggleCurrentJob()   { try { loc.currentJobCheckbox.click(); } catch (Exception ignored) {} }
    public void addMoreExperience()  { util.click(loc.addButton, "Add More Experience"); BrowserManager.getPage().waitForTimeout(500); }
    public void clickSave()          { util.click(loc.saveButton, "Save Experience"); BrowserManager.getPage().waitForTimeout(1000); }
    public String getCompanyError()  { try { return loc.companyInput.evaluate("el => el.validationMessage").toString(); } catch (Exception e) { return ""; } }
}
