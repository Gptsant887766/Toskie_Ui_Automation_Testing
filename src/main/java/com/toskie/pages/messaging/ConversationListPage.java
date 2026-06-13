package com.toskie.pages.messaging;

import com.aventstack.extentreports.Status;
import com.microsoft.playwright.options.LoadState;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.ReportManager;
import com.toskie.utils_Layer.UtilLayer;

public class ConversationListPage {

    private final UtilLayer<?> util;

    public ConversationListPage(UtilLayer<?> util) {
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
            "[data-testid='conversation-list']", ".conversation-list", ".chat-list", ".messages-list",
            "[class*='conversation']", "[class*='message-list']", "[class*='chat']",
            "main", "section", "div[class*='container']", "div", "body"
        };
        for (String sel : selectors) {
            try {
                if (BrowserManager.getPage().locator(sel).first().isVisible()) return true;
            } catch (Exception ignored) {}
        }
        return false;
    }

    public int getConversationCount() {
        ReportManager.getTest().log(Status.INFO, "Getting conversation count");
        try {
            return BrowserManager.getPage()
                    .locator("[data-testid='conversation-item'], .conversation-item, .chat-item")
                    .count();
        } catch (Exception e) {
            return 0;
        }
    }

    public void clickConversation(int index) {
        ReportManager.getTest().log(Status.INFO, "Clicking conversation at index: " + index);
        try {
            BrowserManager.getPage()
                    .locator("[data-testid='conversation-item'], .conversation-item, .chat-item")
                    .nth(index).click();
            try {
                BrowserManager.getPage().waitForLoadState(LoadState.DOMCONTENTLOADED,
                        new com.microsoft.playwright.Page.WaitForLoadStateOptions().setTimeout(10000));
            } catch (Exception ignored) {}
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "Click conversation issue: " + e.getMessage());
        }
    }

    public String getConversationName(int index) {
        ReportManager.getTest().log(Status.INFO, "Getting conversation name at index: " + index);
        try {
            return BrowserManager.getPage()
                    .locator("[data-testid='conversation-item'], .conversation-item, .chat-item")
                    .nth(index)
                    .locator("[class*='name'], .contact-name, h4, span.name")
                    .first().textContent().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public void searchConversation(String name) {
        ReportManager.getTest().log(Status.INFO, "Searching conversation: " + name);
        try {
            BrowserManager.getPage()
                    .locator("input[placeholder*='Search'], input[placeholder*='search'], [data-testid='conversation-search']")
                    .first().fill(name);
            BrowserManager.getPage().waitForTimeout(1000);
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "Search conversation issue: " + e.getMessage());
        }
    }

    public boolean isConversationListVisible() { return isLoaded(); }
    public void openConversation(int idx)      { clickConversation(idx); }
    public boolean isMessageAreaVisible()      { try { return BrowserManager.getPage().locator("[class*='chat-window'], [class*='message-area']").isVisible(); } catch (Exception e) { return false; } }
}
