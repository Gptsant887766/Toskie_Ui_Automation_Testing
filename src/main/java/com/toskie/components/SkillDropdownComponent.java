package com.toskie.components;
import com.microsoft.playwright.*;
import com.toskie.utils_Layer.*;
import java.util.*;

public class SkillDropdownComponent {
    private final UtilLayer<?> util;
    private final Locator searchInput, dropdown, options, selectedList, deleteButtons, addBtn;

    public SkillDropdownComponent(UtilLayer<?> util) {
        this.util = util;
        Page page = BrowserManager.getPage();
        searchInput   = page.locator("[class*='skill'] input[type='text'], [placeholder*='skill' i]").first();
        dropdown      = page.locator("[class*='skill-dropdown'], [class*='skill-list']").first();
        options       = page.locator("[class*='skill-option'], [class*='dropdown-item']");
        selectedList  = page.locator("[class*='selected-skill'], [class*='skill-tag']");
        deleteButtons = page.locator("[class*='skill-tag'] [class*='delete'], [class*='remove-skill']");
        addBtn        = page.locator("//button[contains(.,'Add Skill')] | //button[contains(.,'Add')]").first();
    }

    public void searchSkill(String keyword) { util.fill(searchInput, keyword, "Skill Search"); }
    public void selectSkill(String name)    { options.filter(new Locator.FilterOptions().setHasText(name)).first().click(); }
    public List<String> getSelectedSkills() {
        List<String> skills = new ArrayList<>();
        for (int i = 0; i < selectedList.count(); i++) skills.add(selectedList.nth(i).textContent().trim());
        return skills;
    }
    public void clearSelection()            { for (int i = 0; i < deleteButtons.count(); i++) deleteButtons.first().click(); }
    public boolean isSkillPresent(String n) { return getSelectedSkills().stream().anyMatch(s -> s.contains(n)); }
}
