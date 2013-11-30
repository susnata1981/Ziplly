package com.ziplly.app.client.widget;

import java.util.Date;
import java.util.List;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.ziplly.app.client.activities.TweetPresenter;
import com.ziplly.app.client.view.View;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.model.TweetStatus;
import com.ziplly.app.model.TweetType;
import com.ziplly.app.shared.FieldVerifier;
import com.ziplly.app.shared.ValidationResult;

public class TweetBox extends Composite implements View<TweetPresenter>{

	private static TweetBoxUiBinder uiBinder = GWT
			.create(TweetBoxUiBinder.class);

	interface TweetBoxUiBinder extends UiBinder<Widget, TweetBox> {
	}

	private int maxCharacters = 255;

	@UiField
	ControlGroup tweetCg;
	@UiField
	TextArea tweetTextBox;
	@UiField
	HTMLPanel tweetActionPanel;
	@UiField
	HelpInline tweetHelpInline;
	@UiField
	HTMLPanel tweetCategoryPanel;
	@UiField
	ListBox tweetCategoryList;

	@UiField
	Button tweetBtn;
	@UiField
	Button cancelBtn;

	@UiField
	Anchor embedLinkAnchor;
	@UiField
	Modal embedLinkModal;
	@UiField
	TextBox embedLinkTextBox;
	@UiField
	Button embedLinkButton;
	@UiField
	Button cancelEmbedLinkButton;
	
	boolean showKeystrokeCounter = true;

	private TweetPresenter presenter;

	private String height = "40px";

	private String width = "500px";

	public TweetBox() {
		initWidget(uiBinder.createAndBindUi(this));
//		for (TweetType type : TweetType.values()) {
//			tweetCategoryList.addItem(type.name().toLowerCase());
//		}
		tweetHelpInline.setVisible(true);
		embedLinkModal.hide();
		setupDefaultDimension();
		setupHandlers();
	}

	public void setTweetCategory(List<TweetType> tweetTypes) {
		for (TweetType type : tweetTypes) {
			tweetCategoryList.addItem(type.name().toLowerCase());
		}
	}
		
	private void setupDefaultDimension() {
		tweetTextBox.setHeight(height);
		tweetTextBox.setWidth(width);
		tweetActionPanel.setWidth(width);
		tweetHelpInline.setWidth(width);
		embedLinkModal.setWidth(width);
		embedLinkTextBox.setWidth("90%");
	}

	public void setWidth(String width) {
		this.width = width;
		tweetTextBox.setWidth(width);
		tweetActionPanel.setWidth(width);
	}

	public void setHeight(String height) {
		this.height = height;
		tweetTextBox.setHeight(height);
	}

	public void setMaxCharacters(int max) {
		maxCharacters = max;
	}

	public void setKeystrokeCounter(boolean enable) {
		showKeystrokeCounter = enable;
	}

	void updateUsedCharacterMessage() {
		String content = tweetTextBox.getText();
		ValidationResult result = FieldVerifier.validateTweetLength(content);
		if (result.isValid()) {
			tweetHelpInline.setText((maxCharacters - content.length())
					+ " characters remaining.");
			tweetCg.setType(ControlGroupType.NONE);
		} else {
			tweetHelpInline.setText(result.getErrors().get(0).getErrorMessage());
		}
	}

	private void setupHandlers() {
		tweetCategoryPanel.getElement().getStyle().setDisplay(Display.NONE);
		tweetTextBox.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				tweetCategoryPanel.getElement().getStyle()
						.setDisplay(Display.INLINE);
				updateUsedCharacterMessage();
				tweetHelpInline.setVisible(true);
				tweetTextBox.setHeight("60px");
			}
		});

		if (showKeystrokeCounter) {
			tweetTextBox.addKeyUpHandler(new KeyUpHandler() {
				@Override
				public void onKeyUp(KeyUpEvent event) {
					updateUsedCharacterMessage();
				}
			});
		}

		cancelBtn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				tweetCategoryPanel.getElement().getStyle()
						.setDisplay(Display.NONE);
				tweetHelpInline.setVisible(false);
				tweetTextBox.setText("");
				tweetTextBox.setHeight(height);
			}
		});
		
		embedLinkAnchor.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				embedLinkModal.show();
			}
		});
		
		cancelEmbedLinkButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				embedLinkModal.hide();
			}
		});
		
		embedLinkButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// validate link
				String link = embedLinkTextBox.getText();
				String url = "<a href='"+link+"'>"+link+"</a>";
				tweetTextBox.setText(tweetTextBox.getText()+" "+url+" ");
				embedLinkTextBox.setText("");
				embedLinkModal.hide();
			}
		});
	}

	boolean validate() {
		ValidationResult result = FieldVerifier.validateTweet(tweetTextBox
				.getText());
		if (!result.isValid()) {
			tweetCg.setType(ControlGroupType.ERROR);
			tweetHelpInline
					.setText(result.getErrors().get(0).getErrorMessage());
			return false;
		}

		return true;
	}

	public void clear() {
		tweetCg.setType(ControlGroupType.NONE);
		tweetHelpInline.setText("");
		setupDefaultDimension();
	}

	@UiHandler("tweetBtn")
	void tweet(ClickEvent event) {
		if (!validate()) {
			return;
		}

		TweetDTO tweet = new TweetDTO();
		String content = tweetTextBox.getText().trim();
		tweet.setContent(content);
		String category = tweetCategoryList.getValue(tweetCategoryList.getSelectedIndex()).toUpperCase();
		TweetType tweetType = TweetType.valueOf(category);
		tweet.setType(tweetType);
		tweet.setStatus(TweetStatus.ACTIVE);
		tweet.setTimeCreated(new Date());
		presenter.sendTweet(tweet);
		tweetCategoryPanel.getElement().getStyle().setDisplay(Display.NONE);
		tweetTextBox.setText("");
	}

	@Override
	public void setPresenter(TweetPresenter presenter) {
		this.presenter = presenter;
	}
}
