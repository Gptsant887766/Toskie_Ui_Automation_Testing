package com.toskie.AuthenticationPages.OrPages.page;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class WelcomeToToskieLandingPageOr {


	public Locator NextBT;
	public Locator StartExploring;
	public Locator CreateMYProfile;
	
	public WelcomeToToskieLandingPageOr(Page page) {
        
		NextBT = page.locator("//span[contains(text(),'Next')]");
		StartExploring = page.locator("//span[contains(text(),'Start Exploring')]");
		CreateMYProfile    = page.locator("//span[contains(text(),'Create My Profile')]");
		
	}
}
