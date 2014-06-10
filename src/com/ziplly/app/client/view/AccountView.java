package com.ziplly.app.client.view;

import java.util.List;
import java.util.Set;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.AlertBlock;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.FluidRow;
import com.github.gwtbootstrap.client.ui.Heading;
import com.github.gwtbootstrap.client.ui.Image;
import com.github.gwtbootstrap.client.ui.Paragraph;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
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
import com.ziplly.app.client.activities.TweetPresenter;
import com.ziplly.app.client.places.PersonalAccountSettingsPlace;
import com.ziplly.app.client.resource.ZResources;
import com.ziplly.app.client.view.coupon.CouponTransactionView;
import com.ziplly.app.client.view.event.LoadingEventEnd;
import com.ziplly.app.client.view.factory.ValueType;
import com.ziplly.app.client.widget.AlertModal;
import com.ziplly.app.client.widget.EmailWidget;
import com.ziplly.app.client.widget.GoogleMapWidget;
import com.ziplly.app.client.widget.ProfileStatWidget;
import com.ziplly.app.client.widget.SendMessageWidget;
import com.ziplly.app.client.widget.StyleHelper;
import com.ziplly.app.client.widget.TweetBox;
import com.ziplly.app.model.CommentDTO;
import com.ziplly.app.model.InterestDTO;
import com.ziplly.app.model.LoveDTO;
import com.ziplly.app.model.NeighborhoodDTO;
import com.ziplly.app.model.PersonalAccountDTO;
import com.ziplly.app.model.PurchasedCouponDTO;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.model.TweetType;
import com.ziplly.app.shared.FieldVerifier;
import com.ziplly.app.shared.GetAccountDetailsResult;
import com.ziplly.app.shared.GetLatLngResult;

public class AccountView extends AbstractView implements IAccountView<PersonalAccountDTO>, TopLevelView {

	private static AccountViewUiBinder uiBinder = GWT.create(AccountViewUiBinder.class);

	interface AccountViewUiBinder extends UiBinder<Widget, AccountView> {
	}

	@UiField
	FluidRow profileSectionRow;

	// Basic account info
	@UiField
	Alert message;

	@UiField
	HTMLPanel profileImagePanel;
	@UiField
	Image profileImage;
	@UiField
	Heading name;
	@UiField
	Paragraph description;
	@UiField
	SpanElement gender;
	@UiField
	HTMLPanel badgePanel;
	@UiField
	SpanElement lastLoginTime;
	@UiField
	SpanElement email;
	@UiField
	Anchor emailLink;
	@UiField
	Button inviteBtn;

	@UiField
	SpanElement occupationSpan;
	@UiField
	HTMLPanel interestPanel;
	@UiField
	HTMLPanel accountUpdatePanel;
	
	// Account stats
	@UiField
	ProfileStatWidget tweetCountWidget;
	@UiField
	ProfileStatWidget commentCountWidget;
	@UiField
	ProfileStatWidget likeCountWidget;

	private ITweetView<TweetPresenter> tview = new TweetView();

	// Tweet section
	@UiField
	HTMLPanel tweetSection;
	@UiField(provided = true)
	TweetBox tweetBox;
	@UiField
	HTMLPanel tweetBoxDiv;
	@UiField
	DivElement locationDiv;

	// Coupon transaction section
	@UiField
	HTMLPanel couponTransactionPanel;
	@UiField(provided = true)
	CouponTransactionView couponTransactionView;
	
	// Updates section
	@UiField
	AlertBlock updateAlertBlock;

	EmailWidget emailWidget;
	AccountPresenter<PersonalAccountDTO> presenter;
	private PersonalAccountDTO account;
	private SendMessageWidget smw;
	private GoogleMapWidget mapWidget = new GoogleMapWidget();
	
	private String tweetWidgetWidth = "94%";
	private String tweetBoxWidth = "97%";
	private static final String TWEET_VIEW_HEIGHT = "1115px";

	@Inject
	public AccountView(EventBus eventBus) {
		super(eventBus);
		tweetBox = new TweetBox(eventBus);
		emailWidget = new EmailWidget();
		couponTransactionView = new CouponTransactionView(eventBus);
		initWidget(uiBinder.createAndBindUi(this));
		setupUi();
	}

	private void setupUi() {
		tweetBox.setWidth(tweetBoxWidth);
		tweetBox.setTweetCategory(TweetType.getAllTweetTypeForPublishingByUser());
		tview.setWidth(tweetWidgetWidth);
		tview.setHeight(TWEET_VIEW_HEIGHT);
//		StyleHelper.setBackgroundImage(ZResources.IMPL.profileBackground());
		tweetSection.add(tview);
		displayCouponTransactionPanel(false);
  }

	@Override
	public void displayProfile(PersonalAccountDTO account) {
		if (account == null) {
			throw new IllegalArgumentException();
		}

		message.setVisible(false);
		this.account = account;

		// Image section
		profileImage.setUrl(accountFormatter.format(account, ValueType.PROFILE_IMAGE_URL));
		profileImage.setAltText(account.getDisplayName());
		adjustProfileImagePanel();

		name.setText(account.getDisplayName());

		// about me section
		description.setText(account.getIntroduction());
		email.setInnerText(account.getEmail());
		emailLink.setText(account.getEmail());
		gender.setInnerHTML(basicDataFormatter.format(account.getGender(), ValueType.GENDER));
		badgePanel.getElement().setInnerHTML(accountFormatter.format(account, ValueType.BADGE));
		lastLoginTime.setInnerText(basicDataFormatter.format(
		    account.getLastLoginTime(),
		    ValueType.DATE_VALUE_SHORT));

		// Occupation panel
		setValue(occupationSpan, account.getOccupation());
//		occupationSpan.setInnerHTML(account.getOccupation());

		// Interest section
		populateInterest();

		// Display tweets
		displayTweetBox(true);
		displayProfileSection(true);
	}

	private void setValue(SpanElement elem, String val) {
	  if (val == null) {
	    elem.setInnerHTML(StringConstants.NOT_AVAILABLE);
	  } else {
	    elem.setInnerHTML(val);
	  }
	}
	
	private void displayTweetBox(boolean display) {
		StyleHelper.show(tweetBoxDiv.getElement(), display);
	}
	
	private void adjustProfileImagePanel() {
		profileImage.addLoadHandler(new LoadHandler() {

			@Override
			public void onLoad(LoadEvent event) {
				String height = profileImage.getHeight() + "px";
				profileImagePanel.setHeight(height);
			}

		});
	}

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

	private void populateInterest() {
		interestPanel.clear();
		// interest section
		Set<InterestDTO> interests = account.getInterests();
		StringBuilder sb = new StringBuilder();
		int size = interests.size();
		if (size > 0) {
			for (InterestDTO interest : interests) {
				sb.append(interest.getName() + "<br>");
			}
			interestPanel.add(new HTMLPanel("<span>" + sb.toString() + "</span>"));
		} else {
		  interestPanel.add(new HTMLPanel("<span>" + StringConstants.NOT_AVAILABLE + "</span>"));
		}
	}

	@Override
	public void displayTweets(List<TweetDTO> tweets, boolean displayNoTweetsMessage) {
		displayCouponTransactionPanel(false);
		if (tweets.size() == 0) {
			if (displayNoTweetsMessage) {
				tview.displayNoTweetsMessage();
			}
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
	public void displayPublicProfile(PersonalAccountDTO account) {
		displayProfile(account);
		displayAccontUpdateSection(false);
		displayTweetBox(false);
	}

	@Override
	public void clear() {
		clearTweet();
		StyleHelper.clearBackground();
	}

	public void clearTweet() {
		tview.clear();
	}

	@Override
	public void setPresenter(AccountPresenter<PersonalAccountDTO> presenter) {
		this.presenter = presenter;
		couponTransactionView.setPresenter(presenter);
	}

	@Override
	public void displayModalMessage(String msg, AlertType type) {
		AlertModal modal = new AlertModal();
		modal.showMessage(msg, type);
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
	public void removeTweet(TweetDTO tweet) {
		tview.remove(tweet);
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

	@UiHandler("sendMsgBtn")
	public void onSendMessageWidgetClick(ClickEvent event) {
		openMessageWidget();
	}

	@Override
	public void openMessageWidget() {
		smw = new SendMessageWidget(account);
		smw.setPresenter(presenter.getSendMessagePresenter());
		smw.show();
	}

	@Override
	public void closeMessageWidget() {
		smw.hide();
	}

	@UiHandler("inviteBtn")
	public void invite(ClickEvent event) {
		emailWidget.show();
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
		// TODO Auto-generated method stub

	}

	@Override
	public void displayProfileSection(boolean display) {
		StyleHelper.show(profileSectionRow.getElement(), display);
	}

	@Override
	public void displayTargetNeighborhoods(List<NeighborhoodDTO> targetNeighborhoodList) {
		tweetBox.initializeTargetNeighborhood(targetNeighborhoodList);
	}

	@Override
	public void displayAccontUpdate() {
		if (isAccountNotComplete()) {
			addAccountProfileNotCompleteMessage();
		} else {
			String uploadImageHtml = "There are no updates at this moment";
			updateAlertBlock.setHTML(uploadImageHtml);
		}
		displayAccontUpdateSection(true);
	}

	private void displayAccontUpdateSection(boolean display) {
		StyleHelper.show(accountUpdatePanel, display);
	}

	private boolean isAccountNotComplete() {
		return FieldVerifier.isEmpty(account.getIntroduction()) || account.getInterests().size() == 0
		    || account.getImages().size() == 0;
	}

	private void addAccountProfileNotCompleteMessage() {
		updateAlertBlock.setHTML(StringConstants.ACCOUNT_NOT_COMPLETE);
		Anchor accountSettingsAnchor = new Anchor();
		accountSettingsAnchor.setText("settings");
		accountSettingsAnchor.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				presenter.goTo(new PersonalAccountSettingsPlace());
			}
		});

		updateAlertBlock.add(accountSettingsAnchor);
	}

	@Override
  public ITweetView<TweetPresenter> getTweetView() {
		return tview;
  }

	@Override
  public TweetBox getTweetWidget() {
		return tweetBox;
  }

	@Override
  public EmailWidget getEmailWidget() {
		return emailWidget;
  }
	
	@Override
	public void displayPurchasedCoupons(List<PurchasedCouponDTO> transactions) {
		displayCouponTransactionPanel(true);
		couponTransactionView.displayPurchasedCoupons(transactions);
	}

	private void internalDisplayCouponTransactions() {
		couponTransactionView.loadCouponTransaction();
		displayCouponTransactionPanel(true);
  }

	private void displayCouponTransactionPanel(boolean display) {
		StyleHelper.show(couponTransactionPanel.getElement(), display);
		StyleHelper.show(tweetSection.getElement(), !display);
	}

	@Override
  public void setCouponTransactionCount(Long totalTransactions) {
		couponTransactionView.setRowCount(totalTransactions);
  }
	
	@Override
  public void displayQrCode(String url) {
		Window.open(url, "_blank", "");
  }

	@Override
  public void displayCouponTransactions() {
		internalDisplayCouponTransactions();
  }
}
