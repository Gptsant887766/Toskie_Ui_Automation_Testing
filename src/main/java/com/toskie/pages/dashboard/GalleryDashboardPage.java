package com.toskie.pages.dashboard;
import com.microsoft.playwright.*;
import com.toskie.utils_Layer.*;
import java.nio.file.Paths;

public class GalleryDashboardPage {
    private final UtilLayer<?> util;
    private final Locator uploadInput, galleryItems, deleteButtons, captionInputs,
            addBtn, saveBtn, uploadError, galleryCount;

    public GalleryDashboardPage(UtilLayer<?> util) {
        this.util = util;
        Page page = BrowserManager.getPage();
        uploadInput  = page.locator("input[type='file']").first();
        galleryItems = page.locator("[class*='gallery-item'], [class*='gallery-card']");
        deleteButtons= page.locator("[class*='gallery-item'] [class*='delete']");
        captionInputs= page.locator("[class*='caption'] input");
        addBtn       = page.locator("//button[contains(.,'Add Image')] | //button[contains(.,'Upload')]").first();
        saveBtn      = page.locator("//button[normalize-space()='Save']").first();
        uploadError  = page.locator("[class*='upload-error']").first();
        galleryCount = page.locator("[class*='gallery-count'], [class*='image-count']").first();
    }

    public void uploadImage(String path) { uploadInput.setInputFiles(Paths.get(path)); }
    public int getImageCount()           { return (int) galleryItems.count(); }
    public void deleteImage(int idx)     { deleteButtons.nth(idx).click(); }
    public void saveGallery()            { util.click(saveBtn, "Save Gallery"); }
    public boolean isUploadError()       { try { return uploadError.isVisible(); } catch (Exception e) { return false; } }
}
