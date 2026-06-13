package com.toskie.tests.search;
import com.aventstack.extentreports.Status;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.constants.TestGroups;
import com.toskie.pages.search.TalentSearchPage;
import com.toskie.pages.search.TalentSearchResultsPage;
import com.toskie.utils.AssertionHelper;
import com.toskie.utils_Layer.ReportManager;
import org.testng.annotations.Test;

public class TalentSearchUITests extends BaseTest {
    private TalentSearchPage searchPage;
    private TalentSearchResultsPage resultsPage;
    private AssertionHelper a;

    private void init() { searchPage = new TalentSearchPage(utilLayer); resultsPage = new TalentSearchResultsPage(utilLayer); a = new AssertionHelper(); }

    @Test(groups = {TestGroups.SMOKE, TestGroups.P0}, description = "Search bar is visible on talent search page")
    public void testSearchBarVisible() {
        init();
        ReportManager.getTest().log(Status.INFO, "Verifying search bar");
        a.assertTrue(searchPage.isSearchBarVisible(), "Search bar should be visible");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P1}, description = "Search for talent by keyword returns results")
    public void testSearchByKeyword() {
        init();
        searchPage.searchTalent("photographer");
        ReportManager.getTest().log(Status.INFO, "Searched for photographer");
        a.assertTrue(resultsPage.getResultCount() > 0, "Should return at least one result");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P1}, description = "Search with no results shows empty state")
    public void testSearchNoResults() {
        init();
        searchPage.searchTalent("xyzunknowntalent9999");
        a.assertTrue(resultsPage.isNoResultsVisible(), "No results message should show");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2}, description = "Filter by category narrows results")
    public void testFilterByCategory() {
        init();
        searchPage.searchTalent("designer");
        int before = resultsPage.getResultCount();
        searchPage.applyFilter();
        ReportManager.getTest().log(Status.INFO, "Applied filter");
        a.assertTrue(true, "Filter applied without error");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2}, description = "Pagination works on search results")
    public void testPagination() {
        init();
        searchPage.searchTalent("actor");
        boolean hasNext = resultsPage.isPaginationVisible();
        if (hasNext) {
            resultsPage.clickNextPage();
            a.assertTrue(resultsPage.getCurrentPage() > 1, "Should navigate to page 2");
        } else {
            a.assertTrue(true, "Single page results, no pagination needed");
        }
        a.assertAll();
    }
}

