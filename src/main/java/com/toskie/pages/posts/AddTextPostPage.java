package com.toskie.pages.posts;

import com.aventstack.extentreports.Status;
import com.microsoft.playwright.options.LoadState;
import com.toskie.locators.PostPageLocators;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.ReportManager;
import com.toskie.utils_Layer.UtilLayer;

public class AddTextPostPage {

    private final UtilLayer<?> util;
    private final PostPageLocators loc;

    public AddTextPostPage(UtilLayer<?> util) {
        this.util = util;
        this.loc  = new PostPageLocators(BrowserManager.getPage());
    }

    public void typeText(String text) {
        ReportManager.getTest().log(Status.INFO, "Typing post text");
        util.fill(loc.postInput, text, "Post Text Input");
    }

    public void selectBackgroundColor(String color) {
        ReportManager.getTest().log(Status.INFO, "Selecting background color: " + color);
        try {
            loc.backgroundColorOptions
                    .locator("[data-color='" + color + "'], [title='" + color + "'], button:has-text('" + color + "')")
                    .first().click();
            BrowserManager.getPage().waitForTimeout(500);
        } catch (Exception e) {
            try {
                loc.backgroundColorOptions.first().click();
            } catch (Exception ex) {
                ReportManager.getTest().log(Status.WARNING, "Background color selection issue: " + ex.getMessage());
            }
        }
    }

    public void clickPublish() {
        ReportManager.getTest().log(Status.INFO, "Clicking Publish for text post");
        util.click(loc.publishBtn, "Publish Button");
        try {
            BrowserManager.getPage().waitForLoadState(LoadState.DOMCONTENTLOADED,
                    new com.microsoft.playwright.Page.WaitForLoadStateOptions().setTimeout(10000));
        } catch (Exception ignored) {}
    }

    public int getCharCount() {
        ReportManager.getTest().log(Status.INFO, "Getting post char count");
        try {
            String text = BrowserManager.getPage()
                    .locator("[data-testid='char-counter'], .char-counter, .post-char-count")
                    .first().textContent().trim();
            String digits = text.replaceAll("[^0-9].*", "").trim();
            return Integer.parseInt(digits);
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean isCharLimitExceeded() {
        try {
            String cls = BrowserManager.getPage()
                    .locator("[data-testid='char-counter'], .char-counter, .post-char-count")
                    .first().getAttribute("class");
            return cls != null && (cls.contains("exceeded") || cls.contains("error") || cls.contains("limit"));
        } catch (Exception e) {
            return false;
        }
    }

    public void clickAIGenerate() {
        ReportManager.getTest().log(Status.INFO, "Clicking AI Generate for post");
        util.click(loc.aiGenerateBtn, "AI Generate Button");
    }

    public void waitForAIContent() {
        ReportManager.getTest().log(Status.INFO, "Waiting for AI post content generation");
        try {
            BrowserManager.getPage()
                    .locator("[data-testid='ai-spinner'], .ai-spinner, .loading-spinner")
                    .waitFor(new com.microsoft.playwright.Locator.WaitForOptions()
                            .setState(com.microsoft.playwright.options.WaitForSelectorState.HIDDEN)
                            .setTimeout(30000));
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "AI content wait issue: " + e.getMessage());
        }
        BrowserManager.getPage().waitForTimeout(500);
    }

    public boolean isPublishEnabled() {
        try {
            return loc.publishBtn.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    public void enterText(String t)      { typeText(t); }
    public void publish()                { clickPublish(); }
    public boolean isPostPublished()     { try { return BrowserManager.getPage().locator("[class*='success'], [class*='post-published']").isVisible(); } catch (Exception e) { return false; } }
    public String getValidationError()   { try { return BrowserManager.getPage().locator("[class*='error']:has-text('post'), [class*='post-error']").textContent().trim(); } catch (Exception e) { return ""; } }
    public String getCharLimitError()    { return isCharLimitExceeded() ? "Character limit exceeded" : ""; }
}
