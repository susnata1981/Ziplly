package com.ziplly.app.client.widget;

public enum AccountDetailsType {
    EMAIL ("Email", true, true, true), 
    OCCUPATION ("Occupation", true, true, true),
    TWEETS ("Tweets", true, true, false);
    
    private String section;
	private boolean allowedPublic;
	private boolean allowedCommunity;
	private boolean allowedPrivate;
    
	private AccountDetailsType(String section, boolean allowedPublic, boolean allowedCommunity, boolean allowedPrivate) {
    	this.setSection(section);
    	this.setAllowedPublic(allowedPublic);
    	this.setAllowedCommunity(allowedCommunity);
    	this.allowedPrivate = allowedPrivate;
	}
	
	public String getSection() {
		return section;
	}
	public void setSection(String section) {
		this.section = section;
	}

	public boolean isAllowedPrivate() {
		return allowedPrivate;
	}

	public void setAllowedPrivate(boolean allowedPrivate) {
		this.allowedPrivate = allowedPrivate;
	}

	public boolean isAllowedPublic() {
		return allowedPublic;
	}

	public void setAllowedPublic(boolean allowedPublic) {
		this.allowedPublic = allowedPublic;
	}

	public boolean isAllowedCommunity() {
		return allowedCommunity;
	}

	public void setAllowedCommunity(boolean allowedCommunity) {
		this.allowedCommunity = allowedCommunity;
	}
}