# PROJECT INVENTORY — Toskie Web Automation Framework

**Generated:** 2026-06-17  
**Auditor:** Principal SDET Architect  
**Project:** Toskie_Web_Automation_Framework  
**App Under Test:** https://dev.app.toskie.com/

---

## 1. REPOSITORY SUMMARY

| Category | Count |
|---|---|
| Total Java source files (main) | 111 |
| Total Java test files | 97 |
| Total XML suite files | 31 |
| Modern @Test methods (com.toskie.tests.*) | 477 |
| Legacy @Test methods (com.toskie root) | ~240 |
| **TOTAL @Test methods** | **~717** |
| Disabled tests (@Test enabled=false) | 1 |
| @Ignore annotations | 0 |
| @DataProvider methods | 9 |
| Listener classes (wired) | 0 |
| Listener classes (exist but not wired) | 1 (RetryConfig) |

---

## 2. MAIN SOURCE LAYER — src/main/java/com/toskie/

### Page Objects (45 classes)

| Package | Classes |
|---|---|
| pages.auth | LoginPage, RegisterPage, OTPPage, RecoveryPage |
| pages.profile | PersonalInfoPage, BioPage, SkillPage, ExperiencePage, QualificationPage, ProjectPage, GalleryPage, SurveyPage, ProfileCompletionPage |
| pages.dashboard | DashboardPage, TalentDashboardPage, ProfilePhotoPage, DashboardAboutPage, DashboardCRUDPage |
| pages.posts | FeedPage, AddPostPage, TextPostPage, PostDetailPage, ExplorePage, GalleryExploreDetailPage |
| pages.search | TalentSearchPage, SearchFilterPage |
| pages.messaging | MessagingPage, ConversationPage, MessageRequestPage |
| pages.websocket | WebSocketPage |
| pages.misc | NotificationPage, SettingsPage, ChangeLocationPage |
| pages.landing | LandingPage |
| pages.welcome | WelcomePage |
| pages.blog | BlogPage |
| pages.subscription | SubscriptionPage *(new — 2026-06-17)* |

### Locator Classes (25 classes)

Pattern: `*Locators.java` — pure CSS/XPath selectors, no logic.  
Packages: auth, profile, dashboard, posts, search, messaging, misc, landing, welcome.

### Component Classes (10 classes)

| Class | Purpose |
|---|---|
| HeaderComponent | Top navigation bar |
| FooterComponent | Footer links |
| SidebarComponent | Left sidebar navigation |
| ModalComponent | Generic modal wrapper |
| ToastComponent | Toast/snackbar notifications |
| ProfileCardComponent | Talent profile card |
| PostCardComponent | Feed post card |
| FilterPanelComponent | Search filter panel |
| LoaderComponent | Loading spinner |
| ErrorComponent | Error state display |

### Utility Classes (16 classes)

| Class | Role |
|---|---|
| UtilLayer | Singleton — 80+ action/wait/verify methods, delegates to all utils |
| BrowserManager | ThreadLocal Playwright/Browser/Context/Page lifecycle |
| ApiUtils | GraphQL QA bypass login, OTP bypass, JWT injection |
| ReportManager | ExtentReports HTML + iTextPDF + JFreeChart |
| WaitManager | Exponential backoff retry, safePageLoad() |
| ConfigManager | config.properties loader, -D override support |
| AssertionHelper | SoftAssert wrapper, assertAll() pattern |
| RetryConfig | IRetryAnalyzer (WIRED=false — not wired to any suite XML) |
| ScreenshotUtil | On-failure screenshot capture |
| DataProviderFactory | Shared @DataProvider methods |
| ApiCallHelper | Raw HTTP/GraphQL request helpers |
| JsonParser | JSON response body parser |
| DateTimeUtil | Date formatting for profile fields |
| FileUtil | File upload helpers |
| LogManager | Log4j2 wrapper |
| AppConstants | URL constants (BASE_URL, LOGIN_URL, ..., SUBSCRIPTION_URL) |

### Model Classes (7 classes)

UserModel, TalentModel, PostModel, MessageModel, SearchFilterModel, ProfileModel, ReviewModel

---

## 3. TEST LAYER — src/test/java/com/toskie/tests/

### Test Packages and Class Count

| Package | Classes | @Test Methods | Notes |
|---|---|---|---|
| tests.smoke | 1 | 3 | P0 smoke gate |
| tests.auth | 4 | 83 | Auth, Login, Registration, Recovery, API |
| tests.profile | 10 | 69 | Full profile wizard + visibility |
| tests.dashboard | 12 | ~50 | Dashboard CRUD, API, photo, review, share |
| tests.posts | 13 | 45 | Feed, post creation, social, video |
| tests.search | 4 | 13 | UI search, API search, address, delete recent |
| tests.messaging | 8 | 14 | Request, conversation, call, validate talent |
| tests.websocket | 4 | ~15 | Connection, messages, bulk read, timezone |
| tests.regression | 9 | 83 | Home, login, logout, profile, search, booking, chat, notifications, settings |
| tests.api | 2 | 25 | APIValidation, WebSocket |
| tests.landing | 1 | ~5 | Landing page API |
| tests.blog | 1 | ~5 | Blog API |
| tests.ai | 1 | ~5 | AI bio generation |
| tests.activity | 1 | ~5 | Activity history |
| tests.reviews | 1 | ~8 | Review CRUD |
| tests.misc | 9 | ~25 | Location, notifications, viewer list, tips, privacy, gallery/project/post view API |
| tests.negative | 1 | 15 | Invalid inputs, error states |
| tests.edge | 1 | 15 | Edge cases, boundary conditions |
| tests.security | 1 | ~10 | Auth bypass, XSS, injection (stubs) |
| tests.accessibility | 1 | 12 | WCAG, keyboard nav, screen reader (stubs) |
| tests.performance | 1 | ~8 | Page load, API response time (stubs) |
| tests.subscription | 1 | 8 | Subscription page, plans, upgrade *(new)* |
| tests.e2e | 1 | ~5 | Full end-to-end journey |
| **TOTAL MODERN** | **97** | **477** | |

### Legacy Test Classes (ORPHANED — no active suite runs them)

| Class | Location | @Test Count | Status |
|---|---|---|---|
| AllToskieTestCases | com.toskie root | ~194 | Orphaned |
| WelcomeToToskieLandingTestCase | com.toskie root | ~10 | Referenced in testng-failed.xml (broken) |
| LoginTestCases | com.toskie root | ~8 | Orphaned |
| WelcomePageTestCases | com.toskie root | ~6 | Orphaned |
| LoginPageTestCases | com.toskie root | ~6 | Orphaned |
| ToskieCreatePrifileTestCases | com.toskie root | ~8 | Orphaned |
| ProfileCreationTestCases | com.toskie root | ~4 | Orphaned |
| EndToEndFlowTestCases | com.toskie root | ~4 | Orphaned |
| **TOTAL LEGACY** | | **~240** | **Never run** |

---

## 4. TESTNG SUITE FILES — src/test/resources/ + root

### Active Suite Files (31 total)

| Suite File | Classes | Type | Status |
|---|---|---|---|
| **FullRegressionSuite.xml** | 90 | class-based | DEFAULT (pom.xml) — 21 test blocks |
| **ToskieMasterSuite.xml** | 93 | class-based | Full sequential — 21 test blocks |
| **SmokeSuite.xml** | 7 | class-based + groups | NEW — smoke gate |
| **RegressionSuite.xml** | 53 | class-based | NEW — functional regression |
| **SecuritySuite.xml** | 10 | class-based + groups | NEW — security audit |
| **AccessibilitySuite.xml** | 8 | class-based + groups | NEW — WCAG |
| **PerformanceSuite.xml** | 10 | class-based + groups | NEW — benchmarks |
| **E2ESuite.xml** | 26 | class-based | NEW — end-to-end journeys |
| testng-master.xml | 18 | class-based | Incomplete — 18 classes only |
| testng-api.xml | packages | package-based | browser=chromium (WRONG — should be chrome) |
| testng-regression.xml | packages | package-based | browser=chromium (WRONG) |
| testng-smoke.xml | packages | package-based | browser=chromium (WRONG) |
| testng-websocket.xml | packages | package-based | browser=chromium (WRONG) |
| testng.xml (root) | ~5 legacy | legacy class | BROKEN — references legacy root classes |
| testng-failed.xml | 1 legacy | generated | BROKEN — WelcomeToToskieLandingTestCase no package |
| testng-results.xml | — | generated | Last real run: 2026-05-24, 1 test |
| testng-auth.xml | — | class-based | Subset suite |
| testng-e2e.xml | — | class-based | Subset suite |
| testng-security.xml | — | class-based | Subset suite |
| testng-accessibility.xml | — | class-based | Subset suite |
| testng-performance.xml | — | class-based | Subset suite |
| testng-negative.xml | — | class-based | Subset suite |
| testng-createprofile.xml | — | class-based | Subset suite |
| testng-profile-creation.xml | — | class-based | Subset suite |
| testng-welcome-landing.xml | — | legacy | References legacy classes |
| testng-welcome-login.xml | — | legacy | References legacy classes |
| testng-welcome-page.xml | — | legacy | References legacy classes |
| testng-loginpage.xml | — | legacy | References legacy classes |
| testng-login.xml | — | legacy | Partial — tests.auth.AuthLoginTests only |
| testng-e2eflow.xml | — | class-based | Duplicate of testng-e2e.xml |
| testng-active-suite.xml | — | class-based | Old active subset |

---

## 5. DISABLED / IGNORED TESTS

| Class | Method | Reason |
|---|---|---|
| SkillStepTests | testDuplicateSkillValidation | enabled=false — KNOWN BUG TC_US_70: Duplicate skill not blocked by app. Re-enable when app fix deployed. |

---

## 6. DATAPROVIDIERS

| Class | DataProvider Name | Tests Using It |
|---|---|---|
| DataProviderFactory | invalidLoginData | AuthLoginTests.testInvalidLoginCombinations |
| DataProviderFactory | registrationData | RegistrationTests.testRegistrationVariants |
| DataProviderFactory | searchKeywords | TalentSearchUITests.testMultipleKeywordSearch |
| DataProviderFactory | invalidInputs | NegativeTests.testInvalidFormInputs |
| DataProviderFactory | edgeCaseUrls | EdgeCaseTests.testDirectUrlNavigation |
| DataProviderFactory | bioPrompts | AIBioTests.testAiBioGeneration |
| DataProviderFactory | messagePayloads | MessageRequestTests.testVariousMessageTypes |
| DataProviderFactory | filterCombinations | TalentSearchApiTests.testSearchFilters |
| DataProviderFactory | profileFields | PersonalInfoStepTests.testFieldValidations |

---

## 7. ASSERTION QUALITY SUMMARY

| Pattern | Count | Risk |
|---|---|---|
| `assertTrue(!url.isEmpty())` — always passes | ~40 | HIGH |
| `assertTrue(true, ...)` — hardcoded pass | ~5 | CRITICAL |
| `assertTrue(count >= 0)` — trivially true | ~15 | HIGH |
| Proper `assertContains` / `assertTextEquals` | ~320 | GOOD |
| Missing `assertAll()` call | 0 | OK — BaseTest handles it |
| Hard-coded `Thread.sleep()` / `waitForTimeout(>2000)` | ~10 | MEDIUM |
| Tests with zero assertions | ~8 | HIGH |

---

## 8. FRAMEWORK ARCHITECTURE

```
BaseTest (@BeforeMethod: browser launch + QA login)
    └── UtilLayer (singleton — action/wait/verify)
            ├── BrowserManager (ThreadLocal PW/Browser/Context/Page)
            ├── ApiUtils (GraphQL QA bypass, JWT injection)
            ├── WaitManager (retry/backoff, safePageLoad)
            ├── ReportManager (ExtentReports + PDF + Charts)
            ├── AssertionHelper (SoftAssert + assertAll)
            └── ConfigManager (config.properties + -D overrides)
```

### Key Design Decisions

- **Two-layer Page Object Model**: `*Locators.java` (selectors only) + `*Page.java` (actions)  
- **ThreadLocal browser**: safe for parallel suites but all active suites use `parallel="none"` due to shared QA test data  
- **OTP fallback**: hardcoded `"5100"` returned when `captureOTPFromGraphQL()` fails — fragile, needs dedicated QA secret  
- **safePageLoad()**: tries NETWORKIDLE (5s timeout) then falls back to LOAD — required because app has persistent WebSocket connections  
- **RetryConfig**: `IRetryAnalyzer` implementation exists but is NOT wired to any suite XML — flaky tests will not auto-retry

---

## 9. COVERAGE SNAPSHOT

| Module | Automated | Tests | Coverage Estimate |
|---|---|---|---|
| Auth / Login | Yes | 83 | 85% |
| Registration | Yes | ~20 | 70% |
| Profile Creation Wizard | Yes | 69 | 75% |
| Dashboard | Yes | ~50 | 65% |
| Posts / Feed | Yes | 45 | 70% |
| Search | Yes | 13 | 55% |
| Messaging | Yes | 14 | 50% |
| WebSocket | Yes | ~15 | 60% |
| Notifications | Partial | ~5 | 30% |
| Subscription | Partial | 8 | 40% |
| Bookmarks | Partial | ~3 | 20% |
| Reviews | Yes | ~8 | 50% |
| Security | Stub | ~10 | 15% |
| Accessibility | Stub | 12 | 10% |
| Performance | Stub | ~8 | 10% |
| E2E Flows | Partial | ~5 | 20% |
| Settings | Yes | ~8 | 50% |
| Activity | Yes | ~5 | 40% |
| AI Features | Yes | ~5 | 50% |
| Landing / Blog | Yes | ~10 | 60% |
| **TOTAL** | | **~477** | **~55%** |

---

*Report generated by audit scan on 2026-06-17. Re-run scan after each sprint to keep inventory current.*
