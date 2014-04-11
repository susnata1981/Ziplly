package com.ziplly.app.client.widget.blocks;

import com.github.gwtbootstrap.client.ui.Image;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Panel;
import com.ziplly.app.client.resource.ZResources;
import com.ziplly.app.client.widget.StyleHelper;
import com.ziplly.app.client.widget.VPanel;

public class FormUploadWidget {

	@UiField
	FormPanel uploadForm;
	@UiField
	FileUpload uploadField;
	@UiField
	Image uploadAnchorIcon;
	@UiField
	HTMLPanel profileImagePanel;
	@UiField
	Image imagePreview;
	@UiField
	Image loadingImage;
	private boolean imageUploaded;
	
	private Panel container;

	/*
	 * <g:HTMLPanel addStyleNames="{style.uploadProfileImagePanel}">
									<g:HTMLPanel addStyleNames="{style.profileImagePanel}" ui:field="profileImagePanel">
										<b:Image ui:field="profileImagePreview" addStyleNames="{style.profileImage}" />
									</g:HTMLPanel>
									<g:FormPanel ui:field="uploadForm">
										<g:HTMLPanel>
											<g:FileUpload ui:field="uploadField" name="image" />
											<b:Label>Upload Image</b:Label>
											<b:Image ui:field="uploadAnchorIcon" />
											<b:Image ui:field="loadingImage" />
										</g:HTMLPanel>
									</g:FormPanel>
								</g:HTMLPanel>
	 */
	
	public FormUploadWidget(final Panel container) {
		this.container = container;
		setupUi();
		uploadForm.setEncoding(FormPanel.ENCODING_MULTIPART);
		uploadForm.setMethod(FormPanel.METHOD_POST);
		StyleHelper.show(uploadField.getElement(), false);
		uploadField.setEnabled(false);
		loadingImage.setVisible(false);
		setupHandlers();
	}

	public void enableUploadButton() {
		uploadField.setEnabled(true);
  }	

	public void resetUploadForm() {
		uploadForm.setAction("");
	}

	/**
	 * Sets up form upload end point url
	 */
	public void setUploadFormActionUrl(String imageUrl) {
		uploadForm.setAction(imageUrl);
		loadingImage.setVisible(false);
	}

	/**
	 * Sets up submit handler 
	 */
	public void setUploadFormSubmitCompleteHandler(SubmitCompleteHandler submitCompleteHandler) {
		uploadForm.addSubmitCompleteHandler(submitCompleteHandler);
	}
	
	private void onUpload() {
		uploadForm.submit();
		StyleHelper.show(loadingImage.getElement(), true);
		loadingImage.setUrl(ZResources.IMPL.loadingImageSmall().getSafeUri().asString());
		imageUploaded = true;
	}

	public void displayImagePreview(String imageUrl) {
		if (imageUrl != null) {
			resetProfileImagePanel();
			imagePreview.setUrl(imageUrl);
			adjustProfileImagePanel();
			StyleHelper.show(loadingImage.getElement(), false);
		}
	}
	
	private void resetProfileImagePanel() {
		profileImagePanel.setHeight("200px");
	}
	
	/**
	 * Adjusts the height of profile image panel.
	 */
	private void adjustProfileImagePanel() {
		imagePreview.addLoadHandler(new LoadHandler() {

			@Override
			public void onLoad(LoadEvent event) {
				StyleHelper.setHeight(profileImagePanel, imagePreview.getHeight());
			}
		});
	}

	public boolean isImageUploaded() {
	  return imageUploaded;
  }

	private void setupHandlers() {
		uploadAnchorIcon.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				uploadField.getElement().<InputElement> cast().click();
			}
		});

		uploadField.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				onUpload();
			}
		});
  }

	private void setupUi() {
		Image previewImage = new Image();
		FlowPanel imagePanel = new FlowPanel();
		imagePanel.add(previewImage);
		container.add(imagePanel);
		
		uploadForm = new FormPanel();
		VPanel vpanel = new VPanel();
		uploadField = new FileUpload();
		vpanel.add(uploadField);
		Label uploadImageLabel = new Label("Upload image");
		vpanel.add(uploadImageLabel);
		uploadAnchorIcon = new Image(ZResources.IMPL.uploadIcon());
		vpanel.add(uploadAnchorIcon);
		loadingImage = new Image(ZResources.IMPL.loadingImageSmall());
		vpanel.add(loadingImage);
		uploadForm.add(vpanel);
		container.add(uploadForm);
  }
}
