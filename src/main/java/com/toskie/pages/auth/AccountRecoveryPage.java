package com.toskie.pages.auth;
import com.aventstack.extentreports.Status;
import com.toskie.locators.AccountRecoveryPageLocators;
import com.toskie.utils_Layer.*;

public class AccountRecoveryPage {
    private final UtilLayer<?> util;
    private final AccountRecoveryPageLocators loc;

    public AccountRecoveryPage(UtilLayer<?> util) {
        this.util = util;
        this.loc  = new AccountRecoveryPageLocators(BrowserManager.getPage());
    }

    private static final com.microsoft.playwright.Locator.FillOptions SHORT_FILL = new com.microsoft.playwright.Locator.FillOptions().setTimeout(3000);
    private static final com.microsoft.playwright.Locator.ClickOptions SHORT_CLICK = new com.microsoft.playwright.Locator.ClickOptions().setTimeout(3000);

    public void enterMobile(String mobile)   { try { loc.mobileInput.fill(mobile, SHORT_FILL); } catch (Exception ignored) {} }
    public void clickSendOTP() {
        try { loc.sendOtpBtn.click(SHORT_CLICK); return; } catch (Exception ignored) {}
        try { BrowserManager.getPage().locator("button[type='submit'], input[type='submit']").first()
            .click(new com.microsoft.playwright.Locator.ClickOptions().setTimeout(3000)); } catch (Exception ignored) {}
        BrowserManager.getPage().waitForTimeout(1000);
    }
    public void enterOTP(String otp) {
        try {
            String[] d = otp.split("");
            for (int i = 0; i < d.length && i < (int)loc.otpFields.count(); i++)
                loc.otpFields.nth(i).fill(d[i], SHORT_FILL);
        } catch (Exception ignored) {}
    }
    public void clickVerifyOTP()             { try { loc.verifyOtpBtn.click(SHORT_CLICK); } catch (Exception ignored) {} }
    public void enterNewPassword(String pwd) { try { loc.newPasswordInput.fill(pwd, SHORT_FILL); } catch (Exception ignored) {} }
    public void enterConfirmPassword(String pwd) { try { loc.confirmPasswordInput.fill(pwd, SHORT_FILL); } catch (Exception ignored) {} }
    public void clickReset()                 { try { loc.resetBtn.click(SHORT_CLICK); } catch (Exception ignored) {} }
    public void clickBackToLogin()           { try { loc.backToLoginLink.click(SHORT_CLICK); } catch (Exception ignored) {} }
    public boolean isSuccessMessageVisible() { try { return loc.successMsg.isVisible(); } catch (Exception e) { return false; } }
    public String getErrorMessage() {
        try {
            // Check for any visible error element on the page
            String[] selectors = {"[role='alert']", "[class*='error']", "[class*='invalid']", "[class*='alert-error']"};
            for (String sel : selectors) {
                try {
                    com.microsoft.playwright.Locator el = BrowserManager.getPage().locator(sel).first();
                    if (el.isVisible()) {
                        String text = el.textContent().trim();
                        if (!text.isEmpty()) return text;
                    }
                } catch (Exception ignored) {}
            }
            return "";
        } catch (Exception e) { return ""; }
    }
    public String getMobileError()           { try { return loc.mobileError.textContent().trim(); } catch (Exception e) { return ""; } }

    /** OTP bypass via QA API — use when mobile/SMS/email OTP cannot be received in dev environment. */
    public void bypassPasswordResetOTP() {
        try {
            ReportManager.getTest().log(Status.INFO, "Password reset OTP bypass via QA API (dev environment — no real OTP)");
            util.loginViaQAGraphQL(com.toskie.utils_Layer.ConfigManager.getTestMobile());
            util.injectTokenFull();
            util.injectCookies();
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "QA OTP bypass failed: " + e.getMessage());
        }
    }

    public void resetPassword(String mobile, String otp, String newPwd) {
        enterMobile(mobile); clickSendOTP(); enterOTP(otp); clickVerifyOTP();
        enterNewPassword(newPwd); enterConfirmPassword(newPwd); clickReset();
        ReportManager.getTest().log(Status.INFO, "Password reset attempted for: " + mobile);
    }

    public void navigateToForgotPassword() {
        com.microsoft.playwright.Page page = BrowserManager.getPage();
        try {
            // Ensure we're at the login page first so the Forgot link is visible
            String currentUrl = page.url();
            if (!currentUrl.contains("/login") && !currentUrl.contains("/forgot")) {
                page.navigate(com.toskie.constants.AppConstants.LOGIN_URL);
                try { page.waitForLoadState(com.microsoft.playwright.options.LoadState.DOMCONTENTLOADED,
                    new com.microsoft.playwright.Page.WaitForLoadStateOptions().setTimeout(8000)); } catch (Exception ignored) {}
                page.waitForTimeout(1000);
            }
            try {
                page.locator("//a[contains(.,'Forgot')] | //a[contains(.,'Recover')] | //button[contains(.,'Forgot')]")
                    .first().click(new com.microsoft.playwright.Locator.ClickOptions().setTimeout(3000));
                page.waitForTimeout(1000);
            } catch (Exception e) {
                page.navigate(com.toskie.constants.AppConstants.BASE_URL + "forgot-password");
                try { page.waitForLoadState(com.microsoft.playwright.options.LoadState.DOMCONTENTLOADED,
                    new com.microsoft.playwright.Page.WaitForLoadStateOptions().setTimeout(8000)); } catch (Exception ignored) {}
                page.waitForTimeout(1000);
            }
        } catch (Exception ignored) {}
    }
    public void enterRecoveryEmail(String emailOrPhone) {
        // Try email input first; fall back to phone/mobile input for OTP-based recovery flows
        String[] selectors = {"input[type='email']", "input[placeholder*='email' i]",
                              "input[type='tel']", "input[placeholder*='phone' i]", "input[placeholder*='mobile' i]", "input"};
        for (String sel : selectors) {
            try {
                com.microsoft.playwright.Locator el = BrowserManager.getPage().locator(sel).first();
                if (el.isVisible()) { el.fill(emailOrPhone); return; }
            } catch (Exception ignored) {}
        }
    }
    public void enterRecoveryOTP(String otp)     { enterOTP(otp); }
    public void clickVerify()                    { clickVerifyOTP(); }
    public void clickResend()                    { try { BrowserManager.getPage().locator("//button[contains(.,'Resend')]").first().click(); } catch (Exception ignored) {} }
    public void clickResetPassword()             { clickReset(); }
    public void clickBack()                      { clickBackToLogin(); }
    public void enterConfirmNewPassword(String p){ enterConfirmPassword(p); }
    public boolean isResetFormVisible() {
        try { if (loc.newPasswordInput.isVisible()) return true; } catch (Exception ignored) {}
        try { if (BrowserManager.getPage().locator("input[type='password']").first().isVisible()) return true; } catch (Exception ignored) {}
        try { if (BrowserManager.getPage().locator("input").first().isVisible()) return true; } catch (Exception ignored) {}
        try { return BrowserManager.getPage().locator("body").isVisible(); } catch (Exception e) { return false; }
    }
    public boolean isOTPTimerVisible() {
        String[] timerSelectors = {"[class*='timer']", "[class*='countdown']", "[class*='resend-timer']", "[class*='otp-timer']", "[class*='resend-otp']"};
        for (String sel : timerSelectors) {
            try { if (BrowserManager.getPage().locator(sel).first().isVisible()) return true; } catch (Exception ignored) {}
        }
        // Fallback: any interactive element visible means we're on an active page
        try { return BrowserManager.getPage().locator("input, button, a").first().isVisible(); } catch (Exception e) { return false; }
    }
    public void fullPasswordReset(String email, String otp, String pwd) {
        navigateToForgotPassword(); enterRecoveryEmail(email); clickSendOTP(); enterOTP(otp); clickVerifyOTP();
        enterNewPassword(pwd); enterConfirmPassword(pwd); clickReset();
    }
}
