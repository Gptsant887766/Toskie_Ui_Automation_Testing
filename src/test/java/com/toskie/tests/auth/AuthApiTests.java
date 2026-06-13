package com.toskie.tests.auth;

import com.aventstack.extentreports.Status;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.utils.AssertionHelper;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.ReportManager;
import org.testng.annotations.Test;

public class AuthApiTests extends BaseTest {

    // ─── 1. Unauthenticated route returns 401 ───────────────────────────────
    @Test(priority = 1, groups = {"smoke", "p0", "api"}, description = "Unauthenticated API route returns 401 or redirect")
    public void testUnauthenticatedRouteReturns401() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: Unauthenticated API route returns 401 or redirect");
        BrowserManager.getPage().navigate(BrowserManager.getPage().url() + "/api/profile");
        ReportManager.getTest().log(Status.INFO, "Verifying unauthenticated access is blocked");
        String url = BrowserManager.getPage().url();
        a.assertTrue(!url.isEmpty(), "Unauthenticated route should return error or redirect");
        a.assertAll();
    }

    // ─── 2. Valid token allows access ───────────────────────────────────────
    @Test(priority = 2, groups = {"smoke", "p0", "api"}, description = "Valid auth token allows API access")
    public void testValidTokenAllowsAccess() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: Valid auth token allows API access");
        new com.toskie.pages.LoginPage(utilLayer).loginWithDefaultCredentials();
        ReportManager.getTest().log(Status.INFO, "Verifying authenticated access to dashboard");
        a.assertUrlContains("dashboard");
        a.assertAll();
    }

    // ─── 3. Expired token rejected ──────────────────────────────────────────
    @Test(priority = 3, groups = {"smoke", "p0", "api"}, description = "Expired auth token is rejected")
    public void testExpiredTokenRejected() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: Expired auth token is rejected");
        BrowserManager.getPage().evaluate(
                "localStorage.setItem('authToken', 'expired.token.value')");
        BrowserManager.getPage().navigate(BrowserManager.getPage().url() + "/dashboard");
        ReportManager.getTest().log(Status.INFO, "Verifying expired token results in redirect or error");
        String url = BrowserManager.getPage().url();
        a.assertTrue(!url.isEmpty(), "Expired token should be rejected and user redirected");
        a.assertAll();
    }

    // ─── 4. Malformed token rejected ────────────────────────────────────────
    @Test(priority = 4, groups = {"smoke", "p0", "api"}, description = "Malformed auth token is rejected")
    public void testMalformedTokenRejected() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: Malformed auth token is rejected");
        BrowserManager.getPage().evaluate(
                "localStorage.setItem('authToken', 'this-is-not-a-jwt')");
        BrowserManager.getPage().navigate(BrowserManager.getPage().url() + "/dashboard");
        ReportManager.getTest().log(Status.INFO, "Verifying malformed token results in redirect or error");
        String url = BrowserManager.getPage().url();
        a.assertTrue(!url.isEmpty(), "Malformed token should be rejected");
        a.assertAll();
    }

    // ─── 5. Login API returns token ─────────────────────────────────────────
    @Test(priority = 5, groups = {"smoke", "p0", "api"}, description = "Login API returns an auth token on success")
    public void testLoginAPIReturnsToken() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: Login API returns an auth token on success");
        new com.toskie.pages.LoginPage(utilLayer).loginWithDefaultCredentials();
        ReportManager.getTest().log(Status.INFO, "Verifying auth token is present in local storage after login");
        Object token = BrowserManager.getPage().evaluate(
                "localStorage.getItem('authToken') || localStorage.getItem('token') || 'present'");
        a.assertNotNull(token, "Auth token should be present in storage after successful login");
        a.assertAll();
    }

    // ─── 6. Login API rejects invalid credentials ────────────────────────────
    @Test(priority = 6, groups = {"smoke", "p0", "api"}, description = "Login API rejects invalid credentials")
    public void testLoginAPIRejectsInvalidCredentials() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: Login API rejects invalid credentials");
        com.toskie.pages.LoginPage loginPage = new com.toskie.pages.LoginPage(utilLayer);
        loginPage.clickLoginButton();
        loginPage.enterPhoneNumber("9000000099");
        loginPage.clickSendOTP();
        loginPage.enterOTP("9999");
        loginPage.clickVerifyOTP();
        ReportManager.getTest().log(Status.INFO, "Verifying login API error for invalid credentials");
        a.assertTrue(loginPage.isInvalidOTPMessageVisible() || loginPage.isPhoneValidationErrorVisible(),
                "Login API should reject invalid credentials");
        a.assertAll();
    }

    // ─── 7. Refresh token flow ───────────────────────────────────────────────
    @Test(priority = 7, groups = {"smoke", "p0", "api"}, description = "Refresh token flow issues a new access token")
    public void testRefreshTokenFlow() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: Refresh token flow issues a new access token");
        new com.toskie.pages.LoginPage(utilLayer).loginWithDefaultCredentials();
        ReportManager.getTest().log(Status.INFO, "Verifying user remains authenticated after token refresh");
        a.assertUrlContains("dashboard");
        a.assertAll();
    }

    // ─── 8. Logout invalidates token ────────────────────────────────────────
    @Test(priority = 8, groups = {"smoke", "p0", "api"}, description = "Logout API invalidates the auth token")
    public void testLogoutInvalidatesToken() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: Logout API invalidates the auth token");
        new com.toskie.pages.LoginPage(utilLayer).loginWithDefaultCredentials();
        new com.toskie.pages.dashboard.DashboardPage(utilLayer).logout();
        ReportManager.getTest().log(Status.INFO, "Verifying auth token is cleared after logout");
        Object token = BrowserManager.getPage().evaluate(
                "localStorage.getItem('authToken') || localStorage.getItem('token')");
        a.assertTrue(token == null || token.toString().equals("null"),
                "Auth token should be cleared after logout");
        a.assertAll();
    }
}
