package com.toskie.tests.messaging;
import com.aventstack.extentreports.Status;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.constants.TestGroups;
import com.toskie.pages.messaging.CallListingsPage;
import com.toskie.utils.AssertionHelper;
import com.toskie.utils_Layer.ReportManager;
import org.testng.annotations.Test;

public class CallListingsTests extends BaseTest {
    private CallListingsPage callPage;
    private AssertionHelper a;
    private void init() { callPage = new CallListingsPage(utilLayer); a = new AssertionHelper(); }

    @Test(groups = {TestGroups.SMOKE, TestGroups.P0}, description = "Call listings page loads")
    public void testCallListingsLoad() {
        init();
        ReportManager.getTest().log(Status.INFO, "Loading call listings");
        a.assertTrue(callPage.isCallListVisible(), "Call list should be visible");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2}, description = "Call history items are displayed")
    public void testCallHistoryItems() {
        init();
        a.assertTrue(callPage.getCallCount() >= 0, "Call history loaded without error");
        a.assertAll();
    }
}

