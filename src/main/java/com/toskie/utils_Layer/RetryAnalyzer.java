package com.toskie.utils_Layer;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Retries failed tests up to RetryConfig.MAX_RETRIES times.
 * Applied globally via RetryTransformer -- no per-test annotation needed.
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private int retryCount = 0;

    @Override
    public boolean retry(ITestResult result) {
        if (!RetryConfig.ENABLE_RETRY) return false;
        if (retryCount < RetryConfig.MAX_RETRIES) {
            retryCount++;
            System.out.printf("[RetryAnalyzer] Retrying '%s' (attempt %d of %d)%n",
                    result.getName(), retryCount, RetryConfig.MAX_RETRIES);
            return true;
        }
        return false;
    }
}
