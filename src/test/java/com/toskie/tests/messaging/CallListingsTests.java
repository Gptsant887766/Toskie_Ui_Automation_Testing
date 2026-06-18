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

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2}, description = "Call history shows items or empty state")
    public void testCallHistoryItems() {
        init();
        int count = callPage.getCallCount();
        boolean listVisible = callPage.isCallListVisible();
        ReportManager.getTest().log(Status.INFO, "CALL-2: Call count=" + count + " | list visible=" + listVisible);
        a.assertTrue(listVisible,
                "CALL-2: Call listings container must be visible regardless of call history count (count=" + count + ")");
        a.assertAll();
    }
}

