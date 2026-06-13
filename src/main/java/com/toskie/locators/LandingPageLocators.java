package com.toskie.locators;
import com.microsoft.playwright.*;

public class LandingPageLocators {
    public final Locator heroTitle, heroSubtitle, heroCTA, loginBtn, registerBtn,
            featuresSection, featureCards, testimonialSection, testimonialCards,
            footerLinks, navLinks, bannerImages, scrollToTopBtn,
            heroSection, searchBar, talentCards;

    public LandingPageLocators(Page page) {
        heroTitle          = page.locator("[class*='hero'] h1, [class*='hero-title']").first();
        heroSubtitle       = page.locator("[class*='hero'] p, [class*='hero-subtitle']").first();
        heroCTA            = page.locator("[class*='hero'] button, [class*='cta-btn']").first();
        loginBtn           = page.locator("//a[normalize-space()='Login'] | //button[normalize-space()='Login']").first();
        registerBtn        = page.locator("//a[normalize-space()='Register'] | //a[contains(.,'Sign Up')]").first();
        featuresSection    = page.locator("[class*='features'], #features").first();
        featureCards       = page.locator("[class*='feature-card'], [class*='feature-item']");
        testimonialSection = page.locator("[class*='testimonial'], [class*='review-section']").first();
        testimonialCards   = page.locator("[class*='testimonial-card'], [class*='testimonial-item']");
        footerLinks        = page.locator("footer a, [class*='footer'] a");
        navLinks           = page.locator("nav a, [class*='nav'] a");
        bannerImages       = page.locator("[class*='banner'] img, [class*='hero'] img");
        scrollToTopBtn     = page.locator("[class*='scroll-top'], [aria-label*='scroll to top' i]").first();
        heroSection        = page.locator("[class*='hero'], [class*='banner'], section:first-of-type").first();
        searchBar          = page.locator("input[type='search'], input[placeholder*='search' i], [class*='search-bar'] input").first();
        talentCards        = page.locator("[class*='talent-card'], [class*='profile-card'], [class*='user-card']");
    }
}
