package com.toskie.pages;

import com.aventstack.extentreports.Status;
import com.microsoft.playwright.options.LoadState;
import com.toskie.locators.ProfileCreationLocators;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.ConfigManager;
import com.toskie.utils_Layer.ReportManager;
import com.toskie.utils_Layer.UtilLayer;
import com.toskie.utils_Layer.WaitManager;

public class ProfileCreationPage {

    private final UtilLayer<?> util;
    private final ProfileCreationLocators loc;

    public ProfileCreationPage(UtilLayer<?> util) {
        this.util = util;
        this.loc  = new ProfileCreationLocators(BrowserManager.getPage());
    }

    // ─── Personal Info ────────────────────────────────────────────────────────

    public void enterFirstName(String firstName) {
        util.fill(loc.firstNameInput, firstName, "First Name");
    }

    public void enterLastName(String lastName) {
        util.fill(loc.lastNameInput, lastName, "Last Name");
    }

    public void enterEmail(String email) {
        util.typeValue(loc.emailInput, email);
        ReportManager.getTest().log(Status.INFO, "Email entered: " + email);
    }

    public void clickSendOTP() {
        util.click(loc.sendOtpButton, "Send OTP");
    }

    public void bypassEmailOTP(String email) {
        util.bypassEmailOTP(email);
        WaitManager.safePageLoad();
        BrowserManager.getPage().waitForTimeout(2000);
    }

    // ─── Gender ───────────────────────────────────────────────────────────────

    public void selectGender(String gender) {
        switch (gender.toUpperCase()) {
            case "MALE":
                util.click(loc.genderMaleButton, "Gender: MALE");
                break;
            case "FEMALE":
                util.click(loc.genderFemaleButton, "Gender: FEMALE");
                break;
            case "OTHER":
                util.click(loc.genderOtherButton, "Gender: OTHER");
                break;
            case "PREFER_NOT_TO_SAY":
            case "PREFER NOT TO SAY":
                util.click(loc.genderPreferNotSayButton, "Gender: Prefer Not to Say");
                break;
            default:
                ReportManager.getTest().log(Status.WARNING, "Unknown gender: " + gender);
        }
    }

    // ─── Date of Birth ────────────────────────────────────────────────────────

    public void selectDateOfBirth() {
        util.click(loc.dobButton, "DOB Button");
        util.click(loc.calendarYearHeader, "Year/Month Header");
        util.click(loc.calendarYear2026, "Year 2026");
        util.doubleClick(loc.calendarYearBack);
        util.click(loc.calendarYear1997, "Year 1997");
        util.click(loc.calendarMonthMay, "Month: May");
        util.click(loc.calendarDate25, "Date: 25");
        util.click(loc.calendarSaveButton, "Save DOB");
        BrowserManager.getPage().waitForTimeout(500);
        ReportManager.getTest().log(Status.PASS, "DOB selected: 25 May 1997");
    }

    public void selectCustomDOB(int year, String month, int day) {
        util.click(loc.dobButton, "DOB Button");
        // Navigate to year
        util.click(loc.calendarYearHeader, "Year/Month Header");
        BrowserManager.getPage().waitForTimeout(500);
        // Click the year
        BrowserManager.getPage().locator("//button[contains(text(),'" + year + "')]").click();
        BrowserManager.getPage().waitForTimeout(300);
        // Click month
        BrowserManager.getPage().locator("//button[contains(text(),'" + month + "')]").click();
        BrowserManager.getPage().waitForTimeout(300);
        // Click day
        BrowserManager.getPage().locator("//button[contains(text(),'" + day + "')]").click();
        BrowserManager.getPage().waitForTimeout(300);
        util.click(loc.calendarSaveButton, "Save DOB");
    }

    // ─── Terms & Submit ───────────────────────────────────────────────────────

    public void acceptTermsAndConditions() {
        util.click(loc.termsCheckbox, "Terms & Conditions Checkbox");
    }

    public void clickCreateProfile() {
        util.click(loc.createProfileButton, "Create My Profile Button");
        WaitManager.safePageLoad();
        BrowserManager.getPage().waitForTimeout(3000);
    }

    // ─── Complete flows ───────────────────────────────────────────────────────

    public void createProfileComplete(String firstName, String lastName, String email) {
        ReportManager.getTest().log(Status.INFO, "Starting profile creation for: " + firstName + " " + lastName);

        enterFirstName(firstName);
        enterLastName(lastName);

        BrowserManager.getPage().waitForLoadState(LoadState.DOMCONTENTLOADED);
        BrowserManager.getPage().waitForTimeout(1500);

        enterEmail(email);
        clickSendOTP();
        bypassEmailOTP(email);

        WaitManager.safePageLoad();
        BrowserManager.getPage().waitForTimeout(2000);

        selectGender("MALE");
        selectDateOfBirth();
        acceptTermsAndConditions();
        clickCreateProfile();

        ReportManager.getTest().log(Status.PASS, "Profile creation completed.");
    }

    public void createProfileWithDefaultData() {
        createProfileComplete(
            "Sontosh",
            "Gupta",
            System.getProperty("testEmail", ConfigManager.getTestEmail())
        );
    }

    // ─── Verifications ────────────────────────────────────────────────────────

    public boolean isProfileCreationPageVisible() {
        try { return loc.firstNameInput.isVisible(); }
        catch (Exception e) { return false; }
    }

    public boolean isFirstNameErrorVisible() {
        try { return loc.firstNameError.isVisible(); }
        catch (Exception e) { return false; }
    }

    public boolean isLastNameErrorVisible() {
        try { return loc.lastNameError.isVisible(); }
        catch (Exception e) { return false; }
    }

    public boolean isEmailErrorVisible() {
        try { return loc.emailError.isVisible(); }
        catch (Exception e) { return false; }
    }

    public boolean isAgeValidationErrorVisible() {
        try { return loc.ageValidationError.isVisible(); }
        catch (Exception e) { return false; }
    }

    public boolean isGeneralErrorVisible() {
        try { return loc.generalError.isVisible(); }
        catch (Exception e) { return false; }
    }

    public boolean isCreateButtonEnabled() {
        try { return loc.createProfileButton.isEnabled(); }
        catch (Exception e) { return false; }
    }

    public void verifyAllFieldsVisible() {
        util.verifyElementVisible(loc.firstNameInput, "First Name Input");
        util.verifyElementVisible(loc.lastNameInput,  "Last Name Input");
        util.verifyElementVisible(loc.emailInput,     "Email Input");
        util.verifyElementVisible(loc.sendOtpButton,  "Send OTP Button");
        util.verifyElementVisible(loc.genderMaleButton,"Gender Male Button");
        util.verifyElementVisible(loc.dobButton,      "DOB Button");
        util.verifyElementVisible(loc.termsCheckbox,  "Terms Checkbox");
        util.verifyElementVisible(loc.createProfileButton, "Create Profile Button");
    }
}
