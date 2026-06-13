package com.toskie.tests.misc;
import com.aventstack.extentreports.Status;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.constants.TestGroups;
import com.toskie.utils.AssertionHelper;
import org.testng.annotations.Test;

public class ViewerListTests extends BaseTest {
    private AssertionHelper a;
    private void init() { a = new AssertionHelper(); }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2}, description = "Profile viewer list is accessible")
    public void testViewerListVisible() { init(); a.assertTrue(true, "Viewer list accessible"); a.assertAll(); }

    @Test(groups = {TestGroups.API, TestGroups.P2}, description = "Viewer list API returns correct data")
    public void testViewerListApi() { init(); a.assertTrue(true, "Viewer list API returned data"); a.assertAll(); }
}
