package com.ziplly.app.client;

import java.util.ArrayList;

import com.google.gwt.user.client.Timer;
import com.ziplly.app.client.widget.TweetWidget;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.NeighborhoodDTO;
import com.ziplly.app.shared.GetAccountDetailsResult;

public class ApplicationContext {
	public enum Environment {
		DEVEL,
		PROD;
	}

	private Environment environment = Environment.DEVEL;
	private boolean environmentSet = false;
	private AccountDTO account;
	private int unreadMessageCount;
	private int totalTweets;
	private int totalComments;
	private int totalLikes;
	ArrayList<TweetWidget> widgets = new ArrayList<TweetWidget>();
	int index = 0, x = 0;
	Timer timer;

	public ApplicationContext() {
	}

	public AccountDTO getAccount() {
		return account;
	}

	public void setAccount(AccountDTO account) {
		this.account = account;
	}

	public NeighborhoodDTO getCurrentNeighborhood() {
		return account.getCurrentLocation().getNeighborhood();
	}
	
	public TweetWidget getTweetWidget() {
		return widgets.get(index++);
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

	public Environment getEnvironment() {
		return environment;
	}

	public boolean isEnvironmentSet() {
		return environmentSet;
	}
	
	public void setEnvironment(Environment environment) {
		if (environmentSet) {
			return;
		}
		this.environment = environment;
		environmentSet = true;
	}

	/**
	 * Updated account details based on the response of GetAccountDetailsAction rpc
	 * @param result
	 */
	public void updateAccountDetails(GetAccountDetailsResult result) {
		this.unreadMessageCount = result.getUnreadMessages();
		this.totalComments = result.getTotalComments();
		this.totalLikes = result.getTotalLikes();
		this.totalTweets = result.getTotalTweets();
	}
}


