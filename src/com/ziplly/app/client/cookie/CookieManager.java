package com.ziplly.app.client.cookie;

import java.util.Date;

import com.google.gwt.user.client.Cookies;
import com.ziplly.app.model.AccountDTO;

public class CookieManager {

	public static final String LOGIN_COOKIE_NAME = "sid";

	public static void dropLoginCookie(AccountDTO account) {
		Date expiresIn = new Date(System.currentTimeMillis() + 60 * 60 * 1000);
		Cookies.setCookie(LOGIN_COOKIE_NAME, account.getAccountId().toString(), expiresIn);
	}

	public static String getLoginCookie() {
		return Cookies.getCookie(LOGIN_COOKIE_NAME);
	}

	public static boolean isUserLoggedIn() {
		return getLoginCookie() != null;
	}

	public static void removeCookie(String cookieName) {
		Cookies.removeCookie(cookieName);
	}

	public static void removeLoginCookie() {
		System.out.println("CALLING remove COOKIE...");
		removeCookie(LOGIN_COOKIE_NAME);
	}
}
