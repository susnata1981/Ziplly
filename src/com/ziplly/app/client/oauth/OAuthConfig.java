package com.ziplly.app.client.oauth;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.ziplly.app.client.ApplicationContext.Environment;

@SuppressWarnings("serial")
public class OAuthConfig implements Serializable {
	private String[] scopes;
	private String redirectUri;
	private String clientId;
	private OAuthProvider provider;
	private String key;
	private Environment environment;

	private OAuthConfig(OAuthProvider provider, String clientId, String key, String[] scopes,
			String redirectUri, Environment environment) {
		this.setProvider(provider);
		this.setScopes(scopes);
		this.setRedirectUri(redirectUri);
		this.setClientId(clientId);
		this.setKey(key);
		this.environment = environment;
	}

	public String getAuthorizationUrl() throws UnsupportedEncodingException {
		Map<String, String> paramsMap = new HashMap<String, String>();
		String scope = Arrays.asList(getScopes()).toString().replaceAll(", ", " &amp; ")
				.replaceAll("^\\[|\\]$", "");
		paramsMap.put("scope", scope);
		paramsMap.put("client_id", getClientId());
		paramsMap.put("redirect_uri", getRedirectUri());
		paramsMap.put("response_type", "code");
		paramsMap.put("app_id", "217425525078759");
		return OAuthUtil.getUrlWithParam(getProvider().getAuthUrl(), paramsMap);
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public String[] getScopes() {
		return scopes;
	}

	public void setScopes(String[] scopes) {
		this.scopes = scopes;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	/**********************************************************************
	 * NO NEED TO CHANGE IN PRODUCTION
	 * 
	 * @return
	 * @throws UnsupportedEncodingException
	 **********************************************************************/
	public String getRedirectUri() throws UnsupportedEncodingException {
		// In DEVEL
		if (environment == Environment.DEVEL) {
			Map<String, String> paramsMap = new HashMap<String, String>();
			paramsMap.put("gwt.codesvr", "127.0.0.1:9997");
			return OAuthUtil.getUrlWithParam(OAuthAppProperties.REDIRECT_URL_IN_DEVELOPMENT, paramsMap);
		} else {
			// In PROD
			return OAuthAppProperties.REDIRECT_URL_IN_PRODUCTION;
		}
	}

	public void setRedirectUri(String redirectUri) {
		this.redirectUri = redirectUri;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public OAuthProvider getProvider() {
		return provider;
	}

	public void setProvider(OAuthProvider provider) {
		this.provider = provider;
	}

	public static class Builder {
		private String[] scopes;
		private String redirectUri;
		private String clientId;
		private OAuthProvider provider;
		private String key;
		private Environment environment;

		public OAuthConfig build() {
			return new OAuthConfig(provider, clientId, key, scopes, redirectUri, environment);
		}

		public Builder setScope(String[] scopes) {
			this.scopes = scopes;
			return this;
		}

		public Builder setRedirectUri(String redirectUri) {
			this.redirectUri = redirectUri;
			return this;
		}

		public Builder setClientId(String clientId) {
			this.clientId = clientId;
			return this;
		}

		public Builder setKey(String key) {
			this.key = key;
			return this;
		}

		public Builder setProvider(OAuthProvider provider) {
			this.provider = provider;
			return this;
		}

		public Environment getEnvironment() {
			return environment;
		}

		public Builder setEnvironment(Environment environment) {
			this.environment = environment;
			return this;
		}
	}
}
