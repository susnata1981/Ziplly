package com.ziplly.app.server.oauth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.Map;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.ziplly.app.client.oauth.AccessToken;
import com.ziplly.app.client.oauth.OAuthConfig;
import com.ziplly.app.client.oauth.OAuthProvider;

public abstract class OAuthFlowManager implements IOAuthFlowManager {
	protected OAuthProvider provider;
	protected String clientId;
	protected String key;
	protected String redirectUri;
	
	public OAuthFlowManager(OAuthConfig config) {
		this.provider = config.getProvider();
		this.clientId = config.getClientId();
		this.key = config.getKey();
		try {
			this.redirectUri = config.getRedirectUri();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
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

}
