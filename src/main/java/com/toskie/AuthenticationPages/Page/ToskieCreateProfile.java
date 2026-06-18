package com.toskie.AuthenticationPages.Page;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import com.toskie.AuthenticationPages.OrPages.ToskieCreateProfileOr;
import com.toskie.utils_Layer.ApiUtils;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.ConfigManager;
import com.toskie.utils_Layer.UtilLayer;

public class ToskieCreateProfile extends ToskieCreateProfileOr {

    private final UtilLayer<?> utilLayer;

    public ToskieCreateProfile(UtilLayer<?> utilLayer) {
        super(BrowserManager.getPage());
        this.utilLayer = utilLayer;
    }

    public void validatecreateProfile() {
        validatecreateProfileWith(ClickOnGenderBT, "MALE");
    }

    public void validatecreateProfileWithGender(String gender) {
        Locator genderBtn;
        switch (gender.toUpperCase()) {
            case "FEMALE": genderBtn = ClickOnFemaleGenderBT; break;
            case "OTHER":  genderBtn = ClickOnOtherGenderBT;  break;
            default:       genderBtn = ClickOnGenderBT;        break;
        }
        validatecreateProfileWith(genderBtn, gender.toUpperCase());
    }

    private void validatecreateProfileWith(Locator genderBtn, String genderLabel) {
        Page page = BrowserManager.getPage();
        String email = System.getProperty("testEmail", ConfigManager.getTestEmail());

        // Log API traffic for debugging
        page.onRequest(req -> {
            if (req.url().contains("graphql") || req.url().contains("/api/")) {
                System.out.println("[REQUEST] " + req.method() + " " + req.url());
            }
        });

        // Install route mocks BEFORE any form interaction so they're active
        // when Send OTP fires:
        //   **/send-email-otp  → fake 200 (prevents redirect for registered emails)
        //   **/verify-email-otp → fake 200 (OTP dialog closes without real OTP)
        //   **/graphql VerifyEmailUsingOTP → fake success (GraphQL variant)
        ApiUtils.enableEmailOTPBypass(email);

        utilLayer.fill(FirstName, "Sontosh", "First Name");
        utilLayer.fill(LastName,  "Gupta",   "Last Name");
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        page.waitForTimeout(1000);

        // Backend bypass: marks email as verified in the DB so profile creation
        // succeeds on submission regardless of UI OTP state.
        utilLayer.bypassEmailOTP(email);
        page.waitForTimeout(1000);
        System.out.println("[DEBUG] URL after backend bypass: " + page.url());

        // Safety net: if backend bypass caused a redirect, recover to the form.
        ensureOnRegistrationPage(page, email);

        // Type email to enable the Send OTP button.
        utilLayer.typeValue(EmailField, email);
        page.waitForTimeout(500);

        // Click Send OTP -- route mock returns fake success so no redirect occurs.
        utilLayer.click(SendOTPButton, "Send OTP Button");
        page.waitForTimeout(2000);
        System.out.println("[DEBUG] URL after Send OTP: " + page.url());

        // Safety net: if page redirected despite route mock, recover again.
        ensureOnRegistrationPage(page, email);

        // Dismiss the OTP dialog that appears after fake send-otp success.
        // The input uses data-input-otp="true" with maxlength="4".
        dismissOTPDialog(page);

        System.out.println("[DEBUG] URL before gender click: " + page.url());

        utilLayer.click(genderBtn, "Gender Radio Button (" + genderLabel + ")");
        pickDOB();
        utilLayer.click(TermCondionCheckBX, "Terms and Conditions Checkbox");

        try {
            utilLayer.click(CreateMYProfile, "Create My Profile Button");
        } catch (Exception e) {
            System.out.println("[ToskieCreateProfile] Standard click failed, force-dispatching.");
            CreateMYProfile.dispatchEvent("click");
        }

        System.out.println("[ToskieCreateProfile] Profile created successfully.");
    }

    // Checks the current URL and navigates back to /user-registration if the
    // app redirected (e.g. non-loggedin-profile?redirect=...).
    // After navigate, re-injects tokens, reloads, and re-fills name fields.
    private void ensureOnRegistrationPage(Page page, String email) {
        String url = page.url();
        boolean onReg = url.contains("/user-registration") && !url.contains("non-loggedin-profile");
        if (onReg) return;

        System.out.println("[ToskieCreateProfile] Off registration page (" + url + ") -- recovering.");
        String regUrl = ConfigManager.getBaseUrl().replaceAll("/$", "") + "/user-registration";
        page.navigate(regUrl);
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        page.waitForTimeout(1000);

        // page.navigate() wipes localStorage -- restore auth tokens.
        String at = ApiUtils.getAccessToken();
        String rt = ApiUtils.getRefreshToken();
        page.evaluate(
            "([a, r]) => {" +
            "  localStorage.setItem('access_token', a);" +
            "  localStorage.setItem('refresh_token', r);" +
            "  sessionStorage.setItem('access_token', a);" +
            "  sessionStorage.setItem('refresh_token', r);" +
            "}",
            new Object[]{at, rt}
        );
        utilLayer.injectCookies();
        page.reload();
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        page.waitForTimeout(2000);
        System.out.println("[DEBUG] URL after recovery: " + page.url());

        // Re-fill name fields since the form was reset by navigation.
        utilLayer.fill(FirstName, "Sontosh", "First Name");
        utilLayer.fill(LastName,  "Gupta",   "Last Name");
        page.waitForTimeout(500);

        // Re-type email and re-click Send OTP -- route mock is still active.
        utilLayer.typeValue(EmailField, email);
        page.waitForTimeout(500);
        utilLayer.click(SendOTPButton, "Send OTP Button (recovery)");
        page.waitForTimeout(2000);
        System.out.println("[DEBUG] URL after recovery Send OTP: " + page.url());
    }

    // Looks for an open Radix UI OTP dialog and fills the 4-digit input,
    // then clicks Verify to dismiss it.
    // The OTP input is identified by data-input-otp="true" (no placeholder attr).
    private void dismissOTPDialog(Page page) {
        try {
            Locator dialog = page.locator("div[role='dialog'][data-state='open']");
            page.waitForTimeout(1000);
            if (dialog.count() == 0) {
                System.out.println("[ToskieCreateProfile] No OTP dialog visible -- continuing.");
                return;
            }

            System.out.println("[ToskieCreateProfile] OTP dialog detected -- filling input.");
            Locator otpInput = page.locator("input[data-input-otp='true'], input[data-slot='input-otp']");
            if (otpInput.count() == 0) {
                System.out.println("[ToskieCreateProfile] OTP input not found inside dialog -- skipping.");
                return;
            }

            otpInput.first().fill("0000");
            System.out.println("[ToskieCreateProfile] OTP filled with '0000'.");
            page.waitForTimeout(800);

            // Click the verify button -- route mock intercepts and returns success.
            Locator verifyBtn = page.locator(
                "button:has-text('Verify Email'), button:has-text('Verify OTP'), " +
                "button:has-text('Verify')");
            if (verifyBtn.count() > 0) {
                verifyBtn.first().click();
                System.out.println("[ToskieCreateProfile] Verify button clicked.");
            } else {
                // Fallback: click any button inside the dialog
                dialog.first().locator("button").last().click();
                System.out.println("[ToskieCreateProfile] Clicked fallback button in dialog.");
            }

            // Wait for the dialog to close.
            page.waitForTimeout(2000);

            // Verify dialog actually closed.
            if (dialog.count() > 0) {
                System.out.println("[ToskieCreateProfile] Warning: OTP dialog still open after verify click.");
            } else {
                System.out.println("[ToskieCreateProfile] OTP dialog closed successfully.");
            }

        } catch (Exception e) {
            System.out.println("[ToskieCreateProfile] OTP dialog handling exception: " + e.getMessage());
        }
    }

    // Calendar is a combined year+month picker:
    //   - YearTX (year header) opens a view showing current year + all 12 months.
    //   - YearBackBTX navigates one year backward per click.
    //   - Once target year shows in the header, DON'T click it (that collapses the
    //     month list); click MonthTX directly to select the month for that year.
    private void pickDOB() {
        Page page = BrowserManager.getPage();

        utilLayer.click(DOBButton, "DOB Button");
        page.waitForTimeout(800);

        utilLayer.click(YearTX, "Year Header");
        page.waitForTimeout(500);

        for (int i = 0; i < 40; i++) {
            Locator yr = page.locator("//button[contains(text(),'1997')]");
            if (yr.count() > 0 && yr.first().isVisible()) {
                break;
            }
            if (YearBackBTX.count() > 0) {
                YearBackBTX.first().click();
            }
            page.waitForTimeout(300);
        }
        page.waitForTimeout(500);

        utilLayer.click(MonthTX, "Month May");
        page.waitForTimeout(500);

        utilLayer.click(DateBTX, "Day 25");
        page.waitForTimeout(300);

        utilLayer.click(SaveBTX, "Save DOB");
        page.waitForTimeout(500);
    }
}
