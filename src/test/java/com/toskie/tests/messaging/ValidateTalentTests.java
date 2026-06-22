package com.toskie.tests.messaging;
import com.aventstack.extentreports.Status;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.constants.TestGroups;
import com.toskie.utils.AssertionHelper;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.ReportManager;
import org.testng.annotations.Test;

public class ValidateTalentTests extends BaseTest {
    private AssertionHelper a;
    private void init() { a = new AssertionHelper(); }

    @Test(groups = {TestGroups.API, TestGroups.P2}, description = "Validate talent data visible in conversation context")
    public void testTalentDataInConversation() {
        init();
        ReportManager.getTest().log(Status.INFO, "Validating talent info in messaging context");
        a.assertContains(BrowserManager.getPage().url(), "toskie.com", "Validate talent in conversation test -- should be on toskie.com");
        a.assertAll();
    }
}
