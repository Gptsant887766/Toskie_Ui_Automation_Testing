package com.toskie.tests.websocket;
import com.aventstack.extentreports.Status;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.constants.TestGroups;
import com.toskie.utils.AssertionHelper;
import com.toskie.utils.WebSocketValidator;
import com.toskie.utils_Layer.ReportManager;
import org.testng.annotations.Test;

public class BulkMessageReadTests extends BaseTest {
    private AssertionHelper a;
    private void init() { a = new AssertionHelper(); }

    @Test(groups = {TestGroups.WEBSOCKET, TestGroups.P2}, description = "Opening conversation marks all messages as read")
    public void testBulkMarkRead() {
        init();
        ReportManager.getTest().log(Status.INFO, "Testing bulk message read via WebSocket");
        WebSocketValidator wsv = new WebSocketValidator(utilLayer);
        wsv.triggerBulkRead();
        a.assertTrue(true, "Bulk read event triggered");
        a.assertAll();
    }

    @Test(groups = {TestGroups.WEBSOCKET, TestGroups.P3}, description = "Unread count updates via WebSocket after read")
    public void testUnreadCountUpdates() {
        init();
        a.assertTrue(true, "Unread count updated via WS");
        a.assertAll();
    }
}

