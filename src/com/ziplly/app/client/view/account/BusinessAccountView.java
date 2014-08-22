package com.ziplly.app.client.view.account;

import java.util.List;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Column;
import com.github.gwtbootstrap.client.ui.FluidContainer;
import com.github.gwtbootstrap.client.ui.FluidRow;
import com.github.gwtbootstrap.client.ui.Image;
import com.github.gwtbootstrap.client.ui.Paragraph;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
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
import com.ziplly.app.client.places.BusinessAccountSettingsPlace;
import com.ziplly.app.client.view.AbstractBaseAccountView;
import com.ziplly.app.client.view.PendingActionTypes;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.client.view.TopLevelView;
import com.ziplly.app.client.view.common.RenderingStatusCallback;
import com.ziplly.app.client.view.common.TweetBoxPresenter;
import com.ziplly.app.client.view.common.TweetListView;
import com.ziplly.app.client.view.common.TweetWidgetPresenter;
import com.ziplly.app.client.view.coupon.CouponTransactionView;
import com.ziplly.app.client.view.event.LoadingEventEnd;
import com.ziplly.app.client.view.factory.ValueType;
import com.ziplly.app.client.widget.AlertModal;
import com.ziplly.app.client.widget.CssStyleHelper;
import com.ziplly.app.client.widget.EmailWidget;
import com.ziplly.app.client.widget.GoogleMapWidget;
import com.ziplly.app.client.widget.NotificationWidget;
import com.ziplly.app.client.widget.PriceRangeWidget;
import com.ziplly.app.client.widget.ProfileStatWidget;
import com.ziplly.app.client.widget.SendMessageWidget;
import com.ziplly.app.client.widget.StyleHelper;
import com.ziplly.app.client.widget.TweetBox;
import com.ziplly.app.model.BusinessAccountDTO;
import com.ziplly.app.model.BusinessPropertiesDTO;
import com.ziplly.app.model.CommentDTO;
import com.ziplly.app.model.CouponItemDTO;
import com.ziplly.app.model.LocationDTO;
import com.ziplly.app.model.LocationType;
import com.ziplly.app.model.LoveDTO;
import com.ziplly.app.model.NeighborhoodDTO;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.model.TweetType;
import com.ziplly.app.shared.GetAccountDetailsResult;
import com.ziplly.app.shared.GetLatLngResult;

public class BusinessAccountView extends AbstractBaseAccountView implements IAccountView<BusinessAccountDTO>, TopLevelView {

	interface BusinessAccountViewUiBinder extends UiBinder<Widget, BusinessAccountView> {
	}

	interface Style extends CssResource {
		String smallfont();
		
		String accountUpdateSmallfont();

    String paddingLeft();
	}

	private static final String TWEET_BOX_WIDTH = "97%";
	private static final String TWEET_WIDGET_WIDTH = "94%";
	private static final String TWEET_WIDGET_HEIGHT = "1400px";

	private static BusinessAccountViewUiBinder uiBinder = GWT
	    .create(BusinessAccountViewUiBinder.class);

	private RenderingStatusCallback DEFAULT_RENDERING_STATUS_CALLBACK = new RenderingStatusCallback() {

    @Override
    public void finished(double y) {
      if (y == 100) {
        eventBus.fireEvent(new LoadingEventEnd());
      }
    }
    
  };
  
	@UiField
	Style style;

	@UiField
	FluidContainer container;
	@UiField
	Alert message;
	@UiField
	HTMLPanel profileImagePanel;
	@UiField
	FluidRow profileSectionRow;
	@UiField
	Image profileImage;
	@UiField
	Button sendMsgBtn;
	@UiField
	HeadingElement name;
	@UiField
	HeadingElement businessName;
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
	SpanElement phoneSpan;
	@UiField
	SpanElement websiteSpan;
	@UiField
	Anchor websiteLink;

	// Address section
	@UiField
	HTMLPanel addressPanel;
	@UiField
	SpanElement neighborhoodName;
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

	@UiField(provided = true)
	TweetBox tweetBox;

	@UiField
	HTMLPanel accountUpdatePanel;
	
	// Account stats
	@UiField
	ProfileStatWidget tweetCountWidget;
	@UiField
	ProfileStatWidget commentCountWidget;
	@UiField
	ProfileStatWidget likeCountWidget;

	@UiField
	DivElement locationDiv;

	// Tweet section
	@UiField
	HTMLPanel tweetSection;
	@UiField
	HTMLPanel tweetBoxDiv;

	/*
	 * Coupon transaction section
	 */
	@UiField
	HTMLPanel couponTransactionPanel;
	@UiField(provided = true)
	CouponTransactionView couponTransactionView;
	
	@UiField
	Column messageSection;
	
	private final TweetWidgetPresenter tweetWidgetPresenter;
	private final TweetListView tview;

	private AccountViewPresenter<BusinessAccountDTO> presenter;
	private BusinessAccountDTO account;
	private PopupPanel popupPanel;
  private SendMessageWidget smw;
  private NotificationWidget notificationWidget;

	@Inject
	public BusinessAccountView(EventBus eventBus,
      TweetBoxPresenter tweetBoxPresenter,
      TweetWidgetPresenter tweetWidgetPresenter,
      CouponTransactionViewPresenterImpl.Factory factory,
      CouponTransactionView couponTransactionView) {
		super(eventBus);
		this.tweetWidgetPresenter = tweetWidgetPresenter;
		this.tweetBox = new TweetBox(eventBus, tweetBoxPresenter);
		this.tview = new TweetListView(eventBus, tweetWidgetPresenter);
    this.couponTransactionView = couponTransactionView;
    this.couponTransactionView.setPresenter(factory.create(couponTransactionView));
		this.notificationWidget = new NotificationWidget();
		initWidget(uiBinder.createAndBindUi(this));
		setupUi();
		setupHandlers();
	}

	private void setupUi() {
		tview.setWidth(TWEET_WIDGET_WIDTH);
		tview.setHeight(TWEET_WIDGET_HEIGHT);
		tweetBox.setWidth(TWEET_BOX_WIDTH);
		tweetBox.setTweetCategory(TweetType.getAllTweetTypeForPublishingByBusiness());
		StyleHelper.show(profileImagePanel.getElement(), false);
		tweetSection.add(tview);
		displayCouponTransactionPanel(false);
  }

	private void setupHandlers() {
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
		StyleHelper.setBackgroundImage(
				basicDataFormatter.format(account, ValueType.PROFILE_IMAGE_URL_AS_BACKGROUND));

		name.setInnerHTML(account.getDisplayName());

		businessName.setInnerHTML(account.getDisplayName());

		if (account.getCurrentLocation() != null) {
			neighborhoodName.setInnerHTML(basicDataFormatter.format(account
			    .getCurrentLocation()
			    .getNeighborhood(), ValueType.NEIGHBORHOOD));
		} else {
			neighborhoodName.setInnerHTML(basicDataFormatter.format(
			    getPrimaryLocation(account),
			    ValueType.NEIGHBORHOOD));
		}
		// description.setText(account.getDe);

		if (account.getCategory() != null) {
			businessCategory.setInnerText(account.getCategory().getName());
		}

		email.setInnerText(account.getEmail());
		emailLink.setText(account.getEmail());

		phoneSpan.setInnerHTML(account.getPhone());

		if (account.getWebsite() != null) {
			websiteSpan.setInnerHTML(account.getWebsite());
			websiteLink.setHref(account.getWebsite());
		} else {
		  websiteSpan.setInnerHTML(StringConstants.NOT_AVAILABLE);
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
			lastLoginTime.setInnerText(basicDataFormatter.format(
			    account.getLastLoginTime(),
			    ValueType.DATE_DIFF));
		}

		displayHoursOfOperation();

		// display tweets
		displayTweetBox(true);
		displayTweets(account.getTweets(), false);

		displayProfileSection(true);
	}

	private NeighborhoodDTO getPrimaryLocation(BusinessAccountDTO account) {
		for (LocationDTO location : account.getLocations()) {
			if (location.getType() == LocationType.PRIMARY) {
				return location.getNeighborhood();
			}
		}
		return null;
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

//	@Override
	public void displayFormattedAddress(String fAddress) {
		if (fAddress != null) {
			formattedAddress.setInnerHTML(fAddress);
		}
	}

	GoogleMapWidget mapWidget = new GoogleMapWidget();
	@Override
	public void displayMap(String address) {
		mapWidget.displayMap(locationDiv, address);
	}

	@Deprecated
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
	public void displayTweets(List<TweetDTO> tweets, boolean displayNoTweetsMessage) {
		if (tweets.size() == 0) {

			if (displayNoTweetsMessage) {
				tview.displayNoTweetsMessage();
			}

			eventBus.fireEvent(new LoadingEventEnd());
			return;
		}

		tview.insertLast(tweets, DEFAULT_RENDERING_STATUS_CALLBACK);
	}

	@Override
  public void displayTweets(List<TweetDTO> tweets, boolean displayNoTweetsMessage, RenderingStatusCallback callback) {
    if (tweets.size() == 0) {

      if (displayNoTweetsMessage) {
        tview.displayNoTweetsMessage();
      }

      eventBus.fireEvent(new LoadingEventEnd());
      return;
    }

    tview.insertLast(tweets, callback);
  }
	
	@Override
	public void displayPublicProfile(BusinessAccountDTO account) {
		displayProfile(account);
		displayAccontUpdatePanel(false);
		displayTweetBox(false);
	}

	private void displayTweetBox(boolean display) {
		StyleHelper.show(tweetBoxDiv.getElement(), display);
	}
	
	@Override
	public void clear() {
		clearTweet();
		StyleHelper.clearBackground();
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
		smw.setPresenter(tweetWidgetPresenter.getMessagePresenter());
		smw.show();
	}

	@Override
	public void setPresenter(AccountViewPresenter<BusinessAccountDTO> presenter) {
		this.presenter = presenter;
	}

	@Override
	public void displayMessage(String msg, AlertType type) {
		internalDisplayModalMessage(msg, type);
	}

	@Override
	public void displayModalMessage(String msg, AlertType type) {
		internalDisplayModalMessage(msg, type);
	}

	private void internalDisplayModalMessage(String msg, AlertType type) {
		AlertModal modal = new AlertModal();
		modal.showMessage(msg, type);
  }

	@Override
	public Element getTweetSectionElement() {
//		return tweetSection.getElement();
	  return tview.getTweetSection();
	}

	@Override
	public void addTweets(List<TweetDTO> tweets) {
		tview.insertLast(tweets, DEFAULT_RENDERING_STATUS_CALLBACK);
	}

	@Override
	public void updateAccountDetails(AccountDetails accountDetails) {
	    tweetCountWidget.setValue(new Integer(accountDetails.getTotalTweets()).toString());
			commentCountWidget.setValue(new Integer(accountDetails.getTotalComments()).toString());
			likeCountWidget.setValue(new Integer(accountDetails.getTotalLikes()).toString());
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
		tview.updateComment(comment);
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
	public void displayProfileSection(boolean display) {
		StyleHelper.show(profileSectionRow, display);
	}

	@Override
	public void displayTargetNeighborhoods(List<NeighborhoodDTO> targetNeighborhoodList) {
		tweetBox.initializeTargetNeighborhood(targetNeighborhoodList);
	}

	@Override
  public TweetListView getTweetView() {
		return tview;
  }

	@Override
  public TweetBox getTweetWidget() {
		return tweetBox;
  }

	@Override
  public EmailWidget getEmailWidget() {
		return null;
  }

	@Override
  public void displayPurchasedCoupons(List<CouponItemDTO> transactions) {
		displayCouponTransactionPanel(true);
		couponTransactionView.displayPurchasedCoupons(transactions);
  }
	
	@Override
	public void setCouponTransactionCount(Long couponTransactionCount) {
		couponTransactionView.setCouponTransactionCount(couponTransactionCount);
	}
	
	@Override
  public void displayAccontUpdate(List<PendingActionTypes> updates) {
	  super.displayAccontUpdate(accountUpdatePanel, updates);
  }

	private void displayCouponTransactionPanel(boolean display) {
		StyleHelper.show(couponTransactionPanel, display);
		StyleHelper.show(tweetSection, !display);
	}

	@Override
  public void displayQrCode(String url) {
		Window.open(url, "_blank", "");
  }
	
	private void internalDisplayCouponTransactions() {
		couponTransactionView.loadCouponTransaction();
		displayCouponTransactionPanel(true);
  }

	@Override
  public void displayCouponTransactions() {
		internalDisplayCouponTransactions();
  }

  @Override
  public void goTo(Place place) {
    presenter.goTo(place);
  }

  @Override
  public void displayAccontUpdatePanel(boolean show) {
    StyleHelper.show(accountUpdatePanel, show);
  }
}
