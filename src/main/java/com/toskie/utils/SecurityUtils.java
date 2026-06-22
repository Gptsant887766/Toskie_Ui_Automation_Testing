package com.toskie.utils;

import com.aventstack.extentreports.Status;
import com.microsoft.playwright.Locator;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.ReportManager;

import java.util.Arrays;
import java.util.List;

/**
 * Security testing utilities -- XSS, SQLi, CSRF, auth bypass, token security.
 */
public class SecurityUtils {

    // ─── XSS Payloads ─────────────────────────────────────────────────────────

    public static final List<String> XSS_PAYLOADS = Arrays.asList(
        "<script>alert('XSS')</script>",
        "<img src=x onerror=alert(document.cookie)>",
        "javascript:alert(1)",
        "<svg onload=alert(1)>",
        "'\"><script>alert(1)</script>",
        "<iframe src=javascript:alert(1)>",
        "';alert(String.fromCharCode(88,83,83))//",
        "<body onload=alert('XSS')>",
        "%3Cscript%3Ealert('XSS')%3C/script%3E",
        "<scr<script>ipt>alert('XSS')</scr</script>ipt>"
    );

    // ─── SQL Injection Payloads ────────────────────────────────────────────────

    public static final List<String> SQL_INJECTION_PAYLOADS = Arrays.asList(
        "' OR '1'='1",
        "'; DROP TABLE users; --",
        "' UNION SELECT username,password FROM users --",
        "1' AND SLEEP(5)--",
        "admin'--",
        "' OR 1=1--",
        "' AND 1=2 UNION SELECT 1,2,3 --",
        "';EXEC xp_cmdshell('dir');--"
    );

    // ─── OWASP API Security Payloads ──────────────────────────────────────────

    public static final List<String> BOLA_TEST_IDS = Arrays.asList(
        "1", "2", "999999", "../admin", "undefined", "null"
    );

    // ─── Input injection testing ──────────────────────────────────────────────

    public boolean testXSSInField(Locator inputField, String fieldName) {
        boolean vulnerable = false;
        for (String payload : XSS_PAYLOADS) {
            try {
                inputField.fill(payload);
                BrowserManager.getPage().waitForTimeout(500);

                // Check if alert dialog appeared (XSS execution)
                String pageContent = BrowserManager.getPage().content();
                if (pageContent.contains("<script>alert") || isAlertPresent()) {
                    ReportManager.getTest().log(Status.FAIL,
                        "XSS VULNERABILITY: Field '" + fieldName + "' executed payload: " + payload);
                    vulnerable = true;
                } else {
                    ReportManager.getTest().log(Status.PASS,
                        "XSS payload sanitized in '" + fieldName + "': " + truncate(payload, 50));
                }
            } catch (Exception e) {
                ReportManager.getTest().log(Status.INFO,
                    "XSS test blocked for payload: " + truncate(payload, 50));
            } finally {
                try { inputField.fill(""); } catch (Exception ignored) {}
            }
        }
        return !vulnerable;
    }

    public boolean testSQLInjectionInField(Locator inputField, String fieldName) {
        boolean vulnerable = false;
        for (String payload : SQL_INJECTION_PAYLOADS) {
            try {
                inputField.fill(payload);
                BrowserManager.getPage().keyboard().press("Enter");
                BrowserManager.getPage().waitForTimeout(1000);

                // Look for SQL error messages
                String content = BrowserManager.getPage().content().toLowerCase();
                if (content.contains("sql") || content.contains("syntax error")
                        || content.contains("mysql") || content.contains("ora-")) {
                    ReportManager.getTest().log(Status.FAIL,
                        "SQL INJECTION VULNERABILITY: Field '" + fieldName
                            + "' leaks DB error for: " + payload);
                    vulnerable = true;
                } else {
                    ReportManager.getTest().log(Status.PASS,
                        "SQL injection handled safely in '" + fieldName + "'");
                }
            } catch (Exception e) {
                ReportManager.getTest().log(Status.INFO, "SQLi test for: " + fieldName);
            } finally {
                try { inputField.fill(""); } catch (Exception ignored) {}
            }
        }
        return !vulnerable;
    }

    // ─── Authentication security ──────────────────────────────────────────────

    public void assertTokenNotInLocalStorage(String tokenName) {
        try {
            Object value = BrowserManager.getPage()
                .evaluate("() => localStorage.getItem('" + tokenName + "')");
            if (value != null && !value.toString().isEmpty()) {
                ReportManager.getTest().log(Status.WARNING,
                    "Token '" + tokenName + "' stored in localStorage -- consider httpOnly cookies instead.");
            } else {
                ReportManager.getTest().log(Status.PASS,
                    "Token '" + tokenName + "' not in localStorage.");
            }
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "Could not check localStorage: " + e.getMessage());
        }
    }

    public void assertTokenExpiry() {
        try {
            String token = BrowserManager.getPage()
                .evaluate("() => localStorage.getItem('access_token')").toString();
            if (token == null || token.isEmpty()) {
                ReportManager.getTest().log(Status.WARNING, "No access token found for expiry check.");
                return;
            }
            String[] parts = token.split("\\.");
            if (parts.length == 3) {
                String payload = new String(java.util.Base64.getDecoder().decode(parts[1]));
                org.json.JSONObject json = new org.json.JSONObject(payload);
                long expiry = json.getLong("exp");
                long now    = System.currentTimeMillis() / 1000;
                if (expiry > now) {
                    ReportManager.getTest().log(Status.PASS,
                        "Token is valid (expires in " + (expiry - now) + "s).");
                } else {
                    ReportManager.getTest().log(Status.FAIL, "Token is EXPIRED.");
                }
            }
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "Token expiry check error: " + e.getMessage());
        }
    }

    public void assertNoSensitiveDataInResponse(String urlFragment) {
        BrowserManager.getPage().onResponse(resp -> {
            if (resp.url().contains(urlFragment)) {
                try {
                    String body = resp.text().toLowerCase();
                    if (body.contains("password") || body.contains("qa_secret")
                            || body.contains("private_key")) {
                        ReportManager.getTest().log(Status.FAIL,
                            "SENSITIVE DATA in API response: " + resp.url());
                    } else {
                        ReportManager.getTest().log(Status.PASS,
                            "No sensitive data in response: " + urlFragment);
                    }
                } catch (Exception ignored) {}
            }
        });
    }

    public void assertHTTPSOnly() {
        BrowserManager.getPage().onRequest(req -> {
            if (req.url().startsWith("http://") && !req.url().startsWith("http://localhost")) {
                ReportManager.getTest().log(Status.FAIL,
                    "SECURITY: Plain HTTP request: " + req.url());
            }
        });
        ReportManager.getTest().log(Status.PASS, "HTTPS check listener active.");
    }

    public void assertCSPHeaderPresent() {
        BrowserManager.getPage().onResponse(resp -> {
            if (resp.url().contains(BrowserManager.getPage().url().split("/")[2])) {
                String csp = resp.headers().get("content-security-policy");
                if (csp != null && !csp.isEmpty()) {
                    ReportManager.getTest().log(Status.PASS, "CSP header: " + csp);
                } else {
                    ReportManager.getTest().log(Status.WARNING,
                        "Content-Security-Policy header NOT found on: " + resp.url());
                }
            }
        });
    }

    public void assertNoCORSMisconfiguration() {
        BrowserManager.getPage().onResponse(resp -> {
            String origin = resp.headers().get("access-control-allow-origin");
            if ("*".equals(origin)) {
                ReportManager.getTest().log(Status.WARNING,
                    "Wildcard CORS on: " + resp.url() + " -- review if intentional.");
            }
        });
    }

    // ─── Rate limiting ────────────────────────────────────────────────────────

    public boolean assertRateLimitingOnOTP(Locator sendOTPButton, int attempts) {
        boolean rateLimited = false;
        for (int i = 0; i < attempts; i++) {
            try {
                sendOTPButton.click();
                BrowserManager.getPage().waitForTimeout(1000);
                String content = BrowserManager.getPage().content().toLowerCase();
                if (content.contains("too many") || content.contains("limit")
                        || content.contains("rate") || content.contains("blocked")) {
                    ReportManager.getTest().log(Status.PASS,
                        "Rate limiting triggered after " + (i + 1) + " attempts.");
                    rateLimited = true;
                    break;
                }
            } catch (Exception e) {
                break;
            }
        }
        if (!rateLimited) {
            ReportManager.getTest().log(Status.WARNING,
                "Rate limiting NOT triggered after " + attempts + " OTP requests.");
        }
        return rateLimited;
    }

    // ─── Helper ───────────────────────────────────────────────────────────────

    private boolean isAlertPresent() {
        try {
            final boolean[] alertFired = {false};
            BrowserManager.getPage().onceDialog(dialog -> {
                alertFired[0] = true;
                dialog.dismiss();
            });
            BrowserManager.getPage().waitForTimeout(300);
            return alertFired[0];
        } catch (Exception e) {
            return false;
        }
    }

    private String truncate(String s, int max) {
        return s.length() > max ? s.substring(0, max) + "..." : s;
    }
}
