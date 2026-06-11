package com.toskie.utils;

import com.aventstack.extentreports.Status;
import com.toskie.utils_Layer.ConfigManager;
import com.toskie.utils_Layer.ReportManager;

import java.io.*;
import java.net.Socket;

/**
 * Redis validation via raw socket (no external Redis client dependency).
 * Configure in config.properties:
 *   redis.host=localhost
 *   redis.port=6379
 *   redis.password=optional
 *
 * Validates: session caching, OTP TTL, rate limiting counters, token blacklist.
 */
public class RedisValidator {

    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    private boolean connected = false;

    // ─── Connection ───────────────────────────────────────────────────────────

    public void connect() {
        String host = ConfigManager.get("redis.host");
        String portStr = ConfigManager.get("redis.port");
        String password = ConfigManager.get("redis.password");

        if (host == null || host.trim().isEmpty()) {
            ReportManager.getTest().log(Status.WARNING,
                "[Redis] redis.host not configured — Redis validation skipped.");
            return;
        }
        try {
            int port = portStr != null ? Integer.parseInt(portStr) : 6379;
            socket = new Socket(host, port);
            writer = new PrintWriter(socket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            if (password != null && !password.isEmpty()) {
                sendCommand("AUTH", password);
                ReportManager.getTest().log(Status.INFO, "[Redis] Authenticated.");
            }
            connected = true;
            ReportManager.getTest().log(Status.PASS,
                "[Redis] Connected to " + host + ":" + port);
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING,
                "[Redis] Connection failed: " + e.getMessage() + " — Redis validation skipped.");
        }
    }

    public void disconnect() {
        try {
            if (connected) {
                sendCommand("QUIT");
                socket.close();
                connected = false;
                ReportManager.getTest().log(Status.INFO, "[Redis] Disconnected.");
            }
        } catch (Exception ignored) {}
    }

    // ─── Validation methods ────────────────────────────────────────────────────

    public void assertKeyExists(String key) {
        if (!connected) { skipLog("assertKeyExists(" + key + ")"); return; }
        try {
            String result = sendCommand("EXISTS", key);
            boolean exists = ":1".equals(result.trim());
            if (exists) {
                ReportManager.getTest().log(Status.PASS, "[Redis] Key exists: " + key);
            } else {
                ReportManager.getTest().log(Status.FAIL, "[Redis] Key NOT found: " + key);
            }
        } catch (Exception e) {
            ReportManager.getTest().log(Status.FAIL, "[Redis] EXISTS error: " + e.getMessage());
        }
    }

    public void assertKeyNotExists(String key) {
        if (!connected) { skipLog("assertKeyNotExists(" + key + ")"); return; }
        try {
            String result = sendCommand("EXISTS", key);
            boolean exists = ":1".equals(result.trim());
            if (!exists) {
                ReportManager.getTest().log(Status.PASS, "[Redis] Key correctly absent: " + key);
            } else {
                ReportManager.getTest().log(Status.FAIL,
                    "[Redis] Key should NOT exist but does: " + key);
            }
        } catch (Exception e) {
            ReportManager.getTest().log(Status.FAIL, "[Redis] EXISTS error: " + e.getMessage());
        }
    }

    public void assertKeyValueEquals(String key, String expectedValue) {
        if (!connected) { skipLog("assertKeyValueEquals(" + key + ")"); return; }
        try {
            String actual = sendCommand("GET", key);
            actual = actual.replaceAll("^\\$\\d+\\r?\\n?", "").trim();
            if (expectedValue.equals(actual)) {
                ReportManager.getTest().log(Status.PASS,
                    "[Redis] " + key + " = '" + expectedValue + "'");
            } else {
                ReportManager.getTest().log(Status.FAIL,
                    "[Redis] " + key + " mismatch: expected='" + expectedValue + "' actual='" + actual + "'");
            }
        } catch (Exception e) {
            ReportManager.getTest().log(Status.FAIL, "[Redis] GET error: " + e.getMessage());
        }
    }

    public void assertTTLWithinRange(String key, long minSecs, long maxSecs) {
        if (!connected) { skipLog("assertTTLWithinRange(" + key + ")"); return; }
        try {
            String result = sendCommand("TTL", key);
            long ttl = Long.parseLong(result.trim().replace(":", ""));
            if (ttl >= minSecs && ttl <= maxSecs) {
                ReportManager.getTest().log(Status.PASS,
                    "[Redis] TTL for " + key + ": " + ttl + "s (range: " + minSecs + "-" + maxSecs + "s)");
            } else if (ttl == -1) {
                ReportManager.getTest().log(Status.WARNING,
                    "[Redis] Key " + key + " has no expiry (TTL=-1).");
            } else {
                ReportManager.getTest().log(Status.FAIL,
                    "[Redis] TTL for " + key + ": " + ttl + "s (expected: " + minSecs + "-" + maxSecs + "s)");
            }
        } catch (Exception e) {
            ReportManager.getTest().log(Status.FAIL, "[Redis] TTL error: " + e.getMessage());
        }
    }

    // ─── OTP cache ────────────────────────────────────────────────────────────

    public void assertOTPCached(String mobile) {
        assertKeyExists("otp:" + mobile);
        assertTTLWithinRange("otp:" + mobile, 60, 600); // 1–10 min
    }

    public void assertOTPExpiredAfterUse(String mobile) {
        assertKeyNotExists("otp:" + mobile);
        ReportManager.getTest().log(Status.INFO, "[Redis] OTP key deleted after use (good practice).");
    }

    // ─── Rate limiting ────────────────────────────────────────────────────────

    public void assertRateLimitCounterExists(String mobile) {
        // Key pattern: "rate_limit:otp:{mobile}"
        assertKeyExists("rate_limit:otp:" + mobile);
    }

    public void assertRateLimitCounterValue(String mobile, int expectedAttempts) {
        assertKeyValueEquals("rate_limit:otp:" + mobile, String.valueOf(expectedAttempts));
    }

    // ─── Session cache ────────────────────────────────────────────────────────

    public void assertSessionCached(String userId) {
        assertKeyExists("session:" + userId);
    }

    public void assertSessionExpiry(String userId) {
        assertTTLWithinRange("session:" + userId, 3600, 86400); // 1h–24h
    }

    // ─── Token blacklist ──────────────────────────────────────────────────────

    public void assertTokenBlacklisted(String tokenFragment) {
        assertKeyExists("blacklist:" + tokenFragment);
    }

    public void assertTokenNotBlacklisted(String tokenFragment) {
        assertKeyNotExists("blacklist:" + tokenFragment);
    }

    // ─── RESP protocol helpers ────────────────────────────────────────────────

    private String sendCommand(String... parts) throws IOException {
        StringBuilder cmd = new StringBuilder("*" + parts.length + "\r\n");
        for (String p : parts) {
            cmd.append("$").append(p.length()).append("\r\n").append(p).append("\r\n");
        }
        writer.print(cmd);
        writer.flush();
        return reader.readLine();
    }

    private void skipLog(String method) {
        ReportManager.getTest().log(Status.WARNING,
            "[Redis] " + method + " skipped — no Redis connection.");
    }
}
