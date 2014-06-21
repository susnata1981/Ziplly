package com.ziplly.app.client.view;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.ziplly.app.client.activities.TweetPresenter;
import com.ziplly.app.client.places.HomePlace;
import com.ziplly.app.client.widget.StyleHelper;
import com.ziplly.app.client.widget.TweetWidget;
import com.ziplly.app.model.TweetDTO;

public class TweetDetailsView extends Composite {

	private static TweetDetailsViewUiBinder uiBinder = GWT.create(TweetDetailsViewUiBinder.class);

	interface TweetDetailsViewUiBinder extends UiBinder<Widget, TweetDetailsView> {
	}

	@UiField
	Alert message;
	@UiField
	Button backBtn;
	@UiField
	HTMLPanel communityWallPanel;
	
	private TweetPresenter presenter;

	public TweetDetailsView() {
		initWidget(uiBinder.createAndBindUi(this));
		displayMessage(false);
	}

	private void displayMessage(boolean display) {
	   StyleHelper.show(message, display);
	   StyleHelper.show(backBtn, display);
  }

  public void setPresenter(TweetPresenter presenter) {
		this.presenter = presenter;
	}

	public void display(TweetDTO tweet) {
	  displayMessage(false);
		clearDisplay();
		TweetWidget tw = new TweetWidget();
		tw.setWidth("80%");
		tw.setPresenter(presenter);
		tw.displayTweet(tweet);
		communityWallPanel.add(tw);
	}

	public void clearDisplay() {
		communityWallPanel.clear();
	}

	public void displayMessage(String msg, AlertType type) {
		message.setText(msg);
		message.setType(type);
		displayMessage(true);
	}
	
	@UiHandler("backBtn")
	public void back(ClickEvent event) {
	  presenter.goTo(new HomePlace());
	}
}
