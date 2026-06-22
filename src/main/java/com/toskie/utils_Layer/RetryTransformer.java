package com.toskie.utils_Layer;

import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * TestNG listener that injects RetryAnalyzer into every @Test method at runtime.
 * Register in suite XML -- no @Test(retryAnalyzer=...) needed on individual tests.
 *
 * Usage in testng suite XML:
 *   <listeners>
 *     <listener class-name="com.toskie.utils_Layer.RetryTransformer"/>
 *   </listeners>
 */
public class RetryTransformer implements IAnnotationTransformer {

    @Override
    public void transform(ITestAnnotation annotation,
                          Class testClass,
                          Constructor testConstructor,
                          Method testMethod) {
        if (RetryConfig.ENABLE_RETRY) {
            annotation.setRetryAnalyzer(RetryAnalyzer.class);
        }
    }
}
