package com.ziplly.app.client.oauth;

import com.ziplly.app.client.ApplicationContext.Environment;



public class OAuthFactory {

	/**
	 * This method return the config object which knows how to connect to facebook.
	 * Depending on the environment, it'd set the redirect url.
	 * Currently set to PROD.
	 * 
	 * TODO(shaan): Set the config object based on the environment.
	 * @param provider
	 * @return
	 */
	public static OAuthConfig getAuthConfig(String provider, Environment environment) {
		// TODO when we add twitter/google+
		return OAuthConfig.newBuilder()
				.setProvider(OAuthProvider.FACEBOOK)
				.setClientId(OAuthAppProperties.FB_CLIENT_ID)
				.setKey(OAuthAppProperties.FB_CLIENT_SECRET)
				.setScope(OAuthAppProperties.FB_SCOPES)
//				.setRedirectUri(OAuthAppProperties.REDIRECT_URL_IN_PRODUCTION)
				.setEnvironment(environment)
				.build();
	}
}
