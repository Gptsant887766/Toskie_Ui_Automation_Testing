package com.toskie.locators;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class ProfileViewLocators {

    // ─── Profile Header ───────────────────────────────────────────────────────
    public final Locator profilePhoto;
    public final Locator profileName;
    public final Locator profileCategory;
    public final Locator profileRating;
    public final Locator profileReviewCount;
    public final Locator profileLocation;
    public final Locator profileDistance;
    public final Locator onlineStatusBadge;
    public final Locator verifiedBadge;
    public final Locator followButton;
    public final Locator saveButton;
    public final Locator shareButton;
    public final Locator backButton;
    public final Locator moreOptionsButton;

    // ─── Action Buttons ───────────────────────────────────────────────────────
    public final Locator bookNowButton;
    public final Locator chatButton;
    public final Locator callButton;
    public final Locator sendRequestButton;

    // ─── Profile Tabs ─────────────────────────────────────────────────────────
    public final Locator aboutTab;
    public final Locator servicesTab;
    public final Locator reviewsTab;
    public final Locator portfolioTab;
    public final Locator activeTabIndicator;

    // ─── About Section ────────────────────────────────────────────────────────
    public final Locator bioText;
    public final Locator skillsList;
    public final Locator skillItem;
    public final Locator experienceSection;
    public final Locator educationSection;
    public final Locator languagesList;
    public final Locator availabilityBadge;

    // ─── Services Section ─────────────────────────────────────────────────────
    public final Locator serviceCards;
    public final Locator serviceTitle;
    public final Locator servicePrice;
    public final Locator serviceDuration;
    public final Locator serviceDescription;
    public final Locator bookServiceButton;

    // ─── Reviews Section ──────────────────────────────────────────────────────
    public final Locator overallRating;
    public final Locator ratingBreakdown;
    public final Locator reviewItems;
    public final Locator reviewerName;
    public final Locator reviewText;
    public final Locator reviewDate;
    public final Locator reviewRating;
    public final Locator loadMoreReviewsButton;
    public final Locator writeReviewButton;

    // ─── Portfolio Section ────────────────────────────────────────────────────
    public final Locator portfolioImages;
    public final Locator portfolioVideo;
    public final Locator portfolioViewAllButton;

    // ─── Similar Profiles ─────────────────────────────────────────────────────
    public final Locator similarProfilesSection;
    public final Locator similarProfileCards;

    public ProfileViewLocators(Page page) {
        profilePhoto      = page.locator("[class*='profile-photo'], [class*='profile-image'], [class*='avatar-large']");
        profileName       = page.locator("[class*='profile-name'], h1[class*='name'], [class*='talent-name']");
        profileCategory   = page.locator("[class*='profile-category'], [class*='profession'], [class*='specialty']");
        profileRating     = page.locator("[class*='rating-score'], [class*='avg-rating']");
        profileReviewCount= page.locator("[class*='review-count'], span:has-text('review')");
        profileLocation   = page.locator("[class*='profile-location'], [class*='location-info']");
        profileDistance   = page.locator("[class*='distance'], span:has-text('km'), span:has-text('miles')");
        onlineStatusBadge = page.locator("[class*='online-indicator'], [class*='status-online']");
        verifiedBadge     = page.locator("[class*='verified'], [aria-label*='verified' i]");
        followButton      = page.locator("button:has-text('Follow'), button:has-text('Unfollow')");
        saveButton        = page.locator("button[aria-label*='save' i], [class*='save-profile']");
        shareButton       = page.locator("button[aria-label*='share' i], [class*='share-btn']");
        backButton        = page.locator("button[aria-label='back'], [class*='back-button']");
        moreOptionsButton = page.locator("button[aria-label='more options'], [class*='options-menu']");

        bookNowButton     = page.locator("button:has-text('Book Now'), button:has-text('Book'), [class*='book-btn']");
        chatButton        = page.locator("button:has-text('Chat'), button:has-text('Message'), [class*='chat-cta']");
        callButton        = page.locator("button:has-text('Call'), [class*='call-cta']");
        sendRequestButton = page.locator("button:has-text('Send Request'), button:has-text('Connect')");

        aboutTab      = page.locator("[role='tab']:has-text('About'), [class*='tab']:has-text('About')");
        servicesTab   = page.locator("[role='tab']:has-text('Services'), [class*='tab']:has-text('Services')");
        reviewsTab    = page.locator("[role='tab']:has-text('Reviews'), [class*='tab']:has-text('Reviews')");
        portfolioTab  = page.locator("[role='tab']:has-text('Portfolio'), [class*='tab']:has-text('Portfolio')");
        activeTabIndicator = page.locator("[aria-selected='true'], [class*='tab-active']");

        bioText          = page.locator("[class*='bio-text'], [class*='about-description']");
        skillsList       = page.locator("[class*='skills-list'], [class*='skills-container']");
        skillItem        = page.locator("[class*='skill-tag'], [class*='skill-chip']");
        experienceSection= page.locator("[class*='experience'], section:has-text('Experience')");
        educationSection = page.locator("[class*='education'], section:has-text('Education')");
        languagesList    = page.locator("[class*='languages'], [class*='language-list']");
        availabilityBadge= page.locator("[class*='availability'], span:has-text('Available'), span:has-text('Busy')");

        serviceCards       = page.locator("[class*='service-card'], [class*='offering-card']");
        serviceTitle       = page.locator("[class*='service-card'] [class*='title']");
        servicePrice       = page.locator("[class*='service-card'] [class*='price']");
        serviceDuration    = page.locator("[class*='service-card'] [class*='duration']");
        serviceDescription = page.locator("[class*='service-card'] [class*='description']");
        bookServiceButton  = page.locator("[class*='service-card'] button:has-text('Book')");

        overallRating        = page.locator("[class*='overall-rating'], [class*='avg-rating-display']");
        ratingBreakdown      = page.locator("[class*='rating-breakdown'], [class*='rating-bars']");
        reviewItems          = page.locator("[class*='review-item'], [class*='review-card']");
        reviewerName         = page.locator("[class*='review-item'] [class*='reviewer-name']");
        reviewText           = page.locator("[class*='review-item'] [class*='review-text']");
        reviewDate           = page.locator("[class*='review-item'] [class*='review-date']");
        reviewRating         = page.locator("[class*='review-item'] [class*='star-rating']");
        loadMoreReviewsButton= page.locator("button:has-text('Load More Reviews'), [class*='load-reviews']");
        writeReviewButton    = page.locator("button:has-text('Write a Review'), button:has-text('Add Review')");

        portfolioImages     = page.locator("[class*='portfolio'] img, [class*='portfolio-grid'] img");
        portfolioVideo      = page.locator("[class*='portfolio'] video, [class*='portfolio'] [class*='video']");
        portfolioViewAllButton = page.locator("button:has-text('View All'), [class*='portfolio-all']");

        similarProfilesSection = page.locator("[class*='similar-profiles'], [class*='recommended']");
        similarProfileCards    = page.locator("[class*='similar-profiles'] [class*='card']");
    }
}
