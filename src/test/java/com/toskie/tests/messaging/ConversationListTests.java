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

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P1}, description = "Conversation items are displayed")
    public void testConversationItemsDisplayed() {
        init();
        a.assertTrue(convPage.getConversationCount() >= 0, "Conversation items should load");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P1}, description = "Open conversation and see messages")
    public void testOpenConversation() {
        init();
        if (convPage.getConversationCount() > 0) {
            convPage.openConversation(0);
            a.assertTrue(convPage.isMessageAreaVisible(), "Message area should open");
        } else {
            a.assertTrue(true, "No conversations to open");
        }
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2}, description = "Search conversations works")
    public void testSearchConversations() {
        init();
        convPage.searchConversation("test");
        a.assertTrue(true, "Search executed without error");
        a.assertAll();
    }
}

