package com.toskie.locators;
import com.microsoft.playwright.*;

public class TalentProfileLocators {
    public final Locator profileName, profileRole, location, rating, bio, galleryImages, skills,
            projectCards, reviewCards, connectBtn, messageBtn, similarTalentCards, reportBtn,
            profileImage, skillsTags, gallerySection, experienceSection, qualificationSection,
            aboutSection, likeBtn, bookmarkBtn, shareBtn, ratingDisplay, locationDisplay;

    public TalentProfileLocators(Page page) {
        profileName        = page.locator("[class*='profile-name'], h1").first();
        profileRole        = page.locator("[class*='role'], [class*='designation']").first();
        location           = page.locator("[class*='location']").first();
        rating             = page.locator("[class*='rating'], [class*='stars']").first();
        bio                = page.locator("[class*='bio'], [class*='about-text']").first();
        galleryImages      = page.locator("[class*='gallery'] img, [class*='portfolio'] img");
        skills             = page.locator("[class*='skill-tag'], [class*='skill-badge']");
        projectCards       = page.locator("[class*='project-card']");
        reviewCards        = page.locator("[class*='review-card'], [class*='testimonial']");
        connectBtn         = page.locator("//button[contains(.,'Connect')] | //button[contains(.,'Request')]").first();
        messageBtn         = page.locator("//button[contains(.,'Message')]").first();
        similarTalentCards = page.locator("[class*='similar-talent'] [class*='card']");
        reportBtn           = page.locator("//button[contains(.,'Report')] | [aria-label*='report' i]").first();
        profileImage        = page.locator("[class*='profile-photo'] img, [class*='avatar'] img, img[class*='profile']").first();
        skillsTags          = skills;
        gallerySection      = page.locator("[class*='gallery-section'], [class*='portfolio-section'], section:has([class*='gallery'])").first();
        experienceSection   = page.locator("[class*='experience-section'], section:has-text('Experience')").first();
        qualificationSection= page.locator("[class*='qualification-section'], [class*='education-section'], section:has-text('Education')").first();
        aboutSection        = page.locator("[class*='about-section'], [class*='bio-section'], section:has-text('About')").first();
        likeBtn             = page.locator("[class*='like-btn'], [aria-label*='like' i], button:has-text('Like')").first();
        bookmarkBtn         = page.locator("[class*='bookmark-btn'], [aria-label*='bookmark' i], button:has-text('Save')").first();
        shareBtn            = page.locator("[class*='share-btn'], [aria-label*='share' i], button:has-text('Share')").first();
        ratingDisplay       = rating;
        locationDisplay     = location;
    }
}
