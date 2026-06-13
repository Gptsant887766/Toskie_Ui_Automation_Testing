package com.toskie.locators;
import com.microsoft.playwright.*;

public class AccountRecoveryPageLocators {
    public final Locator mobileInput, sendOtpBtn, otpFields, verifyOtpBtn, newPasswordInput,
            confirmPasswordInput, resetBtn, backToLoginLink, mobileError, successMsg, errorMsg;

    public AccountRecoveryPageLocators(Page page) {
        mobileInput         = page.locator("input[placeholder*='Mobile' i], input[name*='phone' i]").first();
        sendOtpBtn          = page.locator("//button[contains(.,'Send OTP')] | //button[contains(.,'Get OTP')] | //button[contains(.,'Send Code')] | //button[contains(.,'Send')]").first();
        otpFields           = page.locator("input[maxlength='1'], input[class*='otp']");
        verifyOtpBtn        = page.locator("//button[contains(.,'Verify')]").first();
        newPasswordInput    = page.locator("input[name*='newPass' i], input[placeholder*='New Password' i]").first();
        confirmPasswordInput= page.locator("input[name*='confirm' i], input[placeholder*='Confirm' i]").first();
        resetBtn            = page.locator("//button[contains(.,'Reset')] | //button[contains(.,'Update Password')]").first();
        backToLoginLink     = page.locator("//a[contains(.,'Back')] | //a[contains(.,'Login')]").first();
        mobileError         = page.locator("[class*='error']:near(input[name*='phone' i])").first();
        successMsg          = page.locator("[class*='success'], [class*='alert-success'], [role='status']").first();
        errorMsg            = page.locator("[class*='error'], [class*='alert-error'], [class*='invalid'], [role='alert']").first();
    }
}
