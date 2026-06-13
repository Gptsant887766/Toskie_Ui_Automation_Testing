package com.toskie.pages.talent;

import com.aventstack.extentreports.Status;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.ReportManager;
import com.toskie.utils_Layer.UtilLayer;

public class TalentProfileAboutPage {

    private final UtilLayer<?> util;

    public TalentProfileAboutPage(UtilLayer<?> util) {
        this.util = util;
    }

    public String getBioText() {
        ReportManager.getTest().log(Status.INFO, "Getting bio text from talent about section");
        try {
            return BrowserManager.getPage()
                    .locator("[data-testid='bio-text'], .bio-text, .about-bio, p.bio")
                    .first().textContent().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public String getContactDetails() {
        ReportManager.getTest().log(Status.INFO, "Getting contact details from talent about section");
        try {
            return BrowserManager.getPage()
                    .locator("[data-testid='contact-details'], .contact-details, .about-contact")
                    .first().textContent().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public boolean isAboutLoaded() {
        try {
            return BrowserManager.getPage()
                    .locator("[data-testid='about-section'], .about-section, section.bio, .about-container")
                    .isVisible();
        } catch (Exception e) {
            return false;
        }
    }

    public String getContactEmail() {
        ReportManager.getTest().log(Status.INFO, "Getting contact email from talent about section");
        try {
            return BrowserManager.getPage()
                    .locator("[data-testid='contact-email'], .contact-email, a[href^='mailto']")
                    .first().textContent().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public String getContactPhone() {
        ReportManager.getTest().log(Status.INFO, "Getting contact phone from talent about section");
        try {
            return BrowserManager.getPage()
                    .locator("[data-testid='contact-phone'], .contact-phone, a[href^='tel']")
                    .first().textContent().trim();
        } catch (Exception e) {
            return "";
        }
    }
}
