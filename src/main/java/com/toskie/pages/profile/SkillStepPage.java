package com.toskie.pages.profile;

import com.aventstack.extentreports.Status;
import com.toskie.locators.SkillStepPageLocators;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.ReportManager;
import com.toskie.utils_Layer.UtilLayer;

import java.util.ArrayList;
import java.util.List;

public class SkillStepPage {

    private final UtilLayer<?> util;
    private final SkillStepPageLocators loc;

    public SkillStepPage(UtilLayer<?> util) {
        this.util = util;
        this.loc  = new SkillStepPageLocators(BrowserManager.getPage());
    }

    public void searchAndAddSkill(String name, String level) {
        ReportManager.getTest().log(Status.INFO, "Searching and adding skill: " + name + " [" + level + "]");
        util.fill(loc.skillSearchInput, name, "Skill Search Input");
        BrowserManager.getPage().waitForTimeout(1000);
        try {
            loc.options.filter(new com.microsoft.playwright.Locator.FilterOptions().setHasText(name)).first().click();
        } catch (Exception e) {
            try {
                loc.options.first().click();
            } catch (Exception ex) {
                ReportManager.getTest().log(Status.WARNING, "Skill option not found: " + name);
            }
        }
        try {
            loc.experienceLevelSelect.selectOption(level);
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "Level selection issue: " + e.getMessage());
        }
        try {
            util.click(loc.addButton, "Add Skill Button");
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "Add skill button issue: " + e.getMessage());
        }
        BrowserManager.getPage().waitForTimeout(1000);
    }

    public void deleteSkill(String name) {
        ReportManager.getTest().log(Status.INFO, "Deleting skill: " + name);
        try {
            int count = loc.addedList.count();
            for (int i = 0; i < count; i++) {
                String text = loc.addedList.nth(i).textContent();
                if (text != null && text.contains(name)) {
                    loc.deleteButtons.nth(i).click();
                    BrowserManager.getPage().waitForTimeout(1000);
                    return;
                }
            }
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "Delete skill issue: " + e.getMessage());
        }
    }

    public void editSkill(String name, String newLevel) {
        ReportManager.getTest().log(Status.INFO, "Editing skill: " + name + " to level: " + newLevel);
        try {
            int count = loc.addedList.count();
            for (int i = 0; i < count; i++) {
                String text = loc.addedList.nth(i).textContent();
                if (text != null && text.contains(name)) {
                    loc.editButtons.nth(i).click();
                    BrowserManager.getPage().waitForTimeout(500);
                    loc.experienceLevelSelect.selectOption(newLevel);
                    util.click(loc.addButton, "Save Skill Edit");
                    BrowserManager.getPage().waitForTimeout(1000);
                    return;
                }
            }
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "Edit skill issue: " + e.getMessage());
        }
    }

    public int getSkillCount() {
        ReportManager.getTest().log(Status.INFO, "Getting skill count");
        try {
            return loc.addedList.count();
        } catch (Exception e) {
            return 0;
        }
    }

    public List<String> getSkillNames() {
        ReportManager.getTest().log(Status.INFO, "Getting all skill names");
        List<String> names = new ArrayList<>();
        try {
            int count = loc.addedList.count();
            for (int i = 0; i < count; i++) {
                String text = loc.addedList.nth(i).textContent();
                if (text != null) names.add(text.trim());
            }
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "Get skill names issue: " + e.getMessage());
        }
        return names;
    }

    public boolean isSkillPresent(String name) {
        ReportManager.getTest().log(Status.INFO, "Checking if skill is present: " + name);
        try {
            int count = loc.addedList.count();
            for (int i = 0; i < count; i++) {
                String text = loc.addedList.nth(i).textContent();
                if (text != null && text.contains(name)) return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public void clickNext() {
        ReportManager.getTest().log(Status.INFO, "Clicking Next on Skill step");
        util.click(loc.nextButton, "Next Button");
        BrowserManager.getPage().waitForTimeout(1000);
    }

    public boolean isStepLoaded() {
        try {
            return loc.skillSearchInput.isVisible();
        } catch (Exception e) {
            return false;
        }
    }

    public String getNoResultsMessage() {
        try {
            return loc.noResultsMsg.textContent().trim();
        } catch (Exception e) {
            return "";
        }
    }
    public void navigateToSkillStep()       { try { BrowserManager.getPage().navigate(BrowserManager.getPage().url().replaceAll("/profile.*", "/profile/skills")); BrowserManager.getPage().waitForTimeout(1500); } catch (Exception ignored) {} }
    public boolean isNextButtonEnabled()    { try { return loc.nextButton.isEnabled(); } catch (Exception e) { return false; } }
}
