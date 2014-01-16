package com.ziplly.app.shared;

import java.util.List;

import com.ziplly.app.model.AccountNotificationDTO;

import net.customware.gwt.dispatch.shared.Result;

public class GetAccountDetailsResult implements Result {
	private int unreadMessages;
	private int totalTweets;
	private int totalComments;
	private int totalLikes;
	private List<AccountNotificationDTO> notifications;
	
	public GetAccountDetailsResult() {
	}
	public int getUnreadMessages() {
		return unreadMessages;
	}
	public void setUnreadMessages(int unreadMessages) {
		this.unreadMessages = unreadMessages;
	}
	public int getTotalTweets() {
		return totalTweets;
	}
	public void setTotalTweets(int totalTweets) {
		this.totalTweets = totalTweets;
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
	public void setAccountNotifications(List<AccountNotificationDTO> notifications) {
		this.notifications = notifications;
	}
	public List<AccountNotificationDTO> getAccountNotifications() {
		return notifications;
	}
}
