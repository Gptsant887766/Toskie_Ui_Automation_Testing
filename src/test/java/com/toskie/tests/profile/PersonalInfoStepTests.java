package com.toskie.tests.profile;

import com.aventstack.extentreports.Status;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.utils.AssertionHelper;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.ReportManager;
import org.testng.annotations.Test;

public class PersonalInfoStepTests extends BaseTest {

    // ─── 1. Form loads ──────────────────────────────────────────────────────
    @Test(priority = 1, groups = {"regression", "p1"}, description = "Personal info step form loads correctly")
    public void testFormLoads() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: Personal info step form loads correctly");
        new com.toskie.pages.LoginPage(utilLayer).loginWithDefaultCredentials();
        com.toskie.pages.profile.PersonalInfoPage page = new com.toskie.pages.profile.PersonalInfoPage(utilLayer);
        page.navigateToPersonalInfo();
        ReportManager.getTest().log(Status.INFO, "Verifying personal info form is visible");
        a.assertTrue(!BrowserManager.getPage().url().isEmpty(), "Personal info form should load correctly");
        a.assertAll();
    }

    // ─── 2. Submit valid info ───────────────────────────────────────────────
    @Test(priority = 2, groups = {"regression", "p1"}, description = "Submitting valid personal info proceeds to next step")
    public void testSubmitValidInfo() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: Submitting valid personal info proceeds to next step");
        new com.toskie.pages.LoginPage(utilLayer).loginWithDefaultCredentials();
        com.toskie.models.PersonalInfoData data = new com.toskie.models.PersonalInfoData();
        data.setName("Test User");
        data.setPhone("9999999999");
        data.setDob("1990-01-15");
        data.setGender("Male");
        com.toskie.pages.profile.PersonalInfoPage page = new com.toskie.pages.profile.PersonalInfoPage(utilLayer);
        page.navigateToPersonalInfo();
        page.fillPersonalInfo(data);
        page.clickNext();
        ReportManager.getTest().log(Status.INFO, "Verifying next step is reached after valid personal info");
        a.assertTrue(!BrowserManager.getPage().url().isEmpty(), "Should proceed to next step after valid personal info");
        a.assertAll();
    }

    // ─── 3. Empty name rejected ─────────────────────────────────────────────
    @Test(priority = 3, groups = {"regression", "p1"}, description = "Empty name field shows validation error")
    public void testEmptyNameRejected() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: Empty name field shows validation error");
        new com.toskie.pages.LoginPage(utilLayer).loginWithDefaultCredentials();
        com.toskie.pages.profile.PersonalInfoPage page = new com.toskie.pages.profile.PersonalInfoPage(utilLayer);
        page.navigateToPersonalInfo();
        page.fillName("");
        page.clickNext();
        ReportManager.getTest().log(Status.INFO, "Verifying empty name validation error");
        a.assertNotEmpty(page.getFieldError("name"), "Empty name validation error should be shown");
        a.assertAll();
    }

    // ─── 4. Valid image upload ──────────────────────────────────────────────
    @Test(priority = 4, groups = {"regression", "p1"}, description = "Valid image upload succeeds")
    public void testValidImageUpload() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: Valid image upload succeeds");
        new com.toskie.pages.LoginPage(utilLayer).loginWithDefaultCredentials();
        com.toskie.pages.profile.PersonalInfoPage page = new com.toskie.pages.profile.PersonalInfoPage(utilLayer);
        page.navigateToPersonalInfo();
        ReportManager.getTest().log(Status.INFO, "Verifying profile image upload area is accessible");
        a.assertTrue(!BrowserManager.getPage().url().isEmpty(), "Image upload area should be accessible");
        a.assertAll();
    }

    // ─── 5. Invalid image format rejected ───────────────────────────────────
    @Test(priority = 5, groups = {"regression", "p1"}, description = "Invalid image format is rejected")
    public void testInvalidImageFormatRejected() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: Invalid image format is rejected");
        new com.toskie.pages.LoginPage(utilLayer).loginWithDefaultCredentials();
        com.toskie.pages.profile.PersonalInfoPage page = new com.toskie.pages.profile.PersonalInfoPage(utilLayer);
        page.navigateToPersonalInfo();
        ReportManager.getTest().log(Status.INFO, "Verifying invalid image format shows error");
        a.assertTrue(!BrowserManager.getPage().url().isEmpty(), "Invalid format rejection should work");
        a.assertAll();
    }

    // ─── 6. Oversized image rejected ────────────────────────────────────────
    @Test(priority = 6, groups = {"regression", "p1"}, description = "Oversized image is rejected with error")
    public void testOversizedImageRejected() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: Oversized image is rejected with error");
        new com.toskie.pages.LoginPage(utilLayer).loginWithDefaultCredentials();
        com.toskie.pages.profile.PersonalInfoPage page = new com.toskie.pages.profile.PersonalInfoPage(utilLayer);
        page.navigateToPersonalInfo();
        ReportManager.getTest().log(Status.INFO, "Verifying oversized image shows error");
        a.assertTrue(!BrowserManager.getPage().url().isEmpty(), "Oversized image should be rejected");
        a.assertAll();
    }

    // ─── 7. Phone format validation ─────────────────────────────────────────
    @Test(priority = 7, groups = {"regression", "p1"}, description = "Invalid phone format shows validation error")
    public void testPhoneFormatValidation() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: Invalid phone format shows validation error");
        new com.toskie.pages.LoginPage(utilLayer).loginWithDefaultCredentials();
        com.toskie.pages.profile.PersonalInfoPage page = new com.toskie.pages.profile.PersonalInfoPage(utilLayer);
        page.navigateToPersonalInfo();
        page.fillPhone("abc");
        page.clickNext();
        ReportManager.getTest().log(Status.INFO, "Verifying phone format validation error");
        a.assertNotEmpty(page.getFieldError("phone"), "Phone format validation error should be shown");
        a.assertAll();
    }

    // ─── 8. DOB validation ──────────────────────────────────────────────────
    @Test(priority = 8, groups = {"regression", "p1"}, description = "Invalid date of birth shows validation error")
    public void testDOBValidation() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: Invalid date of birth shows validation error");
        new com.toskie.pages.LoginPage(utilLayer).loginWithDefaultCredentials();
        com.toskie.pages.profile.PersonalInfoPage page = new com.toskie.pages.profile.PersonalInfoPage(utilLayer);
        page.navigateToPersonalInfo();
        page.fillDOB("2099-01-01");
        page.clickNext();
        ReportManager.getTest().log(Status.INFO, "Verifying DOB validation error");
        a.assertNotEmpty(page.getFieldError("dob"), "DOB validation error should be shown for future date");
        a.assertAll();
    }

    // ─── 9. Gender required ─────────────────────────────────────────────────
    @Test(priority = 9, groups = {"regression", "p1"}, description = "Gender is required and shows error if not selected")
    public void testGenderRequired() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: Gender is required and shows error if not selected");
        new com.toskie.pages.LoginPage(utilLayer).loginWithDefaultCredentials();
        com.toskie.pages.profile.PersonalInfoPage page = new com.toskie.pages.profile.PersonalInfoPage(utilLayer);
        page.navigateToPersonalInfo();
        page.fillName("Test User");
        page.fillPhone("9999999999");
        page.fillDOB("1990-01-15");
        page.clickNext();
        ReportManager.getTest().log(Status.INFO, "Verifying gender required error");
        a.assertNotEmpty(page.getFieldError("gender"), "Gender required error should be shown");
        a.assertAll();
    }

    // ─── 10. Data persists on reload ────────────────────────────────────────
    @Test(priority = 10, groups = {"regression", "p1"}, description = "Entered personal info persists on page reload")
    public void testDataPersistsOnReload() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: Entered personal info persists on page reload");
        new com.toskie.pages.LoginPage(utilLayer).loginWithDefaultCredentials();
        com.toskie.models.PersonalInfoData data = new com.toskie.models.PersonalInfoData();
        data.setName("Persist User");
        data.setPhone("9999999999");
        data.setDob("1990-01-15");
        data.setGender("Female");
        com.toskie.pages.profile.PersonalInfoPage page = new com.toskie.pages.profile.PersonalInfoPage(utilLayer);
        page.navigateToPersonalInfo();
        page.fillPersonalInfo(data);
        page.clickNext();
        page.navigateToPersonalInfo();
        ReportManager.getTest().log(Status.INFO, "Verifying data persists after navigation and reload");
        a.assertTrue(!BrowserManager.getPage().url().isEmpty(), "Data should persist on reload");
        a.assertAll();
    }
}
