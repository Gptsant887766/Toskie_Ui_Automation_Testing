package com.toskie.pages.dashboard;
import com.microsoft.playwright.*;
import com.toskie.utils_Layer.*;

public class AboutDashboardPage {
    private final UtilLayer<?> util;
    private final Locator bioText, editBioBtn, bioTextarea, saveBtn, cancelBtn, charCount;

    public AboutDashboardPage(UtilLayer<?> util) {
        this.util = util;
        Page page = BrowserManager.getPage();
        bioText    = page.locator("[class*='bio-text'], [class*='about-content'] p").first();
        editBioBtn = page.locator("//button[contains(.,'Edit Bio')] | //button[contains(.,'Edit About')]").first();
        bioTextarea= page.locator("textarea[name*='bio' i], textarea[placeholder*='about' i]").first();
        saveBtn    = page.locator("//button[normalize-space()='Save']").first();
        cancelBtn  = page.locator("//button[normalize-space()='Cancel']").first();
        charCount  = page.locator("[class*='char-count']").first();
    }

    public String getBioText()        { try { return bioText.textContent().trim(); } catch (Exception e) { return ""; } }
    public void clickEditBio()        { util.click(editBioBtn, "Edit Bio"); }
    public void updateBio(String bio) { util.fill(bioTextarea, bio, "Bio Textarea"); }
    public void saveBio()             { util.click(saveBtn, "Save Bio"); }
    public void cancelEdit()          { util.click(cancelBtn, "Cancel Edit"); }
    public boolean isBioVisible()     { try { return bioText.isVisible(); } catch (Exception e) { return false; } }
}
