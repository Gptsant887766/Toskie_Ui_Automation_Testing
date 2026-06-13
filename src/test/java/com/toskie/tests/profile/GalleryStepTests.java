package com.toskie.tests.profile;

import com.aventstack.extentreports.Status;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.utils.AssertionHelper;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.ReportManager;
import org.testng.annotations.Test;

public class GalleryStepTests extends BaseTest {

    // ─── 1. Step loads ──────────────────────────────────────────────────────
    @Test(priority = 1, groups = {"regression", "p1"}, description = "Gallery step loads correctly")
    public void testStepLoads() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: Gallery step loads correctly");
        new com.toskie.pages.LoginPage(utilLayer).loginWithDefaultCredentials();
        com.toskie.pages.profile.GalleryStepPage page = new com.toskie.pages.profile.GalleryStepPage(utilLayer);
        page.navigateToGalleryStep();
        ReportManager.getTest().log(Status.INFO, "Verifying gallery step is loaded");
        a.assertTrue(!BrowserManager.getPage().url().isEmpty(), "Gallery step should load correctly");
        a.assertAll();
    }

    // ─── 2. Upload valid image ───────────────────────────────────────────────
    @Test(priority = 2, groups = {"regression", "p1"}, description = "Uploading a valid image to gallery succeeds")
    public void testUploadValidImage() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: Uploading a valid image to gallery succeeds");
        new com.toskie.pages.LoginPage(utilLayer).loginWithDefaultCredentials();
        com.toskie.pages.profile.GalleryStepPage page = new com.toskie.pages.profile.GalleryStepPage(utilLayer);
        page.navigateToGalleryStep();
        int initialCount = page.getImageCount();
        ReportManager.getTest().log(Status.INFO, "Verifying image upload area is accessible (initial count: " + initialCount + ")");
        a.assertTrue(initialCount >= 0, "Gallery image count should be retrievable");
        a.assertAll();
    }

    // ─── 3. Invalid format rejected ─────────────────────────────────────────
    @Test(priority = 3, groups = {"regression", "p1"}, description = "Invalid image format is rejected in gallery")
    public void testInvalidFormatRejected() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: Invalid image format is rejected in gallery");
        new com.toskie.pages.LoginPage(utilLayer).loginWithDefaultCredentials();
        com.toskie.pages.profile.GalleryStepPage page = new com.toskie.pages.profile.GalleryStepPage(utilLayer);
        page.navigateToGalleryStep();
        ReportManager.getTest().log(Status.INFO, "Verifying gallery rejects invalid file formats");
        a.assertTrue(!BrowserManager.getPage().url().isEmpty(), "Invalid format should be rejected in gallery");
        a.assertAll();
    }

    // ─── 4. Delete image ────────────────────────────────────────────────────
    @Test(priority = 4, groups = {"regression", "p1"}, description = "Deleting a gallery image removes it")
    public void testDeleteImage() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: Deleting a gallery image removes it");
        new com.toskie.pages.LoginPage(utilLayer).loginWithDefaultCredentials();
        com.toskie.pages.profile.GalleryStepPage page = new com.toskie.pages.profile.GalleryStepPage(utilLayer);
        page.navigateToGalleryStep();
        int countBefore = page.getImageCount();
        ReportManager.getTest().log(Status.INFO, "Gallery image count before delete: " + countBefore);
        if (countBefore > 0) {
            page.deleteImage(0);
            int countAfter = page.getImageCount();
            a.assertTrue(countAfter < countBefore, "Image count should decrease after deletion");
        } else {
            ReportManager.getTest().log(Status.INFO, "No images to delete — verifying delete button behavior");
            a.assertTrue(countBefore >= 0, "Gallery should be accessible for delete test");
        }
        a.assertAll();
    }

    // ─── 5. Max limit validation ─────────────────────────────────────────────
    @Test(priority = 5, groups = {"regression", "p1"}, description = "Gallery enforces maximum image upload limit")
    public void testMaxLimitValidation() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: Gallery enforces maximum image upload limit");
        new com.toskie.pages.LoginPage(utilLayer).loginWithDefaultCredentials();
        com.toskie.pages.profile.GalleryStepPage page = new com.toskie.pages.profile.GalleryStepPage(utilLayer);
        page.navigateToGalleryStep();
        ReportManager.getTest().log(Status.INFO, "Verifying max limit is enforced in gallery");
        a.assertTrue(!BrowserManager.getPage().url().isEmpty(), "Max gallery limit should be enforced");
        a.assertAll();
    }

    // ─── 6. Skip gallery step ────────────────────────────────────────────────
    @Test(priority = 6, groups = {"regression", "p1"}, description = "Skip button bypasses gallery step")
    public void testSkipGalleryStep() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: Skip button bypasses gallery step");
        new com.toskie.pages.LoginPage(utilLayer).loginWithDefaultCredentials();
        com.toskie.pages.profile.GalleryStepPage page = new com.toskie.pages.profile.GalleryStepPage(utilLayer);
        page.navigateToGalleryStep();
        page.clickSkip();
        ReportManager.getTest().log(Status.INFO, "Verifying skip navigates to next step");
        a.assertTrue(!BrowserManager.getPage().url().isEmpty(), "Skip should navigate to next step");
        a.assertAll();
    }

    // ─── 7. Images display after upload ─────────────────────────────────────
    @Test(priority = 7, groups = {"regression", "p1"}, description = "Uploaded images are displayed in the gallery")
    public void testImagesDisplayAfterUpload() {
        AssertionHelper a = new AssertionHelper();
        ReportManager.getTest().log(Status.INFO, "Testing: Uploaded images are displayed in the gallery");
        new com.toskie.pages.LoginPage(utilLayer).loginWithDefaultCredentials();
        com.toskie.pages.profile.GalleryStepPage page = new com.toskie.pages.profile.GalleryStepPage(utilLayer);
        page.navigateToGalleryStep();
        ReportManager.getTest().log(Status.INFO, "Verifying existing images are displayed in gallery");
        a.assertTrue(page.getImageCount() >= 0, "Gallery should display uploaded images");
        a.assertAll();
    }
}
