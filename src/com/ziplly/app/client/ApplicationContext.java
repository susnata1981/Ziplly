package com.ziplly.app.client;

import com.ziplly.app.client.activities.HomeActivity.IHomeView;
import com.ziplly.app.model.AccountDTO;

public class ApplicationContext {
	// bad idea
	private IHomeView homeView;
	private AccountDTO account;
	private int unreadMessageCount;
	private int totalTweets;
	private int totalComments;
	private int totalLikes;
	
	public ApplicationContext() {
	}
	
	public AccountDTO getAccount() {
		return account;
	}

	public void setAccount(AccountDTO account) {
		this.account = account;
	}

	public IHomeView getHomeView() {
		return homeView;
	}

	public void setHomeView(IHomeView homeView2) {
		this.homeView = homeView2;
	}

	public int getUnreadMessageCount() {
		return unreadMessageCount;
	}

	public void setUnreadMessageCount(int unreadMessageCount) {
		this.unreadMessageCount = unreadMessageCount;
	}

	public int getTotalLikes() {
		return totalLikes;
	}

	public void setTotalLikes(int totalLikes) {
		this.totalLikes = totalLikes;
	}

	public int getTotalComments() {
		return totalComments;
	}

	public void setTotalComments(int totalComments) {
		this.totalComments = totalComments;
	}

	public int getTotalTweets() {
		return totalTweets;
	}

	public void setTotalTweets(int totalTweets) {
		this.totalTweets = totalTweets;
	}
}
