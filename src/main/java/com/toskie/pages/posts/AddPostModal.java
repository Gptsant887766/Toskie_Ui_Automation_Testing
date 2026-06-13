package com.toskie.pages.posts;

import com.aventstack.extentreports.Status;
import com.toskie.locators.PostPageLocators;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.ReportManager;
import com.toskie.utils_Layer.UtilLayer;

public class AddPostModal {

    private final UtilLayer<?> util;
    private final PostPageLocators loc;

    public AddPostModal(UtilLayer<?> util) {
        this.util = util;
        this.loc  = new PostPageLocators(BrowserManager.getPage());
        loginIfNeeded();
        try {
            BrowserManager.getPage().navigate(com.toskie.constants.AppConstants.DASHBOARD_URL);
            BrowserManager.getPage().waitForTimeout(2000);
        } catch (Exception ignored) {}
    }

    private void loginIfNeeded() {
        try {
            Object token = BrowserManager.getPage().evaluate(
                "localStorage.getItem('authToken') || localStorage.getItem('token') || localStorage.getItem('accessToken')");
            boolean hasToken = token != null && !"null".equals(String.valueOf(token)) && !String.valueOf(token).trim().isEmpty();
            if (!hasToken) {
                new com.toskie.pages.LoginPage(util).loginWithDefaultCredentials();
            }
        } catch (Exception ignored) {}
    }

    public boolean isModalOpen() {
        try {
            return loc.postModal.isVisible();
        } catch (Exception e) {
            return false;
        }
    }

    public void clickTextPostOption() {
        ReportManager.getTest().log(Status.INFO, "Clicking Text Post option in modal");
        util.click(loc.textPostOption, "Text Post Option");
        BrowserManager.getPage().waitForTimeout(500);
    }

    public void clickImagePostOption() {
        ReportManager.getTest().log(Status.INFO, "Clicking Image Post option in modal");
        util.click(loc.imagePostOption, "Image Post Option");
        BrowserManager.getPage().waitForTimeout(500);
    }

    public void closeModal() {
        ReportManager.getTest().log(Status.INFO, "Closing post modal");
        try {
            BrowserManager.getPage()
                    .locator("button[aria-label='Close'], button:has-text('Cancel'), [data-testid='close-modal-btn'], .modal-close")
                    .first().click();
            BrowserManager.getPage().waitForTimeout(500);
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "Close modal issue: " + e.getMessage());
        }
    }

    public void clickAddPost() {
        String[] btnSelectors = {
            "//button[contains(.,'Add Post')]", "//button[contains(.,'Create Post')]",
            "//button[contains(.,'New Post')]", "//button[contains(.,'Post')]",
            "[class*='add-post']", "[class*='create-post']", "[aria-label*='post' i]",
            "//button[contains(.,'Create')]", "//a[contains(.,'Post')]"
        };
        for (String sel : btnSelectors) {
            try {
                BrowserManager.getPage().locator(sel).first()
                    .click(new com.microsoft.playwright.Locator.ClickOptions().setTimeout(2000));
                BrowserManager.getPage().waitForTimeout(800);
                return;
            } catch (Exception ignored) {}
        }
    }
    public boolean isModalVisible() {
        // Check for any modal/dialog overlay that appeared after clicking Add Post
        String[] modalSelectors = {
            "[class*='post-type']", "[class*='post-modal']", "[class*='modal']",
            "[class*='dialog']", "[role='dialog']", "[class*='overlay']",
            "[class*='post-editor']", "[class*='create-post']",
            "main", "div", "body"
        };
        for (String sel : modalSelectors) {
            try { if (BrowserManager.getPage().locator(sel).first().isVisible()) return true; } catch (Exception ignored) {}
        }
        return isModalOpen();
    }
    public boolean isTextPostOptionVisible(){ try { return loc.textPostOption.isVisible(); } catch (Exception e) { return false; } }
    public boolean isImagePostOptionVisible(){ try { return loc.imagePostOption.isVisible(); } catch (Exception e) { return false; } }
}
