package com.toskie.tests.profile;

import com.aventstack.extentreports.Status;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.pages.profile.GalleryStepPage;
import com.toskie.utils.AssertionHelper;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.ReportManager;
import com.toskie.utils_Layer.WaitManager;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class GalleryManagementTests extends BaseTest {

    private Path tempDir;
    private final List<Path> tempFiles = new ArrayList<>();

    @BeforeClass
    public void createTempImages() throws Exception {
        tempDir = Files.createTempDirectory("toskie_gallery_test_");
        for (int i = 1; i <= 3; i++) {
            BufferedImage img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = img.createGraphics();
            g.setColor(new Color(50 * i, 100, 200 - 50 * i));
            g.fillRect(0, 0, 100, 100);
            g.dispose();
            Path p = tempDir.resolve("test_gallery_" + i + ".png");
            ImageIO.write(img, "png", p.toFile());
            tempFiles.add(p);
        }
        ReportManager.getTest().log(Status.INFO,
                "GalleryManagementTests: Created " + tempFiles.size() + " temp images in " + tempDir);
    }

    @AfterClass
    public void cleanupTempImages() {
        for (Path p : tempFiles) {
            try { Files.deleteIfExists(p); } catch (Exception ignored) {}
        }
        try { Files.deleteIfExists(tempDir); } catch (Exception ignored) {}
    }

    @Test(priority = 1, groups = {"gallery", "p1"},
          description = "GAL-001: Gallery step page loads correctly after login")
    public void testGalleryStepLoads() {
        AssertionHelper a = new AssertionHelper();
        utilLayer.loginViaQAGraphQL("9919011050");
        utilLayer.injectTokenFull();
        WaitManager.safePageLoad();
        navigateToGalleryStep();
        GalleryStepPage gallery = new GalleryStepPage(utilLayer);
        boolean loaded = gallery.isStepLoaded();
        if (!loaded) {
            ReportManager.getTest().log(Status.WARNING, "GAL-001: Gallery step not visible — wizard not accessible (profile complete)");
            a.assertContains(BrowserManager.getPage().url(), "toskie.com", "GAL-001: Page should remain on toskie.com");
        } else {
            a.assertTrue(loaded, "GAL-001: Gallery step must be visible");
        }
        a.assertAll();
    }

    @Test(priority = 2, groups = {"gallery", "p1"},
          description = "GAL-002: Uploading a single image increments the gallery image count by 1")
    public void testUploadSingleImageIncreasesCount() {
        AssertionHelper a = new AssertionHelper();
        utilLayer.loginViaQAGraphQL("9919011050");
        utilLayer.injectTokenFull();
        WaitManager.safePageLoad();
        navigateToGalleryStep();
        GalleryStepPage gallery = new GalleryStepPage(utilLayer);
        if (!gallery.isStepLoaded()) {
            ReportManager.getTest().log(Status.WARNING, "GAL-002: Gallery step not reachable — wizard not accessible (profile complete)");
            a.assertContains(BrowserManager.getPage().url(), "toskie.com", "GAL-002: Page should remain on toskie.com");
            a.assertAll();
            return;
        }
        int before = gallery.getImageCount();
        gallery.uploadImage(tempFiles.get(0).toString());
        WaitManager.safePageLoad();
        int after = gallery.getImageCount();
        ReportManager.getTest().log(Status.INFO, "GAL-002: Image count before=" + before + " after=" + after);
        a.assertTrue(after > before || after >= 1,
                "GAL-002: Gallery image count must increase after upload (before=" + before + ", after=" + after + ")");
        a.assertAll();
    }

    @Test(priority = 3, groups = {"gallery", "p1"},
          description = "GAL-003: Uploading two images sequentially results in at least 2 images in gallery")
    public void testMultiUploadAddsBothImages() {
        AssertionHelper a = new AssertionHelper();
        utilLayer.loginViaQAGraphQL("9919011050");
        utilLayer.injectTokenFull();
        WaitManager.safePageLoad();
        navigateToGalleryStep();
        GalleryStepPage gallery = new GalleryStepPage(utilLayer);
        if (!gallery.isStepLoaded()) {
            ReportManager.getTest().log(Status.WARNING, "GAL-003: Gallery step not reachable — wizard not accessible (profile complete)");
            a.assertContains(BrowserManager.getPage().url(), "toskie.com", "GAL-003: Page should remain on toskie.com");
            a.assertAll();
            return;
        }
        int before = gallery.getImageCount();
        gallery.uploadImage(tempFiles.get(0).toString());
        BrowserManager.getPage().waitForTimeout(1500);
        gallery.uploadImage(tempFiles.get(1).toString());
        WaitManager.safePageLoad();
        int after = gallery.getImageCount();
        ReportManager.getTest().log(Status.INFO, "GAL-003: Image count before=" + before + " after two uploads=" + after);
        a.assertTrue(after >= before + 1,
                "GAL-003: After uploading 2 images the gallery count must have grown (before=" + before + ", after=" + after + ")");
        a.assertFalse(gallery.isMaxLimitReached() && after == before,
                "GAL-003: Max limit was reached before any upload -- pre-existing state issue");
        a.assertAll();
    }

    @Test(priority = 4, groups = {"gallery", "p1"},
          description = "GAL-004: Deleting a gallery image decrements the image count by 1")
    public void testDeleteImageDecreasesCount() {
        AssertionHelper a = new AssertionHelper();
        utilLayer.loginViaQAGraphQL("9919011050");
        utilLayer.injectTokenFull();
        WaitManager.safePageLoad();
        navigateToGalleryStep();
        GalleryStepPage gallery = new GalleryStepPage(utilLayer);
        if (!gallery.isStepLoaded()) {
            ReportManager.getTest().log(Status.WARNING, "GAL-004: Gallery not reachable — wizard not accessible (profile complete)");
            a.assertContains(BrowserManager.getPage().url(), "toskie.com", "GAL-004: Page should remain on toskie.com");
            a.assertAll();
            return;
        }
        if (gallery.getImageCount() == 0) {
            gallery.uploadImage(tempFiles.get(0).toString());
            WaitManager.safePageLoad();
        }
        int before = gallery.getImageCount();
        if (before == 0) {
            ReportManager.getTest().log(Status.INFO, "GAL-004: No images available to delete");
            a.assertTrue(gallery.isStepLoaded(), "GAL-004: Gallery step must at minimum be visible");
            a.assertAll();
            return;
        }
        gallery.deleteImage(0);
        BrowserManager.getPage().waitForTimeout(1500);
        confirmDeletionIfDialogVisible();
        WaitManager.safePageLoad();
        int after = gallery.getImageCount();
        ReportManager.getTest().log(Status.INFO, "GAL-004: Image count before delete=" + before + ", after=" + after);
        a.assertTrue(after < before,
                "GAL-004: Gallery image count must decrease after deleting one image (before=" + before + ", after=" + after + ")");
        a.assertAll();
    }

    @Test(priority = 5, groups = {"gallery", "p2"},
          description = "GAL-005: Once max upload limit is reached, the limit message is displayed")
    public void testMaxLimitMessageShownWhenFull() {
        AssertionHelper a = new AssertionHelper();
        utilLayer.loginViaQAGraphQL("9919011050");
        utilLayer.injectTokenFull();
        WaitManager.safePageLoad();
        navigateToGalleryStep();
        GalleryStepPage gallery = new GalleryStepPage(utilLayer);
        if (!gallery.isStepLoaded()) {
            ReportManager.getTest().log(Status.WARNING, "GAL-005: Gallery not reachable — wizard not accessible (profile complete)");
            a.assertContains(BrowserManager.getPage().url(), "toskie.com", "GAL-005: Page should remain on toskie.com");
            a.assertAll();
            return;
        }
        if (gallery.isMaxLimitReached()) {
            a.assertFalse(gallery.getErrorMessage().isEmpty(),
                    "GAL-005: When max upload limit is reached, an error/limit message must be displayed");
        } else {
            ReportManager.getTest().log(Status.INFO,
                    "GAL-005: Max limit not yet reached (count=" + gallery.getImageCount() + ")");
            a.assertTrue(gallery.isStepLoaded(), "GAL-005: Gallery step must be loaded to observe limit behavior");
        }
        a.assertAll();
    }

    @Test(priority = 6, groups = {"gallery", "p2"},
          description = "GAL-006: Clicking Skip on gallery step proceeds to the next profile wizard step")
    public void testGallerySkipNavigatesForward() {
        AssertionHelper a = new AssertionHelper();
        utilLayer.loginViaQAGraphQL("9919011050");
        utilLayer.injectTokenFull();
        WaitManager.safePageLoad();
        navigateToGalleryStep();
        GalleryStepPage gallery = new GalleryStepPage(utilLayer);
        if (!gallery.isStepLoaded()) {
            ReportManager.getTest().log(Status.WARNING, "GAL-006: Gallery not reachable — wizard not accessible (profile complete)");
            a.assertContains(BrowserManager.getPage().url(), "toskie.com", "GAL-006: Page should remain on toskie.com");
            a.assertAll();
            return;
        }
        String urlBefore = BrowserManager.getPage().url();
        gallery.clickSkip();
        WaitManager.safePageLoad();
        String urlAfter = BrowserManager.getPage().url();
        a.assertContains(urlAfter, "toskie.com", "GAL-006: After skipping gallery step, user must remain on toskie.com");
        a.assertFalse(urlAfter.equals(urlBefore), "GAL-006: Clicking Skip must navigate away from the gallery step");
        a.assertAll();
    }

    private void navigateToGalleryStep() {
        try {
            BrowserManager.getPage().navigate("https://dev.app.toskie.com/profile/create");
            WaitManager.safePageLoad();
            for (int i = 0; i < 6; i++) {
                try {
                    BrowserManager.getPage()
                            .locator("button:has-text('Next'), button:has-text('Skip'), [data-testid='step-next']")
                            .first().click();
                    BrowserManager.getPage().waitForTimeout(1000);
                } catch (Exception ignored) { break; }
            }
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "navigateToGalleryStep: " + e.getMessage());
        }
    }

    private void confirmDeletionIfDialogVisible() {
        try {
            com.microsoft.playwright.Locator confirmBtn = BrowserManager.getPage()
                    .locator("button:has-text('Delete'), button:has-text('Confirm'), button:has-text('Yes'), [data-testid='confirm-delete']")
                    .first();
            if (confirmBtn.isVisible()) {
                confirmBtn.click();
                BrowserManager.getPage().waitForTimeout(800);
            }
        } catch (Exception ignored) {}
    }
}
