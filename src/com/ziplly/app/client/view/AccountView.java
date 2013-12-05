package com.ziplly.app.client.view;

import java.util.List;
import java.util.Set;

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
import com.ziplly.app.client.activities.TweetPresenter;
import com.ziplly.app.client.widget.EmailWidget;
import com.ziplly.app.client.widget.ProfileStatWidget;
import com.ziplly.app.client.widget.SendMessageWidget;
import com.ziplly.app.client.widget.TweetBox;
import com.ziplly.app.model.CommentDTO;
import com.ziplly.app.model.InterestDTO;
import com.ziplly.app.model.LoveDTO;
import com.ziplly.app.model.PersonalAccountDTO;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.model.TweetType;
import com.ziplly.app.shared.GetLatLngResult;

public class AccountView extends Composite implements IAccountView<PersonalAccountDTO> {

	private static AccountViewUiBinder uiBinder = GWT
			.create(AccountViewUiBinder.class);

	interface AccountViewUiBinder extends UiBinder<Widget, AccountView> {
	}

	@UiField
	Alert message;
	@UiField
	HTMLPanel mainSection;
	@UiField
	HTMLPanel asidePanel;
	@UiField
	Image profileImage;
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
	Anchor settingsLink;
	@UiField
	Anchor messagesLink;
	@UiField
	Button inviteBtn;
	
	@UiField
	SpanElement occupationSpan;
	@UiField
	HTMLPanel interestPanel;

	// Account stats
	@UiField
	ProfileStatWidget tweetCountWidget;
	@UiField
	ProfileStatWidget commentCountWidget;
	@UiField
	ProfileStatWidget likeCountWidget;
	
	ITweetView<TweetPresenter> tview = new TweetView();
	
	/*
	 * Tweet section
	 */
	@UiField
	HTMLPanel tweetSection;
	@UiField(provided = true)
	TweetBox tweetBox;
	@UiField
	HTMLPanel tweetBoxDiv;
	
	@UiField
	SpanElement unreadMessageCountField;
	
	EmailWidget emailWidget;
	AccountPresenter<PersonalAccountDTO> presenter;
	private PersonalAccountDTO account;
	private SendMessageWidget smw;
	private String tweetWidgetWidth = "68%";
	private String tweetBoxWidth = "68%";
	
	public AccountView() {
		tweetBox = new TweetBox();
		emailWidget = new EmailWidget();
		tweetBox.setWidth(tweetBoxWidth);
		tweetBox.setTweetCategory(TweetType.getAllTweetTypeForPublishingByUser());
		tview.setWidth(tweetWidgetWidth);
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField
	DivElement locationDiv;
	
	@Override
	public void displayProfile(PersonalAccountDTO account) {
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

		// about me section
		description.setText(account.getIntroduction());
		email.setInnerText(account.getEmail());
		emailLink.setText(account.getEmail());
		DateTimeFormat fmt = DateTimeFormat.getFormat("MMMM dd, yyyy");
		lastLoginTime.setInnerText(fmt.format(account.getLastLoginTime()));

		// occupation panel
		occupationSpan.setInnerHTML(account.getOccupation());
		
		// interest section
		populateInterest();
		
		// display tweets
		tweetBoxDiv.getElement().getStyle().setDisplay(Display.BLOCK);
		displayTweets(account.getTweets());
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

	private void populateInterest() {
		interestPanel.clear();
		// interest section
		Set<InterestDTO> interests = account.getInterests();
		StringBuilder sb = new StringBuilder();
		int size = interests.size();
		if (size > 0) {
			for (InterestDTO interest : interests) {
				sb.append(interest.getName()+"<br>");
			}
			interestPanel.add(new HTMLPanel("<span>" + sb.toString() + "</span>"));
		}
	}

	@Override
	public void displayTweets(List<TweetDTO> tweets) {
		tview.displayTweets(tweets);
		tweetSection.add(tview);
	}

	@Override
	public void displayPublicProfile(PersonalAccountDTO account) {
		displayProfile(account);
		asidePanel.getElement().getStyle().setVisibility(Visibility.HIDDEN);
		tweetBoxDiv.getElement().getStyle().setDisplay(Display.NONE);
	}

	@Override
	public void clear() {
	}

	public void clearTweet() {
	}

	@Override
	public void setPresenter(AccountPresenter<PersonalAccountDTO> presenter) {
		this.presenter = presenter;
		tweetBox.setPresenter(presenter);
		tview.setPresenter(presenter);
		emailWidget.setPresenter(presenter);
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
	public void removeTweet(TweetDTO tweet) {
		tview.remove(tweet);
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
	public void closeMessageWidget() {
		smw.hide();
	}
	
	@UiHandler("inviteBtn")
	public void invite(ClickEvent event){
		emailWidget.show();
	}
}
