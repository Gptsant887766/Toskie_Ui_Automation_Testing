---
name: project-toskie-framework
description: "Complete state of Toskie Web Automation Framework — planning document + implementation prompt created from 136 Excel test case sheets"
metadata: 
  node_type: memory
  type: project
  originSessionId: current
---

Full enterprise-grade test automation framework built for Toskie talent marketplace app (https://dev.app.toskie.com/).

**Why:** User requested full 9-phase automation planning from 136-sheet Excel file + Claude implementation prompt.

**How to apply:** When user asks about framework status, sprint progress, or wants to implement a module, reference planning doc and master prompt.

## Framework Location
- Project: `C:\Users\Sontosh\eclipse-workspace\Toskie_Web_Automation_Framework`
- Planning Doc: `Toskie_Automation_Planning_Document.md` (in project root)
- Claude Prompt: `CLAUDE_AUTOMATION_MASTER_PROMPT.md` (in project root)
- VS Code Settings: `.vscode/settings.json` + `.vscode/extensions.json` (created 2026-06-11)

## Stack
- Java 21 · Maven · Playwright 1.53 · TestNG 7.9 · ExtentReports 5.0.9 · Lombok 1.18.38
- Target app: `https://dev.app.toskie.com/`

## Test Case Analysis (from 136-sheet Excel)
- **Total sheets:** 136 (2 empty — Done on Azure, TArun)
- **Total TCs:** ~672
- **Web Desktop TCs:** ~398
- **Mobile Flutter TCs (_w/_W):** ~152
- **API/Integration TCs:** ~122
- **Duplicates removed:** ~42
- **Net unique Web+API TCs:** ~478
- **Currently automated:** ~175

## Key Duplicates Found
1. `Task 5778 Urgent` = exact duplicate of `Activity History UI 5778` → merge
2. `TalentSearchHeaderandTabs` = exact duplicate of `TalentInputandLocationComponent` → merge
3. `Task 3496` ≈ `ProfileDashboardOpenToConnectUI` → merge into `OpenToConnectTests.java`
4. `ProfileCompleteStatus4463Q` (Web) vs `Profile Complete Status_w` (Mobile) → different platforms

## Mobile-Only Sheets (31 sheets — exclude from Web Playwright automation)
All sheets with `_w` or `_W` suffix are Flutter mobile. Don't automate in Playwright.

## Known Failing Tests / Bugs
- TC_US_70 — Duplicate skill not blocked (bug)
- TC_205, TC_195/196 — UI not matching Figma
- TC_887, TC_888 — Search header UI issues
- TC_TP_PI_169/170 — Profile info section styling bugs
- TC_178, TC_179 — Footer icon bugs
- TC_4462_001 — Skill name → Skill ID bug
- TC_961 — Message drawer no validation bug

## Sprint Plan (7 sprints, ~53 dev days)
- Sprint 1 (6d): Auth — Login, Registration, Account Recovery
- Sprint 2 (8d): Profile Creation — all 8 steps
- Sprint 3 (5.5d): Talent Search
- Sprint 4 (8.5d): Talent Profile + Dashboard
- Sprint 5 (8.5d): Messaging + WebSocket
- Sprint 6 (9.5d): Posts + Feed + Address
- Sprint 7 (7d): Blog + Reviews + AI + Activity

## Existing Page Objects
WelcomePage, LoginPage, ProfileCreationPage, HomePage, SearchPage, ChatPage, BookingPage

## Existing Utilities
AssertionHelper, NetworkValidator, WebSocketValidator, PerformanceUtils, SecurityUtils, AccessibilityUtils, DatabaseValidator, RedisValidator, TestDataManager, BrowserManager, ConfigManager, ReportManager, ApiUtils, RetryConfig, WaitManager

## Test Suites (existing + new)
testng.xml, testng-smoke.xml, testng-regression.xml, testng-api.xml, testng-websocket.xml, testng-profile.xml, testng-messaging.xml, testng-feed.xml, testng-e2e.xml, testng-master.xml
