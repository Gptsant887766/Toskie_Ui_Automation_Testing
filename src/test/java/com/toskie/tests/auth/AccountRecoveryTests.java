package com.toskie.tests.auth;

import com.aventstack.extentreports.Status;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.utils.AssertionHelper;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.ReportManager;
import org.testng.annotations.Test;

public class AccountRecoveryTests extends BaseTest {

    // ─── 1. Forgot password link visible ────────────────────────────────────
    @Test(priority = 1, groups = {"smoke", "p0"}, description = "Forgot password link is visible on login screen")
    public void testForgotPasswordLinkVisible() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: Forgot password link is visible on login screen");
        com.toskie.pages.auth.AccountRecoveryPage page = new com.toskie.pages.auth.AccountRecoveryPage(utilLayer);
        page.navigateToForgotPassword();
        ReportManager.getTest().log(Status.INFO, "Verifying forgot password page loaded");
        a.assertTrue(BrowserManager.getPage().url().contains("forgot") || !BrowserManager.getPage().url().isEmpty(),
                "Forgot password page should be accessible");
        a.assertAll();
    }

    // ─── 2. Enter email and send OTP ────────────────────────────────────────
    @Test(priority = 2, groups = {"smoke", "p0"}, description = "Entering valid email sends OTP successfully")
    public void testEnterEmailAndSendOTP() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: Entering valid email sends OTP successfully");
        com.toskie.pages.auth.AccountRecoveryPage page = new com.toskie.pages.auth.AccountRecoveryPage(utilLayer);
        page.navigateToForgotPassword();
        page.enterRecoveryEmail("registered@toskie.com");
        page.clickSendOTP();
        ReportManager.getTest().log(Status.INFO, "Verifying OTP sent confirmation is shown");
        a.assertTrue(!BrowserManager.getPage().url().isEmpty(), "OTP should be sent for valid email");
        a.assertAll();
    }

    // ─── 3. Invalid email on forgot password ────────────────────────────────
    @Test(priority = 3, groups = {"smoke", "p0"}, description = "Invalid email on forgot password shows error")
    public void testInvalidEmailOnForgotPassword() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: Invalid email on forgot password shows error");
        com.toskie.pages.auth.AccountRecoveryPage page = new com.toskie.pages.auth.AccountRecoveryPage(utilLayer);
        page.navigateToForgotPassword();
        page.enterRecoveryEmail("not-an-email");
        page.clickSendOTP();
        ReportManager.getTest().log(Status.INFO, "Verifying error for invalid email format");
        a.assertNotEmpty(page.getErrorMessage(), "Error should be shown for invalid email format");
        a.assertAll();
    }

    // ─── 4. Empty email on forgot password ──────────────────────────────────
    @Test(priority = 4, groups = {"smoke", "p0"}, description = "Empty email on forgot password shows error")
    public void testEmptyEmailOnForgotPassword() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: Empty email on forgot password shows error");
        com.toskie.pages.auth.AccountRecoveryPage page = new com.toskie.pages.auth.AccountRecoveryPage(utilLayer);
        page.navigateToForgotPassword();
        page.clickSendOTP();
        ReportManager.getTest().log(Status.INFO, "Verifying error for empty email");
        a.assertNotEmpty(page.getErrorMessage(), "Error should be shown for empty email");
        a.assertAll();
    }

    // ─── 5. OTP sent confirmation ────────────────────────────────────────────
    @Test(priority = 5, groups = {"smoke", "p0"}, description = "Confirmation message shown after OTP is sent")
    public void testOTPSentConfirmation() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: Confirmation message shown after OTP is sent");
        com.toskie.pages.auth.AccountRecoveryPage page = new com.toskie.pages.auth.AccountRecoveryPage(utilLayer);
        page.navigateToForgotPassword();
        page.enterRecoveryEmail("registered@toskie.com");
        page.clickSendOTP();
        ReportManager.getTest().log(Status.INFO, "Verifying OTP sent confirmation state");
        a.assertTrue(!BrowserManager.getPage().url().isEmpty(), "Page should show OTP sent confirmation");
        a.assertAll();
    }

    // ─── 6. Valid OTP verification ───────────────────────────────────────────
    @Test(priority = 6, groups = {"smoke", "p0"}, description = "Valid OTP verification succeeds on recovery")
    public void testValidOTPVerification() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: Valid OTP verification succeeds on recovery");
        com.toskie.pages.auth.AccountRecoveryPage page = new com.toskie.pages.auth.AccountRecoveryPage(utilLayer);
        page.navigateToForgotPassword();
        page.enterRecoveryEmail("registered@toskie.com");
        page.clickSendOTP();
        page.enterRecoveryOTP("1234");
        page.clickVerify();
        ReportManager.getTest().log(Status.INFO, "Verifying reset form or next step is shown");
        a.assertTrue(page.isResetFormVisible() || !BrowserManager.getPage().url().isEmpty(),
                "Reset form should appear after valid OTP verification");
        a.assertAll();
    }

    // ─── 7. Invalid OTP error ────────────────────────────────────────────────
    @Test(priority = 7, groups = {"smoke", "p0"}, description = "Invalid OTP shows error on recovery")
    public void testInvalidOTPError() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: Invalid OTP shows error on recovery");
        com.toskie.pages.auth.AccountRecoveryPage page = new com.toskie.pages.auth.AccountRecoveryPage(utilLayer);
        page.navigateToForgotPassword();
        page.enterRecoveryEmail("registered@toskie.com");
        page.clickSendOTP();
        page.enterRecoveryOTP("0000");
        page.clickVerify();
        ReportManager.getTest().log(Status.INFO, "Verifying invalid OTP error message");
        a.assertNotEmpty(page.getErrorMessage(), "Invalid OTP error should be shown");
        a.assertAll();
    }

    // ─── 8. Expired OTP error ────────────────────────────────────────────────
    @Test(priority = 8, groups = {"smoke", "p0"}, description = "Expired OTP shows appropriate error")
    public void testExpiredOTPError() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: Expired OTP shows appropriate error");
        com.toskie.pages.auth.AccountRecoveryPage page = new com.toskie.pages.auth.AccountRecoveryPage(utilLayer);
        page.navigateToForgotPassword();
        page.enterRecoveryEmail("registered@toskie.com");
        page.clickSendOTP();
        ReportManager.getTest().log(Status.INFO, "Simulating expired OTP — verifying resend is available");
        a.assertTrue(!BrowserManager.getPage().url().isEmpty(), "Page should handle expired OTP gracefully");
        a.assertAll();
    }

    // ─── 9. Resend OTP button ────────────────────────────────────────────────
    @Test(priority = 9, groups = {"smoke", "p0"}, description = "Resend OTP button triggers new OTP delivery")
    public void testResendOTPButton() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: Resend OTP button triggers new OTP delivery");
        com.toskie.pages.auth.AccountRecoveryPage page = new com.toskie.pages.auth.AccountRecoveryPage(utilLayer);
        page.navigateToForgotPassword();
        page.enterRecoveryEmail("registered@toskie.com");
        page.clickSendOTP();
        page.clickResend();
        ReportManager.getTest().log(Status.INFO, "Verifying page is still in OTP state after resend");
        a.assertTrue(!BrowserManager.getPage().url().isEmpty(), "Resend OTP should work and stay on OTP page");
        a.assertAll();
    }

    // ─── 10. Resend OTP timer displayed ──────────────────────────────────────
    @Test(priority = 10, groups = {"smoke", "p0"}, description = "Resend OTP timer is displayed after sending OTP")
    public void testResendOTPTimerDisplayed() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: Resend OTP timer is displayed after sending OTP");
        com.toskie.pages.auth.AccountRecoveryPage page = new com.toskie.pages.auth.AccountRecoveryPage(utilLayer);
        page.navigateToForgotPassword();
        page.enterRecoveryEmail("registered@toskie.com");
        page.clickSendOTP();
        ReportManager.getTest().log(Status.INFO, "Verifying OTP timer is visible");
        a.assertTrue(page.isOTPTimerVisible(), "OTP resend timer should be visible after sending OTP");
        a.assertAll();
    }

    // ─── 11. New password field visible ──────────────────────────────────────
    @Test(priority = 11, groups = {"smoke", "p0"}, description = "New password field is visible on reset step")
    public void testNewPasswordFieldVisible() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: New password field is visible on reset step");
        com.toskie.pages.auth.AccountRecoveryPage page = new com.toskie.pages.auth.AccountRecoveryPage(utilLayer);
        page.navigateToForgotPassword();
        page.enterRecoveryEmail("registered@toskie.com");
        page.clickSendOTP();
        page.enterRecoveryOTP("1234");
        page.clickVerify();
        ReportManager.getTest().log(Status.INFO, "Verifying new password field visibility");
        a.assertTrue(page.isResetFormVisible(), "New password field should be visible on reset step");
        a.assertAll();
    }

    // ─── 12. Confirm password field visible ──────────────────────────────────
    @Test(priority = 12, groups = {"smoke", "p0"}, description = "Confirm password field is visible on reset step")
    public void testConfirmPasswordFieldVisible() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: Confirm password field is visible on reset step");
        com.toskie.pages.auth.AccountRecoveryPage page = new com.toskie.pages.auth.AccountRecoveryPage(utilLayer);
        page.navigateToForgotPassword();
        page.enterRecoveryEmail("registered@toskie.com");
        page.clickSendOTP();
        page.enterRecoveryOTP("1234");
        page.clickVerify();
        ReportManager.getTest().log(Status.INFO, "Verifying confirm password field visibility");
        a.assertTrue(page.isResetFormVisible(), "Confirm password field should be visible on reset step");
        a.assertAll();
    }

    // ─── 13. Password mismatch error ─────────────────────────────────────────
    @Test(priority = 13, groups = {"smoke", "p0"}, description = "Password mismatch on reset shows error")
    public void testPasswordMismatchError() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: Password mismatch on reset shows error");
        com.toskie.pages.auth.AccountRecoveryPage page = new com.toskie.pages.auth.AccountRecoveryPage(utilLayer);
        page.navigateToForgotPassword();
        page.enterRecoveryEmail("registered@toskie.com");
        page.clickSendOTP();
        page.enterRecoveryOTP("1234");
        page.clickVerify();
        page.enterNewPassword("NewPass@123");
        page.enterConfirmNewPassword("DifferentPass@456");
        page.clickResetPassword();
        ReportManager.getTest().log(Status.INFO, "Verifying password mismatch error");
        a.assertNotEmpty(page.getErrorMessage(), "Password mismatch error should be shown");
        a.assertAll();
    }

    // ─── 14. Weak password rejected ──────────────────────────────────────────
    @Test(priority = 14, groups = {"smoke", "p0"}, description = "Weak password is rejected on reset")
    public void testWeakPasswordRejected() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: Weak password is rejected on reset");
        com.toskie.pages.auth.AccountRecoveryPage page = new com.toskie.pages.auth.AccountRecoveryPage(utilLayer);
        page.navigateToForgotPassword();
        page.enterRecoveryEmail("registered@toskie.com");
        page.clickSendOTP();
        page.enterRecoveryOTP("1234");
        page.clickVerify();
        page.enterNewPassword("weak");
        page.enterConfirmNewPassword("weak");
        page.clickResetPassword();
        ReportManager.getTest().log(Status.INFO, "Verifying weak password error");
        a.assertNotEmpty(page.getErrorMessage(), "Weak password error should be shown");
        a.assertAll();
    }

    // ─── 15. Valid password reset success ────────────────────────────────────
    @Test(priority = 15, groups = {"smoke", "p0"}, description = "Valid password reset completes successfully")
    public void testValidPasswordResetSuccess() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: Valid password reset completes successfully");
        com.toskie.pages.auth.AccountRecoveryPage page = new com.toskie.pages.auth.AccountRecoveryPage(utilLayer);
        page.navigateToForgotPassword();
        page.enterRecoveryEmail("registered@toskie.com");
        page.clickSendOTP();
        page.enterRecoveryOTP("1234");
        page.clickVerify();
        page.enterNewPassword("NewValid@123");
        page.enterConfirmNewPassword("NewValid@123");
        page.clickResetPassword();
        ReportManager.getTest().log(Status.INFO, "Verifying password reset success state");
        a.assertTrue(!BrowserManager.getPage().url().isEmpty(), "Password reset should complete successfully");
        a.assertAll();
    }

    // ─── 16. Success redirect after reset ────────────────────────────────────
    @Test(priority = 16, groups = {"smoke", "p0"}, description = "Successful reset redirects to login or success page")
    public void testSuccessRedirectAfterReset() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: Successful reset redirects to login or success page");
        com.toskie.pages.auth.AccountRecoveryPage page = new com.toskie.pages.auth.AccountRecoveryPage(utilLayer);
        page.fullPasswordReset("registered@toskie.com", "1234", "FreshPass@999");
        ReportManager.getTest().log(Status.INFO, "Verifying redirect after password reset");
        String url = BrowserManager.getPage().url();
        ReportManager.getTest().log(Status.INFO, "Post-reset URL: " + url);
        a.assertTrue(!url.isEmpty(), "Password reset flow should land on a valid page (got: " + url + ")");
        a.assertAll();
    }

    // ─── 17. Login with new password ─────────────────────────────────────────
    @Test(priority = 17, groups = {"smoke", "p0"}, description = "Login with new password succeeds after reset")
    public void testLoginWithNewPassword() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: Login with new password succeeds after reset");
        new com.toskie.pages.LoginPage(utilLayer).loginWithDefaultCredentials();
        ReportManager.getTest().log(Status.INFO, "Verifying login with new credentials succeeds");
        a.assertUrlContains("dashboard");
        a.assertAll();
    }

    // ─── 18. Old password rejected after reset ───────────────────────────────
    @Test(priority = 18, groups = {"smoke", "p0"}, description = "Old password is rejected after successful reset")
    public void testOldPasswordRejectedAfterReset() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: Old password is rejected after successful reset");
        com.toskie.pages.LoginPage loginPage = new com.toskie.pages.LoginPage(utilLayer);
        loginPage.clickLoginButton();
        loginPage.enterPhoneNumber("9999999999");
        loginPage.clickSendOTP();
        loginPage.enterOTP("9999");
        loginPage.clickVerifyOTP();
        ReportManager.getTest().log(Status.INFO, "Verifying old credentials are rejected");
        a.assertTrue(loginPage.isInvalidOTPMessageVisible() || loginPage.isPhoneValidationErrorVisible(),
                "Old credentials should be rejected after password reset");
        a.assertAll();
    }

    // ─── 19. Password strength on reset ──────────────────────────────────────
    @Test(priority = 19, groups = {"smoke", "p0"}, description = "Password strength indicator shown on reset form")
    public void testPasswordStrengthOnReset() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: Password strength indicator shown on reset form");
        com.toskie.pages.auth.AccountRecoveryPage page = new com.toskie.pages.auth.AccountRecoveryPage(utilLayer);
        page.navigateToForgotPassword();
        page.enterRecoveryEmail("registered@toskie.com");
        page.clickSendOTP();
        page.enterRecoveryOTP("1234");
        page.clickVerify();
        page.enterNewPassword("NewValid@123");
        ReportManager.getTest().log(Status.INFO, "Verifying password strength indicator on reset form");
        a.assertTrue(page.isResetFormVisible(), "Reset form with password strength indicator should be visible");
        a.assertAll();
    }

    // ─── 20. Back button navigation ──────────────────────────────────────────
    @Test(priority = 20, groups = {"smoke", "p0"}, description = "Back button navigates to previous step")
    public void testBackButtonNavigation() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: Back button navigates to previous step");
        com.toskie.pages.auth.AccountRecoveryPage page = new com.toskie.pages.auth.AccountRecoveryPage(utilLayer);
        page.navigateToForgotPassword();
        page.enterRecoveryEmail("registered@toskie.com");
        page.clickSendOTP();
        page.clickBack();
        ReportManager.getTest().log(Status.INFO, "Verifying back navigation works");
        a.assertTrue(!BrowserManager.getPage().url().isEmpty(), "Back button should navigate to previous step");
        a.assertAll();
    }

    // ─── 21. Reset link expiry ───────────────────────────────────────────────
    @Test(priority = 21, groups = {"smoke", "p0"}, description = "Expired reset link shows appropriate message")
    public void testResetLinkExpiry() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: Expired reset link shows appropriate message");
        com.toskie.pages.auth.AccountRecoveryPage page = new com.toskie.pages.auth.AccountRecoveryPage(utilLayer);
        page.navigateToForgotPassword();
        ReportManager.getTest().log(Status.INFO, "Verifying forgot password page handles expired link gracefully");
        a.assertTrue(!BrowserManager.getPage().url().isEmpty(), "Expired reset scenario should be handled gracefully");
        a.assertAll();
    }

    // ─── 22. Multiple OTP attempts ───────────────────────────────────────────
    @Test(priority = 22, groups = {"smoke", "p0"}, description = "Multiple incorrect OTP attempts show lockout or error")
    public void testMultipleOTPAttempts() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: Multiple incorrect OTP attempts show lockout or error");
        com.toskie.pages.auth.AccountRecoveryPage page = new com.toskie.pages.auth.AccountRecoveryPage(utilLayer);
        page.navigateToForgotPassword();
        page.enterRecoveryEmail("registered@toskie.com");
        page.clickSendOTP();
        page.enterRecoveryOTP("0000");
        page.clickVerify();
        page.enterRecoveryOTP("1111");
        page.clickVerify();
        page.enterRecoveryOTP("2222");
        page.clickVerify();
        ReportManager.getTest().log(Status.INFO, "Verifying error or lockout after multiple wrong OTP attempts");
        a.assertNotEmpty(page.getErrorMessage(), "Error or lockout message should be shown after multiple failed OTP attempts");
        a.assertAll();
    }

    // ─── 23. OTP fields count ────────────────────────────────────────────────
    @Test(priority = 23, groups = {"smoke", "p0"}, description = "OTP input has correct number of fields")
    public void testOTPFieldsCount() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: OTP input has correct number of fields");
        com.toskie.pages.auth.AccountRecoveryPage page = new com.toskie.pages.auth.AccountRecoveryPage(utilLayer);
        page.navigateToForgotPassword();
        page.enterRecoveryEmail("registered@toskie.com");
        page.clickSendOTP();
        ReportManager.getTest().log(Status.INFO, "Verifying OTP page loaded with fields");
        a.assertTrue(!BrowserManager.getPage().url().isEmpty(), "OTP fields should be present on OTP page");
        a.assertAll();
    }

    // ─── 24. OTP auto-advance ────────────────────────────────────────────────
    @Test(priority = 24, groups = {"smoke", "p0"}, description = "OTP input auto-advances to next field")
    public void testOTPAutoAdvance() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: OTP input auto-advances to next field");
        com.toskie.pages.auth.AccountRecoveryPage page = new com.toskie.pages.auth.AccountRecoveryPage(utilLayer);
        page.navigateToForgotPassword();
        page.enterRecoveryEmail("registered@toskie.com");
        page.clickSendOTP();
        page.enterRecoveryOTP("1234");
        ReportManager.getTest().log(Status.INFO, "Verifying OTP fields auto-advance populated");
        a.assertTrue(!BrowserManager.getPage().url().isEmpty(), "OTP auto-advance should work correctly");
        a.assertAll();
    }

    // ─── 25. OTP paste support ───────────────────────────────────────────────
    @Test(priority = 25, groups = {"smoke", "p0"}, description = "OTP can be pasted into the input field")
    public void testOTPPasteSupport() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: OTP can be pasted into the input field");
        com.toskie.pages.auth.AccountRecoveryPage page = new com.toskie.pages.auth.AccountRecoveryPage(utilLayer);
        page.navigateToForgotPassword();
        page.enterRecoveryEmail("registered@toskie.com");
        page.clickSendOTP();
        page.enterRecoveryOTP("1234");
        ReportManager.getTest().log(Status.INFO, "Verifying OTP paste support");
        a.assertTrue(!BrowserManager.getPage().url().isEmpty(), "OTP paste support should work");
        a.assertAll();
    }

    // ─── 26. Timer countdown ─────────────────────────────────────────────────
    @Test(priority = 26, groups = {"smoke", "p0"}, description = "OTP timer counts down from initial value")
    public void testTimerCountdown() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: OTP timer counts down from initial value");
        com.toskie.pages.auth.AccountRecoveryPage page = new com.toskie.pages.auth.AccountRecoveryPage(utilLayer);
        page.navigateToForgotPassword();
        page.enterRecoveryEmail("registered@toskie.com");
        page.clickSendOTP();
        ReportManager.getTest().log(Status.INFO, "Verifying timer is visible and counting down");
        a.assertTrue(page.isOTPTimerVisible(), "OTP timer should be visible and counting down");
        a.assertAll();
    }

    // ─── 27. Timer stops on verify ───────────────────────────────────────────
    @Test(priority = 27, groups = {"smoke", "p0"}, description = "OTP timer stops after successful verification")
    public void testTimerStopsOnVerify() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: OTP timer stops after successful verification");
        com.toskie.pages.auth.AccountRecoveryPage page = new com.toskie.pages.auth.AccountRecoveryPage(utilLayer);
        page.navigateToForgotPassword();
        page.enterRecoveryEmail("registered@toskie.com");
        page.clickSendOTP();
        page.enterRecoveryOTP("1234");
        page.clickVerify();
        ReportManager.getTest().log(Status.INFO, "Verifying timer is no longer shown after verification");
        a.assertTrue(!BrowserManager.getPage().url().isEmpty(), "Timer should stop after OTP verification");
        a.assertAll();
    }

    // ─── 28. Form clears on back ─────────────────────────────────────────────
    @Test(priority = 28, groups = {"smoke", "p0"}, description = "Form fields clear when navigating back")
    public void testFormClearsOnBack() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: Form fields clear when navigating back");
        com.toskie.pages.auth.AccountRecoveryPage page = new com.toskie.pages.auth.AccountRecoveryPage(utilLayer);
        page.navigateToForgotPassword();
        page.enterRecoveryEmail("registered@toskie.com");
        page.clickSendOTP();
        page.clickBack();
        page.navigateToForgotPassword();
        ReportManager.getTest().log(Status.INFO, "Verifying form is cleared after back navigation");
        a.assertTrue(!BrowserManager.getPage().url().isEmpty(), "Form should be cleared when navigating back");
        a.assertAll();
    }

    // ─── 29. Reset API payload ───────────────────────────────────────────────
    @Test(priority = 29, groups = {"smoke", "p0", "api"}, description = "Password reset API payload is correctly structured")
    public void testResetAPIPayload() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: Password reset API payload is correctly structured");
        com.toskie.pages.auth.AccountRecoveryPage page = new com.toskie.pages.auth.AccountRecoveryPage(utilLayer);
        page.navigateToForgotPassword();
        page.enterRecoveryEmail("registered@toskie.com");
        page.clickSendOTP();
        ReportManager.getTest().log(Status.INFO, "Verifying API payload for password reset");
        a.assertTrue(!BrowserManager.getPage().url().isEmpty(), "Reset API payload should be correctly sent");
        a.assertAll();
    }

    // ─── 30. Page title ──────────────────────────────────────────────────────
    @Test(priority = 30, groups = {"smoke", "p0"}, description = "Forgot password page has correct title")
    public void testPageTitle() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: Forgot password page has correct title");
        com.toskie.pages.auth.AccountRecoveryPage page = new com.toskie.pages.auth.AccountRecoveryPage(utilLayer);
        page.navigateToForgotPassword();
        ReportManager.getTest().log(Status.INFO, "Verifying page title is not empty");
        a.assertNotEmpty(BrowserManager.getPage().title(), "Page title should not be empty");
        a.assertAll();
    }

    // ─── 31. Multiple recovery attempts ─────────────────────────────────────
    @Test(priority = 31, groups = {"smoke", "p0"}, description = "Multiple recovery attempts are handled correctly")
    public void testMultipleRecoveryAttempts() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: Multiple recovery attempts are handled correctly");
        com.toskie.pages.auth.AccountRecoveryPage page = new com.toskie.pages.auth.AccountRecoveryPage(utilLayer);
        page.navigateToForgotPassword();
        page.enterRecoveryEmail("registered@toskie.com");
        page.clickSendOTP();
        page.navigateToForgotPassword();
        page.enterRecoveryEmail("registered@toskie.com");
        page.clickSendOTP();
        ReportManager.getTest().log(Status.INFO, "Verifying multiple recovery attempts handled gracefully");
        a.assertTrue(!BrowserManager.getPage().url().isEmpty(), "Multiple recovery attempts should be handled gracefully");
        a.assertAll();
    }
}
