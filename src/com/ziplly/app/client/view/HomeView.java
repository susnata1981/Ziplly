package com.ziplly.app.client.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.maps.gwt.client.LatLng;
import com.ziplly.app.client.activities.HomeActivity.IHomeView;
import com.ziplly.app.client.activities.SendMessagePresenter;
import com.ziplly.app.client.activities.TweetPresenter;
import com.ziplly.app.client.widget.CommunitySummaryWidget;
import com.ziplly.app.client.widget.TweetBox;
import com.ziplly.app.model.CommentDTO;
import com.ziplly.app.model.HashtagDTO;
import com.ziplly.app.model.LoveDTO;
import com.ziplly.app.model.NeighborhoodDTO;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.model.TweetType;
import com.ziplly.app.shared.GetLatLngResult;

/**
 * Community Wall View
 */
public class HomeView extends Composite implements IHomeView {

	private static final String TWEET_WIDGT_WIDTH = "58%";
	private String tweetWidth = "58%";
	private static HomeViewUiBinder uiBinder = GWT.create(HomeViewUiBinder.class);

	public static interface HomePresenter extends TweetPresenter, SendMessagePresenter {
		void displayTweets(List<TweetDTO> tweets);

		void displayTweetsForCategory(TweetType type);

		void onLogin(String emailInput, String passwordInput);

		void displayHashtag(String text); 
	}

	@UiField
	Alert message;

	@UiField
	HTMLPanel hashtagPanel;
	@UiField
	HTMLPanel filterTweetsPanel;

	@UiField
	HTMLPanel communityWallPanel;

	@UiField
	TweetBox tweetBox;

	@UiField
	CommunitySummaryWidget communitySummaryWidget;

	HomePresenter presenter;
	Map<TweetType, Anchor> filters = new HashMap<TweetType, Anchor>();
	ArrayList<Anchor> hashtagAnchors = new ArrayList<Anchor>();
	ITweetView<TweetPresenter> tview = new TweetView();

	interface HomeViewUiBinder extends UiBinder<Widget, HomeView> {
	}

	public HomeView() {
		initWidget(uiBinder.createAndBindUi(this));
		buildTweetFilters();
		message.setAnimation(true);
		tweetBox.setTweetCategory(TweetType.getAllTweetTypeForPublishingByUser());
		tweetBox.setWidth(tweetWidth);
		tweetBox.getElement().getStyle().setMarginLeft(1.2, Unit.PCT);
		tweetBox.setPresenter(presenter);
		communityWallPanel.add(tweetBox);
		tview.setWidth(TWEET_WIDGT_WIDTH);
		communityWallPanel.add(tview);
		communitySummaryWidget.setHeight("90%");
	}

	private void buildTweetFilters() {
		for (final TweetType type : TweetType.values()) {
			final Anchor anchor = new Anchor();
			String name = type.getTweetName();
			anchor.setText(name);
			anchor.setStyleName("tweetFilterLink");
			anchor.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					clearTweetFilterSelection();
					clearTagSelection();
					anchor.getElement().getStyle().setBackgroundColor("#c2c2c2");
					presenter.displayTweetsForCategory(type);
				}
			});
//			Icon icon = new Icon();
//			icon.setType(IconType.TAG);
//			anchor.setHTML(SafeHtmlUtils.fromSafeConstant("<a>"+type.name()+icon+"</a>"));
			filterTweetsPanel.add(anchor);
			filters.put(type, anchor);
		}
	}

	private void clearTweetFilterSelection() {
		for (Anchor a : filters.values()) {
			clearAnchorSelection(a);
		}
	}

	private void clearTagSelection() {
		for (Anchor a : hashtagAnchors) {
			clearAnchorSelection(a);
		}
	}

	private void clearAnchorSelection(Anchor a) {
		a.getElement().getStyle().setBackgroundColor("");
	}

	@Override
	public void displayHashtag(List<HashtagDTO> hashtags) {
		hashtagPanel.clear();
		for (HashtagDTO h : hashtags) {
			final Anchor anchor = new Anchor(h.getTag());
			anchor.addStyleName("tweetFilterLink");
			anchor.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					for (Anchor a : hashtagAnchors) {
						a.getElement().getStyle().setBackgroundColor("#c2c2c2");
					}
					clearTweetFilterSelection();
					presenter.displayHashtag(anchor.getText());
				}
			});
			hashtagAnchors.add(anchor);
			hashtagPanel.add(anchor);
		}
	}

	@Override
	public void setPresenter(HomePresenter presenter) {
		this.presenter = presenter;
		tweetBox.setPresenter(presenter);
		tview.setPresenter(presenter);
	}

	@Override
	public void clear() {
	}

	public void hideMessage(boolean hide) {
		if (hide) {
			message.setVisible(false);
		} else {
			message.setVisible(true);
		}
	}

	@Override
	public void display(List<TweetDTO> tweets) {
		message.clear();
		hideMessage(true);
		tview.clear();
		tview.displayTweets(tweets);
	}

	@Override
	public void addTweets(List<TweetDTO> tweets) {
		tview.add(tweets);
	}

	@Override
	public void addTweet(TweetDTO tweet) {
		tview.addTweet(tweet);
	}

	@Override
	public void insertTweet(TweetDTO tweet) {
		tview.insertTweet(tweet);
	}
	
	@Override
	public void updateTweets(List<TweetDTO> tweets) {
		tview.add(tweets);
	}

	@Override
	public void updateTweet(TweetDTO tweet) {
		if (tweet == null || tweet.getTweetId() == null) {
			throw new IllegalArgumentException();
		}
		hideMessage(true);
		tview.updateTweet(tweet);
	}

	@Override
	public void updateComment(CommentDTO comment) {
		if (comment == null) {
			throw new IllegalArgumentException();
		}
		hideMessage(true);
		tview.updateComment(comment);
	}

	@Override
	public void updateTweetLike(LoveDTO like) {
		if (like == null) {
			throw new IllegalArgumentException();
		}
		hideMessage(true);
		tview.updateLike(like);
	}

	@Override
	public Element getTweetSectionElement() {
		return communityWallPanel.getElement();
	}

	@Override
	public void displayMessage(String msg, AlertType type) {
		message.setText(msg);
		message.setType(type);
		hideMessage(false);
	}

	@Override
	public void removeTweet(TweetDTO tweet) {
		hideMessage(true);
		tview.remove(tweet);
	}

	@Override
	public void displayMap(GetLatLngResult result) {
		if (result != null) {
			LatLng ll = LatLng.create(result.getLat(), result.getLng());
			communitySummaryWidget.displayMap(ll);
		}
	}
	
	@Override
	public void updateTweetCategoryCount(Map<TweetType, Integer> data) {
		int totalTweetCount = 0;
		for(Map.Entry<TweetType, Anchor> entry : filters.entrySet()) {
			Anchor a = entry.getValue();
			int count = 0;
			if (data.get(entry.getKey()) != null) {
				count = data.get(entry.getKey());
				totalTweetCount += count;
			}
			setCountOnAnchor(a, entry.getKey().name(), count);
		}
		setCountOnAnchor(filters.get(TweetType.ALL), TweetType.ALL.name(), totalTweetCount);
	}

	private void setCountOnAnchor(Anchor a, String name, int count) {
		String text = name +" ("+count+")";
		a.getElement().setInnerHTML(text);
	}

	@Override
	public void displaySummaryData(NeighborhoodDTO neighborhood) {
		communitySummaryWidget.displaySummaryData(neighborhood);
	}
}
