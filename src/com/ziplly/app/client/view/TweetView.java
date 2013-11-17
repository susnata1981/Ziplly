package com.ziplly.app.client.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.ziplly.app.client.activities.TweetPresenter;
import com.ziplly.app.client.widget.TweetWidget;
import com.ziplly.app.model.CommentDTO;
import com.ziplly.app.model.LoveDTO;
import com.ziplly.app.model.TweetDTO;

public class TweetView extends Composite implements
		ITweetView<TweetPresenter> {

	private static TweetViewUiBinder uiBinder = GWT
			.create(TweetViewUiBinder.class);

	interface TweetViewUiBinder extends UiBinder<Widget, TweetView> {
	}

	@UiField
	HTMLPanel tweetsSection;
	TweetPresenter presenter;
	Map<Long, TweetWidget> tweetWidgetMap = new HashMap<Long, TweetWidget>();
	
	public TweetView() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void setPresenter(TweetPresenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void clear() {
		tweetsSection.clear();
	}

	@Override
	public void displayTweets(List<TweetDTO> tweets) {
		tweetsSection.clear();
		doDisplayTweets(tweets);
	}

	private void doDisplayTweets(List<TweetDTO> tweets) {
		if (tweets != null) {
			for (TweetDTO tweet : tweets) {
				addTweet(tweet);
			}
		}
	}
	
	private void addTweet(TweetDTO tweet) {
		TweetWidget tw = new TweetWidget();
		tw.setWidth("60%");
		tw.setPresenter(presenter);
		tw.displayTweet(tweet);
		tweetsSection.add(tw);
		tweetWidgetMap.put(tweet.getTweetId(), tw);
	}

	@Override
	public void add(List<TweetDTO> tweets) {
		doDisplayTweets(tweets);
	}

	@Override
	public void updateComment(CommentDTO comment) {
		TweetWidget tweetWidget = tweetWidgetMap.get(comment.getTweet().getTweetId());
		tweetWidget.updateComment(comment);
	}

	@Override
	public void updateLike(LoveDTO like) {
		TweetWidget tweetWidget = tweetWidgetMap.get(like.getTweet().getTweetId());
		tweetWidget.updateLike(like);
	}

	@Override
	public void updateTweet(TweetDTO tweet) {
		TweetWidget tweetWidget = tweetWidgetMap.get(tweet.getTweetId());
		tweetWidget.updateTweet(tweet);
	}
}
