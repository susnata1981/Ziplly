package com.ziplly.app.shared.facebook;

import java.io.Serializable;

import com.restfb.Facebook;

public class FFriendRelation implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Facebook("uid2")
	public String uid;
}