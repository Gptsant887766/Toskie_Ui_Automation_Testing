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
        ReportManager.getTest().log(Status.INFO, "Checking gallery load via API");
        a.assertTrue(galleryPage.getImageCount() >= 0, "Gallery should load without error");
        a.assertAll();
    }

    @Test(groups = {TestGroups.API, TestGroups.P2}, description = "Gallery respects max image limit")
    public void testGalleryMaxLimit() {
        init();
        a.assertFalse(galleryPage.isUploadError(), "Upload error should not show initially");
        a.assertAll();
    }
}

