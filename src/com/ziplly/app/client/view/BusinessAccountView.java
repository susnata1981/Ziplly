package com.ziplly.app.client.view;

import java.util.List;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Image;
import com.github.gwtbootstrap.client.ui.Paragraph;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
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
import com.ziplly.app.client.widget.ProfileStatWidget;
import com.ziplly.app.client.widget.SendMessageWidget;
import com.ziplly.app.client.widget.TweetBox;
import com.ziplly.app.model.BusinessAccountDTO;
import com.ziplly.app.model.CommentDTO;
import com.ziplly.app.model.LoveDTO;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.model.TweetType;
import com.ziplly.app.shared.GetLatLngResult;

public class BusinessAccountView extends Composite implements IBusinessAccountView {

	interface BusinessAccountViewUiBinder extends UiBinder<Widget, BusinessAccountView> {
	}

	private static BusinessAccountViewUiBinder uiBinder = GWT
			.create(BusinessAccountViewUiBinder.class);

	@UiField
	Alert message;
	@UiField
	HTMLPanel mainSection;
	@UiField
	HTMLPanel asidePanel;
	@UiField
	Image profileImage;
	@UiField
	Button sendMsgBtn;
	@UiField
	HeadingElement name;
	@UiField
	Paragraph description;
	@UiField
	SpanElement lastLoginTime;
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

	@UiField
	Anchor settingsLink;
	@UiField
	Anchor messagesLink;
	
	@UiField(provided=true)
	TweetBox tweetBox;
	
	// Account stats
	@UiField
	ProfileStatWidget tweetCountWidget;
	@UiField
	ProfileStatWidget commentCountWidget;
	@UiField
	ProfileStatWidget likeCountWidget;

	ITweetView<TweetPresenter> tview = new TweetView();
	SendMessageWidget smw;
	
	/*
	 * Tweet section
	 */
	@UiField
	HTMLPanel tweetSection;
	@UiField
	HTMLPanel tweetBoxDiv;
	
	@UiField
	SpanElement unreadMessageCountField;
	
	AccountPresenter<BusinessAccountDTO> presenter;
	BusinessAccountDTO account;

	public BusinessAccountView() {
		tweetBox = new TweetBox();
		tweetBox.setTweetCategory(TweetType.getAllTweetTypeForPublishingByBusiness());
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField
	DivElement locationDiv;
	
	@Override
	public void displayProfile(BusinessAccountDTO account) {
		if (account == null) {
			throw new IllegalArgumentException();
		}
		message.setVisible(false);
		this.account = account;
		
		// aside
		asidePanel.getElement().getStyle().setVisibility(Visibility.VISIBLE);
		
		// image section
		if (account.getImageUrl() != null) {
			profileImage.setUrl(account.getImageUrl());
		}
		
		profileImage.setAltText(account.getDisplayName());
		name.setInnerHTML(account.getDisplayName());

//		description.setText(account.get);
		email.setInnerText(account.getEmail());
		emailLink.setText(account.getEmail());

		if (account.getWebsite() != null) {
			websiteSpan.setInnerHTML(account.getWebsite());
			websiteLink.setHref(account.getWebsite());
		}
		// last login time
		if (account.getLastLoginTime() != null) {
			DateTimeFormat fmt = DateTimeFormat.getFormat("MMMM dd, yyyy");
			lastLoginTime.setInnerText(fmt.format(account.getLastLoginTime()));
		}

		// display tweets
		tweetBoxDiv.getElement().getStyle().setDisplay(Display.BLOCK);
		displayTweets(account.getTweets());
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
		tweetSection.add(tview);
	}

	@Override
	public void displayPublicProfile(BusinessAccountDTO account) {
		displayProfile(account);
		asidePanel.getElement().getStyle().setVisibility(Visibility.HIDDEN);
		tweetBoxDiv.getElement().getStyle().setDisplay(Display.NONE);
	}

	@Override
	public void clear() {
		this.account = null;
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
	
	@UiHandler("settingsLink")
	void settingsLinkClicked(ClickEvent event) {
		presenter.settingsLinkClicked();
	}
	
	@UiHandler("messagesLink")
	void messagesLinkClicked(ClickEvent event) {
		presenter.messagesLinkClicked();
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
			if (ctx.getUnreadMessageCount()>0) {
				unreadMessageCountField.setInnerHTML("("+ctx.getUnreadMessageCount()+")");
			} else {
				unreadMessageCountField.setInnerHTML("");
			}
			tweetCountWidget.setValue(new Integer(ctx.getTotalTweets()).toString());
			commentCountWidget.setValue(new Integer(ctx.getTotalComments()).toString());
			likeCountWidget.setValue(new Integer(ctx.getTotalLikes()).toString());
		}
	}

	@Override
	public void updateComment(CommentDTO comment) {
		tview.updateComment(comment);
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeTweet(TweetDTO tweet) {
		// TODO Auto-generated method stub
		
	}
}
