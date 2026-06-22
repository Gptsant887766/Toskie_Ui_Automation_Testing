# BUILD FIX REPORT

**Date:** 2026-06-17  
**Framework:** Toskie Web Automation Framework  
**Objective:** Achieve `mvn clean test-compile` BUILD SUCCESS

---

## Phase 1: Errors Found and Fixed

### Fix 1 — RetryTransformer: `setRetryAnalyzerClass` not in TestNG 7.9.0

| Property | Value |
|---|---|
| File | `src/main/java/com/toskie/utils_Layer/RetryTransformer.java:26` |
| Error | `cannot find symbol: method setRetryAnalyzerClass(Class<RetryAnalyzer>)` |
| Root Cause | TestNG 7.x uses `setRetryAnalyzer()` — `setRetryAnalyzerClass` does not exist |
| Fix | `annotation.setRetryAnalyzerClass(RetryAnalyzer.class)` → `annotation.setRetryAnalyzer(RetryAnalyzer.class)` |

---

### Fix 2 — UTF-8 BOM in 83 Java files

| Property | Value |
|---|---|
| Files | 83 Java files (AllToskieTestCases.java, LoginTestCases.java, EndToEndTests.java, EdgeCaseTests.java, NegativeTests.java, PerformanceTests.java, plus 77 others) |
| Error | `illegal character: '﻿'` at line 1 of each file |
| Root Cause | Files were saved with UTF-8 BOM (Byte Order Mark) by the Windows editor |
| Fix | PowerShell batch strip: read raw bytes, skip first 3 bytes if `EF BB BF`, write back as UTF-8-no-BOM |

---

### Fix 3 — Curly quotes and special Unicode in string literals

| Property | Value |
|---|---|
| Files | `EndToEndTests.java`, `WebSocketTests.java`, `PerformanceTests.java`, `EdgeCaseTests.java`, `SecurityTests.java`, and others |
| Error | `illegal character: '“'`, `'”'`, `'†'` — curly quotes and dagger chars in string literals |
| Root Cause | Content was pasted from rich-text sources (Word, browser copy-paste) into Java strings |
| Fix | PowerShell global replace: `“”` → `"`, `‘’` → `'`, `†` → stripped |

---

### Fix 4 — Garbled multi-byte artifact sequences

| Property | Value |
|---|---|
| Files | `WebSocketTests.java:67`, `EndToEndTests.java:22,52`, `PerformanceTests.java:92,96,115,135,182` |
| Error | `unclosed string literal`, `not a statement` — sequences like `a€"` and `aœ"` inside strings |
| Root Cause | Files originally contained em-dashes (`—` U+2014) and checkmarks encoded as Windows-1252 multi-byte sequences; partial conversion left garbled artifact chars (`a€"` from `E2 80 94`) |
| Fix | PowerShell pattern replace of artifact sequences; then restoring `a.assert` method calls corrupted to `-assert` by overly broad regex |

---

### Fix 5 — `IdorTests.java`: Array index on `List<String>`

| Property | Value |
|---|---|
| File | `src/test/java/com/toskie/tests/security/IdorTests.java:78,137,181,226` |
| Error | `array required, but java.util.List<java.lang.String> found` |
| Root Cause | `SecurityUtils.BOLA_TEST_IDS` is declared as `List<String>` but code used `[0]` array-index access |
| Fix | `SecurityUtils.BOLA_TEST_IDS[0]` → `SecurityUtils.BOLA_TEST_IDS.get(0)` (4 occurrences) |

---

### Fix 6 — `LoginTestCases.java`: Dead `InterruptedException` catch

| Property | Value |
|---|---|
| File | `src/test/java/com/toskie/LoginTestCases.java:27` |
| Error | `exception java.lang.InterruptedException is never thrown in body of corresponding try statement` |
| Root Cause | `WaitManager.safePageLoad()` does not declare `throws InterruptedException` but a `catch (InterruptedException)` block existed around it |
| Fix | Removed the orphaned `try/catch` wrapper; `safePageLoad()` called directly |

---

## Final Result

| Metric | Value |
|---|---|
| Starting error count | 206+ compiler errors |
| Remaining errors after all fixes | **0** |
| `mvn clean test-compile` status | **BUILD SUCCESS** |
| Build time | 15.9 seconds |
| Files modified | 83 (encoding) + 5 (logic fixes) = **88 files** |
| Build time (2026-06-17) | 15:32:35 IST |

---

## Files Changed (Summary)

| File | Change Type |
|---|---|
| `RetryTransformer.java` | Logic — wrong API method name |
| `IdorTests.java` | Logic — List vs array index access |
| `LoginTestCases.java` | Logic — dead catch block |
| 83 Java source files | Encoding — BOM strip + Unicode char normalization |

---

*Report generated: 2026-06-17*
