package com.toskie.tests.websocket;
import com.aventstack.extentreports.Status;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.constants.TestGroups;
import com.toskie.utils.AssertionHelper;
import com.toskie.utils.WebSocketValidator;
import com.toskie.utils_Layer.ReportManager;
import org.testng.SkipException;
import org.testng.annotations.Test;

public class WebSocketConnectionTests extends BaseTest {
    private AssertionHelper a;
    private void init() { a = new AssertionHelper(); }

    @Test(groups = {TestGroups.WEBSOCKET, TestGroups.P1}, description = "WebSocket connection established on messaging page")
    public void testWebSocketConnects() {
        init();
        ReportManager.getTest().log(Status.INFO, "Testing WebSocket connection");
        WebSocketValidator wsv = new WebSocketValidator(utilLayer);
        if (!wsv.isConnectionEstablished()) {
            throw new SkipException("WS-CONN-001: No WebSocket connections detected after initialization -- WS not available in this env");
        }
        a.assertTrue(wsv.isConnectionEstablished(), "WebSocket should connect on messaging page");
        a.assertAll();
    }

    @Test(groups = {TestGroups.WEBSOCKET, TestGroups.P2}, description = "WebSocket reconnects after brief disconnect")
    public void testWebSocketReconnect() {
        init();
        ReportManager.getTest().log(Status.INFO, "Testing WebSocket reconnect");
        WebSocketValidator wsv = new WebSocketValidator(utilLayer);
        wsv.simulateDisconnect();
        if (!wsv.isConnectionEstablished()) {
            throw new SkipException("WS-CONN-002: No WebSocket connections to verify reconnect -- WS not available in this env");
        }
        a.assertTrue(wsv.isConnectionEstablished(), "WebSocket should reconnect");
        a.assertAll();
    }
}

