package com.ziplly.app.client.activities;

import java.util.List;

import com.ziplly.app.model.TweetDTO;

public interface HomePresenter extends Presenter {
	void displayTweets(List<TweetDTO> tweets);

	void displayProfile(Long accountId);
}
