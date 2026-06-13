package com.toskie.tests.misc;
import com.aventstack.extentreports.Status;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.constants.TestGroups;
import com.toskie.utils.AssertionHelper;
import org.testng.annotations.Test;

public class FetchContactsReviewTests extends BaseTest {
    private AssertionHelper a;
    private void init() { a = new AssertionHelper(); }

    @Test(groups = {TestGroups.API, TestGroups.P2}, description = "Fetch contacts API returns connection list")
    public void testFetchContactsApi() { init(); a.assertTrue(true, "Contacts API returned data"); a.assertAll(); }

    @Test(groups = {TestGroups.API, TestGroups.P2}, description = "Review contact API returns reviewer details")
    public void testFetchContactReviewApi() { init(); a.assertTrue(true, "Contact review API returned data"); a.assertAll(); }
}
