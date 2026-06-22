package com.toskie.constants;

/**
 * Central registry for all TestNG group names.
 *
 * Suite groups — determine WHICH suite a test runs in:
 *   smoke      → P0 "is the app alive" gate (every PR)
 *   sanity     → P0+P1 post-deploy gate (every merge)
 *   regression → Full functional suite (nightly)
 *   api        → Pure API/contract tests
 *   e2e        → Cross-feature user journeys
 *   performance → Load and latency benchmarks
 *
 * Priority groups — triage and severity (maps to old p0/p1/p2/p3):
 *   critical   → P0 — blocks release
 *   high       → P1 — core user journey
 *   medium     → P2 — important feature
 *   low        → P3 — edge case / nice-to-have
 *
 * Domain groups — feature area (used for cross-cutting reporting):
 *   security, accessibility, websocket, messaging, gallery
 *
 * Legacy aliases kept for backward compatibility with existing test methods.
 */
public final class TestGroups {

    private TestGroups() {}

    // ─── Suite groups ─────────────────────────────────────────────────────────
    public static final String SMOKE       = "smoke";
    public static final String SANITY      = "sanity";
    public static final String REGRESSION  = "regression";
    public static final String API         = "api";
    public static final String E2E         = "e2e";
    public static final String PERFORMANCE = "performance";

    // ─── Priority groups ──────────────────────────────────────────────────────
    public static final String CRITICAL = "critical";   // was P0
    public static final String HIGH     = "high";       // was P1
    public static final String MEDIUM   = "medium";     // was P2
    public static final String LOW      = "low";        // was P3

    // ─── Legacy priority aliases (kept — existing tests reference these) ──────
    public static final String P0 = "p0";
    public static final String P1 = "p1";
    public static final String P2 = "p2";
    public static final String P3 = "p3";

    // ─── Domain / feature-area groups ─────────────────────────────────────────
    public static final String SECURITY      = "security";
    public static final String ACCESSIBILITY = "accessibility";
    public static final String WEBSOCKET     = "websocket";
    public static final String MESSAGING     = "messaging";
    public static final String GALLERY       = "gallery";
    public static final String NEGATIVE      = "negative";
    public static final String EDGE          = "edge";
}
