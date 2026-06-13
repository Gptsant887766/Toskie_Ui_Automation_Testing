package com.toskie.tests.dashboard;
import com.aventstack.extentreports.Status;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.constants.TestGroups;
import com.toskie.pages.dashboard.DashboardPage;
import com.toskie.utils.AssertionHelper;
import org.testng.annotations.Test;

public class OpenToConnectTests extends BaseTest {
    private DashboardPage dash;
    private AssertionHelper a;
    private void init() { dash = new DashboardPage(utilLayer); a = new AssertionHelper(); }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P1}, description = "Toggle Open to Connect ON")
    public void testEnableOpenToConnect() {
        init(); dash.toggleOpenToConnect();
        a.assertTrue(dash.isOpenToConnectEnabled(), "Should be enabled after toggle");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P1}, description = "Toggle Open to Connect OFF")
    public void testDisableOpenToConnect() {
        init();
        if (dash.isOpenToConnectEnabled()) dash.toggleOpenToConnect();
        a.assertFalse(dash.isOpenToConnectEnabled(), "Should be disabled");
        a.assertAll();
    }
}
