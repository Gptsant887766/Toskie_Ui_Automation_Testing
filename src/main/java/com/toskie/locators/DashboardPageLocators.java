package com.toskie.locators;
import com.microsoft.playwright.*;

public class DashboardPageLocators {
    public final Locator profileName, profileRole, profilePhoto, profileCompletionBar, completionPct,
            editProfileBtn, aboutSection, gallerySection, skillsSection, projectsSection,
            reviewsSection, shareBtn, openToConnectToggle, connectionsCount, viewsCount;

    public DashboardPageLocators(Page page) {
        profileName        = page.locator("[class*='profile-name'], h1[class*='name']").first();
        profileRole        = page.locator("[class*='profile-role'], [class*='designation']").first();
        profilePhoto       = page.locator("[class*='profile-photo'] img, [class*='avatar'] img").first();
        profileCompletionBar= page.locator("[class*='progress-bar'], [role='progressbar']").first();
        completionPct      = page.locator("[class*='completion'], [class*='percent']").first();
        editProfileBtn     = page.locator("//button[contains(.,'Edit')] | //a[contains(.,'Edit Profile')]").first();
        aboutSection       = page.locator("[class*='about-section'], #about").first();
        gallerySection     = page.locator("[class*='gallery-section'], #gallery").first();
        skillsSection      = page.locator("[class*='skills-section'], #skills").first();
        projectsSection    = page.locator("[class*='projects-section'], #projects").first();
        reviewsSection     = page.locator("[class*='reviews-section'], #reviews").first();
        shareBtn           = page.locator("//button[contains(.,'Share')] | [aria-label*='share' i]").first();
        openToConnectToggle= page.locator("[class*='open-to-connect'], input[name*='connect' i]").first();
        connectionsCount   = page.locator("[class*='connections-count'], [class*='connects']").first();
        viewsCount         = page.locator("[class*='views-count'], [class*='profile-views']").first();
    }
}
