package com.ziplly.app.shared.facebook;

import java.util.List;

import com.restfb.Facebook;
import com.ziplly.app.model.FLike;

public class MQueryResults {
	@Facebook
	public List<FStatus> statusList;

	@Facebook
	public List<FLike> likes;

	@Facebook
	public List<FFriend> friends;

	@Facebook
	public List<FFriendRelation> friendRelations;
}
