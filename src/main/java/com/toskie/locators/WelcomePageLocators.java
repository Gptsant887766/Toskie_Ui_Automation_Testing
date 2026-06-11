package com.toskie.locators;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class WelcomePageLocators {

    public final Locator nextButton;
    public final Locator startExploringButton;
    public final Locator createMyProfileButton;
    public final Locator skipButton;
    public final Locator onboardingSlide;
    public final Locator progressDots;
    public final Locator welcomeHeading;
    public final Locator welcomeSubText;
    public final Locator appLogo;
    public final Locator slide1Image;
    public final Locator slide2Image;
    public final Locator slide3Image;

    public WelcomePageLocators(Page page) {
        nextButton           = page.locator("//button[.//span[contains(text(),'Next')]]");
        startExploringButton = page.locator("//button[.//span[contains(text(),'Start Exploring')]]");
        createMyProfileButton= page.locator("//button[.//span[contains(text(),'Create My Profile')]]");
        skipButton           = page.locator("//button[.//span[contains(text(),'Skip')] or contains(text(),'Skip')]");
        onboardingSlide      = page.locator("[class*='swiper-slide'], [class*='onboarding'], [class*='slide']");
        progressDots         = page.locator("[class*='dot'], [class*='indicator'], [role='tablist']");
        welcomeHeading       = page.locator("h1, h2").first();
        welcomeSubText       = page.locator("p").first();
        appLogo              = page.locator("img[alt*='toskie' i], img[src*='logo'], img[src*='image.svg']");
        slide1Image          = page.locator("[data-slide='0'], [class*='slide']:nth-child(1) img");
        slide2Image          = page.locator("[data-slide='1'], [class*='slide']:nth-child(2) img");
        slide3Image          = page.locator("[data-slide='2'], [class*='slide']:nth-child(3) img");
    }
}
