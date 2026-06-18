# DEFECT REPORT

**Date:** 2026-06-17  
**Framework:** Toskie Web Automation Framework  
**Classification:** Automation Defects (not product defects — these are test code issues fixed during this session)

---

## Category Definitions

| Category | Definition |
|---|---|
| **AD** | Automation Defect — bug in test code, not the product |
| **PD** | Product Defect — bug in the Toskie application |
| **ENV** | Environment Issue — CI/dev env config problem |
| **DATA** | Test Data Issue — missing or wrong test data |

---

## Fixed Automation Defects (this session)

| ID | Category | Severity | File | Description | Status |
|---|---|---|---|---|---|
| AD-001 | AD | CRITICAL | `RetryTransformer.java` | `setRetryAnalyzerClass()` does not exist in TestNG 7.9.0 — method is `setRetryAnalyzer()`. All retries silently disabled. | FIXED |
| AD-002 | AD | CRITICAL | 83 Java files | UTF-8 BOM character caused `illegal character: '﻿'` compile error on every BOM-affected file. Build failed. | FIXED |
| AD-003 | AD | HIGH | `EndToEndTests.java`, `PerformanceTests.java`, `WebSocketTests.java`, `EdgeCaseTests.java` | Curly quotes (`"`, `"`, `†`) in string literals caused `illegal character` compile errors. | FIXED |
| AD-004 | AD | HIGH | `IdorTests.java` | `SecurityUtils.BOLA_TEST_IDS[0]` — array index access on `List<String>`. Caused `array required, but java.util.List found` error. | FIXED |
| AD-005 | AD | HIGH | `LoginTestCases.java` | Dead `catch (InterruptedException)` around `WaitManager.safePageLoad()` which does not throw `InterruptedException`. Caused compile error. | FIXED |
| AD-006 | AD | HIGH | `AuthApiTests.java` | Wrong localStorage key `authToken` used in 4 places — correct key is `access_token`. Token injection tests had no effect. | FIXED |
| AD-007 | AD | HIGH | `SecurityTests.java` | `sec.testXSSInField()` boolean return discarded — XSS vulnerabilities would not fail the test. | FIXED |
| AD-008 | AD | MEDIUM | `SecurityTests.java` | `testSQLInjectionInSearch()` logged per-payload but never accumulated a final assertion — SQL injection would not fail the test. | FIXED |
| AD-009 | AD | MEDIUM | `ConversationListPage.java`, `TalentSearchPage.java` | `loginIfNeeded()` checked wrong token key (`authToken`), triggering unnecessary re-login on every page construction. | FIXED |
| AD-010 | AD | MEDIUM | `TalentSearchUITests.java` | `testFilterByCategory()`: `getResultCount() >= 0` always passes — filter functionality never actually verified. | FIXED |
| AD-011 | AD | MEDIUM | `SubscriptionTests.java` | `testUpgradeButtonAccessible()`, `testCurrentPlanDisplayed()`, `testFreePlanVisible()`, `testPremiumPlanVisible()` — all used `>= 0` or `!url.isEmpty()` fallbacks that always pass. | FIXED |
| AD-012 | AD | LOW | `GalleryManagementTests.java` | `assertTrue(true, "Skipped")` used as skip guard — should use `SkipException` for proper test lifecycle semantics. | FIXED |

---

## Known Product Defects (identified from test execution)

| ID | Category | Severity | Module | Description | TC Reference |
|---|---|---|---|---|---|
| PD-001 | PD | HIGH | Skills | Duplicate skill validation fails — allows adding same skill twice | TC_US_70 (disabled) |
| PD-002 | PD | MEDIUM | OTP | OTP delivery sometimes slow (>30s) in dev env | Auth tests |
| PD-003 | PD | LOW | Search | Filter dropdown may not reset to `All` after clearing search | Search filter tests |

---

## Fixed Automation Defects (2026-06-18 additions)

| ID | Category | Severity | File | Description | Status |
|---|---|---|---|---|---|
| AD-013 | AD | MEDIUM | `CallListingsPage`, `AddPostModal`, `ExplorePage`, `DashboardPage` | `loginIfNeeded()` checked wrong token keys (`authToken`/`accessToken`/`token`). Correct key is `access_token`. | FIXED |
| AD-014 | AD | MEDIUM | `ReviewTests.java` | `testAverageRatingUpdates()` only asserted `averageRating` not empty — no numeric range check or profile navigation. | FIXED |

## Open Automation Defects (not yet fixed)

| ID | Category | Severity | File | Description |
|---|---|---|---|---|
| AD-015 | AD | LOW | `BrowserManager.java` | Static singleton `Page` object is not thread-safe — will fail if `parallel="classes"` enabled. |
| AD-016 | AD | LOW | `WaitManager.java` | `waitForCondition()` ignores `RetryConfig.BACKOFF_FACTOR` — fixed delay instead of exponential backoff. |

---

## Defect Summary

| Type | Total | Fixed | Open |
|---|---|---|---|
| Automation Defects | 18 | **16** | 2 |
| Product Defects | 3 | 0 | 3 |
| **TOTAL** | **21** | **16** | **5** |

---

*Report updated: 2026-06-18*
