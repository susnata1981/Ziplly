package com.ziplly.app.shared.facebook;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FacebookUserInterest implements Serializable {
	private static final long serialVersionUID = 1L;

	public List<String> interests = new ArrayList<String>();
}
