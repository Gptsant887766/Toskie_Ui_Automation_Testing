package com.toskie.components;
import com.aventstack.extentreports.Status;
import com.microsoft.playwright.*;
import com.toskie.utils_Layer.*;

public class OTPInputComponent {
    private final UtilLayer<?> util;
    private final Page page;
    private final Locator otpFields, resendBtn, verifyBtn, timer, errorMsg;

    public OTPInputComponent(UtilLayer<?> util) {
        this.util = util;
        this.page = BrowserManager.getPage();
        otpFields = page.locator("input[maxlength='1'], input[class*='otp']");
        resendBtn = page.locator("//button[contains(.,'Resend')] | //span[contains(.,'Resend')]").first();
        verifyBtn = page.locator("//button[contains(.,'Verify')] | //button[contains(.,'Submit')]").first();
        timer     = page.locator("[class*='timer'], [class*='countdown']").first();
        errorMsg  = page.locator("[class*='error']:has-text('OTP'), p[class*='error']").first();
    }

    public void enterOTP(String otp) {
        String[] digits = otp.split("");
        for (int i = 0; i < digits.length && i < otpFields.count(); i++) {
            otpFields.nth(i).fill(digits[i]);
        }
        ReportManager.getTest().log(Status.INFO, "OTP entered: " + otp);
    }
    public void clearOTP() { for (int i = 0; i < otpFields.count(); i++) otpFields.nth(i).fill(""); }
    public boolean isOTPFieldVisible() { try { return otpFields.first().isVisible(); } catch (Exception e) { return false; } }
    public void clickResend() { util.click(resendBtn, "Resend OTP"); }
    public void clickVerify() { util.click(verifyBtn, "Verify OTP"); }
    public boolean isErrorVisible() { try { return errorMsg.isVisible(); } catch (Exception e) { return false; } }
    public String getErrorText() { try { return errorMsg.textContent().trim(); } catch (Exception e) { return ""; } }
}
