package com.toskie.components;
import com.aventstack.extentreports.Status;
import com.microsoft.playwright.*;
import com.toskie.utils_Layer.*;

public class HeaderComponent {
    private final UtilLayer<?> util;
    private final Page page;
    private final Locator logo, searchBar, loginBtn, registerBtn, profileMenu, homeTab, exploreTab, messagesTab, profileTab;

    public HeaderComponent(UtilLayer<?> util) {
        this.util = util;
        this.page = BrowserManager.getPage();
        logo        = page.locator("[class*='logo'], img[alt*='toskie' i]").first();
        searchBar   = page.locator("input[placeholder*='search' i], [class*='search-bar'] input").first();
        loginBtn    = page.locator("//button[normalize-space()='Login'] | //a[normalize-space()='Login']").first();
        registerBtn = page.locator("//button[contains(.,'Register')] | //a[contains(.,'Sign Up')]").first();
        profileMenu = page.locator("[class*='profile-menu'], [class*='avatar'], [aria-label*='profile' i]").first();
        homeTab     = page.locator("//a[normalize-space()='Home'] | //span[normalize-space()='Home']").first();
        exploreTab  = page.locator("//a[normalize-space()='Explore'] | //span[normalize-space()='Explore']").first();
        messagesTab = page.locator("//a[contains(.,'Message')] | //span[contains(.,'Message')]").first();
        profileTab  = page.locator("//a[normalize-space()='Profile'] | //span[normalize-space()='Profile']").first();
    }

    public void clickLogin() { util.forceClick(loginBtn, "Header Login Btn"); }
    public void clickRegister() { util.forceClick(registerBtn, "Header Register Btn"); }
    public void openProfileMenu() { util.click(profileMenu, "Profile Menu"); }
    public void navigateToTab(String tab) {
        switch (tab.toLowerCase()) {
            case "home"    -> util.click(homeTab, "Home Tab");
            case "explore" -> util.click(exploreTab, "Explore Tab");
            case "messages"-> util.click(messagesTab, "Messages Tab");
            case "profile" -> util.click(profileTab, "Profile Tab");
        }
    }
    public boolean isLoggedIn() { try { return profileMenu.isVisible(); } catch (Exception e) { return false; } }
    public String getLoggedInUserName() { try { return profileMenu.textContent().trim(); } catch (Exception e) { return ""; } }
}
