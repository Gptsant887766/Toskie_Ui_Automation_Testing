package com.toskie.locators;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class ChatPageLocators {

    // ─── Conversation List ────────────────────────────────────────────────────
    public final Locator chatListContainer;
    public final Locator chatItems;
    public final Locator firstChatItem;
    public final Locator chatItemName;
    public final Locator chatItemLastMessage;
    public final Locator chatItemTimestamp;
    public final Locator chatItemUnreadBadge;
    public final Locator chatItemAvatar;
    public final Locator emptyChatMessage;
    public final Locator chatSearchInput;

    // ─── Chat Window ──────────────────────────────────────────────────────────
    public final Locator chatWindowHeader;
    public final Locator chatPartnerName;
    public final Locator chatPartnerAvatar;
    public final Locator chatPartnerOnlineStatus;
    public final Locator chatBackButton;
    public final Locator chatOptionsMenu;
    public final Locator chatCallButton;
    public final Locator chatVideoCallButton;

    // ─── Message List ─────────────────────────────────────────────────────────
    public final Locator messageContainer;
    public final Locator allMessages;
    public final Locator sentMessages;
    public final Locator receivedMessages;
    public final Locator messageTimestamp;
    public final Locator messageStatusTick;
    public final Locator dateSeperator;
    public final Locator typingIndicator;

    // ─── Message Input ────────────────────────────────────────────────────────
    public final Locator messageInput;
    public final Locator sendButton;
    public final Locator attachmentButton;
    public final Locator emojiButton;
    public final Locator voiceNoteButton;
    public final Locator imageAttachInput;
    public final Locator attachmentPreview;
    public final Locator cancelAttachment;
    public final Locator messageInputPlaceholder;

    // ─── Message Actions (long press/hover) ───────────────────────────────────
    public final Locator replyButton;
    public final Locator deleteMessageButton;
    public final Locator copyMessageButton;
    public final Locator messageContextMenu;

    // ─── WebSocket status ─────────────────────────────────────────────────────
    public final Locator connectionStatusBanner;
    public final Locator reconnectingIndicator;
    public final Locator offlineBanner;

    public ChatPageLocators(Page page) {
        // Conversation list
        chatListContainer    = page.locator("[class*='chat-list'], [class*='conversation-list'], [class*='inbox']");
        chatItems            = page.locator("[class*='chat-item'], [class*='conversation-item']");
        firstChatItem        = chatItems.first();
        chatItemName         = page.locator("[class*='chat-item'] [class*='name'], [class*='conversation-item'] [class*='username']");
        chatItemLastMessage  = page.locator("[class*='chat-item'] [class*='last-message'], [class*='preview']");
        chatItemTimestamp    = page.locator("[class*='chat-item'] [class*='time'], [class*='timestamp']");
        chatItemUnreadBadge  = page.locator("[class*='unread-count'], [class*='badge']:not(:empty)");
        chatItemAvatar       = page.locator("[class*='chat-item'] img, [class*='chat-item'] [class*='avatar']");
        emptyChatMessage     = page.locator("[class*='empty-chat'], p:has-text('No conversations'), p:has-text('Start a conversation')");
        chatSearchInput      = page.locator("[class*='chat-search'] input, input[placeholder*='Search messages' i]");

        // Chat window
        chatWindowHeader     = page.locator("[class*='chat-header'], [class*='conversation-header']");
        chatPartnerName      = page.locator("[class*='chat-header'] [class*='name'], [class*='chat-username']");
        chatPartnerAvatar    = page.locator("[class*='chat-header'] img, [class*='chat-header'] [class*='avatar']");
        chatPartnerOnlineStatus = page.locator("[class*='online-status'], [class*='status-dot']");
        chatBackButton       = page.locator("[class*='chat-back'], button[aria-label='back'], [class*='back-arrow']");
        chatOptionsMenu      = page.locator("[class*='chat-options'], button[aria-label='more'], [class*='dots-menu']");
        chatCallButton       = page.locator("button[aria-label*='call' i], [class*='call-btn']");
        chatVideoCallButton  = page.locator("button[aria-label*='video' i], [class*='video-call']");

        // Messages
        messageContainer  = page.locator("[class*='message-list'], [class*='messages-container']");
        allMessages       = page.locator("[class*='message-bubble'], [class*='chat-message']");
        sentMessages      = page.locator("[class*='message-sent'], [class*='outgoing']");
        receivedMessages  = page.locator("[class*='message-received'], [class*='incoming']");
        messageTimestamp  = page.locator("[class*='message-time'], [class*='msg-timestamp']");
        messageStatusTick = page.locator("[class*='read-receipt'], [class*='tick'], [class*='delivered']");
        dateSeperator     = page.locator("[class*='date-divider'], [class*='date-separator']");
        typingIndicator   = page.locator("[class*='typing'], [class*='is-typing']");

        // Input
        messageInput         = page.locator("textarea[placeholder*='Type' i], input[placeholder*='Type' i], [class*='message-input']");
        sendButton           = page.locator("button[aria-label*='send' i], [class*='send-btn'], button:has(svg[class*='send'])");
        attachmentButton     = page.locator("button[aria-label*='attach' i], [class*='attach-btn'], [class*='paperclip']");
        emojiButton          = page.locator("button[aria-label*='emoji' i], [class*='emoji-btn']");
        voiceNoteButton      = page.locator("button[aria-label*='voice' i], [class*='mic-btn'], [class*='voice-note']");
        imageAttachInput     = page.locator("input[type='file'][accept*='image']");
        attachmentPreview    = page.locator("[class*='attachment-preview'], [class*='file-preview']");
        cancelAttachment     = page.locator("button[aria-label='cancel attachment'], [class*='cancel-attach']");
        messageInputPlaceholder = page.locator("textarea[placeholder], input[placeholder]").first();

        // Message actions
        replyButton          = page.locator("button:has-text('Reply'), [class*='reply-btn']");
        deleteMessageButton  = page.locator("button:has-text('Delete'), [class*='delete-msg']");
        copyMessageButton    = page.locator("button:has-text('Copy'), [class*='copy-msg']");
        messageContextMenu   = page.locator("[class*='message-actions'], [class*='message-menu']");

        // WebSocket status
        connectionStatusBanner = page.locator("[class*='connection-status'], [class*='ws-status']");
        reconnectingIndicator  = page.locator("[class*='reconnecting'], p:has-text('Reconnecting')");
        offlineBanner          = page.locator("[class*='offline-banner'], p:has-text('offline'), [class*='no-internet']");
    }
}
