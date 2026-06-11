# Toskie Web Automation Framework — Context

## Overview

End-to-end browser automation framework for the Toskie web application (`https://dev.app.toskie.com/`).
Built with Java + Playwright, orchestrated by TestNG, and reported via ExtentReports.

---

## Tech Stack

| Layer | Technology | Version |
|---|---|---|
| Language | Java | 21 |
| Build tool | Apache Maven | 3.9.14 |
| Browser automation | Microsoft Playwright (Java) | 1.53.0 |
| Test framework | TestNG | 7.9.0 |
| Reporting | ExtentReports | 5.0.9 |
| PDF reports | iText PDF | bundled |
| IDE | Eclipse / VS Code | — |
| OS | Windows 11 | — |

---

## Application Under Test

| Property | Value |
|---|---|
| Environment | DEV |
| Base URL | `https://dev.app.toskie.com/` |
| API (GraphQL) | `https://toskie-api.wasd.in/graphql` |
| App type | React Single-Page Application (SPA) |
| Auth model | Phone OTP login (bypassed in QA via GraphQL mutation) |

### Critical SPA Behaviour
- The app renders all routes at the **same URL** (`https://dev.app.toskie.com/`) using React state.
- `waitForURL()` and `waitForLoadState(DOMCONTENTLOADED)` are **unreliable** for detecting route changes.
- Use `locator.waitFor(VISIBLE/HIDDEN)` to detect component mount/unmount.
- After clicking "Start Exploring" on the welcome screen, the home page renders **in-place** — URL never changes.

---

## Project Structure

```
Toskie_Web_Automation_Framework/
│
├── FRAMEWORK_CONTEXT.md                      ← This file
├── TEST_COVERAGE.md                          ← Test inventory and pass/fail status
├── FRAMEWORK_RULES.md                        ← Coding rules and conventions
├── pom.xml                                   ← Maven build + dependency config
├── TODO.md                                   ← Pending tasks and notes
│
├── [Root config / debug files]
│   ├── config / debug PNGs / run logs
│   └── master_run_output.txt, auth_run.txt
│
├── [TestNG Suite XMLs — 19 total]
│   ├── testng.xml                            ← Default suite
│   ├── testng-master.xml                     ← Runs all active classes
│   ├── testng-all-testcases.xml
│   ├── testng-auth.xml                       ← Multi-class auth suite
│   ├── testng-e2e.xml
│   ├── testng-e2eflow.xml                    ← EndToEndFlowTestCases only
│   ├── testng-login.xml                      ← LoginTestCases only
│   ├── testng-loginpage.xml                  ← LoginPageTestCases only
│   ├── testng-welcome-page.xml               ← WelcomePageTestCases only
│   ├── testng-welcome-landing.xml            ← WelcomeToToskieLandingTestCase only
│   ├── testng-welcome-login.xml
│   ├── testng-createprofile.xml              ← ToskieCreatePrifileTestCases only
│   ├── testng-profile-creation.xml           ← ProfileCreationTestCases only
│   ├── testng-smoke.xml
│   ├── testng-regression.xml
│   ├── testng-security.xml
│   ├── testng-api.xml
│   ├── testng-accessibility.xml
│   └── testng-performance.xml
│
├── src/
│   ├── main/
│   │   ├── java/com/toskie/
│   │   │   ├── AuthenticationPages/
│   │   │   │   ├── OrPages/                  ← Locator base classes (PageObject pattern)
│   │   │   │   │   ├── ToskieCreateProfileOr.java
│   │   │   │   │   └── page/
│   │   │   │   │       ├── LoginPageOr.java
│   │   │   │   │       └── WelcomeToToskieLandingPageOr.java
│   │   │   │   └── Page/                     ← Page action classes (extend OrPages)
│   │   │   │       ├── LoginPage.java        ← ACTIVE
│   │   │   │       ├── ToskieCreateProfile.java
│   │   │   │       └── WelcomeToToskieLandingPage.java
│   │   │   ├── locators/                     ← Legacy locator classes (not active)
│   │   │   │   ├── BookingLocators.java
│   │   │   │   ├── ChatPageLocators.java
│   │   │   │   ├── HomePageLocators.java
│   │   │   │   ├── LoginPageLocators.java
│   │   │   │   ├── NotificationsLocators.java
│   │   │   │   ├── ProfileCreationLocators.java
│   │   │   │   ├── ProfileViewLocators.java
│   │   │   │   ├── SearchPageLocators.java
│   │   │   │   ├── SettingsPageLocators.java
│   │   │   │   └── WelcomePageLocators.java
│   │   │   ├── pages/                        ← Legacy page classes (not active)
│   │   │   │   ├── BookingPage.java
│   │   │   │   ├── ChatPage.java
│   │   │   │   ├── HomePage.java
│   │   │   │   ├── LoginPage.java            ← LEGACY (use AuthenticationPages/Page/ instead)
│   │   │   │   ├── ProfileCreationPage.java
│   │   │   │   ├── SearchPage.java
│   │   │   │   └── WelcomePage.java
│   │   │   └── utils_Layer/                  ← Core framework utilities
│   │   │       ├── ApiUtils.java
│   │   │       ├── BrowserManager.java
│   │   │       ├── ConfigManager.java
│   │   │       ├── ReportManager.java
│   │   │       ├── RetryConfig.java
│   │   │       ├── UtilLayer.java
│   │   │       └── WaitManager.java
│   │   └── resources/
│   │       └── config.properties             ← Runtime config (URLs, browser, credentials)
│   │
│   └── test/
│       ├── java/com/toskie/
│       │   ├── [Active test classes]
│       │   │   ├── AllToskieTestCases.java
│       │   │   ├── EndToEndFlowTestCases.java      ← TC-E2E-001..006  (6 PASSED)
│       │   │   ├── LoginPageTestCases.java         ← TC-LG-001..012   (12 PASSED)
│       │   │   ├── LoginTestCases.java             ← 2 tests          (2 PASSED)
│       │   │   ├── ProfileCreationTestCases.java   ← TC-PC-001..014   (16 PASSED)
│       │   │   ├── ToskieCreatePrifileTestCases.java ← 1 test         (1 PASSED)
│       │   │   ├── WelcomePageTestCases.java       ← TC-WP-001..008   (8 PASSED)
│       │   │   └── WelcomeToToskieLandingTestCase.java ← 1 test       (1 PASSED)
│       │   ├── BaseTest_Layer/
│       │   │   └── BaseTest.java                   ← @Before/@After lifecycle
│       │   └── tests/                              ← Extended suites (not yet run)
│       │       ├── accessibility/
│       │       │   └── AccessibilityTests.java
│       │       ├── api/
│       │       │   ├── APIValidationTests.java
│       │       │   └── WebSocketTests.java
│       │       ├── e2e/
│       │       │   └── EndToEndTests.java
│       │       ├── edge/
│       │       │   └── EdgeCaseTests.java
│       │       ├── negative/
│       │       │   └── NegativeTests.java
│       │       ├── performance/
│       │       │   └── PerformanceTests.java
│       │       ├── regression/
│       │       │   ├── BookingTests.java
│       │       │   ├── ChatTests.java
│       │       │   ├── HomeTests.java
│       │       │   ├── LoginTests.java
│       │       │   ├── LogoutTests.java
│       │       │   ├── NotificationTests.java
│       │       │   ├── ProfileTests.java
│       │       │   ├── SearchTests.java
│       │       │   └── SettingsTests.java
│       │       ├── security/
│       │       │   └── SecurityTests.java
│       │       └── smoke/
│       │           └── SmokeTests.java
│       └── resources/
│           ├── config.properties
│           ├── testcases/
│           │   ├── AllToskieTestScripts_Mapping.csv
│           │   └── Toskie_Web_Desktop_TestCases.csv
│           └── testdata/
│               ├── edge-case-data.json
│               ├── login-data.json
│               ├── negative-data.json
│               ├── profile-data.json
│               ├── search-data.json
│               └── security-payloads.json
│
├── Reports/
│   ├── HTML/                                 ← ExtentReports HTML (one per run)
│   ├── PDF/                                  ← PDF reports (one per run)
│   └── Charts/
│       ├── piechart.png
│       └── barchart.png
│
├── SnapShots/                                ← Full-page screenshots per test (name + timestamp)
├── target/                                   ← Maven build output + surefire reports
│   └── surefire-reports/                     ← JUnit XML + TestNG HTML per suite
└── test-output/                              ← TestNG default HTML output
```

---

## Core Utility Classes

### `UtilLayer` — `src/main/java/com/toskie/utils_Layer/UtilLayer.java`
Central action hub. All page interactions go through this class.

| Method | Purpose |
|---|---|
| `loginViaQAGraphQL(mobile)` | Pure HTTP GraphQL call — no browser interaction |
| `injectTokenFull()` | Writes access + refresh tokens to localStorage and sessionStorage |
| `injectCookies()` | Sets auth cookies in the browser context |
| `bypassEmailOTP(email)` | Calls QA GraphQL mutation to mark email as verified in the DB |
| `enableEmailOTPBypass(email)` | Installs Playwright route mocks for send-otp and verify-otp endpoints |
| `fill(locator, value, name)` | Fills an input field with logging |
| `typeValue(locator, value)` | Types value character by character |
| `click(locator, name)` | Standard click with logging |
| `forceClick(locator, name)` | `click({force:true})` — bypasses actionability checks |
| `clickThreeTimes(locator, name)` | Clicks a locator 3 times (used for welcome slide navigation) |

### `BrowserManager` — `src/main/java/com/toskie/utils_Layer/BrowserManager.java`
Manages Playwright browser lifecycle. `BrowserManager.getPage()` returns the current `Page` instance.

### `ApiUtils` — `src/main/java/com/toskie/utils_Layer/ApiUtils.java`
Handles all HTTP/GraphQL API calls outside the browser.

| Method | Purpose |
|---|---|
| `getAccessToken()` | Returns the last issued access token |
| `getRefreshToken()` | Returns the last issued refresh token |
| `isTokenExpired()` | Decodes JWT and checks expiry |

### `ConfigManager` — `src/main/java/com/toskie/utils_Layer/ConfigManager.java`
Reads `config.properties`. All values overridable via `-D` system properties at runtime.

### `ReportManager` — `src/main/java/com/toskie/utils_Layer/ReportManager.java`
Manages ExtentReports HTML + PDF generation. Called by `BaseTest.afterSuite()`.

### `WaitManager` — `src/main/java/com/toskie/utils_Layer/WaitManager.java`
Provides `waitForPageLoad(LoadState)` and other wait utilities.

---

## Page Object Architecture

```
WelcomeToToskieLandingPageOr  (locators only)
    └── WelcomeToToskieLandingPage  (actions + assertions)

LoginPageOr  (locators only)
    └── LoginPage  (actions + assertions)

ToskieCreateProfileOr  (locators only)
    └── ToskieCreateProfile  (actions + assertions)
```

---

## BaseTest Lifecycle

```
@BeforeSuite  → create UtilLayer, init ExtentReports
@BeforeMethod → launch browser, navigate to baseUrl, log test start
@AfterMethod  → capture full-page screenshot, log pass/fail/skip, tearDown browser
@AfterSuite   → generate HTML + PDF reports, save charts
```

Screenshot naming: `{testMethodName}_{dd_MM_yyyy_HH_mm_ss_AM/PM}.png` → saved to `SnapShots/`

---

## Configuration (`config.properties`)

```properties
baseUrl=https://dev.app.toskie.com/
apiUrl=https://toskie-api.wasd.in/graphql
browser=chrome
headless=false
testMobile=9919011050
testEmail=gptsant@gmail.com
implicit.wait=10
page.load.timeout=30000
```

All properties can be overridden at runtime:
```bash
mvn test -Dbrowser=firefox -DtestMobile=9876543210 -Dsuite.xml.file=testng-smoke.xml
```

---

## QA Login Bypass

The app requires phone OTP for real login. QA environments expose a `QA_Bypass_Login` GraphQL mutation that issues valid JWT tokens without OTP.

**Flow:**
1. `utilLayer.loginViaQAGraphQL(mobile)` — POST to GraphQL, stores access + refresh tokens in `ApiUtils`
2. `utilLayer.injectTokenFull()` — writes tokens to `localStorage` and `sessionStorage`
3. `utilLayer.injectCookies()` — sets cookies in Playwright browser context
4. `page.navigate("/home")` — app validates tokens, redirects to `/user-registration` for new users

**Email OTP bypass (profile creation):**
- `ApiUtils.enableEmailOTPBypass(email)` installs route mocks: `send-email-otp → fake 200`, `verify-email-otp → fake 200`
- `utilLayer.bypassEmailOTP(email)` calls `QA_Bypass_Verify_Email_Otp` mutation to mark email as verified in the DB

---

## Running Tests

```bash
# Run a specific class
mvn test -Dsuite.xml.file=testng-welcome-page.xml -B

# Run all active test suites
mvn test -Dsuite.xml.file=testng-master.xml -B

# Override browser or credentials
mvn test -Dsuite.xml.file=testng-smoke.xml -Dbrowser=firefox -DtestMobile=9876543210 -B
```

Default suite (when no `-D` override): `testng-master.xml`
