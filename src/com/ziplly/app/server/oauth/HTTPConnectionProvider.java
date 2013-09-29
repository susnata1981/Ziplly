package com.ziplly.app.server.oauth;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

public class HTTPConnectionProvider {

	private static final int TIMEOUT = 5000;

	public static HttpURLConnection getConnection(String url, HTTPRequestType type) throws IOException {
		// TODO: sanitise the url
		URL safeUrl = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) safeUrl.openConnection();
		switch(type) {
			case POST:
				conn.setDoInput(true);
				conn.setDoOutput(true);
				conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				conn.setRequestMethod("POST");
				conn.setConnectTimeout(TIMEOUT);
				conn.setReadTimeout(TIMEOUT);
				return conn;
			default:
				conn.setDoOutput(true);
				return conn;
		}
	}
	
	public static HttpURLConnection getConnection(URL url, HTTPRequestType type) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		switch(type) {
			case POST:
				conn.setDoInput(true);
				conn.setDoOutput(true);
				conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				conn.setRequestMethod("POST");
				conn.setConnectTimeout(TIMEOUT);
				conn.setReadTimeout(TIMEOUT);
				return conn;
			default:
				return conn;
		}
	}
	
	public static URL getUrlWithParams(String url, Map<String,String> params) throws UnsupportedEncodingException, MalformedURLException {
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
			sb.append(URLEncoder.encode(params.get(key),"utf-8"));
		}
		String nurl = url + "?" + sb.substring(0, sb.length()).toString();
		return new URL(nurl);
	}

}
