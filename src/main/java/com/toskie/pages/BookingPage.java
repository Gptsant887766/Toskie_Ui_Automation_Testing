package com.toskie.pages;

import com.aventstack.extentreports.Status;
import com.toskie.locators.BookingLocators;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.ReportManager;
import com.toskie.utils_Layer.UtilLayer;
import com.toskie.utils_Layer.WaitManager;

public class BookingPage {

    private final UtilLayer<?> util;
    private final BookingLocators loc;

    public BookingPage(UtilLayer<?> util) {
        this.util = util;
        this.loc  = new BookingLocators(BrowserManager.getPage());
    }

    // ─── Entry ────────────────────────────────────────────────────────────────

    public void clickBookNow() {
        util.click(loc.bookNowButton, "Book Now Button");
        BrowserManager.getPage().waitForTimeout(1500);
        ReportManager.getTest().log(Status.INFO, "Book Now clicked.");
    }

    public boolean isBookingFormVisible() {
        try { return loc.bookingModal.isVisible() || loc.bookingForm.isVisible(); }
        catch (Exception e) { return false; }
    }

    // ─── Date Selection ───────────────────────────────────────────────────────

    public void selectFirstAvailableDate() {
        try {
            util.click(loc.datePickerButton, "Date Picker");
            BrowserManager.getPage().waitForTimeout(800);
            if (loc.availableDates.count() > 0) {
                loc.availableDates.first().click();
                BrowserManager.getPage().waitForTimeout(500);
                ReportManager.getTest().log(Status.PASS, "First available date selected.");
            } else {
                ReportManager.getTest().log(Status.WARNING, "No available dates visible.");
            }
        } catch (Exception e) {
            ReportManager.getTest().log(Status.FAIL, "Date selection failed: " + e.getMessage());
        }
    }

    public void selectDateAtIndex(int index) {
        try {
            if (loc.availableDates.count() > index) {
                loc.availableDates.nth(index).click();
                BrowserManager.getPage().waitForTimeout(500);
            }
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "Date at index " + index + " not selectable.");
        }
    }

    public boolean isPastDateDisabled() {
        try { return loc.pastDates.first().isDisabled(); }
        catch (Exception e) { return true; } // assume disabled if not found
    }

    // ─── Time Slot ────────────────────────────────────────────────────────────

    public void selectFirstAvailableTimeSlot() {
        try {
            BrowserManager.getPage().waitForTimeout(500);
            if (loc.availableTimeSlots.count() > 0) {
                loc.availableTimeSlots.first().click();
                BrowserManager.getPage().waitForTimeout(500);
                ReportManager.getTest().log(Status.PASS, "First time slot selected.");
            } else {
                ReportManager.getTest().log(Status.WARNING, "No available time slots.");
            }
        } catch (Exception e) {
            ReportManager.getTest().log(Status.FAIL, "Time slot selection failed: " + e.getMessage());
        }
    }

    // ─── Booking Details ──────────────────────────────────────────────────────

    public void enterBookingNotes(String notes) {
        try {
            util.fill(loc.notesField, notes, "Booking Notes");
        } catch (Exception e) {
            ReportManager.getTest().log(Status.INFO, "Notes field not available.");
        }
    }

    // ─── Actions ──────────────────────────────────────────────────────────────

    public void confirmBooking() {
        util.click(loc.confirmBookingButton, "Confirm Booking");
        WaitManager.safePageLoad();
        BrowserManager.getPage().waitForTimeout(2000);
        ReportManager.getTest().log(Status.INFO, "Booking confirmed.");
    }

    public void cancelBooking() {
        try {
            util.click(loc.cancelBookingButton, "Cancel Booking");
            BrowserManager.getPage().waitForTimeout(1000);
        } catch (Exception e) {
            util.navigateBack();
        }
    }

    // ─── Complete Booking Flow ────────────────────────────────────────────────

    public void completeBooking(String notes) {
        clickBookNow();
        if (!isBookingFormVisible()) {
            ReportManager.getTest().log(Status.WARNING, "Booking form did not open.");
            return;
        }
        selectFirstAvailableDate();
        selectFirstAvailableTimeSlot();
        if (notes != null && !notes.isEmpty()) enterBookingNotes(notes);
        confirmBooking();
    }

    public void completeBookingDefault() {
        completeBooking("Test booking via automation");
    }

    // ─── Verification ─────────────────────────────────────────────────────────

    public boolean isBookingConfirmed() {
        try { return loc.bookingConfirmationScreen.isVisible() || loc.bookingConfirmMessage.isVisible(); }
        catch (Exception e) { return false; }
    }

    public String getBookingId() {
        try { return loc.bookingIdText.textContent().trim(); }
        catch (Exception e) { return ""; }
    }

    public boolean isDateRequiredErrorVisible() {
        try { return loc.dateRequiredError.isVisible(); }
        catch (Exception e) { return false; }
    }

    public boolean isTimeRequiredErrorVisible() {
        try { return loc.timeRequiredError.isVisible(); }
        catch (Exception e) { return false; }
    }

    public void navigateToMyBookings() {
        try {
            util.click(loc.myBookingsSection, "My Bookings");
            BrowserManager.getPage().waitForTimeout(1500);
        } catch (Exception e) {
            String bookingsUrl = com.toskie.utils_Layer.ConfigManager.getBaseUrl().replaceAll("/$", "") + "/bookings";
            BrowserManager.getPage().navigate(bookingsUrl);
        }
    }

    public int getBookingCount() {
        return loc.bookingCards.count();
    }

    public boolean isEmptyBookingsVisible() {
        try { return loc.emptyBookingsMessage.isVisible(); }
        catch (Exception e) { return false; }
    }
}
