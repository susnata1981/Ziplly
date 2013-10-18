package com.ziplly.app.client.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.ziplly.app.client.activities.HomePresenter;
import com.ziplly.app.client.view.View;
import com.ziplly.app.model.TweetDTO;

public class TweetWidget extends Composite implements View<HomePresenter> {

	private static TweetWidgetUiBinder uiBinder = GWT
			.create(TweetWidgetUiBinder.class);

	interface TweetWidgetUiBinder extends UiBinder<Widget, TweetWidget> {
	}

	@UiField
	Element tweetContent;
	@UiField
	Element timeCreated;
	
	@UiField
	Image authorImage;
	@UiField
	Anchor authorProfileLink;
	@UiField
	Element authorName;
	
	private HomePresenter presenter;
	private TweetDTO tweet;
	
	public TweetWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void displayTweet(TweetDTO tweet) {
		if (tweet != null) { 
			this.tweet = tweet;
			tweetContent.setInnerText(tweet.getContent());
			tweet.getTimeCreated();
			String time = 
					DateTimeFormat.getFormat(PredefinedFormat.DATE_LONG).format(tweet.getTimeCreated());
			timeCreated.setInnerText(time);
			authorImage.setUrl(tweet.getSender().getImageUrl());
			authorName.setInnerText(tweet.getSender().getDisplayName());
		}
	}
	
	@UiHandler("authorProfileLink")
	void displayProfile(ClickEvent event) {
		presenter.displayProfile(tweet.getSender().getAccountId());
	}

	@Override
	public void setPresenter(HomePresenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void clear() {
	}

}
