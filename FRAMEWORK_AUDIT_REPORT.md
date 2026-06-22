# FRAMEWORK AUDIT REPORT

**Date:** 2026-06-17  
**Auditor:** Principal SDET Architect  
**Framework:** Toskie Web Automation Framework (Playwright Java 1.53.0 + TestNG 7.9.0)

---

## 1. ARCHITECTURE OVERVIEW

```
src/main/java/com/toskie/
├── BaseTest_Layer/     BaseTest.java (TestNG base)
├── utils_Layer/        BrowserManager, WaitManager, RetryAnalyzer, RetryTransformer,
│                       RetryConfig, ApiUtils, ConfigManager, ReportManager
├── utils/              AssertionHelper, SecurityUtils, NetworkValidator,
│                       AccessibilityUtils, WebSocketValidator
├── pages/              Page Object Model (POM) — 50+ page classes
├── locators/           Locator classes per page
├── constants/          AppConstants, TestGroups
└── models/             Data model classes
```

---

## 2. ARCHITECTURE ISSUES

### A-01: Wrong localStorage key in `ConversationListPage` and `TalentSearchPage`
- **Severity:** HIGH
- **File:** `pages/messaging/ConversationListPage.java:25`, `pages/search/TalentSearchPage.java:31`
- **Issue:** `loginIfNeeded()` checks `localStorage.getItem('authToken')` — wrong key. Correct key is `access_token` (confirmed in `ApiUtils.injectTokenFull()`).
- **Impact:** `loginIfNeeded()` always sees `null`, always triggers re-login on every page construction — doubles login time for all messaging/search tests.
- **Fix:** Change to `localStorage.getItem('access_token')`.

### A-02: `BrowserManager` is a static singleton with no thread isolation
- **Severity:** HIGH
- **File:** `utils_Layer/BrowserManager.java`
- **Issue:** `getPage()` returns a shared static `Page` instance. If tests ever run in parallel (`parallel="classes"` or `parallel="methods"` in TestNG), they will share browser state, causing intermittent failures.
- **Fix:** Use `ThreadLocal<Page>` for parallel safety. Currently `parallel="none"` in all suites — acceptable short-term but blocks future parallelization.

### A-03: Page Objects use hardcoded `waitForTimeout()` (3 remaining legacy files)
- **Severity:** MEDIUM
- **Files:** `ConversationListPage.java:19`, `TalentSearchPage.java:25`, multiple others
- **Issue:** `BrowserManager.getPage().waitForTimeout(2000)` in constructors — arbitrary delays that either make tests slow or flaky on slow CI.
- **Fix:** Replace with `WaitManager.safePageLoad()` or `waitForElementVisible()`.

### A-04: Legacy `com.toskie.*` classes shadow modern classes
- **Severity:** MEDIUM
- **Files:** `AllToskieTestCases.java`, `LoginTestCases.java`, `ProfileCreationTestCases.java` (8 total)
- **Issue:** These are the original test classes that pre-date the POM refactor. They import `com.toskie.AuthenticationPages.Page.*` which uses the old WelcomeToToskieLandingPage API. They are NOT in any active suite XML but still compile, inflating `@Test` count by 240.
- **Recommendation:** Move to `legacy/` source set or delete.

### A-05: `AssertionHelper.assertContains` signature mismatch potential
- **Severity:** LOW
- **File:** `utils/AssertionHelper.java`
- **Issue:** `assertContains(String actual, String expected, String message)` — but callers in some test files pass args in wrong order. No static analysis catches this at compile time.
- **Fix:** Add explicit param name Javadoc or rename to `assertStringContains(actual, substring, msg)`.

---

## 3. DESIGN ISSUES

### D-01: `BaseTest` does not enforce per-test login state isolation
- **Severity:** HIGH
- **File:** `BaseTest_Layer/BaseTest.java`
- **Issue:** `@BeforeMethod` likely calls login once but individual tests also call `utilLayer.loginViaQAGraphQL()`. This means some tests execute with pre-existing auth state, some create new sessions — inconsistent isolation.
- **Recommendation:** Standardize: either login in `@BeforeMethod` (BaseTest) or in each test, not both.

### D-02: `GalleryStepPage` constructor navigates within page object
- **Severity:** MEDIUM
- **Issue:** Several page objects navigate the browser in their constructor (e.g., `ConversationListPage`, `TalentSearchPage`). This mixes navigation responsibility with page interaction, making tests harder to read and causing unexpected navigations.
- **Recommendation:** Separate navigation from page object construction. Navigation should be explicit in test methods.

### D-03: `AssertionHelper` soft-assertion accumulation across test retries
- **Severity:** MEDIUM
- **Issue:** `AssertionHelper` collects soft assertions in an instance variable. If `assertAll()` is not called (e.g., test throws before `assertAll()`), accumulated failures are silently dropped. With `RetryAnalyzer` active, a retry could pick up stale state from the previous attempt.
- **Fix:** Always create a new `AssertionHelper` instance per test; confirm `assertAll()` is called in finally block or use TestNG's built-in soft assertions.

### D-04: `WaitManager.waitForCondition` retry backoff not honoured
- **Severity:** LOW
- **File:** `utils_Layer/WaitManager.java`
- **Issue:** `RetryConfig.BACKOFF_FACTOR = 1.5` is defined but `waitForCondition` uses a fixed `initialDelay` between retries — the backoff factor is never applied.
- **Fix:** Multiply delay by `BACKOFF_FACTOR` each iteration.

---

## 4. DUPLICATE CODE

| Pattern | Occurrences | Files |
|---|---|---|
| `loginIfNeeded()` with wrong `authToken` key | 4+ | ConversationListPage, TalentSearchPage, TalentSearchResultsPage, SearchPage |
| `buildExpiredJwt()` | 2 | AuthApiTests, JwtSecurityTests |
| `clearTokens()` / `injectTokens()` helpers | 3 | AuthApiTests, JwtSecurityTests, SecurityTests |
| `navigateAndWait()` pattern | 20+ | All page objects (inline in constructors) |

**Recommendation:** Consolidate JWT helpers into `ApiUtils`; consolidate `loginIfNeeded()` into `BaseTest`.

---

## 5. FLAKY AREAS

| Area | Risk Level | Cause |
|---|---|---|
| OTP entry in registration tests | HIGH | OTP arrives via SMS — can be slow/delayed in dev env |
| File upload in gallery tests | HIGH | `setInputFiles()` depends on OS file dialog behavior |
| WebSocket connection tests | HIGH | WS connections time out under load |
| Logout tests | MEDIUM | Multi-step UI navigation can miss intermediate states |
| Search result count assertions | MEDIUM | Dev env data changes between runs |
| `testConversationListUpdatesAfterSend` | MEDIUM | Real-time update depends on WebSocket flush time |

---

## 6. SECURITY RISKS

| Risk | Severity | Detail |
|---|---|---|
| QA secret key in test source | MEDIUM | `SecurityUtils.java` contains the actual QA secret string in source — should be in config/env var |
| `BOLA_TEST_IDS` hardcoded in source | LOW | User IDs in source are dev-only but committed to repo |
| Token injection via localStorage in tests | LOW | Acceptable for test automation but tokens should come from config, not hardcoded numbers |

---

## 7. PERFORMANCE BOTTLENECKS

| Issue | Impact | Recommendation |
|---|---|---|
| Sequential test execution (`parallel="none"`) | HIGH — slow suite runs | Enable `parallel="classes"` after `BrowserManager` is thread-safe |
| 743 tests in single sequential suite | HIGH — 8+ hours estimated | Shard into parallel jobs in CI |
| `waitForTimeout(2000)` still in 10+ page objects | MEDIUM | Replace with condition-based waits |
| `@BeforeMethod` login on every test | MEDIUM | Cache auth token across test class methods using `@BeforeClass` |

---

## 8. REFACTORING RECOMMENDATIONS

Priority order:

1. **Fix `loginIfNeeded()` wrong token key** — all messaging and search tests are effectively logging in twice per test
2. **Extract `buildExpiredJwt()` and `clearTokens()` to `ApiUtils`** — eliminate 3 duplicate helper copies
3. **Make `BrowserManager` thread-safe** — prerequisite for any parallel execution
4. **Add `@BeforeClass` login** in messaging/search/security test classes instead of per-test login
5. **Remove legacy 8 classes** from compile scope — they inflate count, cause confusion, and won't pass
6. **Replace constructor-navigation in page objects** with explicit `navigate()` methods

---

## 9. POSITIVE FINDINGS

| Area | Assessment |
|---|---|
| WaitManager strategy | GOOD — `safePageLoad()` with graceful fallback is solid |
| AssertionHelper soft assertions | GOOD — all test methods use `assertAll()` pattern correctly |
| RetryAnalyzer wiring | GOOD — all suites now have `RetryTransformer` listener |
| Security test coverage (Sprint 2) | EXCELLENT — JWT lifecycle, IDOR, XSS, SQLi, HTTPS all covered |
| Report generation | GOOD — ExtentReports with PDF export |
| Page Object isolation | GOOD — locators separated from page interactions |
| TestNG group taxonomy | GOOD — `smoke`, `p0`, `p1`, `regression` groups consistently used |

---

*Report generated: 2026-06-17 | Auditor: Principal SDET Architect*
