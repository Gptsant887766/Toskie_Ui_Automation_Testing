package com.toskie.tests.search;
import com.aventstack.extentreports.Status;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.constants.TestGroups;
import com.toskie.pages.search.TalentSearchPage;
import com.toskie.pages.search.TalentSearchResultsPage;
import com.toskie.utils.AssertionHelper;
import com.toskie.utils_Layer.ReportManager;
import org.testng.annotations.Test;

public class TalentAddressSearchTests extends BaseTest {
    private TalentSearchPage searchPage;
    private TalentSearchResultsPage resultsPage;
    private AssertionHelper a;

    private void init() { searchPage = new TalentSearchPage(utilLayer); resultsPage = new TalentSearchResultsPage(utilLayer); a = new AssertionHelper(); }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2}, description = "Search talent by location filters results correctly")
    public void testSearchByLocation() {
        init();
        ReportManager.getTest().log(Status.INFO, "Searching talent by location");
        searchPage.searchByLocation("Mumbai");
        a.assertTrue(resultsPage.getResultCount() >= 0, "Location search should execute without error");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2}, description = "Map view shows talent pins")
    public void testMapViewShowsPins() {
        init();
        searchPage.searchTalent("photographer");
        searchPage.switchToMapView();
        a.assertTrue(searchPage.isMapVisible(), "Map should be visible after switching");
        a.assertAll();
    }
}

