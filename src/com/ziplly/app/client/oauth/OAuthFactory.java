package com.ziplly.app.client.oauth;



public class OAuthFactory {

	public static OAuthConfig getAuthConfig(String provider) {
		// TODO when we add twitter/google+
		return OAuthConfig.newBuilder()
				.setProvider(OAuthProvider.FACEBOOK)
				.setClientId(OAuthAppProperties.FB_CLIENT_ID)
				.setKey(OAuthAppProperties.FB_CLIENT_SECRET)
				.setScope(OAuthAppProperties.FB_SCOPES)
				.setRedirectUri(OAuthAppProperties.REDIRECT_URL_IN_PRODUCTION)
				.build();
	}
}
