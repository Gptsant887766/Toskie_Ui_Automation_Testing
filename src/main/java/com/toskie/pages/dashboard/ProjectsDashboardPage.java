package com.toskie.pages.dashboard;
import com.microsoft.playwright.*;
import com.toskie.utils_Layer.*;

public class ProjectsDashboardPage {
    private final UtilLayer<?> util;
    private final Locator projectCards, addProjectBtn, titleInput, descInput, urlInput,
            imageUpload, saveBtn, cancelBtn, deleteButtons, editButtons, noProjectMsg;

    public ProjectsDashboardPage(UtilLayer<?> util) {
        this.util = util;
        Page page = BrowserManager.getPage();
        projectCards = page.locator("[class*='project-card']");
        addProjectBtn= page.locator("//button[contains(.,'Add Project')]").first();
        titleInput   = page.locator("input[name*='title' i], input[placeholder*='Project Title' i]").first();
        descInput    = page.locator("textarea[name*='desc' i], textarea[placeholder*='Description' i]").first();
        urlInput     = page.locator("input[type='url'], input[name*='url' i]").first();
        imageUpload  = page.locator("input[type='file']").first();
        saveBtn      = page.locator("//button[normalize-space()='Save']").first();
        cancelBtn    = page.locator("//button[normalize-space()='Cancel']").first();
        deleteButtons= page.locator("[class*='project-card'] [class*='delete']");
        editButtons  = page.locator("[class*='project-card'] [class*='edit']");
        noProjectMsg = page.locator("[class*='empty'], [class*='no-project']").first();
    }

    public int getProjectCount()         { return (int) projectCards.count(); }
    public void clickAddProject()        { util.click(addProjectBtn, "Add Project"); }
    public void fillProjectTitle(String t){ util.fill(titleInput, t, "Project Title"); }
    public void fillProjectDesc(String d) { util.fill(descInput, d, "Project Desc"); }
    public void saveProject()            { util.click(saveBtn, "Save Project"); }
    public void deleteProject(int idx)   { deleteButtons.nth(idx).click(); }
    public boolean isEmptyState()        { try { return noProjectMsg.isVisible(); } catch (Exception e) { return false; } }
}
