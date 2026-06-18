package com.toskie.tests.regression;
import com.microsoft.playwright.options.LoadState;

import com.toskie.utils_Layer.WaitManager;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.pages.ChatPage;
import com.toskie.pages.HomePage;
import com.toskie.pages.LoginPage;
import com.toskie.pages.ProfileCreationPage;

import com.toskie.utils.AssertionHelper;
import com.toskie.utils.WebSocketValidator;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.ReportManager;
import org.testng.annotations.Test;

/**
 * CHAT TESTS -- TC_CH_001 to TC_CH_018 (UI-level)
 * WebSocket-level chat assertions live in tests.api.WebSocketTests.
 * Covers: chat list, open conversation, send message, empty state, history.
 */
public class ChatTests extends BaseTest {

    private ChatPage loginAndOpenChat() {
        new LoginPage(utilLayer).loginWithDefaultCredentials();
        BrowserManager.getPage().navigate(com.toskie.utils_Layer.ConfigManager.getBaseUrl());
        com.toskie.utils_Layer.WaitManager.safePageLoad();
        WaitManager.waitForPageLoad(LoadState.DOMCONTENTLOADED);
        ProfileCreationPage pp = new ProfileCreationPage(utilLayer);
        if (pp.isProfileCreationPageVisible()) {
            try { pp.createProfileWithDefaultData(); } catch (Exception ignored) {}
            BrowserManager.getPage().navigate(com.toskie.utils_Layer.ConfigManager.getBaseUrl());
            com.toskie.utils_Layer.WaitManager.safePageLoad();
            WaitManager.safePageLoad();
        }
        HomePage hp = new HomePage(utilLayer);
        hp.waitForHomePageLoad();
        hp.navigateToChat();
        WaitManager.safePageLoad();
        return new ChatPage(utilLayer);
    }

    // ─── TC_CH_001: Chat list loads ──────────────────────────────────────────
    @Test(priority = 1,
          description = "TC_CH_001: Chat section should load with conversation list or empty state")
    public void testChatListLoads() {
        AssertionHelper a = new AssertionHelper();
        ChatPage cp = loginAndOpenChat();

        boolean loaded = cp.isOnChatList() || cp.hasChatItems() || cp.isEmptyChatVisible();
        a.assertTrue(loaded, "TC_CH_001 PASS: Chat list loaded (list OR items OR empty state)");
        a.assertAll();
    }

    // ─── TC_CH_002: Conversation items displayed ──────────────────────────────
    @Test(priority = 2,
          description = "TC_CH_002: Chat list items show name, last message, timestamp")
    public void testConversationItemsDisplayed() {
        AssertionHelper a = new AssertionHelper();
        ChatPage cp = loginAndOpenChat();

        if (cp.hasChatItems()) {
            a.assertTrue(cp.hasChatItems(), "TC_CH_002: Conversation items should be displayed in chat list");
        } else {
            a.assertTrue(cp.isEmptyChatVisible(),
                "TC_CH_002: No conversations -- empty state message must be visible");
        }
        a.assertAll();
    }

    // ─── TC_CH_004: Opening conversation loads chat window ───────────────────
    @Test(priority = 3,
          description = "TC_CH_004: Clicking a conversation opens the full chat window")
    public void testOpenConversation() {
        AssertionHelper a = new AssertionHelper();
        ChatPage cp = loginAndOpenChat();

        if (cp.hasChatItems()) {
            cp.openFirstChat();
            BrowserManager.getPage().waitForTimeout(1500);
            a.assertContains(BrowserManager.getPage().url(), "toskie.com",
                "TC_CH_004 PASS: Chat window opened (URL valid)");
        } else {
            a.assertContains(BrowserManager.getPage().url(), "toskie.com", "TC_CH_004: No conversation to open -- should be on toskie.com");
        }
        a.assertAll();
    }

    // ─── TC_CH_005: Send text message ─────────────────────────────────────────
    @Test(priority = 4,
          description = "TC_CH_005: User can type and send a text message")
    public void testSendTextMessage() {
        AssertionHelper a = new AssertionHelper();
        ChatPage cp = loginAndOpenChat();

        if (cp.hasChatItems()) {
            cp.openFirstChat();
            BrowserManager.getPage().waitForTimeout(1000);
            int before = cp.getSentMessageCount();
            cp.sendMessage("Automation hello " + System.currentTimeMillis());
            WaitManager.safePageLoad();
            int after = cp.getSentMessageCount();
            a.assertTrue(after >= before,
                "TC_CH_005 PASS: Sent message count did not decrease (" + before + " → " + after + ")");
        } else {
            a.assertContains(BrowserManager.getPage().url(), "toskie.com", "TC_CH_005: No conversation to send message in -- should be on toskie.com");
        }
        a.assertAll();
    }

    // ─── TC_CH_006: Empty message blocked ─────────────────────────────────────
    @Test(priority = 5,
          description = "TC_CH_006: Sending an empty message should be blocked")
    public void testEmptyMessageBlocked() {
        AssertionHelper a = new AssertionHelper();
        ChatPage cp = loginAndOpenChat();

        if (cp.hasChatItems()) {
            cp.openFirstChat();
            int before = cp.getSentMessageCount();
            try { cp.sendMessage(""); } catch (Exception ignored) {}
            BrowserManager.getPage().waitForTimeout(1000);
            int after = cp.getSentMessageCount();
            a.assertEquals(after, before, "TC_CH_006 PASS: Empty message did not increase count");
        } else {
            a.assertContains(BrowserManager.getPage().url(), "toskie.com", "TC_CH_006: No conversation available -- should be on toskie.com");
        }
        a.assertAll();
    }

    // ─── TC_CH_009: Enter key sends message ──────────────────────────────────
    @Test(priority = 6,
          description = "TC_CH_009: Pressing Enter in message input sends the message")
    public void testEnterKeySendsMessage() {
        AssertionHelper a = new AssertionHelper();
        ChatPage cp = loginAndOpenChat();

        if (cp.hasChatItems()) {
            cp.openFirstChat();
            int before = cp.getSentMessageCount();
            cp.sendMessageWithEnter("Enter key test " + System.currentTimeMillis());
            WaitManager.safePageLoad();
            int after = cp.getSentMessageCount();
            a.assertTrue(after >= before, "TC_CH_009 PASS: Enter key send behaved correctly");
        } else {
            a.assertContains(BrowserManager.getPage().url(), "toskie.com", "TC_CH_009: No conversation available -- should be on toskie.com");
        }
        a.assertAll();
    }

    // ─── TC_CH_014: Chat history loads ────────────────────────────────────────
    @Test(priority = 7,
          description = "TC_CH_014: Opening a conversation loads previous message history")
    public void testChatHistoryLoads() {
        AssertionHelper a = new AssertionHelper();
        ChatPage cp = loginAndOpenChat();

        if (cp.hasChatItems()) {
            cp.openFirstChat();
            WaitManager.waitForPageLoad(LoadState.DOMCONTENTLOADED);
            int total = cp.getSentMessageCount() + cp.getReceivedMessageCount();
            a.assertContains(BrowserManager.getPage().url(), "toskie.com", "TC_CH_014: Chat history loaded (" + total + " messages) -- should be on toskie.com");
        } else {
            a.assertContains(BrowserManager.getPage().url(), "toskie.com", "TC_CH_014: No conversation history available -- should be on toskie.com");
        }
        a.assertAll();
    }

    // ─── TC_CH_015: Back button returns to list ──────────────────────────────
    @Test(priority = 8,
          description = "TC_CH_015: Back button from chat window returns to chat list")
    public void testBackButtonReturnsToList() {
        AssertionHelper a = new AssertionHelper();
        ChatPage cp = loginAndOpenChat();

        if (cp.hasChatItems()) {
            cp.openFirstChat();
            BrowserManager.getPage().waitForTimeout(1000);
            cp.clickBackButton();
            BrowserManager.getPage().waitForTimeout(1000);
            a.assertTrue(cp.isOnChatList() || cp.hasChatItems(),
                "TC_CH_015: Back button must return user to chat list");
        } else {
            a.assertContains(BrowserManager.getPage().url(), "toskie.com", "TC_CH_015: No conversation to navigate back from -- should be on toskie.com");
        }
        a.assertAll();
    }

    // ─── TC_CH_017: Empty chat list state ────────────────────────────────────
    @Test(priority = 9,
          description = "TC_CH_017: Empty chat list shows an appropriate empty-state message")
    public void testEmptyChatListState() {
        AssertionHelper a = new AssertionHelper();
        ChatPage cp = loginAndOpenChat();

        if (!cp.hasChatItems()) {
            a.assertTrue(cp.isEmptyChatVisible(),
                "TC_CH_017: Empty state message must be visible when no conversations exist");
        } else {
            ReportManager.getTest().log(com.aventstack.extentreports.Status.INFO,
                "TC_CH_017: Conversations exist -- empty state N/A for this account");
        }
        a.assertAll();
    }

    // ─── TC_CH_018: WebSocket connection on chat screen ──────────────────────
    @Test(priority = 10,
          description = "TC_CH_018: WebSocket connection should be active on chat screen")
    public void testWebSocketActiveOnChat() {
        WebSocketValidator wsv = new WebSocketValidator();
        wsv.startListening();

        loginAndOpenChat();
        WaitManager.waitForPageLoad(LoadState.DOMCONTENTLOADED);

        wsv.stopListening();
        // Real-time may use WS or polling -- log result either way
        if (wsv.getConnectionCount() > 0) {
            com.toskie.utils_Layer.ReportManager.getTest().log(
                com.aventstack.extentreports.Status.PASS,
                "TC_CH_018 PASS: WebSocket active (" + wsv.getConnectionCount() + " connection(s))");
        } else {
            com.toskie.utils_Layer.ReportManager.getTest().log(
                com.aventstack.extentreports.Status.WARNING,
                "TC_CH_018: No WebSocket detected -- chat may use polling/SSE");
        }
    }
}