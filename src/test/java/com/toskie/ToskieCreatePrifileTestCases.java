package com.toskie;

import org.testng.annotations.Test;

import com.microsoft.playwright.options.LoadState;
import com.toskie.AuthenticationPages.Page.ToskieCreateProfile;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.ConfigManager;

public class ToskieCreatePrifileTestCases extends BaseTest {

    @Test
    public void verifyCreateProfile() {
        // Authenticate via QA GraphQL bypass and inject tokens into the browser.
        // The /user-registration form is accessible to authenticated users who have
        // not completed their profile: navigating to /home with valid tokens causes
        // the app to redirect there automatically.
        String mobile = System.getProperty("testMobile", ConfigManager.getTestMobile());
        utilLayer.loginViaQAGraphQL(mobile);
        utilLayer.injectTokenFull();
        utilLayer.injectCookies();

        String homeUrl = ConfigManager.getBaseUrl().replaceAll("/$", "") + "/home";
        BrowserManager.getPage().navigate(homeUrl,
            new com.microsoft.playwright.Page.NavigateOptions().setTimeout(15000));
        BrowserManager.getPage().waitForLoadState(LoadState.DOMCONTENTLOADED);

        // Fill the "Toskie Account Creation" profile form.
        ToskieCreateProfile profilePage = new ToskieCreateProfile(utilLayer);
        profilePage.validatecreateProfile();
    }
}
