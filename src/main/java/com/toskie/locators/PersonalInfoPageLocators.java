package com.toskie.locators;
import com.microsoft.playwright.*;

public class PersonalInfoPageLocators {
    public final Locator fullNameInput, emailInput, dobInput, genderDropdown, cityInput,
            stateInput, countryDropdown, pinCodeInput, addressInput, profilePhoto,
            saveBtn, nextBtn, nameError, emailError, dobError,
            nameInput, phoneInput, genderSelect, profileImageUpload, nextButton, saveButton,
            phoneError, genderError, imagePreview;

    public PersonalInfoPageLocators(Page page) {
        fullNameInput  = page.locator("input[name='fullName'], input[placeholder*='Full Name' i]").first();
        emailInput     = page.locator("input[type='email'], input[name='email']").first();
        dobInput       = page.locator("input[type='date'], input[name='dob'], input[placeholder*='Date' i]").first();
        genderDropdown = page.locator("select[name='gender'], [class*='gender-select']").first();
        cityInput      = page.locator("input[name='city'], input[placeholder*='City' i]").first();
        stateInput     = page.locator("input[name='state'], input[placeholder*='State' i]").first();
        countryDropdown= page.locator("select[name='country'], [class*='country-select']").first();
        pinCodeInput   = page.locator("input[name='pinCode'], input[placeholder*='PIN' i]").first();
        addressInput   = page.locator("textarea[name='address'], input[placeholder*='Address' i]").first();
        profilePhoto   = page.locator("input[type='file'], [class*='upload-photo']").first();
        saveBtn        = page.locator("//button[normalize-space()='Save']").first();
        nextBtn        = page.locator("//button[normalize-space()='Next'] | //button[contains(.,'Continue')]").first();
        nameError      = page.locator("[class*='error']:near(input[name='fullName'])").first();
        emailError     = page.locator("[class*='error']:near(input[type='email'])").first();
        dobError          = page.locator("[class*='error']:near(input[name='dob'])").first();
        nameInput         = fullNameInput;
        phoneInput        = page.locator("input[type='tel'], input[name*='phone' i], input[placeholder*='Phone' i]").first();
        genderSelect      = genderDropdown;
        profileImageUpload = profilePhoto;
        nextButton        = nextBtn;
        saveButton        = saveBtn;
        phoneError        = page.locator("[class*='error']:near(input[type='tel']), [class*='phone-error']").first();
        genderError       = page.locator("[class*='error']:near(select[name='gender']), [class*='gender-error']").first();
        imagePreview      = page.locator("[class*='profile-preview'], img[class*='preview'], [class*='avatar-preview']").first();
    }
}
