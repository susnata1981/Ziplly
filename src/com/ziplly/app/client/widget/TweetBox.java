package com.ziplly.app.client.widget;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.FileUpload;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.Image;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.ziplly.app.client.activities.TweetPresenter;
import com.ziplly.app.client.resource.ZResources;
import com.ziplly.app.client.view.ImageUtil;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.client.view.View;
import com.ziplly.app.client.view.factory.AbstractValueFormatterFactory;
import com.ziplly.app.client.view.factory.BasicDataFormatter;
import com.ziplly.app.client.view.factory.ValueFamilyType;
import com.ziplly.app.client.view.factory.ValueType;
import com.ziplly.app.model.ImageDTO;
import com.ziplly.app.model.NeighborhoodDTO;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.model.TweetStatus;
import com.ziplly.app.model.TweetType;
import com.ziplly.app.shared.FieldVerifier;
import com.ziplly.app.shared.ValidationResult;

public class TweetBox extends Composite implements View<TweetPresenter> {

	private static TweetBoxUiBinder uiBinder = GWT.create(TweetBoxUiBinder.class);

	interface TweetBoxUiBinder extends UiBinder<Widget, TweetBox> {
	}

	private int maxCharacters = FieldVerifier.MAX_TWEET_LENGTH;

	@UiField
	ControlGroup tweetCg;
	@UiField
	TextArea tweetTextBox;
	@UiField
	Panel tweetActionPanel;
	@UiField
	DivElement tweetHelpDiv;
	@UiField
	HelpInline tweetHelpInline;
	@UiField
	HTMLPanel tweetCategoryPanel;
	@UiField
	ListBox tweetCategoryList;

	@UiField
	ListBox tweetTargetNeighborhoodList;

	@UiField
	Button tweetBtn;
	@UiField
	Button cancelBtn;

	//
	// Upload image form
	//
	@UiField
	FormPanel uploadForm;
	@UiField
	FileUpload fileUpload;
	@UiField
	Image uploadAnchorIcon;

	//
	// Preview panel
	//
	@UiField
	Anchor previewLinkAnchor;
	@UiField
	HTMLPanel previewPanel;
	@UiField
	HTMLPanel previewTweetImagePanel;
	@UiField
	Image previewTweetImage;
	@UiField
	HTMLPanel tweetTextPreview;

	@UiField
	Button closePreviewBtn;

	TweetBoxState tweetBoxState = new TweetBoxState();

	boolean showKeystrokeCounter = true;
	private TweetPresenter presenter;
	private BasicDataFormatter basicDataFormatter =
	    (BasicDataFormatter) AbstractValueFormatterFactory
	        .getValueFamilyFormatter(ValueFamilyType.BASIC_DATA_VALUE);

	private String height = "40px";
	private String width = "40%";

	private List<TweetType> tweetTypes;

	private Map<String, NeighborhoodDTO> neighborhoodNameMap = new HashMap<String, NeighborhoodDTO>();
	private List<NeighborhoodDTO> neighborhoods;

	public TweetBox() {
		initWidget(uiBinder.createAndBindUi(this));
		tweetHelpInline.setVisible(false);
		initUploadForm();
		setupActionBar();
		StyleHelper.show(tweetCategoryPanel.getElement(), Display.NONE);
		setupDefaultDimension();
		setupHandlers();
	}

	private void setupActionBar() {
		StyleHelper.show(fileUpload.getElement(), false);
		uploadAnchorIcon.setUrl(ZResources.IMPL.uploadIcon().getSafeUri());
	}

	private void initUploadForm() {
		uploadForm.setEncoding(FormPanel.ENCODING_MULTIPART);
		uploadForm.setMethod(FormPanel.METHOD_POST);
		fileUpload.setEnabled(false);
		displayPreview();
	}

	public void setTweetCategory(List<TweetType> tweetTypes) {
		this.tweetTypes = tweetTypes;
		for (TweetType type : tweetTypes) {
			tweetCategoryList.addItem(basicDataFormatter.format(type, ValueType.TWEET_TYPE));
		}
	}

	private void setupDefaultDimension() {
		tweetTextBox.setHeight(height);
		tweetTextBox.setWidth(width);
		tweetActionPanel.setWidth(width);
		tweetHelpInline.setWidth(width);
	}

	public void setWidth(String width) {
		this.width = width;
		tweetTextBox.setWidth(width);
		tweetActionPanel.setWidth(width);
		previewPanel.setWidth(width);
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
			tweetHelpInline.setText((maxCharacters - content.length()) + " characters remaining.");
			tweetCg.setType(ControlGroupType.NONE);
		} else {
			tweetHelpInline.setText(result.getErrors().get(0).getErrorMessage());
		}
	}

	private void setupHandlers() {
		tweetTextBox.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				StyleHelper.show(tweetHelpInline.getElement(), true);
				StyleHelper.show(tweetCategoryPanel.getElement(), Display.INLINE_BLOCK);
				updateUsedCharacterMessage();
				tweetHelpInline.setVisible(true);
				tweetTextBox.setHeight("60px");
			}
		});

		tweetTextBox.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				tweetTextPreview.getElement().setInnerHTML(TweetUtils.getContent(tweetTextBox.getText()));
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
				tweetCategoryPanel.getElement().getStyle().setDisplay(Display.NONE);
				tweetHelpInline.setVisible(false);
				tweetTextBox.setText("");
				tweetTextBox.setHeight(height);
				previewTweetImage.setUrl("");
				tweetBoxState.cancel();
				displayPreview();
			}
		});

		uploadAnchorIcon.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				fileUpload.getElement().<InputElement> cast().click();
			}
		});

		fileUpload.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				String fname = fileUpload.getFilename();
				StyleHelper.show(tweetCategoryPanel.getElement(), Display.INLINE_BLOCK);
				if (fname != null && !fname.equals("")) {
					// already has an image, need to delete it
					if (tweetBoxState.isImageUploaded()) {
						presenter.deleteImage(previewTweetImage.getUrl());
					}
					uploadForm.submit();
				}
			}
		});
	}

	boolean validate() {
		ValidationResult result = FieldVerifier.validateTweet(tweetTextBox.getText());
		if (!result.isValid()) {
			tweetCg.setType(ControlGroupType.ERROR);
			tweetHelpInline.setText(result.getErrors().get(0).getErrorMessage());
			StyleHelper.show(tweetHelpInline.getElement(), true);
			return false;
		}

		return true;
	}

	public void clear() {
		// clear control errors.
		clearError();

		setupDefaultDimension();

		// clear text
		tweetTextBox.setText("");

		// hide category
		tweetCategoryPanel.getElement().getStyle().setDisplay(Display.NONE);

		// clear preview section
		// tweetTextPreview.setInnerText("");
		tweetTextPreview.clear();
		previewTweetImage.setUrl("");

		// hide preview
		tweetBoxState.reset();
		displayPreview();
	}

	private void clearError() {
		tweetCg.setType(ControlGroupType.NONE);
		tweetHelpInline.setText("");
		StyleHelper.show(tweetHelpInline.getElement(), false);
	}

	@UiHandler("tweetBtn")
	void tweet(ClickEvent event) {
		clearError();

		if (!validate()) {
			return;
		}

		TweetDTO tweet = new TweetDTO();
		String content = tweetTextBox.getText().trim();
		tweet.setContent(content);
		String category = tweetTypes.get(tweetCategoryList.getSelectedIndex()).name();
		TweetType tweetType = TweetType.valueOf(category);
		tweet.setType(tweetType);
		tweet.setStatus(TweetStatus.ACTIVE);
		tweet.setTimeCreated(new Date());

		if (neighborhoods != null) {
			NeighborhoodDTO neighborhood =
			    neighborhoodNameMap.get(tweetTargetNeighborhoodList
			        .getItemText(tweetTargetNeighborhoodList.getSelectedIndex()));
			tweet.getTargetNeighborhoods().add(neighborhood);
		}

		if (tweetBoxState.isImageUploaded()) {
			tweet.setImage(previewTweetImage.getUrl());
			tweet.addImage(tweetBoxState.getCurrentUploadedImage());
		}

		presenter.sendTweet(tweet);
		clear();
	}

	public void setImageUploadUrl(String imageUrl) {
		uploadForm.setAction(imageUrl);
		fileUpload.setEnabled(true);
	}

	public void addUploadFormHandler(SubmitCompleteHandler submitCompleteHandler) {
		uploadForm.addSubmitCompleteHandler(submitCompleteHandler);
	}

	@Override
	public void setPresenter(TweetPresenter presenter) {
		this.presenter = presenter;
	}

	public void previewImage(String imageUrl) {
		// previewTweetImage.setUrl(imageUrl);
		try {
			ImageDTO currentUploadedImage = ImageUtil.parseImageUrl(imageUrl);
			previewTweetImage.setUrl(currentUploadedImage.getUrl());
			tweetBoxState.preview(currentUploadedImage);
			displayPreview();
		} catch (RuntimeException ex) {
			GWT.log("Invalid image url/format: " + imageUrl);
			tweetHelpInline.setText(StringConstants.INVALID_IMAGE);
			tweetCg.setType(ControlGroupType.ERROR);
			StyleHelper.show(tweetHelpInline.getElement(), true);
		}
	}

	public void displayPreview() {
		displayFileUploadSection(tweetBoxState.isImageUploaded());
		StyleHelper.show(previewPanel.getElement(), tweetBoxState.isPreviewPanelVisible());
	}

	private void displayFileUploadSection(boolean b) {
		Display display = b ? Display.BLOCK : Display.NONE;
		previewTweetImagePanel.getElement().getStyle().setDisplay(display);
	}

	@UiHandler("closePreviewBtn")
	void closePreview(ClickEvent event) {
		tweetBoxState.cancel();
		displayPreview();
	}

	@UiHandler("previewLinkAnchor")
	void togglePreview(ClickEvent event) {
		tweetBoxState.toggle();
		displayPreview();
	}

	// Clears upload form
	public void resetImageUploadUrl() {
		uploadForm.setAction("");
		fileUpload.getElement().setPropertyString("value", "");
		// imageUploaded = false;
		// tweetBoxState.reset();
	}

	public void initializeTargetNeighborhood(List<NeighborhoodDTO> neighborhoods) {
		if (neighborhoods != null) {
			tweetTargetNeighborhoodList.clear();
			this.neighborhoods = neighborhoods;
			for (NeighborhoodDTO n : neighborhoods) {
				tweetTargetNeighborhoodList.addItem(n.getName());
				neighborhoodNameMap.put(n.getName(), n);
			}
			StyleHelper.show(tweetTargetNeighborhoodList.getElement(), true);
		}
	}
}
