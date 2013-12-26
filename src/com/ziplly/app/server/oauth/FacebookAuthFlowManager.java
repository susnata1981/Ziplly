package com.ziplly.app.server.oauth;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gwt.json.client.JSONException;
import com.ziplly.app.client.oauth.AccessToken;
import com.ziplly.app.client.oauth.OAuthConfig;

public class FacebookAuthFlowManager extends OAuthFlowManager {

	public FacebookAuthFlowManager(OAuthConfig config) {
		super(config);
	}

	@Override
	public Map<String,String> getRequestParams(String code)
			throws UnsupportedEncodingException {
		Map<String,String> paramsMap = new HashMap<String,String>();
		paramsMap.put("code",code);
		paramsMap.put("client_id",clientId);
		paramsMap.put("client_secret",key);
		paramsMap.put("redirect_uri",redirectUri);
		return paramsMap;
	}

	@Override
	public String doExchange(Map<String,String> params) throws IOException {
		String exchangeTokenUrl = getUrlWithParam(provider.getTokenUrl(), params);
		
		HttpURLConnection conn = HTTPConnectionProvider.getConnection(
				exchangeTokenUrl,
				HTTPRequestType.GET);
		
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
	
//	public static void main(String[] args) throws JSONException {
//		FacebookAuthFlowManager fm = new FacebookAuthFlowManager(new OAuthConfig());
//		String s = "access_token=djksfdsdkljf&expires=5125091";
//		System.out.println(fm.parseResponse(s));
//	}
}
