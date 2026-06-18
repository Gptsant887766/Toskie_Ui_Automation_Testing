package com.toskie.pages.subscription;

import com.aventstack.extentreports.Status;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.ReportManager;
import com.toskie.utils_Layer.UtilLayer;

public class SubscriptionPage {

    private final UtilLayer<?> util;
    private static final String SUBSCRIPTION_URL = "https://dev.app.toskie.com/subscription";
    private static final String SETTINGS_URL     = "https://dev.app.toskie.com/settings";

    public SubscriptionPage(UtilLayer<?> util) {
        this.util = util;
    }

    public void navigateToSubscription() {
        try {
            BrowserManager.getPage().navigate(SUBSCRIPTION_URL);
            BrowserManager.getPage().waitForTimeout(2000);
            ReportManager.getTest().log(Status.INFO, "Navigated to subscription page");
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "Subscription page navigation: " + e.getMessage());
        }
    }

    public void navigateToSettings() {
        try {
            BrowserManager.getPage().navigate(SETTINGS_URL);
            BrowserManager.getPage().waitForTimeout(2000);
            ReportManager.getTest().log(Status.INFO, "Navigated to settings page");
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "Settings page navigation: " + e.getMessage());
        }
    }

    public boolean isSubscriptionPageVisible() {
        try {
            return BrowserManager.getPage().locator(
                "[data-testid='subscription'], .subscription-page, " +
                ".subscription-plans, h1:has-text('Subscription'), h2:has-text('Plan')"
            ).first().isVisible();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean hasPlansListed() {
        try {
            return BrowserManager.getPage().locator(
                ".plan-card, .subscription-card, [data-testid='plan-card'], " +
                ".plan-option, .pricing-card"
            ).count() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public int getPlanCount() {
        try {
            return BrowserManager.getPage().locator(
                ".plan-card, .subscription-card, [data-testid='plan-card'], .plan-option"
            ).count();
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean isFreePlanVisible() {
        try {
            return BrowserManager.getPage().locator(
                ":text('Free'), :text('Basic'), [data-plan='free'], .plan-free"
            ).count() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isPremiumPlanVisible() {
        try {
            return BrowserManager.getPage().locator(
                ":text('Premium'), :text('Pro'), [data-plan='premium'], .plan-premium"
            ).count() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public void clickUpgradeButton() {
        try {
            BrowserManager.getPage().locator(
                "button:has-text('Upgrade'), button:has-text('Subscribe'), " +
                "a:has-text('Upgrade'), [data-testid='upgrade-btn']"
            ).first().click();
            BrowserManager.getPage().waitForTimeout(1500);
            ReportManager.getTest().log(Status.INFO, "Clicked Upgrade button");
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "Upgrade button click: " + e.getMessage());
        }
    }

    public boolean isCurrentPlanDisplayed() {
        try {
            return BrowserManager.getPage().locator(
                ":text('Current Plan'), :text('Active'), [data-testid='current-plan'], " +
                ".current-plan, .active-plan"
            ).count() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public String getCurrentPlanName() {
        try {
            return BrowserManager.getPage().locator(
                "[data-testid='current-plan-name'], .current-plan-name, " +
                ".active-plan-title, .plan-name"
            ).first().textContent().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public boolean isSettingsPageLoaded() {
        try {
            return BrowserManager.getPage().locator(
                "[data-testid='settings'], .settings-page, " +
                "h1:has-text('Settings'), h2:has-text('Settings')"
            ).first().isVisible();
        } catch (Exception e) {
            return BrowserManager.getPage().url().contains("settings");
        }
    }
}
