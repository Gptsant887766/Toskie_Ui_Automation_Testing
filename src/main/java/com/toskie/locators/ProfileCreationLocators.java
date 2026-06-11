package com.toskie.locators;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class ProfileCreationLocators {

    // ─── Personal Info ─────────────────────────────────────────────────────────
    public final Locator firstNameInput;
    public final Locator lastNameInput;
    public final Locator emailInput;
    public final Locator sendOtpButton;
    public final Locator emailOtpInput;
    public final Locator verifyEmailButton;
    public final Locator profilePhotoUpload;
    public final Locator profilePhotoPreview;

    // ─── Gender ────────────────────────────────────────────────────────────────
    public final Locator genderMaleButton;
    public final Locator genderFemaleButton;
    public final Locator genderOtherButton;
    public final Locator genderPreferNotSayButton;
    public final Locator selectedGenderIndicator;

    // ─── Date of Birth ────────────────────────────────────────────────────────
    public final Locator dobButton;
    public final Locator dobCalendarDialog;
    public final Locator calendarYearMonthHeader;
    public final Locator calendarNextArrow;
    public final Locator calendarPrevArrow;
    public final Locator calendarYearHeader;
    public final Locator calendarYear2026;
    public final Locator calendarYearBack;
    public final Locator calendarYear1997;
    public final Locator calendarMonthMay;
    public final Locator calendarDate25;
    public final Locator calendarSaveButton;
    public final Locator selectedDobDisplay;
    public final Locator ageValidationError;

    // ─── Location ─────────────────────────────────────────────────────────────
    public final Locator locationInput;
    public final Locator locationSuggestionsDropdown;
    public final Locator detectLocationButton;

    // ─── Skills / Category ────────────────────────────────────────────────────
    public final Locator categoryDropdown;
    public final Locator skillsInput;
    public final Locator skillTag;
    public final Locator addSkillButton;

    // ─── Bio / About ──────────────────────────────────────────────────────────
    public final Locator bioTextarea;
    public final Locator bioCharCount;

    // ─── Terms & Submit ───────────────────────────────────────────────────────
    public final Locator termsCheckbox;
    public final Locator createProfileButton;
    public final Locator profileCreatedSuccessMessage;
    public final Locator profileProgressBar;

    // ─── Validation messages ──────────────────────────────────────────────────
    public final Locator firstNameError;
    public final Locator lastNameError;
    public final Locator emailError;
    public final Locator generalError;

    public ProfileCreationLocators(Page page) {
        // Personal Info
        firstNameInput       = page.locator("//input[@placeholder='Enter First Name']");
        lastNameInput        = page.locator("//input[@placeholder='Enter Last Name']");
        emailInput           = page.locator("//input[@placeholder='Enter Email ID']");
        sendOtpButton        = page.locator("//button[normalize-space()='Send OTP']");
        emailOtpInput        = page.locator("input[placeholder*='Enter OTP'], input[placeholder*='email otp' i]");
        verifyEmailButton    = page.locator("//button[contains(text(),'Verify Email')]");
        profilePhotoUpload   = page.locator("input[type='file'], [class*='upload-photo'], [class*='avatar-upload']");
        profilePhotoPreview  = page.locator("[class*='avatar'], [class*='profile-photo'] img");

        // Gender
        genderMaleButton         = page.locator("//button[@id='MALE']");
        genderFemaleButton       = page.locator("//button[@id='FEMALE']");
        genderOtherButton        = page.locator("//button[@id='OTHER']");
        genderPreferNotSayButton = page.locator("//button[@id='PREFER_NOT_TO_SAY']");
        selectedGenderIndicator  = page.locator("[class*='selected'][class*='gender'], button[aria-pressed='true'][id*='MALE'], button[aria-pressed='true'][id*='FEMALE']");

        // Date of Birth
        dobButton            = page.locator("//button[contains(@class,'inline-flex') and contains(@class,'h-12') and contains(@class,'w-full') and contains(@class,'justify-between')]");
        dobCalendarDialog    = page.locator("//div[@role='dialog']");
        calendarYearMonthHeader = page.locator("//button[@class='heading-5 text-text-text cursor-pointer rounded px-2 py-1 transition-colors']");
        calendarNextArrow    = page.locator("//div[@role='dialog']//*[name()='svg']").last();
        calendarPrevArrow    = page.locator("//div[@role='dialog']//*[name()='svg']").first();
        calendarYearHeader   = page.locator("//button[contains(@class,'heading-5')]");
        calendarYear2026     = page.locator("//button[contains(text(),'2026')]");
        calendarYearBack     = page.locator("//button[contains(@class,'size-9') and contains(@class,'h-3')]");
        calendarYear1997     = page.locator("//button[contains(text(),'1997')]");
        calendarMonthMay     = page.locator("//button[contains(text(),'May')]");
        calendarDate25       = page.locator("//button[contains(text(),'25')]");
        calendarSaveButton   = page.locator("//button[.//span[contains(text(),'Save')]]");
        selectedDobDisplay   = page.locator("[class*='dob-display'], [class*='date-selected']");
        ageValidationError   = page.locator("[class*='error']:has-text('age'), [class*='error']:has-text('18')");

        // Location
        locationInput              = page.locator("input[placeholder*='location' i], input[placeholder*='city' i], input[placeholder*='area' i]");
        locationSuggestionsDropdown= page.locator("[class*='suggestions'], [class*='dropdown-location']");
        detectLocationButton       = page.locator("button:has-text('Detect Location'), [class*='detect-location']");

        // Skills
        categoryDropdown = page.locator("select[name*='category'], [class*='category-select'], [role='combobox']");
        skillsInput      = page.locator("input[placeholder*='skill' i], input[placeholder*='Add skill' i]");
        skillTag         = page.locator("[class*='skill-tag'], [class*='tag-item']");
        addSkillButton   = page.locator("button:has-text('Add'), [class*='add-skill']");

        // Bio
        bioTextarea  = page.locator("textarea[placeholder*='about' i], textarea[placeholder*='bio' i], textarea[name*='bio']");
        bioCharCount = page.locator("[class*='char-count'], [class*='character-limit']");

        // Terms & Submit
        termsCheckbox               = page.locator("//button[@id='terms-checkbox']");
        createProfileButton         = page.locator("//button[contains(@class,'bg-text-primary') and contains(@class,'w-full')]");
        profileCreatedSuccessMessage= page.locator("[class*='success']:has-text('Profile'), [class*='toast']:has-text('created')");
        profileProgressBar          = page.locator("[class*='progress'], [role='progressbar']");

        // Validation
        firstNameError = page.locator("[class*='error']:has-text('First Name'), [class*='error']:has-text('first name')");
        lastNameError  = page.locator("[class*='error']:has-text('Last Name'), [class*='error']:has-text('last name')");
        emailError     = page.locator("[class*='error']:has-text('Email'), [class*='error']:has-text('email')");
        generalError   = page.locator("[class*='error-message'], [class*='toast-error'], [role='alert']");
    }
}
