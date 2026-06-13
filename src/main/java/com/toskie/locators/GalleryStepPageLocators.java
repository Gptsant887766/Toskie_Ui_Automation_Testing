package com.toskie.locators;
import com.microsoft.playwright.*;

public class GalleryStepPageLocators {
    public final Locator uploadInput, uploadArea, galleryItems, deleteButtons, captionInputs,
            addMoreBtn, saveBtn, nextBtn, prevBtn, uploadError, maxLimitMsg,
            imageItems, skipButton, nextButton, galleryGrid, addImageButton, errorMessage;

    public GalleryStepPageLocators(Page page) {
        uploadInput  = page.locator("input[type='file']");
        uploadArea   = page.locator("[class*='upload'], [class*='dropzone']").first();
        galleryItems = page.locator("[class*='gallery-item'], [class*='gallery-card']");
        deleteButtons= page.locator("[class*='gallery-item'] [class*='delete'], [class*='remove-image']");
        captionInputs= page.locator("[class*='caption'] input, input[placeholder*='caption' i]");
        addMoreBtn   = page.locator("//button[contains(.,'Add More')] | //button[contains(.,'Upload')]").first();
        saveBtn      = page.locator("//button[normalize-space()='Save']").first();
        nextBtn      = page.locator("//button[normalize-space()='Next'] | //button[contains(.,'Continue')]").first();
        prevBtn      = page.locator("//button[normalize-space()='Back'] | //button[normalize-space()='Previous']").first();
        uploadError  = page.locator("[class*='upload-error'], [class*='error']:has-text('image')").first();
        maxLimitMsg    = page.locator("[class*='limit'], [class*='max']:has-text('image')").first();
        imageItems     = galleryItems;
        skipButton     = page.locator("//button[normalize-space()='Skip'] | //button[contains(.,'Skip')]").first();
        nextButton     = nextBtn;
        galleryGrid    = page.locator("[class*='gallery-grid'], [class*='gallery-container'], [class*='image-grid']").first();
        addImageButton = addMoreBtn;
        errorMessage   = uploadError;
    }
}
