package com.toskie.AuthenticationPages.OrPages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class ToskieCreateProfileOr {

        public Locator FirstName;
        public Locator LastName;
        public Locator EmailField;
        public Locator SendOTPButton;
        public Locator ClickOnGenderBT; // selects MALE
        public Locator ClickOnFemaleGenderBT; // selects FEMALE
        public Locator ClickOnOtherGenderBT; // selects OTHER
        public Locator DOBButton;
        public Locator TermCondionCheckBX;
        public Locator CreateMYProfile;
        public Locator YearTX;
        public Locator YearBTX;
        public Locator YearBackBTX;
        public Locator YearselectBT;
        public Locator MonthTX;
        public Locator DateBTX;
        public Locator SaveBTX;

        public ToskieCreateProfileOr(Page page) {

                FirstName = page.locator(
                                "//input[@placeholder='Enter First Name']");

                LastName = page.locator(
                                "//input[@placeholder='Enter Last Name']");

                EmailField = page.locator(
                                "//input[@placeholder='Enter Email ID']");

                SendOTPButton = page.locator(
                                "//button[normalize-space()='Send OTP']");

                ClickOnGenderBT = page.locator(
                                "//button[@id='MALE']");

                ClickOnFemaleGenderBT = page.locator(
                                "//button[@id='FEMALE']");

                ClickOnOtherGenderBT = page.locator(
                                "//button[@id='OTHER']");

                DOBButton = page.locator(
                                "//button[@class=\"inline-flex items-center gap-2 whitespace-nowrap rounded-md text-sm font-medium transition-all disabled:pointer-events-none disabled:opacity-50 [&_svg]:pointer-events-none [&_svg:not([class*='size-'])]:size-4 shrink-0 [&_svg]:shrink-0 outline-none focus-visible:border-ring focus-visible:ring-ring/50 focus-visible:ring-[3px] aria-invalid:ring-destructive/20 dark:aria-invalid:ring-destructive/40 aria-invalid:border-destructive bg-background hover:bg-accent hover:text-accent-foreground dark:bg-input/30 dark:border-input dark:hover:bg-input/50 px-4 py-2 has-[>svg]:px-3 body-2 h-12 w-full justify-between border text-left shadow-none lg:h-14 xl:h-12 text-muted-foreground border-text-disabled-text\"]");

                YearTX = page.locator(
                                "//button[@class='heading-5 text-text-text cursor-pointer rounded px-2 py-1 transition-colors']");

                YearBTX = page.locator(
                                "//button[contains(text(),'" + java.time.Year.now().getValue() + "')]");

                YearBackBTX = page.locator(
                                "//button[@class=\"inline-flex items-center justify-center gap-2 whitespace-nowrap rounded-md text-sm font-medium transition-all disabled:pointer-events-none disabled:opacity-50 [&_svg]:pointer-events-none [&_svg:not([class*='size-'])]:size-4 shrink-0 [&_svg]:shrink-0 outline-none focus-visible:border-ring focus-visible:ring-ring/50 focus-visible:ring-[3px] aria-invalid:ring-destructive/20 dark:aria-invalid:ring-destructive/40 aria-invalid:border-destructive hover:bg-accent hover:text-accent-foreground dark:hover:bg-accent/50 size-9 h-3 w-1.5 cursor-pointer shadow-none\"]");

                YearselectBT = page.locator(
                                "//button[contains(text(),'1997')]");

                MonthTX = page.locator(
                                "//button[contains(text(),'May')]");
                DateBTX = page.locator(
                                "//button[contains(text(),'25')]");

                SaveBTX = page.locator(
                                "//span[contains(text(),'Save')]");

                TermCondionCheckBX = page.locator(
                                "//button[@id='terms-checkbox']");

                CreateMYProfile = page.locator(
                                "//button[@class=\"inline-flex items-center justify-center gap-2 whitespace-nowrap text-sm font-medium disabled:opacity-50 [&_svg]:pointer-events-none [&_svg:not([class*='size-'])]:size-4 shrink-0 [&_svg]:shrink-0 outline-none focus-visible:border-ring focus-visible:ring-ring/50 aria-invalid:ring-destructive/20 dark:aria-invalid:ring-destructive/40 aria-invalid:border-destructive has-[>svg]:px-3 cursor-pointer ring-0 transition-colors focus:ring-0 focus:outline-none focus-visible:ring-0 disabled:pointer-events-none disabled:select-none max-w-[400px] bg-text-primary hover:bg-text-primary text-text-light h-12 w-full px-6 py-3 rounded-md\"]");
        }
}