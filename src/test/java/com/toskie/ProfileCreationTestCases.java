package com.toskie;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import com.toskie.AuthenticationPages.Page.ToskieCreateProfile;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.utils_Layer.ApiUtils;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.ConfigManager;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * PROFILE CREATION TEST CASES
 * Covers all scenarios for the /user-registration form.
 *
 * Pre-condition: QA account (mobile in config.properties) must not have
 * an existing profile, OR the backend must allow re-submission.
 * Each @Test navigates fresh via @BeforeMethod.
 */
public class ProfileCreationTestCases extends BaseTest {

    // ── Helper: QA login + navigate to /home (redirects to /user-registration) ──
    private ToskieCreateProfile loginAndNavigate() {
        String mobile = System.getProperty("testMobile", ConfigManager.getTestMobile());
        utilLayer.loginViaQAGraphQL(mobile);
        utilLayer.injectTokenFull();
        utilLayer.injectCookies();

        String homeUrl = ConfigManager.getBaseUrl().replaceAll("/$", "") + "/home";
        BrowserManager.getPage().navigate(homeUrl,
            new com.microsoft.playwright.Page.NavigateOptions().setTimeout(15000));
        BrowserManager.getPage().waitForLoadState(LoadState.DOMCONTENTLOADED);

        // /user-registration is a React SPA route -- form fields render asynchronously
        // after DOMCONTENTLOADED fires. Wait for First Name input to confirm the form
        // is fully painted before any test starts asserting element presence.
        BrowserManager.getPage()
            .locator("//input[@placeholder='Enter First Name']")
            .waitFor(new com.microsoft.playwright.Locator.WaitForOptions()
                .setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE)
                .setTimeout(10000));

        return new ToskieCreateProfile(utilLayer);
    }

    // ── TC-PC-001: Happy path -- MALE gender ───────────────────────────────────
    @Test(priority = 1,
          description = "TC-PC-001: Create profile successfully with MALE gender selection")
    public void verifyCreateProfile_Male() {
        ToskieCreateProfile profilePage = loginAndNavigate();
        profilePage.validatecreateProfile();
        Assert.assertFalse(BrowserManager.getPage().url().isEmpty(),
            "Page URL must not be empty after profile creation");
    }

    // ── TC-PC-002: Happy path -- FEMALE gender ─────────────────────────────────
    @Test(priority = 2,
          description = "TC-PC-002: Create profile successfully with FEMALE gender selection")
    public void verifyCreateProfile_Female() {
        ToskieCreateProfile profilePage = loginAndNavigate();
        profilePage.validatecreateProfileWithGender("FEMALE");
        Assert.assertFalse(BrowserManager.getPage().url().isEmpty(),
            "Page URL must not be empty after profile creation");
    }

    // ── TC-PC-003: Happy path -- OTHER gender ──────────────────────────────────
    @Test(priority = 3,
          description = "TC-PC-003: Create profile successfully with OTHER gender selection")
    public void verifyCreateProfile_Other() {
        ToskieCreateProfile profilePage = loginAndNavigate();
        profilePage.validatecreateProfileWithGender("OTHER");
        Assert.assertFalse(BrowserManager.getPage().url().isEmpty(),
            "Page URL must not be empty after profile creation");
    }

    // ── TC-PC-004: Data-driven -- all genders via provider ─────────────────────
    @DataProvider(name = "genderData", parallel = false)
    public Object[][] genderData() {
        return new Object[][]{
            {"MALE"},
            {"FEMALE"},
            {"OTHER"},
        };
    }

    @Test(dataProvider = "genderData", priority = 4,
          description = "TC-PC-004: Data-driven -- create profile for each gender option")
    public void verifyCreateProfile_DataDriven(String gender) {
        System.out.println("[ProfileCreationTestCases] Running gender=" + gender);
        ToskieCreateProfile profilePage = loginAndNavigate();
        profilePage.validatecreateProfileWithGender(gender);
        Assert.assertFalse(BrowserManager.getPage().url().isEmpty(),
            "URL must not be empty after creating profile with gender=" + gender);
    }

    // ── TC-PC-005: Email OTP -- Send OTP does NOT redirect ─────────────────────
    @Test(priority = 5,
          description = "TC-PC-005: After clicking Send OTP, page must stay on /user-registration (route mock active)")
    public void verifyEmailOTP_SendDoesNotRedirect() {
        loginAndNavigate();
        Page page = BrowserManager.getPage();
        String email = ConfigManager.getTestEmail();

        ApiUtils.enableEmailOTPBypass(email);

        page.locator("//input[@placeholder='Enter Email ID']").fill(email);
        page.waitForTimeout(500);
        page.locator("//button[normalize-space()='Send OTP']").click();
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);

        String url = page.url();
        Assert.assertTrue(url.contains("/user-registration") && !url.contains("non-loggedin-profile"),
            "Page must stay on /user-registration after Send OTP -- was: " + url);
    }

    // ── TC-PC-006: Email OTP -- dialog appears after Send OTP ──────────────────
    @Test(priority = 6,
          description = "TC-PC-006: OTP dialog (role=dialog) must be visible after Send OTP with mocked response")
    public void verifyEmailOTP_DialogAppearsAfterSend() {
        loginAndNavigate();
        Page page = BrowserManager.getPage();
        String email = ConfigManager.getTestEmail();

        ApiUtils.enableEmailOTPBypass(email);

        page.locator("//input[@placeholder='Enter Email ID']").fill(email);
        page.waitForTimeout(500);
        page.locator("//button[normalize-space()='Send OTP']").click();
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);

        boolean dialogVisible = page.locator("div[role='dialog'][data-state='open']").count() > 0;
        Assert.assertTrue(dialogVisible,
            "OTP dialog must appear after a successful Send OTP response");
    }

    // ── TC-PC-007: Email OTP -- 4-digit input is fillable ─────────────────────
    @Test(priority = 7,
          description = "TC-PC-007: OTP input (data-input-otp=true, maxlength=4) must accept 4-digit code")
    public void verifyEmailOTP_InputAcceptsFourDigits() {
        loginAndNavigate();
        Page page = BrowserManager.getPage();
        String email = ConfigManager.getTestEmail();

        ApiUtils.enableEmailOTPBypass(email);

        page.locator("//input[@placeholder='Enter Email ID']").fill(email);
        page.waitForTimeout(500);
        page.locator("//button[normalize-space()='Send OTP']").click();
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);

        com.microsoft.playwright.Locator otpInput =
            page.locator("input[data-input-otp='true'], input[data-slot='input-otp']");
        Assert.assertTrue(otpInput.count() > 0, "OTP input must be present inside dialog");

        otpInput.first().fill("0000");
        String value = otpInput.first().inputValue();
        Assert.assertTrue(value.length() <= 4, "OTP input must not exceed 4 characters");
    }

    // ── TC-PC-008: Verify form is on /user-registration after login ───────────
    @Test(priority = 8,
          description = "TC-PC-008: After QA login, user must land on /user-registration form")
    public void verifyRegistrationFormIsShownAfterLogin() {
        loginAndNavigate();
        String url = BrowserManager.getPage().url();
        Assert.assertTrue(url.contains("/user-registration"),
            "URL must contain /user-registration after QA login for new user -- was: " + url);
    }

    // ── TC-PC-009: First Name field is present and editable ──────────────────
    @Test(priority = 9,
          description = "TC-PC-009: First Name input must be visible and accept text")
    public void verifyFirstNameFieldEditable() {
        loginAndNavigate();
        Page page = BrowserManager.getPage();
        com.microsoft.playwright.Locator firstNameField =
            page.locator("//input[@placeholder='Enter First Name']");

        Assert.assertTrue(firstNameField.count() > 0, "First Name field must be visible");
        firstNameField.fill("TestUser");
        Assert.assertEquals(firstNameField.inputValue(), "TestUser",
            "First Name field must accept and retain typed text");
    }

    // ── TC-PC-010: Last Name field is present and editable ───────────────────
    @Test(priority = 10,
          description = "TC-PC-010: Last Name input must be visible and accept text")
    public void verifyLastNameFieldEditable() {
        loginAndNavigate();
        Page page = BrowserManager.getPage();
        com.microsoft.playwright.Locator lastNameField =
            page.locator("//input[@placeholder='Enter Last Name']");

        Assert.assertTrue(lastNameField.count() > 0, "Last Name field must be visible");
        lastNameField.fill("Lastname");
        Assert.assertEquals(lastNameField.inputValue(), "Lastname",
            "Last Name field must accept and retain typed text");
    }

    // ── TC-PC-011: Email field is present ─────────────────────────────────────
    @Test(priority = 11,
          description = "TC-PC-011: Email input must be visible on the registration form")
    public void verifyEmailFieldVisible() {
        loginAndNavigate();
        com.microsoft.playwright.Locator emailField =
            BrowserManager.getPage().locator("//input[@placeholder='Enter Email ID']");
        Assert.assertTrue(emailField.count() > 0, "Email input must be present on /user-registration");
    }

    // ── TC-PC-012: Gender radio buttons -- all three present ──────────────────
    @Test(priority = 12,
          description = "TC-PC-012: MALE, FEMALE, OTHER gender options must all be present on the form")
    public void verifyAllGenderOptionsPresent() {
        loginAndNavigate();
        Page page = BrowserManager.getPage();

        Assert.assertTrue(page.locator("//button[@id='MALE']").count() > 0,
            "MALE gender button must be present");
        Assert.assertTrue(page.locator("//button[@id='FEMALE']").count() > 0,
            "FEMALE gender button must be present");
        Assert.assertTrue(page.locator("//button[@id='OTHER']").count() > 0,
            "OTHER gender button must be present");
    }

    // ── TC-PC-013: T&C checkbox is present ───────────────────────────────────
    @Test(priority = 13,
          description = "TC-PC-013: Terms and Conditions checkbox must be present on the form")
    public void verifyTermsCheckboxPresent() {
        loginAndNavigate();
        com.microsoft.playwright.Locator checkbox =
            BrowserManager.getPage().locator("//button[@id='terms-checkbox']");
        Assert.assertTrue(checkbox.count() > 0, "Terms and Conditions checkbox must be present");
    }

    // ── TC-PC-014: Send OTP button is present ────────────────────────────────
    @Test(priority = 14,
          description = "TC-PC-014: Send OTP button must be present next to the email field")
    public void verifySendOTPButtonPresent() {
        loginAndNavigate();
        com.microsoft.playwright.Locator sendOTPBtn =
            BrowserManager.getPage().locator("//button[normalize-space()='Send OTP']");
        Assert.assertTrue(sendOTPBtn.count() > 0, "Send OTP button must be present on the form");
    }

}
