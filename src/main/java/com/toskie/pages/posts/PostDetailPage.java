package com.toskie.pages.posts;

import com.aventstack.extentreports.Status;
import com.toskie.locators.PostPageLocators;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.ReportManager;
import com.toskie.utils_Layer.UtilLayer;

public class PostDetailPage {

    private final UtilLayer<?> util;
    private final PostPageLocators loc;

    public PostDetailPage(UtilLayer<?> util) {
        this.util = util;
        this.loc  = new PostPageLocators(BrowserManager.getPage());
    }

    public boolean isLoaded() {
        try {
            return loc.postCard.isVisible() || BrowserManager.getPage()
                    .locator("[data-testid='post-detail'], .post-detail, .post-view")
                    .isVisible();
        } catch (Exception e) {
            return false;
        }
    }

    public String getPostContent() {
        ReportManager.getTest().log(Status.INFO, "Getting post content text");
        try {
            return BrowserManager.getPage()
                    .locator("[data-testid='post-content'], .post-content, .post-text, p.post-body")
                    .first().textContent().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public void likePost() {
        ReportManager.getTest().log(Status.INFO, "Liking the post");
        try {
            BrowserManager.getPage()
                    .locator("button[aria-label='Like'], button:has-text('Like'), [data-testid='like-btn']")
                    .first().click();
            BrowserManager.getPage().waitForTimeout(500);
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "Like post issue: " + e.getMessage());
        }
    }

    public void bookmarkPost() {
        ReportManager.getTest().log(Status.INFO, "Bookmarking the post");
        try {
            BrowserManager.getPage()
                    .locator("button[aria-label='Bookmark'], button:has-text('Save'), [data-testid='bookmark-btn']")
                    .first().click();
            BrowserManager.getPage().waitForTimeout(500);
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "Bookmark post issue: " + e.getMessage());
        }
    }

    public void sharePost() {
        ReportManager.getTest().log(Status.INFO, "Sharing the post");
        try {
            BrowserManager.getPage()
                    .locator("button[aria-label='Share'], button:has-text('Share'), [data-testid='share-btn']")
                    .first().click();
            BrowserManager.getPage().waitForTimeout(500);
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "Share post issue: " + e.getMessage());
        }
    }

    public int getCommentCount() {
        ReportManager.getTest().log(Status.INFO, "Getting post comment count");
        try {
            String text = BrowserManager.getPage()
                    .locator("[data-testid='comment-count'], .comment-count, span.comments")
                    .first().textContent().trim();
            return Integer.parseInt(text.replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            return 0;
        }
    }

    public int getLikeCount() {
        ReportManager.getTest().log(Status.INFO, "Getting post like count");
        try {
            String text = BrowserManager.getPage()
                    .locator("[data-testid='like-count'], .like-count, span.likes")
                    .first().textContent().trim();
            return Integer.parseInt(text.replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean isEditVisible() {
        try {
            return loc.editPostBtn.isVisible();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isDeleteVisible() {
        try {
            return loc.deletePostBtn.isVisible();
        } catch (Exception e) {
            return false;
        }
    }

    public void clickEdit() {
        ReportManager.getTest().log(Status.INFO, "Clicking Edit on post detail");
        util.click(loc.editPostBtn, "Edit Post Button");
        BrowserManager.getPage().waitForTimeout(500);
    }

    public void clickDelete() {
        ReportManager.getTest().log(Status.INFO, "Clicking Delete on post detail");
        util.click(loc.deletePostBtn, "Delete Post Button");
        BrowserManager.getPage().waitForTimeout(500);
    }

    public void confirmDelete() {
        ReportManager.getTest().log(Status.INFO, "Confirming post deletion");
        try {
            BrowserManager.getPage()
                    .locator("button:has-text('Confirm'), button:has-text('Yes'), button:has-text('Delete'), [data-testid='confirm-delete-btn']")
                    .first().click();
            BrowserManager.getPage().waitForTimeout(1500);
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "Confirm delete issue: " + e.getMessage());
        }
    }

    public boolean isPostContentVisible() { return isLoaded(); }
    public boolean isCommentsVisible()    { try { return BrowserManager.getPage().locator("[class*='comment'], [class*='comments-section']").isVisible(); } catch (Exception e) { return false; } }
    public boolean isLiked()              { try { return BrowserManager.getPage().locator("[class*='like-btn'][class*='active'], [aria-pressed='true'][aria-label*='like' i]").isVisible(); } catch (Exception e) { return false; } }
    public boolean isBookmarked()         { try { return BrowserManager.getPage().locator("[class*='bookmark-btn'][class*='active'], [aria-pressed='true'][aria-label*='bookmark' i]").isVisible(); } catch (Exception e) { return false; } }
}
