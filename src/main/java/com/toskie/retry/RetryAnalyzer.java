package com.toskie.retry;

import com.toskie.config.ConfigReader;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Enterprise retry analyzer — reads retry count from ConfigReader so it can be
 * overridden per-environment without recompiling (e.g. -Dmax.retries=0 in prod).
 *
 * Registration: either via RetryTransformer (applies to all tests automatically)
 * or per-test: @Test(retryAnalyzer = RetryAnalyzer.class)
 *
 * Note: TestNG records each retry attempt as SKIPPED. Achieving SKIPPED=0 in
 * reports requires FAILED=0 — there is no way to suppress retry SKIPs at the
 * TestNG API level. Use ExtentManager/TestListener to filter them from HTML output.
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private int count = 0;

    @Override
    public boolean retry(ITestResult result) {
        boolean retryEnabled = ConfigReader.getBoolean("enable.retry", true);
        int maxRetries = ConfigReader.getMaxRetries();

        if (!retryEnabled) return false;

        if (count < maxRetries) {
            count++;
            System.out.printf("[RetryAnalyzer] '%s' failed — retry %d/%d%n",
                    result.getName(), count, maxRetries);
            return true;
        }
        return false;
    }
}
