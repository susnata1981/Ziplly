package com.ziplly.app.client.oauth;




public class OAuthAppProperties {
	
	/*
	 * Google
	 */
	public static final String APP_NAME = "Ziplly";
	public static final String GOOGLE_CLIENT_ID = "808128945093-oval63pn5kmt7nub9icoiln323n134qg.apps.googleusercontent.com";
	public static final String GOOGLE_CLIENT_SECRET = "BjIdWVQVrMmLxZ4nqGwD6PR6";
	
	public static final String REDIRECT_URL_IN_DEVELOPMENT = "http://localhost:8888/Ziplly.html";//?gwt.codesvr=127.0.0.1%3A9997";
	public static final String REDIRECT_URL_IN_PRODUCTION = "http://www.ziplly.com";
	
	public static final String [] SCOPES = {
		"https://www.googleapis.com/auth/userinfo.email",
		"https://www.googleapis.com/auth/userinfo.profile"
	};
	
	/*
	 * Facebook
	 */
	public static final String FB_CLIENT_ID = "217425525078759";
	public static final String FB_CLIENT_SECRET = "d2cf33cf366eaa96ec64a137adcb2914";
	public static final String[] FB_SCOPES = {
		"email," +
		"user_about_me,"
//		"user_activities," +
//		"user_interests," +
//		"read_friendlists," + 
//		"read_stream,"+
//		"friends_location"
//		"user_likes," +
//		"user_checkins,"
	};
}
