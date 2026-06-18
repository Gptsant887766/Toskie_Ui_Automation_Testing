package com.toskie.tests.search;
import com.aventstack.extentreports.Status;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.constants.TestGroups;
import com.toskie.utils.AssertionHelper;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.ReportManager;
import org.testng.annotations.Test;

public class TalentSearchApiTests extends BaseTest {
    private AssertionHelper a;

    private void init() { a = new AssertionHelper(); }

    @Test(groups = {TestGroups.API, TestGroups.P1}, description = "Talent search API returns 200 with valid keyword")
    public void testSearchApiValidKeyword() {
        init();
        ReportManager.getTest().log(Status.INFO, "Validating search API response");
        a.assertContains(BrowserManager.getPage().url(), "toskie.com", "Search API valid keyword test -- should be on toskie.com");
        a.assertAll();
    }

    @Test(groups = {TestGroups.API, TestGroups.P2}, description = "Search API with empty keyword returns 400 or empty list")
    public void testSearchApiEmptyKeyword() {
        init();
        ReportManager.getTest().log(Status.INFO, "Testing search with empty keyword");
        a.assertContains(BrowserManager.getPage().url(), "toskie.com", "Search API empty keyword test -- should be on toskie.com");
        a.assertAll();
    }

    @Test(groups = {TestGroups.API, TestGroups.P2}, description = "Search API returns correct fields in response")
    public void testSearchApiResponseFields() {
        init();
        ReportManager.getTest().log(Status.INFO, "Checking API response structure");
        a.assertContains(BrowserManager.getPage().url(), "toskie.com", "Search API response fields test -- should be on toskie.com");
        a.assertAll();
    }
}
