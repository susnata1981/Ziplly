package com.ziplly.app.client.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.ziplly.app.client.activities.TweetPresenter;
import com.ziplly.app.client.widget.StyleHelper;
import com.ziplly.app.client.widget.TweetWidget;
import com.ziplly.app.model.CommentDTO;
import com.ziplly.app.model.LoveDTO;
import com.ziplly.app.model.TweetDTO;

public class TweetView extends Composite implements ITweetView<TweetPresenter> {

	private static TweetViewUiBinder uiBinder = GWT.create(TweetViewUiBinder.class);

	interface TweetViewUiBinder extends UiBinder<Widget, TweetView> {
	}

	interface Style extends CssResource {
		String tweetWidget();
	}

	@UiField
	Style style;

	@UiField
	Alert message;
	
	@UiField
	FlowPanel tweetsSection;
	TweetPresenter presenter;

	HTMLPanel tempPanel = new HTMLPanel("");

	// TweetId ---> TweetWidget
	Map<Long, TweetWidget> tweetWidgetMap = new HashMap<Long, TweetWidget>();

	private String tweetWidgetWidth = "68%";

	public TweetView() {
		initWidget(uiBinder.createAndBindUi(this));
		message.setAnimation(true);
		StyleHelper.show(message.getElement(), false);
	}

	@Override
	public void setPresenter(TweetPresenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void clear() {
		StyleHelper.show(message.getElement(), false);
		tweetsSection.clear();
	}

	@Override
	public void displayTweets(List<TweetDTO> tweets, boolean displayNoTweetsMessage) {
		if (tweets != null) {
			tweetsSection.clear();
			StyleHelper.show(message.getElement(), false);
			
			if (tweets.isEmpty()) {
				if (displayNoTweetsMessage) {
					displayNoTweetsMessage();
				}
				return;
			}
			
			doDisplayTweets(tweets);
		}
	}

	@Override
	public void displayTweets(List<TweetDTO> tweets) {
		if (tweets != null) {
			displayTweets(tweets, true);
		}
	}
	
	private void displayNoTweetsMessage() {
		message.setText(StringConstants.TWEET_NOT_POSTED);
		StyleHelper.show(message.getElement(), true);
	}

	@Override
	public void setWidth(String width) {
		tweetWidgetWidth = width;
	}

	@Override
	public void setHeight(String tweetWidgetHeight) {
		tweetsSection.setHeight(tweetWidgetHeight);
	}
	
	private void doDisplayTweets(List<TweetDTO> tweets) {
		for (TweetDTO tweet : tweets) {
			addTweet(tweet);
		}
	}

	@Override
	public void addTweet(final TweetDTO tweet) {
		Scheduler.get().scheduleDeferred(new Command() {
			@Override
			public void execute() {
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					
					@Override
					public void execute() {
//						long s1 = System.currentTimeMillis();
						TweetWidget tw = new TweetWidget();
						tw.setWidth(tweetWidgetWidth);
						tw.setPresenter(presenter);
						tw.displayTweet(tweet);
						tw.addStyleName(style.tweetWidget());
						tweetsSection.add(tw);
//						int sh = tweetsSection.getElement().getScrollHeight();
//						long e1 = System.currentTimeMillis();
//						System.out.println("Time to create widget("+tweet.getTweetId()+") "+(e1-s1));
						tweetWidgetMap.put(tweet.getTweetId(), tw);
					}
				});
			}
		});
	}

	@Override
	public void insertTweet(final TweetDTO tweet) {
		Scheduler.get().scheduleDeferred(new Command() {
			@Override
			public void execute() {
//				long s1 = System.currentTimeMillis();
				TweetWidget tw = new TweetWidget();
				tw.setWidth(tweetWidgetWidth);
				tw.setPresenter(presenter);
				tw.displayTweet(tweet);
				tw.addStyleName(style.tweetWidget());
				tweetsSection.insert(tw, 0);
//				int sh = tweetsSection.getElement().getScrollHeight();
//				long e1 = System.currentTimeMillis();
				// System.out.println("Time to create widget("+tweet.getTweetId()+") "+(e1-s1));
				tweetWidgetMap.put(tweet.getTweetId(), tw);
			}
		});
	}

	@Override
	public void add(List<TweetDTO> tweets) {
		doDisplayTweets(tweets);
	}

	@Override
	public void remove(TweetDTO tweet) {
		TweetWidget widget = tweetWidgetMap.get(tweet.getTweetId());
		widget.removeFromParent();
	}

	@Override
	public void addComment(CommentDTO comment) {
		TweetWidget tweetWidget = tweetWidgetMap.get(comment.getTweet().getTweetId());
		tweetWidget.addComment(comment);
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

	public Element getTweetSection() {
		return tweetsSection.getElement();
	}

	@Override
	public void displayMessage(String msg, AlertType type) {
		message.setText(msg);
		message.setType(type);
		StyleHelper.show(message.getElement(), true);
	}
}
