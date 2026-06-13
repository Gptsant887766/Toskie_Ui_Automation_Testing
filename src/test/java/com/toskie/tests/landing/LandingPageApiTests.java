package com.toskie.tests.landing;
import com.aventstack.extentreports.Status;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.constants.TestGroups;
import com.toskie.pages.landing.LandingPage;
import com.toskie.utils.AssertionHelper;
import org.testng.annotations.Test;

public class LandingPageApiTests extends BaseTest {
    private LandingPage landingPage;
    private AssertionHelper a;
    private void init() { landingPage = new LandingPage(utilLayer); a = new AssertionHelper(); }

    @Test(groups = {TestGroups.SMOKE, TestGroups.P0}, description = "Landing page loads successfully")
    public void testLandingPageLoads() { init(); a.assertTrue(landingPage.isHeroVisible(), "Hero section should be visible"); a.assertAll(); }

    @Test(groups = {TestGroups.API, TestGroups.P1}, description = "Landing page API data loads correctly")
    public void testLandingPageApiData() { init(); a.assertNotEmpty(landingPage.getHeroTitle(), "Hero title should be populated from API"); a.assertAll(); }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P1}, description = "Login button visible on landing page")
    public void testLoginBtnVisible() { init(); a.assertTrue(landingPage.isLoginBtnVisible(), "Login button should be visible"); a.assertAll(); }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P1}, description = "Register button visible on landing page")
    public void testRegisterBtnVisible() { init(); a.assertTrue(landingPage.isRegisterBtnVisible(), "Register button should be visible"); a.assertAll(); }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2}, description = "Feature cards are displayed on landing page")
    public void testFeatureCardsVisible() { init(); a.assertTrue(landingPage.getFeatureCardCount() > 0, "Feature cards should be shown"); a.assertAll(); }
}
