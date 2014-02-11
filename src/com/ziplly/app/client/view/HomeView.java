package com.ziplly.app.client.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Container;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.github.gwtbootstrap.client.ui.constants.Device;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.maps.gwt.client.LatLng;
import com.ziplly.app.client.activities.HomeActivity.IHomeView;
import com.ziplly.app.client.activities.SendMessagePresenter;
import com.ziplly.app.client.activities.TweetPresenter;
import com.ziplly.app.client.places.BusinessPlace;
import com.ziplly.app.client.places.ConversationPlace;
import com.ziplly.app.client.view.factory.AbstractValueFormatterFactory;
import com.ziplly.app.client.view.factory.BasicDataFormatter;
import com.ziplly.app.client.view.factory.ValueFamilyType;
import com.ziplly.app.client.view.factory.ValueType;
import com.ziplly.app.client.widget.CommunitySummaryWidget;
import com.ziplly.app.client.widget.FeedbackWidget;
import com.ziplly.app.client.widget.StyleHelper;
import com.ziplly.app.client.widget.TweetBox;
import com.ziplly.app.model.CommentDTO;
import com.ziplly.app.model.HashtagDTO;
import com.ziplly.app.model.LoveDTO;
import com.ziplly.app.model.NeighborhoodDTO;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.model.TweetType;
import com.ziplly.app.shared.GetLatLngResult;
import com.ziplly.app.shared.GetNeighborhoodDetailsResult;
import com.ziplly.app.shared.ValidationResult;

/**
 * Community Wall View
 */
public class HomeView extends Composite implements IHomeView {

	private static final String TWEET_WIDGET_WIDTH = "80%";
	private String tweetWidth = "80%";
	private BasicDataFormatter basicDataFormatter = (BasicDataFormatter) AbstractValueFormatterFactory.getValueFamilyFormatter(ValueFamilyType.BASIC_DATA_VALUE);
	private static HomeViewUiBinder uiBinder = GWT.create(HomeViewUiBinder.class);
	private FeedbackWidget feedbackWidget = new FeedbackWidget();
	
	public interface Style extends CssResource {
		String tweetFilterLink();

		String selectedTweetType();
	}
	
	public static interface HomePresenter extends TweetPresenter, SendMessagePresenter {
		void displayTweets(List<TweetDTO> tweets);

		void displayTweetsForCategory(TweetType type);

		void displayHashtag(String text);

		void sendFeedback(String content);

		void displayCommunityWallForNeighborhood(NeighborhoodDTO neighborhood);

		void gotoResidentPlace();

		void gotoBusinessPlace(); 
	}

	@UiField
	Style style;
	
	@UiField
	Alert message;

	@UiField
	HTMLPanel hashtagPanel;
	@UiField
	HTMLPanel filterTweetsPanel;

	@UiField
	Anchor messagesLink;
	@UiField
	SpanElement unreadMessageCountField;
	
	@UiField
	HTMLPanel communityWallPanel;

	@UiField
	TweetBox tweetBox;

	@UiField
	NavLink feedbackLink;
	@UiField
	Container communitySummaryContainer;
	@UiField
	CommunitySummaryWidget communitySummaryWidget;

	@UiField
	HTMLPanel neighborhoodsPanel;
	
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
		StyleHelper.show(message.getElement(), false);
		
		tweetBox.setTweetCategory(TweetType.getAllTweetTypeForPublishingByUser());
		tweetBox.setWidth(tweetWidth);
		tweetBox.setPresenter(presenter);
		
		tview.setWidth(TWEET_WIDGET_WIDTH);
		
		communitySummaryContainer.setShowOn(Device.DESKTOP);
		communityWallPanel.add(tview);
		communitySummaryWidget.setHeight("270px");
		communitySummaryWidget.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				presenter.gotoResidentPlace();
			}
		});
		
		communitySummaryWidget.addClickHandlerForBusinessLink(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				presenter.gotoBusinessPlace();
			}
		});
		
		feedbackWidget.getSubmitButton().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				feedbackWidget.clear();
				ValidationResult result = feedbackWidget.validate();
				if (!result.isValid()) {
					feedbackWidget.displayMessage(result.getErrors().get(0).getErrorMessage(), AlertType.ERROR);
					return;
				}
				presenter.sendFeedback(feedbackWidget.getContent());
				feedbackWidget.show(false);
			}
		});
	}

	private void buildTweetFilters() {
		for (final TweetType type : TweetType.values()) {
			final Anchor anchor = new Anchor();
			String name = type.getTweetName();
			anchor.setText(name);
			anchor.setStyleName(style.tweetFilterLink());
			anchor.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					clearTweetFilterSelection();
					clearTagSelection();
					highlightTweetType(type);
					presenter.displayTweetsForCategory(type);
				}
			});
			filterTweetsPanel.add(anchor);
			filters.put(type, anchor);
		}
	}

	private void clearHighlightOnTweetType() {
		for(TweetType type : filters.keySet()) {
			filters.get(type).removeStyleName(style.selectedTweetType());
		}
	}
	
	@Override
	public void highlightTweetType(TweetType type) {
		clearHighlightOnTweetType();
		filters.get(type).addStyleName(style.selectedTweetType());
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
		tview.displayTweets(tweets, false);
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
	public void addComment(CommentDTO comment) {
		if (comment == null) {
			throw new IllegalArgumentException();
		}
		hideMessage(true);
		tview.addComment(comment);
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
//		return communityWallPanel.getElement();
		return tview.getTweetSection();
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
			setCountOnAnchor(a, entry.getKey().getTweetName(), count);
		}
		setCountOnAnchor(filters.get(TweetType.ALL), TweetType.ALL.getTweetName(), totalTweetCount);
	}
	
	@Override
	public void setUnreadMessageCount(Long count) {
		unreadMessageCountField.setInnerText(basicDataFormatter.format(count, ValueType.UNREAD_MESSAGE_COUNT));
	}

	@UiHandler("messagesLink")
	public void onMessageLinkClick(ClickEvent event) {
		presenter.goTo(new ConversationPlace());
	}
	
	private void setCountOnAnchor(Anchor a, String name, int count) {
		String text = name +" ("+count+")";
		a.getElement().setInnerHTML(text);
	}

	@Override
	public void displaySummaryData(NeighborhoodDTO neighborhood) {
		communitySummaryWidget.displaySummaryData(neighborhood);
	}

	@Override
	public void displayCommunitySummaryDetails(GetNeighborhoodDetailsResult result) {
		communitySummaryWidget.setResidentCount(result.getTotalResidents());
		communitySummaryWidget.setBusinessCount(result.getTotalBusinesses());
	}
	
	@Override
	public void setImageUploadUrl(String imageUrl) {
		tweetBox.setImageUploadUrl(imageUrl);
	}

	@Override
	public void addUploadFormHandler(SubmitCompleteHandler submitCompleteHandler) {
		tweetBox.addUploadFormHandler(submitCompleteHandler);
	}

	@Override
	public void displayProfileImagePreview(String imageUrl) {
		if (imageUrl != null) {
			tweetBox.previewImage(imageUrl);
		}
	}

	@Override
	public void resetImageUploadUrl() {
		tweetBox.resetImageUploadUrl();
	}
	
	@UiHandler("feedbackLink")
	public void feedbackLinkClicked(ClickEvent event) {
		feedbackWidget.show(true);
	}

	@Override
	public void displayTargetNeighborhoods(List<NeighborhoodDTO> targetNeighborhoodList) {
		tweetBox.initializeTargetNeighborhood(targetNeighborhoodList);
		neighborhoodsPanel.clear();
		for (final NeighborhoodDTO neighborhood : targetNeighborhoodList) {
			final Anchor anchor = new Anchor(neighborhood.getName());
			anchor.setStyleName(style.tweetFilterLink());
			anchor.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
//					presenter.getCommunityPanelDataForNeighborhood(neighborhood);
					presenter.displayCommunityWallForNeighborhood(neighborhood);
				}
			});
			neighborhoodsPanel.add(anchor);
		}

	}
}
