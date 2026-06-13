package com.toskie.components;
import com.microsoft.playwright.*;
import com.toskie.utils_Layer.*;

public class DrawerComponent {
    private final UtilLayer<?> util;
    private final Locator drawerContainer, drawerTitle, closeBtn, backdrop;

    public DrawerComponent(UtilLayer<?> util) {
        this.util = util;
        Page page = BrowserManager.getPage();
        drawerContainer = page.locator("[class*='drawer'], [class*='side-panel']").first();
        drawerTitle     = page.locator("[class*='drawer-title'], [class*='drawer'] h2").first();
        closeBtn        = page.locator("[class*='drawer'] [class*='close'], [aria-label='Close drawer']").first();
        backdrop        = page.locator("[class*='backdrop'], [class*='overlay']").first();
    }

    public boolean isDrawerOpen() { try { return drawerContainer.isVisible(); } catch (Exception e) { return false; } }
    public String getDrawerTitle(){ try { return drawerTitle.textContent().trim(); } catch (Exception e) { return ""; } }
    public void closeDrawer()     { util.click(closeBtn, "Close Drawer"); }
}
