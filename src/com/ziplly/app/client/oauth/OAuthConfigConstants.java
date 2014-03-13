package com.ziplly.app.client.oauth;

public class OAuthConfigConstants {
	/*
	 * Google
	 */
	public static final String GOOGLE_AUTH_URL = "https://accounts.google.com/o/oauth2/auth";
	public static final String GOOGLE_TOKEN_URL = "https://accounts.google.com/o/oauth2/token";
	public static final String GOOGLE_USER_INFO_URL = "https://www.googleapis.com/oauth2/v1/userinfo";
	public static final String GOOGLE_LOGOUT_URL = "https://mail.google.com/mail/?logout";
	public static final String GOOGLE_PLUS_USER_URL = "https://www.googleapis.com/plus/v1/people";

	/*
	 * Facebook
	 */
	public static final String FB_AUTH_URL = "https://www.facebook.com/dialog/oauth";
	public static final String FB_TOKEN_URL = "https://graph.facebook.com/oauth/access_token";
	public static final String FB_USER_INFO_URL = "https://graph.facebook.com/me";
	public static final String FB_LOGOUT_URL = "https://www.facebook.com/logout.php";
}
