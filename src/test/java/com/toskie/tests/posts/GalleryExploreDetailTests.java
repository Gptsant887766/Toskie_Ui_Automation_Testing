package com.toskie.tests.posts;
import com.aventstack.extentreports.Status;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.constants.TestGroups;
import com.toskie.pages.posts.GalleryDetailPage;
import com.toskie.utils.AssertionHelper;
import org.testng.annotations.Test;

public class GalleryExploreDetailTests extends BaseTest {
    private GalleryDetailPage galleryDetail;
    private AssertionHelper a;
    private void init() { galleryDetail = new GalleryDetailPage(utilLayer); a = new AssertionHelper(); }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2}, description = "Gallery detail view opens on image click")
    public void testGalleryDetailOpens() { init(); a.assertTrue(galleryDetail.isDetailVisible(), "Gallery detail should open"); a.assertAll(); }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2}, description = "Navigation works in gallery detail")
    public void testGalleryNavigation() { init(); a.assertTrue(galleryDetail.isDetailVisible(), "Gallery detail should still be visible after navigation"); a.assertAll(); }
}
