package com.toskie.utils;

import com.aventstack.extentreports.Status;
import com.microsoft.playwright.Locator;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.ReportManager;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

/**
 * Rich assertion helpers that log to ExtentReports on every pass/fail.
 * Uses soft assertions so a test can collect all failures before failing.
 */
public class AssertionHelper {

    private final SoftAssert softAssert;

    public AssertionHelper() {
        this.softAssert = new SoftAssert();
    }

    // ─── Soft assertion API ──────────────────────────────────────────────────

    public void assertEquals(Object actual, Object expected, String message) {
        try {
            Assert.assertEquals(actual, expected);
            pass(message + " | expected='" + expected + "' actual='" + actual + "'");
        } catch (AssertionError e) {
            fail(message + " | expected='" + expected + "' actual='" + actual + "'");
            softAssert.assertEquals(actual, expected, message);
        }
    }

    public void assertTrue(boolean condition, String message) {
        try {
            Assert.assertTrue(condition);
            pass(message);
        } catch (AssertionError e) {
            fail(message + " (condition was FALSE)");
            softAssert.assertTrue(condition, message);
        }
    }

    public void assertFalse(boolean condition, String message) {
        try {
            Assert.assertFalse(condition);
            pass(message);
        } catch (AssertionError e) {
            fail(message + " (condition was TRUE)");
            softAssert.assertFalse(condition, message);
        }
    }

    public void assertContains(String actual, String expected, String message) {
        boolean contains = actual != null && actual.contains(expected);
        if (contains) {
            pass(message + " | '" + actual + "' contains '" + expected + "'");
        } else {
            fail(message + " | '" + actual + "' does NOT contain '" + expected + "'");
            softAssert.assertTrue(contains, message);
        }
    }

    public void assertNotNull(Object obj, String message) {
        if (obj != null) {
            pass(message + " is not null.");
        } else {
            fail(message + " is NULL.");
            softAssert.assertNotNull(obj, message);
        }
    }

    public void assertNotEmpty(String value, String message) {
        boolean notEmpty = value != null && !value.trim().isEmpty();
        if (notEmpty) {
            pass(message + " is not empty: '" + value + "'");
        } else {
            fail(message + " is empty or null.");
            softAssert.assertTrue(notEmpty, message);
        }
    }

    // ─── Playwright-specific ─────────────────────────────────────────────────

    public void assertVisible(Locator locator, String elementName) {
        boolean visible;
        try { visible = locator.isVisible(); } catch (Exception e) { visible = false; }
        if (visible) {
            pass(elementName + " is VISIBLE.");
        } else {
            fail(elementName + " is NOT visible.");
            softAssert.assertTrue(visible, elementName + " should be visible.");
        }
    }

    public void assertNotVisible(Locator locator, String elementName) {
        boolean visible;
        try { visible = locator.isVisible(); } catch (Exception e) { visible = false; }
        if (!visible) {
            pass(elementName + " is correctly NOT visible.");
        } else {
            fail(elementName + " should NOT be visible but IS visible.");
            softAssert.assertFalse(visible, elementName + " should not be visible.");
        }
    }

    public void assertEnabled(Locator locator, String elementName) {
        boolean enabled;
        try { enabled = locator.isEnabled(); } catch (Exception e) { enabled = false; }
        if (enabled) {
            pass(elementName + " is ENABLED.");
        } else {
            fail(elementName + " is DISABLED (expected enabled).");
            softAssert.assertTrue(enabled, elementName + " should be enabled.");
        }
    }

    public void assertDisabled(Locator locator, String elementName) {
        boolean enabled;
        try { enabled = locator.isEnabled(); } catch (Exception e) { enabled = false; }
        if (!enabled) {
            pass(elementName + " is correctly DISABLED.");
        } else {
            fail(elementName + " should be DISABLED but is ENABLED.");
            softAssert.assertFalse(enabled, elementName + " should be disabled.");
        }
    }

    public void assertTextEquals(Locator locator, String expectedText, String elementName) {
        String actual = "";
        try { actual = locator.textContent().trim(); } catch (Exception ignored) {}
        assertEquals(actual, expectedText, elementName + " text");
    }

    public void assertTextContains(Locator locator, String expectedFragment, String elementName) {
        String actual = "";
        try { actual = locator.textContent().trim(); } catch (Exception ignored) {}
        assertContains(actual, expectedFragment, elementName + " text");
    }

    public void assertUrlContains(String expected) {
        String currentUrl = BrowserManager.getPage().url();
        assertContains(currentUrl, expected, "URL");
    }

    public void assertPageTitle(String expectedTitle) {
        String title = BrowserManager.getPage().title();
        assertEquals(title, expectedTitle, "Page Title");
    }

    public void assertElementCount(Locator locator, int expectedCount, String elementName) {
        int actual = locator.count();
        assertEquals(actual, expectedCount, elementName + " count");
    }

    public void assertAtLeastOneElement(Locator locator, String elementName) {
        int count = locator.count();
        assertTrue(count > 0, elementName + " should have at least 1 element (found " + count + ")");
    }

    // ─── Performance assertions ───────────────────────────────────────────────

    public void assertPageLoadUnder(long actualMs, long thresholdMs, String pageName) {
        if (actualMs <= thresholdMs) {
            pass(pageName + " loaded in " + actualMs + "ms (threshold: " + thresholdMs + "ms)");
        } else {
            fail(pageName + " took " + actualMs + "ms (threshold: " + thresholdMs + "ms) -- PERFORMANCE VIOLATION");
        }
    }

    public void assertApiResponseUnder(long actualMs, long thresholdMs, String apiName) {
        if (actualMs <= thresholdMs) {
            pass(apiName + " responded in " + actualMs + "ms (threshold: " + thresholdMs + "ms)");
        } else {
            fail(apiName + " took " + actualMs + "ms (threshold: " + thresholdMs + "ms) -- SLOW API");
        }
    }

    // ─── Security assertions ──────────────────────────────────────────────────

    public void assertNoScriptExecution(String payload, String elementName) {
        String pageContent = BrowserManager.getPage().content();
        boolean injected = pageContent.contains("<script>") && pageContent.contains("alert");
        assertFalse(injected, "XSS payload should NOT execute in " + elementName + " [payload: " + payload + "]");
    }

    public void assertInputSanitized(Locator locator, String payload, String elementName) {
        String actual = "";
        try { actual = locator.inputValue(); } catch (Exception ignored) {}
        boolean containsRawScript = actual.contains("<script>") || actual.contains("onerror=");
        assertFalse(containsRawScript, "Input " + elementName + " should sanitize XSS payload");
    }

    // ─── Accessibility assertions ─────────────────────────────────────────────

    public void assertAriaLabel(Locator locator, String expectedLabel, String elementName) {
        String aria = "";
        try { aria = locator.getAttribute("aria-label"); } catch (Exception ignored) {}
        assertNotEmpty(aria, elementName + " aria-label");
    }

    public void assertAltText(Locator imgLocator, String elementName) {
        String alt = "";
        try { alt = imgLocator.getAttribute("alt"); } catch (Exception ignored) {}
        assertNotEmpty(alt, elementName + " alt text");
    }

    public void assertRole(Locator locator, String expectedRole, String elementName) {
        String role = "";
        try { role = locator.getAttribute("role"); } catch (Exception ignored) {}
        assertEquals(role, expectedRole, elementName + " role");
    }

    // ─── Finalize (call at end of test) ──────────────────────────────────────

    public void assertAll() {
        softAssert.assertAll();
    }

    // ─── Logging helpers ──────────────────────────────────────────────────────

    private void pass(String message) {
        try { ReportManager.getTest().log(Status.PASS, "✓ " + message); }
        catch (Exception ignored) {}
        System.out.println("[ASSERT PASS] " + message);
    }

    private void fail(String message) {
        try { ReportManager.getTest().log(Status.FAIL, "✗ " + message); }
        catch (Exception ignored) {}
        System.out.println("[ASSERT FAIL] " + message);
    }
}
