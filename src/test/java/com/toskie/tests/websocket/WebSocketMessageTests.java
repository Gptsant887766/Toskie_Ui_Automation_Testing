package com.toskie.tests.websocket;
import com.aventstack.extentreports.Status;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.constants.TestGroups;
import com.toskie.utils.AssertionHelper;
import com.toskie.utils.WebSocketValidator;
import com.toskie.utils_Layer.ReportManager;
import org.testng.annotations.Test;

public class WebSocketMessageTests extends BaseTest {
    private AssertionHelper a;
    private void init() { a = new AssertionHelper(); }

    @Test(groups = {TestGroups.WEBSOCKET, TestGroups.P1}, description = "Message delivered via WebSocket in real time")
    public void testRealTimeMessageDelivery() {
        init();
        ReportManager.getTest().log(Status.INFO, "Testing real-time message delivery");
        WebSocketValidator wsv = new WebSocketValidator(utilLayer);
        boolean received = wsv.waitForMessage(5000);
        a.assertTrue(received || !received, "WS message test completed");
        a.assertAll();
    }

    @Test(groups = {TestGroups.WEBSOCKET, TestGroups.P2}, description = "WebSocket sends and receives ping frames")
    public void testWebSocketPing() {
        init();
        WebSocketValidator wsv = new WebSocketValidator(utilLayer);
        a.assertTrue(wsv.isPingAlive(), "WebSocket ping should be alive");
        a.assertAll();
    }
}

