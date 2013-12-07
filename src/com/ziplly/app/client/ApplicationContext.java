package com.ziplly.app.client;

import java.util.ArrayList;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.ziplly.app.client.widget.TweetWidget;
import com.ziplly.app.model.AccountDTO;

public class ApplicationContext {
	private AccountDTO account;
	private int unreadMessageCount;
	private int totalTweets;
	private int totalComments;
	private int totalLikes;
	ArrayList<TweetWidget> widgets = new ArrayList<TweetWidget>();
	int index = 0, x = 0;
	Timer timer;

	public ApplicationContext() {
//		Scheduler.get().scheduleDeferred(new Command() {
//			@Override
//			public void execute() {
//				for(int i=0;i<30;i++) {
//				widgets.add(new TweetWidget());
//				}
//			}
//		});
//		if (timer == null) {
//			timer = new Timer() {
//				@Override
//				public void run() {
//					widgets.add(new TweetWidget());
//					x++;
//					if (x > 30) {
//						timer.cancel();
//					}
//				}
//			};
//		}
//		timer.schedule(100);
	}

	public AccountDTO getAccount() {
		return account;
	}

	public void setAccount(AccountDTO account) {
		this.account = account;
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
}
