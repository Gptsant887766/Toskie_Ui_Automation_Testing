package com.toskie.pages.messaging;

import com.aventstack.extentreports.Status;
import com.toskie.locators.MessagingPageLocators;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.ReportManager;
import com.toskie.utils_Layer.UtilLayer;

import java.util.ArrayList;
import java.util.List;

public class MessageRequestDrawer {

    private final UtilLayer<?> util;
    private final MessagingPageLocators loc;

    public MessageRequestDrawer(UtilLayer<?> util) {
        this.util = util;
        this.loc  = new MessagingPageLocators(BrowserManager.getPage());
    }

    public boolean isDrawerOpen() {
        try {
            return loc.drawerContainer.isVisible();
        } catch (Exception e) {
            return false;
        }
    }

    public void typeMessage(String msg) {
        ReportManager.getTest().log(Status.INFO, "Typing message in drawer");
        util.fill(loc.messageInput, msg, "Message Input");
    }

    public void clickSend() {
        ReportManager.getTest().log(Status.INFO, "Clicking Send in message drawer");
        util.click(loc.sendBtn, "Send Button");
        BrowserManager.getPage().waitForTimeout(1000);
    }

    public int getCharCount() {
        ReportManager.getTest().log(Status.INFO, "Getting message char count");
        try {
            String text = loc.charCounter.textContent().trim();
            String digits = text.replaceAll("[^0-9].*", "").trim();
            return Integer.parseInt(digits);
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean isCharLimitReached() {
        try {
            String cls = loc.charCounter.getAttribute("class");
            return cls != null && (cls.contains("limit") || cls.contains("exceeded") || cls.contains("error"));
        } catch (Exception e) {
            return false;
        }
    }

    public List<String> getSentRequests() {
        ReportManager.getTest().log(Status.INFO, "Getting sent requests list");
        List<String> items = new ArrayList<>();
        try {
            switchToSentTab();
            int count = loc.requestList.count();
            for (int i = 0; i < count; i++) {
                String text = loc.requestList.nth(i).textContent();
                if (text != null) items.add(text.trim());
            }
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "Get sent requests issue: " + e.getMessage());
        }
        return items;
    }

    public List<String> getReceivedRequests() {
        ReportManager.getTest().log(Status.INFO, "Getting received requests list");
        List<String> items = new ArrayList<>();
        try {
            switchToReceivedTab();
            int count = loc.requestList.count();
            for (int i = 0; i < count; i++) {
                String text = loc.requestList.nth(i).textContent();
                if (text != null) items.add(text.trim());
            }
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "Get received requests issue: " + e.getMessage());
        }
        return items;
    }

    public void switchToSentTab() {
        ReportManager.getTest().log(Status.INFO, "Switching to Sent tab in message drawer");
        util.click(loc.sentTab, "Sent Tab");
        BrowserManager.getPage().waitForTimeout(500);
    }

    public void switchToReceivedTab() {
        ReportManager.getTest().log(Status.INFO, "Switching to Received tab in message drawer");
        util.click(loc.receivedTab, "Received Tab");
        BrowserManager.getPage().waitForTimeout(500);
    }

    public void acceptRequest(int index) {
        ReportManager.getTest().log(Status.INFO, "Accepting message request at index: " + index);
        try {
            loc.acceptBtn.nth(index).click();
            BrowserManager.getPage().waitForTimeout(1000);
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "Accept request issue: " + e.getMessage());
        }
    }

    public void declineRequest(int index) {
        ReportManager.getTest().log(Status.INFO, "Declining message request at index: " + index);
        try {
            loc.declineBtn.nth(index).click();
            BrowserManager.getPage().waitForTimeout(1000);
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "Decline request issue: " + e.getMessage());
        }
    }

    public String getErrorMessage() {
        try {
            return loc.errorMsg.textContent().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public void openDrawer()            { try { BrowserManager.getPage().locator("//button[contains(.,'Request')] | //button[contains(.,'Message Request')]").first().click(); BrowserManager.getPage().waitForTimeout(500); } catch (Exception ignored) {} }
    public void sendRequest(String msg) { typeMessage(msg); clickSend(); }
    public boolean isRequestSent()      { try { return BrowserManager.getPage().locator("[class*='success'], [class*='sent']").isVisible(); } catch (Exception e) { return false; } }
}
