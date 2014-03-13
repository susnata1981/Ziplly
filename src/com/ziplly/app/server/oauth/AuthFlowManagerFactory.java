package com.ziplly.app.server.oauth;

import com.ziplly.app.client.oauth.OAuthConfig;

public class AuthFlowManagerFactory {

	public static OAuthFlowManager get(OAuthConfig config) {
		switch (config.getProvider()) {
			case GOOGLE:
				return new GoogleAuthFlowManager(config);
			case FACEBOOK:
			default:
				return new FacebookAuthFlowManager(config);
		}
	}
}
