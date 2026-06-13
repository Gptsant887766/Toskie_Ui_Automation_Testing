package com.toskie.locators;
import com.microsoft.playwright.*;

public class SkillStepPageLocators {
    public final Locator searchInput, dropdown, skillOptions, selectedSkills, removeButtons,
            addSkillBtn, saveBtn, nextBtn, prevBtn, noSkillsMsg, skillCount,
            skillSearchInput, options, experienceLevelSelect, addButton, addedList, deleteButtons,
            editButtons, nextButton, noResultsMsg;

    public SkillStepPageLocators(Page page) {
        searchInput  = page.locator("input[placeholder*='Skill' i], input[placeholder*='search skill' i]").first();
        dropdown     = page.locator("[class*='skill-dropdown'], [class*='skill-list']").first();
        skillOptions = page.locator("[class*='skill-option'], [class*='option']");
        selectedSkills= page.locator("[class*='skill-tag'], [class*='selected-skill']");
        removeButtons= page.locator("[class*='skill-tag'] [class*='close'], [class*='remove-skill']");
        addSkillBtn  = page.locator("//button[contains(.,'Add Skill')]").first();
        saveBtn      = page.locator("//button[normalize-space()='Save']").first();
        nextBtn      = page.locator("//button[normalize-space()='Next'] | //button[contains(.,'Continue')]").first();
        prevBtn      = page.locator("//button[normalize-space()='Back']").first();
        noSkillsMsg  = page.locator("[class*='empty-state'], [class*='no-skill']").first();
        skillCount          = page.locator("[class*='skill-count'], [class*='count']").first();
        skillSearchInput    = searchInput;
        options             = skillOptions;
        experienceLevelSelect = page.locator("select[name*='level' i], select[name*='experience' i], [class*='level-select']").first();
        addButton           = addSkillBtn;
        addedList           = selectedSkills;
        deleteButtons       = removeButtons;
        editButtons         = page.locator("[class*='skill-tag'] [class*='edit'], [class*='edit-skill']");
        nextButton          = nextBtn;
        noResultsMsg        = noSkillsMsg;
    }
}
