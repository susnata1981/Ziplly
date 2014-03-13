package com.ziplly.app.client.oauth;

public enum OAuthProvider {
	GOOGLE(OAuthConfigConstants.GOOGLE_AUTH_URL, OAuthConfigConstants.GOOGLE_TOKEN_URL),
	FACEBOOK(OAuthConfigConstants.FB_AUTH_URL, OAuthConfigConstants.FB_TOKEN_URL);

	private String authUrl;
	private String tokenUrl;

	OAuthProvider(String authUrl, String tokenUrl) {
		this.setAuthUrl(authUrl);
		this.setTokenUrl(tokenUrl);
	}

	public String getAuthUrl() {
		return authUrl;
	}

	public void setAuthUrl(String authUrl) {
		this.authUrl = authUrl;
	}

	public String getTokenUrl() {
		return tokenUrl;
	}

	public void setTokenUrl(String tokenUrl) {
		this.tokenUrl = tokenUrl;
	}
}
