# Toskie Web Automation Framework — Complete Planning Document
**Prepared by:** Principal QA Automation Architect  
**Date:** 2026-06-11  
**Source:** 136-sheet Excel file — `Toskie_Web_Desktop_Frontend_TestCases (1).xlsx`  
**Existing Framework:** `C:\Users\Sontosh\eclipse-workspace\Toskie_Web_Automation_Framework`  
**Stack:** Java 21 · Maven · Playwright 1.53 · TestNG 7.9 · ExtentReports 5.0.9

---

## EXECUTIVE SUMMARY

| Metric | Count |
|--------|-------|
| Total Sheets in Excel | 136 |
| Empty / Irrelevant Sheets | 2 (Done on Azure, TArun) |
| **Active Test Sheets** | **134** |
| **Total Test Cases (all)** | **~672** |
| Web Desktop TCs | ~398 |
| Mobile Flutter TCs (_w/_W suffix) | ~152 |
| API/Integration TCs | ~122 |
| Duplicate TCs identified | ~42 |
| **Net unique Web + API TCs** | **~478** |
| Currently automated in framework | ~175 |
| **Automation gap** | **~303 TCs** |

---

## PHASE 1 — TEST CASE DISCOVERY

### 1.1 Complete Sheet Inventory

#### MODULE: Authentication & Registration
| Sheet Name | TCs | Platform | Type | Positive | Negative | Edge |
|-----------|-----|----------|------|----------|----------|------|
| LoginAndRegistrUserRegistration | 26 | Web | UI | 18 | 5 | 3 |
| FE \| Toskie Web \| Login Flow | 18 | Web | UI | 14 | 2 | 2 |
| FE \| Toskie Web\|AccountRecovery | 31 | Web | UI | 22 | 6 | 3 |
| Login_Check_Logic_API_1751 | 3 | Web | API | 2 | 1 | 0 |
| BlockUsertoaccessPublicrout3577 | 5 | Web | API | 3 | 2 | 0 |
| **MODULE TOTAL** | **83** | | | | | |

#### MODULE: Talent Profile Creation (Registration Flow)
| Sheet Name | TCs | Platform | Type | Positive | Negative | Edge |
|-----------|-----|----------|------|----------|----------|------|
| FE \| Toskie Web \| Gallery step | 7 | Web | UI | 5 | 1 | 1 |
| FE \| Toskie Web \| Personal info | 10 | Web | UI | 7 | 2 | 1 |
| ToskieWeb\|skill step | 39 | Web | UI | 26 | 8 | 5 |
| ToskieWeb\|ExperiencestepUI | 15 | Web | UI | 10 | 3 | 2 |
| ToskieWeb QualificationsStepUI | 17 | Web | UI | 12 | 3 | 2 |
| ToskieWeb ProjectStepUI | 21 | Web | UI | 15 | 3 | 3 |
| Bio step 5011 | 10 | Web | UI | 7 | 2 | 1 |
| SurveyPageUi4890 | 2 | Web | UI | 2 | 0 | 0 |
| Bio_Step_AI_W | 4 | Mobile | UI | 3 | 0 | 1 |
| new_bio_step_W | 6 | Mobile | UI | 4 | 1 | 1 |
| **MODULE TOTAL** | **131** | | | | | |

#### MODULE: Talent Profile View & Dashboard
| Sheet Name | TCs | Platform | Type | Positive | Negative | Edge |
|-----------|-----|----------|------|----------|----------|------|
| Talent_profile | 13 | Web | UI | 9 | 0 | 4 |
| Talentprofile-profileInfoSectio | 9 | Web | UI | 5 | 2 | 2 |
| Talent profile footer | 8 | Web | UI | 4 | 0 | 4 |
| ProfileDashboardAboutUI4292 | 18 | Web | UI | 14 | 2 | 2 |
| ProfileCompleteStatus4463Q | 6 | Web | UI | 5 | 0 | 1 |
| User Profile UI 4601 | 18 | Web | UI | 12 | 2 | 4 |
| Guest_User_Profile_W | 8 | Mobile | UI | 6 | 1 | 1 |
| ProfileDashboardOpenToConnectUI | 8 | Web | UI | 6 | 1 | 1 |
| Task 3496 | 15 | Web | UI | 10 | 3 | 2 |
| 4411 (Open To Connect / Unavail) | 10 | Web | Integration | 6 | 2 | 2 |
| UpdateorDeleteprofilephoto 4348 | 5 | Web | UI | 3 | 1 | 1 |
| Changeserviceablelocationin4347 | 5 | Web | UI | 3 | 1 | 1 |
| Edit Profile Button w | 4 | Mobile | UI | 3 | 0 | 1 |
| Profile switch hide-w | 2 | Mobile | UI | 2 | 0 | 0 |
| User_Profile_UI_ | 12 | Mobile | UI | 9 | 1 | 2 |
| Profile Complete Status_w | 3 | Mobile | UI | 3 | 0 | 0 |
| **MODULE TOTAL** | **144** | | | | | |

#### MODULE: Gallery / Media
| Sheet Name | TCs | Platform | Type | Positive | Negative | Edge |
|-----------|-----|----------|------|----------|----------|------|
| ProfileGalleryAPI_Integration | 9 | Web | API | 6 | 1 | 2 |
| FetchAddEditDeleteGallery4465 | 7 | Web | API | 6 | 0 | 1 |
| Profile Dashboard GalleryUi4300 | 5 | Web | UI | 4 | 0 | 1 |
| Gallery Detail Page UI4591 | 18 | Web | UI | 13 | 1 | 4 |
| Gallery View API Non-Logged4575 | 1 | Web | API | 1 | 0 | 0 |
| Gallery View API Logged-in4577 | 1 | Web | API | 1 | 0 | 0 |
| Gallery Viewers List4579 | 1 | Web | API | 1 | 0 | 0 |
| **MODULE TOTAL** | **42** | | | | | |

#### MODULE: Projects
| Sheet Name | TCs | Platform | Type | Positive | Negative | Edge |
|-----------|-----|----------|------|----------|----------|------|
| ToskieWeb ProjectStepUI | 21 | Web | UI | 15 | 3 | 3 |
| Get_profile_project_API(1081) | 5 | Web | API | 4 | 1 | 0 |
| FetchAddEditDeleteProjects 4464 | 7 | Web | API | 6 | 0 | 1 |
| Projects View API NonLogged4576 | 1 | Web | API | 1 | 0 | 0 |
| Projects View API Logged-in4578 | 1 | Web | API | 1 | 0 | 0 |
| Project Viewers List 4580 | 1 | Web | API | 1 | 0 | 0 |
| Project UI_w | 4 | Mobile | UI | 3 | 0 | 1 |
| **MODULE TOTAL** | **40** | | | | | |

#### MODULE: Skills
| Sheet Name | TCs | Platform | Type | Positive | Negative | Edge |
|-----------|-----|----------|------|----------|----------|------|
| ToskieWeb\|skill step | 39 | Web | UI | 26 | 8 | 5 |
| Skill_Request_UI | 9 | Web | UI | 5 | 2 | 2 |
| Skill Edit 3495 | 13 | Web | UI | 9 | 2 | 2 |
| get skills api (1080) | 7 | Web | API | 5 | 1 | 1 |
| FetchAddEditDeleteSkills 4462 | 6 | Web | API | 4 | 1 | 1 |
| Display Skills-w | 2 | Mobile | UI | 2 | 0 | 0 |
| Similar Skills Cards_w | 5 | Mobile | UI | 3 | 1 | 1 |
| **MODULE TOTAL** | **81** | (skill step counted in Profile Creation too) | | | | |

#### MODULE: Talent Search
| Sheet Name | TCs | Platform | Type | Positive | Negative | Edge |
|-----------|-----|----------|------|----------|----------|------|
| TalentSearchUI | 11 | Web | UI | 7 | 0 | 4 |
| Talent_search_cards_UI | 11 | Web | UI | 7 | 1 | 3 |
| TalentInputandLocationComponent | 7 | Web | UI | 4 | 2 | 1 |
| TalentSearchHeaderandTabs | 7 | Web | UI | 4 | 2 | 1 |
| TalentSearchCardsUIImprovment | 6 | Web | UI | 5 | 0 | 1 |
| Talent Address UI (1129) | 9 | Web | UI | 6 | 1 | 2 |
| trending search api(1077) | 5 | Web | API | 3 | 1 | 1 |
| search results api (1076) | 5 | Web | API | 3 | 1 | 1 |
| recentsearchapiintegration(1075) | 2 | Web | API | 1 | 1 | 0 |
| Delete Recent Search API 3570 | 8 | Web | API | 4 | 2 | 2 |
| Similar Skills Cards_w | 5 | Mobile | UI | 3 | 1 | 1 |
| **MODULE TOTAL** | **76** | | | | | |

#### MODULE: Messaging
| Sheet Name | TCs | Platform | Type | Positive | Negative | Edge |
|-----------|-----|----------|------|----------|----------|------|
| Message_Request_Drawer_1746 | 6 | Web | UI | 4 | 1 | 1 |
| Call_Listings_Ui_1796 | 6 | Web | UI | 4 | 1 | 1 |
| Request_Sent_Successfully_1748 | 4 | Web | UI | 3 | 1 | 0 |
| Fetch message skills list(1799) | 10 | Web | API | 7 | 2 | 1 |
| MessageRequestSendAPIIntegr2922 | 10 | Web | API | 7 | 2 | 1 |
| SimilarTalentsforMessageAPI2935 | 5 | Web | API | 4 | 1 | 0 |
| Talent_Message_Ui_3107 | 12 | Web | UI | 8 | 1 | 3 |
| FetchMessageRequestReceived3421 | 13 | Web | API | 9 | 2 | 2 |
| FetchMessageRequestSentAPI3422 | 9 | Web | API | 6 | 2 | 1 |
| FetchConversationListAPI 3423 | 13 | Web | API | 9 | 2 | 2 |
| RequestAcceptandDeclineAPI 3424 | 8 | Web | API | 5 | 2 | 1 |
| ValidateTalentAPIbeforeMess3576 | 10 | Web | API | 6 | 3 | 1 |
| LikeModalonTalentViewafter3005 | 7 | Web | UI | 4 | 0 | 3 |
| AI Message Request_W | 3 | Mobile | UI | 2 | 0 | 1 |
| Message Request Tabs UI_w | 18 | Mobile | UI | 12 | 2 | 4 |
| Links in messages_w | 8 | Mobile | UI | 6 | 1 | 1 |
| Infinite Scroll for Message-w | 2 | Mobile | UI | 1 | 0 | 1 |
| Last seen and active w | 2 | Mobile | UI | 1 | 0 | 1 |
| Display Skills-w | 2 | Mobile | UI | 2 | 0 | 0 |
| **MODULE TOTAL** | **148** | | | | | |

#### MODULE: WebSocket
| Sheet Name | TCs | Platform | Type | Positive | Negative | Edge |
|-----------|-----|----------|------|----------|----------|------|
| WebSocketAPIconnectionatApp3578 | 7 | Web | WebSocket | 4 | 1 | 2 |
| Websocketformessagesent,del3579 | 8 | Web | WebSocket | 5 | 1 | 2 |
| FetchSingleUserMessageConve3580 | 7 | Web | WebSocket | 4 | 1 | 2 |
| BulkMessageReadWebSocket4132 | 4 | Web | WebSocket | 2 | 0 | 2 |
| TimezoneadjustmentWebSocket4134 | 5 | Web | WebSocket | 2 | 1 | 2 |
| **MODULE TOTAL** | **31** | | | | | |

#### MODULE: Address Management
| Sheet Name | TCs | Platform | Type | Positive | Negative | Edge |
|-----------|-----|----------|------|----------|----------|------|
| Talent Address UI (1129) | 9 | Web | UI | 6 | 1 | 2 |
| Fetch&Display_Adress_API_1752 | 3 | Web | API | 2 | 1 | 0 |
| Add_New_Address_API_1753 | 3 | Web | API | 2 | 1 | 0 |
| Edit_Address_Integration_1754 | 3 | Web | API | 2 | 1 | 0 |
| Delete_Address_API_1755 | 3 | Web | API | 2 | 1 | 0 |
| Mark_Default_Address_API_1756 | 4 | Web | API | 2 | 1 | 1 |
| **MODULE TOTAL** | **25** | | | | | |

#### MODULE: Posts / Social Feed
| Sheet Name | TCs | Platform | Type | Positive | Negative | Edge |
|-----------|-----|----------|------|----------|----------|------|
| Add Post API4548 | 12 | Web | API | 7 | 3 | 2 |
| Explore Feed API 4572 | 6 | Web | API | 5 | 0 | 1 |
| Explore Feed NotInterested4573 | 11 | Web | UI | 7 | 1 | 3 |
| Feed Report API 4574 | 9 | Web | API | 6 | 1 | 2 |
| Add Text Post Ui 4819 | 19 | Web | UI | 12 | 3 | 4 |
| AI Post Model UI 4815 | 8 | Web | UI | 6 | 1 | 1 |
| Post Background Color & Te 5194 | 14 | Web | UI | 9 | 2 | 3 |
| Dashboard Post Detail Page 4461 | 19 | Web | UI | 13 | 2 | 4 |
| Dashboard Share Button4138 | 9 | Web | UI | 6 | 1 | 2 |
| Explore Page UI 4581 | 13 | Web | UI | 9 | 1 | 3 |
| Explore Detail Page UI 4584 | 13 | Web | UI | 9 | 2 | 2 |
| Explore Detail Page Reactio4585 | 16 | Web | UI | 10 | 2 | 4 |
| TextPostBug 4936 | 3 | Web | UI | 2 | 1 | 0 |
| SwitchProfileBug 4937 | 2 | Web | UI | 1 | 1 | 0 |
| SavedPostAndUi5192 | 2 | Web | UI | 2 | 0 | 0 |
| View Post API Logged out 4556 | 1 | Web | API | 1 | 0 | 0 |
| View Post API Logged in 4557 | 1 | Web | API | 1 | 0 | 0 |
| Add Post_w | 9 | Mobile | UI | 6 | 1 | 2 |
| ProfilePostDetailPage_W | 10 | Mobile | UI | 7 | 1 | 2 |
| Post Ideas UI_W | 3 | Mobile | UI | 2 | 0 | 1 |
| Post Generate APIs_W | 3 | Mobile | API | 2 | 1 | 0 |
| Enhance Post_W | 3 | Mobile | UI | 2 | 0 | 1 |
| Enhance Caption AI_W | 2 | Mobile | UI | 1 | 0 | 1 |
| Add Background Blur w | 2 | Mobile | UI | 2 | 0 | 0 |
| **MODULE TOTAL** | **189** | | | | | |

#### MODULE: Like / Bookmark / Toggle
| Sheet Name | TCs | Platform | Type | Positive | Negative | Edge |
|-----------|-----|----------|------|----------|----------|------|
| Toggle_LikeandBookmark (1078) | 10 | Web | API | 6 | 3 | 1 |
| **MODULE TOTAL** | **10** | | | | | |

#### MODULE: Blog (Toskie Blog)
| Sheet Name | TCs | Platform | Type | Positive | Negative | Edge |
|-----------|-----|----------|------|----------|----------|------|
| ToskieBlog\|Setupforapiinteg1807 | 8 | Web | API | 5 | 1 | 2 |
| ToskieBlog\|LandingPageApii1808 | 5 | Web | API | 3 | 1 | 1 |
| ToskieBlog\|Aboutpageapiinte3007 | 5 | Web | API | 4 | 0 | 1 |
| **MODULE TOTAL** | **18** | | | | | |

#### MODULE: Reviews
| Sheet Name | TCs | Platform | Type | Positive | Negative | Edge |
|-----------|-----|----------|------|----------|----------|------|
| Dashboard Review UI4133 | 16 | Web | UI | 10 | 2 | 4 |
| Fetch Contacts For Reviews 4583 | 3 | Web | API | 2 | 1 | 0 |
| Submit Review API 4588 | 2 | Web | API | 1 | 1 | 0 |
| Fetch All Reviews API4589 | 1 | Web | API | 1 | 0 | 0 |
| **MODULE TOTAL** | **22** | | | | | |

#### MODULE: Notifications
| Sheet Name | TCs | Platform | Type | Positive | Negative | Edge |
|-----------|-----|----------|------|----------|----------|------|
| Notification Get APi-w | 2 | Mobile | API | 2 | 0 | 0 |
| Infinite Scroll in notifica-w | 2 | Mobile | UI | 1 | 0 | 1 |
| **MODULE TOTAL** | **4** | | | | | |

#### MODULE: Landing Page
| Sheet Name | TCs | Platform | Type | Positive | Negative | Edge |
|-----------|-----|----------|------|----------|----------|------|
| Landing Page API4668 | 4 | Web | API | 3 | 0 | 1 |
| notLoginLandingPage_w | 8 | Mobile | UI | 6 | 0 | 2 |
| **MODULE TOTAL** | **12** | | | | | |

#### MODULE: Activity & Privacy
| Sheet Name | TCs | Platform | Type | Positive | Negative | Edge |
|-----------|-----|----------|------|----------|----------|------|
| Activity History UI 5778 | 5 | Web | UI | 4 | 0 | 1 |
| Task 5778 Urgent | 12 | Web | UI | 8 | 1 | 3 |
| Privacy Policy 5013 | 17 | Web | UI | 12 | 1 | 4 |
| **MODULE TOTAL** | **34** | | | | | |

#### MODULE: AI Features (Cross-Platform)
| Sheet Name | TCs | Platform | Type | Positive | Negative | Edge |
|-----------|-----|----------|------|----------|----------|------|
| AI Post Model UI 4815 | 8 | Web | UI | 6 | 1 | 1 |
| Bio_Step_AI_W | 4 | Mobile | UI | 3 | 0 | 1 |
| AI_Bio_Dashboard_W | 2 | Mobile | UI | 2 | 0 | 0 |
| AI Message Request_W | 3 | Mobile | UI | 2 | 0 | 1 |
| Post Ideas UI_W | 3 | Mobile | UI | 2 | 0 | 1 |
| Post Generate APIs_W | 3 | Mobile | API | 2 | 1 | 0 |
| Enhance Post_W | 3 | Mobile | UI | 2 | 0 | 1 |
| Enhance Caption AI_W | 2 | Mobile | UI | 1 | 0 | 1 |
| **MODULE TOTAL** | **28** | | | | | |

#### MODULE: Miscellaneous / Other
| Sheet Name | TCs | Platform | Type | Notes |
|-----------|-----|----------|------|-------|
| User Tips Carousel_W | 8 | Mobile | UI | Onboarding tips |
| ShareProfileShowRecentConne4412 | 9 | Web | UI | Share profile feature |
| Profile switch hide-w | 2 | Mobile | UI | Role-based visibility |
| Native Share Integration_w | 2 | Mobile | UI | Native share sheet |
| user info api w | 1 | Mobile | API | User type API |
| mic functionality w | 2 | Mobile | UI | Address field mic |
| SurveyPageUi4890 | 2 | Web | UI | Post-onboarding survey |
| Done on Azure | 0 | — | — | Empty sheet |
| TArun | 0 | — | — | Empty sheet |
| **MODULE TOTAL** | **28** | | | |

---

## PHASE 2 — DUPLICATE ANALYSIS

### 2.1 Confirmed Duplicates

| Duplicate Sheet | Original Sheet | Overlap | Reuse Strategy |
|----------------|----------------|---------|----------------|
| `Task 5778 Urgent` (12 TCs) | `Activity History UI 5778` (5 TCs) | 100% — same feature, TC IDs match | Delete Task 5778 Urgent; keep Activity History UI 5778 |
| `Task 3496` (15 TCs) | `ProfileDashboardOpenToConnectUI` (8 TCs) | 90% — same Open To Connect feature | Merge into ProfileDashboardOpenToConnectUI; extra TCs from Task 3496 are additional |
| `TalentSearchHeaderandTabs` (7 TCs) | `TalentInputandLocationComponent` (7 TCs) | 100% — TC_887-890 = TC_894-897, exact same scenario titles, same steps | Keep TalentSearchHeaderandTabs; mark TalentInputandLocationComponent as MERGED |
| `ProfileCompleteStatus4463Q` (6 TCs) | `Profile Complete Status_w` (3 TCs) | 60% — same 3 scenarios on both Web and Mobile | Web tests in ProfileCompleteStatus4463Q are Web; _w sheet is Mobile-only. Keep both; share Page Object logic |
| `User_Profile_UI_` (12 TCs, Mobile) | `User Profile UI 4601` (18 TCs, Web) | 50% — profile load, image, details scenarios | Different platforms; reuse assertion patterns via shared BaseTest |
| `Bio step 5011` (10 TCs, Web) | `Bio_Step_AI_W` (4 TCs) + `new_bio_step_W` (6 TCs) | 40% — bio validation, bio submission overlap | Web bio tests in Bio step 5011; mobile bio in _W sheets |

### 2.2 Near-Duplicate Patterns (Reusable Flows)

| Pattern | Found In | Reuse Strategy |
|---------|---------|----------------|
| Fetch + Display + Empty State + Loader | ProfileGallery, Projects, Skills, Reviews, Blog | Create `FetchDisplayValidator` utility |
| Add + Edit + Delete flow | Gallery, Projects, Skills, Address | Create `CRUDTestBase` abstract class |
| API trigger on page load | All API sheets (1074–1082, 3421–3424) | Create `ApiTriggerValidator` utility |
| WebSocket connect/disconnect | WS sheets 3578–3580, 4132, 4134 | Create `WebSocketTestBase` class |
| Modal open on desktop / Drawer on mobile | Review, Edit Profile, Message Request | Create `ResponsiveComponentTest` base |
| Empty state message | Search, Gallery, Messages, Blog | Reuse `EmptyStateAssertions` |

### 2.3 Mobile vs Web Overlapping Scenarios

| Module | Web Sheet | Mobile Sheet | Shared Logic |
|--------|-----------|-------------|--------------|
| Post Detail | Dashboard Post Detail Page 4461 | ProfilePostDetailPage_W | Navigation, display, header |
| User Profile | User Profile UI 4601 | User_Profile_UI_ | Load, image, details |
| Bio Step | Bio step 5011 | Bio_Step_AI_W, new_bio_step_W | Validation, submission |
| Profile Complete | ProfileCompleteStatus4463Q | Profile Complete Status_w | Display, redirect |
| Add Post | Add Post API4548 | Add Post_w | Upload, modal, image crop |
| Links/Messages | FetchConversationListAPI 3423 | Links in messages_w, Infinite Scroll for Message-w | Conversation display |
| Notifications | (API sheets) | Notification Get APi-w, Infinite Scroll in notifica-w | Load, scroll |

---

## PHASE 3 — AUTOMATION PRIORITIZATION

### P0 — SMOKE (Must pass on every deploy)

| Priority | Module | Test Classes | Reason |
|----------|--------|-------------|--------|
| P0 | Login (OTP) | LoginTests | Entry point — all tests depend on auth |
| P0 | User Registration | RegistrationTests | Core onboarding |
| P0 | Account Recovery | AccountRecoveryTests | Revenue risk on broken recovery |
| P0 | Talent Search | TalentSearchTests | Core discovery feature |
| P0 | Message Request Send | MessageRequestTests | Core engagement |
| P0 | WebSocket Connection | WebSocketConnectionTests | Messaging infra |
| P0 | Profile Load | TalentProfileTests | Core display |
| P0 | Add Post (image) | AddPostTests | Core content creation |
| P0 | Gallery Load | GalleryTests | Visual portfolio |

### P1 — REGRESSION (Sprint 1+2, full regression gate)

| Priority | Module | Reason |
|----------|--------|--------|
| P1 | Profile Creation (all 8 steps) | Critical registration funnel |
| P1 | Talent Search — all API + UI | Core product feature |
| P1 | Messaging — Request/Accept/Decline | Core engagement flow |
| P1 | Address Management (CRUD) | Location-based service |
| P1 | Gallery CRUD | Profile completeness |
| P1 | Skills CRUD | Profile completeness |
| P1 | Projects CRUD | Profile completeness |
| P1 | Toggle Like/Bookmark | Social engagement |
| P1 | Feed — Explore, Report, Not Interested | Discovery flow |
| P1 | Profile Complete Status | Onboarding funnel |

### P2 — EXTENDED REGRESSION (Sprint 3-5)

| Priority | Module | Reason |
|----------|--------|--------|
| P2 | WebSocket Messages (send/read/delete) | Complex async flows |
| P2 | Reviews (full cycle) | Trust & reputation feature |
| P2 | Blog API Integration | Content marketing |
| P2 | Dashboard Share | Viral growth |
| P2 | Activity History | User engagement |
| P2 | Open To Connect / Unavailable | Profile status flows |
| P2 | Similar Talents for Message | Discovery after messaging |
| P2 | AI Bio, AI Post, AI Message | AI-assisted features |
| P2 | Post Background Colors | Text post feature |
| P2 | Privacy Policy | Legal compliance |

### P3 — LOW PRIORITY (Sprint 6+)

| Priority | Module | Reason |
|----------|--------|--------|
| P3 | User Tips Carousel | Single one-time flow |
| P3 | Timezone WebSocket | Platform-level edge case |
| P3 | Native Share Integration (mobile) | Mobile-only feature |
| P3 | Mic functionality | Device capability |
| P3 | Add Background Blur | Visual enhancement |
| P3 | Profile switch hide | Role display edge case |
| P3 | Survey Page | Post-onboarding one-time UI |
| P3 | Call Listings | Secondary feature |
| P3 | Profile Viewers List (Gallery/Projects) | Analytics feature |

---

## PHASE 4 — EXISTING FRAMEWORK MAPPING

### 4.1 Current Framework Inventory

```
src/main/java/com/toskie/
├── AuthenticationPages/
│   ├── OrPages/page/
│   │   ├── WelcomeToToskieLandingPageOr.java
│   │   ├── LoginPageOr.java
│   │   └── ToskieCreateProfileOr.java
│   └── Page/
│       ├── ToskieCreateProfile.java
│       └── WelcomeToToskieLandingPage.java
├── locators/
│   ├── WelcomePageLocators.java
│   ├── LoginPageLocators.java
│   ├── ProfileCreationLocators.java
│   ├── HomePageLocators.java
│   ├── SearchPageLocators.java
│   ├── ChatPageLocators.java
│   ├── ProfileViewLocators.java
│   ├── NotificationsLocators.java
│   ├── SettingsPageLocators.java
│   └── BookingLocators.java
├── pages/
│   ├── WelcomePage.java
│   ├── LoginPage.java
│   ├── ProfileCreationPage.java
│   ├── HomePage.java
│   ├── SearchPage.java
│   ├── ChatPage.java
│   └── BookingPage.java
├── utils/
│   ├── AssertionHelper.java
│   ├── NetworkValidator.java
│   ├── WebSocketValidator.java
│   ├── PerformanceUtils.java
│   ├── SecurityUtils.java
│   ├── AccessibilityUtils.java
│   ├── DatabaseValidator.java
│   ├── RedisValidator.java
│   └── TestDataManager.java
└── utils_Layer/
    ├── BrowserManager.java
    ├── ConfigManager.java
    ├── ReportManager.java
    ├── ApiUtils.java
    ├── RetryConfig.java
    ├── UtilLayer.java
    └── WaitManager.java
```

### 4.2 Existing vs Required Coverage Gap

| Manual Module | Existing Coverage | Gap | Priority |
|--------------|------------------|-----|----------|
| Login / OTP | LoginTests (10 TCs) | Missing: OTP edge cases, country selector, mobile validation | P0 |
| Registration | RegistrationTests (partial) | Missing: all 8 profile creation steps | P1 |
| Talent Profile View | ProfileTests (10 TCs) | Missing: about/bio, footer, responsive, info sections | P1 |
| Talent Search | SearchTests (10 TCs) | Missing: API assertions, recent/trending, cards improvement | P1 |
| Gallery | GalleryTests (partial) | Missing: CRUD API, gallery detail page, viewer list | P1 |
| Projects | (not automated) | All 40 TCs missing | P1 |
| Skills | (partial) | Skill CRUD, skill edit, display skills missing | P1 |
| Messaging | (partial via ChatPage) | 148 TCs — most missing | P0 |
| WebSocket | WebSocketTests (10 TCs) | Missing: bulk read, timezone, single conversation | P1 |
| Address | (not automated) | All 25 TCs missing | P1 |
| Posts/Feed | (not automated) | All 189 TCs missing | P1 |
| Blog | (not automated) | All 18 TCs missing | P2 |
| Reviews | (not automated) | All 22 TCs missing | P2 |
| Like/Bookmark | (not automated) | All 10 TCs missing | P1 |
| Activity History | (not automated) | All 17 TCs missing | P2 |
| Privacy Policy | (not automated) | All 17 TCs missing | P3 |
| AI Features | (not automated) | All 28 TCs missing | P2 |
| Landing Page | (not automated) | All 12 TCs missing | P2 |
| Notifications | (not automated) | All 4 TCs missing | P2 |

**Summary:**
- Currently automated: ~175 test methods
- Tests with real backend assertions: ~80
- **Gap: ~303 TCs need automation scripts**

---

## PHASE 5 — SUITE DESIGN

### Smoke Suite (`testng-smoke.xml`)
**Runtime:** ~8 minutes | **Tests:** 25 | **Dependencies:** Live Toskie dev server

| Module | Test Class | TCs |
|--------|-----------|-----|
| Login | LoginTests | 5 |
| Registration | RegistrationSmokeTests | 3 |
| Talent Search | TalentSearchSmokeTests | 4 |
| Profile Load | TalentProfileSmokeTests | 3 |
| Message Request | MessagingSmoke Tests | 3 |
| Add Post | PostSmokeTests | 3 |
| WebSocket | WebSocketSmokeTests | 4 |

### Regression Suite (`testng-regression.xml`)
**Runtime:** ~45 minutes | **Tests:** 200+ | **Threads:** 3

- Login, Registration, Account Recovery
- Profile Creation (all 8 steps)
- Talent Search (all API + UI)
- Gallery, Projects, Skills CRUD
- Messaging Request/Accept/Decline
- Address CRUD
- Feed, Explore, Post detail
- Toggle Like/Bookmark
- Profile Complete Status

### API Suite (`testng-api.xml`)
**Runtime:** ~25 minutes | **Tests:** 100+

- All API integration sheets (1074–1082)
- Messaging API (3421–3424)
- Address API (1752–1756)
- Post/Feed API (4548, 4572–4574)
- Gallery/Project/Skills API
- Blog API
- Review API
- View API (Logged in/out)

### WebSocket Suite (`testng-websocket.xml`)
**Runtime:** ~15 minutes | **Tests:** 31

- WebSocket connection tests
- Message send/delete
- Single conversation fetch
- Bulk message read
- Timezone adjustment

### Profile Suite (`testng-profile.xml`)
**Runtime:** ~20 minutes | **Tests:** 80+

- Profile creation all steps
- Talent profile view
- Dashboard about section
- Profile complete status
- Open To Connect
- Gallery/Projects/Skills on dashboard

### Messaging Suite (`testng-messaging.xml`)
**Runtime:** ~20 minutes | **Tests:** 70+

- Message request drawer
- Message request send API
- Received/Sent API
- Conversation list
- Accept/Decline
- WebSocket messaging
- Similar talents for message

### Feed Suite (`testng-feed.xml`)
**Runtime:** ~15 minutes | **Tests:** 60+

- Add Post (image, text, AI)
- Explore feed
- Feed Not Interested
- Feed Report
- Post detail page
- Like/Bookmark toggle
- Share button

---

## PHASE 6 — PLAYWRIGHT ARCHITECTURE

### 6.1 New Page Objects Required

```
src/main/java/com/toskie/pages/
│
├── auth/
│   ├── LoginPage.java              ← EXTEND existing (add OTP, country selector)
│   ├── RegistrationPage.java       ← EXTEND existing
│   └── AccountRecoveryPage.java    ← NEW
│
├── profile/
│   ├── ProfileCreationPage.java    ← EXTEND existing (all 8 steps)
│   ├── PersonalInfoPage.java       ← NEW
│   ├── GalleryStepPage.java        ← NEW
│   ├── SkillStepPage.java          ← NEW
│   ├── ExperienceStepPage.java     ← NEW
│   ├── QualificationStepPage.java  ← NEW
│   ├── ProjectStepPage.java        ← NEW
│   ├── BioStepPage.java            ← NEW
│   └── SurveyPage.java             ← NEW
│
├── talent/
│   ├── TalentProfilePage.java      ← EXTEND existing
│   ├── TalentProfileInfoPage.java  ← NEW
│   ├── TalentProfileAboutPage.java ← NEW
│   └── TalentProfileFooterPage.java ← NEW
│
├── dashboard/
│   ├── DashboardPage.java          ← NEW
│   ├── GalleryDashboardPage.java   ← NEW
│   ├── ProjectsDashboardPage.java  ← NEW
│   ├── SkillsDashboardPage.java    ← NEW
│   ├── ReviewDashboardPage.java    ← NEW
│   └── AboutDashboardPage.java     ← NEW
│
├── search/
│   ├── TalentSearchPage.java       ← EXTEND existing
│   ├── TalentSearchResultsPage.java ← NEW
│   └── TalentSearchCardsPage.java  ← NEW
│
├── messaging/
│   ├── MessageRequestDrawer.java   ← NEW
│   ├── ConversationListPage.java   ← NEW
│   ├── ChatPage.java               ← EXTEND existing
│   ├── CallListingsPage.java       ← NEW
│   └── MessageRequestTabsPage.java ← NEW
│
├── address/
│   └── AddressManagementPage.java  ← NEW
│
├── posts/
│   ├── AddPostModal.java           ← NEW
│   ├── AddTextPostPage.java        ← NEW
│   ├── PostDetailPage.java         ← NEW
│   ├── ExplorePage.java            ← NEW
│   ├── ExploreDetailPage.java      ← NEW
│   ├── GalleryDetailPage.java      ← NEW
│   └── SavedPostsPage.java         ← NEW
│
├── profile_view/
│   ├── UserProfilePage.java        ← NEW
│   └── GuestUserProfilePage.java   ← NEW
│
├── blog/
│   └── BlogPage.java               ← NEW
│
├── reviews/
│   └── ReviewPage.java             ← NEW
│
├── activity/
│   ├── ActivityHistoryPage.java    ← NEW
│   └── NotificationsPage.java      ← NEW
│
└── landing/
    └── LandingPage.java            ← NEW
```

### 6.2 Components (Reusable)

```
src/main/java/com/toskie/components/
├── HeaderComponent.java       ← Navigation header, profile icon, search
├── FooterComponent.java       ← Mobile footer (visible ≤768px)
├── SearchComponent.java       ← Talent search input + dropdown
├── ToastComponent.java        ← Success/Error/Info toasts
├── ModalComponent.java        ← Generic modal — open/close/assert
├── DrawerComponent.java       ← Right-side drawer — open/close/assert
├── SkillDropdownComponent.java ← Skills selection across multiple flows
├── ImageUploadComponent.java  ← Gallery, Post, Profile image upload
├── MapComponent.java          ← Address map interaction
├── WebSocketComponent.java    ← WS connection state + message helpers
└── OTPInputComponent.java     ← OTP field (Login, Account Recovery)
```

### 6.3 API Client Classes

```
src/main/java/com/toskie/api/
├── AuthApiClient.java         ← Login, OTP, Recovery
├── ProfileApiClient.java      ← Profile CRUD, Gallery, Skills, Projects
├── SearchApiClient.java       ← Talent search, trending, recent search
├── MessagingApiClient.java    ← Message requests, conversations
├── PostApiClient.java         ← Add post, feed, explore
├── AddressApiClient.java      ← CRUD address operations
├── BlogApiClient.java         ← Blog landing, about
├── ReviewApiClient.java       ← Reviews list, submit
├── WebSocketApiClient.java    ← WS connection, message events
└── LandingApiClient.java      ← Landing page markers
```

---

## PHASE 7 — AUTOMATION SPRINT PLAN

### Sprint 1 — Foundation + Auth (Week 1-2)
**Goal:** Framework stabilization, auth flows, smoke suite green

| Task | Files | TCs | Effort |
|------|-------|-----|--------|
| Extend BaseTest with RetryConfig + Reports | BaseTest.java | — | 0.5 day |
| Login + OTP flow | LoginTests.java, LoginPage.java | 18 | 1.5 days |
| Registration flow | RegistrationTests.java | 26 | 1.5 days |
| Account Recovery | AccountRecoveryTests.java, AccountRecoveryPage.java | 31 | 1.5 days |
| Block user public route | AuthSecurityTests.java | 5 | 0.5 day |
| Login Check API | LoginApiTests.java | 3 | 0.5 day |
| **Sprint 1 Total** | | **83** | **6 days** |

### Sprint 2 — Profile Creation (Week 3-4)
**Goal:** All 8 registration steps automated

| Task | Files | TCs | Effort |
|------|-------|-----|--------|
| Personal Info step | PersonalInfoStepTests.java | 10 | 1 day |
| Gallery step | GalleryStepTests.java | 7 | 0.5 day |
| Skills step | SkillStepTests.java | 39 | 2 days |
| Experience step | ExperienceStepTests.java | 15 | 1 day |
| Qualification step | QualificationStepTests.java | 17 | 1 day |
| Projects step | ProjectStepTests.java | 21 | 1 day |
| Bio step + AI | BioStepTests.java | 14 | 1 day |
| Profile Complete Status | ProfileCompleteStatusTests.java | 9 | 0.5 day |
| **Sprint 2 Total** | | **132** | **8 days** |

### Sprint 3 — Talent Search (Week 5-6)
**Goal:** Full talent search coverage

| Task | Files | TCs | Effort |
|------|-------|-----|--------|
| Search UI (headers, tabs, cards) | TalentSearchUITests.java | 38 | 2 days |
| Search API (trending, results, recent) | TalentSearchApiTests.java | 12 | 1 day |
| Delete Recent Search | DeleteRecentSearchTests.java | 8 | 0.5 day |
| Search location / address | TalentAddressSearchTests.java | 9 | 1 day |
| Like/Bookmark toggle | LikeBookmarkTests.java | 10 | 1 day |
| **Sprint 3 Total** | | **77** | **5.5 days** |

### Sprint 4 — Talent Profile + Dashboard (Week 7-8)
**Goal:** Full profile view and dashboard automation

| Task | Files | TCs | Effort |
|------|-------|-----|--------|
| Talent profile sections | TalentProfileTests.java | 30 | 2 days |
| Dashboard about section | DashboardAboutTests.java | 18 | 1 day |
| Open To Connect / Status | OpenToConnectTests.java | 25 | 1.5 days |
| Gallery/Projects/Skills API on Dashboard | DashboardCRUDTests.java | 20 | 1.5 days |
| Update profile photo | UpdateProfilePhotoTests.java | 5 | 0.5 day |
| Profile sharing | ShareProfileTests.java | 9 | 0.5 day |
| User Profile UI | UserProfileTests.java | 30 | 1.5 days |
| **Sprint 4 Total** | | **137** | **8.5 days** |

### Sprint 5 — Messaging (Week 9-10)
**Goal:** Full messaging lifecycle automated

| Task | Files | TCs | Effort |
|------|-------|-----|--------|
| Message Request Drawer + Send API | MessageRequestTests.java | 16 | 1.5 days |
| Fetch Received + Sent Requests | FetchRequestsTests.java | 22 | 1.5 days |
| Conversation List API | ConversationListTests.java | 13 | 1 day |
| Accept/Decline Request API | RequestAcceptDeclineTests.java | 8 | 0.5 day |
| Validate Talent before Message | ValidateTalentTests.java | 10 | 0.5 day |
| Similar Talents for Message | SimilarTalentsTests.java | 5 | 0.5 day |
| Like Modal on Talent View | LikeModalTests.java | 7 | 0.5 day |
| WebSocket Messaging | WebSocketMessageTests.java | 31 | 2.5 days |
| **Sprint 5 Total** | | **112** | **8.5 days** |

### Sprint 6 — Posts, Feed & Address (Week 11-12)
**Goal:** Social feed + address management

| Task | Files | TCs | Effort |
|------|-------|-----|--------|
| Add Post (image + API) | AddPostTests.java | 21 | 1.5 days |
| Add Text Post UI | AddTextPostTests.java | 19 | 1.5 days |
| Explore Feed + Not Interested | ExploreFeedTests.java | 17 | 1.5 days |
| Feed Report | FeedReportTests.java | 9 | 0.5 day |
| Post detail page | PostDetailTests.java | 19 | 1 day |
| Gallery + Explore detail | GalleryExploreDetailTests.java | 31 | 1.5 days |
| Address CRUD | AddressManagementTests.java | 25 | 2 days |
| **Sprint 6 Total** | | **141** | **9.5 days** |

### Sprint 7 — Blog, Reviews, AI & Others (Week 13-14)
**Goal:** Remaining coverage

| Task | Files | TCs | Effort |
|------|-------|-----|--------|
| Blog API integration | BlogTests.java | 18 | 1.5 days |
| Reviews (full cycle) | ReviewTests.java | 22 | 1.5 days |
| AI Post Model + AI Bio | AIFeatureTests.java | 28 | 2 days |
| Activity History + Privacy | ActivityPrivacyTests.java | 22 | 1 day |
| Landing Page API | LandingPageTests.java | 12 | 0.5 day |
| Notifications | NotificationTests.java | 4 | 0.5 day |
| **Sprint 7 Total** | | **106** | **7 days** |

**Total Automation Effort: ~53 developer days**

---

## PHASE 8 — RECOMMENDED FOLDER STRUCTURE

```
Toskie_Web_Automation_Framework/
│
├── src/
│   ├── main/java/com/toskie/
│   │   ├── pages/                    ← Page Objects (all new + existing)
│   │   │   ├── auth/
│   │   │   ├── profile/
│   │   │   ├── dashboard/
│   │   │   ├── search/
│   │   │   ├── messaging/
│   │   │   ├── address/
│   │   │   ├── posts/
│   │   │   ├── blog/
│   │   │   ├── reviews/
│   │   │   ├── activity/
│   │   │   └── landing/
│   │   ├── components/               ← Reusable UI components
│   │   ├── api/                      ← API client classes
│   │   ├── locators/                 ← All CSS/XPath locators
│   │   ├── utils/                    ← Framework utilities
│   │   ├── utils_Layer/              ← BrowserManager, ConfigManager, etc.
│   │   ├── AuthenticationPages/      ← EXISTING — keep as-is
│   │   └── constants/                ← NEW: URLs, Timeouts, Enums
│   │       ├── AppConstants.java
│   │       ├── TestGroups.java
│   │       └── PageUrls.java
│   │
│   └── test/
│       ├── java/com/toskie/
│       │   ├── BaseTest_Layer/
│       │   │   └── BaseTest.java     ← EXISTING — extend with retry + WS
│       │   ├── tests/
│       │   │   ├── smoke/            ← SmokeTests.java (EXISTING + extend)
│       │   │   ├── auth/             ← LoginTests, RegistrationTests, AccountRecoveryTests
│       │   │   ├── profile/          ← All profile step tests
│       │   │   ├── dashboard/        ← Dashboard section tests
│       │   │   ├── search/           ← Talent search tests
│       │   │   ├── messaging/        ← All messaging tests
│       │   │   ├── address/          ← Address CRUD tests
│       │   │   ├── posts/            ← Post, feed, explore tests
│       │   │   ├── websocket/        ← WebSocket tests
│       │   │   ├── blog/             ← Blog tests
│       │   │   ├── reviews/          ← Review tests
│       │   │   ├── ai/               ← AI feature tests
│       │   │   ├── activity/         ← Activity + notifications
│       │   │   ├── landing/          ← Landing page tests
│       │   │   ├── regression/       ← EXISTING (extend)
│       │   │   ├── api/              ← EXISTING API + WebSocket tests
│       │   │   ├── security/         ← EXISTING + auth security
│       │   │   ├── accessibility/    ← EXISTING
│       │   │   ├── performance/      ← EXISTING
│       │   │   ├── edge/             ← EXISTING
│       │   │   ├── negative/         ← EXISTING
│       │   │   └── e2e/              ← EXISTING E2E
│       │   └── authentication/       ← EXISTING UserLoginTestCases
│       │
│       └── resources/
│           ├── config.properties
│           ├── testdata/             ← JSON test data files
│           └── suites/
│               ├── testng.xml        ← EXISTING (master)
│               ├── testng-smoke.xml
│               ├── testng-regression.xml
│               ├── testng-api.xml
│               ├── testng-websocket.xml
│               ├── testng-profile.xml
│               ├── testng-messaging.xml
│               ├── testng-feed.xml
│               ├── testng-e2e.xml
│               └── testng-master.xml
│
└── ExecutionReports/                 ← ExtentReports output
```

---

## PHASE 9 — FINAL DELIVERABLES SUMMARY

### 1. Automation Coverage Matrix

| Module | Total TCs | Web TCs | Mobile TCs | API TCs | Currently Auto | After Sprint 7 |
|--------|-----------|---------|-----------|---------|---------------|----------------|
| Authentication | 83 | 78 | 0 | 8 | 35 | 83 |
| Profile Creation | 131 | 121 | 10 | 0 | 20 | 131 |
| Talent Profile/Dashboard | 144 | 110 | 34 | 0 | 30 | 120 |
| Gallery | 42 | 40 | 0 | 9 | 5 | 42 |
| Projects | 40 | 36 | 4 | 9 | 0 | 40 |
| Skills | 41 | 35 | 7 | 7 | 5 | 41 |
| Talent Search | 76 | 71 | 5 | 12 | 10 | 76 |
| Messaging | 148 | 86 | 62 | 47 | 20 | 148 |
| WebSocket | 31 | 31 | 0 | 31 | 10 | 31 |
| Address | 25 | 25 | 0 | 25 | 0 | 25 |
| Posts/Feed | 189 | 142 | 47 | 20 | 0 | 189 |
| Like/Bookmark | 10 | 10 | 0 | 10 | 0 | 10 |
| Blog | 18 | 18 | 0 | 18 | 0 | 18 |
| Reviews | 22 | 22 | 0 | 6 | 0 | 22 |
| AI Features | 28 | 8 | 20 | 3 | 0 | 28 |
| Activity/Privacy | 34 | 34 | 0 | 0 | 0 | 34 |
| Landing/Notif | 16 | 8 | 8 | 4 | 0 | 16 |
| **TOTAL** | **~1,078** | **~875** | **~197** | **~209** | **~135** | **~1,054** |

> Note: Some TCs appear in multiple modules (skills in both Profile Creation and Skills).

### 2. Duplicate Removal — 42 TCs removed
- Task 5778 Urgent merged → Activity History UI 5778 (saves 8 TCs)
- TalentSearchHeaderandTabs merged with TalentInputandLocationComponent (saves 8 TCs)
- Task 3496 merged into ProfileDashboardOpenToConnectUI (saves 7 TCs)
- Mobile _w sheets kept separate — different platform, not duplicates
- Near-duplicate assertion patterns handled via shared base class

### 3. Framework Gaps
- No `AccountRecoveryPage.java` — **must create**
- No `AddressManagementPage.java` — **must create**
- No `PostPage.java` / `FeedPage.java` — **must create**
- No `DashboardPage.java` with gallery/projects/skills sections — **must create**
- Missing `components/` package — **must create**
- No `api/` client classes — **must create**
- `testng.xml` needs new groups: `auth`, `profile`, `search`, `messaging`, `address`, `posts`, `blog`, `reviews`, `ai`, `activity`

### 4. CI/CD Strategy
```yaml
# On PR:
mvn test -Dsurefire.suiteXmlFiles=testng-smoke.xml

# On merge to main:
mvn test -Dsurefire.suiteXmlFiles=testng-regression.xml,testng-api.xml

# Nightly:
mvn test -Dsurefire.suiteXmlFiles=testng-master.xml
```

### 5. Known Failing Tests (from Excel, Status = Fail)
| TC | Module | Issue |
|----|--------|-------|
| TC_205 | Talent Search Cards | UI not matching Figma |
| TC_195, TC_196 | Skill Request UI | UI layout breaks |
| TC_887, TC_888, TC_894, TC_895 | Search Header & Tabs | UI not matching Figma (height, padding) |
| TC_TP_PI_169, TC_TP_PI_170 | Talent Profile Info | Font weight, image size wrong |
| TC_178, TC_179 | Talent Profile Footer | Icon alignment, consistency wrong |
| TC_4462_001 | Skills Edit | Skill name changes to ID — BUG |
| TC_961 | Message Drawer | No validation on empty send — BUG |
| TC_903 | Gallery Load | Loader not smooth — performance issue |
| TC_1036 | Blog Landing Page | Reload failure |
| TC_1077 | Conversation List | Intermittent display failure |

> These 10 failing test cases represent known bugs — automation should assert the EXPECTED result and mark as blocked/known-fail.

---

## APPENDIX: MOBILE SHEETS EXCLUSION LIST

The following 31 sheets are Flutter Mobile only — **exclude from Web Playwright automation**:

```
Add Post_w, ProfilePostDetailPage_W, Guest_User_Profile_W, Bio_Step_AI_W,
AI_Bio_Dashboard_W, AI Message Request_W, Post Ideas UI_W, Post Generate APIs_W,
Enhance Post_W, User Tips Carousel_W, Enhance Caption AI_W, new_bio_step_W,
User_Profile_UI_, Message Request Tabs UI_w, Similar Skills Cards_w,
notLoginLandingPage_w, Edit Profile Button w, Project UI_w,
ProfileDashboardOpenToConnectUI (partially mobile), Profile Complete Status_w,
Links in messages_w, Native Share Integration_w, Notification Get APi-w,
Infinite Scroll in notifica-w, Infinite Scroll for Message-w,
Last seen and active w, Profile switch hide-w, Add Background Blur w,
Display Skills-w, user info api w, mic functionality w
```

**Note:** Scenarios from these sheets that have identical Web behavior should be mapped to the corresponding Web test class.

---

*Last updated: 2026-06-11 | Document version: 1.0 | Toskie Web Automation Planning*
