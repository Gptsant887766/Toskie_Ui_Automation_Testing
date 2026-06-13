package com.toskie.pages.profile;

import com.aventstack.extentreports.Status;
import com.toskie.locators.ProjectStepPageLocators;
import com.toskie.models.ProjectData;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.ReportManager;
import com.toskie.utils_Layer.UtilLayer;

import java.nio.file.Paths;

public class ProjectStepPage {

    private final UtilLayer<?> util;
    private final ProjectStepPageLocators loc;

    public ProjectStepPage(UtilLayer<?> util) {
        this.util = util;
        this.loc  = new ProjectStepPageLocators(BrowserManager.getPage());
    }

    public void addProject(ProjectData data) {
        ReportManager.getTest().log(Status.INFO, "Adding project: " + data.getTitle());
        util.click(loc.addButton, "Add Project Button");
        BrowserManager.getPage().waitForTimeout(500);
        util.fill(loc.titleInput, data.getTitle(), "Project Title Input");
        if (data.getDescription() != null && !data.getDescription().isEmpty()) {
            util.fill(loc.descriptionInput, data.getDescription(), "Project Description");
        }
        if (data.getUrl() != null && !data.getUrl().isEmpty()) {
            util.fill(loc.urlInput, data.getUrl(), "Project URL Input");
        }
        if (data.getImagePath() != null && !data.getImagePath().isEmpty()) {
            try {
                loc.imageUpload.setInputFiles(Paths.get(data.getImagePath()));
                BrowserManager.getPage().waitForTimeout(1000);
            } catch (Exception e) {
                ReportManager.getTest().log(Status.WARNING, "Project image upload issue: " + e.getMessage());
            }
        }
        util.click(loc.saveButton, "Save Project");
        BrowserManager.getPage().waitForTimeout(1000);
    }

    public void updateProject(int index, ProjectData data) {
        ReportManager.getTest().log(Status.INFO, "Updating project at index: " + index);
        try {
            loc.editButtons.nth(index).click();
            BrowserManager.getPage().waitForTimeout(500);
            util.fill(loc.titleInput, data.getTitle(), "Project Title Input");
            if (data.getDescription() != null && !data.getDescription().isEmpty()) {
                util.fill(loc.descriptionInput, data.getDescription(), "Project Description");
            }
            if (data.getUrl() != null && !data.getUrl().isEmpty()) {
                util.fill(loc.urlInput, data.getUrl(), "Project URL Input");
            }
            util.click(loc.saveButton, "Save Project Edit");
            BrowserManager.getPage().waitForTimeout(1000);
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "Update project issue: " + e.getMessage());
        }
    }

    public void removeProject(int index) {
        ReportManager.getTest().log(Status.INFO, "Removing project at index: " + index);
        try {
            loc.removeButtons.nth(index).click();
            BrowserManager.getPage().waitForTimeout(1000);
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "Remove project issue: " + e.getMessage());
        }
    }

    public int getCount() {
        ReportManager.getTest().log(Status.INFO, "Getting project count");
        try {
            return loc.list.count();
        } catch (Exception e) {
            return 0;
        }
    }

    public void clickSkip() {
        ReportManager.getTest().log(Status.INFO, "Clicking Skip on Project step");
        util.click(loc.skipButton, "Skip Button");
        BrowserManager.getPage().waitForTimeout(1000);
    }

    public boolean isURLValid(String url) {
        ReportManager.getTest().log(Status.INFO, "Checking URL validity: " + url);
        try {
            util.fill(loc.urlInput, url, "URL Input for validation");
            BrowserManager.getPage().waitForTimeout(500);
            return !loc.urlError.isVisible();
        } catch (Exception e) {
            return true;
        }
    }

    public boolean isStepLoaded() {
        try {
            return loc.addButton.isVisible();
        } catch (Exception e) {
            return false;
        }
    }

    public void addProject(String title, String desc, String url) {
        addProject(ProjectData.builder().title(title).description(desc).projectUrl(url).build());
    }
    public int getProjectCount()   { return getCount(); }
    public boolean isProjectSaved(){ return getCount() > 0; }
    public void deleteProject(int i){ removeProject(i); }
    public String getUrlError()    { try { return BrowserManager.getPage().locator("[class*='error']:has-text('URL'), [class*='url-error']").textContent().trim(); } catch (Exception e) { return ""; } }
}
