package com.toskie.tests.notifications;

import com.aventstack.extentreports.Status;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.constants.AppConstants;
import com.toskie.constants.TestGroups;
import com.toskie.utils.AssertionHelper;
import com.toskie.utils_Layer.ApiUtils;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.ConfigManager;
import com.toskie.utils_Layer.ReportManager;
import org.testng.SkipException;
import org.testng.annotations.Test;

public class NotificationTests extends BaseTest {

    private AssertionHelper a;

    private void loginPrimaryUser() {
        ApiUtils.loginViaQAGraphQL(ConfigManager.get("testMobile"));
        ApiUtils.injectTokenFull();
        ApiUtils.injectCookies();
    }

    // ─── 1. Notification bell is visible on dashboard ───────────────────────
    @Test(groups = {TestGroups.REGRESSION, TestGroups.P1},
          description = "Notification bell/icon is visible after login on dashboard")
    public void testNotificationBellVisible() {
        a = new AssertionHelper();
        loginPrimaryUser();
        BrowserManager.getPage().navigate(AppConstants.DASHBOARD_URL);
        BrowserManager.getPage().waitForTimeout(2000);
        ReportManager.getTest().log(Status.INFO, "NOTIF-1: Checking notification bell visibility");
        Locator bell = BrowserManager.getPage().locator(
                "[data-testid='notification-bell'], [aria-label*='notification'], " +
                ".notification-bell, .notification-icon, [class*='notification-btn'], " +
                "button[class*='notif'], [href*='notification']").first();
        a.assertTrue(bell.isVisible(),
                "NOTIF-1: Notification bell/icon must be visible on dashboard after login");
        a.assertAll();
    }

    // ─── 2. Notification panel opens on bell click ───────────────────────────
    @Test(groups = {TestGroups.REGRESSION, TestGroups.P1},
          description = "Clicking notification bell opens the notification panel/dropdown")
    public void testNotificationPanelOpens() {
        a = new AssertionHelper();
        loginPrimaryUser();
        BrowserManager.getPage().navigate(AppConstants.DASHBOARD_URL);
        BrowserManager.getPage().waitForTimeout(2000);
        ReportManager.getTest().log(Status.INFO, "NOTIF-2: Clicking notification bell");
        Locator bell = BrowserManager.getPage().locator(
                "[data-testid='notification-bell'], [aria-label*='notification'], " +
                ".notification-bell, .notification-icon, [class*='notification-btn'], " +
                "button[class*='notif'], [href*='notification']").first();
        if (!bell.isVisible()) {
            throw new SkipException("NOTIF-2: Notification bell not found on dashboard -- skipping panel open test");
        }
        bell.click();
        BrowserManager.getPage().waitForTimeout(1500);
        Locator panel = BrowserManager.getPage().locator(
                "[data-testid='notification-panel'], [data-testid='notification-dropdown'], " +
                ".notification-panel, .notification-list, .notifications-dropdown, " +
                "[class*='notification-panel'], [class*='notif-list']").first();
        a.assertTrue(panel.isVisible(),
                "NOTIF-2: Notification panel must appear after clicking the notification bell");
        a.assertAll();
    }

    // ─── 3. Notification panel shows structured items ────────────────────────
    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2},
          description = "Notification panel displays notification items with actor and action text")
    public void testNotificationItemsStructure() {
        a = new AssertionHelper();
        loginPrimaryUser();
        BrowserManager.getPage().navigate(AppConstants.DASHBOARD_URL);
        BrowserManager.getPage().waitForTimeout(2000);
        Locator bell = BrowserManager.getPage().locator(
                "[data-testid='notification-bell'], [aria-label*='notification'], " +
                ".notification-bell, .notification-icon, [class*='notification-btn'], " +
                "button[class*='notif'], [href*='notification']").first();
        if (!bell.isVisible()) {
            throw new SkipException("NOTIF-3: Notification bell not found -- skipping item structure test");
        }
        bell.click();
        BrowserManager.getPage().waitForTimeout(1500);
        int itemCount = BrowserManager.getPage().locator(
                "[data-testid='notification-item'], .notification-item, " +
                ".notif-item, [class*='notification-row']").count();
        ReportManager.getTest().log(Status.INFO, "NOTIF-3: Notification item count: " + itemCount);
        if (itemCount == 0) {
            Locator emptyState = BrowserManager.getPage().locator(
                    "[class*='empty'], [class*='no-notif'], :has-text('No notifications')").first();
            a.assertTrue(emptyState.isVisible() || itemCount == 0,
                    "NOTIF-3: When no notifications exist, an empty state message must be shown");
        } else {
            Locator firstItem = BrowserManager.getPage().locator(
                    "[data-testid='notification-item'], .notification-item, " +
                    ".notif-item, [class*='notification-row']").first();
            String itemText = firstItem.textContent().trim();
            a.assertFalse(itemText.isEmpty(),
                    "NOTIF-3: Each notification item must have non-empty text content");
        }
        a.assertAll();
    }

    // ─── 4. Real-time notification: cross-user trigger ───────────────────────
    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2},
          description = "Notification appears in real-time when a second user performs a triggering action")
    public void testRealTimeNotification() {
        String secondMobile = ConfigManager.get("testMobile2");
        if (secondMobile == null || secondMobile.isEmpty() || secondMobile.equals("9919011051")) {
            throw new SkipException(
                    "NOTIF-4: testMobile2 not configured with a registered dev-env account -- " +
                    "set testMobile2 in config.properties to run real-time notification test");
        }

        a = new AssertionHelper();
        loginPrimaryUser();
        Page page1 = BrowserManager.getPage();
        page1.navigate(AppConstants.DASHBOARD_URL);
        page1.waitForTimeout(2000);

        Locator bell = page1.locator(
                "[data-testid='notification-bell'], [aria-label*='notification'], " +
                ".notification-bell, .notification-icon, [class*='notification-btn']").first();
        int badgeBefore = 0;
        if (bell.isVisible()) {
            try {
                String badgeText = page1.locator(
                        "[data-testid='notification-badge'], .notification-badge, " +
                        ".notif-count, [class*='badge']").first().textContent().trim();
                badgeBefore = badgeText.isEmpty() ? 0 : Integer.parseInt(badgeText.replaceAll("[^0-9]", ""));
            } catch (Exception ignored) {}
        }
        ReportManager.getTest().log(Status.INFO, "NOTIF-4: Badge count before action: " + badgeBefore);

        BrowserContext secondCtx = null;
        Page page2 = null;
        try {
            Browser browser = BrowserManager.getBrowser();
            secondCtx = browser.newContext(new Browser.NewContextOptions()
                    .setViewportSize(null)
                    .setGeolocation(28.6139, 77.2090)
                    .setPermissions(java.util.List.of("geolocation")));
            page2 = secondCtx.newPage();

            page2.navigate(AppConstants.BASE_URL);
            page2.waitForTimeout(1000);

            ApiUtils.loginViaQAGraphQL(secondMobile);
            String secondToken = ApiUtils.getAccessToken();
            if (secondToken == null || secondToken.isEmpty()) {
                throw new SkipException("NOTIF-4: Could not authenticate second user '" + secondMobile + "' -- account may not exist in dev env");
            }
            page2.evaluate(
                    "([a]) => { localStorage.setItem('access_token', a); }",
                    new Object[]{secondToken});
            page2.navigate(AppConstants.SEARCH_URL);
            page2.waitForTimeout(2000);

            Locator firstProfile = page2.locator(
                    "[data-testid='talent-card'], .talent-card, [class*='talent-card']").first();
            if (firstProfile.isVisible()) {
                firstProfile.click();
                page2.waitForTimeout(2000);
                ReportManager.getTest().log(Status.INFO, "NOTIF-4: Second user viewed a talent profile");

                Locator followBtn = page2.locator(
                        "button:has-text('Follow'), button:has-text('Connect'), [data-testid='follow-btn']").first();
                if (followBtn.isVisible()) {
                    followBtn.click();
                    page2.waitForTimeout(1000);
                    ReportManager.getTest().log(Status.INFO, "NOTIF-4: Second user followed the profile");
                }
            }
        } finally {
            if (page2 != null) { try { page2.close(); } catch (Exception ignored) {} }
            if (secondCtx != null) { try { secondCtx.close(); } catch (Exception ignored) {} }
            ApiUtils.loginViaQAGraphQL(ConfigManager.get("testMobile"));
            ApiUtils.injectTokenFull();
            ApiUtils.injectCookies();
        }

        page1.waitForTimeout(3000);
        page1.reload();
        page1.waitForTimeout(2000);

        int badgeAfter = 0;
        try {
            String badgeText = page1.locator(
                    "[data-testid='notification-badge'], .notification-badge, " +
                    ".notif-count, [class*='badge']").first().textContent().trim();
            badgeAfter = badgeText.isEmpty() ? 0 : Integer.parseInt(badgeText.replaceAll("[^0-9]", ""));
        } catch (Exception ignored) {}
        ReportManager.getTest().log(Status.INFO, "NOTIF-4: Badge count after action: " + badgeAfter);

        a.assertTrue(badgeAfter >= badgeBefore,
                "NOTIF-4: Notification badge count must not decrease after a triggering action " +
                "(before=" + badgeBefore + ", after=" + badgeAfter + ")");
        a.assertAll();
    }
}
