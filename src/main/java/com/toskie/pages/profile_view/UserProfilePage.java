package com.toskie.pages.profile_view;

import com.aventstack.extentreports.Status;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.ReportManager;
import com.toskie.utils_Layer.UtilLayer;

import java.nio.file.Paths;

public class UserProfilePage {

    private final UtilLayer<?> util;

    public UserProfilePage(UtilLayer<?> util) {
        this.util = util;
    }

    public boolean isLoaded() {
        try {
            return BrowserManager.getPage()
                    .locator("[data-testid='user-profile'], .user-profile, .profile-container, .profile-page")
                    .isVisible();
        } catch (Exception e) {
            return false;
        }
    }

    public String getProfileName() {
        ReportManager.getTest().log(Status.INFO, "Getting user profile name");
        try {
            return BrowserManager.getPage()
                    .locator("[data-testid='profile-name'], .profile-name, h1.username, h1.name")
                    .first().textContent().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public void clickEditProfile() {
        ReportManager.getTest().log(Status.INFO, "Clicking Edit Profile on user profile page");
        try {
            BrowserManager.getPage()
                    .locator("button:has-text('Edit Profile'), [data-testid='edit-profile-btn'], a:has-text('Edit Profile')")
                    .first().click();
            BrowserManager.getPage().waitForTimeout(1000);
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "Edit profile click issue: " + e.getMessage());
        }
    }

    public void updateName(String name) {
        ReportManager.getTest().log(Status.INFO, "Updating profile name to: " + name);
        try {
            com.microsoft.playwright.Locator nameField = BrowserManager.getPage()
                    .locator("input[name='name'], input[placeholder*='Name'], [data-testid='name-input']")
                    .first();
            nameField.clear();
            nameField.fill(name);
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "Update name issue: " + e.getMessage());
        }
    }

    public void saveProfile() {
        ReportManager.getTest().log(Status.INFO, "Saving user profile");
        try {
            BrowserManager.getPage()
                    .locator("button:has-text('Save'), button[type='submit'], [data-testid='save-profile-btn']")
                    .first().click();
            BrowserManager.getPage().waitForTimeout(1500);
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "Save profile issue: " + e.getMessage());
        }
    }

    public void clickSettings() {
        ReportManager.getTest().log(Status.INFO, "Clicking Settings on user profile");
        try {
            BrowserManager.getPage()
                    .locator("button:has-text('Settings'), a:has-text('Settings'), [data-testid='settings-btn']")
                    .first().click();
            BrowserManager.getPage().waitForTimeout(1000);
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "Settings click issue: " + e.getMessage());
        }
    }

    public void updateProfilePhoto(String imagePath) {
        ReportManager.getTest().log(Status.INFO, "Updating profile photo: " + imagePath);
        try {
            BrowserManager.getPage()
                    .locator("input[type='file'][accept*='image'], [data-testid='profile-photo-upload']")
                    .first().setInputFiles(Paths.get(imagePath));
            BrowserManager.getPage().waitForTimeout(2000);
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "Profile photo update issue: " + e.getMessage());
        }
    }

    public String getShareableURL() {
        ReportManager.getTest().log(Status.INFO, "Getting shareable profile URL");
        try {
            return BrowserManager.getPage()
                    .locator("[data-testid='shareable-url'], .shareable-url, input.share-url, .profile-share-link")
                    .first().inputValue().trim();
        } catch (Exception e) {
            try {
                return BrowserManager.getPage()
                        .locator("[data-testid='shareable-url'], .shareable-url")
                        .first().textContent().trim();
            } catch (Exception ex) {
                return BrowserManager.getPage().url();
            }
        }
    }

    public void clickShare() {
        ReportManager.getTest().log(Status.INFO, "Clicking Share on user profile");
        try {
            BrowserManager.getPage()
                    .locator("button:has-text('Share'), [data-testid='share-btn'], button[aria-label='Share profile']")
                    .first().click();
            BrowserManager.getPage().waitForTimeout(500);
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "Share click issue: " + e.getMessage());
        }
    }
}
