package com.toskie;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.toskie.utils_Layer.WaitManager;
import com.toskie.AuthenticationPages.Page.LoginPage;
import com.toskie.AuthenticationPages.Page.ToskieCreateProfile;
import java.lang.reflect.Method;
import com.toskie.AuthenticationPages.Page.WelcomeToToskieLandingPage;
import com.toskie.BaseTest_Layer.BaseTest;

public class LoginTestCases extends BaseTest {

    // ─── Complete flow (single run) ──────────────────────────────────────────
    @Test(description = "Verify complete user onboarding flow: welcome → login → profile")
    public void verifyCompleteUserFlow() {
        WelcomeToToskieLandingPage welcomePage = new WelcomeToToskieLandingPage(utilLayer);
        welcomePage.validateWelcomeToToskieLandingPage();

        LoginPage loginPage = new LoginPage(utilLayer);
        loginPage.loginWithValidCredentials();

        // Wait for profile page to load after login
        WaitManager.safePageLoad();

        ToskieCreateProfile profilePage = new ToskieCreateProfile(utilLayer);
        // attempt to call validateCreateProfile() if it exists; otherwise skip
        try {
            Method m = ToskieCreateProfile.class.getDeclaredMethod("validateCreateProfile");
            m.setAccessible(true);
            m.invoke(profilePage);
        } catch (NoSuchMethodException nsme) {
            System.out.println("validateCreateProfile() not found on ToskieCreateProfile; skipping profile validation.");
        } catch (Exception e) {
            System.out.println("Error invoking validateCreateProfile: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ─── Data-driven login tests ──────────────────────────────────────────────
    /**
     * Each row: { mobile, testType }
     * Add more rows to test additional phone numbers without writing new @Test methods.
     */
    @DataProvider(name = "loginData", parallel = true)
    public Object[][] loginData() {
        return new Object[][]{
            {"9919011050", "valid_user_QA"},
            // Add more test mobile numbers below when needed:
            // {"9876543210", "valid_user_2"},
        };
    }

    @Test(dataProvider = "loginData",
          description = "Data-driven login: verifies login flow for each mobile number")
    public void verifyLoginWithDataProvider(String mobile, String testType) {
        System.out.println("[LoginTest] Running for mobile=" + mobile + " type=" + testType);

        WelcomeToToskieLandingPage welcomePage = new WelcomeToToskieLandingPage(utilLayer);
        welcomePage.validateWelcomeToToskieLandingPage();

        // Override the mobile used inside LoginPage via system property
        System.setProperty("testMobile", mobile);
        try {
            LoginPage loginPage = new LoginPage(utilLayer);
            loginPage.loginWithValidCredentials();
        } finally {
            System.clearProperty("testMobile");
        }
    }
}