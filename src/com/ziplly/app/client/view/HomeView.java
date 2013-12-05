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
import com.ziplly.app.client.activities.HomeActivity.IHomeView;
import com.ziplly.app.client.activities.TweetPresenter;
import com.ziplly.app.client.widget.ITweetWidgetView;
import com.ziplly.app.client.widget.TweetBox;
import com.ziplly.app.client.widget.TweetWidget;
import com.ziplly.app.model.CommentDTO;
import com.ziplly.app.model.LoveDTO;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.model.TweetType;

/**
 * Community Wall View
 */
public class HomeView extends Composite implements IHomeView {

	private String tweetWidth = "55%";
	private static HomeViewUiBinder uiBinder = GWT
			.create(HomeViewUiBinder.class);
	
	public static interface HomePresenter extends TweetPresenter {
		void displayTweets(List<TweetDTO> tweets);
		void displayTweetsForCategory(TweetType type);
		void onLogin(String emailInput, String passwordInput);
	}
	
	@UiField
	Alert message;
	
	@UiField
	HTMLPanel filterTweetsPanel;
	
	@UiField
	HTMLPanel communityWallPanel;
	
	TweetBox tweetBox;
	
	HomePresenter presenter;
	List<Anchor> filters = new ArrayList<Anchor>();
	
	// tweetId --> TweetWidget
	Map<Long, ITweetWidgetView<?>> tweetWidgetMap = new HashMap<Long, ITweetWidgetView<?>>();
	
	interface HomeViewUiBinder extends UiBinder<Widget, HomeView> {
	}
	
	public HomeView() {
		initWidget(uiBinder.createAndBindUi(this));
		buildTweetFilters();
		message.setAnimation(true);
	}

	private void buildTweetFilters() {
		for(final TweetType type : TweetType.values()) {
			final Anchor anchor = new Anchor(type.name().toLowerCase());
			anchor.setStyleName("tweetFilterLink");
			anchor.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					for(Anchor a : filters) {
						a.getElement().getStyle().setBackgroundColor("");
					}
					anchor.getElement().getStyle().setBackgroundColor("#c2c2c2");
					presenter.displayTweetsForCategory(type);
				}
			});
			filterTweetsPanel.add(anchor);
			filters.add(anchor);
		}
	}

	@Override
	public void setPresenter(HomePresenter presenter) {
		this.presenter = presenter;
//		tweetBox.setPresenter(presenter);
	}

	@Override
	public void clear() {
	}

	ITweetView<TweetPresenter> tview = new TweetView();

	@Override
	public void display(List<TweetDTO> tweets) {
//		long time1 = System.currentTimeMillis();
		message.clear();
		message.setVisible(false);
		communityWallPanel.clear();
		tweetBox = new TweetBox();
		tweetBox.setTweetCategory(TweetType.getAllTweetTypeForPublishingByUser());
		tweetBox.setWidth(tweetWidth);
		tweetBox.getElement().getStyle().setMarginLeft(1.2, Unit.PCT);
		tweetBox.setPresenter(presenter);
		communityWallPanel.add(tweetBox);
		
//		for(TweetDTO tweet: tweets) {
//			long time11 = System.currentTimeMillis();
//			TweetWidget tw = new TweetWidget();
//			tw.setWidth(tweetWidth);
//			long time12 = System.currentTimeMillis();
//			tw.setPresenter(presenter);
//			tw.displayTweet(tweet);
//			long time13 = System.currentTimeMillis();
//			communityWallPanel.add(tw);
//			long time14 = System.currentTimeMillis();
//			
//			System.out.println("Time to create tweet widget: "+(time12-time11)+" to render: "+(time13-time12)+" to addToPanel: "+(time14-time13));
//			tweetWidgetMap.put(tweet.getTweetId(), tw);
//		}
		tview.setWidth("55%");
		tview.add(tweets);
		communityWallPanel.add(tview);
	}
	
	@Override
	public void addTweet(TweetDTO tweet) {
		tview.addTweet(tweet);
	}
	
	@Override
	public void updateTweets(List<TweetDTO> tweets) {
		long time1 = System.currentTimeMillis();
		for(TweetDTO tweet: tweets) {
			long time11 = System.currentTimeMillis();
			TweetWidget tw = new TweetWidget();
			tw.setWidth(tweetWidth);
			long time12 = System.currentTimeMillis();
			tw.setPresenter(presenter);
			tw.displayTweet(tweet);
			long time13 = System.currentTimeMillis();
			communityWallPanel.add(tw);
			long time14 = System.currentTimeMillis();
			
			System.out.println("Time to create tweet widget: "+(time12-time11)+" to render: "+(time13-time12)+" to addToPanel: "+(time14-time13));
			tweetWidgetMap.put(tweet.getTweetId(), tw);
		}
		
		long time2 = System.currentTimeMillis();
		System.out.println("Time to render wall: "+(time2-time1));
	}
	
	@Override
	public void updateTweet(TweetDTO tweet) {
		if (tweet == null || tweet.getTweetId() == null) {
			throw new IllegalArgumentException();
		}
		
		ITweetWidgetView<?> tw = tweetWidgetMap.get(tweet.getTweetId());
		if (tw == null) {
			// do nothing
			return;
		}
		tw.displayTweet(tweet);
	}

	@Override
	public void updateComment(CommentDTO comment) {
		if (comment == null) {
			throw new IllegalArgumentException();
		}
		
		ITweetWidgetView<?> tw = tweetWidgetMap.get(comment.getTweet().getTweetId());
		tw.updateComment(comment);
	}

	@Override
	public void updateTweetLike(LoveDTO like) {
		if (like == null) {
			throw new IllegalArgumentException();
		}
		
		ITweetWidgetView<?> tw = tweetWidgetMap.get(like.getTweet().getTweetId());
		tw.updateLike(like);
	}

	@Override
	public Element getTweetSectionElement() {
		return communityWallPanel.getElement();
	}

	@Override
	public void displayMessage(String msg, AlertType type) {
		message.setText(msg);
		message.setType(type);
		message.setVisible(true);
	}

	@Override
	public void removeTweet(TweetDTO tweet) {
		ITweetWidgetView<?> widget = tweetWidgetMap.get(tweet.getTweetId());
		widget.remove();
	}
}
