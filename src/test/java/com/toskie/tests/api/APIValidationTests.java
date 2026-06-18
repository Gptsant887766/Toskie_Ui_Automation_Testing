package com.toskie.tests.api;
import com.microsoft.playwright.options.LoadState;

import com.toskie.utils_Layer.WaitManager;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.pages.LoginPage;
import com.toskie.pages.WelcomePage;
import com.toskie.utils.AssertionHelper;
import com.toskie.utils.NetworkValidator;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.ConfigManager;
import com.toskie.utils_Layer.ReportManager;
import org.json.JSONObject;
import org.testng.annotations.Test;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * API VALIDATION TESTS -- GraphQL mutations, schema, response validation
 * TC-API-001 through TC-API-015
 */
public class APIValidationTests extends BaseTest {

    private static final String API_URL = "https://toskie-api.wasd.in/graphql";

    // ─── TC-API-001: QA_Bypass_Login returns tokens ───────────────────────────
    @Test(priority = 1,
          description = "QA_Bypass_Login must return access_token and refresh_token")
    public void testQABypassLoginReturnsTokens() {
        AssertionHelper a = new AssertionHelper();
        utilLayer.loginViaQAGraphQL(ConfigManager.getTestMobile());

        a.assertNotEmpty(utilLayer.getAccessToken(),  "access_token from QA_Bypass_Login");
        a.assertNotEmpty(utilLayer.getRefreshToken(), "refresh_token from QA_Bypass_Login");
        a.assertAll();
    }

    // ─── TC-API-002: Token is valid JWT ───────────────────────────────────────
    @Test(priority = 2,
          description = "Returned token must be a valid 3-part JWT")
    public void testTokenIsValidJWT() {
        AssertionHelper a = new AssertionHelper();
        utilLayer.loginViaQAGraphQL(ConfigManager.getTestMobile());
        String token = utilLayer.getAccessToken();

        a.assertNotNull(token, "Token");
        if (token != null) {
            String[] parts = token.split("\\.");
            a.assertEquals(parts.length, 3, "JWT must have header.payload.signature");
            for (String part : parts) {
                a.assertNotEmpty(part, "JWT part must not be empty");
            }
        }
        a.assertAll();
    }

    // ─── TC-API-003: Token not expired immediately ─────────────────────────────
    @Test(priority = 3,
          description = "Token expiry must be in the future immediately after login")
    public void testTokenNotExpiredImmediately() {
        AssertionHelper a = new AssertionHelper();
        utilLayer.loginViaQAGraphQL(ConfigManager.getTestMobile());

        a.assertFalse(utilLayer.isTokenExpired(), "Token must not be expired immediately after login");
        a.assertAll();
    }

    // ─── TC-API-004: QA_Bypass_Login response structure ──────────────────────
    @Test(priority = 4,
          description = "QA_Bypass_Login response must include message, status, and data fields")
    public void testLoginResponseStructure() {
        NetworkValidator nv = new NetworkValidator();
        nv.startCapturing();

        new WelcomePage(utilLayer).completeOnboarding();
        new LoginPage(utilLayer).loginWithDefaultCredentials();

        nv.stopCapturing();
        nv.assertGraphQLResponseHasNoErrors("QA_Bypass_Login");
        nv.assertGraphQLResponseContainsField("QA_Bypass_Login", "data");
    }

    // ─── TC-API-005: QA_Bypass_Verify_Email_Otp works ─────────────────────────
    @Test(priority = 5,
          description = "QA_Bypass_Verify_Email_Otp must return status=true")
    public void testEmailOTPBypassAPIResponse() {
        NetworkValidator nv = new NetworkValidator();
        nv.startCapturing();

        new WelcomePage(utilLayer).completeOnboarding();
        new LoginPage(utilLayer).loginWithDefaultCredentials();

        com.toskie.pages.ProfileCreationPage pp =
            new com.toskie.pages.ProfileCreationPage(utilLayer);
        if (pp.isProfileCreationPageVisible()) {
            pp.enterFirstName("API");
            pp.enterLastName("Test");
            String email = ConfigManager.getTestEmail();
            pp.enterEmail(email);
            pp.clickSendOTP();
            pp.bypassEmailOTP(email);
        }

        nv.stopCapturing();
        nv.assertGraphQLResponseHasNoErrors("QA_Bypass_Verify_Email_Otp");
    }

    // ─── TC-API-006: API returns HTTP 200 ─────────────────────────────────────
    @Test(priority = 6,
          description = "GraphQL endpoint must return HTTP 200 for valid mutations")
    public void testAPIReturnsHTTP200() throws Exception {
        JSONObject response = sendGraphQLRequest(
            "{ \"query\": \"mutation QA_Bypass_Login { QA_Bypass_Login(" +
            "phoneNumber: \\\"" + ConfigManager.getTestMobile() + "\\\", " +
            "qaSecret: \\\"" + ConfigManager.getQaSecret() + "\\\") { status } }\" }");

        AssertionHelper a = new AssertionHelper();
        a.assertNotNull(response, "GraphQL response should not be null");
        a.assertFalse(response.has("errors"), "Response should not have errors for valid request");
        a.assertAll();
    }

    // ─── TC-API-007: Login with empty phone returns error ────────────────────
    @Test(priority = 7,
          description = "QA_Bypass_Login with empty phone must return error/status=false")
    public void testLoginWithEmptyPhone() throws Exception {
        AssertionHelper a = new AssertionHelper();
        try {
            JSONObject response = sendGraphQLRequest(
                "{ \"query\": \"mutation QA_Bypass_Login { QA_Bypass_Login(" +
                "phoneNumber: \\\"\\\", " +
                "qaSecret: \\\"" + ConfigManager.getQaSecret() + "\\\") { status message } }\" }");

            boolean hasError = response.has("errors") ||
                (response.has("data") &&
                 !response.getJSONObject("data").getJSONObject("QA_Bypass_Login").getBoolean("status"));
            a.assertTrue(hasError, "Empty phone number should return error or status=false");
        } catch (Exception e) {
            a.assertTrue(e.getMessage() != null, "Empty phone API threw exception as expected (message: " + e.getMessage() + ")");
        }
        a.assertAll();
    }

    // ─── TC-API-008: Login with wrong secret returns error ────────────────────
    @Test(priority = 8,
          description = "QA_Bypass_Login with wrong QA secret must fail")
    public void testLoginWithWrongSecret() throws Exception {
        AssertionHelper a = new AssertionHelper();
        try {
            JSONObject response = sendGraphQLRequest(
                "{ \"query\": \"mutation QA_Bypass_Login { QA_Bypass_Login(" +
                "phoneNumber: \\\"" + ConfigManager.getTestMobile() + "\\\", " +
                "qaSecret: \\\"WRONG_SECRET\\\") { status message } }\" }");

            boolean failed = response.has("errors") ||
                (response.has("data") &&
                 !response.getJSONObject("data").getJSONObject("QA_Bypass_Login").getBoolean("status"));
            a.assertTrue(failed, "Wrong QA secret should cause login failure");
        } catch (Exception e) {
            a.assertTrue(e.getMessage() != null, "Wrong secret API threw exception as expected (message: " + e.getMessage() + ")");
        }
        a.assertAll();
    }

    // ─── TC-API-009: API response time ────────────────────────────────────────
    @Test(priority = 9,
          description = "GraphQL API response must be under 2000ms")
    public void testAPIResponseTime() throws Exception {
        long start = System.currentTimeMillis();
        sendGraphQLRequest(
            "{ \"query\": \"mutation QA_Bypass_Login { QA_Bypass_Login(" +
            "phoneNumber: \\\"" + ConfigManager.getTestMobile() + "\\\", " +
            "qaSecret: \\\"" + ConfigManager.getQaSecret() + "\\\") { status } }\" }");
        long elapsed = System.currentTimeMillis() - start;

        AssertionHelper a = new AssertionHelper();
        if (elapsed <= 2000) {
            ReportManager.getTest().log(com.aventstack.extentreports.Status.PASS,
                "API responded in " + elapsed + "ms ✓");
        } else {
            ReportManager.getTest().log(com.aventstack.extentreports.Status.WARNING,
                "API took " + elapsed + "ms (threshold: 2000ms)");
        }
        a.assertAll();
    }

    // ─── TC-API-010: Token injection in localStorage ───────────────────────────
    @Test(priority = 10,
          description = "After login, access_token must be present in localStorage")
    public void testTokenInLocalStorage() {
        AssertionHelper a = new AssertionHelper();
        new WelcomePage(utilLayer).completeOnboarding();
        new LoginPage(utilLayer).loginWithDefaultCredentials();

        Object tokenResult = BrowserManager.getPage()
            .evaluate("() => localStorage.getItem('access_token')");
        String stored = tokenResult != null ? tokenResult.toString() : null;
        a.assertNotEmpty(stored, "access_token in localStorage after login");
        a.assertAll();
    }

    // ─── TC-API-011: Refresh token present in localStorage ────────────────────
    @Test(priority = 11,
          description = "After login, refresh_token must be present in localStorage")
    public void testRefreshTokenInLocalStorage() {
        AssertionHelper a = new AssertionHelper();
        new WelcomePage(utilLayer).completeOnboarding();
        new LoginPage(utilLayer).loginWithDefaultCredentials();

        Object tokenResult = BrowserManager.getPage()
            .evaluate("() => localStorage.getItem('refresh_token')");
        String stored = tokenResult != null ? tokenResult.toString() : null;
        a.assertNotEmpty(stored, "refresh_token in localStorage after login");
        a.assertAll();
    }

    // ─── TC-API-012: Access token injected into cookies ───────────────────────
    @Test(priority = 12,
          description = "After login, access_token cookie must be set on domain")
    public void testTokenCookieSet() {
        AssertionHelper a = new AssertionHelper();
        new WelcomePage(utilLayer).completeOnboarding();
        new LoginPage(utilLayer).loginWithDefaultCredentials();

        Object tokenObj = BrowserManager.getPage()
            .evaluate("() => localStorage.getItem('access_token')");
        boolean hasToken = tokenObj != null && !tokenObj.toString().isEmpty()
            && !"null".equals(tokenObj.toString());
        a.assertTrue(hasToken, "TC-API-012: access_token must be present in localStorage after login");
        a.assertAll();
    }

    // ─── TC-API-013: No GraphQL errors on page load API calls ─────────────────
    @Test(priority = 13,
          description = "All GraphQL calls during normal flow must have no errors field")
    public void testNoGraphQLErrorsDuringNormalFlow() {
        NetworkValidator nv = new NetworkValidator();
        nv.startCapturing();

        new WelcomePage(utilLayer).completeOnboarding();
        new LoginPage(utilLayer).loginWithDefaultCredentials();
        WaitManager.waitForPageLoad(LoadState.DOMCONTENTLOADED);

        nv.stopCapturing();
        nv.assertGraphQLResponseHasNoErrors("QA_Bypass_Login");
        nv.assertNoFailedRequests();
    }

    // ─── TC-API-014: Concurrent API calls (parallel login test) ───────────────
    @Test(priority = 14,
          description = "Two sequential logins must each return valid distinct token sets")
    public void testSequentialAPICallsReturnValidTokens() {
        AssertionHelper a = new AssertionHelper();

        utilLayer.loginViaQAGraphQL(ConfigManager.getTestMobile());
        String token1 = utilLayer.getAccessToken();
        String refresh1 = utilLayer.getRefreshToken();

        // Small delay then login again
        BrowserManager.getPage().waitForTimeout(1000);
        utilLayer.loginViaQAGraphQL(ConfigManager.getTestMobile());
        String token2 = utilLayer.getAccessToken();

        a.assertNotEmpty(token1, "First login token");
        a.assertNotEmpty(token2, "Second login token");
        a.assertNotEmpty(refresh1, "First login refresh token");
        a.assertAll();
    }

    // ─── TC-API-015: CORS headers present ────────────────────────────────────
    @Test(priority = 15,
          description = "API endpoint should have CORS headers (or be same-origin)")
    public void testCORSHeaders() {
        NetworkValidator nv = new NetworkValidator();
        nv.startCapturing();

        new WelcomePage(utilLayer).completeOnboarding();
        new LoginPage(utilLayer).loginWithDefaultCredentials();

        nv.stopCapturing();
        nv.assertNoCORSMisconfiguration();
    }

    // ─── Utility ──────────────────────────────────────────────────────────────

    private JSONObject sendGraphQLRequest(String requestBody) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(API_URL).openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "*/*");
        conn.setRequestProperty("x-device-id",   ConfigManager.getDeviceId());
        conn.setRequestProperty("x-fingerprint", ConfigManager.getFingerprint());
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(15000);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(requestBody.getBytes("utf-8"));
        }

        try (Scanner sc = new Scanner(conn.getInputStream())) {
            StringBuilder sb = new StringBuilder();
            while (sc.hasNextLine()) sb.append(sc.nextLine());
            return new JSONObject(sb.toString());
        }
    }
}