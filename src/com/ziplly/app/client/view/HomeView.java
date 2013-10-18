package com.ziplly.app.client.view;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.ziplly.app.client.activities.HomePresenter;
import com.ziplly.app.client.widget.TweetWidget;
import com.ziplly.app.model.TweetDTO;

public class HomeView extends Composite implements View<HomePresenter> {

	private static HomeViewUiBinder uiBinder = GWT
			.create(HomeViewUiBinder.class);
	HomePresenter presenter;
	
	interface HomeViewUiBinder extends UiBinder<Widget, HomeView> {
	}

	public HomeView() {
//		cww = new CommunityWallWidget(null, null);
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField
	HTMLPanel tweetFilterPanel;
	
	@UiField
	HTMLPanel communityWallPanel;
	
//	@UiField(provided=true)
//	CommunityWallWidget cww;

	@Override
	public void setPresenter(HomePresenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void clear() {
	}

	public void display(List<TweetDTO> tweets) {
		for(TweetDTO tweet: tweets) {
//			HTMLPanel html = new HTMLPanel("<p>"+t.getContent()+"</p>");
			TweetWidget tw = new TweetWidget();
			tw.setPresenter(presenter);
			tw.displayTweet(tweet);
			communityWallPanel.add(tw);
		}
	}
}
