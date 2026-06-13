package com.toskie.locators;
import com.microsoft.playwright.*;

public class MessagingPageLocators {
    public final Locator conversationList, conversationItems, activeConversation, messageInput,
            sendBtn, messageItems, unreadBadge, searchConversation, acceptBtn, declineBtn,
            requestList, callListBtn, callItems, onlineIndicator, typingIndicator,
            drawerContainer, charCounter, sentTab, receivedTab, errorMsg;

    public MessagingPageLocators(Page page) {
        conversationList   = page.locator("[class*='conversation-list'], [class*='chat-list']").first();
        conversationItems  = page.locator("[class*='conversation-item'], [class*='chat-item']");
        activeConversation = page.locator("[class*='active-chat'], [class*='chat-window']").first();
        messageInput       = page.locator("textarea[placeholder*='message' i], input[placeholder*='message' i]").first();
        sendBtn            = page.locator("//button[normalize-space()='Send'] | [aria-label='Send message']").first();
        messageItems       = page.locator("[class*='message-bubble'], [class*='chat-message']");
        unreadBadge        = page.locator("[class*='unread-count'], [class*='badge']").first();
        searchConversation = page.locator("input[placeholder*='Search conversation' i]").first();
        acceptBtn          = page.locator("//button[normalize-space()='Accept'] | //button[contains(.,'Accept')]").first();
        declineBtn         = page.locator("//button[normalize-space()='Decline'] | //button[contains(.,'Reject')]").first();
        requestList        = page.locator("[class*='request-list'], [class*='message-requests']").first();
        callListBtn        = page.locator("//button[contains(.,'Calls')] | //a[contains(.,'Calls')]").first();
        callItems          = page.locator("[class*='call-item'], [class*='call-history']");
        onlineIndicator    = page.locator("[class*='online'], [class*='status-dot']").first();
        typingIndicator    = page.locator("[class*='typing'], [class*='typing-indicator']").first();
        drawerContainer    = page.locator("[class*='drawer'], [class*='request-drawer'], [class*='message-drawer']").first();
        charCounter        = page.locator("[class*='char-count'], [class*='character-count']").first();
        sentTab            = page.locator("//button[contains(.,'Sent')] | //a[contains(.,'Sent')] | [role='tab']:has-text('Sent')").first();
        receivedTab        = page.locator("//button[contains(.,'Received')] | //a[contains(.,'Received')] | [role='tab']:has-text('Received')").first();
        errorMsg           = page.locator("[class*='error-msg'], [class*='error-message'], [role='alert']").first();
    }
}
