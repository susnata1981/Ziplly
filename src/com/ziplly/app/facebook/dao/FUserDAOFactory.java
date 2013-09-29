package com.ziplly.app.facebook.dao;

import com.restfb.DefaultFacebookClient;

public class FUserDAOFactory {
	public static IFUserDAO getFUserDao(String accessToken) {
		return new FUserDAO(new DefaultFacebookClient(accessToken));
	}
}
