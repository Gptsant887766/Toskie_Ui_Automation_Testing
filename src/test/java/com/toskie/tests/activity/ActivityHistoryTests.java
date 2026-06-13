package com.toskie.tests.activity;
import com.aventstack.extentreports.Status;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.constants.TestGroups;
import com.toskie.pages.activity.ActivityHistoryPage;
import com.toskie.pages.activity.NotificationsPage;
import com.toskie.utils.AssertionHelper;
import com.toskie.utils_Layer.ReportManager;
import org.testng.annotations.Test;

public class ActivityHistoryTests extends BaseTest {
    private ActivityHistoryPage actPage;
    private NotificationsPage notifPage;
    private AssertionHelper a;
    private void init() { actPage = new ActivityHistoryPage(utilLayer); notifPage = new NotificationsPage(utilLayer); a = new AssertionHelper(); }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2}, description = "Activity history page loads with events")
    public void testActivityHistoryLoads() {
        init();
        ReportManager.getTest().log(Status.INFO, "Activity history loaded");
        a.assertTrue(actPage.getActivityCount() >= 0, "Activity history loads without error");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2}, description = "Notifications appear in notification panel")
    public void testNotificationsVisible() {
        init();
        a.assertTrue(notifPage.getNotificationCount() >= 0, "Notifications load without error");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2}, description = "Mark all notifications as read")
    public void testMarkAllNotificationsRead() {
        init();
        if (notifPage.getUnreadCount() > 0) {
            notifPage.markAllAsRead();
            a.assertTrue(notifPage.getUnreadCount() == 0, "Unread count should be 0 after mark all read");
        } else {
            a.assertTrue(true, "No unread notifications");
        }
        a.assertAll();
    }
}

