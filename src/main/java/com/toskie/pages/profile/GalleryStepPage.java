package com.toskie.pages.profile;

import com.aventstack.extentreports.Status;
import com.toskie.locators.GalleryStepPageLocators;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.ReportManager;
import com.toskie.utils_Layer.UtilLayer;

import java.nio.file.Paths;

public class GalleryStepPage {

    private final UtilLayer<?> util;
    private final GalleryStepPageLocators loc;

    public GalleryStepPage(UtilLayer<?> util) {
        this.util = util;
        this.loc  = new GalleryStepPageLocators(BrowserManager.getPage());
    }

    public void uploadImage(String path) {
        ReportManager.getTest().log(Status.INFO, "Uploading gallery image: " + path);
        try {
            loc.uploadInput.setInputFiles(Paths.get(path));
            BrowserManager.getPage().waitForTimeout(2000);
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "Gallery image upload issue: " + e.getMessage());
        }
    }

    public void deleteImage(int index) {
        ReportManager.getTest().log(Status.INFO, "Deleting gallery image at index: " + index);
        try {
            loc.deleteButtons.nth(index).click();
            BrowserManager.getPage().waitForTimeout(1000);
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "Delete gallery image issue: " + e.getMessage());
        }
    }

    public int getImageCount() {
        ReportManager.getTest().log(Status.INFO, "Getting gallery image count");
        try {
            return loc.imageItems.count();
        } catch (Exception e) {
            return 0;
        }
    }

    public void clickSkip() {
        ReportManager.getTest().log(Status.INFO, "Clicking Skip on Gallery step");
        util.click(loc.skipButton, "Skip Button");
        BrowserManager.getPage().waitForTimeout(1000);
    }

    public void clickNext() {
        ReportManager.getTest().log(Status.INFO, "Clicking Next on Gallery step");
        util.click(loc.nextButton, "Next Button");
        BrowserManager.getPage().waitForTimeout(1000);
    }

    public boolean isMaxLimitReached() {
        try {
            return loc.maxLimitMsg.isVisible();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isStepLoaded() {
        try {
            return loc.galleryGrid.isVisible() || loc.addImageButton.isVisible();
        } catch (Exception e) {
            return false;
        }
    }

    public String getErrorMessage() {
        try {
            return loc.errorMessage.textContent().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public boolean isImageDisplayed(int index) {
        try {
            return loc.imageItems.nth(index).isVisible();
        } catch (Exception e) {
            return false;
        }
    }
    public void navigateToGalleryStep()     { try { BrowserManager.getPage().navigate(BrowserManager.getPage().url().replaceAll("/profile.*", "/profile/gallery")); BrowserManager.getPage().waitForTimeout(1500); } catch (Exception ignored) {} }
}
