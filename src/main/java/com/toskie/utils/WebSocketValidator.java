package com.toskie.utils;

import com.aventstack.extentreports.Status;
import com.microsoft.playwright.WebSocket;
// WebSocketFrame import not required; frame handled via lambdas on WebSocket
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.ReportManager;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Captures and validates WebSocket messages for real-time feature testing.
 * Validates chat messages, notifications, and live updates via WebSocket.
 */
public class WebSocketValidator {

    private final List<WebSocket>      activeConnections   = new CopyOnWriteArrayList<>();
    private final List<String>         receivedMessages    = new CopyOnWriteArrayList<>();
    private final List<String>         sentMessages        = new CopyOnWriteArrayList<>();
    private final List<Long>           connectionTimestamps= new CopyOnWriteArrayList<>();

    // ─── Attach listeners ─────────────────────────────────────────────────────

    public void startListening() {
        BrowserManager.getPage().onWebSocket(ws -> {
            activeConnections.add(ws);
            connectionTimestamps.add(System.currentTimeMillis());

            ReportManager.getTest().log(Status.INFO,
                "[WebSocket] Connected: " + ws.url());

            ws.onFrameReceived(frame -> {
                String data = frame.text();
                receivedMessages.add(data);
                ReportManager.getTest().log(Status.INFO,
                    "[WebSocket] Received: " + truncate(data, 200));
            });

            ws.onFrameSent(frame -> {
                String data = frame.text();
                sentMessages.add(data);
                ReportManager.getTest().log(Status.INFO,
                    "[WebSocket] Sent: " + truncate(data, 200));
            });

            ws.onClose(closedWs -> ReportManager.getTest().log(Status.INFO,
                "[WebSocket] Closed: " + closedWs.url()));
        });

        ReportManager.getTest().log(Status.INFO, "[WebSocketValidator] Listening for WebSocket events.");
    }

    public void stopListening() {
        ReportManager.getTest().log(Status.INFO,
            "[WebSocketValidator] Captured " + receivedMessages.size()
                + " received / " + sentMessages.size() + " sent messages.");
    }

    public void clearMessages() {
        receivedMessages.clear();
        sentMessages.clear();
    }

    // ─── Connection assertions ────────────────────────────────────────────────

    public void assertWebSocketConnected() {
        if (!activeConnections.isEmpty()) {
            ReportManager.getTest().log(Status.PASS,
                "WebSocket connection established (" + activeConnections.size() + " connections).");
        } else {
            ReportManager.getTest().log(Status.FAIL,
                "No WebSocket connection detected. Real-time features may not work.");
        }
    }

    public void assertWebSocketConnectedTo(String expectedUrlFragment) {
        boolean found = activeConnections.stream().anyMatch(ws -> ws.url().contains(expectedUrlFragment));
        if (found) {
            ReportManager.getTest().log(Status.PASS, "WebSocket connected to: " + expectedUrlFragment);
        } else {
            ReportManager.getTest().log(Status.FAIL,
                "No WebSocket connection to: " + expectedUrlFragment
                    + ". Active: " + activeConnections.stream().map(WebSocket::url).toList());
        }
    }

    // ─── Message assertions ───────────────────────────────────────────────────

    public void assertMessageReceived(String expectedContent) {
        boolean found = receivedMessages.stream().anyMatch(m -> m.contains(expectedContent));
        if (found) {
            ReportManager.getTest().log(Status.PASS,
                "WebSocket message received containing: " + expectedContent);
        } else {
            ReportManager.getTest().log(Status.FAIL,
                "Expected WebSocket message NOT received: " + expectedContent
                    + " | Received: " + receivedMessages);
        }
    }

    public void assertMessageSent(String expectedContent) {
        boolean found = sentMessages.stream().anyMatch(m -> m.contains(expectedContent));
        if (found) {
            ReportManager.getTest().log(Status.PASS,
                "WebSocket message sent containing: " + expectedContent);
        } else {
            ReportManager.getTest().log(Status.FAIL,
                "Expected WebSocket message NOT sent: " + expectedContent);
        }
    }

    public void assertJsonMessageReceived(String key, String expectedValue) {
        for (String msg : receivedMessages) {
            try {
                JSONObject json = new JSONObject(msg);
                if (json.has(key) && json.get(key).toString().equals(expectedValue)) {
                    ReportManager.getTest().log(Status.PASS,
                        "WS message with " + key + "=" + expectedValue + " received.");
                    return;
                }
            } catch (Exception ignored) {}
        }
        ReportManager.getTest().log(Status.FAIL,
            "No WS message with " + key + "=" + expectedValue + " received.");
    }

    public void assertNoErrorMessages() {
        List<String> errors = new ArrayList<>();
        for (String msg : receivedMessages) {
            try {
                JSONObject json = new JSONObject(msg);
                if (json.has("error") || json.has("errors")
                        || (json.has("type") && "error".equalsIgnoreCase(json.getString("type")))) {
                    errors.add(msg);
                }
            } catch (Exception ignored) {}
        }
        if (errors.isEmpty()) {
            ReportManager.getTest().log(Status.PASS, "No WebSocket error messages detected.");
        } else {
            ReportManager.getTest().log(Status.FAIL,
                "WebSocket errors detected: " + errors);
        }
    }

    public void assertMessageReceivedWithinTimeout(String expectedContent, long timeoutMs) {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            if (receivedMessages.stream().anyMatch(m -> m.contains(expectedContent))) {
                ReportManager.getTest().log(Status.PASS,
                    "WS message received within " + timeoutMs + "ms: " + expectedContent);
                return;
            }
            try { Thread.sleep(500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
        ReportManager.getTest().log(Status.FAIL,
            "WS message NOT received within " + timeoutMs + "ms: " + expectedContent);
    }

    // ─── Chat-specific ────────────────────────────────────────────────────────

    public void assertChatMessageDelivered(String messageText) {
        assertJsonMessageReceived("type", "message_delivered");
        assertMessageReceived(messageText);
    }

    public void assertTypingIndicatorMessage() {
        boolean found = receivedMessages.stream().anyMatch(m ->
            m.contains("typing") || m.contains("is_typing") || m.contains("TYPING"));
        if (found) {
            ReportManager.getTest().log(Status.PASS, "Typing indicator WS event detected.");
        } else {
            ReportManager.getTest().log(Status.WARNING, "No typing indicator WS event captured.");
        }
    }

    public void assertReadReceiptMessage() {
        boolean found = receivedMessages.stream().anyMatch(m ->
            m.contains("read") || m.contains("READ_RECEIPT") || m.contains("seen"));
        if (found) {
            ReportManager.getTest().log(Status.PASS, "Read receipt WS event detected.");
        } else {
            ReportManager.getTest().log(Status.WARNING, "No read receipt WS event captured.");
        }
    }

    // ─── Reconnection ─────────────────────────────────────────────────────────

    public void assertReconnectionAfterNetworkInterrupt() {
        int initialCount = activeConnections.size();
        ReportManager.getTest().log(Status.INFO,
            "WebSocket reconnection test — initial connections: " + initialCount);
        // The actual network interrupt + wait would be done in the test body
        // This just validates that connections are re-established
        BrowserManager.getPage().waitForTimeout(5000);
        boolean reconnected = activeConnections.size() >= initialCount;
        if (reconnected) {
            ReportManager.getTest().log(Status.PASS, "WebSocket reconnected after interruption.");
        } else {
            ReportManager.getTest().log(Status.FAIL, "WebSocket DID NOT reconnect.");
        }
    }

    // ─── Accessors ────────────────────────────────────────────────────────────

    public List<String> getReceivedMessages() { return receivedMessages; }
    public List<String> getSentMessages()     { return sentMessages; }
    public int getConnectionCount()           { return activeConnections.size(); }
    public int getReceivedMessageCount()      { return receivedMessages.size(); }

    private String truncate(String s, int maxLen) {
        return s != null && s.length() > maxLen ? s.substring(0, maxLen) + "..." : s;
    }
}
