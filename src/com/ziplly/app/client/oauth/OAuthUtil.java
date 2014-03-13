package com.ziplly.app.client.oauth;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.http.client.URL;

public class OAuthUtil {

	public static String
	    getUrlWithParam(String url, Map<String, String> params) throws UnsupportedEncodingException {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String key : params.keySet()) {
			if (!first) {
				sb.append("&");
			} else {
				first = false;
			}
			sb.append(key);
			sb.append("=");
			sb.append(encode(params.get(key)));
		}
		return url + "?" + sb.substring(0, sb.length()).toString();
	}

	public static String encode(String val) throws UnsupportedEncodingException {
		return URL.encode(val);
	}

	/*
	 * This is for the client side.
	 */
	public static String getParamUrl(Map<String, String> params) throws UnsupportedEncodingException {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String key : params.keySet()) {
			if (!first) {
				sb.append("&");
			} else {
				first = false;
			}
			sb.append(key);
			sb.append("=");
			sb.append(encode(params.get(key)));
		}
		return sb.toString();
	}

	public static void main(String[] args) throws UnsupportedEncodingException {
		Map<String, String> paramsMap = new HashMap<String, String>();
		paramsMap.put("scope", "scope");
		paramsMap.put("client_id", "clientId");
		paramsMap.put("redirect_uri", "http://redirect.com");
		paramsMap.put("response_type", "code");
		String url = OAuthConfigConstants.FB_TOKEN_URL;
		System.out.println(OAuthUtil.getUrlWithParam(url, paramsMap));
	}
}
