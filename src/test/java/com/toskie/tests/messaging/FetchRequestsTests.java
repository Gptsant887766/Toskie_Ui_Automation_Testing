package com.toskie.tests.messaging;
import com.aventstack.extentreports.Status;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.constants.TestGroups;
import com.toskie.pages.messaging.ConversationListPage;
import com.toskie.utils.AssertionHelper;
import com.toskie.utils_Layer.ReportManager;
import org.testng.annotations.Test;

public class FetchRequestsTests extends BaseTest {
    private ConversationListPage convPage;
    private AssertionHelper a;
    private void init() { convPage = new ConversationListPage(utilLayer); a = new AssertionHelper(); }

    @Test(groups = {TestGroups.API, TestGroups.P1}, description = "Message requests are fetched and displayed")
    public void testFetchMessageRequests() {
        init();
        ReportManager.getTest().log(Status.INFO, "Fetching message requests");
        boolean listVisible = convPage.isConversationListVisible();
        ReportManager.getTest().log(Status.INFO, "MSG-REQ-1: Conversation/request list visible: " + listVisible);
        a.assertTrue(listVisible,
                "MSG-REQ-1: Message requests page must show the conversation/requests list after login");
        a.assertAll();
    }
}

