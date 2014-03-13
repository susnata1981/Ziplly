package com.ziplly.app.facebook.dao;

import com.restfb.Connection;
import com.restfb.types.Post;
import com.restfb.types.User;
import com.ziplly.app.model.LatLong;
import com.ziplly.app.shared.facebook.FacebookUserInterest;
import com.ziplly.app.shared.facebook.FacebookUserStats;

public interface IFUserDAO {
	User getUser();

	Connection<Post> getPosts();

	LatLong getLocationInfo(User user);

	FacebookUserStats getStats();

	FacebookUserInterest getInterests();

	FacebookUserStats getUserLikeDetails(String uid);

	FacebookUserStats getUserFriendDetails(String uid);
}
