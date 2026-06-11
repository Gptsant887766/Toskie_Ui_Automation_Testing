package com.toskie.locators;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class BookingLocators {

    // ─── Entry ────────────────────────────────────────────────────────────────
    public final Locator bookNowButton;
    public final Locator bookingModal;
    public final Locator bookingForm;
    public final Locator bookingPageHeader;

    // ─── Date Selection ───────────────────────────────────────────────────────
    public final Locator datePickerButton;
    public final Locator calendarContainer;
    public final Locator calendarNextMonth;
    public final Locator calendarPrevMonth;
    public final Locator availableDates;
    public final Locator selectedDate;
    public final Locator pastDates; // should be disabled

    // ─── Time Slots ───────────────────────────────────────────────────────────
    public final Locator timeSlotContainer;
    public final Locator availableTimeSlots;
    public final Locator selectedTimeSlot;
    public final Locator unavailableTimeSlots;

    // ─── Booking Details ──────────────────────────────────────────────────────
    public final Locator serviceDropdown;
    public final Locator notesField;
    public final Locator addressField;
    public final Locator contactField;
    public final Locator bookingSummary;
    public final Locator serviceName;
    public final Locator servicePrice;

    // ─── Actions ──────────────────────────────────────────────────────────────
    public final Locator confirmBookingButton;
    public final Locator cancelBookingButton;
    public final Locator editBookingButton;

    // ─── Confirmation ─────────────────────────────────────────────────────────
    public final Locator bookingConfirmationScreen;
    public final Locator bookingIdText;
    public final Locator bookingConfirmMessage;
    public final Locator viewBookingButton;
    public final Locator goHomeAfterBooking;

    // ─── My Bookings ──────────────────────────────────────────────────────────
    public final Locator myBookingsSection;
    public final Locator bookingCards;
    public final Locator firstBookingCard;
    public final Locator bookingStatusBadge;
    public final Locator bookingDateText;
    public final Locator emptyBookingsMessage;
    public final Locator cancelBookingFromList;
    public final Locator rescheduleButton;

    // ─── Validation ───────────────────────────────────────────────────────────
    public final Locator dateRequiredError;
    public final Locator timeRequiredError;
    public final Locator generalBookingError;

    public BookingLocators(Page page) {
        bookNowButton   = page.locator("button:has-text('Book Now'), button:has-text('Book'), [class*='book-btn']");
        bookingModal    = page.locator("[class*='booking-modal'], [class*='booking-drawer'], [role='dialog']:has-text('Book')");
        bookingForm     = page.locator("[class*='booking-form'], form[class*='booking']");
        bookingPageHeader = page.locator("h1:has-text('Book'), h2:has-text('Booking'), [class*='booking-header']");

        datePickerButton  = page.locator("button:has-text('Select Date'), [class*='date-picker-btn'], [placeholder*='Select date']");
        calendarContainer = page.locator("[class*='calendar'], [class*='date-picker'], [role='grid']");
        calendarNextMonth = page.locator("[aria-label='Go to next month'], button[class*='next-month'], [class*='cal-next']");
        calendarPrevMonth = page.locator("[aria-label='Go to previous month'], button[class*='prev-month']");
        availableDates    = page.locator("[class*='calendar'] button:not([disabled]):not([class*='disabled'])");
        selectedDate      = page.locator("[class*='calendar'] [aria-selected='true'], [class*='selected-date']");
        pastDates         = page.locator("[class*='calendar'] button[disabled], [class*='past-date']");

        timeSlotContainer     = page.locator("[class*='time-slots'], [class*='time-slot-list']");
        availableTimeSlots    = page.locator("[class*='time-slot']:not([disabled]):not([class*='unavailable'])");
        selectedTimeSlot      = page.locator("[class*='time-slot'][class*='selected'], [class*='time-slot'][aria-selected='true']");
        unavailableTimeSlots  = page.locator("[class*='time-slot'][class*='unavailable'], [class*='time-slot'][disabled]");

        serviceDropdown  = page.locator("select[name*='service'], [class*='service-select'], [aria-label*='service' i]");
        notesField       = page.locator("textarea[placeholder*='note' i], textarea[placeholder*='requirement' i], [class*='booking-notes']");
        addressField     = page.locator("input[placeholder*='address' i], [class*='service-address']");
        contactField     = page.locator("input[placeholder*='contact' i], [class*='contact-number']");
        bookingSummary   = page.locator("[class*='booking-summary'], [class*='order-summary']");
        serviceName      = page.locator("[class*='booking-summary'] [class*='service-name']");
        servicePrice     = page.locator("[class*='booking-summary'] [class*='price']");

        confirmBookingButton = page.locator("button:has-text('Confirm'), button:has-text('Confirm Booking'), [class*='confirm-booking']");
        cancelBookingButton  = page.locator("button:has-text('Cancel'), [class*='cancel-booking-btn']");
        editBookingButton    = page.locator("button:has-text('Edit'), [class*='edit-booking']");

        bookingConfirmationScreen = page.locator("[class*='booking-confirmed'], [class*='confirmation-screen']");
        bookingIdText             = page.locator("[class*='booking-id'], span:has-text('Booking #')");
        bookingConfirmMessage     = page.locator("[class*='success-message']:has-text('Booking'), p:has-text('confirmed')");
        viewBookingButton         = page.locator("button:has-text('View Booking'), [class*='view-booking']");
        goHomeAfterBooking        = page.locator("button:has-text('Go Home'), button:has-text('Back to Home')");

        myBookingsSection   = page.locator("[class*='my-bookings'], [href*='bookings'], [class*='bookings-list']");
        bookingCards        = page.locator("[class*='booking-card'], [class*='booking-item']");
        firstBookingCard    = bookingCards.first();
        bookingStatusBadge  = page.locator("[class*='booking-card'] [class*='status-badge'], [class*='booking-status']");
        bookingDateText     = page.locator("[class*='booking-card'] [class*='booking-date']");
        emptyBookingsMessage= page.locator("[class*='no-bookings'], p:has-text('No bookings'), p:has-text('no upcoming')");
        cancelBookingFromList = page.locator("[class*='booking-card'] button:has-text('Cancel')");
        rescheduleButton    = page.locator("[class*='booking-card'] button:has-text('Reschedule')");

        dateRequiredError    = page.locator("[class*='error']:has-text('date'), [class*='date-error']");
        timeRequiredError    = page.locator("[class*='error']:has-text('time'), [class*='time-error']");
        generalBookingError  = page.locator("[class*='booking-error'], [role='alert']:has-text('booking')");
    }
}
