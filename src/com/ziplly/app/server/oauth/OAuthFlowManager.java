package com.ziplly.app.server.oauth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.json.client.JSONException;
import com.ziplly.app.client.oauth.AccessToken;
import com.ziplly.app.client.oauth.OAuthAppProperties;
import com.ziplly.app.client.oauth.OAuthConfig;
import com.ziplly.app.client.oauth.OAuthProvider;

public abstract class OAuthFlowManager implements IOAuthFlowManager {
	protected OAuthProvider provider;
	protected String clientId;
	protected String key;
	protected String redirectUri = OAuthAppProperties.REDIRECT_URL_IN_DEVELOPMENT;
	
	public OAuthFlowManager(OAuthConfig config) {
		this.provider = config.getProvider();
		this.clientId = config.getClientId();
		this.key = config.getKey();
		try {
			this.redirectUri = getRedirectUri();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}	
	
	public String getRedirectUri() throws UnsupportedEncodingException {
		Map<String,String> paramsMap = new HashMap<String,String>();
		// TODO IMP.
		paramsMap.put("gwt.codesvr","127.0.0.1:9997");
		return FacebookAuthFlowManager.getUrlWithParam(redirectUri, paramsMap);
	}
	
	@Override
	public AccessToken exchange(String code) throws IOException, JSONException {
		Map<String,String> params = getRequestParams(code);
		
		String response = doExchange(params);

		return parseResponse(response);
	}
	
	protected String getResponse(HttpURLConnection conn) throws IOException {
		if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
			System.out.println(conn.getResponseCode());
			throw new RuntimeException(conn.getResponseMessage());
		}
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				conn.getInputStream()));
		String line = null;
		StringBuilder sb = new StringBuilder();
		while ((line = reader.readLine()) != null) {
			System.out.println(line);
			sb.append(line);
		}
		reader.close();
		return sb.toString();
	}
	
	public static String getUrlWithParam(String url, Map<String,String> params) throws UnsupportedEncodingException {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for(String key: params.keySet()) {
			if (!first) {
				sb.append("&");
			} else {
				first = false;
			}
			sb.append(key);
			sb.append("=");
			String val = params.get(key);
			sb.append(URLEncoder.encode(val,"utf-8"));
		}
		return url + "?" + sb.substring(0, sb.length()).toString();
	}

}
