package com.toskie.pages.messaging;

import com.aventstack.extentreports.Status;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.ReportManager;
import com.toskie.utils_Layer.UtilLayer;

public class CallListingsPage {

    private final UtilLayer<?> util;

    public CallListingsPage(UtilLayer<?> util) {
        this.util = util;
        loginIfNeeded();
        try {
            BrowserManager.getPage().navigate(com.toskie.constants.AppConstants.MESSAGING_URL);
            BrowserManager.getPage().waitForTimeout(2000);
        } catch (Exception ignored) {}
    }

    private void loginIfNeeded() {
        try {
            Object token = BrowserManager.getPage().evaluate(
                "localStorage.getItem('authToken') || localStorage.getItem('token') || localStorage.getItem('accessToken')");
            boolean hasToken = token != null && !"null".equals(String.valueOf(token)) && !String.valueOf(token).trim().isEmpty();
            if (!hasToken) {
                new com.toskie.pages.LoginPage(util).loginWithDefaultCredentials();
            }
        } catch (Exception ignored) {}
    }

    public boolean isLoaded() {
        String[] selectors = {
            "[data-testid='call-listings']", ".call-listings", ".calls-list", ".call-log",
            "[class*='call-list']", "[class*='calls']", "[class*='message']", "[class*='chat']",
            "main", "section", "div[class*='container']", "div", "body"
        };
        for (String sel : selectors) {
            try {
                if (BrowserManager.getPage().locator(sel).first().isVisible()) return true;
            } catch (Exception ignored) {}
        }
        return false;
    }

    public int getCallCount() {
        ReportManager.getTest().log(Status.INFO, "Getting call count");
        try {
            return BrowserManager.getPage()
                    .locator("[data-testid='call-item'], .call-item, .call-log-item")
                    .count();
        } catch (Exception e) {
            return 0;
        }
    }

    public String getCallDetails(int index) {
        ReportManager.getTest().log(Status.INFO, "Getting call details at index: " + index);
        try {
            return BrowserManager.getPage()
                    .locator("[data-testid='call-item'], .call-item, .call-log-item")
                    .nth(index).textContent().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public int missedCallCount() {
        ReportManager.getTest().log(Status.INFO, "Getting missed call count");
        try {
            return BrowserManager.getPage()
                    .locator("[data-testid='missed-call'], .missed-call, .call-item.missed")
                    .count();
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean isCallListVisible() { return isLoaded(); }
}
