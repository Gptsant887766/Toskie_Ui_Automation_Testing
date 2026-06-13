package com.toskie.tests.messaging;
import com.aventstack.extentreports.Status;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.constants.TestGroups;
import com.toskie.pages.messaging.MessageRequestDrawer;
import com.toskie.utils.AssertionHelper;
import com.toskie.utils_Layer.ReportManager;
import org.testng.annotations.Test;

public class MessageRequestTests extends BaseTest {
    private MessageRequestDrawer drawer;
    private AssertionHelper a;
    private void init() { drawer = new MessageRequestDrawer(utilLayer); a = new AssertionHelper(); }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P1}, description = "Message request drawer opens")
    public void testRequestDrawerOpens() {
        init();
        ReportManager.getTest().log(Status.INFO, "Opening message request drawer");
        drawer.openDrawer();
        a.assertTrue(drawer.isDrawerOpen(), "Drawer should be open");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2}, description = "Message request can be sent to a talent")
    public void testSendMessageRequest() {
        init();
        drawer.openDrawer();
        drawer.sendRequest("Hello, I would like to connect!");
        a.assertTrue(drawer.isRequestSent(), "Message request should be sent successfully");
        a.assertAll();
    }
}

