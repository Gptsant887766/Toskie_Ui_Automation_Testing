package com.toskie.constants;

import com.toskie.config.ConfigReader;

/**
 * Application URL constants.
 *
 * All URLs are derived from the resolved baseUrl so they work correctly across
 * every Maven profile (dev/qa/stage/prod) without any code change.
 *
 * Priority chain (highest → lowest):
 *   1. -DbaseUrl=https://...  (CLI / CI pipeline override)
 *   2. ENVIRONMENT env var    (e.g. ENVIRONMENT=qa → loads environments/qa.properties)
 *   3. environments/{env}.properties overlay
 *   4. config.properties base
 *
 * Usage: AppConstants.DASHBOARD_URL resolves to the active environment's URL.
 */
public final class AppConstants {

    private AppConstants() {}

    // Resolved once at class-load time from the active environment config
    private static final String BASE = normalise(ConfigReader.getBaseUrl());

    public static final String BASE_URL          = BASE;
    public static final String LOGIN_URL         = BASE + "login";
    public static final String REGISTER_URL      = BASE + "register";
    public static final String DASHBOARD_URL     = BASE + "dashboard";
    public static final String TALENT_DASHBOARD_URL = BASE + "talent/dashboard";
    public static final String SEARCH_URL        = BASE + "search";
    public static final String PROFILE_URL       = BASE + "profile";
    public static final String MESSAGING_URL     = BASE + "messages";
    public static final String POSTS_URL         = BASE + "posts";
    public static final String EXPLORE_URL       = BASE + "explore";
    public static final String BLOG_URL          = BASE + "blog";
    public static final String SUBSCRIPTION_URL  = BASE + "subscription";
    public static final String SETTINGS_URL      = BASE + "settings";
    public static final String NOTIFICATIONS_URL = BASE + "notifications";

    /** Ensures the base URL always ends with exactly one slash. */
    private static String normalise(String url) {
        if (url == null || url.isBlank()) return "https://dev.app.toskie.com/";
        return url.endsWith("/") ? url : url + "/";
    }
}
