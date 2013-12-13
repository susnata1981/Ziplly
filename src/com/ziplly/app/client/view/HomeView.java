package com.ziplly.app.client.view;

import java.util.ArrayList;
import java.util.List;

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
import com.ziplly.app.client.activities.TweetPresenter;
import com.ziplly.app.client.widget.CommunitySummaryWidget;
import com.ziplly.app.client.widget.TweetBox;
import com.ziplly.app.model.CommentDTO;
import com.ziplly.app.model.HashtagDTO;
import com.ziplly.app.model.LoveDTO;
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

	public static interface HomePresenter extends TweetPresenter {
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
	List<Anchor> filters = new ArrayList<Anchor>();
	ArrayList<Anchor> hashtagAnchors = new ArrayList<Anchor>();
	ITweetView<TweetPresenter> tview = new TweetView();

	interface HomeViewUiBinder extends UiBinder<Widget, HomeView> {
	}

	public HomeView() {
		initWidget(uiBinder.createAndBindUi(this));
		buildTweetFilters();
		message.setAnimation(true);
		// tweetBox = new TweetBox();
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
			final Anchor anchor = new Anchor(type.name().toLowerCase());
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
			filterTweetsPanel.add(anchor);
			filters.add(anchor);
		}
	}

	private void clearTweetFilterSelection() {
		for (Anchor a : filters) {
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
		for (HashtagDTO h : hashtags) {
			final Anchor anchor = new Anchor(h.getTag());
			anchor.setStyleName("tweetFilterLink");
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

}
