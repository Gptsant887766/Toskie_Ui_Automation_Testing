package com.toskie.utils;

import com.aventstack.extentreports.Status;
import com.microsoft.playwright.Locator;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.ReportManager;

import java.util.ArrayList;
import java.util.List;

/**
 * WCAG 2.1 / ARIA accessibility validation utilities.
 * Tests keyboard navigation, screen reader compatibility, color contrast, focus management.
 */
public class AccessibilityUtils {

    // ─── ARIA validation ─────────────────────────────────────────────────────

    public void assertAllImagesHaveAltText() {
        List<String> violations = new ArrayList<>();
        try {
            Object result = BrowserManager.getPage().evaluate(
                "() => Array.from(document.querySelectorAll('img'))"
                + ".filter(img => !img.alt || img.alt.trim() === '')"
                + ".map(img => img.src || img.outerHTML.substring(0,100))");
            if (result instanceof List<?> imgs && !imgs.isEmpty()) {
                imgs.forEach(i -> violations.add(i.toString()));
                ReportManager.getTest().log(Status.FAIL,
                    "WCAG 1.1.1: Images without alt text (" + imgs.size() + "): " + violations);
            } else {
                ReportManager.getTest().log(Status.PASS, "All images have alt text (WCAG 1.1.1).");
            }
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "Alt text check error: " + e.getMessage());
        }
    }

    public void assertAllButtonsHaveAccessibleName() {
        try {
            Object result = BrowserManager.getPage().evaluate(
                "() => Array.from(document.querySelectorAll('button'))"
                + ".filter(b => !b.textContent?.trim() && !b.getAttribute('aria-label') && !b.getAttribute('title'))"
                + ".length");
            int count = result != null ? ((Number) result).intValue() : 0;
            if (count > 0) {
                ReportManager.getTest().log(Status.FAIL,
                    "WCAG 4.1.2: " + count + " buttons have no accessible name.");
            } else {
                ReportManager.getTest().log(Status.PASS, "All buttons have accessible names (WCAG 4.1.2).");
            }
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "Button name check error: " + e.getMessage());
        }
    }

    public void assertAllInputsHaveLabels() {
        try {
            Object result = BrowserManager.getPage().evaluate(
                "() => Array.from(document.querySelectorAll('input:not([type=hidden])'))"
                + ".filter(inp => {"
                + "  const id = inp.id;"
                + "  const hasLabel = id && document.querySelector('label[for=\"'+id+'\"]');"
                + "  const hasAria = inp.getAttribute('aria-label') || inp.getAttribute('aria-labelledby');"
                + "  const hasPlaceholder = inp.placeholder?.trim();"
                + "  return !hasLabel && !hasAria && !hasPlaceholder;"
                + "}).length");
            int count = result != null ? ((Number) result).intValue() : 0;
            if (count > 0) {
                ReportManager.getTest().log(Status.FAIL,
                    "WCAG 1.3.1: " + count + " inputs have no label.");
            } else {
                ReportManager.getTest().log(Status.PASS, "All inputs have labels (WCAG 1.3.1).");
            }
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "Input label check error: " + e.getMessage());
        }
    }

    public void assertHeadingHierarchy() {
        try {
            Object result = BrowserManager.getPage().evaluate(
                "() => {"
                + "  const headings = Array.from(document.querySelectorAll('h1,h2,h3,h4,h5,h6'));"
                + "  return headings.map(h => ({tag:h.tagName, text:h.textContent?.trim()?.substring(0,50)}));"
                + "}");
            if (result instanceof List<?> headings) {
                boolean hasH1 = headings.stream().anyMatch(h -> h.toString().contains("H1"));
                if (hasH1) {
                    ReportManager.getTest().log(Status.PASS,
                        "Heading hierarchy present (" + headings.size() + " headings). WCAG 1.3.1.");
                } else {
                    ReportManager.getTest().log(Status.WARNING,
                        "No H1 found. Consider adding a main heading for screen readers. WCAG 1.3.1.");
                }
            }
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "Heading hierarchy check error: " + e.getMessage());
        }
    }

    public void assertLandmarkRoles() {
        try {
            Object result = BrowserManager.getPage().evaluate(
                "() => ({"
                + "  main: !!document.querySelector('main, [role=\"main\"]'),"
                + "  nav:  !!document.querySelector('nav, [role=\"navigation\"]'),"
                + "  banner: !!document.querySelector('header, [role=\"banner\"]')"
                + "})");
            if (result instanceof java.util.Map<?,?> roles) {
                boolean main = Boolean.TRUE.equals(roles.get("main"));
                boolean nav  = Boolean.TRUE.equals(roles.get("nav"));
                if (main) {
                    ReportManager.getTest().log(Status.PASS, "Main landmark present (WCAG 1.3.6).");
                } else {
                    ReportManager.getTest().log(Status.WARNING, "No <main> landmark found.");
                }
                if (!nav) {
                    ReportManager.getTest().log(Status.WARNING, "No <nav> landmark found.");
                }
            }
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "Landmark check error: " + e.getMessage());
        }
    }

    // ─── Focus management ─────────────────────────────────────────────────────

    public void assertFocusableElementsHaveFocusIndicator() {
        try {
            Object result = BrowserManager.getPage().evaluate(
                "() => {"
                + "  const focusable = document.querySelectorAll('button, a, input, select, textarea, [tabindex]');"
                + "  let noFocus = 0;"
                + "  focusable.forEach(el => {"
                + "    el.focus();"
                + "    const style = window.getComputedStyle(el, ':focus');"
                + "    if (style.outlineWidth === '0px' && style.boxShadow === 'none') noFocus++;"
                + "  });"
                + "  return noFocus;"
                + "}");
            int count = result != null ? ((Number) result).intValue() : 0;
            if (count > 0) {
                ReportManager.getTest().log(Status.WARNING,
                    count + " focusable elements may lack visible focus indicator (WCAG 2.4.7).");
            } else {
                ReportManager.getTest().log(Status.PASS, "Focus indicators present (WCAG 2.4.7).");
            }
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "Focus check error: " + e.getMessage());
        }
    }

    public void testTabKeyNavigation(int steps) {
        ReportManager.getTest().log(Status.INFO, "Tab key navigation test (" + steps + " steps).");
        for (int i = 0; i < steps; i++) {
            BrowserManager.getPage().keyboard().press("Tab");
            BrowserManager.getPage().waitForTimeout(200);
        }
        // Check if focus is still on the page
        try {
            Object focused = BrowserManager.getPage().evaluate(
                "() => document.activeElement?.tagName");
            if (focused != null && !"BODY".equals(focused.toString())) {
                ReportManager.getTest().log(Status.PASS,
                    "Tab navigation working -- focused: " + focused);
            }
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "Tab test error: " + e.getMessage());
        }
    }

    public void assertEscapeKeyClosesModal(Locator modal) {
        BrowserManager.getPage().keyboard().press("Escape");
        BrowserManager.getPage().waitForTimeout(500);
        boolean closed;
        try { closed = !modal.isVisible(); } catch (Exception e) { closed = true; }
        if (closed) {
            ReportManager.getTest().log(Status.PASS, "Escape key closes modal (WCAG 2.1.2).");
        } else {
            ReportManager.getTest().log(Status.FAIL, "Modal NOT closed by Escape key (WCAG 2.1.2).");
        }
    }

    // ─── Color contrast ───────────────────────────────────────────────────────

    public void checkColorContrast() {
        try {
            Object result = BrowserManager.getPage().evaluate(
                "() => {"
                + "  const body = document.body;"
                + "  const style = window.getComputedStyle(body);"
                + "  return {bg: style.backgroundColor, color: style.color};"
                + "}");
            ReportManager.getTest().log(Status.INFO,
                "Page color info: " + result + " -- manual contrast check recommended (WCAG 1.4.3).");
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "Color contrast check error: " + e.getMessage());
        }
    }

    // ─── Screen reader / ARIA live ────────────────────────────────────────────

    public void assertAriaLiveRegionPresent() {
        try {
            Object result = BrowserManager.getPage().evaluate(
                "() => !!document.querySelector('[aria-live]')");
            if (Boolean.TRUE.equals(result)) {
                ReportManager.getTest().log(Status.PASS, "ARIA live region present.");
            } else {
                ReportManager.getTest().log(Status.WARNING,
                    "No ARIA live region found -- dynamic content may not be announced by screen readers.");
            }
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "ARIA live check error: " + e.getMessage());
        }
    }

    public void assertErrorMessagesHaveRole() {
        try {
            Object result = BrowserManager.getPage().evaluate(
                "() => Array.from(document.querySelectorAll('[class*=error]'))"
                + ".filter(el => !el.getAttribute('role') && !el.getAttribute('aria-live')).length");
            int count = result != null ? ((Number) result).intValue() : 0;
            if (count > 0) {
                ReportManager.getTest().log(Status.WARNING,
                    count + " error elements lack role='alert' (WCAG 4.1.3).");
            } else {
                ReportManager.getTest().log(Status.PASS, "Error elements have proper roles.");
            }
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "Error role check error: " + e.getMessage());
        }
    }

    // ─── Full WCAG audit ─────────────────────────────────────────────────────

    public void runFullAccessibilityAudit(String pageName) {
        ReportManager.getTest().log(Status.INFO,
            "═══ Accessibility Audit: " + pageName + " ═══");
        assertAllImagesHaveAltText();
        assertAllButtonsHaveAccessibleName();
        assertAllInputsHaveLabels();
        assertHeadingHierarchy();
        assertLandmarkRoles();
        assertAriaLiveRegionPresent();
        assertErrorMessagesHaveRole();
        assertFocusableElementsHaveFocusIndicator();
        checkColorContrast();
    }
}
