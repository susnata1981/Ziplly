package com.ziplly.app.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AccountDetails implements Serializable {
	private static final long serialVersionUID = 1L;
	public Account account;
	public List<Category> categories = new ArrayList<Category>();
}
