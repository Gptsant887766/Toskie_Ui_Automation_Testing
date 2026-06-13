package com.toskie.locators;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class LoginPageLocators {

    // ─── Entry points ──────────────────────────────────────────────────────────
    public final Locator loginButton;
    public final Locator signUpButton;

    // ─── Phone OTP flow ────────────────────────────────────────────────────────
    public final Locator phoneNumberInput;
    public final Locator countryCodeDropdown;
    public final Locator sendOtpButton;
    public final Locator otpField1;
    public final Locator otpField2;
    public final Locator otpField3;
    public final Locator otpField4;
    public final Locator otpAllFields;
    public final Locator verifyOtpButton;
    public final Locator resendOtpButton;
    public final Locator resendOtpTimer;
    public final Locator changeNumberLink;
    public final Locator otpExpiryMessage;

    // ─── Validation messages ───────────────────────────────────────────────────
    public final Locator phoneValidationError;
    public final Locator otpValidationError;
    public final Locator invalidOtpMessage;
    public final Locator tooManyAttemptsMessage;

    // ─── Page content ──────────────────────────────────────────────────────────
    public final Locator loginHeading;
    public final Locator loginSubHeading;
    public final Locator termsText;
    public final Locator privacyPolicyLink;
    public final Locator termsOfServiceLink;
    public final Locator backButton;

    public LoginPageLocators(Page page) {
        // Entry points — use span locator (original); forceClick in LoginPage bypasses footer interception
        loginButton         = page.locator("//span[text()='Login'] | //button[normalize-space(.)='Login'] | //a[normalize-space(.)='Login']").first();
        signUpButton        = page.locator("//span[contains(text(),'Sign Up')] | //button[contains(text(),'Register')]").first();

        // Phone OTP flow
        phoneNumberInput    = page.locator("input[type='tel'], input[placeholder*='phone' i], input[placeholder*='mobile' i], input[placeholder*='number' i]");
        countryCodeDropdown = page.locator("[class*='country-code'], [class*='phone-prefix'], select[name*='country']");
        sendOtpButton       = page.locator("//button[contains(.,'Send OTP')] | //button[contains(.,'Get OTP')] | //button[contains(.,'Send Code')] | //span[contains(text(),'Send OTP')]");
        otpField1           = page.locator("input[maxlength='1']").nth(0);
        otpField2           = page.locator("input[maxlength='1']").nth(1);
        otpField3           = page.locator("input[maxlength='1']").nth(2);
        otpField4           = page.locator("input[maxlength='1']").nth(3);
        otpAllFields        = page.locator("input[maxlength='1'], input[maxlength='4'], input[maxlength='6'], input[class*='otp'], input[name*='otp'], input[placeholder*='OTP' i], input[placeholder*='code' i]");
        verifyOtpButton     = page.locator("//button[contains(.,'Verify')] | //button[contains(.,'Confirm')] | //button[contains(.,'Submit OTP')] | //span[contains(text(),'Verify OTP')]");
        resendOtpButton     = page.locator("//button[contains(text(),'Resend')] | //span[contains(text(),'Resend OTP')]");
        resendOtpTimer      = page.locator("[class*='timer'], [class*='countdown'], [class*='resend-timer']");
        changeNumberLink    = page.locator("//a[contains(text(),'Change')] | //button[contains(text(),'Change Number')]");
        otpExpiryMessage    = page.locator("[class*='expiry'], [class*='expired']");

        // Validation messages — broad patterns to match various UI implementations
        phoneValidationError    = page.locator("[class*='error'], [class*='invalid'], [role='alert'], [class*='helper-text']:not(:empty)").first();
        otpValidationError      = page.locator("[class*='error'], [class*='invalid'], [role='alert']").first();
        invalidOtpMessage       = page.locator("//p[contains(text(),'Invalid')] | //span[contains(text(),'invalid')] | //p[contains(text(),'incorrect')] | //span[contains(text(),'wrong')] | [class*='error']:not(:empty)").first();
        tooManyAttemptsMessage  = page.locator("//p[contains(text(),'Too many')] | //span[contains(text(),'attempts')] | //p[contains(text(),'blocked')] | //p[contains(text(),'limit')]").first();

        // Page content
        loginHeading        = page.locator("h1:has-text('Login'), h2:has-text('Login'), [class*='heading']:has-text('Login')");
        loginSubHeading     = page.locator("p:has-text('OTP'), p:has-text('mobile'), p:has-text('phone')");
        termsText           = page.locator("[class*='terms'], p:has-text('Terms')");
        privacyPolicyLink   = page.locator("a:has-text('Privacy Policy')");
        termsOfServiceLink  = page.locator("a:has-text('Terms of Service'), a:has-text('Terms & Conditions')");
        backButton          = page.locator("button[aria-label='back'], [class*='back-btn'], svg[class*='arrow']").first();
    }
}
