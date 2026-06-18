package com.toskie.AuthenticationPages.Page;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import com.toskie.AuthenticationPages.OrPages.page.LoginPageOr;
import com.toskie.utils_Layer.ApiUtils;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.ConfigManager;
import com.toskie.utils_Layer.UtilLayer;
import com.toskie.utils_Layer.WaitManager;

public class LoginPage extends LoginPageOr {

    private final UtilLayer<?> utilLayer;

    public LoginPage(UtilLayer<?> utilLayer) {
        super(BrowserManager.getPage());
        this.utilLayer = utilLayer;
    }

    public void loginWithValidCredentials() {
        try {
            WaitManager.waitForPageLoad(LoadState.DOMCONTENTLOADED);

            // Click the Login button only if it is visible on the current page.
            // Tests that arrive here after validateWelcomeToToskieLandingPage() have already
            // navigated away from the welcome screen, so the button will not be present.
            // loginViaQAGraphQL is a pure HTTP call and does not need any UI navigation.
            try {
                ClickOnLoginBT.waitFor(new com.microsoft.playwright.Locator.WaitForOptions()
                    .setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE)
                    .setTimeout(5000));
                utilLayer.click(ClickOnLoginBT, "Login Button");
                WaitManager.waitForPageLoad(LoadState.DOMCONTENTLOADED);
            } catch (Exception navEx) {
                System.out.println("[LoginPage] Login button not visible -- skipping UI navigation (QA bypass).");
            }

            String mobile = System.getProperty("testMobile", ConfigManager.getTestMobile());
            utilLayer.loginViaQAGraphQL(mobile);

            String accessToken  = ApiUtils.getAccessToken();
            String refreshToken = ApiUtils.getRefreshToken();

            if (accessToken == null || accessToken.isEmpty())
                throw new RuntimeException("Access token missing after login");
            if (refreshToken == null || refreshToken.isEmpty())
                throw new RuntimeException("Refresh token missing after login");

            System.out.println("[LoginPage] Tokens generated.");

            utilLayer.injectTokenFull();
            utilLayer.injectCookies();

            // Navigate directly to /home with injected auth tokens.
            // reload + waitForURL("**home**") is unreliable: reloading an anonymous-session
            // page (e.g. the phone-login page) does not trigger an SPA auth-redirect, so
            // waitForURL times out. An explicit navigate forces the app to re-read
            // localStorage/cookies and authenticate on the destination route.
            String homeUrl = ConfigManager.getBaseUrl().replaceAll("/$", "") + "/home";
            BrowserManager.getPage().navigate(homeUrl,
                new Page.NavigateOptions().setTimeout(15000));
            BrowserManager.getPage().waitForLoadState(LoadState.DOMCONTENTLOADED);

            System.out.println("[LoginPage] QA Login Success");

        } catch (Exception e) {
            System.out.println("[LoginPage] LOGIN FAILED");
            e.printStackTrace();
            throw new RuntimeException("Login Failed", e);
        }
    }
}
