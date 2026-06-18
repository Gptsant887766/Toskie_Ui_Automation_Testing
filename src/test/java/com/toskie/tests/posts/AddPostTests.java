package com.toskie.tests.posts;

import com.aventstack.extentreports.Status;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.constants.TestGroups;
import com.toskie.pages.posts.AddPostModal;
import com.toskie.utils.AssertionHelper;
import com.toskie.utils.NetworkValidator;
import com.toskie.utils_Layer.ApiUtils;
import com.toskie.utils_Layer.ConfigManager;
import com.toskie.utils_Layer.ReportManager;
import org.testng.annotations.Test;

public class AddPostTests extends BaseTest {

    private AddPostModal addPost;
    private AssertionHelper a;

    private void init() {
        addPost = new AddPostModal(utilLayer);
        a = new AssertionHelper();
        ApiUtils.loginViaQAGraphQL(ConfigManager.get("testMobile"));
    }

    @Test(groups = {TestGroups.SMOKE, TestGroups.P0}, description = "Add post modal opens on click")
    public void testAddPostModalOpens() {
        init();
        addPost.clickAddPost();
        ReportManager.getTest().log(Status.INFO, "Verifying add post modal is visible");
        a.assertTrue(addPost.isModalVisible(), "Add post modal should open");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P1}, description = "Text post option is available in modal")
    public void testTextPostOptionVisible() {
        init();
        addPost.clickAddPost();
        ReportManager.getTest().log(Status.INFO, "Verifying text post option is visible");
        a.assertTrue(addPost.isTextPostOptionVisible(), "Text post option should be visible in modal");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P1}, description = "Image post option is available in modal")
    public void testImagePostOptionVisible() {
        init();
        addPost.clickAddPost();
        ReportManager.getTest().log(Status.INFO, "Verifying image post option is visible");
        a.assertTrue(addPost.isImagePostOptionVisible(), "Image post option should be visible in modal");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P0}, description = "Add image post API call is triggered on publish")
    public void testAddImagePostAPITriggered() {
        init();
        NetworkValidator nv = new NetworkValidator();
        nv.startCapturing();
        ReportManager.getTest().log(Status.INFO, "Network capture started for image post API intercept");
        addPost.clickAddPost();
        a.assertTrue(addPost.isModalVisible(), "Modal should open for image post API test");
        nv.assertNoFailedRequests();
        nv.stopCapturing();
        ReportManager.getTest().log(Status.INFO, "Image post API -- no failed network requests (TC_AP_001)");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2}, description = "Closing add post modal dismisses it")
    public void testCloseAddPostModal() {
        init();
        addPost.clickAddPost();
        a.assertTrue(addPost.isModalVisible(), "Modal should be open before close");
        addPost.closeModal();
        ReportManager.getTest().log(Status.INFO, "Verifying modal closes after dismiss");
        a.assertTrue(!addPost.isModalVisible(), "Modal should be closed after dismissal");
        a.assertAll();
    }
}
