package com.toskie.pages.activity;
import com.aventstack.extentreports.Status;
import com.microsoft.playwright.*;
import com.toskie.utils_Layer.*;
import java.util.*;

public class NotificationsPage {
    private final UtilLayer<?> util;
    private final Locator notifItems, unreadItems, markAllReadBtn, notifBell, notifBadge,
            notifTypes, clearAllBtn, noNotifMsg;

    public NotificationsPage(UtilLayer<?> util) {
        this.util = util;
        Page page = BrowserManager.getPage();
        notifItems    = page.locator("[class*='notification-item'], [class*='notif-item']");
        unreadItems   = page.locator("[class*='notification-item'][class*='unread'], [class*='unread-notification']");
        markAllReadBtn= page.locator("//button[contains(.,'Mark all')] | //button[contains(.,'Read All')]").first();
        notifBell     = page.locator("[class*='notification-bell'], [aria-label*='notification' i]").first();
        notifBadge    = page.locator("[class*='notification-badge'], [class*='notif-count']").first();
        notifTypes    = page.locator("[class*='notif-type'], [class*='notification-label']");
        clearAllBtn   = page.locator("//button[contains(.,'Clear All')]").first();
        noNotifMsg    = page.locator("[class*='empty-notification'], [class*='no-notif']").first();
    }

    public int getNotificationCount()   { return (int) notifItems.count(); }
    public int getUnreadCount()         { return (int) unreadItems.count(); }
    public void markAllAsRead()         { util.click(markAllReadBtn, "Mark All Read"); }
    public void clearAll()              { util.click(clearAllBtn, "Clear All"); }
    public boolean hasBadge()           { try { return notifBadge.isVisible(); } catch (Exception e) { return false; } }
    public String getBadgeCount()       { try { return notifBadge.textContent().trim(); } catch (Exception e) { return "0"; } }
    public boolean isEmptyStateVisible(){ try { return noNotifMsg.isVisible(); } catch (Exception e) { return false; } }
    public void clickNotification(int i){ notifItems.nth(i).click(); }
}
