package com.toskie.locators;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class SettingsPageLocators {

    public final Locator settingsHeader;
    public final Locator profileSection;
    public final Locator editProfileButton;
    public final Locator changePhoneNumber;
    public final Locator changeEmail;
    public final Locator changePassword;
    public final Locator notificationSettings;
    public final Locator pushNotificationsToggle;
    public final Locator emailNotificationsToggle;
    public final Locator smsNotificationsToggle;
    public final Locator privacySettings;
    public final Locator profileVisibilityToggle;
    public final Locator locationSharingToggle;
    public final Locator blockedUsersSection;
    public final Locator languageSettings;
    public final Locator themeToggle;
    public final Locator helpAndSupport;
    public final Locator faqLink;
    public final Locator contactSupportButton;
    public final Locator reportBugButton;
    public final Locator aboutSection;
    public final Locator appVersionText;
    public final Locator termsOfServiceLink;
    public final Locator privacyPolicyLink;
    public final Locator logoutButton;
    public final Locator deleteAccountButton;
    public final Locator logoutConfirmButton;
    public final Locator deleteConfirmInput;
    public final Locator deleteConfirmButton;

    public SettingsPageLocators(Page page) {
        settingsHeader         = page.locator("h1:has-text('Settings'), [class*='settings-header']");
        profileSection         = page.locator("[class*='profile-section'], [class*='account-section']");
        editProfileButton      = page.locator("button:has-text('Edit Profile'), [class*='edit-profile']");
        changePhoneNumber      = page.locator("button:has-text('Change Phone'), [class*='change-phone']");
        changeEmail            = page.locator("button:has-text('Change Email'), [class*='change-email']");
        changePassword         = page.locator("button:has-text('Change Password'), [class*='change-password']");
        notificationSettings   = page.locator("[class*='notification-settings'], [href*='notifications']");
        pushNotificationsToggle= page.locator("[id*='push-notif'], [class*='push-toggle']");
        emailNotificationsToggle=page.locator("[id*='email-notif'], [class*='email-toggle']");
        smsNotificationsToggle = page.locator("[id*='sms-notif'], [class*='sms-toggle']");
        privacySettings        = page.locator("[class*='privacy-settings'], [href*='privacy']");
        profileVisibilityToggle= page.locator("[id*='profile-visibility'], [class*='visibility-toggle']");
        locationSharingToggle  = page.locator("[id*='location-sharing'], [class*='location-toggle']");
        blockedUsersSection    = page.locator("[class*='blocked-users'], [href*='blocked']");
        languageSettings       = page.locator("[class*='language-settings'], [href*='language']");
        themeToggle            = page.locator("[id*='theme'], [class*='theme-toggle'], [aria-label*='dark mode' i]");
        helpAndSupport         = page.locator("[class*='help-support'], [href*='help'], button:has-text('Help')");
        faqLink                = page.locator("a:has-text('FAQ'), [href*='faq']");
        contactSupportButton   = page.locator("button:has-text('Contact Support'), [href*='support']");
        reportBugButton        = page.locator("button:has-text('Report Bug'), [class*='report-bug']");
        aboutSection           = page.locator("[class*='about-section'], [href*='about']");
        appVersionText         = page.locator("[class*='app-version'], span:has-text('Version'), p:has-text('v1.')");
        termsOfServiceLink     = page.locator("a:has-text('Terms of Service'), a:has-text('Terms & Conditions')");
        privacyPolicyLink      = page.locator("a:has-text('Privacy Policy')");
        logoutButton           = page.locator("button:has-text('Logout'), button:has-text('Log Out'), button:has-text('Sign Out')");
        deleteAccountButton    = page.locator("button:has-text('Delete Account'), [class*='delete-account']");
        logoutConfirmButton    = page.locator("[class*='confirm-logout'] button:has-text('Logout'), [role='dialog'] button:has-text('Logout')");
        deleteConfirmInput     = page.locator("input[placeholder*='DELETE'], input[placeholder*='confirm' i]");
        deleteConfirmButton    = page.locator("[role='dialog'] button:has-text('Delete'), [class*='confirm-delete']");
    }
}
