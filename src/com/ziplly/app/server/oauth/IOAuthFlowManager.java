package com.ziplly.app.server.oauth;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import com.google.gwt.json.client.JSONException;
import com.ziplly.app.client.oauth.AccessToken;

public interface IOAuthFlowManager {
	/* Derived classes needs to implements */
	Map<String,String> getRequestParams(String code) throws UnsupportedEncodingException;
	
	/* provided by the abstract class */
	AccessToken exchange(String code) throws IOException, JSONException;
	
	/* Derived classes needs to implements */
	String doExchange(Map<String,String> params) throws IOException;
	
	/* Derived classes needs to implements */
	AccessToken parseResponse(String response) throws JSONException;
}
