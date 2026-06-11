package com.toskie.locators;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class HomePageLocators {

    // ─── Header / Nav ─────────────────────────────────────────────────────────
    public final Locator appLogo;
    public final Locator searchBar;
    public final Locator notificationsIcon;
    public final Locator profileAvatar;
    public final Locator hamburgerMenu;
    public final Locator notificationBadge;

    // ─── Bottom Navigation ────────────────────────────────────────────────────
    public final Locator bottomNavHome;
    public final Locator bottomNavSearch;
    public final Locator bottomNavPost;
    public final Locator bottomNavChat;
    public final Locator bottomNavProfile;

    // ─── Feed / Talent Cards ──────────────────────────────────────────────────
    public final Locator talentCards;
    public final Locator firstTalentCard;
    public final Locator talentCardName;
    public final Locator talentCardCategory;
    public final Locator talentCardRating;
    public final Locator talentCardLocation;
    public final Locator talentCardPrice;
    public final Locator talentCardImage;
    public final Locator talentCardBookButton;
    public final Locator talentCardChatButton;
    public final Locator talentCardSaveButton;
    public final Locator savedBadge;

    // ─── Filters ──────────────────────────────────────────────────────────────
    public final Locator filterButton;
    public final Locator filterCategoryChips;
    public final Locator filterLocationChip;
    public final Locator filterRatingChip;
    public final Locator filterPriceRangeSlider;
    public final Locator filterApplyButton;
    public final Locator filterResetButton;
    public final Locator activeFilterIndicator;

    // ─── Category Chips / Quick Filters ───────────────────────────────────────
    public final Locator categoryChips;
    public final Locator allCategoryChip;
    public final Locator nearbyChip;

    // ─── Location Banner ──────────────────────────────────────────────────────
    public final Locator locationDisplayText;
    public final Locator changeLocationButton;

    // ─── Loaders / Empty States ───────────────────────────────────────────────
    public final Locator loadingSpinner;
    public final Locator emptyStateMessage;
    public final Locator skeletonCards;
    public final Locator loadMoreButton;
    public final Locator endOfListMessage;

    // ─── Toast / Alerts ───────────────────────────────────────────────────────
    public final Locator toastMessage;
    public final Locator successToast;
    public final Locator errorToast;

    // ─── Profile Completion Banner ────────────────────────────────────────────
    public final Locator profileCompletionBanner;
    public final Locator completeProfileButton;

    public HomePageLocators(Page page) {
        // Header - scoped to header section
        appLogo              = page.locator("header img[src*='logo'], header img[alt*='toskie' i], header a[href='/'] img");
        searchBar            = page.locator("header input[type='search'], header input[placeholder*='search' i], header [class*='search-bar'] input");
        notificationsIcon    = page.locator("header [aria-label*='notification' i], header button:has(svg[class*='bell']), header [class*='notification-icon']");
        profileAvatar        = page.locator("header [class*='avatar'], header [class*='user-avatar'], header img[alt*='profile' i]");
        hamburgerMenu        = page.locator("header [aria-label='menu'], header button:has(svg[class*='menu']), header [class*='hamburger']");
        notificationBadge    = page.locator("header [class*='badge'], header [class*='notification-count'], header [class*='red-dot']");

        // Bottom Nav - scoped to nav/footer
        bottomNavHome    = page.locator("nav [aria-label='home' i], nav a[href='/home'], nav a[href='/'], footer nav a:first-child");
        bottomNavSearch  = page.locator("nav [aria-label='search' i], nav a[href*='search'], footer nav a:nth-child(2)");
        bottomNavPost    = page.locator("nav [aria-label='post' i], nav a[href*='post'], nav [class*='post-btn']");
        bottomNavChat    = page.locator("nav [aria-label='chat' i], nav a[href*='chat'], nav a[href*='message']");
        bottomNavProfile = page.locator("nav [aria-label='profile' i], nav a[href*='profile'], footer nav a:last-child");

        // Talent Cards - scoped to main content
        talentCards        = page.locator("main [class*='talent-card'], main [class*='service-card'], main [class*='professional-card'], section [class*='card'][class*='profile']");
        firstTalentCard    = talentCards.first();
        talentCardName     = page.locator("main [class*='talent-name'], main [class*='professional-name'], main [class*='card-title']");
        talentCardCategory = page.locator("main [class*='category'], main [class*='skill-badge'], main [class*='service-type']");
        talentCardRating   = page.locator("main [class*='rating'], main [aria-label*='rating'], main [class*='star-count']");
        talentCardLocation = page.locator("main [class*='location'], main [class*='distance'], main [class*='area']");
        talentCardPrice    = page.locator("main [class*='price'], main [class*='rate'], main [class*='amount']");
        talentCardImage    = page.locator("main [class*='card'] img, main [class*='avatar'] img");
        talentCardBookButton= page.locator("//main//button[contains(text(),'Book')] | //main//button[contains(text(),'Hire')]");
        talentCardChatButton= page.locator("//main//button[contains(text(),'Chat')] | //main//button[contains(text(),'Message')]");
        talentCardSaveButton= page.locator("main button[aria-label*='save' i], main [class*='bookmark'], main [class*='save-btn']");
        savedBadge         = page.locator("main [class*='saved'], main [aria-label*='saved' i]");

        // Filters - scoped to main
        filterButton          = page.locator("main button:has-text('Filter'), main [class*='filter-btn'], main [aria-label='filter']");
        filterCategoryChips   = page.locator("main [class*='filter'] [class*='chip'], main [class*='category-filter']");
        filterLocationChip    = page.locator("main [class*='filter']:has-text('Location'), main [class*='location-filter']");
        filterRatingChip      = page.locator("main [class*='filter']:has-text('Rating')");
        filterPriceRangeSlider= page.locator("main [type='range'], main [class*='price-slider'], main [class*='range-slider']");
        filterApplyButton     = page.locator("main button:has-text('Apply'), main [class*='apply-filter']");
        filterResetButton     = page.locator("main button:has-text('Reset'), main button:has-text('Clear All')");
        activeFilterIndicator = page.locator("main [class*='active-filter'], main [class*='filter-count']");

        // Category chips
        categoryChips  = page.locator("main [class*='category-chip'], main [class*='filter-pill']");
        allCategoryChip= page.locator("main [class*='chip']:has-text('All')");
        nearbyChip     = page.locator("main [class*='chip']:has-text('Nearby')");

        // Location
        locationDisplayText  = page.locator("main [class*='location-text'], main [class*='current-location']");
        changeLocationButton = page.locator("main button:has-text('Change Location'), main [class*='location-change']");

        // Loaders
        loadingSpinner  = page.locator("[class*='spinner'], [class*='loader'], [role='progressbar']");
        emptyStateMessage= page.locator("main [class*='empty-state'], main [class*='no-results'], main p:has-text('No talent found')");
        skeletonCards   = page.locator("main [class*='skeleton'], main [class*='shimmer']");
        loadMoreButton  = page.locator("main button:has-text('Load More'), main button:has-text('Show More')");
        endOfListMessage= page.locator("main [class*='end-of-list'], main p:has-text('No more'), main p:has-text('end')");

        // Toasts
        toastMessage = page.locator("[class*='toast'], [role='alert'], [class*='snackbar']");
        successToast = page.locator("[class*='toast-success'], [class*='success-toast']");
        errorToast   = page.locator("[class*='toast-error'], [class*='error-toast']");

        // Profile completion
        profileCompletionBanner = page.locator("[class*='profile-completion'], [class*='complete-banner']");
        completeProfileButton   = page.locator("button:has-text('Complete Profile'), [class*='complete-profile']");
    }
}
