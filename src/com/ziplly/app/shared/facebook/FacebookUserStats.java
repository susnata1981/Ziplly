package com.ziplly.app.shared.facebook;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ziplly.app.model.FLike;

public class FacebookUserStats implements Serializable{
	private static final long serialVersionUID = 1L;
	public int totalFriendCount;
	public List<FacebookPost> posts;
	public long postCount;
	public long likeCount;
	public long checkInCount;
	public Map<String,Integer> likeDetails = new HashMap<String,Integer>();
	public List<FFriend> friends = new ArrayList<FFriend>();
	
	public void addLikeInfo(FLike fl) {
		if (!likeDetails.containsKey(fl.objectType)) {
			likeDetails.put(fl.objectType, 0);
		}
		Integer count = likeDetails.get(fl.objectType);
		likeDetails.put(fl.objectType,count+1);
	}
	
	public void addLikeInfo(List<FLike> likes) {
		for(FLike fl: likes) {
			addLikeInfo(fl);
		}
	}
	
	public static class LikeInfo {
		public String objectType;
	}
}
