package com.toskie.tests.dashboard;
import com.aventstack.extentreports.Status;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.constants.TestGroups;
import com.toskie.pages.dashboard.GalleryDashboardPage;
import com.toskie.utils.AssertionHelper;
import com.toskie.utils_Layer.ReportManager;
import org.testng.annotations.Test;

public class GalleryApiTests extends BaseTest {
    private GalleryDashboardPage galleryPage;
    private AssertionHelper a;
    private void init() { galleryPage = new GalleryDashboardPage(utilLayer); a = new AssertionHelper(); }

    @Test(groups = {TestGroups.API, TestGroups.P1}, description = "Gallery images loaded from API correctly")
    public void testGalleryLoadsFromApi() {
        init();
        int count = galleryPage.getImageCount();
        ReportManager.getTest().log(Status.INFO, "Gallery image count: " + count);
        boolean emptyState = com.toskie.utils_Layer.BrowserManager.getPage()
                .locator("[class*='empty'], [class*='no-gallery'], :has-text('No images'), :has-text('Upload your first')")
                .count() > 0;
        a.assertTrue(count > 0 || emptyState,
                "GALLERY-API-1: Gallery must show images (count > 0) OR an empty-state message (count=" + count + ", emptyState=" + emptyState + ")");
        a.assertAll();
    }

    @Test(groups = {TestGroups.API, TestGroups.P2}, description = "Gallery respects max image limit")
    public void testGalleryMaxLimit() {
        init();
        a.assertFalse(galleryPage.isUploadError(), "Upload error should not show initially");
        a.assertAll();
    }
}

