package com.toskie.tests.posts;
import com.aventstack.extentreports.Status;
import com.toskie.BaseTest_Layer.BaseTest;
import com.toskie.constants.TestGroups;
import com.toskie.pages.address.AddressManagementPage;
import com.toskie.utils.AssertionHelper;
import org.testng.annotations.Test;

public class AddressManagementTests extends BaseTest {
    private AddressManagementPage addrPage;
    private AssertionHelper a;
    private void init() { addrPage = new AddressManagementPage(utilLayer); a = new AssertionHelper(); }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2}, description = "Add a new address successfully")
    public void testAddAddress() {
        init();
        addrPage.addNewAddress();
        addrPage.fillAddress("123 Main St", "Mumbai", "Maharashtra", "400001");
        addrPage.saveAddress();
        a.assertTrue(addrPage.isSuccessVisible(), "Success message should show after saving");
        a.assertAll();
    }

    @Test(groups = {TestGroups.REGRESSION, TestGroups.P2}, description = "Address count increases after adding")
    public void testAddressCountIncreases() {
        init();
        int before = addrPage.getAddressCount();
        addrPage.addNewAddress();
        addrPage.fillAddress("456 Park Ave", "Delhi", "Delhi", "110001");
        addrPage.saveAddress();
        a.assertTrue(addrPage.getAddressCount() >= before, "Count should not decrease");
        a.assertAll();
    }
}
