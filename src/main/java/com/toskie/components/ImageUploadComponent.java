package com.toskie.components;
import com.microsoft.playwright.*;
import com.toskie.utils_Layer.*;
import java.nio.file.Paths;

public class ImageUploadComponent {
    private final UtilLayer<?> util;
    private final Locator uploadInput, uploadArea, preview, removeBtn, errorMsg;

    public ImageUploadComponent(UtilLayer<?> util) {
        this.util = util;
        Page page = BrowserManager.getPage();
        uploadInput = page.locator("input[type='file']").first();
        uploadArea  = page.locator("[class*='upload-area'], [class*='dropzone'], [class*='image-upload']").first();
        preview     = page.locator("[class*='preview'] img, [class*='image-preview']").first();
        removeBtn   = page.locator("[class*='remove'], [class*='delete-image'], [aria-label*='remove' i]").first();
        errorMsg    = page.locator("[class*='error']:has-text('image'), [class*='upload-error']").first();
    }

    public void uploadImage(String path) { uploadInput.setInputFiles(Paths.get(path)); }
    public void removeImage()            { util.click(removeBtn, "Remove Image"); }
    public boolean isPreviewVisible()    { try { return preview.isVisible(); } catch (Exception e) { return false; } }
    public String getPreviewSrc()        { try { return preview.getAttribute("src"); } catch (Exception e) { return ""; } }
    public String getErrorMessage()      { try { return errorMsg.textContent().trim(); } catch (Exception e) { return ""; } }
}
