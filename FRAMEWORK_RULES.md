# Toskie Web Automation Framework — Rules & Conventions

## 1. SPA Rendering Rules

The app is a **React Single-Page Application**. These rules are mandatory.

### NEVER use these for element detection
```java
// WRONG — returns 0 before SPA renders
if (locator.count() > 0) { ... }
if (locator.isVisible()) { ... }
waitForLoadState(DOMCONTENTLOADED)   // fires before React paints
waitForURL(predicate)                // URL never changes on SPA route swap
```

### ALWAYS use waitFor() first
```java
// CORRECT — waits up to N ms for element to appear
locator.waitFor(new Locator.WaitForOptions()
    .setState(WaitForSelectorState.VISIBLE)
    .setTimeout(5000));
// Then safe to assert
Assert.assertTrue(locator.count() > 0, "...");
```

### Detecting navigation / component unmount
```java
// CORRECT — waits for an element to DISAPPEAR (e.g. welcome slides dismissed)
NextBT.waitFor(new Locator.WaitForOptions()
    .setState(WaitForSelectorState.HIDDEN)
    .setTimeout(10000));
```

### After navigating to a form page
Always wait for a known form element to be visible before asserting field presence:
```java
page.locator("//input[@placeholder='Enter First Name']")
    .waitFor(new Locator.WaitForOptions()
        .setState(WaitForSelectorState.VISIBLE)
        .setTimeout(10000));
```

---

## 2. Login Rules

### Use QA GraphQL bypass — never real OTP
```java
utilLayer.loginViaQAGraphQL(mobile);  // HTTP call, no browser
utilLayer.injectTokenFull();           // writes to localStorage + sessionStorage
utilLayer.injectCookies();             // sets cookies in browser context
```

### Login button is conditional
`LoginPage.loginWithValidCredentials()` conditionally clicks the Login button only if visible (5s timeout). If the app is already past the welcome screen (e.g. home page is showing), it skips the click and proceeds directly with QA bypass. **Do not expect the Login button to always be present.**

### Token verification after login
```java
Assert.assertNotNull(ApiUtils.getAccessToken(), "...");
Assert.assertFalse(ApiUtils.isTokenExpired(), "...");
```

---

## 3. Welcome / Onboarding Rules

### Current app CTA (2026-06-09)
The final welcome slide shows **"Start Exploring"** — not "Create My Profile". The "Create My Profile" element still exists in the DOM but is not the active CTA.

`validateWelcomeToToskieLandingPage()` handles both builds automatically:
- Tries "Start Exploring" first (3s timeout)
- Falls back to force-clicking "Create My Profile"

### After onboarding completes
The URL **does not change**. Do not assert URL differences. The correct assertion is:
```java
Assert.assertFalse(welcomePage.isNextButtonStillVisible(),
    "Next button must be gone after onboarding completes");
```

### isWelcomePageLoaded()
Returns `true` if either `NextBT` or `StartExploring` is visible within the timeout. Always call this before `validateWelcomeToToskieLandingPage()` in tests that need a precondition check.

---

## 4. Profile Creation Rules

### Email OTP — always install route mocks first
```java
ApiUtils.enableEmailOTPBypass(email);   // route mock: fake send-otp + verify-otp
utilLayer.bypassEmailOTP(email);        // backend: mark email as verified in DB
```

These must be called **before** interacting with the email field, so the route mock is active when the Send OTP button is clicked.

### Recovery after redirect
If the app redirects away from `/user-registration` (e.g. to `/non-loggedin-profile`), `ToskieCreateProfile.ensureOnRegistrationPage()` navigates back, re-injects tokens, and re-fills name fields. This is handled internally — no action needed in test code.

### DOB calendar
- Click the year header to open year/month picker
- Navigate years with back arrow until target year is visible
- Click the month directly — **do not click the year button again** (collapses picker)
- Target DOB: **1997-May-25**

---

## 5. TestNG XML Rules

### One class per dedicated XML
Each active test class has its own `testng-<name>.xml`. Never add extra classes to a single-class suite file.

```xml
<suite name="Suite Name" parallel="none" thread-count="1" verbose="2">
    <parameter name="browser" value="chrome"/>
    <parameter name="baseUrl"  value="https://dev.app.toskie.com/"/>
    <test name="Test Name">
        <classes>
            <class name="com.toskie.YourTestClass"/>
        </classes>
    </test>
</suite>
```

### Always use `parallel="none"`
Tests share a single browser instance via `BrowserManager`. Parallel execution will cause race conditions.

### Run command pattern
```bash
mvn test -Dsuite.xml.file=testng-<name>.xml -B
```

---

## 6. Locator Rules

### Use XPath with text matching for SPA elements
```java
// Preferred for React-rendered text buttons
page.locator("//span[contains(text(),'Next')]")
page.locator("//button[@id='MALE']")
page.locator("//input[@placeholder='Enter First Name']")
```

### Locators belong in OrPages classes
Never define locators directly in test classes or Page action classes. Add them to the appropriate `*Or.java` base class.

```
WelcomeToToskieLandingPageOr  ← NextBT, StartExploring, CreateMYProfile
LoginPageOr                   ← ClickOnLoginBT, mobile input
ToskieCreateProfileOr         ← FirstName, LastName, EmailField, gender buttons, DOB, etc.
```

---

## 7. Assertions Rules

### Prefer meaningful failure messages
```java
// GOOD
Assert.assertTrue(condition, "Next button must be visible — SPA not rendered yet");

// BAD
Assert.assertTrue(condition);
```

### Do not assert URL equality for SPA navigation
The app uses same-URL state rendering. `url().equals(before)` will always be true after React route changes.

### Assert element state, not URL
```java
// CORRECT — meaningful for SPA
Assert.assertFalse(welcomePage.isNextButtonStillVisible(), "...");
Assert.assertTrue(locator.count() > 0, "...");

// WRONG — always equal for SPA
Assert.assertNotEquals(urlAfter, urlBefore);
```

---

## 8. Screenshot and Reporting Rules

- Screenshots are auto-captured in `@AfterMethod` by `BaseTest` — no manual capture needed in tests.
- Naming: `{testMethodName}_{dd_MM_yyyy_HH_mm_ss_AM/PM}.png`
- HTML + PDF reports are generated in `@AfterSuite` — always present after a run.
- Report location: `Reports/HTML/` and `Reports/PDF/`

---

## 9. BaseTest Inheritance Rules

- All test classes **must extend `BaseTest`**.
- `utilLayer` is available in every test method via `BaseTest.utilLayer`.
- `browser` and `baseUrl` are read from `config.properties` with `-D` override support.
- Do not override `@BeforeSuite`, `@BeforeMethod`, `@AfterMethod`, or `@AfterSuite` in test classes unless absolutely necessary.

---

## 10. Known Gotchas

| Gotcha | Details |
|---|---|
| SPA same-URL routing | URL never changes after React route swaps — use element state, not URL |
| Login button absent | After `validateWelcomeToToskieLandingPage()`, home page shows — no Login button |
| `count() > 0` race | Always `waitFor(VISIBLE)` before calling `count()` or `isVisible()` |
| `navigate()` clears storage | After `page.navigate()`, localStorage is wiped — re-inject tokens |
| "Create My Profile" vs "Start Exploring" | App CTA changed; both handled in `validateWelcomeToToskieLandingPage()` |
| OTP dialog may not appear | Route mock must be installed *before* filling the email field |
| DOB year picker collapse | Clicking year button again collapses the picker — click month directly |
| Two `LoginPage.java` files | `AuthenticationPages/Page/LoginPage.java` is active; `pages/LoginPage.java` is legacy |
