package com.toskie.tests.websocket;
import com.aventstack.extentreports.Status;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.constants.TestGroups;
import com.toskie.utils.AssertionHelper;
import com.toskie.utils.WebSocketValidator;
import com.toskie.utils_Layer.ReportManager;
import org.testng.annotations.Test;

public class TimezoneWebSocketTests extends BaseTest {
    private AssertionHelper a;
    private void init() { a = new AssertionHelper(); }

    @Test(groups = {TestGroups.WEBSOCKET, TestGroups.P3}, description = "Message timestamps are in correct timezone")
    public void testMessageTimestampTimezone() {
        init();
        ReportManager.getTest().log(Status.INFO, "Validating WS message timestamps");
        WebSocketValidator wsv = new WebSocketValidator(utilLayer);
        a.assertTrue(wsv.isTimestampValid(), "Timestamps should be in server timezone");
        a.assertAll();
    }
}

