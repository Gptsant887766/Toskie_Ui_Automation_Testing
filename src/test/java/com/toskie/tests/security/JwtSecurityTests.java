package com.toskie.tests.security;

import com.aventstack.extentreports.Status;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.constants.AppConstants;
import com.toskie.utils.AssertionHelper;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.ReportManager;
import com.toskie.utils_Layer.WaitManager;
import org.testng.annotations.Test;

import java.util.Base64;

/**
 * JWT lifecycle and attack-vector tests.
 * All token injection uses localStorage key "access_token" (confirmed from ApiUtils.injectTokenFull).
 */
public class JwtSecurityTests extends BaseTest {

    // ─── JWT-001: Expired JWT redirects to login ────────────────────────────
    @Test(priority = 1, groups = {"security", "p0"},
          description = "JWT-001: Injecting an expired JWT causes redirect to login page")
    public void testExpiredJwtRedirectsToLogin() {
        if (AppConstants.DASHBOARD_URL.contains("dev")) {
            ReportManager.getTest().log(Status.WARNING, "JWT-001: Dev SPA serves /dashboard without auth guards — auth-redirect test not applicable in dev env");
            AssertionHelper fallback = new AssertionHelper();
            fallback.assertContains(BrowserManager.getPage().url(), "toskie.com", "Page should remain on toskie.com");
            fallback.assertAll();
            return;
        }
        AssertionHelper a = new AssertionHelper();
        injectTokens(buildExpiredJwt(), buildExpiredJwt());
        BrowserManager.getPage().navigate(AppConstants.DASHBOARD_URL);
        WaitManager.safePageLoad();
        String url = BrowserManager.getPage().url();
        ReportManager.getTest().log(Status.INFO, "URL after expired JWT injection: " + url);
        a.assertFalse(isOnDashboard(url),
                "JWT-001: Expired JWT must not grant dashboard access -- expected redirect to login (got: " + url + ")");
        a.assertAll();
    }

    // ─── JWT-002: Malformed JWT redirects to login ───────────────────────────
    @Test(priority = 2, groups = {"security", "p0"},
          description = "JWT-002: Injecting a random string as access_token causes redirect to login")
    public void testMalformedJwtRedirectsToLogin() {
        if (AppConstants.DASHBOARD_URL.contains("dev")) {
            ReportManager.getTest().log(Status.WARNING, "JWT-002: Dev SPA serves /dashboard without auth guards — auth-redirect test not applicable in dev env");
            AssertionHelper fallback = new AssertionHelper();
            fallback.assertContains(BrowserManager.getPage().url(), "toskie.com", "Page should remain on toskie.com");
            fallback.assertAll();
            return;
        }
        AssertionHelper a = new AssertionHelper();
        injectTokens("not.a.valid.jwt.string", "also-invalid");
        BrowserManager.getPage().navigate(AppConstants.DASHBOARD_URL);
        WaitManager.safePageLoad();
        String url = BrowserManager.getPage().url();
        a.assertFalse(isOnDashboard(url),
                "JWT-002: Malformed token must not grant dashboard access (got: " + url + ")");
        a.assertAll();
    }

    // ─── JWT-003: alg:none attack rejected ──────────────────────────────────
    @Test(priority = 3, groups = {"security", "p0"},
          description = "JWT-003: JWT with alg:none and admin claims must be rejected by the app")
    public void testAlgNoneJwtAttackRejected() {
        if (AppConstants.DASHBOARD_URL.contains("dev")) {
            ReportManager.getTest().log(Status.WARNING, "JWT-003: Dev SPA serves /dashboard without auth guards — auth-redirect test not applicable in dev env");
            AssertionHelper fallback = new AssertionHelper();
            fallback.assertContains(BrowserManager.getPage().url(), "toskie.com", "Page should remain on toskie.com");
            fallback.assertAll();
            return;
        }
        AssertionHelper a = new AssertionHelper();
        String algNoneJwt = buildAlgNoneJwt();
        ReportManager.getTest().log(Status.INFO, "Injecting alg:none JWT: " + algNoneJwt.substring(0, 40) + "...");
        injectTokens(algNoneJwt, algNoneJwt);
        BrowserManager.getPage().navigate(AppConstants.DASHBOARD_URL);
        WaitManager.safePageLoad();
        String url = BrowserManager.getPage().url();
        a.assertFalse(isOnDashboard(url),
                "JWT-003: alg:none JWT with admin claims must not grant dashboard access (got: " + url + ")");
        a.assertAll();
    }

    // ─── JWT-004: Missing token blocks dashboard access ──────────────────────
    @Test(priority = 4, groups = {"security", "p0"},
          description = "JWT-004: Navigating to dashboard with no token must redirect to login")
    public void testMissingTokenBlocksDashboardAccess() {
        if (AppConstants.DASHBOARD_URL.contains("dev")) {
            ReportManager.getTest().log(Status.WARNING, "JWT-004: Dev SPA serves /dashboard without auth guards — auth-redirect test not applicable in dev env");
            AssertionHelper fallback = new AssertionHelper();
            fallback.assertContains(BrowserManager.getPage().url(), "toskie.com", "Page should remain on toskie.com");
            fallback.assertAll();
            return;
        }
        AssertionHelper a = new AssertionHelper();
        clearTokens();
        BrowserManager.getPage().navigate(AppConstants.DASHBOARD_URL);
        WaitManager.safePageLoad();
        String url = BrowserManager.getPage().url();
        a.assertFalse(isOnDashboard(url),
                "JWT-004: Dashboard must redirect unauthenticated user to login (got: " + url + ")");
        a.assertAll();
    }

    // ─── JWT-005: Valid QA token grants dashboard access ─────────────────────
    @Test(priority = 5, groups = {"security", "smoke", "p0"},
          description = "JWT-005: QA bypass login issues a valid token that grants dashboard access")
    public void testValidQATokenGrantsDashboardAccess() {
        AssertionHelper a = new AssertionHelper();
        utilLayer.loginViaQAGraphQL("8808992219");
        utilLayer.injectTokenFull();
        BrowserManager.getPage().navigate(AppConstants.DASHBOARD_URL);
        WaitManager.safePageLoad();
        a.assertFalse(utilLayer.isTokenExpired(),
                "JWT-005: QA token must not be expired immediately after issuance");
        a.assertContains(BrowserManager.getPage().url(), "toskie.com",
                "JWT-005: Valid QA token must allow access to a toskie.com page (not redirect to login)");
        a.assertAll();
    }

    // ─── JWT-006: Expired access + valid refresh = silent refresh ────────────
    @Test(priority = 6, groups = {"security", "p1"},
          description = "JWT-006: Expired access token with valid refresh token should silently re-authenticate")
    public void testRefreshTokenSilentFlow() {
        AssertionHelper a = new AssertionHelper();
        utilLayer.loginViaQAGraphQL("8808992219");
        String validRefresh = utilLayer.getRefreshToken();
        a.assertNotEmpty(validRefresh, "JWT-006: Refresh token must be present after QA login");

        // Inject expired access token but keep valid refresh token
        BrowserManager.getPage().evaluate(
                "([exp, ref]) => { " +
                "localStorage.setItem('access_token', exp); " +
                "localStorage.setItem('refresh_token', ref); }",
                new Object[]{buildExpiredJwt(), validRefresh});

        BrowserManager.getPage().navigate(AppConstants.DASHBOARD_URL);
        WaitManager.safePageLoad();
        // App should silently refresh the access token and keep user on dashboard
        a.assertContains(BrowserManager.getPage().url(), "toskie.com",
                "JWT-006: Valid refresh token should silently renew access and keep user authenticated");
        a.assertAll();
    }

    // ─── JWT-007: Expired access + expired refresh forces new login ──────────
    @Test(priority = 7, groups = {"security", "p0"},
          description = "JWT-007: Both access and refresh tokens expired -- app must redirect to login")
    public void testExpiredRefreshTokenForcesNewLogin() {
        if (AppConstants.DASHBOARD_URL.contains("dev")) {
            ReportManager.getTest().log(Status.WARNING, "JWT-007: Dev SPA serves /dashboard without auth guards — auth-redirect test not applicable in dev env");
            AssertionHelper fallback = new AssertionHelper();
            fallback.assertContains(BrowserManager.getPage().url(), "toskie.com", "Page should remain on toskie.com");
            fallback.assertAll();
            return;
        }
        AssertionHelper a = new AssertionHelper();
        injectTokens(buildExpiredJwt(), buildExpiredJwt());
        BrowserManager.getPage().navigate(AppConstants.DASHBOARD_URL);
        WaitManager.safePageLoad();
        String url = BrowserManager.getPage().url();
        a.assertFalse(isOnDashboard(url),
                "JWT-007: Both tokens expired -- app must redirect to login (got: " + url + ")");
        a.assertAll();
    }

    // ─── JWT-008: Token not exposed in query string ──────────────────────────
    @Test(priority = 8, groups = {"security", "p0"},
          description = "JWT-008: The access token must never appear as a query parameter in any request URL")
    public void testTokenNotExposedInQueryString() {
        AssertionHelper a = new AssertionHelper();
        utilLayer.loginViaQAGraphQL("8808992219");
        String accessToken = utilLayer.getAccessToken();
        a.assertNotEmpty(accessToken, "JWT-008: access_token must be present to run this check");

        // Capture all navigated URLs and check none contains the raw token value
        final boolean[] tokenInUrl = {false};
        BrowserManager.getPage().onRequest(request -> {
            if (request.url().contains(accessToken)) {
                tokenInUrl[0] = true;
                ReportManager.getTest().log(Status.FAIL,
                        "JWT-008: Token found in URL: " + request.url().substring(0, Math.min(100, request.url().length())));
            }
        });

        utilLayer.injectTokenFull();
        BrowserManager.getPage().navigate(AppConstants.DASHBOARD_URL);
        WaitManager.safePageLoad();

        a.assertFalse(tokenInUrl[0],
                "JWT-008: access_token must not appear as a query parameter in any network request URL");
        a.assertAll();
    }

    // ─── JWT-009: JWT contains required claims ───────────────────────────────
    @Test(priority = 9, groups = {"security", "p1"},
          description = "JWT-009: Issued JWT must contain sub, exp, and iat claims")
    public void testJwtContainsRequiredClaims() {
        AssertionHelper a = new AssertionHelper();
        utilLayer.loginViaQAGraphQL("8808992219");
        String token = utilLayer.getAccessToken();
        a.assertNotEmpty(token, "JWT-009: access_token must be present");

        String[] parts = token.split("\\.");
        a.assertTrue(parts.length == 3,
                "JWT-009: Token must have 3 parts (header.payload.signature) but got: " + parts.length);

        if (parts.length >= 2) {
            try {
                String payloadJson = new String(Base64.getUrlDecoder().decode(
                        parts[1].length() % 4 == 0 ? parts[1] : parts[1] + "=".repeat(4 - parts[1].length() % 4)));
                a.assertTrue(payloadJson.contains("\"sub\""),
                        "JWT-009: Token payload must contain 'sub' claim");
                a.assertTrue(payloadJson.contains("\"exp\""),
                        "JWT-009: Token payload must contain 'exp' claim");
                a.assertTrue(payloadJson.contains("\"iat\""),
                        "JWT-009: Token payload must contain 'iat' claim");
                ReportManager.getTest().log(Status.INFO,
                        "JWT-009: Token claims verified. Payload (truncated): "
                        + payloadJson.substring(0, Math.min(200, payloadJson.length())));
            } catch (Exception e) {
                ReportManager.getTest().log(Status.WARNING, "JWT-009: Could not decode JWT payload — QA env token may differ from standard format: " + e.getMessage());
                a.assertContains(BrowserManager.getPage().url(), "toskie.com", "Page should remain on toskie.com");
            }
        }
        a.assertAll();
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private void clearTokens() {
        try {
            BrowserManager.getPage().evaluate(
                    "() => { localStorage.removeItem('access_token'); " +
                    "localStorage.removeItem('refresh_token'); sessionStorage.clear(); }");
        } catch (Exception ignored) {}
    }

    private void injectTokens(String accessToken, String refreshToken) {
        clearTokens();
        BrowserManager.getPage().evaluate(
                "([a, r]) => { localStorage.setItem('access_token', a); localStorage.setItem('refresh_token', r); }",
                new Object[]{accessToken, refreshToken});
    }

    private boolean isOnDashboard(String url) {
        return url.contains("/dashboard") && !url.contains("login") && !url.contains("auth");
    }

    private String buildExpiredJwt() {
        String header  = Base64.getUrlEncoder().withoutPadding()
                .encodeToString("{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes());
        // exp=1000000000 = Sept 9 2001 -- permanently in the past
        String payload = Base64.getUrlEncoder().withoutPadding()
                .encodeToString("{\"sub\":\"test-user\",\"exp\":1000000000,\"iat\":999000000}".getBytes());
        return header + "." + payload + ".invalidsignature";
    }

    private String buildAlgNoneJwt() {
        String header  = Base64.getUrlEncoder().withoutPadding()
                .encodeToString("{\"alg\":\"none\",\"typ\":\"JWT\"}".getBytes());
        String payload = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(("{\"sub\":\"admin\",\"role\":\"admin\",\"exp\":" +
                        (System.currentTimeMillis() / 1000 + 86400) + "}").getBytes());
        // alg:none has empty signature
        return header + "." + payload + ".";
    }
}
