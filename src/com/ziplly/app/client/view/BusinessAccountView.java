package com.ziplly.app.client.view;

import java.util.List;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Column;
import com.github.gwtbootstrap.client.ui.FluidRow;
import com.github.gwtbootstrap.client.ui.Image;
import com.github.gwtbootstrap.client.ui.Paragraph;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.maps.gwt.client.GoogleMap;
import com.google.maps.gwt.client.LatLng;
import com.google.maps.gwt.client.MapOptions;
import com.google.maps.gwt.client.MapTypeId;
import com.google.maps.gwt.client.Marker;
import com.google.maps.gwt.client.MarkerOptions;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.activities.AccountPresenter;
import com.ziplly.app.client.activities.BusinessAccountActivity.IBusinessAccountView;
import com.ziplly.app.client.activities.TweetPresenter;
import com.ziplly.app.client.places.BusinessAccountSettingsPlace;
import com.ziplly.app.client.view.factory.ValueType;
import com.ziplly.app.client.widget.CssStyleHelper;
import com.ziplly.app.client.widget.NotificationWidget;
import com.ziplly.app.client.widget.PriceRangeWidget;
import com.ziplly.app.client.widget.ProfileStatWidget;
import com.ziplly.app.client.widget.SendMessageWidget;
import com.ziplly.app.client.widget.StyleHelper;
import com.ziplly.app.client.widget.TweetBox;
import com.ziplly.app.model.BusinessAccountDTO;
import com.ziplly.app.model.BusinessPropertiesDTO;
import com.ziplly.app.model.CommentDTO;
import com.ziplly.app.model.LoveDTO;
import com.ziplly.app.model.NeighborhoodDTO;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.model.TweetType;
import com.ziplly.app.shared.GetAccountDetailsResult;
import com.ziplly.app.shared.GetLatLngResult;

public class BusinessAccountView extends AbstractView implements IBusinessAccountView {

	interface BusinessAccountViewUiBinder extends UiBinder<Widget, BusinessAccountView> {
	}

	interface Style extends CssResource {
		String smallfont();
	}

	private static final String TWEET_BOX_WIDTH = "94%";

	private static final String TWEET_WIDGET_WIDTH = "94%";

	private static final String TWEET_WIDGET_HEIGHT = "1100px";
	
	private static BusinessAccountViewUiBinder uiBinder = GWT
			.create(BusinessAccountViewUiBinder.class);

	@UiField
	Style style;
	
	@UiField
	Alert message;
	@UiField
	FluidRow profileSectionRow;
	@UiField
	Image profileImage;
	@UiField
	Button sendMsgBtn;
	@UiField
	HeadingElement name;
	@UiField
	Paragraph description;
	@UiField
	PriceRangeWidget priceRangeWidget;
	@UiField
	HTMLPanel parkingAvailableSpan;
	@UiField
	HTMLPanel wifiAvailableSpan;
	@UiField
	SpanElement lastLoginTime;
	@UiField
	SpanElement businessCategory;
	@UiField
	SpanElement email;
	@UiField
	Anchor emailLink;
	@UiField
	SpanElement websiteSpan;
	@UiField
	Anchor websiteLink;
	
	// Address section
	@UiField
	HTMLPanel addressPanel;
	@UiField
	SpanElement formattedAddress;

	// Hours of operation
	@UiField
	SpanElement mondayStart;
	@UiField
	SpanElement mondayEnd;
	@UiField
	SpanElement tuesdayStart;
	@UiField
	SpanElement tuesdayEnd;
	@UiField
	SpanElement wednesdayStart;
	@UiField
	SpanElement wednesdayEnd;
	@UiField
	SpanElement thursdayStart;
	@UiField
	SpanElement thursdayEnd;
	@UiField
	SpanElement fridayStart;
	@UiField
	SpanElement fridayEnd;
	@UiField
	SpanElement saturdayStart;
	@UiField
	SpanElement saturdayEnd;
	@UiField
	SpanElement sundayStart;
	@UiField
	SpanElement sundayEnd;

	@UiField(provided=true)
	TweetBox tweetBox;
	
	// Account stats
	@UiField
	ProfileStatWidget tweetCountWidget;
	@UiField
	ProfileStatWidget commentCountWidget;
	@UiField
	ProfileStatWidget likeCountWidget;

	@UiField
	DivElement locationDiv;
	
	ITweetView<TweetPresenter> tview = new TweetView();
	SendMessageWidget smw;
	NotificationWidget notificationWidget;
	
	/*
	 * Tweet section
	 */
	@UiField
	HTMLPanel tweetSection;
	@UiField
	HTMLPanel tweetBoxDiv;
	
	@UiField
	Column messageSection;
	
	AccountPresenter<BusinessAccountDTO> presenter;
	BusinessAccountDTO account;
	PopupPanel popupPanel;
	
	@Inject
	public BusinessAccountView(EventBus eventBus) {
		super(eventBus);
		tview.setWidth(TWEET_WIDGET_WIDTH);
		tview.setHeight(TWEET_WIDGET_HEIGHT);
		tweetBox = new TweetBox();
		tweetBox.setWidth(TWEET_BOX_WIDTH);
		tweetBox.setTweetCategory(TweetType.getAllTweetTypeForPublishingByBusiness());
		initWidget(uiBinder.createAndBindUi(this));
		tweetSection.add(tview);
		notificationWidget = new NotificationWidget();
		notificationWidget.getAccountSettingButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (presenter != null) {
					presenter.goTo(new BusinessAccountSettingsPlace());
				}
			}
		});
		notificationWidget.getCloseButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				popupPanel.hide();
				messageSection.remove(popupPanel);
			}
		});
	}

	@Override
	public void displayProfile(BusinessAccountDTO account) {
		if (account == null) {
			throw new IllegalArgumentException();
		}
		message.setVisible(false);
		this.account = account;
		
		// image section
		profileImage.setUrl(accountFormatter.format(account, ValueType.PROFILE_IMAGE_URL));
		profileImage.setAltText(account.getDisplayName());
		
		name.setInnerHTML(account.getDisplayName());

//		description.setText(account.get);
		
		if (account.getCategory() != null) {
			businessCategory.setInnerText(account.getCategory().getName());
		}
		
		email.setInnerText(account.getEmail());
		emailLink.setText(account.getEmail());

		if (account.getWebsite() != null) {
			websiteSpan.setInnerHTML(account.getWebsite());
			websiteLink.setHref(account.getWebsite());
		}
		
		// price range test
		BusinessPropertiesDTO props = account.getProperties();
		
		priceRangeWidget.clear();
		if (props.getPriceRange() != null) {
			priceRangeWidget.setRange(account.getProperties().getPriceRange());
		}
		
		parkingAvailableSpan.clear();
		if (props.isParkingAvailable()) {
			parkingAvailableSpan.add(CssStyleHelper.getIcon(IconType.THUMBS_UP));
		} else {
			parkingAvailableSpan.add(CssStyleHelper.getIcon(IconType.THUMBS_DOWN));
		}

		wifiAvailableSpan.clear();
		if (props.getWifiAvailable()) {
			wifiAvailableSpan.add(CssStyleHelper.getIcon(IconType.THUMBS_UP));
		} else {
			wifiAvailableSpan.add(CssStyleHelper.getIcon(IconType.THUMBS_DOWN));
		}
		
		// last login time shouldn't be null
		if (account.getLastLoginTime() != null) {
			lastLoginTime.setInnerText(basicDataFormatter.format(account.getLastLoginTime(), ValueType.DATE_VALUE_SHORT));
		}

		displayHoursOfOperation();
		
		// display tweets
		tweetBoxDiv.getElement().getStyle().setDisplay(Display.BLOCK);
		displayTweets(account.getTweets());
		
		StyleHelper.show(profileSectionRow.getElement(), true);
	}

	private void displayHoursOfOperation() {
		mondayStart.setInnerText(account.getProperties().getMondayEndTime());
		mondayEnd.setInnerText(account.getProperties().getMondayEndTime());
		
		tuesdayStart.setInnerText(account.getProperties().getTuesdayStartTime());
		tuesdayEnd.setInnerText(account.getProperties().getTuesdayEndTime());
		
		wednesdayStart.setInnerText(account.getProperties().getWednesdayStartTime());
		wednesdayEnd.setInnerText(account.getProperties().getWednesdayEndTime());
		
		thursdayStart.setInnerText(account.getProperties().getThursdayStartTime());
		thursdayEnd.setInnerText(account.getProperties().getThursdayEndTime());
		
		fridayStart.setInnerText(account.getProperties().getFridayStartTime());
		fridayEnd.setInnerText(account.getProperties().getFridayEndTime());
		
		saturdayStart.setInnerText(account.getProperties().getSaturdayStartTime());
		saturdayEnd.setInnerText(account.getProperties().getSaturdayEndTime());
		
		sundayStart.setInnerText(account.getProperties().getSundayStartTime());
		sundayEnd.setInnerText(account.getProperties().getSundayEndTime());
	}

	@Override
	public void displayFormattedAddress(String fAddress) {
		if (account != null) {
			formattedAddress.setInnerHTML(fAddress);
		}
	}

	@Override
	public void displayLocationInMap(GetLatLngResult input) {
		LatLng myLatLng = LatLng.create(input.getLat(), input.getLng());
	    MapOptions myOptions = MapOptions.create();
	    myOptions.setZoom(10.0);
	    myOptions.setCenter(myLatLng);
	    myOptions.setMapMaker(true);
	    myOptions.setMapTypeId(MapTypeId.ROADMAP);

	    GoogleMap map = GoogleMap.create(locationDiv, myOptions);
		MarkerOptions markerOpts = MarkerOptions.create();
        markerOpts.setMap(map);
        markerOpts.setPosition(myLatLng);
        Marker.create(markerOpts);
	}

	@Override
	public void displayTweets(List<TweetDTO> tweets) {
		tview.displayTweets(tweets);
	}

	@Override
	public void displayTweets(List<TweetDTO> tweets, boolean displayNoTweetsMessage) {
		tview.displayTweets(tweets, displayNoTweetsMessage);
	}
	
	@Override
	public void displayPublicProfile(BusinessAccountDTO account) {
		displayProfile(account);
		tweetBoxDiv.getElement().getStyle().setDisplay(Display.NONE);
	}

	@Override
	public void clear() {
//		this.account = null;
		clearTweet();
	}

	public void onCloseSendMessageWidgetClick(ClickEvent event) {
		closeMessageWidget();
	}
	
	@Override
	public void closeMessageWidget() {
		smw.hide();
	}

	@UiHandler("sendMsgBtn")
	public void onSendMessageWidgetClick(ClickEvent event) {
		openMessageWidget();
	}
	
	@Override
	public void openMessageWidget() {
		smw = new SendMessageWidget(account);
		smw.setPresenter(presenter);
		smw.show();
	}

	@Override
	public void setPresenter(AccountPresenter<BusinessAccountDTO> presenter) {
		this.presenter = presenter;
		tweetBox.setPresenter(presenter);
		tview.setPresenter(presenter);
	}

	@Override
	public void displayMessage(String msg, AlertType type) {
		message.setText(msg);
		message.setType(type);
		message.setVisible(true);
	}
	
	@Override
	public Element getTweetSectionElement() {
		return tweetSection.getElement();
	}

	@Override
	public void addTweets(List<TweetDTO> tweets) {
		tview.add(tweets);
	}

	@Override
	public void updateAccountDetails(ApplicationContext ctx) {
		if (ctx != null) {
			tweetCountWidget.setValue(new Integer(ctx.getTotalTweets()).toString());
			commentCountWidget.setValue(new Integer(ctx.getTotalComments()).toString());
			likeCountWidget.setValue(new Integer(ctx.getTotalLikes()).toString());
		}
	}

	@Override
	public void updatePublicAccountDetails(GetAccountDetailsResult result) {
		if (result != null) {
			tweetCountWidget.setValue(new Integer(result.getTotalTweets()).toString());
			commentCountWidget.setValue(new Integer(result.getTotalComments()).toString());
			likeCountWidget.setValue(new Integer(result.getTotalLikes()).toString());
		}
	}
	
	@Override
	public void updateComment(CommentDTO comment) {
		tview.addComment(comment);
	}

	@Override
	public void addComment(CommentDTO comment) {
		tview.addComment(comment);
	}
	
	@Override
	public void updateTweetLike(LoveDTO like) {
		tview.updateLike(like);
	}

	@Override
	public void updateTweet(TweetDTO tweet) {
		tview.updateTweet(tweet);
	}

	@Override
	public void clearTweet() {
		tview.clear();
	}

	@Override
	public void removeTweet(TweetDTO tweet) {
		tview.remove(tweet);
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
		tweetBox.previewImage(imageUrl);
	}

	@Override
	public void resetImageUploadUrl() {
		tweetBox.resetImageUploadUrl();
	}
	
	@Override
	public void displayTweetViewMessage(String msg, AlertType type) {
		tview.displayMessage(msg, type);
	}
	
	@Override
	public void displayNotificationWidget(boolean show) {
		if (show) {
			popupPanel = new PopupPanel();
			popupPanel.setWidget(notificationWidget);
			popupPanel.setPopupPosition(Window.getClientWidth()-270,70);
			messageSection.add(popupPanel);
			popupPanel.show();
		} else {
			if (popupPanel != null) {
				popupPanel.hide();
				messageSection.remove(popupPanel);
			}
		}
	}

	@Override
	public void hideProfileSection() {
		StyleHelper.show(profileSectionRow.getElement(), false);
	}

	@Override
	public void displayTargetNeighborhoods(List<NeighborhoodDTO> targetNeighborhoodList) {
		tweetBox.initializeTargetNeighborhood(targetNeighborhoodList);
	}
}
