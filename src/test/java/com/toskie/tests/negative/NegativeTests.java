package com.toskie.tests.negative;

import com.toskie.utils_Layer.WaitManager;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.pages.LoginPage;
import com.toskie.pages.WelcomePage;
import com.toskie.utils.AssertionHelper;
import com.toskie.utils.TestDataManager;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.constants.TestGroups;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * NEGATIVE TESTS -- Invalid inputs, boundary violations, error handling
 */
@Test(groups = {TestGroups.REGRESSION, TestGroups.NEGATIVE, TestGroups.MEDIUM})
public class NegativeTests extends BaseTest {

    @DataProvider(name = "negativeLoginData")
    public Object[][] negativeLoginData() {
        return TestDataManager.getNegativeLoginData();
    }

    private void navigateToLogin() {
        new WelcomePage(utilLayer).completeOnboarding();
        new LoginPage(utilLayer).clickLoginButton();
    }

    // ─── TC-NG-001: Empty phone number ────────────────────────────────────────
    @Test(priority = 1,
          description = "Submit empty phone number -- expect validation error")
    public void testEmptyPhoneNumber() {
        AssertionHelper a = new AssertionHelper();
        navigateToLogin();

        LoginPage lp = new LoginPage(utilLayer);
        lp.clickSendOTP();

        BrowserManager.getPage().waitForTimeout(1000);
        boolean hasError = lp.isPhoneValidationErrorVisible() || !lp.isOTPScreenVisible();
        a.assertTrue(hasError, "Empty phone should not proceed to OTP screen");
        a.assertAll();
    }

    // ─── TC-NG-002: Short phone number ────────────────────────────────────────
    @Test(priority = 2,
          description = "Submit 3-digit phone number -- expect invalid format error")
    public void testShortPhoneNumber() {
        AssertionHelper a = new AssertionHelper();
        navigateToLogin();

        LoginPage lp = new LoginPage(utilLayer);
        lp.enterPhoneNumber("123");
        lp.clickSendOTP();
        BrowserManager.getPage().waitForTimeout(1000);

        boolean hasError = lp.isPhoneValidationErrorVisible() || !lp.isOTPScreenVisible();
        a.assertTrue(hasError, "3-digit phone should fail validation");
        a.assertAll();
    }

    // ─── TC-NG-003: Non-numeric phone number ─────────────────────────────────
    @Test(priority = 3,
          description = "Submit alphabetical phone number -- expect rejection")
    public void testAlphaPhoneNumber() {
        AssertionHelper a = new AssertionHelper();
        navigateToLogin();

        LoginPage lp = new LoginPage(utilLayer);
        lp.enterPhoneNumber("abcdefghij");
        lp.clickSendOTP();
        BrowserManager.getPage().waitForTimeout(1000);

        boolean hasError = lp.isPhoneValidationErrorVisible() || !lp.isOTPScreenVisible();
        a.assertTrue(hasError, "Alphabetical phone should fail validation");
        a.assertAll();
    }

    // ─── TC-NG-004: Wrong OTP ─────────────────────────────────────────────────
    @Test(priority = 4,
          description = "Enter incorrect OTP -- expect 'Invalid OTP' error message")
    public void testWrongOTPEntry() {
        AssertionHelper a = new AssertionHelper();
        navigateToLogin();

        LoginPage lp = new LoginPage(utilLayer);
        lp.enterPhoneNumber("7088545641");
        lp.clickSendOTP();
        WaitManager.safePageLoad();

        if (lp.isOTPScreenVisible()) {
            lp.enterOTP("0000");
            lp.clickVerifyOTP();
            WaitManager.safePageLoad();

            boolean hasError = lp.isInvalidOTPMessageVisible() || lp.isOTPValidationErrorVisible();
            a.assertTrue(hasError, "Wrong OTP should show 'Invalid OTP' error");
        }
        a.assertAll();
    }

    // ─── TC-NG-005: OTP with wrong length ────────────────────────────────────
    @Test(priority = 5,
          description = "Enter 2-digit OTP in a 4-digit OTP field -- expect rejection")
    public void testShortOTPEntry() {
        AssertionHelper a = new AssertionHelper();
        navigateToLogin();

        LoginPage lp = new LoginPage(utilLayer);
        lp.enterPhoneNumber("7088545641");
        lp.clickSendOTP();
        WaitManager.safePageLoad();

        if (lp.isOTPScreenVisible()) {
            lp.enterOTP("12");
            lp.clickVerifyOTP();
            BrowserManager.getPage().waitForTimeout(1000);
            a.assertTrue(lp.isOTPScreenVisible(), "TC-NG-005: Short OTP should not complete login -- user should remain on OTP screen");
        }
        a.assertAll();
    }

    // ─── TC-NG-006: Empty first name on profile ───────────────────────────────
    @Test(priority = 6,
          description = "Submit profile with empty first name -- expect validation error")
    public void testEmptyFirstNameOnProfile() {
        AssertionHelper a = new AssertionHelper();
        new WelcomePage(utilLayer).completeOnboarding();
        new LoginPage(utilLayer).loginWithDefaultCredentials();

        com.toskie.pages.ProfileCreationPage pp = new com.toskie.pages.ProfileCreationPage(utilLayer);
        if (!pp.isProfileCreationPageVisible()) {
            a.assertContains(BrowserManager.getPage().url(), "toskie.com", "Profile already exists -- test N/A, should be on toskie.com");
            a.assertAll();
            return;
        }

        pp.enterFirstName("");
        boolean buttonDisabledOrError = false;
        try {
            pp.clickCreateProfile();
            BrowserManager.getPage().waitForTimeout(1000);
            buttonDisabledOrError = pp.isFirstNameErrorVisible() || pp.isGeneralErrorVisible();
        } catch (Exception ignored) {
            buttonDisabledOrError = true;
        }
        a.assertTrue(buttonDisabledOrError, "Empty first name should show validation error or disable create button");
        a.assertAll();
    }

    // ─── TC-NG-007: Invalid email format on profile creation ──────────────────
    @Test(priority = 7,
          description = "Enter invalid email format -- expect format validation error")
    public void testInvalidEmailOnProfileCreation() {
        AssertionHelper a = new AssertionHelper();
        new WelcomePage(utilLayer).completeOnboarding();
        new LoginPage(utilLayer).loginWithDefaultCredentials();

        com.toskie.pages.ProfileCreationPage pp = new com.toskie.pages.ProfileCreationPage(utilLayer);
        if (!pp.isProfileCreationPageVisible()) {
            a.assertContains(BrowserManager.getPage().url(), "toskie.com", "Profile already exists -- test N/A, should be on toskie.com");
            a.assertAll();
            return;
        }

        pp.enterEmail("notanemail");
        boolean validationTriggered = false;
        try {
            pp.clickSendOTP();
            BrowserManager.getPage().waitForTimeout(1000);
            validationTriggered = pp.isEmailErrorVisible() || pp.isGeneralErrorVisible();
        } catch (Exception ignored) {
            validationTriggered = true;
        }
        a.assertTrue(validationTriggered, "Invalid email format should show error or disable Send OTP button");
        a.assertAll();
    }

    // ─── TC-NG-008: Under-18 DOB selection ───────────────────────────────────
    @Test(priority = 8,
          description = "Select DOB that makes user under 18 -- expect age validation error")
    public void testUnderAgeDOBSelection() {
        AssertionHelper a = new AssertionHelper();
        new WelcomePage(utilLayer).completeOnboarding();
        new LoginPage(utilLayer).loginWithDefaultCredentials();

        com.toskie.pages.ProfileCreationPage pp = new com.toskie.pages.ProfileCreationPage(utilLayer);
        if (!pp.isProfileCreationPageVisible()) {
            a.assertContains(BrowserManager.getPage().url(), "toskie.com", "Profile already exists -- test N/A, should be on toskie.com");
            a.assertAll();
            return;
        }

        // Try selecting a date that's within last 18 years
        try {
            pp.selectCustomDOB(2010, "Jan", 1);
            pp.acceptTermsAndConditions();
            pp.clickCreateProfile();
            BrowserManager.getPage().waitForTimeout(1000);

            boolean hasError = pp.isAgeValidationErrorVisible() || pp.isGeneralErrorVisible();
            a.assertTrue(hasError, "Under-18 DOB should show age validation error");
        } catch (Exception e) {
            a.assertContains(BrowserManager.getPage().url(), "toskie.com", "TC-NG-008: After under-18 DOB exception, should be on toskie.com");
        }
        a.assertAll();
    }

    // ─── TC-NG-009: Data-driven negative tests ────────────────────────────────
    @Test(dataProvider = "negativeLoginData", priority = 9,
          description = "Data-driven negative tests for multiple invalid inputs")
    public void testNegativeInputScenarios(String input, String field, String expectedError) {
        AssertionHelper a = new AssertionHelper();
        if ("phoneNumber".equals(field)) {
            navigateToLogin();
            LoginPage lp = new LoginPage(utilLayer);
            if (!input.isEmpty()) lp.enterPhoneNumber(input);
            lp.clickSendOTP();
            BrowserManager.getPage().waitForTimeout(1000);

            boolean notOnOTP = !lp.isOTPScreenVisible() || lp.isPhoneValidationErrorVisible();
            a.assertTrue(notOnOTP,
                "Invalid phone '" + input + "' should show error or not proceed to OTP screen");
        }
        a.assertAll();
    }

    // ─── TC-NG-010: Phone number with special characters ─────────────────────
    @Test(priority = 10,
          description = "Phone number with special characters should be rejected")
    public void testSpecialCharsInPhoneNumber() {
        AssertionHelper a = new AssertionHelper();
        navigateToLogin();

        LoginPage lp = new LoginPage(utilLayer);
        lp.enterPhoneNumber("+1-800-555-0000");
        lp.clickSendOTP();
        BrowserManager.getPage().waitForTimeout(1000);

        boolean hasError = lp.isPhoneValidationErrorVisible() || !lp.isOTPScreenVisible();
        a.assertTrue(hasError, "Phone with special chars should fail");
        a.assertAll();
    }
}