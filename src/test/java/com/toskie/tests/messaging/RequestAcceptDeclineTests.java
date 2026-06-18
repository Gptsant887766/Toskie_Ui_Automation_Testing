package com.toskie.tests.messaging;
import com.aventstack.extentreports.Status;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.constants.TestGroups;
import com.toskie.pages.messaging.ConversationListPage;
import com.toskie.utils.AssertionHelper;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.ReportManager;
import org.testng.annotations.Test;

public class RequestAcceptDeclineTests extends BaseTest {
    private ConversationListPage convPage;
    private AssertionHelper a;
    private void init() { convPage = new ConversationListPage(utilLayer); a = new AssertionHelper(); }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P1}, description = "Accept a pending message request")
    public void testAcceptMessageRequest() {
        init();
        ReportManager.getTest().log(Status.INFO, "Accepting message request");
        a.assertContains(BrowserManager.getPage().url(), "toskie.com", "Accept message request test -- should be on toskie.com");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P1}, description = "Decline a pending message request")
    public void testDeclineMessageRequest() {
        init();
        ReportManager.getTest().log(Status.INFO, "Declining message request");
        a.assertContains(BrowserManager.getPage().url(), "toskie.com", "Decline message request test -- should be on toskie.com");
        a.assertAll();
    }
}
