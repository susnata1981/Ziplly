package com.ziplly.app.facebook.dao;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.restfb.Connection;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.json.JsonArray;
import com.restfb.json.JsonObject;
import com.restfb.types.Post;
import com.restfb.types.User;
import com.ziplly.app.model.LatLong;
import com.ziplly.app.shared.facebook.FFriend;
import com.ziplly.app.shared.facebook.FFriendRelation;
import com.ziplly.app.shared.facebook.FacebookPost;
import com.ziplly.app.shared.facebook.FacebookUserInterest;
import com.ziplly.app.shared.facebook.FacebookUserStats;
import com.ziplly.app.shared.facebook.MQueryResults;

public class FUserDAO implements IFUserDAO {
	Logger logger = Logger.getLogger("FUserDAO");
	private FacebookClient fbClient;
	private static final int FEED_LOOKUP_THRESHOLD = 500;

	public FUserDAO(FacebookClient fbClient) {
		this.fbClient = fbClient;
	}

	@Override
	public User getUser() {
		User user = fbClient.fetchObject("me", User.class);
		logger.log(Level.INFO, String.format("Following user logged in %s", user));
		// getUserFriendDetails(user.getId());
		return user;
	}

	@Override
	public FacebookUserInterest getInterests() {
		JsonObject jsonObject = fbClient.fetchObject("me/interests", JsonObject.class);
		FacebookUserInterest fui = new FacebookUserInterest();
		JsonArray interestList = jsonObject.getJsonArray("data");
		for (int i = 0; i < interestList.length(); i++) {
			String interest = (String) interestList.getJsonObject(i).get("name");
			if (interest != null) {
				fui.interests.add(interest);
			}
		}
		return fui;
	}

	@Override
	public Connection<Post> getPosts() {
		Connection<Post> posts = fbClient.fetchConnection("me/feed", Post.class);
		logger.log(Level.INFO, String.format("Posts %s", posts));
		return posts;
	}

	@Override
	public LatLong getLocationInfo(User user) {
		JsonObject locationData = fbClient.fetchObject(user.getLocation().getId(), JsonObject.class);
		JsonObject location = locationData.getJsonObject("location");
		String longitude = location.get("longitude").toString();
		String latitude = location.get("latitude").toString();
		LatLong locInfo = new LatLong();
		logger.log(
		    Level.INFO,
		    String.format("LocationData(%s): %s", user.getLocation().getId(), locationData));
		locInfo.longitude = longitude;
		locInfo.latitude = latitude;
		return locInfo;
	}

	@Override
	public FacebookUserStats getUserFriendDetails(String uid) {
		FacebookUserStats fus = new FacebookUserStats();
		String query = String.format("select uid2  from friend where uid1=%s", uid);
		List<FFriendRelation> friendRelations = fbClient.executeFqlQuery(query, FFriendRelation.class);
		String param = join(friendRelations);
		query =
		    String.format("select uid, first_name, current_location from user where uid in %s", param);
		List<FFriend> friends = fbClient.executeFqlQuery(query, FFriend.class);
		fus.friends.addAll(friends);
		return fus;
	}

	String join(List<FFriendRelation> input) {
		StringBuilder qparam = new StringBuilder("(");
		int length = input.size();
		for (int i = 0; i < length; i++) {
			FFriendRelation friend = input.get(i);
			qparam.append(friend.uid);
			if (i != length - 1) {
				qparam.append(",");
			}
		}
		qparam.append(")");
		return qparam.toString();
	}

	/*
	 * Gets total posts
	 */
	public List<FacebookPost> getAllPosts() {
		Connection<Post> posts =
		    fbClient.fetchConnection(
		        "me/feed",
		        Post.class,
		        Parameter.with("limit", FEED_LOOKUP_THRESHOLD));
		List<FacebookPost> response = Lists.newArrayList();
		if (posts != null) {
			for (Post p : posts.getData()) {
				FacebookPost post = new FacebookPost();
				// post.description = p.getDescription();
				post.timeCreated = p.getCreatedTime();
				response.add(post);
			}
			return response;
		}
		return null;
	}

	@Override
	public FacebookUserStats getStats() {
		FacebookUserStats fus = new FacebookUserStats();
		fus.posts = getAllPosts();
		return fus;
	}

	public MQueryResults getUserStatusDetails(String uid) {
		Map<String, String> queries =
		    ImmutableMap.of(
		        "statusList",
		        String.format("Select like_info.like_count,time from status where uid = %s", uid));
		MQueryResults mqr = fbClient.executeFqlMultiquery(queries, MQueryResults.class);
		java.util.Date time = new java.util.Date((long) mqr.statusList.get(0).time * 1000);

		System.out.println("Result size=" + mqr.statusList.size() + " count(0)="
		    + mqr.statusList.get(0).likeInfo.likeCount + " time=" + time);
		return mqr;
	}

	@Override
	public FacebookUserStats getUserLikeDetails(String uid) {
		FacebookUserStats fus = new FacebookUserStats();
		Map<String, String> queries =
		    ImmutableMap.of(
		        "likes",
		        String.format("Select object_type from like where user_id = %s", uid));
		MQueryResults mqr = fbClient.executeFqlMultiquery(queries, MQueryResults.class);
		fus.addLikeInfo(mqr.likes);
		return fus;
	}
}