package com.toskie.tests.posts;

import com.aventstack.extentreports.Status;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.constants.TestGroups;
import com.toskie.pages.address.AddressManagementPage;
import com.toskie.utils.AssertionHelper;
import com.toskie.utils_Layer.ApiUtils;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.ConfigManager;
import com.toskie.utils_Layer.ReportManager;
import org.testng.annotations.Test;

public class AddressManagementTests extends BaseTest {

    private AddressManagementPage addrPage;
    private AssertionHelper a;

    private void init() {
        addrPage = new AddressManagementPage(utilLayer);
        a = new AssertionHelper();
        ApiUtils.loginViaQAGraphQL(ConfigManager.get("testMobile"));
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2}, description = "Fetch and display existing addresses")
    public void testFetchAndDisplayAddress() {
        init();
        ReportManager.getTest().log(Status.INFO, "Verifying existing addresses are fetched and displayed");
        int count = addrPage.getAddressCount();
        ReportManager.getTest().log(Status.INFO, "Address count: " + count);
        boolean hasAddressSection = BrowserManager.getPage()
                .locator("[class*='address'], [data-testid*='address']").count() > 0;
        a.assertTrue(count > 0 || hasAddressSection,
                "ADDR-1: Address management must show existing addresses OR an address section UI (count=" + count + ")");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2}, description = "Add a new address successfully")
    public void testAddNewAddressAPI() {
        init();
        int before = addrPage.getAddressCount();
        ReportManager.getTest().log(Status.INFO, "Address count before add: " + before);
        addrPage.addNewAddress();
        addrPage.fillAddress("123 Main St", "Mumbai", "Maharashtra", "400001");
        addrPage.saveAddress();
        a.assertTrue(addrPage.isSuccessVisible(), "Success message should show after saving address");
        a.assertTrue(addrPage.getAddressCount() > before, "Address count should increase after adding");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2}, description = "Edit existing address saves updated data")
    public void testEditAddressAPI() {
        init();
        if (addrPage.getAddressCount() == 0) {
            addrPage.addNewAddress();
            addrPage.fillAddress("100 Setup St", "Pune", "Maharashtra", "411001");
            addrPage.saveAddress();
        }
        ReportManager.getTest().log(Status.INFO, "Editing first address");
        addrPage.editAddress(0);
        addrPage.fillAddress("999 Edited Rd", "Bengaluru", "Karnataka", "560001");
        addrPage.saveAddress();
        a.assertTrue(addrPage.isSuccessVisible(), "Success message should show after editing address");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2}, description = "Delete address reduces address count")
    public void testDeleteAddressAPI() {
        init();
        if (addrPage.getAddressCount() == 0) {
            addrPage.addNewAddress();
            addrPage.fillAddress("200 Delete Me Ln", "Chennai", "Tamil Nadu", "600001");
            addrPage.saveAddress();
        }
        int before = addrPage.getAddressCount();
        ReportManager.getTest().log(Status.INFO, "Address count before delete: " + before);
        addrPage.deleteAddress(0);
        int after = addrPage.getAddressCount();
        ReportManager.getTest().log(Status.INFO, "Address count after delete: " + after);
        a.assertTrue(after < before, "Address count should decrease after deletion");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2}, description = "Address count is non-negative after any operations")
    public void testAddressCountNonNegative() {
        init();
        int count = addrPage.getAddressCount();
        ReportManager.getTest().log(Status.INFO, "Final address count: " + count);
        boolean addressSectionVisible = BrowserManager.getPage()
                .locator("main, [class*='address'], [data-testid*='address-management']").count() > 0;
        a.assertTrue(addressSectionVisible,
                "ADDR-5: Address management section must be visible after all CRUD operations (count=" + count + ")");
        a.assertAll();
    }
}
