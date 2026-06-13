package com.toskie.pages.profile;

import com.aventstack.extentreports.Status;
import com.microsoft.playwright.options.LoadState;
import com.toskie.locators.PersonalInfoPageLocators;
import com.toskie.models.PersonalInfoData;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.ReportManager;
import com.toskie.utils_Layer.UtilLayer;

import java.nio.file.Paths;

public class PersonalInfoPage {

    private final UtilLayer<?> util;
    private final PersonalInfoPageLocators loc;

    public PersonalInfoPage(UtilLayer<?> util) {
        this.util = util;
        this.loc  = new PersonalInfoPageLocators(BrowserManager.getPage());
    }

    public void fillPersonalInfo(PersonalInfoData data) {
        ReportManager.getTest().log(Status.INFO, "Filling personal info for: " + data.getName());
        fillName(data.getName());
        fillPhone(data.getPhone());
        fillDOB(data.getDob());
        selectGender(data.getGender());
        if (data.getProfileImagePath() != null && !data.getProfileImagePath().isEmpty()) {
            uploadProfileImage(data.getProfileImagePath());
        }
    }

    public void fillName(String name) {
        ReportManager.getTest().log(Status.INFO, "Filling name: " + name);
        util.fill(loc.nameInput, name, "Name Input");
    }

    public void fillPhone(String phone) {
        ReportManager.getTest().log(Status.INFO, "Filling phone: " + phone);
        util.fill(loc.phoneInput, phone, "Phone Input");
    }

    public void fillDOB(String dob) {
        ReportManager.getTest().log(Status.INFO, "Filling DOB: " + dob);
        util.fill(loc.dobInput, dob, "DOB Input");
    }

    public void selectGender(String gender) {
        ReportManager.getTest().log(Status.INFO, "Selecting gender: " + gender);
        try {
            loc.genderSelect.selectOption(gender);
        } catch (Exception e) {
            try {
                BrowserManager.getPage()
                        .locator("label:has-text('" + gender + "'), [value='" + gender + "']")
                        .click();
            } catch (Exception ex) {
                ReportManager.getTest().log(Status.WARNING, "Gender selection issue: " + ex.getMessage());
            }
        }
    }

    public void uploadProfileImage(String path) {
        ReportManager.getTest().log(Status.INFO, "Uploading profile image: " + path);
        try {
            loc.profileImageUpload.setInputFiles(Paths.get(path));
            BrowserManager.getPage().waitForTimeout(1500);
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "Profile image upload issue: " + e.getMessage());
        }
    }

    public void removeImage() {
        ReportManager.getTest().log(Status.INFO, "Removing profile image");
        try {
            BrowserManager.getPage()
                    .locator("button[aria-label='Remove image'], button:has-text('Remove'), [data-testid='remove-image-btn']")
                    .click();
            BrowserManager.getPage().waitForTimeout(1000);
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "Remove image issue: " + e.getMessage());
        }
    }

    public void clickNext() {
        ReportManager.getTest().log(Status.INFO, "Clicking Next on Personal Info");
        util.click(loc.nextButton, "Next Button");
        try {
            BrowserManager.getPage().waitForLoadState(LoadState.DOMCONTENTLOADED,
                    new com.microsoft.playwright.Page.WaitForLoadStateOptions().setTimeout(10000));
        } catch (Exception ignored) {}
    }

    public void clickSave() {
        ReportManager.getTest().log(Status.INFO, "Clicking Save on Personal Info");
        util.click(loc.saveButton, "Save Button");
        BrowserManager.getPage().waitForTimeout(1500);
    }

    public String getFieldError(String fieldName) {
        ReportManager.getTest().log(Status.INFO, "Getting field error for: " + fieldName);
        try {
            switch (fieldName.toLowerCase()) {
                case "name":   return loc.nameError.textContent().trim();
                case "phone":  return loc.phoneError.textContent().trim();
                case "dob":    return loc.dobError.textContent().trim();
                case "gender": return loc.genderError.textContent().trim();
                default:
                    return BrowserManager.getPage()
                            .locator("#" + fieldName + "-helper-text, ." + fieldName + "-error")
                            .textContent().trim();
            }
        } catch (Exception e) {
            return "";
        }
    }

    public boolean isImagePreviewVisible() {
        try {
            return loc.imagePreview.isVisible();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isFormLoaded() {
        try {
            return loc.nameInput.isVisible() && loc.phoneInput.isVisible();
        } catch (Exception e) {
            return false;
        }
    }
    public void navigateToPersonalInfo()    { try { BrowserManager.getPage().navigate(BrowserManager.getPage().url().replaceAll("/profile.*", "/profile/personal-info")); BrowserManager.getPage().waitForTimeout(1500); } catch (Exception ignored) {} }
}
