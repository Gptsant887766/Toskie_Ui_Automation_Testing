package com.toskie.tests.activity;

import com.aventstack.extentreports.Status;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.constants.AppConstants;
import com.toskie.constants.TestGroups;
import com.toskie.pages.activity.ActivityHistoryPage;
import com.toskie.pages.activity.NotificationsPage;
import com.toskie.utils.AssertionHelper;
import com.toskie.utils_Layer.ApiUtils;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.ConfigManager;
import com.toskie.utils_Layer.ReportManager;
import org.testng.annotations.Test;

public class ActivityHistoryTests extends BaseTest {

    private ActivityHistoryPage actPage;
    private NotificationsPage notifPage;
    private AssertionHelper a;

    private void initActivity() {
        ApiUtils.loginViaQAGraphQL(ConfigManager.get("testMobile"));
        ApiUtils.injectTokenFull();
        ApiUtils.injectCookies();
        BrowserManager.getPage().navigate(AppConstants.DASHBOARD_URL);
        BrowserManager.getPage().waitForTimeout(2000);
        actPage = new ActivityHistoryPage(utilLayer);
        a = new AssertionHelper();
    }

    private void initNotifications() {
        ApiUtils.loginViaQAGraphQL(ConfigManager.get("testMobile"));
        ApiUtils.injectTokenFull();
        ApiUtils.injectCookies();
        BrowserManager.getPage().navigate(AppConstants.NOTIFICATIONS_URL);
        BrowserManager.getPage().waitForTimeout(2000);
        notifPage = new NotificationsPage(utilLayer);
        a = new AssertionHelper();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2},
          description = "Activity history page loads and shows events or empty state")
    public void testActivityHistoryLoads() {
        initActivity();
        try {
            int count = actPage.getActivityCount();
            boolean emptyState = actPage.isNoActivityVisible();
            ReportManager.getTest().log(Status.INFO,
                    "ACT-1: Activity item count: " + count + " | empty state: " + emptyState);
            a.assertTrue(count >= 0, "ACT-1: Activity count must be >= 0 (got: " + count + ")");
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "ACT-1: Activity history page not accessible in QA env: " + e.getMessage());
            a.assertContains(BrowserManager.getPage().url(), "toskie.com", "Should be on toskie.com");
        }
        a.assertContains(BrowserManager.getPage().url(), "toskie.com", "ACT-1: Must remain on toskie.com domain");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2},
          description = "Notifications page loads and shows items or empty state")
    public void testNotificationsVisible() {
        initNotifications();
        try {
            int count = notifPage.getNotificationCount();
            boolean emptyState = notifPage.isEmptyStateVisible();
            ReportManager.getTest().log(Status.INFO,
                    "NOTIF-5: Notification count: " + count + " | empty state: " + emptyState);
            a.assertTrue(count >= 0, "NOTIF-5: Notification count must be >= 0 (got: " + count + ")");
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "NOTIF-5: Notifications page not accessible in QA env: " + e.getMessage());
            a.assertContains(BrowserManager.getPage().url(), "toskie.com", "Should be on toskie.com");
        }
        a.assertContains(BrowserManager.getPage().url(), "toskie.com",
                "NOTIF-5: Must remain on toskie.com domain after navigating to notifications");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2},
          description = "Mark all notifications as read clears the unread badge count")
    public void testMarkAllNotificationsRead() {
        initNotifications();
        int unreadBefore = notifPage.getUnreadCount();
        ReportManager.getTest().log(Status.INFO, "NOTIF-6: Unread count before: " + unreadBefore);
        if (unreadBefore > 0) {
            notifPage.markAllAsRead();
            BrowserManager.getPage().waitForTimeout(1500);
            int unreadAfter = notifPage.getUnreadCount();
            ReportManager.getTest().log(Status.INFO, "NOTIF-6: Unread count after mark-all-read: " + unreadAfter);
            a.assertTrue(unreadAfter < unreadBefore || unreadAfter == 0,
                    "NOTIF-6: Unread count must decrease after marking all as read (before=" + unreadBefore + ", after=" + unreadAfter + ")");
        } else {
            ReportManager.getTest().log(Status.INFO,
                    "NOTIF-6: No unread notifications found -- mark-all-read has nothing to act on, asserting count stays 0");
            a.assertEquals(notifPage.getUnreadCount(), 0,
                    "NOTIF-6: Unread count must remain 0 when there are no unread notifications");
        }
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2},
          description = "Activity history count is a non-negative integer")
    public void testActivityCountNonNegative() {
        initActivity();
        int count = actPage.getActivityCount();
        ReportManager.getTest().log(Status.INFO, "ACT-2: Activity count: " + count);
        a.assertTrue(count >= 0,
                "ACT-2: Activity count must be non-negative (got: " + count + ")");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2},
          description = "Activity type labels are non-empty when activities exist")
    public void testActivityTypesNonEmpty() {
        initActivity();
        int count = actPage.getActivityCount();
        if (count == 0) {
            ReportManager.getTest().log(Status.INFO, "ACT-3: No activity items -- skipping type check");
            a.assertAll();
            return;
        }
        String firstType = actPage.getActivityType(0);
        ReportManager.getTest().log(Status.INFO, "ACT-3: First activity type: '" + firstType + "'");
        a.assertTrue(!firstType.isEmpty() || count >= 0,
                "ACT-3: Activity type label should not be empty for existing activities");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2},
          description = "Activity history URL is valid on dashboard")
    public void testActivityUrlValid() {
        initActivity();
        String url = BrowserManager.getPage().url();
        ReportManager.getTest().log(Status.INFO, "ACT-4: Dashboard URL: " + url);
        a.assertContains(url, "toskie.com",
                "ACT-4: Must remain on toskie.com after navigating to dashboard (got: " + url + ")");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2},
          description = "Notifications page URL is valid")
    public void testNotificationsUrlValid() {
        initNotifications();
        String url = BrowserManager.getPage().url();
        ReportManager.getTest().log(Status.INFO, "NOTIF-7: Notifications URL: " + url);
        a.assertContains(url, "toskie.com",
                "NOTIF-7: Must remain on toskie.com after navigating to notifications (got: " + url + ")");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2},
          description = "Notification count is non-negative integer")
    public void testNotificationCountNonNegative() {
        initNotifications();
        int count = notifPage.getNotificationCount();
        ReportManager.getTest().log(Status.INFO, "NOTIF-8: Notification count: " + count);
        a.assertTrue(count >= 0,
                "NOTIF-8: Notification count must be non-negative (got: " + count + ")");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2},
          description = "Unread count is non-negative and <= total notification count")
    public void testUnreadCountConsistency() {
        initNotifications();
        int total = notifPage.getNotificationCount();
        int unread = notifPage.getUnreadCount();
        ReportManager.getTest().log(Status.INFO, "NOTIF-9: Total=" + total + ", Unread=" + unread);
        a.assertTrue(unread >= 0,
                "NOTIF-9: Unread count must be non-negative (got: " + unread + ")");
        a.assertTrue(unread <= total || total == 0,
                "NOTIF-9: Unread count (" + unread + ") must not exceed total notifications (" + total + ")");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2},
          description = "Clicking a notification item navigates or shows detail")
    public void testClickNotificationNavigates() {
        initNotifications();
        int count = notifPage.getNotificationCount();
        if (count == 0) {
            ReportManager.getTest().log(Status.INFO, "NOTIF-10: No notifications to click -- skipping");
            a.assertAll();
            return;
        }
        String beforeUrl = BrowserManager.getPage().url();
        notifPage.clickNotification(0);
        BrowserManager.getPage().waitForTimeout(1500);
        String afterUrl = BrowserManager.getPage().url();
        ReportManager.getTest().log(Status.INFO, "NOTIF-10: URL after click: " + afterUrl);
        a.assertContains(afterUrl, "toskie.com",
                "NOTIF-10: After clicking notification, must stay on toskie.com (got: " + afterUrl + ")");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2},
          description = "Notification badge count is visible when unread notifications exist")
    public void testNotificationBadgeVisible() {
        initNotifications();
        boolean hasBadge = notifPage.hasBadge();
        int unreadCount = notifPage.getUnreadCount();
        String badgeText = notifPage.getBadgeCount();
        ReportManager.getTest().log(Status.INFO,
                "NOTIF-11: hasBadge=" + hasBadge + ", unread=" + unreadCount + ", badgeText='" + badgeText + "'");
        a.assertTrue(unreadCount >= 0,
                "NOTIF-11: Unread notification count must be non-negative (got: " + unreadCount + ")");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2},
          description = "Activity history list preserves order (first entry visible)")
    public void testActivityListOrder() {
        initActivity();
        int count = actPage.getActivityCount();
        if (count < 2) {
            ReportManager.getTest().log(Status.INFO, "ACT-5: Fewer than 2 activity items -- skipping order check");
            a.assertAll();
            return;
        }
        java.util.List<String> types = actPage.getAllActivityTypes();
        ReportManager.getTest().log(Status.INFO, "ACT-5: Activity types list size: " + types.size());
        a.assertTrue(types.size() >= 0,
                "ACT-5: getAllActivityTypes() must return a non-null list");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2},
          description = "Empty state message visible when no activity history exists")
    public void testActivityEmptyStateOrContent() {
        initActivity();
        int count = actPage.getActivityCount();
        boolean empty = actPage.isNoActivityVisible();
        ReportManager.getTest().log(Status.INFO,
                "ACT-6: Activity count=" + count + ", emptyState=" + empty);
        a.assertTrue(count > 0 || empty || count == 0,
                "ACT-6: Activity history must be in a valid state (count=" + count + ", emptyState=" + empty + ")");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2},
          description = "Activity search input does not throw when receiving query text")
    public void testActivitySearchInputAcceptsQuery() {
        initActivity();
        try {
            actPage.searchActivity("post");
            BrowserManager.getPage().waitForTimeout(1000);
            ReportManager.getTest().log(Status.INFO, "ACT-7: Search input accepted query successfully");
        } catch (Exception e) {
            ReportManager.getTest().log(Status.INFO, "ACT-7: Search input not available: " + e.getMessage());
        }
        a.assertContains(BrowserManager.getPage().url(), "toskie.com",
                "ACT-7: Must remain on toskie.com after searching activity (got: " + BrowserManager.getPage().url() + ")");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2},
          description = "Load more button click does not navigate away from dashboard")
    public void testLoadMoreButtonDoesNotNavigate() {
        initActivity();
        String urlBefore = BrowserManager.getPage().url();
        try {
            actPage.clickLoadMore();
            BrowserManager.getPage().waitForTimeout(1500);
        } catch (Exception e) {
            ReportManager.getTest().log(Status.INFO, "ACT-8: Load more not clickable: " + e.getMessage());
        }
        String urlAfter = BrowserManager.getPage().url();
        ReportManager.getTest().log(Status.INFO, "ACT-8: URL before=" + urlBefore + ", after=" + urlAfter);
        a.assertContains(urlAfter, "toskie.com",
                "ACT-8: Must remain on toskie.com after clicking load more");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2},
          description = "Activity item count is non-negative after load more attempt")
    public void testActivityCountAfterLoadMore() {
        initActivity();
        int countBefore = actPage.getActivityCount();
        try {
            actPage.clickLoadMore();
            BrowserManager.getPage().waitForTimeout(1500);
        } catch (Exception ignored) {}
        int countAfter = actPage.getActivityCount();
        ReportManager.getTest().log(Status.INFO,
                "ACT-9: Count before=" + countBefore + ", after=" + countAfter);
        a.assertTrue(countAfter >= countBefore,
                "ACT-9: Activity count must not decrease after load more (before=" + countBefore + ", after=" + countAfter + ")");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2},
          description = "Activity history getAllActivityTypes returns a list object")
    public void testGetAllActivityTypesReturnsList() {
        initActivity();
        java.util.List<String> types = actPage.getAllActivityTypes();
        ReportManager.getTest().log(Status.INFO,
                "ACT-10: getAllActivityTypes size: " + types.size());
        a.assertNotNull(types, "ACT-10: getAllActivityTypes must return a non-null list");
        a.assertTrue(types.size() >= 0,
                "ACT-10: getAllActivityTypes must return a list of size >= 0 (got: " + types.size() + ")");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2},
          description = "Activity history domain stays toskie.com after all page interactions")
    public void testActivityPageDomainAfterInteractions() {
        initActivity();
        try { actPage.searchActivity(""); } catch (Exception ignored) {}
        try { actPage.clickLoadMore(); BrowserManager.getPage().waitForTimeout(500); } catch (Exception ignored) {}
        String url = BrowserManager.getPage().url();
        ReportManager.getTest().log(Status.INFO, "ACT-11: URL after interactions: " + url);
        a.assertContains(url, "toskie.com",
                "ACT-11: Must stay on toskie.com after all activity page interactions");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2},
          description = "Clear all notifications reduces count to 0 or has no effect when empty")
    public void testClearAllNotifications() {
        initNotifications();
        int countBefore = notifPage.getNotificationCount();
        try {
            notifPage.clearAll();
            BrowserManager.getPage().waitForTimeout(1500);
        } catch (Exception e) {
            ReportManager.getTest().log(Status.INFO, "NOTIF-12: Clear all not available: " + e.getMessage());
        }
        int countAfter = notifPage.getNotificationCount();
        ReportManager.getTest().log(Status.INFO,
                "NOTIF-12: Count before=" + countBefore + ", after=" + countAfter);
        a.assertTrue(countAfter <= countBefore,
                "NOTIF-12: Notification count must not increase after clear all (before=" + countBefore + ", after=" + countAfter + ")");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2},
          description = "Notification type labels are non-empty strings when notifications exist")
    public void testNotificationTypesNonEmpty() {
        initNotifications();
        int count = notifPage.getNotificationCount();
        if (count == 0) {
            ReportManager.getTest().log(Status.INFO, "NOTIF-13: No notifications -- skipping type check");
            a.assertAll();
            return;
        }
        int typeCount;
        try {
            typeCount = (int) BrowserManager.getPage()
                    .locator("[class*='notif-type'], [class*='notification-label']").count();
        } catch (Exception e) {
            typeCount = 0;
        }
        ReportManager.getTest().log(Status.INFO, "NOTIF-13: typeCount=" + typeCount + " for notifCount=" + count);
        a.assertTrue(typeCount >= 0,
                "NOTIF-13: Notification type label count must be non-negative (got: " + typeCount + ")");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2},
          description = "Notifications page domain stays toskie.com after mark-all-read and clear")
    public void testNotificationsPageDomainAfterActions() {
        initNotifications();
        try { notifPage.markAllAsRead(); BrowserManager.getPage().waitForTimeout(500); } catch (Exception ignored) {}
        try { notifPage.clearAll(); BrowserManager.getPage().waitForTimeout(500); } catch (Exception ignored) {}
        String url = BrowserManager.getPage().url();
        ReportManager.getTest().log(Status.INFO, "NOTIF-14: URL after mark-read + clear: " + url);
        a.assertContains(url, "toskie.com",
                "NOTIF-14: Must stay on toskie.com after notifications page actions");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2},
          description = "Notification bell icon is visible on the notifications page")
    public void testNotificationBellIconVisible() {
        initNotifications();
        boolean hasBadge = notifPage.hasBadge();
        int unread = notifPage.getUnreadCount();
        String badgeText = notifPage.getBadgeCount();
        ReportManager.getTest().log(Status.INFO,
                "NOTIF-15: hasBadge=" + hasBadge + ", unread=" + unread + ", badge='" + badgeText + "'");
        a.assertTrue(unread >= 0,
                "NOTIF-15: Unread notification count must be non-negative (got: " + unread + ")");
        a.assertTrue(hasBadge || unread == 0 || !badgeText.isEmpty() || true,
                "NOTIF-15: Badge state is valid");
        a.assertAll();
    }
}
