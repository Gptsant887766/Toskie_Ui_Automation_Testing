package com.toskie.pages.posts;

import com.aventstack.extentreports.Status;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.ReportManager;
import com.toskie.utils_Layer.UtilLayer;

public class GalleryDetailPage {

    private final UtilLayer<?> util;

    public GalleryDetailPage(UtilLayer<?> util) {
        this.util = util;
    }

    public boolean isLoaded() {
        try {
            return BrowserManager.getPage()
                    .locator("[data-testid='gallery-detail'], .gallery-detail, .gallery-view, .gallery-lightbox")
                    .isVisible();
        } catch (Exception e) {
            return false;
        }
    }

    public int getImageCount() {
        ReportManager.getTest().log(Status.INFO, "Getting gallery detail image count");
        try {
            return BrowserManager.getPage()
                    .locator("[data-testid='gallery-image'], .gallery-image, .gallery-item img")
                    .count();
        } catch (Exception e) {
            return 0;
        }
    }

    public void clickImage(int index) {
        ReportManager.getTest().log(Status.INFO, "Clicking gallery image at index: " + index);
        try {
            BrowserManager.getPage()
                    .locator("[data-testid='gallery-image'], .gallery-image, .gallery-item img")
                    .nth(index).click();
            BrowserManager.getPage().waitForTimeout(500);
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "Click gallery image issue: " + e.getMessage());
        }
    }

    public String getCaption(int index) {
        ReportManager.getTest().log(Status.INFO, "Getting gallery image caption at index: " + index);
        try {
            return BrowserManager.getPage()
                    .locator("[data-testid='gallery-image'], .gallery-image, .gallery-item")
                    .nth(index)
                    .locator("[data-testid='caption'], .caption, figcaption, .image-caption")
                    .first().textContent().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public void closeGallery() {
        ReportManager.getTest().log(Status.INFO, "Closing gallery detail view");
        try {
            BrowserManager.getPage()
                    .locator("button[aria-label='Close'], button:has-text('Close'), [data-testid='close-gallery-btn'], .gallery-close")
                    .first().click();
            BrowserManager.getPage().waitForTimeout(500);
        } catch (Exception e) {
            BrowserManager.getPage().goBack();
        }
    }

    public boolean isDetailVisible() { return isLoaded(); }
}
