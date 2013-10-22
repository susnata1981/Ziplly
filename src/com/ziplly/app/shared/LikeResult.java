package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Result;

import com.ziplly.app.model.LoveDTO;

public class LikeResult implements Result{
	private LoveDTO like;
	
	public LikeResult(LoveDTO like) {
		this.setLike(like);
	}
	
	public LikeResult() {
	}

	public LoveDTO getLike() {
		return like;
	}

	public void setLike(LoveDTO like) {
		this.like = like;
	}
}
