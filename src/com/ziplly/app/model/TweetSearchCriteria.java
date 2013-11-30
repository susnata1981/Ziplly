package com.ziplly.app.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TweetSearchCriteria implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private int zip;
	private List<TweetType> types = new ArrayList<TweetType>();
	private TweetStatus status;
	
	public int getZip() {
		return zip;
	}
	public void setZip(int zip) {
		this.zip = zip;
	}
	public List<TweetType> getType() {
		return types;
	}
	public void addType(TweetType type) {
		types.add(type);
	}
	public TweetStatus getStatus() {
		return status;
	}
	public void setStatus(TweetStatus status) {
		this.status = status;
	}
	
	
}
