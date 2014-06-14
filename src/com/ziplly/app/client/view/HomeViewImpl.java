package com.ziplly.app.client.view;

import java.util.ArrayList;
import java.util.Collections;
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
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.ziplly.app.client.activities.HomeActivity.HomeView;
import com.ziplly.app.client.activities.Presenter;
import com.ziplly.app.client.activities.TweetPresenter;
import com.ziplly.app.client.places.ConversationPlace;
import com.ziplly.app.client.places.HomePlace;
import com.ziplly.app.client.view.event.LoadingEventEnd;
import com.ziplly.app.client.view.factory.ValueType;
import com.ziplly.app.client.widget.AlertModal;
import com.ziplly.app.client.widget.CommunitySummaryWidget;
import com.ziplly.app.client.widget.FeedbackWidget;
import com.ziplly.app.client.widget.ProfileListWidget;
import com.ziplly.app.client.widget.StyleHelper;
import com.ziplly.app.client.widget.TweetBox;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.CommentDTO;
import com.ziplly.app.model.HashtagDTO;
import com.ziplly.app.model.LoveDTO;
import com.ziplly.app.model.NeighborhoodDTO;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.model.TweetType;
import com.ziplly.app.shared.GetNeighborhoodDetailsResult;
import com.ziplly.app.shared.ValidationResult;

/**
 * Community View
 */
public class HomeViewImpl extends AbstractView implements HomeView {
	
	public static interface HomePresenter extends Presenter {
		void displayCommunityWallForNeighborhood(NeighborhoodDTO neighborhood);

		void displayHashtag(String text);

		void displayTweets(List<TweetDTO> tweets);

		void gotoBusinessPlace();

		void gotoResidentPlace();

		void sendFeedback(String content);
	}
	
	public interface Style extends CssResource {
		String selectedTweetType();

		String tweetFilterLink();
	}

	interface HomeViewUiBinder extends UiBinder<Widget, HomeViewImpl> {
	}
	
	private static final String TWEET_WIDGET_WIDTH = "91%";

	private String tweetWidth = "91%";

	private static HomeViewUiBinder uiBinder = GWT.create(HomeViewUiBinder.class);

	private FeedbackWidget feedbackWidget = new FeedbackWidget();

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

	@UiField(provided = true)
	TweetBox tweetBox;
	@UiField
	NavLink feedbackLink;
	@UiField
	Container communitySummaryContainer;

	@UiField
	CommunitySummaryWidget communitySummaryWidget;

	@UiField
	HTMLPanel neighborhoodsPanel;
	
	@UiField
	Container newMembersListContainer;
	
	@UiField(provided = true)
	ProfileListWidget profileListWidget;
	HomePresenter presenter;

	ITweetView<TweetPresenter> tview = new TweetView();
	
	// Tweet filters
	Map<TweetType, Anchor> filters = new HashMap<TweetType, Anchor>();

	// Hashtags
	List<Anchor> hashtagAnchors = new ArrayList<Anchor>();

	@Inject
	public HomeViewImpl(EventBus eventBus) {
		super(eventBus);
    System.out.println("HOMEVIEW BUS="+eventBus);
		tweetBox = new TweetBox(eventBus);
		profileListWidget = new ProfileListWidget(eventBus);
		initWidget(uiBinder.createAndBindUi(this));
		setupUi();
		setupHandlers();
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
	public void addTweet(TweetDTO tweet) {
		tview.addTweet(tweet);
	}

	@Override
	public void addTweets(List<TweetDTO> tweets) {
		tview.add(tweets);
	}

	@Override
	public void addUploadFormHandler(SubmitCompleteHandler submitCompleteHandler) {
		tweetBox.addUploadFormHandler(submitCompleteHandler);
	}

	@Override
	public void clear() {
	}

	@Override
	public void display(List<TweetDTO> tweets) {
		message.clear();
		hideMessage(true);
		tview.clear();

		if (tweets.size() == 0) {
			eventBus.fireEvent(new LoadingEventEnd());
			return;
		}

		tview.displayTweets(tweets, new TweetViewDisplayStatusCallback() {

			@Override
			public void hasFinished(double y) {
				if (y == 100) {
					eventBus.fireEvent(new LoadingEventEnd());
				}
			}
		});
	}

	@Override
	public void displayCommunitySummaryDetails(GetNeighborhoodDetailsResult result) {
		communitySummaryWidget.setResidentCount(result.getTotalResidents());
		communitySummaryWidget.setBusinessCount(result.getTotalBusinesses());
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
	public void displayMap(String address) {
			communitySummaryWidget.displayMap(address);
	}

	@Override
	public void displayMessage(String msg, AlertType type) {
		internalDisplayModalMessage(msg, type);
	}

	@Override
	public void displayNeighborhoodImage(NeighborhoodDTO neighborhood) {
		StyleHelper.setBackgroundImage(basicDataFormatter.format(
		    neighborhood,
		    ValueType.NEIGHBORHOOD_IMAGE));
	}

	@Override
	public void displayNewMembers(List<AccountDTO> accounts) {
		if (accounts.size() == 0) {
			profileListWidget.displayMessage(stringDefinitions.noNewMemberJoined(), AlertType.INFO);
			return;
		}
		
		profileListWidget.displayProfiles(accounts);
	}

	@Override
	public void displayProfileImagePreview(String imageUrl) {
		if (imageUrl != null) {
			tweetBox.previewImage(imageUrl);
		}
	}

	@Override
	public void displaySummaryData(NeighborhoodDTO neighborhood) {
		communitySummaryWidget.displaySummaryData(neighborhood);
	}

	@Override
	public void displayTargetNeighborhoods(List<NeighborhoodDTO> targetNeighborhoodList) {
		neighborhoodsPanel.clear();
		tweetBox.initializeTargetNeighborhood(targetNeighborhoodList);

		// Display neighborhood list in reverse (parent first)
		Collections.reverse(targetNeighborhoodList);
		int margin = 0;
		for (final NeighborhoodDTO neighborhood : targetNeighborhoodList) {
			final Anchor anchor = new Anchor(neighborhood.getName());
			anchor.setStyleName(style.tweetFilterLink());
			anchor.getElement().getStyle().setMarginLeft(margin, Unit.PX);
			margin += 20;
			anchor.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					presenter.displayCommunityWallForNeighborhood(neighborhood);
				}
			});
			neighborhoodsPanel.add(anchor);
		}
	}

	@UiHandler("feedbackLink")
	public void feedbackLinkClicked(ClickEvent event) {
		feedbackWidget.show(true);
	}

	@Override
	public Element getTweetSectionElement() {
		return tview.getTweetSection();
	}

	@Override
  public ITweetView<TweetPresenter> getTweetView() {
	  return tview;
  }

	@Override
	public TweetBox getTweetWidget() {
		return tweetBox;
	}

	public void hideMessage(boolean hide) {
		if (hide) {
			message.setVisible(false);
		} else {
			message.setVisible(true);
		}
	}

	@Override
	public void highlightTweetType(TweetType type) {
		clearHighlightOnTweetType();
		filters.get(type).addStyleName(style.selectedTweetType());
	}

	@Override
	public void insertTweet(TweetDTO tweet) {
		tview.insertTweet(tweet);
	}

	@UiHandler("messagesLink")
	public void onMessageLinkClick(ClickEvent event) {
		presenter.goTo(new ConversationPlace());
	}

	@Override
	public void removeTweet(TweetDTO tweet) {
		hideMessage(true);
		tview.remove(tweet);
	}

	@Override
	public void resetImageUploadUrl() {
		tweetBox.resetImageUploadUrl();
	}

	@Override
	public void resizeMap() {
		communitySummaryWidget.resize();
	}

	@Override
	public void setImageUploadUrl(String imageUrl) {
		tweetBox.setImageUploadUrl(imageUrl);
	}
	
	@Override
	public void setPresenter(HomePresenter presenter) {
		this.presenter = presenter;
		profileListWidget.setPresenter(presenter);
	}

	@Override
	public void setUnreadMessageCount(Long count) {
		unreadMessageCountField.setInnerText(basicDataFormatter.format(
		    count,
		    ValueType.UNREAD_MESSAGE_COUNT));
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
	public void updateTweet(TweetDTO tweet) {
		if (tweet == null || tweet.getTweetId() == null) {
			throw new IllegalArgumentException();
		}
		hideMessage(true);
		tview.updateTweet(tweet);
	}

	@Override
	public void updateTweetCategoryCount(Map<TweetType, Integer> data) {
		int totalTweetCount = 0;
		for (Map.Entry<TweetType, Anchor> entry : filters.entrySet()) {
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
	public void updateTweetLike(LoveDTO like) {
		if (like == null) {
			throw new IllegalArgumentException();
		}
		hideMessage(true);
		tview.updateLike(like);
	}

	@Override
	public void updateTweets(List<TweetDTO> tweets) {
		tview.add(tweets);
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
					HomePlace place = new HomePlace(type);
					presenter.goTo(place);
				}
			});
			filterTweetsPanel.add(anchor);
			filters.put(type, anchor);
		}
	}

	private void clearAnchorSelection(Anchor a) {
		a.getElement().getStyle().setBackgroundColor("");
	}

	private void clearHighlightOnTweetType() {
		for (TweetType type : filters.keySet()) {
			filters.get(type).removeStyleName(style.selectedTweetType());
		}
	}

	private void clearTagSelection() {
		for (Anchor a : hashtagAnchors) {
			clearAnchorSelection(a);
		}
	}

	private void clearTweetFilterSelection() {
		for (Anchor a : filters.values()) {
			clearAnchorSelection(a);
		}
	}

	private void internalDisplayModalMessage(String msg, AlertType type) {
		AlertModal modal = new AlertModal();
		modal.showMessage(msg, type);
  }

	private void setCountOnAnchor(Anchor a, String name, int count) {
		String text = name + " (" + count + ")";
		a.getElement().setInnerHTML(text);
	}

	private void setupHandlers() {
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
					feedbackWidget.displayMessage(
					    result.getErrors().get(0).getErrorMessage(),
					    AlertType.ERROR);
					return;
				}
				presenter.sendFeedback(feedbackWidget.getContent());
				feedbackWidget.show(false);
			}
		});
  }
	
	private void setupUi() {
		buildTweetFilters();
		message.setAnimation(true);

		StyleHelper.show(message.getElement(), false);

		tweetBox.setTweetCategory(TweetType.getAllTweetTypeForPublishingByUser());
		tweetBox.setWidth(tweetWidth);

		tview.setWidth(TWEET_WIDGET_WIDTH);

		communitySummaryContainer.setShowOn(Device.DESKTOP);
		newMembersListContainer.setShowOn(Device.DESKTOP);
		communityWallPanel.add(tview);
		communitySummaryWidget.setHeight("270px");
  }

	@Override
  public void refreshTweet(TweetDTO tweet) {
		tview.refreshTweet(tweet);
  }
}
