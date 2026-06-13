package com.toskie.pages.profile;

import com.aventstack.extentreports.Status;
import com.toskie.locators.QualificationStepPageLocators;
import com.toskie.models.QualificationData;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.ReportManager;
import com.toskie.utils_Layer.UtilLayer;

public class QualificationStepPage {

    private final UtilLayer<?> util;
    private final QualificationStepPageLocators loc;

    public QualificationStepPage(UtilLayer<?> util) {
        this.util = util;
        this.loc  = new QualificationStepPageLocators(BrowserManager.getPage());
    }

    public void addQualification(QualificationData data) {
        ReportManager.getTest().log(Status.INFO, "Adding qualification: " + data.getDegree());
        util.click(loc.addButton, "Add Qualification Button");
        BrowserManager.getPage().waitForTimeout(500);
        util.fill(loc.degreeInput, data.getDegree(), "Degree Input");
        util.fill(loc.institutionInput, data.getInstitution(), "Institution Input");
        util.fill(loc.yearInput, data.getYear(), "Year Input");
        if (data.getFieldOfStudy() != null && !data.getFieldOfStudy().isEmpty()) {
            util.fill(loc.fieldOfStudyInput, data.getFieldOfStudy(), "Field of Study Input");
        }
        util.click(loc.saveButton, "Save Qualification");
        BrowserManager.getPage().waitForTimeout(1000);
    }

    public void editQualification(int index, QualificationData data) {
        ReportManager.getTest().log(Status.INFO, "Editing qualification at index: " + index);
        try {
            loc.editButtons.nth(index).click();
            BrowserManager.getPage().waitForTimeout(500);
            util.fill(loc.degreeInput, data.getDegree(), "Degree Input");
            util.fill(loc.institutionInput, data.getInstitution(), "Institution Input");
            util.fill(loc.yearInput, data.getYear(), "Year Input");
            if (data.getFieldOfStudy() != null && !data.getFieldOfStudy().isEmpty()) {
                util.fill(loc.fieldOfStudyInput, data.getFieldOfStudy(), "Field of Study Input");
            }
            util.click(loc.saveButton, "Save Qualification Edit");
            BrowserManager.getPage().waitForTimeout(1000);
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "Edit qualification issue: " + e.getMessage());
        }
    }

    public void deleteQualification(int index) {
        ReportManager.getTest().log(Status.INFO, "Deleting qualification at index: " + index);
        try {
            loc.deleteButtons.nth(index).click();
            BrowserManager.getPage().waitForTimeout(1000);
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "Delete qualification issue: " + e.getMessage());
        }
    }

    public int getCount() {
        ReportManager.getTest().log(Status.INFO, "Getting qualification count");
        try {
            return loc.list.count();
        } catch (Exception e) {
            return 0;
        }
    }

    public void clickSkip() {
        ReportManager.getTest().log(Status.INFO, "Clicking Skip on Qualification step");
        util.click(loc.skipButton, "Skip Button");
        BrowserManager.getPage().waitForTimeout(1000);
    }

    public void clickNext() {
        ReportManager.getTest().log(Status.INFO, "Clicking Next on Qualification step");
        try {
            BrowserManager.getPage()
                    .locator("button:has-text('Next'), [data-testid='next-btn']")
                    .click();
            BrowserManager.getPage().waitForTimeout(1000);
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "Next button issue: " + e.getMessage());
        }
    }

    public boolean isStepLoaded() {
        try {
            return loc.addButton.isVisible();
        } catch (Exception e) {
            return false;
        }
    }

    public void addQualification(String degree, String inst, String field, String start, String end) {
        addQualification(QualificationData.builder().degree(degree).institution(inst).fieldOfStudy(field).startYear(start).endYear(end).build());
    }
    public int getQualificationCount()  { return getCount(); }
    public boolean isQualificationSaved(){ return getCount() > 0; }
    public void addMoreQualification()  { try { BrowserManager.getPage().locator("//button[contains(.,'Add More')] | //button[contains(.,'Add Qualification')]").first().click(); BrowserManager.getPage().waitForTimeout(500); } catch (Exception ignored) {} }
}
