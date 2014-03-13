package com.ziplly.app.server.oauth;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gwt.json.client.JSONException;
import com.ziplly.app.client.oauth.AccessToken;
import com.ziplly.app.client.oauth.OAuthConfig;

public class FacebookAuthFlowManager extends OAuthFlowManager {
	private Logger logger = Logger.getLogger(FacebookAuthFlowManager.class.getCanonicalName());

	public FacebookAuthFlowManager(OAuthConfig config) {
		super(config);
	}

	@Override
	public Map<String, String> getRequestParams(String code) throws UnsupportedEncodingException {
		Map<String, String> paramsMap = new LinkedHashMap<String, String>();
		paramsMap.put("client_id", clientId);
		paramsMap.put("redirect_uri", redirectUri);
		paramsMap.put("client_secret", key);
		paramsMap.put("code", code);
		return paramsMap;
	}

	@Override
	public String doExchange(Map<String, String> params) throws IOException {
		String exchangeTokenUrl = getUrlWithParam(provider.getTokenUrl(), params);
		logger.info(String.format("OAuth Trying to connect to %s", exchangeTokenUrl));

		HttpURLConnection conn =
		    HTTPConnectionProvider.getConnection(exchangeTokenUrl, HTTPRequestType.GET);
		String response = getResponse(conn);
		return response;
	}

	@Override
	public AccessToken parseResponse(String response) throws JSONException {
		AccessToken at = new AccessToken();
		Pattern pattern = Pattern.compile("^access_token=([^&]+)&expires=(.*)");
		Matcher matcher = pattern.matcher(response);
		if (matcher.find()) {
			at.setAccess_token(matcher.group(1));
			at.setExpires_in(matcher.group(2));
		}
		return at;
	}

	// public static void main(String[] args) throws JSONException {
	// FacebookAuthFlowManager fm = new FacebookAuthFlowManager(new
	// OAuthConfig());
	// String s = "access_token=djksfdsdkljf&expires=5125091";
	// System.out.println(fm.parseResponse(s));
	// }
}
