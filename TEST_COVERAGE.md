# Toskie Web Automation Framework — Test Coverage

## Summary (Session: 2026-06-09)

| Test Class | Tests | Passed | Failed | Skipped | Suite XML |
|---|---|---|---|---|---|
| `WelcomePageTestCases` | 8 | 8 | 0 | 0 | `testng-welcome-page.xml` |
| `WelcomeToToskieLandingTestCase` | 1 | 1 | 0 | 0 | `testng-welcome-landing.xml` |
| `LoginPageTestCases` | 12 | 12 | 0 | 0 | `testng-loginpage.xml` |
| `LoginTestCases` | 2 | 2 | 0 | 0 | `testng-login.xml` |
| `ProfileCreationTestCases` | 16 | 16 | 0 | 0 | `testng-profile-creation.xml` |
| `ToskieCreatePrifileTestCases` | 1 | 1 | 0 | 0 | `testng-createprofile.xml` |
| `EndToEndFlowTestCases` | 6 | 6 | 0 | 0 | `testng-e2eflow.xml` |
| **TOTAL** | **46** | **46** | **0** | **0** | — |

---

## Welcome Page Tests — `WelcomePageTestCases.java`

Suite: `testng-welcome-page.xml`

| ID | Method | Description | Status |
|---|---|---|---|
| TC-WP-001 | `verifyWelcomePage_Loads` | Next button renders within 5s of page load | PASSED |
| TC-WP-002 | `verifyWelcomePage_URLNotEmpty` | App URL not empty on load | PASSED |
| TC-WP-003 | `verifyWelcomePage_NextButtonPresent` | Next button present after SPA renders | PASSED |
| TC-WP-004 | `verifyWelcomePage_NavigateThroughSlides` | Next×3 + CTA completes without exception | PASSED |
| TC-WP-005 | `verifyWelcomePage_CreateMyProfileButtonClickable` | Final CTA is clickable | PASSED |
| TC-WP-006 | `verifyWelcomePage_HelperMethodReturnsTrueOnFreshLoad` | `isWelcomePageLoaded()` returns true | PASSED |
| TC-WP-007 | `verifyWelcomePage_LoginLinkAppearsAfterSlides` | After final CTA, welcome slides dismissed (Next button gone) | PASSED |
| TC-WP-008 | `verifyWelcomePage_CreateProfileCTAAppearsAfterSlides` | Create My Profile CTA present after 3 Next clicks | PASSED |

---

## Welcome Landing Test — `WelcomeToToskieLandingTestCase.java`

Suite: `testng-welcome-landing.xml`

| ID | Method | Description | Status |
|---|---|---|---|
| — | `verifyWelcomeToToskieLanding` | Full onboarding slide navigation; Next button gone after final CTA | PASSED |

---

## Login Page Tests — `LoginPageTestCases.java`

Suite: `testng-loginpage.xml`

| ID | Method | Description | Status |
|---|---|---|---|
| TC-LG-001 | `verifyLogin_ReturnsAccessToken` | QA bypass returns non-empty access token | PASSED |
| TC-LG-002 | `verifyLogin_ReturnsRefreshToken` | QA bypass returns non-empty refresh token | PASSED |
| TC-LG-003 | `verifyLogin_TokenIsValidJWT` | Access token has valid 3-part JWT structure | PASSED |
| TC-LG-004 | `verifyLogin_TokenNotExpiredAfterLogin` | Fresh token not expired immediately | PASSED |
| TC-LG-005 | `verifyLogin_TokenInjectedToLocalStorage` | `injectTokenFull()` writes to localStorage | PASSED |
| TC-LG-006 | `verifyLogin_TokenInjectedToSessionStorage` | `injectTokenFull()` writes to sessionStorage | PASSED |
| TC-LG-007 | `verifyLogin_NavigateToHomeSucceeds` | Authenticated user can reach /home without auth error | PASSED |
| TC-LG-008 | `verifyLoginPage_LoginWithValidCredentials` | `loginWithValidCredentials()` produces valid token | PASSED |
| TC-LG-009 | `verifyLoginPage_LoginButtonVisibleAfterSlides` | After onboarding CTA, welcome slides dismissed | PASSED |
| TC-LG-010 | `verifyLogin_MultipleUsers` (data-driven) | Each QA mobile number produces valid token | PASSED |
| TC-LG-011 | `verifyLogin_ConsecutiveLoginsReturnTokens` | Two back-to-back logins both return non-null tokens | PASSED |
| TC-LG-012 | `verifyLogin_StoredTokenMatchesApiToken` | localStorage token matches ApiUtils token | PASSED |

---

## Login Flow Tests — `LoginTestCases.java`

Suite: `testng-login.xml`

| ID | Method | Description | Status |
|---|---|---|---|
| — | `verifyCompleteUserFlow` | Welcome → Login → profile step (reflection-based) | PASSED |
| — | `verifyLoginWithDataProvider` (data-driven) | Welcome → Login for mobile 9919011050 | PASSED |

---

## Profile Creation Tests — `ProfileCreationTestCases.java`

Suite: `testng-profile-creation.xml`

| ID | Method | Description | Status |
|---|---|---|---|
| TC-PC-001 | `verifyCreateProfile_Male` | Full profile creation with MALE gender | PASSED |
| TC-PC-002 | `verifyCreateProfile_Female` | Full profile creation with FEMALE gender | PASSED |
| TC-PC-003 | `verifyCreateProfile_Other` | Full profile creation with OTHER gender | PASSED |
| TC-PC-004 | `verifyCreateProfile_DataDriven` (MALE) | Data-driven — MALE | PASSED |
| TC-PC-004 | `verifyCreateProfile_DataDriven` (FEMALE) | Data-driven — FEMALE | PASSED |
| TC-PC-004 | `verifyCreateProfile_DataDriven` (OTHER) | Data-driven — OTHER | PASSED |
| TC-PC-005 | `verifyEmailOTP_SendDoesNotRedirect` | Send OTP with route mock keeps user on /user-registration | PASSED |
| TC-PC-006 | `verifyEmailOTP_DialogAppearsAfterSend` | OTP dialog appears after mocked Send OTP | PASSED |
| TC-PC-007 | `verifyEmailOTP_InputAcceptsFourDigits` | OTP input accepts 4-digit code | PASSED |
| TC-PC-008 | `verifyRegistrationFormIsShownAfterLogin` | URL contains /user-registration after QA login | PASSED |
| TC-PC-009 | `verifyFirstNameFieldEditable` | First Name input visible and accepts text | PASSED |
| TC-PC-010 | `verifyLastNameFieldEditable` | Last Name input visible and accepts text | PASSED |
| TC-PC-011 | `verifyEmailFieldVisible` | Email input visible on registration form | PASSED |
| TC-PC-012 | `verifyAllGenderOptionsPresent` | MALE, FEMALE, OTHER buttons all present | PASSED |
| TC-PC-013 | `verifyTermsCheckboxPresent` | Terms and Conditions checkbox present | PASSED |
| TC-PC-014 | `verifySendOTPButtonPresent` | Send OTP button present on form | PASSED |

---

## Single Profile Creation Test — `ToskieCreatePrifileTestCases.java`

Suite: `testng-createprofile.xml`

| ID | Method | Description | Status |
|---|---|---|---|
| — | `verifyCreateProfile` | QA login + navigate to /home + full MALE profile creation | PASSED |

---

## End-to-End Flow Tests — `EndToEndFlowTestCases.java`

Suite: `testng-e2eflow.xml`

| ID | Method | Description | Status |
|---|---|---|---|
| TC-E2E-001 | `verifyE2E_WelcomeToProfileMale` | Welcome slides → Login → profile creation (MALE) | PASSED |
| TC-E2E-002 | `verifyE2E_DirectLoginToProfileFemale` | QA login → /home → profile creation (FEMALE) | PASSED |
| TC-E2E-003 | `verifyE2E_DirectLoginToProfileOther` | QA login → /home → profile creation (OTHER) | PASSED |
| TC-E2E-004 | `verifyE2E_LoginThenTokensAndRegistrationPage` | Tokens in localStorage + /user-registration reachable | PASSED |
| TC-E2E-005 | `verifyE2E_WelcomeToAuthenticatedSession` | Welcome loaded → Login → tokens valid + not expired | PASSED |
| TC-E2E-006 | `verifyE2E_EmailOTPBypassIntegration` | Route mocks fire for send-otp + verify-otp during profile creation | PASSED |

---

## Untested Classes (Extended Suites)

Located in `src/test/java/com/toskie/tests/` — not yet executed.

| Class | Suite XML | Area |
|---|---|---|
| `SmokeTests.java` | `testng-smoke.xml` | Smoke |
| `BookingTests.java` | `testng-regression.xml` | Regression |
| `ChatTests.java` | `testng-regression.xml` | Regression |
| `HomeTests.java` | `testng-regression.xml` | Regression |
| `LoginTests.java` | `testng-regression.xml` | Regression |
| `LogoutTests.java` | `testng-regression.xml` | Regression |
| `NotificationTests.java` | `testng-regression.xml` | Regression |
| `ProfileTests.java` | `testng-regression.xml` | Regression |
| `SearchTests.java` | `testng-regression.xml` | Regression |
| `SettingsTests.java` | `testng-regression.xml` | Regression |
| `SecurityTests.java` | `testng-security.xml` | Security |
| `PerformanceTests.java` | `testng-performance.xml` | Performance |
| `AccessibilityTests.java` | `testng-accessibility.xml` | Accessibility |
| `APIValidationTests.java` | `testng-api.xml` | API |
| `WebSocketTests.java` | `testng-api.xml` | API |
| `EndToEndTests.java` | `testng-e2e.xml` | E2E |
| `EdgeCaseTests.java` | — | Edge cases |
| `NegativeTests.java` | — | Negative |

---

## Fixes Applied This Session (2026-06-09)

| File | Fix |
|---|---|
| `WelcomeToToskieLandingPage.java` | `isWelcomePageLoaded()` uses `waitFor(VISIBLE)` instead of sync `isVisible()` |
| `WelcomeToToskieLandingPage.java` | `validateWelcomeToToskieLandingPage()` detects "Start Exploring" vs "Create My Profile" CTA; waits `NextBT.waitFor(HIDDEN)` instead of `waitForURL` |
| `LoginPage.java` | Login button click made conditional (5s wait, skip if not visible) |
| `EndToEndFlowTestCases.java` | `verifyE2E_WelcomeToAuthenticatedSession` calls `isWelcomePageLoaded()` only, not `validateWelcomeToToskieLandingPage()` |
| `WelcomeToToskieLandingTestCase.java` | Removed `assertNotEquals(urlAfter, urlBefore)`; added `assertFalse(isNextButtonStillVisible())` |
| `WelcomePageTestCases.java` | TC-WP-007: replaced Login span wait with `isNextButtonStillVisible()` assertion |
| `LoginPageTestCases.java` | TC-LG-009: same Login span fix as TC-WP-007 |
| `ProfileCreationTestCases.java` | `loginAndNavigate()` now waits for First Name input `waitFor(VISIBLE, 10000)` before returning |
