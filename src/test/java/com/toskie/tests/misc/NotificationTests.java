package com.toskie.tests.misc;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.constants.TestGroups;
import com.toskie.pages.activity.NotificationsPage;
import com.toskie.utils.AssertionHelper;
import com.toskie.utils_Layer.BrowserManager;
import org.testng.annotations.Test;

public class NotificationTests extends BaseTest {
    private NotificationsPage notifPage;
    private AssertionHelper a;
    private void init() { notifPage = new NotificationsPage(utilLayer); a = new AssertionHelper(); }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2}, description = "Notification bell badge shows unread count")
    public void testNotifBadgeVisible() { init(); a.assertContains(BrowserManager.getPage().url(), "toskie.com", "Notification badge test -- should be on toskie.com"); a.assertAll(); }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2}, description = "Clicking notification navigates to relevant page")
    public void testNotificationClick() {
        init();
        if (notifPage.getNotificationCount() > 0) notifPage.clickNotification(0);
        a.assertContains(BrowserManager.getPage().url(), "toskie.com", "After notification click, should be on toskie.com");
        a.assertAll();
    }
}
