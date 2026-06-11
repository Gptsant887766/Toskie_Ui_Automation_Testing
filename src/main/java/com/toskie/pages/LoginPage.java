package com.toskie.pages;

import com.aventstack.extentreports.Status;
import com.microsoft.playwright.options.LoadState;
import com.toskie.locators.LoginPageLocators;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.ConfigManager;
import com.toskie.utils_Layer.ReportManager;
import com.toskie.utils_Layer.UtilLayer;
import com.toskie.utils_Layer.WaitManager;

public class LoginPage {

    private final UtilLayer<?> util;
    private final LoginPageLocators loc;

    public LoginPage(UtilLayer<?> util) {
        this.util = util;
        this.loc  = new LoginPageLocators(BrowserManager.getPage());
    }

    // ─── QA fast-login (API bypass — no UI OTP) ──────────────────────────────

    public void loginWithQABypass(String mobile) {
        ReportManager.getTest().log(Status.INFO, "QA Bypass Login for mobile: " + mobile);
        // Use DOMCONTENTLOADED — NETWORKIDLE never fires when the app has active WebSocket/polling connections
        try {
            BrowserManager.getPage().waitForLoadState(LoadState.DOMCONTENTLOADED,
                    new com.microsoft.playwright.Page.WaitForLoadStateOptions().setTimeout(10000));
        } catch (Exception ignored) {}
        BrowserManager.getPage().waitForTimeout(2000);

        // Login button is optional — app may navigate past the landing page automatically after onboarding
        try {
            if (loc.loginButton.isVisible(new com.microsoft.playwright.Locator.IsVisibleOptions().setTimeout(3000))) {
                util.forceClick(loc.loginButton, "Login Button");
                BrowserManager.getPage().waitForTimeout(1500);
            }
        } catch (Exception e) {
            ReportManager.getTest().log(Status.INFO, "Login button not visible — proceeding with QA API bypass directly");
        }

        util.loginViaQAGraphQL(mobile);
        util.injectTokenFull();
        util.injectCookies();

        try {
            BrowserManager.getPage().waitForLoadState(LoadState.DOMCONTENTLOADED,
                    new com.microsoft.playwright.Page.WaitForLoadStateOptions().setTimeout(10000));
        } catch (Exception ignored) {}
        BrowserManager.getPage().waitForTimeout(4000);
        ReportManager.getTest().log(Status.PASS, "QA bypass login successful: " + mobile);
    }

    public void loginWithDefaultCredentials() {
        loginWithQABypass(System.getProperty("testMobile", ConfigManager.getTestMobile()));
    }

    // ─── UI-based OTP login (real flow) ───────────────────────────────────────

    public void clickLoginButton() {
        util.forceClick(loc.loginButton, "Login Button");
        BrowserManager.getPage().waitForTimeout(1000);
    }

    public void enterPhoneNumber(String phone) {
        util.fill(loc.phoneNumberInput, phone, "Phone Number Input");
    }

    public void clickSendOTP() {
        util.click(loc.sendOtpButton, "Send OTP Button");
        BrowserManager.getPage().waitForTimeout(2000);
    }

    public void enterOTP(String otp) {
        if (otp.length() == 4) {
            // Split into individual fields
            String[] digits = otp.split("");
            try {
                util.fill(loc.otpField1, digits[0], "OTP Field 1");
                util.fill(loc.otpField2, digits[1], "OTP Field 2");
                util.fill(loc.otpField3, digits[2], "OTP Field 3");
                util.fill(loc.otpField4, digits[3], "OTP Field 4");
            } catch (Exception e) {
                // Try single combined field
                util.fill(loc.otpAllFields.first(), otp, "OTP Combined Field");
            }
        } else {
            util.fill(loc.otpAllFields.first(), otp, "OTP Field");
        }
        ReportManager.getTest().log(Status.INFO, "OTP entered.");
    }

    public void clickVerifyOTP() {
        util.click(loc.verifyOtpButton, "Verify OTP Button");
        WaitManager.safePageLoad();
        BrowserManager.getPage().waitForTimeout(2000);
    }

    public void clickResendOTP() {
        util.click(loc.resendOtpButton, "Resend OTP");
        BrowserManager.getPage().waitForTimeout(2000);
    }

    public void loginWithOTPFlow(String mobile, String otp) {
        clickLoginButton();
        enterPhoneNumber(mobile);
        clickSendOTP();
        enterOTP(otp);
        clickVerifyOTP();
    }

    // ─── Verification helpers ────────────────────────────────────────────────

    public boolean isLoginButtonVisible() {
        try { return loc.loginButton.isVisible(); }
        catch (Exception e) { return false; }
    }

    public boolean isOTPScreenVisible() {
        try { return loc.otpAllFields.first().isVisible(); }
        catch (Exception e) { return false; }
    }

    public boolean isPhoneValidationErrorVisible() {
        try { return loc.phoneValidationError.isVisible(); }
        catch (Exception e) { return false; }
    }

    public boolean isOTPValidationErrorVisible() {
        try { return loc.otpValidationError.isVisible(); }
        catch (Exception e) { return false; }
    }

    public boolean isInvalidOTPMessageVisible() {
        try { return loc.invalidOtpMessage.isVisible(); }
        catch (Exception e) { return false; }
    }

    public boolean isTooManyAttemptsVisible() {
        try { return loc.tooManyAttemptsMessage.isVisible(); }
        catch (Exception e) { return false; }
    }

    public boolean isResendTimerVisible() {
        try { return loc.resendOtpTimer.isVisible(); }
        catch (Exception e) { return false; }
    }

    public String getPhoneValidationErrorText() {
        try { return loc.phoneValidationError.textContent().trim(); }
        catch (Exception e) { return ""; }
    }

    public String getOTPValidationErrorText() {
        try { return loc.otpValidationError.textContent().trim(); }
        catch (Exception e) { return ""; }
    }

    public void verifyLoginPageElements() {
        util.verifyElementVisible(loc.loginButton, "Login Button");
        ReportManager.getTest().log(Status.PASS, "Login page elements verified.");
    }
}
