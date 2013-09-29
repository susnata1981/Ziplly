package com.ziplly.app.server.oauth;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.ziplly.app.client.oauth.AccessToken;
import com.ziplly.app.client.oauth.OAuthConfig;
import com.ziplly.app.client.oauth.OAuthUtil;

public class GoogleAuthFlowManager extends OAuthFlowManager {

	public GoogleAuthFlowManager(OAuthConfig config) {
		super(config);
	}

	@Override
	public Map<String, String> getRequestParams(String code)
			throws UnsupportedEncodingException {
		Map<String, String> paramsMap = new HashMap<String, String>();
		paramsMap.put("code", code);
		paramsMap.put("client_id", clientId);
		paramsMap.put("client_secret", key);
		paramsMap.put("key", key);
		paramsMap.put("redirect_uri", redirectUri);
		paramsMap.put("grant_type", "authorization_code");
		return paramsMap;
	}

	public AccessToken parseResponse(String response) throws JSONException {
		Gson gson = new Gson();
		return gson.fromJson(response, AccessToken.class);
	}

	@Override
	public String doExchange(Map<String, String> params) throws IOException {
		String data = OAuthUtil.getParamUrl(params);
		HttpURLConnection conn = HTTPConnectionProvider.getConnection(
				provider.getTokenUrl(), HTTPRequestType.POST);
		doPost(conn, data);
		return getResponse(conn);
	}

	private void doPost(HttpURLConnection conn, String data) throws IOException {
		OutputStreamWriter writer = new OutputStreamWriter(
				conn.getOutputStream());
		writer.write(data);
		writer.close();
	}
	
	// @Override
	// public User getUser(AccessToken token) throws IOException {
	// // get user id
	// Map<String,String> params = new HashMap<String,String>();
	// params.put("access_token",token.getAccess_token());
	// URL url = HTTPConnectionProvider.getUrlWithParams(
	// OAuthConfigConstants.GOOGLE_USER_INFO_URL,
	// params);
	// HttpURLConnection conn =
	// HTTPConnectionProvider.getConnection(url,HTTPRequestType.GET);
	// String response = getResponse(conn);
	// Gson gson = new Gson();
	// User emailScopedUser = gson.fromJson(response, User.class);
	//
	// // retrieve google plus profile info
	// String plusUrl = OAuthConfigConstants.GOOGLE_PLUS_USER_URL + "/" +
	// emailScopedUser.getId();
	// url = HTTPConnectionProvider.getUrlWithParams(plusUrl,params);
	// conn = HTTPConnectionProvider.getConnection(url,HTTPRequestType.GET);
	// response = getResponse(conn);
	// User user = gson.fromJson(response, User.class);
	// user.setEmail(emailScopedUser.getEmail());
	// return user;
	// }
}
