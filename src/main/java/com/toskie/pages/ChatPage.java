package com.toskie.pages;

import com.aventstack.extentreports.Status;
import com.toskie.locators.ChatPageLocators;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.ReportManager;
import com.toskie.utils_Layer.UtilLayer;

public class ChatPage {

    private final UtilLayer<?> util;
    private final ChatPageLocators loc;

    public ChatPage(UtilLayer<?> util) {
        this.util = util;
        this.loc  = new ChatPageLocators(BrowserManager.getPage());
    }

    public boolean isOnChatList() {
        try { return loc.chatListContainer.isVisible(); }
        catch (Exception e) { return false; }
    }

    public boolean hasChatItems() {
        return loc.chatItems.count() > 0;
    }

    public void openFirstChat() {
        util.click(loc.firstChatItem, "First Chat Item");
        BrowserManager.getPage().waitForTimeout(1500);
    }

    public void openChatByName(String name) {
        BrowserManager.getPage().locator("[class*='chat-item']:has-text('" + name + "')").first().click();
        BrowserManager.getPage().waitForTimeout(1500);
        ReportManager.getTest().log(Status.INFO, "Opened chat: " + name);
    }

    public void typeMessage(String message) {
        util.fill(loc.messageInput, message, "Message Input");
    }

    public void sendMessage(String message) {
        typeMessage(message);
        util.click(loc.sendButton, "Send Button");
        BrowserManager.getPage().waitForTimeout(1000);
        ReportManager.getTest().log(Status.INFO, "Message sent: " + message);
    }

    public void sendMessageWithEnter(String message) {
        typeMessage(message);
        BrowserManager.getPage().keyboard().press("Enter");
        BrowserManager.getPage().waitForTimeout(1000);
    }

    public int getSentMessageCount() {
        return loc.sentMessages.count();
    }

    public int getReceivedMessageCount() {
        return loc.receivedMessages.count();
    }

    public String getLastSentMessageText() {
        try {
            int count = loc.sentMessages.count();
            if (count > 0) return loc.sentMessages.nth(count - 1).textContent().trim();
        } catch (Exception ignored) {}
        return "";
    }

    public boolean isTypingIndicatorVisible() {
        try { return loc.typingIndicator.isVisible(); }
        catch (Exception e) { return false; }
    }

    public boolean isOnlineStatusVisible() {
        try { return loc.chatPartnerOnlineStatus.isVisible(); }
        catch (Exception e) { return false; }
    }

    public void scrollUpToLoadHistory() {
        BrowserManager.getPage().locator(
            "[class*='message-list'], [class*='messages-container']"
        ).evaluate("el => el.scrollTop = 0");
        BrowserManager.getPage().waitForTimeout(2000);
    }

    public void clickBackButton() {
        util.click(loc.chatBackButton, "Chat Back");
        BrowserManager.getPage().waitForTimeout(800);
    }

    public boolean isEmptyChatVisible() {
        try { return loc.emptyChatMessage.isVisible(); }
        catch (Exception e) { return false; }
    }

    public boolean isOfflineBannerVisible() {
        try { return loc.offlineBanner.isVisible(); }
        catch (Exception e) { return false; }
    }

    public String getChatPartnerName() {
        try { return loc.chatPartnerName.textContent().trim(); }
        catch (Exception e) { return ""; }
    }

    public boolean isSendButtonEnabled() {
        try { return loc.sendButton.isEnabled(); }
        catch (Exception e) { return false; }
    }

    public void verifyMessageDelivered(String message) {
        try {
            BrowserManager.getPage()
                .locator("[class*='message-sent']:has-text('" + message + "') [class*='tick']")
                .waitFor(new com.microsoft.playwright.Locator.WaitForOptions().setTimeout(10000));
            ReportManager.getTest().log(Status.PASS, "Message delivered: " + message);
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "Delivery tick not visible for: " + message);
        }
    }
}
