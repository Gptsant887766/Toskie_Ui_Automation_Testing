package com.toskie.locators;
import com.microsoft.playwright.*;

public class RegistrationPageLocators {
    public final Locator mobileInput, sendOtpBtn, otpFields, verifyOtpBtn, fullNameInput,
            emailInput, passwordInput, confirmPasswordInput, registerBtn, loginLink,
            termsCheckbox, mobileError, emailError, passwordError, nameError;

    public RegistrationPageLocators(Page page) {
        mobileInput         = page.locator("input[placeholder*='Mobile' i], input[name*='phone' i], input[name*='mobile' i]").first();
        sendOtpBtn          = page.locator("//button[contains(.,'Send OTP')] | //button[contains(.,'Get OTP')]").first();
        otpFields           = page.locator("input[maxlength='1'], input[class*='otp']");
        verifyOtpBtn        = page.locator("//button[contains(.,'Verify')] | //button[contains(.,'Confirm OTP')]").first();
        fullNameInput       = page.locator("input[placeholder*='Name' i], input[name='name'], input[name='fullName']").first();
        emailInput          = page.locator("input[type='email'], input[name='email'], input[placeholder*='email' i]").first();
        passwordInput       = page.locator("input[type='password'][name*='pass' i], input[placeholder*='password' i]").first();
        confirmPasswordInput= page.locator("input[name*='confirm' i], input[placeholder*='confirm' i]").first();
        registerBtn         = page.locator("//button[normalize-space()='Register'] | //button[contains(.,'Create Account')]").first();
        loginLink           = page.locator("//a[contains(.,'Login')] | //a[contains(.,'Sign In')]").first();
        termsCheckbox       = page.locator("input[type='checkbox'][name*='terms' i], input[type='checkbox'][name*='agree' i]").first();
        mobileError         = page.locator("[class*='error']:near(input[name*='mobile' i])").first();
        emailError          = page.locator("[class*='error']:near(input[type='email'])").first();
        passwordError       = page.locator("[class*='error']:near(input[type='password'])").first();
        nameError           = page.locator("[class*='error']:near(input[name='name'])").first();
    }
}
