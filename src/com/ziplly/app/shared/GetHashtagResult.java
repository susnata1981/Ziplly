package com.ziplly.app.shared;

import java.util.List;

import net.customware.gwt.dispatch.shared.Result;

import com.ziplly.app.model.HashtagDTO;

public class GetHashtagResult implements Result {
	private List<HashtagDTO> hashtags;

	public GetHashtagResult() {
	}

	public GetHashtagResult(List<HashtagDTO> hashtags) {
		this.setHashtags(hashtags);
	}

	public List<HashtagDTO> getHashtags() {
		return hashtags;
	}

	public void setHashtags(List<HashtagDTO> hashtags) {
		this.hashtags = hashtags;
	}
}
