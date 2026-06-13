package com.toskie.pages.dashboard;
import com.microsoft.playwright.*;
import com.toskie.utils_Layer.*;
import java.util.*;

public class SkillsDashboardPage {
    private final UtilLayer<?> util;
    private final Locator skillTags, addSkillBtn, searchInput, skillOptions, removeButtons,
            saveBtn, noSkillMsg;

    public SkillsDashboardPage(UtilLayer<?> util) {
        this.util = util;
        Page page = BrowserManager.getPage();
        skillTags    = page.locator("[class*='skill-tag'], [class*='skill-badge']");
        addSkillBtn  = page.locator("//button[contains(.,'Add Skill')]").first();
        searchInput  = page.locator("input[placeholder*='skill' i]").first();
        skillOptions = page.locator("[class*='skill-option'], [class*='dropdown-item']");
        removeButtons= page.locator("[class*='skill-tag'] [class*='remove'], [class*='skill-tag'] [class*='close']");
        saveBtn      = page.locator("//button[normalize-space()='Save']").first();
        noSkillMsg   = page.locator("[class*='empty'], [class*='no-skill']").first();
    }

    public int getSkillCount()          { return (int) skillTags.count(); }
    public void addSkill(String s)      { util.click(addSkillBtn, "Add Skill"); util.fill(searchInput, s, "Skill Search"); skillOptions.first().click(); }
    public void removeSkill(int idx)    { removeButtons.nth(idx).click(); }
    public void saveSkills()            { util.click(saveBtn, "Save Skills"); }
    public List<String> getSkillNames() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < skillTags.count(); i++) list.add(skillTags.nth(i).textContent().trim());
        return list;
    }
    public boolean isSkillPresent(String s) { return getSkillNames().stream().anyMatch(n -> n.contains(s)); }
}
