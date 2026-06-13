package com.toskie.locators;
import com.microsoft.playwright.*;

public class PostPageLocators {
    public final Locator addPostBtn, postTypeModal, textPostOption, imagePostOption,
            postTextarea, postImageUpload, publishBtn, cancelBtn, postCards, likeBtn,
            bookmarkBtn, shareBtn, commentInput, commentBtn, postAuthor, postContent,
            postImages, postLikeCount, postShareCount, feedPosts, exploreSection,
            postModal, postInput, backgroundColorOptions, aiGenerateBtn, postCard,
            editPostBtn, deletePostBtn;

    public PostPageLocators(Page page) {
        addPostBtn      = page.locator("//button[contains(.,'Add Post')] | //button[contains(.,'Create Post')]").first();
        postTypeModal   = page.locator("[class*='post-type'], [class*='post-modal']").first();
        textPostOption  = page.locator("//button[contains(.,'Text')] | [class*='text-post']").first();
        imagePostOption = page.locator("//button[contains(.,'Image')] | [class*='image-post']").first();
        postTextarea    = page.locator("textarea[placeholder*='post' i], textarea[placeholder*='write' i]").first();
        postImageUpload = page.locator("input[type='file']").first();
        publishBtn      = page.locator("//button[contains(.,'Publish')] | //button[normalize-space()='Post']").first();
        cancelBtn       = page.locator("//button[normalize-space()='Cancel']").first();
        postCards       = page.locator("[class*='post-card'], [class*='feed-post']");
        likeBtn         = page.locator("[class*='like-btn'], [aria-label*='like' i]").first();
        bookmarkBtn     = page.locator("[class*='bookmark-btn'], [aria-label*='bookmark' i]").first();
        shareBtn        = page.locator("[class*='share-btn'], [aria-label*='share' i]").first();
        commentInput    = page.locator("input[placeholder*='comment' i], textarea[placeholder*='comment' i]").first();
        commentBtn      = page.locator("//button[normalize-space()='Comment'] | //button[contains(.,'Send')]").first();
        postAuthor      = page.locator("[class*='post-author'], [class*='post-user']").first();
        postContent     = page.locator("[class*='post-content'], [class*='post-body']").first();
        postImages      = page.locator("[class*='post-image'] img, [class*='post-media'] img");
        postLikeCount  = page.locator("[class*='like-count']").first();
        postShareCount = page.locator("[class*='share-count']").first();
        feedPosts      = page.locator("[class*='feed'], [class*='post-list']").first();
        exploreSection         = page.locator("[class*='explore'], [class*='discover']").first();
        postModal              = postTypeModal;
        postInput              = postTextarea;
        backgroundColorOptions = page.locator("[class*='color-picker'], [class*='bg-color'], [class*='background-color']");
        aiGenerateBtn          = page.locator("//button[contains(.,'AI')] | //button[contains(.,'Generate')]").first();
        postCard               = postCards.first();
        editPostBtn            = page.locator("//button[contains(.,'Edit Post')] | [class*='edit-post']").first();
        deletePostBtn          = page.locator("//button[contains(.,'Delete Post')] | [class*='delete-post']").first();
    }
}
