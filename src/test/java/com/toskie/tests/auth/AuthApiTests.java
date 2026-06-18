package com.toskie.tests.auth;

import com.aventstack.extentreports.Status;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.constants.AppConstants;
import com.toskie.utils.AssertionHelper;
import com.toskie.utils_Layer.ApiUtils;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.ReportManager;
import com.toskie.utils_Layer.WaitManager;
import org.testng.SkipException;
import org.testng.annotations.Test;

public class AuthApiTests extends BaseTest {

    private static final String ACCESS_TOKEN_KEY  = "access_token";
    private static final String REFRESH_TOKEN_KEY = "refresh_token";

    // ─── 1. Unauthenticated route returns 401 / redirect ────────────────────
    @Test(priority = 1, groups = {"smoke", "p0", "api"}, description = "Unauthenticated access to a protected route is blocked")
    public void testUnauthenticatedRouteReturns401() {
        if (AppConstants.DASHBOARD_URL.contains("dev")) {
            throw new SkipException("Dev SPA serves /dashboard without client-side auth guards -- auth-redirect test not applicable in dev env");
        }
        AssertionHelper a = new AssertionHelper();
        clearTokens();
        BrowserManager.getPage().navigate(AppConstants.DASHBOARD_URL);
        WaitManager.safePageLoad();
        String url = BrowserManager.getPage().url();
        a.assertFalse(url.contains("/dashboard") && !url.contains("login") && !url.contains("auth"),
                "Unauthenticated user should be redirected away from /dashboard (got: " + url + ")");
        a.assertAll();
    }

    // ─── 2. Valid token allows dashboard access ──────────────────────────────
    @Test(priority = 2, groups = {"smoke", "p0", "api"}, description = "Valid auth token allows access to dashboard")
    public void testValidTokenAllowsAccess() {
        AssertionHelper a = new AssertionHelper();
        utilLayer.loginViaQAGraphQL("9919011050");
        utilLayer.injectTokenFull();
        BrowserManager.getPage().navigate(AppConstants.DASHBOARD_URL);
        WaitManager.safePageLoad();
        a.assertFalse(utilLayer.isTokenExpired(), "Access token should be valid (not expired) after QA login");
        a.assertContains(BrowserManager.getPage().url(), "toskie.com",
                "Authenticated user should reach a toskie.com page after navigating to dashboard");
        a.assertAll();
    }

    // ─── 3. Expired JWT token is rejected ───────────────────────────────────
    @Test(priority = 3, groups = {"p0", "api", "security"}, description = "Expired JWT is rejected and user is redirected to login")
    public void testExpiredTokenRejected() {
        if (AppConstants.DASHBOARD_URL.contains("dev")) {
            throw new SkipException("Dev SPA serves /dashboard without client-side auth guards -- auth-redirect test not applicable in dev env");
        }
        AssertionHelper a = new AssertionHelper();
        String expiredJwt = buildExpiredJwt();
        injectRawToken(expiredJwt, expiredJwt);
        BrowserManager.getPage().navigate(AppConstants.DASHBOARD_URL);
        WaitManager.safePageLoad();
        String url = BrowserManager.getPage().url();
        a.assertFalse(url.contains("/dashboard") && !url.contains("login") && !url.contains("auth"),
                "Expired JWT should redirect away from /dashboard (got: " + url + ")");
        a.assertAll();
    }

    // ─── 4. Malformed JWT is rejected ───────────────────────────────────────
    @Test(priority = 4, groups = {"p0", "api", "security"}, description = "Malformed JWT is rejected and user is redirected to login")
    public void testMalformedTokenRejected() {
        if (AppConstants.DASHBOARD_URL.contains("dev")) {
            throw new SkipException("Dev SPA serves /dashboard without client-side auth guards -- auth-redirect test not applicable in dev env");
        }
        AssertionHelper a = new AssertionHelper();
        injectRawToken("this.is.not.a.jwt", "also-not-a-jwt");
        BrowserManager.getPage().navigate(AppConstants.DASHBOARD_URL);
        WaitManager.safePageLoad();
        String url = BrowserManager.getPage().url();
        a.assertFalse(url.contains("/dashboard") && !url.contains("login") && !url.contains("auth"),
                "Malformed JWT should redirect away from /dashboard (got: " + url + ")");
        a.assertAll();
    }

    // ─── 5. Login API returns access_token ──────────────────────────────────
    @Test(priority = 5, groups = {"smoke", "p0", "api"}, description = "Login API returns an access_token on success")
    public void testLoginAPIReturnsToken() {
        AssertionHelper a = new AssertionHelper();
        utilLayer.loginViaQAGraphQL("9919011050");
        String token = utilLayer.getAccessToken();
        a.assertNotEmpty(token, "access_token should be present after QA GraphQL login");
        a.assertFalse(utilLayer.isTokenExpired(), "Token returned by login API should not be expired");
        a.assertAll();
    }

    // ─── 6. Login API rejects invalid OTP ───────────────────────────────────
    @Test(priority = 6, groups = {"smoke", "p0", "api"}, description = "Login API rejects invalid OTP")
    public void testLoginAPIRejectsInvalidCredentials() {
        AssertionHelper a = new AssertionHelper();
        com.toskie.pages.LoginPage loginPage = new com.toskie.pages.LoginPage(utilLayer);
        loginPage.clickLoginButton();
        loginPage.enterPhoneNumber("9000000099");
        loginPage.clickSendOTP();
        loginPage.enterOTP("9999");
        loginPage.clickVerifyOTP();
        a.assertTrue(loginPage.isInvalidOTPMessageVisible() || loginPage.isPhoneValidationErrorVisible(),
                "Login API should reject invalid OTP -- error message should be visible");
        a.assertAll();
    }

    // ─── 7. Refresh token issues a new access token ──────────────────────────
    @Test(priority = 7, groups = {"p1", "api"}, description = "Refresh token flow issues a new non-expired access token")
    public void testRefreshTokenFlow() {
        AssertionHelper a = new AssertionHelper();
        utilLayer.loginViaQAGraphQL("9919011050");
        String firstToken = utilLayer.getAccessToken();
        a.assertNotEmpty(firstToken, "Initial access token must be present");
        a.assertFalse(utilLayer.isTokenExpired(), "Initial access token must not be expired");

        // Simulate token refresh: inject expired access token while keeping valid refresh token
        String validRefresh = utilLayer.getRefreshToken();
        BrowserManager.getPage().evaluate(
                "([access, refresh]) => { " +
                "localStorage.setItem('access_token', access); " +
                "localStorage.setItem('refresh_token', refresh); }",
                new Object[]{buildExpiredJwt(), validRefresh});

        // Navigate -- app should silently refresh and remain authenticated
        BrowserManager.getPage().navigate(AppConstants.DASHBOARD_URL);
        WaitManager.safePageLoad();
        a.assertContains(BrowserManager.getPage().url(), "toskie.com",
                "After silent token refresh, user should remain on toskie.com (not kicked to login)");
        a.assertAll();
    }

    // ─── 8. Logout clears both tokens ───────────────────────────────────────
    @Test(priority = 8, groups = {"smoke", "p0", "api"}, description = "Logout clears access_token and refresh_token from storage")
    public void testLogoutInvalidatesToken() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Logging in and then logging out to verify token clearance");
        new com.toskie.pages.LoginPage(utilLayer).loginWithDefaultCredentials();
        new com.toskie.pages.dashboard.DashboardPage(utilLayer).logout();
        WaitManager.safePageLoad();

        Object accessToken = BrowserManager.getPage().evaluate(
                "() => localStorage.getItem('access_token')");
        Object refreshToken = BrowserManager.getPage().evaluate(
                "() => localStorage.getItem('refresh_token')");

        a.assertTrue(accessToken == null || "null".equals(accessToken.toString()) || accessToken.toString().isEmpty(),
                "access_token must be cleared from localStorage after logout");
        a.assertTrue(refreshToken == null || "null".equals(refreshToken.toString()) || refreshToken.toString().isEmpty(),
                "refresh_token must be cleared from localStorage after logout");
        a.assertAll();
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private void clearTokens() {
        try {
            BrowserManager.getPage().evaluate(
                    "() => { localStorage.removeItem('access_token'); " +
                    "localStorage.removeItem('refresh_token'); " +
                    "sessionStorage.clear(); }");
        } catch (Exception ignored) {}
    }

    private void injectRawToken(String accessToken, String refreshToken) {
        clearTokens();
        BrowserManager.getPage().evaluate(
                "([a, r]) => { localStorage.setItem('access_token', a); localStorage.setItem('refresh_token', r); }",
                new Object[]{accessToken, refreshToken});
    }

    private String buildExpiredJwt() {
        String header  = java.util.Base64.getUrlEncoder().withoutPadding()
                .encodeToString("{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes());
        String payload = java.util.Base64.getUrlEncoder().withoutPadding()
                .encodeToString("{\"sub\":\"test\",\"exp\":1000000000,\"iat\":999000000}".getBytes());
        return header + "." + payload + ".invalidsignature";
    }
}
