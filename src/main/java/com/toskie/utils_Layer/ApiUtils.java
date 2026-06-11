package com.toskie.utils_Layer;

import com.aventstack.extentreports.Status;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Route;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Handles all GraphQL / REST API interactions for QA bypasses,
 * token management, and OTP operations.
 * Tokens are stored per-thread via ThreadLocal for parallel-test safety.
 */
public class ApiUtils {

    private static final ThreadLocal<String> accessTokenHolder  = new ThreadLocal<>();
    private static final ThreadLocal<String> refreshTokenHolder = new ThreadLocal<>();

    // ─── Token accessors ──────────────────────────────────────────────────────
    public static String getAccessToken()  { return accessTokenHolder.get(); }
    public static String getRefreshToken() { return refreshTokenHolder.get(); }

    // ─── QA Login ─────────────────────────────────────────────────────────────
    public static void loginViaQAGraphQL(String mobile) {
        HttpURLConnection conn = null;
        try {
            if (mobile == null || mobile.trim().isEmpty())
                throw new RuntimeException("Mobile number is empty");

            String url     = ConfigManager.getApiUrl();
            String secret  = ConfigManager.getQaSecret();
            String body    = "{ \"query\": \"mutation QA_Bypass_Login { QA_Bypass_Login(" +
                    "phoneNumber: \\\"" + mobile + "\\\", qaSecret: \\\"" + secret + "\\\") " +
                    "{ message status data { access_token refresh_token } } }\" }";

            conn = openPost(url);
            writeBody(conn, body);

            String response = readResponse(conn);
            System.out.println("[ApiUtils] LOGIN HTTP " + conn.getResponseCode() + " RESPONSE => " + response);

            if (!response.startsWith("{") && !response.startsWith("[")) {
                throw new RuntimeException("API returned non-JSON response (HTTP " + conn.getResponseCode() + "): " + response.substring(0, Math.min(200, response.length())));
            }
            JSONObject json = new JSONObject(response);
            if (json.has("errors"))
                throw new RuntimeException(json.getJSONArray("errors").toString());

            JSONObject loginData = json.getJSONObject("data").getJSONObject("QA_Bypass_Login");
            if (!loginData.getBoolean("status"))
                throw new RuntimeException("QA Login status=false");

            JSONObject tokens = loginData.getJSONObject("data");
            String access  = tokens.getString("access_token");
            String refresh = tokens.getString("refresh_token");

            if (access == null || access.isEmpty())   throw new RuntimeException("Access token missing");
            if (refresh == null || refresh.isEmpty()) throw new RuntimeException("Refresh token missing");

            accessTokenHolder.set(access);
            refreshTokenHolder.set(refresh);
            System.out.println("[ApiUtils] Login SUCCESS");

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("QA Login failed", e);
        } finally {
            if (conn != null) conn.disconnect();
        }
    }

    // ─── Email OTP Bypass ────────────────────────────────────────────────────
    public static void bypassEmailOTP(String email) {
        HttpURLConnection conn = null;
        try {
            String token = getAccessToken();
            if (token == null || token.trim().isEmpty())
                throw new RuntimeException("Access token missing. Call loginViaQAGraphQL first.");
            if (isTokenExpired())
                throw new RuntimeException("Access token expired.");

            String url    = ConfigManager.getApiUrl();
            String secret = ConfigManager.getQaSecret();
            String body   = "{ \"query\": \"mutation QA_Bypass_Verify_Email_Otp { " +
                    "QA_Bypass_Verify_Email_Otp(email: \\\"" + email + "\\\", " +
                    "qaSecret: \\\"" + secret + "\\\") { message status data { email } } }\" }";

            conn = openPost(url);
            conn.setRequestProperty("Authorization", "Bearer " + token);
            conn.setRequestProperty("Accept", "application/json");
            writeBody(conn, body);

            String response = readResponse(conn);
            System.out.println("[ApiUtils] Email OTP Response: " + response);

            ReportManager.getTest().log(Status.INFO, "Email OTP Response: " + response);

            if (response.contains("invalid token") || response.contains("Unauthorized"))
                throw new RuntimeException("Invalid or expired token.");

            JSONObject json = new JSONObject(response);
            if (json.has("errors"))
                throw new RuntimeException("GraphQL Error: " + json.getJSONArray("errors"));

            JSONObject bypass = json.getJSONObject("data").getJSONObject("QA_Bypass_Verify_Email_Otp");
            if (!bypass.getBoolean("status"))
                throw new RuntimeException("Email OTP bypass failed: " + bypass.getString("message"));

            try {
                Page page = BrowserManager.getPage();
                page.waitForLoadState();
                page.waitForTimeout(2000);
            } catch (Exception pageEx) {
                System.out.println("[ApiUtils] Page wait after bypass (non-fatal): " + pageEx.getMessage());
            }

            ReportManager.getTest().log(Status.PASS, "Email OTP bypass success: " + email);
            System.out.println("[ApiUtils] Email OTP bypass success");

        } catch (Exception e) {
            ReportManager.getTest().log(Status.FAIL, "Email OTP bypass failed: " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            if (conn != null) conn.disconnect();
        }
    }

    public static void verifyEmailViaQAGraphQL(String email) {
        HttpURLConnection conn = null;
        try {
            String url    = ConfigManager.getApiUrl();
            String secret = ConfigManager.getQaSecret();
            String body   = "{ \"query\": \"mutation QA_Bypass_Verify_Email_Otp { " +
                    "QA_Bypass_Verify_Email_Otp(email: \\\"" + email + "\\\", " +
                    "qaSecret: \\\"" + secret + "\\\") { message status data { email } } }\" }";

            conn = openPost(url);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("x-device-id",   ConfigManager.getDeviceId());
            conn.setRequestProperty("x-fingerprint", ConfigManager.getFingerprint());
            writeBody(conn, body);

            String response = readResponse(conn);
            System.out.println("[ApiUtils] Email QA Bypass Response: " + response);

            JSONObject json   = new JSONObject(response);
            boolean    status = json.getJSONObject("data")
                    .getJSONObject("QA_Bypass_Verify_Email_Otp").getBoolean("status");
            if (!status) throw new RuntimeException("Email QA bypass failed");

            BrowserManager.getPage().reload();
            BrowserManager.getPage().waitForLoadState();
            ReportManager.getTest().log(Status.PASS, "Email verified via QA bypass: " + email);
        } catch (Exception e) {
            throw new RuntimeException("Email QA bypass failed", e);
        } finally {
            if (conn != null) conn.disconnect();
        }
    }

    // ─── Token injection into browser ─────────────────────────────────────────
    public static void injectTokenFull() {
        Page page = BrowserManager.getPage();
        page.waitForLoadState();
        page.waitForTimeout(3000);
        page.evaluate(
                "([access, refresh]) => {" +
                        "window.localStorage.setItem('access_token', access);" +
                        "window.localStorage.setItem('refresh_token', refresh);" +
                        "window.sessionStorage.setItem('access_token', access);" +
                        "window.sessionStorage.setItem('refresh_token', refresh);" +
                        "}",
                new Object[]{getAccessToken(), getRefreshToken()});
        page.waitForTimeout(3000);
        System.out.println("[ApiUtils] Tokens injected into localStorage/sessionStorage");
    }

    public static void injectCookies() {
        try {
            com.microsoft.playwright.options.Cookie accessCookie =
                    new com.microsoft.playwright.options.Cookie("access_token", getAccessToken())
                            .setDomain("dev.app.toskie.com").setPath("/");
            com.microsoft.playwright.options.Cookie refreshCookie =
                    new com.microsoft.playwright.options.Cookie("refresh_token", getRefreshToken())
                            .setDomain("dev.app.toskie.com").setPath("/");
            BrowserManager.getContext().addCookies(java.util.List.of(accessCookie, refreshCookie));
            System.out.println("[ApiUtils] Cookies injected");
        } catch (Exception e) {
            throw new RuntimeException("Cookie inject failed", e);
        }
    }

    // ─── Token validation ────────────────────────────────────────────────────
    public static boolean isTokenExpired() {
        try {
            String token = getAccessToken();
            if (token == null || token.isEmpty()) return true;
            String[] chunks  = token.split("\\.");
            String   payload = new String(java.util.Base64.getDecoder().decode(chunks[1]));
            long     expiry  = new JSONObject(payload).getLong("exp") * 1000;
            return System.currentTimeMillis() > expiry;
        } catch (Exception e) {
            return true;
        }
    }

    // ─── OTP helpers ─────────────────────────────────────────────────────────
    public static String captureOTPFromGraphQL(Runnable action) {
        final String[] otpHolder = {null};
        try {
            BrowserManager.getPage().onResponse(resp -> {
                try {
                    if (resp.url().contains("graphql")) {
                        String body = resp.text();
                        if (body.contains("otp")) {
                            otpHolder[0] = body.split("\"otp\":\"")[1].split("\"")[0];
                            ReportManager.getTest().log(Status.PASS, "OTP captured: " + otpHolder[0]);
                        }
                    }
                } catch (Exception ignored) {}
            });
            action.run();
            BrowserManager.getPage().waitForTimeout(4000);
            return otpHolder[0] != null ? otpHolder[0] : "5100";
        } catch (Exception e) {
            return "5100";
        }
    }

    public static String getDynamicOTP(String mobile) {
        try {
            Page page = BrowserManager.getPage();
            final String[] otp = {null};
            page.onResponse(resp -> {
                try {
                    if (resp.url().contains("graphql")) {
                        String body = resp.text();
                        if (body.contains("otp")) {
                            otp[0] = body.split("\"otp\":\"")[1].split("\"")[0];
                        }
                    }
                } catch (Exception ignored) {}
            });
            page.waitForTimeout(4000);
            if (otp[0] != null) return otp[0];
        } catch (Exception e) {
            ReportManager.getTest().log(Status.INFO, "OTP not captured, using fallback");
        }
        ReportManager.getTest().log(Status.WARNING, "Using fallback OTP: 5100");
        return "5100";
    }

    // ─── Route mocking (convenience delegated from UtilLayer) ────────────────

    public static void enableEmailOTPBypass(String email) {
        Page p = BrowserManager.getPage();

        // REST: POST /api/auth/user-update/send-email-otp {"email":"..."}
        // Real backend redirects for already-registered emails → mock success keeps page on form.
        p.route("**/send-email-otp", route -> {
            System.out.println("[RouteInterceptor] send-email-otp → fake success");
            route.fulfill(new Route.FulfillOptions()
                    .setStatus(200).setContentType("application/json")
                    .setBody("{ \"message\": \"OTP sent successfully\", \"status\": true }"));
        });

        // REST: POST /api/auth/user-update/verify-email-otp (likely URL for OTP verification).
        // The UI shows a 4-digit OTP modal; intercept the verify call and return success
        // so the dialog closes and the form can proceed.
        p.route("**/verify-email-otp", route -> {
            System.out.println("[RouteInterceptor] verify-email-otp → fake success");
            route.fulfill(new Route.FulfillOptions()
                    .setStatus(200).setContentType("application/json")
                    .setBody("{ \"message\": \"Email OTP verified successfully\", \"status\": true }"));
        });

        p.route("**/graphql", route -> {
            String requestBody = route.request().postData();
            if (requestBody == null) { route.resume(); return; }

            System.out.println("[RouteInterceptor] GraphQL body: "
                    + requestBody.substring(0, Math.min(300, requestBody.length())));

            if (requestBody.contains("VerifyEmailUsingOTP")) {
                System.out.println("[RouteInterceptor] Mocking VerifyEmailUsingOTP → success");
                route.fulfill(new Route.FulfillOptions()
                        .setStatus(200).setContentType("application/json")
                        .setBody("{ \"data\": { \"VerifyEmailUsingOTP\": { " +
                                "\"message\": \"OTP verified successfully\", \"status\": true, " +
                                "\"data\": { \"email\": \"" + email + "\" } } } }"));

            } else if (requestBody.contains("QA_Bypass")) {
                System.out.println("[RouteInterceptor] Pass-through QA_Bypass mutation");
                route.resume();

            } else if (isEmailOtpSendMutation(requestBody)) {
                String opName = extractOperationName(requestBody, "SendEmailOTP");
                System.out.println("[RouteInterceptor] Mocking Send-OTP mutation '" + opName + "' → success");
                route.fulfill(new Route.FulfillOptions()
                        .setStatus(200).setContentType("application/json")
                        .setBody("{ \"data\": { \"" + opName + "\": { " +
                                "\"message\": \"OTP sent successfully\", \"status\": true } } }"));

            } else {
                route.resume();
            }
        });
    }

    private static boolean isEmailOtpSendMutation(String body) {
        String lower = body.toLowerCase();
        return lower.contains("mutation")
                && lower.contains("email")
                && lower.contains("otp")
                && !lower.contains("verify")
                && !lower.contains("qa_bypass");
    }

    private static String extractOperationName(String body, String fallback) {
        try {
            java.util.regex.Matcher m = java.util.regex.Pattern
                    .compile("mutation\\s+(\\w+)\\s*[{(]")
                    .matcher(body);
            if (m.find()) return m.group(1);
            m = java.util.regex.Pattern
                    .compile("mutation\\s*\\{\\s*(\\w+)\\s*\\(")
                    .matcher(body);
            if (m.find()) return m.group(1);
        } catch (Exception ignored) {}
        return fallback;
    }

    // ─── Internal HTTP helpers ────────────────────────────────────────────────
    private static HttpURLConnection openPost(String url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setConnectTimeout(30_000);
        conn.setReadTimeout(30_000);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept",       "application/json");
        conn.setRequestProperty("x-device-id",   ConfigManager.getDeviceId());
        conn.setRequestProperty("x-fingerprint", ConfigManager.getFingerprint());
        conn.setRequestProperty("User-Agent",    "Mozilla/5.0");
        return conn;
    }

    private static void writeBody(HttpURLConnection conn, String body) throws IOException {
        try (OutputStream os = conn.getOutputStream()) {
            os.write(body.getBytes("utf-8"));
            os.flush();
        }
    }

    private static String readResponse(HttpURLConnection conn) throws IOException {
        int code   = conn.getResponseCode();
        InputStream stream = (code >= 200 && code < 300) ? conn.getInputStream() : conn.getErrorStream();
        if (stream == null) {
            throw new IOException("Response stream is null for HTTP " + code);
        }
        try (BufferedReader br = new BufferedReader(new InputStreamReader(stream, "utf-8"))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line.trim());
            String result = sb.toString();
            // Strip UTF-8 BOM (﻿) that some servers include at the start
            if (result.startsWith("﻿")) result = result.substring(1);
            return result;
        }
    }
}
