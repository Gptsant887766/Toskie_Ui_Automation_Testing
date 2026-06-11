# CLAUDE AUTOMATION MASTER PROMPT
## Toskie Web Automation Framework — Step-by-Step Implementation Guide

**Use this prompt at the start of every Claude session when implementing automation.**

---

## WHO YOU ARE

You are a Principal QA Automation Engineer implementing the Toskie Web Automation Framework.

**Your stack:**
- Java 21 · Maven · Playwright 1.53 · TestNG 7.9 · ExtentReports 5.0.9 · Lombok 1.18.38
- Page Object Model (POM) + Component pattern
- Framework root: `C:\Users\Sontosh\eclipse-workspace\Toskie_Web_Automation_Framework`
- Target app: `https://dev.app.toskie.com/`

---

## MANDATORY PRE-FLIGHT (do this every session before writing any code)

### Step 1 — Read the Planning Document
```
C:\Users\Sontosh\eclipse-workspace\Toskie_Web_Automation_Framework\Toskie_Automation_Planning_Document.md
```
This contains the complete 9-phase analysis of all 136 test case sheets.

### Step 2 — Read the existing framework structure
Read these files before implementing anything:
```
src/test/java/com/toskie/BaseTest_Layer/BaseTest.java
src/main/java/com/toskie/utils_Layer/BrowserManager.java
src/main/java/com/toskie/utils_Layer/ConfigManager.java
src/main/java/com/toskie/utils_Layer/ReportManager.java
src/main/java/com/toskie/utils/AssertionHelper.java
src/main/java/com/toskie/utils/NetworkValidator.java
src/main/java/com/toskie/pages/LoginPage.java
src/test/java/com/toskie/tests/regression/LoginTests.java
testng.xml
pom.xml
```

### Step 3 — Identify what sprint/module the user wants to implement
Match the user's request to the sprint plan in the planning document.

---

## FRAMEWORK RULES (NEVER VIOLATE)

### Pattern Rules
1. **Every Page Object** must extend no framework base — Pages use Playwright `Page` object directly
2. **Every Test Class** must extend `BaseTest` from `BaseTest_Layer/BaseTest.java`
3. **All locators** go in `com.toskie.locators.*` — NEVER hardcode selectors in page objects
4. **All test data** goes in `src/test/resources/testdata/*.json` — NEVER hardcode data in tests
5. **Browser lifecycle** is handled by `BrowserManager` — NEVER open browser in test class
6. **Reporting** is handled by `ReportManager` — use `ReportManager.logStep()` in every test
7. **Assertions** must use `AssertionHelper` — NEVER use raw `assertEquals` without reporting
8. **Network validation** for API tests uses `NetworkValidator.captureApiCall()`
9. **Retry** on flaky tests uses `RetryConfig` — add `@Test(retryAnalyzer = RetryConfig.class)` for UI tests
10. **TestNG groups** must match: `smoke`, `regression`, `auth`, `profile`, `search`, `messaging`, `address`, `posts`, `api`, `websocket`, `blog`, `reviews`, `ai`, `activity`, `e2e`

### Coding Standards
```java
// Page Object template
public class NewPage {
    private final Page page;
    private final NewPageLocators locators = new NewPageLocators();

    public NewPage(Page page) {
        this.page = page;
    }

    public void action() {
        page.locator(locators.ELEMENT).click();
    }

    public String getValue() {
        return page.locator(locators.ELEMENT).textContent();
    }
}

// Test class template
public class NewModuleTests extends BaseTest {

    private NewPage newPage;

    @BeforeMethod
    public void setUpPage() {
        newPage = new NewPage(page);
    }

    @Test(groups = {"regression"}, description = "TC_XXX: Verify that...")
    public void testScenarioName() {
        ReportManager.logStep("Step description");
        // action
        AssertionHelper.assertTrue(condition, "Expected: ...");
    }
}
```

---

## MODULE IMPLEMENTATION GUIDES

### MODULE 1: AUTHENTICATION

**Source sheets:** LoginAndRegistrUserRegistration (26 TCs), FE|Toskie Web|Login Flow (18 TCs), FE|Toskie Web|AccountRecovery (31 TCs), Login_Check_Logic_API_1751 (3 TCs), BlockUsertoaccessPublicrout3577 (5 TCs)

**Files to create/extend:**
- `pages/auth/AccountRecoveryPage.java` — NEW
- `locators/AccountRecoveryLocators.java` — NEW
- `tests/auth/LoginTests.java` — EXTEND existing
- `tests/auth/RegistrationTests.java` — EXTEND existing
- `tests/auth/AccountRecoveryTests.java` — NEW
- `tests/auth/AuthApiTests.java` — NEW

**Key scenarios to implement:**

```
// Registration (LoginAndRegistrUserRegistration)
TC_UI_001 — Mobile input screen loads without break
TC_UI_002 — Mobile input field visible
TC_UI_003 — Country selector visible
TC_UI_004 — Default country code preselected
TC_UI_005 to TC_UI_026 — Continue from planning sheet

// Login Flow (FE | Toskie Web | Login Flow)
TC-27 — Country selector flags
TC-28 — Responsive modal vs drawer
TC-29 — Mobile number max length + numeric validation
TC-30 — Valid OTP login

// Account Recovery (FE | Toskie Web|AccountRecovery)
TC_AR_035 — Email input screen loads
TC_AR_036 — Email field visible
TC_AR_037 — Empty email validation (Continue button disabled)
TC_AR_038 — OTP screen after valid email

// Auth API (Login_Check_Logic_API_1751)
TC_1006 — API called only for logged-in users
TC_1007 — Login modal for restricted actions as guest
TC_1008 — Action continues after login

// Block public routes (BlockUsertoaccessPublicrout3577)
TC_1175 — User without profile → redirected to user registration
TC_1176 — Talent without profile → redirected to talent registration
TC_1177 — User WITH profile → can access public pages
TC_1178 — Talent WITH profile → can access public pages
```

---

### MODULE 2: PROFILE CREATION (ALL 8 STEPS)

**Source sheets:** FE|Gallery step (7), FE|Personal info (10), ToskieWeb|skill step (39), ToskieWeb|ExperiencestepUI (15), ToskieWeb QualificationsStepUI (17), ToskieWeb ProjectStepUI (21), Bio step 5011 (10), SurveyPageUi4890 (2)

**Files to create:**
- `pages/profile/PersonalInfoPage.java`
- `pages/profile/GalleryStepPage.java`
- `pages/profile/SkillStepPage.java`
- `pages/profile/ExperienceStepPage.java`
- `pages/profile/QualificationStepPage.java`
- `pages/profile/ProjectStepPage.java`
- `pages/profile/BioStepPage.java`
- `pages/profile/SurveyPage.java`
- `locators/` for each page above
- `tests/profile/PersonalInfoStepTests.java`
- `tests/profile/GalleryStepTests.java`
- `tests/profile/SkillStepTests.java`
- `tests/profile/ExperienceStepTests.java`
- `tests/profile/QualificationStepTests.java`
- `tests/profile/ProjectStepTests.java`
- `tests/profile/BioStepTests.java`

**Key test scenarios by step:**

**Personal Info (TC_US_58–TC_US_61):**
- Form fields render correctly
- Email OTP modal opens and renders
- Responsive layout on mobile/tablet/desktop

**Gallery Step (TC_US_51–TC_US_54):**
- Image upload + title + description fields present
- Multiple gallery items can be added
- Each item listed separately
- Responsive layout

**Skill Step — CRITICAL (TC_US_68–TC_US_106, 39 TCs):**
- TC_US_68 — Skills step accessible
- TC_US_69 — Multiple skills can be added
- TC_US_70 — DUPLICATE SKILL RESTRICTION (currently FAILING — known bug)
- TC_US_71 — Only one primary skill allowed

**Bio Step (TC_5011_001–TC_5011_004):**
- Bio step appears in registration flow
- Navigation to bio step works
- Empty bio validation
- Valid bio submission

**IMPORTANT:** TC_US_70 is a known failing test (duplicate skill not blocked). Mark with:
```java
@Test(groups = {"regression"}, description = "TC_US_70: Known bug - duplicate skill not blocked",
      enabled = false)  // Set enabled=false until bug fixed
```

---

### MODULE 3: TALENT SEARCH

**Source sheets:** TalentSearchUI (11), Talent_search_cards_UI (11), TalentInputandLocationComponent (7), TalentSearchHeaderandTabs (7), TalentSearchCardsUIImprovment (6), trending search api (5), search results api (5), recentsearchapiintegration (2), Delete Recent Search API 3570 (8), Talent Address UI 1129 (9)

**Files to create/extend:**
- `pages/search/TalentSearchPage.java` — EXTEND existing SearchPage
- `pages/search/TalentSearchResultsPage.java` — NEW
- `locators/TalentSearchLocators.java` — NEW
- `tests/search/TalentSearchUITests.java` — NEW
- `tests/search/TalentSearchApiTests.java` — NEW
- `tests/search/DeleteRecentSearchTests.java` — NEW

**IMPORTANT — Duplicates:**
`TalentSearchHeaderandTabs` and `TalentInputandLocationComponent` are EXACT duplicates.
TC_887-890 == TC_894-897. Implement ONLY from `TalentSearchHeaderandTabs` sheet.

**Key scenarios:**
```
UI Tests:
TC_184 — Recent Search section visible
TC_185 — Trending Search section visible
TC_186 — UI layout matches design
TC_187 — Responsive ≤768px

Cards Tests:
TC_204 — Cards load without issues
TC_205 — FAILING: Cards UI not matching Figma (implement, assert expected, mark as known fail)
TC_206 — Card details (photo, name, role, location, skills, experience, buttons)
TC_207 — Empty state for no results

API Tests:
TC_931 — Trending search API triggered on input focus
TC_932 — Trending keywords rendered from API response
TC_933 — Keywords clickable
TC_934 — Empty trending state message

TC_936 — Search results API returns and renders talent cards
TC_937 — Empty state: "Can't find what you're looking for?"
TC_938 — Location-based filter search
TC_939 — Location filter change refreshes results

Delete Recent Search:
TC_1159 — Delete recent search API triggered (logged in)
TC_1160 — Clear all recent searches (logged in)
TC_1161 — Delete from localStorage (not logged in)
TC_1162 — Clear all from localStorage (not logged in)
```

**NetworkValidator usage for API tests:**
```java
// Example for API test
NetworkApiRequest request = NetworkValidator.captureApiCall(page,
    "**/talent/search**",
    () -> page.locator(locators.SEARCH_INPUT).click()
);
AssertionHelper.assertEquals(request.getStatus(), 200, "Trending API status");
```

---

### MODULE 4: TALENT PROFILE & DASHBOARD

**Source sheets:** Talent_profile (13), Talentprofile-profileInfoSectio (9), Talent profile footer (8), ProfileDashboardAboutUI4292 (18), ProfileCompleteStatus4463Q (6), User Profile UI 4601 (18), 4411 (10), UpdateorDeleteprofilephoto 4348 (5), Changeserviceablelocationin4347 (5), Task 3496 + ProfileDashboardOpenToConnectUI (merged, 23 unique TCs), ShareProfileShowRecentConne4412 (9), Dashboard Review UI4133 (16), FetchAddEditDeleteGallery4465 (7), FetchAddEditDeleteProjects 4464 (7), FetchAddEditDeleteSkills 4462 (6), ProfileCompleteStatus4463Q (6)

**IMPORTANT — Duplicate:**
`Task 3496` and `ProfileDashboardOpenToConnectUI` cover the same feature.
Implement all unique TCs from both, but as ONE test class: `OpenToConnectTests.java`.

**Files to create:**
- `pages/dashboard/DashboardPage.java`
- `pages/dashboard/GalleryDashboardPage.java`
- `pages/dashboard/ProjectsDashboardPage.java`
- `pages/dashboard/SkillsDashboardPage.java`
- `pages/profile/TalentProfileInfoPage.java`
- `pages/profile/TalentProfileAboutPage.java`
- `tests/profile/TalentProfileTests.java` — EXTEND existing
- `tests/dashboard/DashboardAboutTests.java`
- `tests/dashboard/OpenToConnectTests.java` (merges Task 3496 + ProfileDashboardOpenToConnectUI)
- `tests/dashboard/DashboardCRUDTests.java` (Gallery, Projects, Skills CRUD from dashboard)
- `tests/dashboard/ReviewDashboardTests.java`

**KNOWN FAILING TEST:**
```
TC_TP_PI_169 — Field labels font weight wrong (Figma: 400, Build: 500) → known fail
TC_TP_PI_170 — Profile image size wrong (Figma: 88x88, Build: 83x83) → known fail
TC_178 — Footer icon alignment not matching Figma → known fail
TC_179 — Footer icons inconsistent size/opacity → known fail
```

**Skills CRUD known bug:**
```
TC_4462_001 — Skill name changes to Skill ID after clicking Add New Skills → BUG
```
Mark this test with `@Test(enabled = false)` until bug resolved.

---

### MODULE 5: MESSAGING

**Source sheets:** Message_Request_Drawer_1746 (6), Call_Listings_Ui_1796 (6), Request_Sent_Successfully_1748 (4), Fetch message skills list 1799 (10), MessageRequestSendAPIIntegr2922 (10), SimilarTalentsforMessageAPI2935 (5), Talent_Message_Ui_3107 (12), FetchMessageRequestReceived3421 (13), FetchMessageRequestSentAPI3422 (9), FetchConversationListAPI 3423 (13), RequestAcceptandDeclineAPI 3424 (8), ValidateTalentAPIbeforeMess3576 (10), LikeModalonTalentViewafter3005 (7)

**Files to create:**
- `pages/messaging/MessageRequestDrawer.java`
- `pages/messaging/ConversationListPage.java`
- `pages/messaging/CallListingsPage.java`
- `tests/messaging/MessageRequestTests.java`
- `tests/messaging/ConversationListTests.java`
- `tests/messaging/RequestAcceptDeclineTests.java`
- `tests/messaging/ValidateTalentTests.java`
- `tests/messaging/SimilarTalentsTests.java`

**KNOWN FAILING TEST:**
```
TC_961 — Message drawer: No validation on empty Send → BUG (expected: validation shown, actual: unexpected error)
```

**KNOWN PENDING TEST:**
```
TC_1058 — Display Message Image → "Coming soon" feature — mark @Test(enabled = false)
TC_1069 — Display Message Image → "Coming soon" feature — mark @Test(enabled = false)
TC_1078 — Display Conversation Image → Pending
```

**Like Modal timing logic (LikeModalonTalentViewafter3005):**
```
TC_999 — Modal must NOT appear after 20s without tab interaction
TC_1000 — Modal must NOT appear without tab interaction
TC_1001 — Modal must NOT appear before 15s even with tab click
TC_1002 — Modal MUST appear after 15s + tab interaction
// Use Playwright waitForTimeout() and explicit tab click simulation
```

---

### MODULE 6: WEBSOCKET TESTS

**Source sheets:** WebSocketAPIconnectionatApp3578 (7), Websocketformessagesent,del3579 (8), FetchSingleUserMessageConve3580 (7), BulkMessageReadWebSocket4132 (4), TimezoneadjustmentWebSocket4134 (5, NOT EXECUTED status)

**Files to create/extend:**
- `api/WebSocketApiClient.java`
- `tests/websocket/WebSocketConnectionTests.java`
- `tests/websocket/WebSocketMessageTests.java`
- `tests/websocket/BulkMessageReadTests.java`
- `tests/websocket/TimezoneWebSocketTests.java`

**WS connection test pattern:**
```java
// Use existing WebSocketValidator
WebSocketValidator wsValidator = new WebSocketValidator(page);
wsValidator.connectAndValidate("wss://...");
AssertionHelper.assertTrue(wsValidator.isConnected(), "WebSocket should connect after login");
```

**TIMEZONE TESTS (TC_4134_001–004):**
Status in Excel = "Not Executed". Mark these as `@Test(enabled = false)` initially.
Implement skeleton test logic but disable pending environment setup.

**CORS test (TC_1203):**
```java
// Validate no CORS errors in browser console
page.onConsoleMessage(msg -> {
    AssertionHelper.assertFalse(msg.text().contains("CORS"), "No CORS errors in console");
});
```

---

### MODULE 7: ADDRESS MANAGEMENT

**Source sheets:** Talent Address UI 1129 (9), Fetch&Display_Adress_API_1752 (3), Add_New_Address_API_1753 (3), Edit_Address_Integration_1754 (3), Delete_Address_API_1755 (3), Mark_Default_Address_API_1756 (4)

**Files to create:**
- `pages/address/AddressManagementPage.java`
- `locators/AddressLocators.java`
- `tests/address/AddressManagementTests.java`

**Key scenarios:**
```
UI:
TC_949 — Location UI loads with expected elements
TC_950 — Address drawer opens on click
TC_951 — Current location sets city and closes drawer
TC_952 — Searched location pinned on map

API:
TC_1009 — Address list renders correctly (empty/data/loader states)
TC_1010 — Address data correctness (label, full address, default)
TC_1011 — API failure shows error message
TC_1012 — Add address with validation (valid/invalid)
TC_1015 — Edit address with validation and prefill
TC_1016 — Location update via map pin change
TC_1018 — Delete with confirm and cancel
TC_1019 — Delete default address behavior
TC_1021 — Set default and switch (only one default)
TC_1022 — Default persists after reload
TC_1023 — API failure does not change default
TC_1024 — Delete default behavior
```

---

### MODULE 8: POSTS & SOCIAL FEED

**Source sheets:** Add Post API4548 (12), Explore Feed API 4572 (6), Explore Feed NotInterested4573 (11), Feed Report API 4574 (9), Add Text Post Ui 4819 (19), AI Post Model UI 4815 (8), Post Background Color 5194 (14), Dashboard Post Detail Page 4461 (19), Dashboard Share Button4138 (9), Explore Page UI 4581 (13), Explore Detail Page UI 4584 (13), Explore Detail Page Reactio4585 (16), TextPostBug 4936 (3), SwitchProfileBug 4937 (2), SavedPostAndUi5192 (2), Toggle_LikeandBookmark 1078 (10), View Post API 4556/4557 (2)

**Files to create:**
- `pages/posts/AddPostModal.java`
- `pages/posts/AddTextPostPage.java`
- `pages/posts/PostDetailPage.java`
- `pages/posts/ExplorePage.java`
- `pages/posts/ExploreDetailPage.java`
- `pages/posts/GalleryDetailPage.java`
- `pages/posts/SavedPostsPage.java`
- `tests/posts/AddPostTests.java`
- `tests/posts/TextPostTests.java`
- `tests/posts/ExploreTests.java`
- `tests/posts/FeedTests.java`
- `tests/posts/LikeBookmarkTests.java`

**Add Post key scenarios (TC_4548_API_01–04):**
```
TC_4548_API_01 — Successful post creation (valid image + caption → 200 response)
TC_4548_API_02 — API failure handling (error displayed, post NOT created)
TC_4548_API_03 — API timeout handling (timeout message displayed)
TC_4548_API_04 — Loader visible during API request
```

**Like/Bookmark (TC_921–924):**
```
TC_921 — Toggle Like API triggered on like action (assert HTTP 200 + icon active state)
TC_922 — Toggle Like API on unlike (assert icon reverts)
TC_923 — Unauthenticated user: redirected to login, NO API triggered
TC_924 — API failure (500): error toast shown, like state unchanged
```

**Explore Feed Not Interested (TC_4573_01–04):**
```
TC_4573_01 — Post removed immediately after Not Interested
TC_4573_02 — Post disappears from list
TC_4573_03 — Post does not reappear on refresh
TC_4573_04 — Multiple posts marked as Not Interested
```

**Text Post known scenarios (TC_4936_001–003):**
```
TC_4936_001 — Cursor movement, placeholder, text input
TC_4936_002 — Empty post prevented (button hidden/disabled)
TC_4936_003 — Text limit 250 enforced
```

**SwitchProfile Bug (TC_4937_001–002):**
```
TC_4937_001 — Switch Profile panel opens and closes
TC_4937_002 — Outside click closes panel WITHOUT triggering underlying action
```

---

### MODULE 9: BLOG TESTS

**Source sheets:** ToskieBlog|Setupforapiinteg1807 (8), ToskieBlog|LandingPageApii1808 (5), ToskieBlog|Aboutpageapiinte3007 (5)

**Files to create:**
- `pages/blog/BlogPage.java`
- `tests/blog/BlogApiTests.java`

**Note:** Blog is a separate sub-project (Strapi-based). Tests verify API integration from the client.
```
TC_1025 — Blog project setup (build and run without errors)
TC_1026 — Strapi API base URL configured
TC_1027 — Client successfully connects to Strapi
TC_1028 — Blog list data fetched and displayed
TC_1033 — Landing page data fetch success
TC_1036 — FAILING: Data refresh on reload fails after multiple reloads (known bug)
```

---

### MODULE 10: REVIEWS

**Source sheets:** Dashboard Review UI4133 (16), Fetch Contacts For Reviews 4583 (3), Submit Review API 4588 (2), Fetch All Reviews API4589 (1)

**Files to create:**
- `pages/reviews/ReviewPage.java`
- `tests/reviews/ReviewTests.java`

**Key scenarios:**
```
TC_4133_001 — Star ratings visible in Reviews UI
TC_4133_002 — Review Request button visible in Profile Dashboard
TC_4133_003 — Modal opens on desktop click
TC_4133_004 — Drawer opens on mobile click
TC_REV_001 — Reviews List API + status display
TC_REV_002 — "Request Sent" status shown correctly
TC_REV_006 — Review submission from notification
TC_REV_007 — Duplicate review submission PREVENTED
TC_REV_008 — Reviews list displays correctly; empty state when none
```

---

### MODULE 11: AI FEATURES

**Source sheets:** AI Post Model UI 4815 (8), Bio_Step_AI_W (4), AI_Bio_Dashboard_W (2), AI Message Request_W (3), Post Ideas UI_W (3), Post Generate APIs_W (3), Enhance Post_W (3), Enhance Caption AI_W (2)

**Note:** Bio_Step_AI_W, AI_Bio_Dashboard_W, AI Message Request_W, Post Ideas UI_W, Post Generate APIs_W, Enhance Post_W, Enhance Caption AI_W are **mobile Flutter sheets**.
For web, implement only: AI Post Model UI 4815 + any web equivalents in the AI bio/dashboard.

**Files to create:**
- `tests/ai/AIPostTests.java`
- `tests/ai/AIBioTests.java`

**Web AI scenarios:**
```
TC_4815_001 — Add Post modal opens with Upload + AI options
TC_4815_002 — AI modal opens with all UI elements
TC_4815_003 — Textarea, Enhance button, Generate Post Idea, Generate Image, Post as Text visible
TC_4815_004 — Text input accepted in textarea
```

---

### MODULE 12: ACTIVITY, PRIVACY & LANDING

**Source sheets:** Activity History UI 5778 (5), Task 5778 Urgent (12 — DUPLICATE), Privacy Policy 5013 (17), Landing Page API4668 (4)

**Note:** `Task 5778 Urgent` = exact duplicate of `Activity History UI 5778`. Use Activity History sheet only.

**Files to create:**
- `pages/activity/ActivityHistoryPage.java`
- `tests/activity/ActivityHistoryTests.java`
- `tests/activity/PrivacyPolicyTests.java`
- `tests/landing/LandingPageApiTests.java`

**Key scenarios:**
```
Activity History:
TC_5778_001 — Opens from Profile Menu
TC_5778_002 — Each record: profile picture, owner name, activity type
TC_5778_003 — Click activity → redirects to post
TC_5778_004 — Click profile picture/name → owner profile

Privacy Policy:
TC_5013_001 — Privacy Policy visible in footer/legal section
TC_5013_002 — Label text "Privacy Policy" displayed correctly
TC_5013_003 — Click opens Privacy page UI
TC_5013_004 — Correct URL loaded

Landing Page API:
TC_LP_001 — API loads, map markers displayed
TC_LP_002 — Logged-in user logo marker shown
TC_LP_003 — Guest: default marker shown
TC_LP_004 — Marker info displayed on click
```

---

## TESTNIG SUITE CONFIGURATION

When adding new test classes, register them in the appropriate suite XML.

### testng-smoke.xml additions:
```xml
<test name="Auth Smoke">
    <classes>
        <class name="com.toskie.tests.auth.LoginTests">
            <methods><include name="testValidOTPLogin"/></methods>
        </class>
    </classes>
</test>
```

### testng-regression.xml additions:
```xml
<groups>
    <run>
        <include name="auth"/>
        <include name="profile"/>
        <include name="search"/>
        <include name="messaging"/>
        <include name="address"/>
        <include name="posts"/>
    </run>
</groups>
```

---

## TEST DATA TEMPLATES

Add these test data files in `src/test/resources/testdata/`:

### auth-data.json
```json
{
  "validUser": {
    "phoneNumber": "9876543210",
    "countryCode": "+91",
    "email": "test@toskie.com"
  },
  "invalidUser": {
    "phoneNumber": "1234",
    "email": "invalid-email"
  }
}
```

### search-data.json
```json
{
  "validKeyword": "developer",
  "invalidKeyword": "xyzabcnotfound12345",
  "location": "Mumbai"
}
```

### messaging-data.json
```json
{
  "messageText": "Hello, I would like to connect with you.",
  "skillName": "Web Development",
  "preference": "Work together"
}
```

### address-data.json
```json
{
  "validAddress": {
    "label": "Home",
    "city": "Mumbai",
    "pincode": "400001",
    "fullAddress": "123 Test Street, Bandra, Mumbai"
  },
  "invalidPincode": "999",
  "missingLabel": ""
}
```

### post-data.json
```json
{
  "textPost": {
    "content": "This is a test post content.",
    "maxLength": 250
  },
  "imagePost": {
    "imagePath": "src/test/resources/testdata/sample.jpg",
    "caption": "Test image caption"
  }
}
```

---

## KNOWN BUGS — AUTOMATION HANDLING

For these failing TCs, write the test asserting the EXPECTED behavior, then mark as known-fail:

```java
@Test(groups = {"regression"}, description = "TC_US_70: KNOWN BUG - Duplicate skill not blocked",
      enabled = false)
public void testDuplicateSkillNotAllowed() {
    // Test logic here — re-enable when bug is fixed
}
```

| TC | Module | Bug | Action |
|----|--------|-----|--------|
| TC_US_70 | Skills | Duplicate skills allowed | enabled=false |
| TC_205 | Talent Search Cards | UI not matching Figma | implement + soft assert |
| TC_195, TC_196 | Skill Request UI | Layout breaks | implement + soft assert |
| TC_887, TC_888 | Search Header | UI not Figma | soft assert |
| TC_TP_PI_169 | Profile Info | Font weight wrong | soft assert |
| TC_TP_PI_170 | Profile Info | Image size wrong | soft assert |
| TC_178, TC_179 | Profile Footer | Icons wrong | soft assert |
| TC_4462_001 | Skills CRUD | Skill→ID bug | enabled=false |
| TC_961 | Message Drawer | No validation | enabled=false |
| TC_903 | Gallery | Performance issue | add explicit waits |
| TC_1036 | Blog | Reload failure | retry=3 |
| TC_1077 | Conversation | Intermittent | retry=3 |

---

## SPRINT EXECUTION ORDER

Work through sprints in this exact order:

```
Sprint 1 → Auth (Login, Registration, Account Recovery)     [6 dev days]
Sprint 2 → Profile Creation (all 8 steps)                   [8 dev days]
Sprint 3 → Talent Search                                     [5.5 dev days]
Sprint 4 → Talent Profile + Dashboard                       [8.5 dev days]
Sprint 5 → Messaging                                         [8.5 dev days]
Sprint 6 → Posts + Feed + Address                           [9.5 dev days]
Sprint 7 → Blog + Reviews + AI + Activity                   [7 dev days]
```

**At the end of each sprint:**
1. Run `mvn test -Dsurefire.suiteXmlFiles=testng-regression.xml`
2. Check ExtentReports in `ExecutionReports/`
3. Update test status in planning document
4. Add any new bugs found to the known-bugs table above

---

## CI/CD PIPELINE SETUP

```yaml
# .github/workflows/toskie-automation.yml
name: Toskie Automation Tests
on:
  pull_request:
    branches: [ main, develop ]

jobs:
  smoke-tests:
    steps:
      - run: mvn test -Dsurefire.suiteXmlFiles=testng-smoke.xml

  regression-tests:
    needs: smoke-tests
    if: github.event_name == 'push'
    steps:
      - run: mvn test -Dsurefire.suiteXmlFiles=testng-regression.xml,testng-api.xml
```

---

## QUICK REFERENCE: ALL TC SHEET → TEST CLASS MAPPING

| Excel Sheet | Test Class | Sprint |
|-------------|-----------|--------|
| LoginAndRegistrUserRegistration | LoginTests.java | 1 |
| FE\|Toskie Web\|Login Flow | LoginTests.java | 1 |
| FE\|Toskie Web\|AccountRecovery | AccountRecoveryTests.java | 1 |
| Login_Check_Logic_API_1751 | AuthApiTests.java | 1 |
| BlockUsertoaccessPublicrout3577 | AuthApiTests.java | 1 |
| FE\|Gallery step | GalleryStepTests.java | 2 |
| FE\|Personal info | PersonalInfoStepTests.java | 2 |
| ToskieWeb\|skill step | SkillStepTests.java | 2 |
| ToskieWeb\|ExperiencestepUI | ExperienceStepTests.java | 2 |
| ToskieWeb QualificationsStepUI | QualificationStepTests.java | 2 |
| ToskieWeb ProjectStepUI | ProjectStepTests.java | 2 |
| Bio step 5011 | BioStepTests.java | 2 |
| TalentSearchUI | TalentSearchUITests.java | 3 |
| Talent_search_cards_UI | TalentSearchUITests.java | 3 |
| TalentSearchHeaderandTabs | TalentSearchUITests.java | 3 |
| TalentInputandLocationComponent | MERGED into TalentSearchHeaderandTabs | 3 |
| TalentSearchCardsUIImprovment | TalentSearchUITests.java | 3 |
| trending search api(1077) | TalentSearchApiTests.java | 3 |
| search results api (1076) | TalentSearchApiTests.java | 3 |
| recentsearchapiintegration(1075) | TalentSearchApiTests.java | 3 |
| Delete Recent Search API 3570 | DeleteRecentSearchTests.java | 3 |
| Talent Address UI (1129) | TalentAddressSearchTests.java | 3 |
| Toggle_LikeandBookmark (1078) | LikeBookmarkTests.java | 3 |
| Talent_profile | TalentProfileTests.java | 4 |
| Talentprofile-profileInfoSectio | TalentProfileTests.java | 4 |
| Talent profile footer | TalentProfileTests.java | 4 |
| ProfileDashboardAboutUI4292 | DashboardAboutTests.java | 4 |
| ProfileCompleteStatus4463Q | ProfileCompleteStatusTests.java | 4 |
| User Profile UI 4601 | UserProfileTests.java | 4 |
| 4411 | OpenToConnectTests.java | 4 |
| UpdateorDeleteprofilephoto 4348 | UpdateProfilePhotoTests.java | 4 |
| Changeserviceablelocationin4347 | ChangeLocationTests.java | 4 |
| Task 3496 | OpenToConnectTests.java | 4 |
| ProfileDashboardOpenToConnectUI | OpenToConnectTests.java (MERGED) | 4 |
| ShareProfileShowRecentConne4412 | ShareProfileTests.java | 4 |
| Dashboard Review UI4133 | ReviewDashboardTests.java | 4 |
| FetchAddEditDeleteGallery4465 | DashboardCRUDTests.java | 4 |
| FetchAddEditDeleteProjects 4464 | DashboardCRUDTests.java | 4 |
| FetchAddEditDeleteSkills 4462 | DashboardCRUDTests.java | 4 |
| ProfileGalleryAPI_Integration | GalleryApiTests.java | 4 |
| get skills api (1080) | SkillsApiTests.java | 4 |
| Get_profile_project_API(1081) | ProjectsApiTests.java | 4 |
| talent profile about api (1079) | TalentProfileApiTests.java | 4 |
| Message_Request_Drawer_1746 | MessageRequestTests.java | 5 |
| Call_Listings_Ui_1796 | CallListingsTests.java | 5 |
| Request_Sent_Successfully_1748 | MessageRequestTests.java | 5 |
| Fetch message skills list(1799) | MessageRequestTests.java | 5 |
| MessageRequestSendAPIIntegr2922 | MessageRequestTests.java | 5 |
| SimilarTalentsforMessageAPI2935 | SimilarTalentsTests.java | 5 |
| Talent_Message_Ui_3107 | ChatPageTests.java | 5 |
| FetchMessageRequestReceived3421 | FetchRequestsTests.java | 5 |
| FetchMessageRequestSentAPI3422 | FetchRequestsTests.java | 5 |
| FetchConversationListAPI 3423 | ConversationListTests.java | 5 |
| RequestAcceptandDeclineAPI 3424 | RequestAcceptDeclineTests.java | 5 |
| ValidateTalentAPIbeforeMess3576 | ValidateTalentTests.java | 5 |
| LikeModalonTalentViewafter3005 | LikeModalTests.java | 5 |
| WebSocketAPIconnectionatApp3578 | WebSocketConnectionTests.java | 5 |
| Websocketformessagesent,del3579 | WebSocketMessageTests.java | 5 |
| FetchSingleUserMessageConve3580 | WebSocketMessageTests.java | 5 |
| BulkMessageReadWebSocket4132 | BulkMessageReadTests.java | 5 |
| TimezoneadjustmentWebSocket4134 | TimezoneWebSocketTests.java | 5 |
| Add Post API4548 | AddPostTests.java | 6 |
| Explore Feed API 4572 | ExploreTests.java | 6 |
| Explore Feed NotInterested4573 | FeedTests.java | 6 |
| Feed Report API 4574 | FeedTests.java | 6 |
| Add Text Post Ui 4819 | TextPostTests.java | 6 |
| AI Post Model UI 4815 | AIPostTests.java | 6 |
| Post Background Color & Te 5194 | TextPostTests.java | 6 |
| Dashboard Post Detail Page 4461 | PostDetailTests.java | 6 |
| Dashboard Share Button4138 | ShareButtonTests.java | 6 |
| Explore Page UI 4581 | ExploreTests.java | 6 |
| Explore Detail Page UI 4584 | ExploreDetailTests.java | 6 |
| Explore Detail Page Reactio4585 | ExploreDetailTests.java | 6 |
| TextPostBug 4936 | TextPostTests.java | 6 |
| SwitchProfileBug 4937 | SwitchProfileTests.java | 6 |
| SavedPostAndUi5192 | SavedPostTests.java | 6 |
| Fetch&Display_Adress_API_1752 | AddressManagementTests.java | 6 |
| Add_New_Address_API_1753 | AddressManagementTests.java | 6 |
| Edit_Address_Integration_1754 | AddressManagementTests.java | 6 |
| Delete_Address_API_1755 | AddressManagementTests.java | 6 |
| Mark_Default_Address_API_1756 | AddressManagementTests.java | 6 |
| ToskieBlog\|Setupforapiinteg1807 | BlogApiTests.java | 7 |
| ToskieBlog\|LandingPageApii1808 | BlogApiTests.java | 7 |
| ToskieBlog\|Aboutpageapiinte3007 | BlogApiTests.java | 7 |
| Dashboard Review UI4133 | ReviewTests.java | 7 |
| Fetch Contacts For Reviews 4583 | ReviewTests.java | 7 |
| Submit Review API 4588 | ReviewTests.java | 7 |
| Fetch All Reviews API4589 | ReviewTests.java | 7 |
| Activity History UI 5778 | ActivityHistoryTests.java | 7 |
| Task 5778 Urgent | MERGED into ActivityHistoryTests.java | 7 |
| Privacy Policy 5013 | PrivacyPolicyTests.java | 7 |
| Landing Page API4668 | LandingPageApiTests.java | 7 |
| View Post API 4556/4557 | ViewPostApiTests.java | 7 |
| Gallery View API 4575/4577 | GalleryViewApiTests.java | 7 |
| Projects View API 4576/4578 | ProjectViewApiTests.java | 7 |
| Gallery Viewers List4579 | ViewerListTests.java | 7 |
| Project Viewers List 4580 | ViewerListTests.java | 7 |
| SurveyPageUi4890 | SurveyPageTests.java | 7 |

---

*End of Claude Automation Master Prompt — Toskie Web Automation Framework*
*Use this file at the start of every implementation session.*
