package com.toskie.pages.talent;

import com.aventstack.extentreports.Status;
import com.toskie.locators.TalentProfileLocators;
import com.toskie.utils_Layer.BrowserManager;
import com.toskie.utils_Layer.ReportManager;
import com.toskie.utils_Layer.UtilLayer;

import java.util.ArrayList;
import java.util.List;

public class TalentProfileInfoPage {

    private final UtilLayer<?> util;
    private final TalentProfileLocators loc;

    public TalentProfileInfoPage(UtilLayer<?> util) {
        this.util = util;
        this.loc  = new TalentProfileLocators(BrowserManager.getPage());
    }

    public boolean isProfileLoaded() {
        try {
            return loc.profileName.isVisible();
        } catch (Exception e) {
            return false;
        }
    }

    public String getProfileName() {
        ReportManager.getTest().log(Status.INFO, "Getting talent profile name");
        try {
            return loc.profileName.textContent().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public String getProfileImage() {
        ReportManager.getTest().log(Status.INFO, "Getting talent profile image src");
        try {
            return loc.profileImage.getAttribute("src");
        } catch (Exception e) {
            return "";
        }
    }

    public List<String> getSkillTags() {
        ReportManager.getTest().log(Status.INFO, "Getting skill tags from talent profile");
        List<String> tags = new ArrayList<>();
        try {
            int count = loc.skillsTags.count();
            for (int i = 0; i < count; i++) {
                String text = loc.skillsTags.nth(i).textContent();
                if (text != null) tags.add(text.trim());
            }
        } catch (Exception e) {
            ReportManager.getTest().log(Status.WARNING, "Get skill tags issue: " + e.getMessage());
        }
        return tags;
    }

    public boolean isGallerySectionVisible() {
        try {
            return loc.gallerySection.isVisible();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isExperienceSectionVisible() {
        try {
            return loc.experienceSection.isVisible();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isQualificationSectionVisible() {
        try {
            return loc.qualificationSection.isVisible();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isAboutSectionVisible() {
        try {
            return loc.aboutSection.isVisible();
        } catch (Exception e) {
            return false;
        }
    }

    public void clickLike() {
        ReportManager.getTest().log(Status.INFO, "Clicking Like on talent profile");
        util.click(loc.likeBtn, "Like Button");
        BrowserManager.getPage().waitForTimeout(500);
    }

    public void clickBookmark() {
        ReportManager.getTest().log(Status.INFO, "Clicking Bookmark on talent profile");
        util.click(loc.bookmarkBtn, "Bookmark Button");
        BrowserManager.getPage().waitForTimeout(500);
    }

    public void clickShare() {
        ReportManager.getTest().log(Status.INFO, "Clicking Share on talent profile");
        util.click(loc.shareBtn, "Share Button");
        BrowserManager.getPage().waitForTimeout(500);
    }

    public void clickMessage() {
        ReportManager.getTest().log(Status.INFO, "Clicking Message on talent profile");
        util.click(loc.messageBtn, "Message Button");
        BrowserManager.getPage().waitForTimeout(500);
    }

    public String getRating() {
        ReportManager.getTest().log(Status.INFO, "Getting talent rating");
        try {
            return loc.ratingDisplay.textContent().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public String getLocation() {
        ReportManager.getTest().log(Status.INFO, "Getting talent location");
        try {
            return loc.locationDisplay.textContent().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public boolean isContactVisible() {
        try {
            return BrowserManager.getPage()
                    .locator("[data-testid='contact-info'], .contact-info, .talent-contact")
                    .isVisible();
        } catch (Exception e) {
            return false;
        }
    }

    public String getLikeCount() {
        ReportManager.getTest().log(Status.INFO, "Getting like count");
        try {
            return BrowserManager.getPage()
                    .locator("[data-testid='like-count'], .like-count, span.likes")
                    .first().textContent().trim();
        } catch (Exception e) {
            return "";
        }
    }
}
