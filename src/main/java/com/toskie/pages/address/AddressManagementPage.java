package com.toskie.pages.address;
import com.aventstack.extentreports.Status;
import com.microsoft.playwright.*;
import com.toskie.utils_Layer.*;

public class AddressManagementPage {
    private final UtilLayer<?> util;
    private final Locator streetInput, cityInput, stateInput, countryDropdown, pinInput,
            saveBtn, editBtn, deleteBtn, addressCards, addNewBtn, successMsg, errorMsg, mapPin;

    public AddressManagementPage(UtilLayer<?> util) {
        this.util = util;
        Page page = BrowserManager.getPage();
        streetInput    = page.locator("input[name*='street' i], textarea[name*='address' i]").first();
        cityInput      = page.locator("input[name='city'], input[placeholder*='City' i]").first();
        stateInput     = page.locator("input[name='state'], input[placeholder*='State' i]").first();
        countryDropdown= page.locator("select[name='country'], [class*='country-select']").first();
        pinInput       = page.locator("input[name*='pin' i], input[name*='zip' i], input[placeholder*='PIN' i]").first();
        saveBtn        = page.locator("//button[normalize-space()='Save'] | //button[contains(.,'Save Address')]").first();
        editBtn        = page.locator("//button[contains(.,'Edit')]").first();
        deleteBtn      = page.locator("//button[contains(.,'Delete')]").first();
        addressCards   = page.locator("[class*='address-card'], [class*='address-item']");
        addNewBtn      = page.locator("//button[contains(.,'Add Address')] | //button[contains(.,'Add New')]").first();
        successMsg     = page.locator("[class*='success'], [class*='alert-success']").first();
        errorMsg       = page.locator("[class*='error'], [class*='alert-error']").first();
        mapPin         = page.locator("[class*='map-pin'], [class*='marker']").first();
    }

    public void fillAddress(String street, String city, String state, String pin) {
        util.fill(streetInput, street, "Street"); util.fill(cityInput, city, "City");
        util.fill(stateInput, state, "State"); util.fill(pinInput, pin, "PIN");
    }
    public void saveAddress()         { util.click(saveBtn, "Save Address"); }
    public void addNewAddress()       { util.click(addNewBtn, "Add New Address"); }
    public boolean isSuccessVisible() { try { return successMsg.isVisible(); } catch (Exception e) { return false; } }
    public int getAddressCount()      { return (int) addressCards.count(); }
    public void editAddress(int idx)  { addressCards.nth(idx).locator("//button[contains(.,'Edit')]").click(); }
    public void deleteAddress(int idx){ addressCards.nth(idx).locator("//button[contains(.,'Delete')]").click(); }
}
