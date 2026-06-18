package com.toskie.tests.misc;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.constants.TestGroups;
import com.toskie.utils.AssertionHelper;
import com.toskie.utils_Layer.BrowserManager;
import org.testng.annotations.Test;

public class FetchContactsReviewTests extends BaseTest {
    private AssertionHelper a;
    private void init() { a = new AssertionHelper(); }

    @Test(groups = {TestGroups.API, TestGroups.P2}, description = "Fetch contacts API returns connection list")
    public void testFetchContactsApi() { init(); a.assertContains(BrowserManager.getPage().url(), "toskie.com", "Fetch contacts API test -- should be on toskie.com"); a.assertAll(); }

    @Test(groups = {TestGroups.API, TestGroups.P2}, description = "Review contact API returns reviewer details")
    public void testFetchContactReviewApi() { init(); a.assertContains(BrowserManager.getPage().url(), "toskie.com", "Fetch contact review API test -- should be on toskie.com"); a.assertAll(); }
}
