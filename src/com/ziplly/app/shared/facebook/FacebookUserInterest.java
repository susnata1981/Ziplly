package com.ziplly.app.shared.facebook;

import java.io.Serializable;
import java.util.List;

import com.google.common.collect.Lists;

public class FacebookUserInterest implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public List<String> interests = Lists.newArrayList();
}
