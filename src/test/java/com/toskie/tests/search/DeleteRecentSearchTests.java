package com.toskie.tests.search;
import com.aventstack.extentreports.Status;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.constants.TestGroups;
import com.toskie.pages.search.TalentSearchPage;
import com.toskie.utils.AssertionHelper;
import org.testng.annotations.Test;

public class DeleteRecentSearchTests extends BaseTest {
    private TalentSearchPage searchPage;
    private AssertionHelper a;

    private void init() { searchPage = new TalentSearchPage(utilLayer); a = new AssertionHelper(); }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2}, description = "Recent searches appear after search")
    public void testRecentSearchAppears() {
        init();
        searchPage.searchTalent("singer");
        searchPage.openSearch();
        a.assertTrue(searchPage.hasRecentSearches(), "Recent searches should be shown");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2}, description = "Delete single recent search entry")
    public void testDeleteSingleRecentSearch() {
        init();
        searchPage.searchTalent("dancer");
        searchPage.openSearch();
        int before = searchPage.getRecentSearchCount();
        searchPage.deleteRecentSearch(0);
        a.assertTrue(searchPage.getRecentSearchCount() < before, "Recent search should be removed");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P3}, description = "Clear all recent searches")
    public void testClearAllRecentSearches() {
        init();
        searchPage.searchTalent("actor");
        searchPage.openSearch();
        searchPage.clearAllRecentSearches();
        a.assertFalse(searchPage.hasRecentSearches(), "All recent searches should be cleared");
        a.assertAll();
    }
}
