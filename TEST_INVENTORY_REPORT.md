# TEST INVENTORY REPORT

**Date:** 2026-06-17  
**Framework:** Toskie Web Automation Framework  
**Scan Scope:** `src/test/java/com/toskie/**`

---

## 1. TOTALS

| Metric | Count |
|---|---|
| Total Java test files | **100** |
| Total `@Test` method annotations | **743** |
| Modern test classes (`com.toskie.tests.*`) | **92** |
| Legacy test classes (`com.toskie.*` root) | **8** |
| Suite XML files | **31** |

---

## 2. TEST COUNT BY PACKAGE / MODULE

| Package | Classes | @Test Methods | Module |
|---|---|---|---|
| `com.toskie` (legacy) | 8 | 240 | Legacy (never run in modern suites) |
| `com.toskie.tests.smoke` | 1 | 5 | Smoke Gate |
| `com.toskie.tests.auth` | 4 | 83 | Auth / Login / Registration |
| `com.toskie.tests.profile` | 11 | 75 | Profile Creation Wizard |
| `com.toskie.tests.regression` | 9 | 83 | Full Regression |
| `com.toskie.tests.posts` | 13 | 45 | Posts / Feed |
| `com.toskie.tests.dashboard` | 12 | 21 | Dashboard |
| `com.toskie.tests.messaging` | 9 | 19 | Messaging |
| `com.toskie.tests.api` | 2 | 25 | API / WebSocket |
| `com.toskie.tests.search` | 4 | 13 | Search |
| `com.toskie.tests.security` | 3 | 30 | Security / JWT / IDOR |
| `com.toskie.tests.accessibility` | 1 | 12 | Accessibility (WCAG) |
| `com.toskie.tests.performance` | 1 | 10 | Performance |
| `com.toskie.tests.subscription` | 1 | 8 | Subscription / Settings |
| `com.toskie.tests.reviews` | 1 | 4 | Reviews |
| `com.toskie.tests.e2e` | 1 | 7 | End-to-End Journeys |
| `com.toskie.tests.websocket` | 4 | 7 | WebSocket |
| `com.toskie.tests.misc` | 9 | 15 | Misc (notifications, tips, privacy) |
| `com.toskie.tests.edge` | 1 | 15 | Edge Cases |
| `com.toskie.tests.negative` | 1 | 10 | Negative / Invalid Input |
| `com.toskie.tests.activity` | 1 | 3 | Activity History |
| `com.toskie.tests.ai` | 1 | 4 | AI Bio |
| `com.toskie.tests.blog` | 1 | 4 | Blog API |
| `com.toskie.tests.landing` | 1 | 5 | Landing Page |
| **TOTAL** | **100** | **743** | |

---

## 3. TEST CLASSES BY CATEGORY

### Smoke Tests (P0)
| Class | Methods |
|---|---|
| `SmokeTests.java` | 5 |
| `AuthLoginTests.java` | 10 |
| `RegistrationTests.java` | 26 |

### Security Tests
| Class | Methods | Added |
|---|---|---|
| `SecurityTests.java` | 15 | Original (strengthened 2026-06-17) |
| `JwtSecurityTests.java` | 9 | NEW 2026-06-17 |
| `IdorTests.java` | 6 | NEW 2026-06-17 |

### Messaging Tests
| Class | Methods | Added |
|---|---|---|
| `ConversationListTests.java` | 4 | Original |
| `MessagingSendReceiveTests.java` | 5 | NEW 2026-06-17 |
| `MessageRequestTests.java` | 3 | Original |
| `RequestAcceptDeclineTests.java` | 2 | Original |
| `FetchRequestsTests.java` | 2 | Original |
| `LikeModalTests.java` | 1 | Original |
| `SimilarTalentsTests.java` | 1 | Original |
| `ValidateTalentTests.java` | 1 | Original |

### Profile Tests
| Class | Methods |
|---|---|
| `PersonalInfoStepTests.java` | 8 |
| `BioStepTests.java` | 8 |
| `SkillStepTests.java` | 10 |
| `ExperienceStepTests.java` | 8 |
| `QualificationStepTests.java` | 8 |
| `ProjectStepTests.java` | 8 |
| `GalleryStepTests.java` | 5 |
| `GalleryManagementTests.java` | 6 (NEW) |
| `ProfileCompleteStatusTests.java` | 4 |
| `ProfileVisibilityTests.java` | 8 |
| `SurveyPageTests.java` | 4 |

### Auth Tests
| Class | Methods |
|---|---|
| `AuthLoginTests.java` | 10 |
| `AuthApiTests.java` | 8 (rewritten 2026-06-17) |
| `RegistrationTests.java` | 26 |
| `AccountRecoveryTests.java` | 8 |

---

## 4. SUITE XML COVERAGE

| Suite XML | Classes | Scope |
|---|---|---|
| `SmokeSuite.xml` | 7 | P0 smoke gate |
| `SecuritySuite.xml` | 10 | Security module |
| `RegressionSuite.xml` | 53 | Full functional regression |
| `FullRegressionSuite.xml` | 94 | All modern classes (DEFAULT) |
| `ToskieMasterSuite.xml` | 97 | All classes including edge/negative |
| `E2ESuite.xml` | 26 | End-to-end journeys |
| `AccessibilitySuite.xml` | 8 | WCAG checks |
| `PerformanceSuite.xml` | 10 | Load benchmarks |

---

## 5. NEW TEST CLASSES ADDED (Sprint 2+3)

| Class | Tests | Sprint |
|---|---|---|
| `JwtSecurityTests.java` | 9 | Sprint 2 |
| `IdorTests.java` | 6 | Sprint 2 |
| `MessagingSendReceiveTests.java` | 5 | Sprint 3 |
| `GalleryManagementTests.java` | 6 | Sprint 3 |
| `AuthApiTests.java` (rewritten) | 8 | Sprint 2 |
| `SecurityTests.java` (strengthened) | 15 | Sprint 2 |

---

*Report generated: 2026-06-17 | Branch: main*
