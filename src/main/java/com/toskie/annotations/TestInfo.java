package com.toskie.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declarative test metadata read by TestListener to stamp Allure labels,
 * Extent categories, and CI reports without boilerplate in every test body.
 *
 * Usage:
 * <pre>
 *   &#64;Test(groups = {TestGroups.SMOKE, TestGroups.P0})
 *   &#64;TestInfo(
 *       module   = "Authentication",
 *       owner    = "qa-auth-team",
 *       tcId     = "TC-AUTH-001",
 *       severity = SeverityLevel.CRITICAL
 *   )
 *   public void verifyLogin() { ... }
 * </pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TestInfo {

    /** Feature module this test belongs to (e.g., "Authentication", "Dashboard"). */
    String module() default "";

    /** Team or individual responsible for this test. */
    String owner() default "qa-team";

    /** Test plan / JIRA ticket ID (e.g., "TC-AUTH-001", "TOSK-1234"). */
    String tcId() default "";

    /** Allure severity label. Uses string to avoid compile-time Allure dependency. */
    String severity() default "normal";   // blocker | critical | normal | minor | trivial

    /** Free-form description appended to the Allure test description panel. */
    String description() default "";
}
