package com.toskie.tests.posts;
import com.aventstack.extentreports.Status;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.constants.TestGroups;
import com.toskie.pages.posts.AddPostModal;
import com.toskie.utils.AssertionHelper;
import org.testng.annotations.Test;

public class AddPostTests extends BaseTest {
    private AddPostModal addPost;
    private AssertionHelper a;
    private void init() { addPost = new AddPostModal(utilLayer); a = new AssertionHelper(); }

    @Test(groups = {TestGroups.SMOKE, TestGroups.P0}, description = "Add post modal opens on click")
    public void testAddPostModalOpens() { init(); addPost.clickAddPost(); a.assertTrue(addPost.isModalVisible(), "Add post modal should open"); a.assertAll(); }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P1}, description = "Text post option is available")
    public void testTextPostOptionVisible() { init(); addPost.clickAddPost(); a.assertTrue(addPost.isTextPostOptionVisible(), "Text post option should be visible"); a.assertAll(); }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P1}, description = "Image post option is available")
    public void testImagePostOptionVisible() { init(); addPost.clickAddPost(); a.assertTrue(addPost.isImagePostOptionVisible(), "Image post option should be visible"); a.assertAll(); }
}
