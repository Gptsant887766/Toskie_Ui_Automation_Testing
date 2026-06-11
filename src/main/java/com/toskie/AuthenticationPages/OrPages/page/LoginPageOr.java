package com.toskie.AuthenticationPages.OrPages.page;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class LoginPageOr {

	public Locator ClickOnLoginBT;

	public LoginPageOr(Page page) {

		ClickOnLoginBT = page.locator("//span[contains(text(),'Login')]");

	}
}
