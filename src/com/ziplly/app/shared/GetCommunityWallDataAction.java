package com.ziplly.app.shared;

import com.ziplly.app.model.TweetType;

import net.customware.gwt.dispatch.shared.Action;

public class GetCommunityWallDataAction implements Action<GetCommunityWallDataResult> {
	private TweetType type;
	
	public GetCommunityWallDataAction() {
	}
	
	public GetCommunityWallDataAction(TweetType type) {
		this.type = type;
	}
	
	public TweetType getType() {
		return type;
	}
	
	public void setType(TweetType type) {
		this.type = type;
	}
}
