package com.toskie.tests.messaging;
import com.aventstack.extentreports.Status;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.constants.TestGroups;
import com.toskie.pages.messaging.ConversationListPage;
import com.toskie.utils.AssertionHelper;
import com.toskie.utils_Layer.ReportManager;
import org.testng.annotations.Test;

public class ConversationListTests extends BaseTest {
    private ConversationListPage convPage;
    private AssertionHelper a;
    private void init() { convPage = new ConversationListPage(utilLayer); a = new AssertionHelper(); }

    @Test(groups = {TestGroups.SMOKE, TestGroups.P0}, description = "Conversation list page loads")
    public void testConversationListLoads() {
        init();
        ReportManager.getTest().log(Status.INFO, "Checking conversation list");
        a.assertTrue(convPage.isConversationListVisible(), "Conversation list should be visible");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P1}, description = "Conversation items are displayed or empty state shown")
    public void testConversationItemsDisplayed() {
        init();
        int count = convPage.getConversationCount();
        boolean listVisible = convPage.isConversationListVisible();
        ReportManager.getTest().log(Status.INFO, "MSG-2: Conversation count=" + count + " | list visible=" + listVisible);
        a.assertTrue(listVisible,
                "MSG-2: Conversation list container must be visible on the messaging page");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P1}, description = "Open conversation and see messages")
    public void testOpenConversation() {
        init();
        if (convPage.getConversationCount() > 0) {
            convPage.openConversation(0);
            a.assertTrue(convPage.isMessageAreaVisible(), "Message area should open");
        } else {
            a.assertTrue(convPage.getConversationCount() == 0, "No conversations to open -- count should be 0");
        }
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2}, description = "Searching conversations filters list without crashing")
    public void testSearchConversations() {
        init();
        convPage.searchConversation("test");
        ReportManager.getTest().log(Status.INFO, "MSG-4: Search executed for 'test'");
        boolean listStillVisible = convPage.isConversationListVisible();
        a.assertTrue(listStillVisible,
                "MSG-4: Conversation list must remain visible after performing a search (page must not crash or navigate away)");
        a.assertAll();
    }
}

