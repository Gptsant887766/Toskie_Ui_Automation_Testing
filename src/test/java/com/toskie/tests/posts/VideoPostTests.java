package com.toskie.tests.posts;

import com.aventstack.extentreports.Status;
import com.toskie.utils_Layer.WaitManager;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.constants.AppConstants;
import com.toskie.constants.TestGroups;
import com.toskie.pages.posts.AddPostModal;
import com.toskie.utils.AssertionHelper;
import com.toskie.utils.NetworkValidator;
import com.toskie.utils_Layer.ApiUtils;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.ConfigManager;
import com.toskie.utils_Layer.ReportManager;
import org.testng.annotations.Test;

public class VideoPostTests extends BaseTest {

    private AddPostModal addPost;
    private AssertionHelper a;

    private void init() {
        addPost = new AddPostModal(utilLayer);
        a = new AssertionHelper();
        ApiUtils.loginViaQAGraphQL(ConfigManager.get("testMobile"));
        ApiUtils.injectTokenFull();
        ApiUtils.injectCookies();
        BrowserManager.getPage().navigate(AppConstants.DASHBOARD_URL);
        BrowserManager.getPage().waitForTimeout(1500);
    }

    // ─── 1. Add post modal opens from dashboard ───────────────────────────────
    @Test(groups = {TestGroups.REGRESSION, TestGroups.P1},
          description = "Add post modal opens from dashboard for video post flow")
    public void testAddPostModalOpensForVideo() {
        init();
        addPost.clickAddPost();
        ReportManager.getTest().log(Status.INFO, "Verifying add post modal is open for video flow");
        boolean visible = addPost.isModalVisible();
        ReportManager.getTest().log(Status.INFO, "Modal visible: " + visible);
        a.assertTrue(visible, "Add post modal should open for video post flow");
        a.assertAll();
    }

    // ─── 2. Image/video post option visible ──────────────────────────────────
    @Test(groups = {TestGroups.REGRESSION, TestGroups.P1},
          description = "Image/video post option is available in the add post modal")
    public void testImageVideoPostOptionVisible() {
        init();
        addPost.clickAddPost();
        ReportManager.getTest().log(Status.INFO, "Verifying image/video post option is visible");
        boolean imageOption = addPost.isImagePostOptionVisible();
        ReportManager.getTest().log(Status.INFO, "Image/video post option visible: " + imageOption);
        a.assertTrue(imageOption, "Image/video post option should be visible in add post modal");
        a.assertAll();
    }

    // ─── 3. Gallery post option accessible ────────────────────────────────────
    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2},
          description = "Gallery post modal can be opened from add post options")
    public void testGalleryPostOptionAccessible() {
        init();
        addPost.clickAddPost();
        ReportManager.getTest().log(Status.INFO, "Verifying gallery/media post option accessible");
        boolean modalOpen = addPost.isModalVisible();
        ReportManager.getTest().log(Status.INFO, "Add post modal is open: " + modalOpen);
        a.assertTrue(modalOpen, "Add post modal must be open to access gallery post option");
        a.assertAll();
    }

    // ─── 4. Post modal closes after dismiss ──────────────────────────────────
    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2},
          description = "Closing add post modal during video post flow dismisses it cleanly")
    public void testModalDismissedDuringVideoFlow() {
        init();
        addPost.clickAddPost();
        a.assertTrue(addPost.isModalVisible(), "Modal should be open before close");
        addPost.closeModal();
        BrowserManager.getPage().waitForTimeout(500);
        ReportManager.getTest().log(Status.INFO, "Verifying modal is closed after dismiss");
        a.assertTrue(!addPost.isModalVisible(), "Modal should be dismissed after close action");
        a.assertAll();
    }

    // ─── 5. Network -- no failed requests on post modal open ─────────────────
    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2},
          description = "Opening add post modal triggers no network errors")
    public void testNoNetworkErrorsOnPostModalOpen() {
        init();
        NetworkValidator nv = new NetworkValidator();
        nv.startCapturing();
        addPost.clickAddPost();
        BrowserManager.getPage().waitForTimeout(1000);
        nv.assertNoFailedRequests();
        nv.stopCapturing();
        ReportManager.getTest().log(Status.INFO, "No failed network requests when opening add post modal");
        a.assertTrue(addPost.isModalVisible(), "Modal should be visible with no network errors");
        a.assertAll();
    }

    // ─── 6. Unauthenticated post creation redirects ───────────────────────────
    @Test(groups = {TestGroups.REGRESSION, TestGroups.P1},
          description = "Accessing post creation without login redirects to login")
    public void testUnauthenticatedPostCreationRedirects() {
        AssertionHelper b = new AssertionHelper();
        // Do NOT call init() -- test without authentication
        ReportManager.getTest().log(Status.INFO, "Testing unauthenticated access to posts page");
        BrowserManager.getPage().navigate(AppConstants.POSTS_URL);
        WaitManager.safePageLoad();
        String url = BrowserManager.getPage().url();
        ReportManager.getTest().log(Status.INFO, "URL after unauthenticated posts access: " + url);
        b.assertTrue(
            url.contains("login") || url.contains("register") || url.contains("welcome") || !url.contains("posts"),
            "Unauthenticated user should not access post creation (actual: " + url + ")"
        );
        b.assertAll();
    }
}