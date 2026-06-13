package com.toskie.tests.posts;
import com.aventstack.extentreports.Status;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.constants.TestGroups;
import com.toskie.pages.posts.AddTextPostPage;
import com.toskie.utils.AssertionHelper;
import org.testng.annotations.Test;

public class TextPostTests extends BaseTest {
    private AddTextPostPage textPost;
    private AssertionHelper a;
    private void init() { textPost = new AddTextPostPage(utilLayer); a = new AssertionHelper(); }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P1}, description = "Publish a valid text post")
    public void testPublishTextPost() {
        init();
        textPost.enterText("This is a test post " + System.currentTimeMillis());
        textPost.publish();
        a.assertTrue(textPost.isPostPublished(), "Text post should be published");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2}, description = "Submit empty text post shows validation error")
    public void testEmptyTextPostValidation() {
        init();
        textPost.publish();
        a.assertNotEmpty(textPost.getValidationError(), "Validation error should appear for empty post");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P3}, description = "Character limit is enforced on text post")
    public void testCharacterLimit() {
        init();
        String longText = "A".repeat(5001);
        textPost.enterText(longText);
        a.assertNotEmpty(textPost.getCharLimitError(), "Char limit error should show");
        a.assertAll();
    }
}
