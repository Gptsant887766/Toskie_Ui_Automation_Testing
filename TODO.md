# Code Changes TODO List

## Issues Found:

### 1. UtilLayer.java - Missing Semicolons
- **Location:** `captureScreenshot` method around line 1047
- **Issue:** Missing semicolons in two lines:
  - `getTest().fail("Test Failed - Screenshot attached")` - needs semicolon
  - `getTest().info("Screenshot captured")` - needs semicolon

### 2. ExplorePage.java - Incomplete Method
- **Location:** `verifyExplorePage()` method
- **Issue:** Method only has TODO comment and print statement, no actual implementation

## Plan:
1. Fix semicolons in UtilLayer.java - captureScreenshot method
2. Implement verifyExplorePage() method in ExplorePage.java

## Status: ✅ COMPLETED
- [x] Fix semicolons in UtilLayer.java - DONE
- [x] Implement verifyExplorePage() method - DONE
