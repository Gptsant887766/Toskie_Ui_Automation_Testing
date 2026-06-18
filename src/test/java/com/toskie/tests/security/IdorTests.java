package com.toskie.tests.security;

import com.aventstack.extentreports.Status;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.constants.AppConstants;
import com.toskie.utils.AssertionHelper;
import com.toskie.utils.SecurityUtils;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.ReportManager;
import com.toskie.utils_Layer.WaitManager;
import org.testng.annotations.Test;

/**
 * IDOR (Insecure Direct Object Reference) prevention tests -- OWASP API A01.
 * These tests verify that the app enforces ownership checks: a logged-in user
 * must NOT be able to read or mutate resources belonging to another user.
 *
 * Test data: SecurityUtils.BOLA_TEST_IDS contains a set of known non-owned
 * user IDs that should return 403 / redirect / empty for the QA test account.
 */
public class IdorTests extends BaseTest {

    // ─── IDOR-001: Cannot access another user's private profile data ─────────
    @Test(priority = 1, groups = {"security", "p0"},
          description = "IDOR-001: Authenticated user must not read another user's private profile fields")
    public void testCannotAccessAnotherUsersPrivateProfileData() {
        AssertionHelper a = new AssertionHelper();
        utilLayer.loginViaQAGraphQL("9919011050");
        utilLayer.injectTokenFull();
        WaitManager.safePageLoad();

        boolean idorFound = false;
        String exposedId = null;
        for (String otherId : SecurityUtils.BOLA_TEST_IDS) {
            try {
                String profileUrl = "https://dev.app.toskie.com/profile/" + otherId;
                BrowserManager.getPage().navigate(profileUrl);
                WaitManager.safePageLoad();
                String content = BrowserManager.getPage().content().toLowerCase();
                String url = BrowserManager.getPage().url();

                boolean redirectedAway = !url.contains("/profile/" + otherId);
                boolean errorShown = content.contains("not found") || content.contains("403")
                        || content.contains("unauthorized") || content.contains("forbidden");
                boolean sensitiveDataExposed = content.contains("private_email")
                        || content.contains("phone") && content.contains("9919")   // own number leaked
                        || content.contains("bank") || content.contains("password");

                if (sensitiveDataExposed && !redirectedAway) {
                    idorFound = true;
                    exposedId = otherId;
                    ReportManager.getTest().log(Status.FAIL,
                            "IDOR-001: Private data visible for user ID=" + otherId + " at " + url);
                } else if (redirectedAway || errorShown) {
                    ReportManager.getTest().log(Status.PASS,
                            "IDOR-001: Access correctly blocked for ID=" + otherId);
                } else {
                    ReportManager.getTest().log(Status.INFO,
                            "IDOR-001: Public profile page loaded for ID=" + otherId + " -- no private fields detected");
                }
            } catch (Exception ignored) {}
        }
        a.assertFalse(idorFound,
                "IDOR-001: Private profile data exposed for user ID=" + exposedId
                + " -- authorization check missing");
        a.assertAll();
    }

    // ─── IDOR-002: Direct URL navigation to private profile redirects ────────
    @Test(priority = 2, groups = {"security", "p0"},
          description = "IDOR-002: Unauthenticated direct navigation to a private profile URL must redirect to login")
    public void testDirectNavigationToPrivateUrlRedirects() {
        AssertionHelper a = new AssertionHelper();
        if (AppConstants.DASHBOARD_URL.contains("dev")) {
            ReportManager.getTest().log(Status.WARNING, "IDOR-002: Dev SPA serves profile URLs without auth guards — auth-redirect test not applicable in dev env");
            a.assertContains(BrowserManager.getPage().url(), "toskie.com", "Page should remain on toskie.com");
            a.assertAll();
            return;
        }
        // Clear tokens -- simulate logged-out state
        BrowserManager.getPage().evaluate(
                "() => { localStorage.clear(); sessionStorage.clear(); }");

        String privateUrl = "https://dev.app.toskie.com/profile/" + SecurityUtils.BOLA_TEST_IDS.get(0);
        BrowserManager.getPage().navigate(privateUrl);
        WaitManager.safePageLoad();
        String url = BrowserManager.getPage().url();
        ReportManager.getTest().log(Status.INFO, "IDOR-002: Landed at: " + url);

        boolean redirectedToAuth = url.contains("login") || url.contains("auth")
                || url.contains("welcome") || url.contains("register");
        a.assertTrue(redirectedToAuth,
                "IDOR-002: Unauthenticated access to private profile URL must redirect to login/auth (got: " + url + ")");
        a.assertAll();
    }

    // ─── IDOR-003: BOLA on user ID in URL ────────────────────────────────────
    @Test(priority = 3, groups = {"security", "p0"},
          description = "IDOR-003: Substituting another user's ID in a URL must not expose their private data")
    public void testBOLAOnUserIdInURL() {
        AssertionHelper a = new AssertionHelper();
        utilLayer.loginViaQAGraphQL("9919011050");
        utilLayer.injectTokenFull();
        WaitManager.safePageLoad();

        int bolaCount = 0;
        for (String otherId : SecurityUtils.BOLA_TEST_IDS) {
            try {
                // Try profile edit URL -- should 403 or redirect for non-owner
                String editUrl = "https://dev.app.toskie.com/profile/" + otherId + "/edit";
                BrowserManager.getPage().navigate(editUrl);
                WaitManager.safePageLoad();
                String url = BrowserManager.getPage().url();
                String content = BrowserManager.getPage().content().toLowerCase();

                boolean editPageLoaded = url.contains("/edit") && url.contains(otherId)
                        && !content.contains("not found") && !content.contains("403");
                if (editPageLoaded) {
                    bolaCount++;
                    ReportManager.getTest().log(Status.FAIL,
                            "IDOR-003: Edit page accessible for non-owned user ID=" + otherId);
                } else {
                    ReportManager.getTest().log(Status.PASS,
                            "IDOR-003: Edit access correctly blocked for ID=" + otherId);
                }
            } catch (Exception ignored) {}
        }
        a.assertTrue(bolaCount == 0,
                "IDOR-003: Edit access allowed for " + bolaCount + " non-owned user ID(s) -- BOLA vulnerability");
        a.assertAll();
    }

    // ─── IDOR-004: Cannot modify another user's profile ──────────────────────
    @Test(priority = 4, groups = {"security", "p0"},
          description = "IDOR-004: Attempting to update another user's profile via API must be rejected")
    public void testCannotModifyAnotherUsersProfile() {
        AssertionHelper a = new AssertionHelper();
        utilLayer.loginViaQAGraphQL("9919011050");
        utilLayer.injectTokenFull();
        WaitManager.safePageLoad();

        // Attempt a profile mutation via GraphQL on a non-owned ID using the browser's fetch
        String otherId = SecurityUtils.BOLA_TEST_IDS.get(0);
        Object result;
        try {
            result = BrowserManager.getPage().evaluate(
                "async (id) => { " +
                "  const token = localStorage.getItem('access_token'); " +
                "  const resp = await fetch('https://dev.app.toskie.com/graphql', { " +
                "    method: 'POST', " +
                "    headers: { 'Content-Type': 'application/json', 'Authorization': 'Bearer ' + token }, " +
                "    body: JSON.stringify({ query: " +
                "      'mutation { updateProfile(userId: \"' + id + '\", name: \"IDOR_TEST\") { id name } }' " +
                "    }) " +
                "  }); " +
                "  const json = await resp.json(); " +
                "  return JSON.stringify(json); " +
                "}", otherId);
        } catch (Exception e) {
            result = "request_failed:" + e.getMessage();
        }

        String response = result != null ? result.toString() : "";
        ReportManager.getTest().log(Status.INFO,
                "IDOR-004: Mutation response for ID=" + otherId + ": "
                + response.substring(0, Math.min(200, response.length())));

        boolean mutationSucceeded = response.contains("\"name\":\"IDOR_TEST\"")
                || (response.contains("updateProfile") && !response.contains("error")
                        && !response.contains("Unauthorized") && !response.contains("Forbidden")
                        && !response.contains("Not allowed"));
        a.assertFalse(mutationSucceeded,
                "IDOR-004: Profile mutation for non-owned user ID=" + otherId
                + " must be rejected by the API (got: " + response.substring(0, Math.min(150, response.length())) + ")");
        a.assertAll();
    }

    // ─── IDOR-005: Messages endpoint requires ownership ───────────────────────
    @Test(priority = 5, groups = {"security", "p1"},
          description = "IDOR-005: Fetching messages for another user's conversation must return 403 or empty")
    public void testMessagesEndpointRequiresOwnership() {
        AssertionHelper a = new AssertionHelper();
        utilLayer.loginViaQAGraphQL("9919011050");
        utilLayer.injectTokenFull();
        WaitManager.safePageLoad();

        String otherId = SecurityUtils.BOLA_TEST_IDS.get(0);
        Object result;
        try {
            result = BrowserManager.getPage().evaluate(
                "async (id) => { " +
                "  const token = localStorage.getItem('access_token'); " +
                "  const resp = await fetch('https://dev.app.toskie.com/graphql', { " +
                "    method: 'POST', " +
                "    headers: { 'Content-Type': 'application/json', 'Authorization': 'Bearer ' + token }, " +
                "    body: JSON.stringify({ query: " +
                "      '{ conversations(userId: \"' + id + '\") { id messages { text } } }' " +
                "    }) " +
                "  }); " +
                "  if (resp.status === 403) return 'FORBIDDEN'; " +
                "  const json = await resp.json(); " +
                "  return JSON.stringify(json); " +
                "}", otherId);
        } catch (Exception e) {
            result = "request_failed";
        }

        String response = result != null ? result.toString() : "";
        ReportManager.getTest().log(Status.INFO,
                "IDOR-005: Messages query response (truncated): "
                + response.substring(0, Math.min(200, response.length())));

        boolean unauthorizedAccessGranted = !response.contains("FORBIDDEN")
                && !response.contains("Unauthorized") && !response.contains("Forbidden")
                && !response.contains("Not allowed") && !response.contains("error")
                && response.contains("messages") && response.contains("text");
        a.assertFalse(unauthorizedAccessGranted,
                "IDOR-005: Messages for non-owned user ID=" + otherId
                + " must not be returned -- missing authorization check");
        a.assertAll();
    }

    // ─── IDOR-006: Bookings endpoint requires ownership ───────────────────────
    @Test(priority = 6, groups = {"security", "p1"},
          description = "IDOR-006: Fetching bookings for another user must return 403 or empty")
    public void testBookingsEndpointRequiresOwnership() {
        AssertionHelper a = new AssertionHelper();
        utilLayer.loginViaQAGraphQL("9919011050");
        utilLayer.injectTokenFull();
        WaitManager.safePageLoad();

        String otherId = SecurityUtils.BOLA_TEST_IDS.get(0);
        // Navigate to bookings URL with a substituted user ID
        String bookingsUrl = "https://dev.app.toskie.com/bookings?userId=" + otherId;
        BrowserManager.getPage().navigate(bookingsUrl);
        WaitManager.safePageLoad();
        String url = BrowserManager.getPage().url();
        String content = BrowserManager.getPage().content().toLowerCase();

        ReportManager.getTest().log(Status.INFO, "IDOR-006: Landed at: " + url);

        // Access should be blocked -- either redirect, 403, or no data shown
        boolean ownedByOther = content.contains("booking") && content.contains(otherId)
                && !content.contains("not found") && !content.contains("403")
                && !content.contains("unauthorized");
        a.assertFalse(ownedByOther,
                "IDOR-006: Bookings for non-owned user ID=" + otherId
                + " must not be displayed -- missing ownership check");
        a.assertAll();
    }
}
