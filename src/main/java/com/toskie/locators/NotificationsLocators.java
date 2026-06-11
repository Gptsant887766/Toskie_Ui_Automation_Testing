package com.toskie.locators;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class NotificationsLocators {

    public final Locator notificationsHeader;
    public final Locator markAllReadButton;
    public final Locator clearAllButton;
    public final Locator notificationItems;
    public final Locator unreadNotifications;
    public final Locator readNotifications;
    public final Locator notificationAvatar;
    public final Locator notificationText;
    public final Locator notificationTimestamp;
    public final Locator notificationActionButton;
    public final Locator notificationTypeBooking;
    public final Locator notificationTypeChat;
    public final Locator notificationTypeSystem;
    public final Locator notificationTypeReview;
    public final Locator emptyNotificationsMessage;
    public final Locator notificationsLoadSpinner;
    public final Locator notificationFilterTabs;
    public final Locator allNotificationsTab;
    public final Locator unreadTab;

    public NotificationsLocators(Page page) {
        notificationsHeader    = page.locator("h1:has-text('Notifications'), h2:has-text('Notifications'), [class*='notif-header']");
        markAllReadButton      = page.locator("button:has-text('Mark all read'), [class*='mark-all-read']");
        clearAllButton         = page.locator("button:has-text('Clear All'), [class*='clear-notifications']");
        notificationItems      = page.locator("[class*='notification-item'], [class*='notif-card']");
        unreadNotifications    = page.locator("[class*='notification-item'][class*='unread'], [class*='notif-unread']");
        readNotifications      = page.locator("[class*='notification-item'][class*='read'], [class*='notif-read']");
        notificationAvatar     = page.locator("[class*='notification-item'] img, [class*='notification-item'] [class*='avatar']");
        notificationText       = page.locator("[class*='notification-item'] [class*='text'], [class*='notif-message']");
        notificationTimestamp  = page.locator("[class*='notification-item'] [class*='time']");
        notificationActionButton = page.locator("[class*='notification-item'] button");
        notificationTypeBooking = page.locator("[class*='notif-booking'], [data-type='booking']");
        notificationTypeChat   = page.locator("[class*='notif-chat'], [data-type='chat']");
        notificationTypeSystem = page.locator("[class*='notif-system'], [data-type='system']");
        notificationTypeReview = page.locator("[class*='notif-review'], [data-type='review']");
        emptyNotificationsMessage = page.locator("[class*='empty-notifications'], p:has-text('No notifications')");
        notificationsLoadSpinner = page.locator("[class*='notif-loading'], [class*='spinner']");
        notificationFilterTabs = page.locator("[class*='notif-tabs'], [role='tablist']");
        allNotificationsTab    = page.locator("[role='tab']:has-text('All'), [class*='tab']:has-text('All')");
        unreadTab              = page.locator("[role='tab']:has-text('Unread'), [class*='tab']:has-text('Unread')");
    }
}
