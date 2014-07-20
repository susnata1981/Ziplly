package com.ziplly.app.client.widget.blocks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.gwtbootstrap.client.ui.Image;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.ziplly.app.client.resource.ZResources;
import com.ziplly.app.client.view.ImageUtil;
import com.ziplly.app.client.widget.StyleHelper;
import com.ziplly.app.model.ImageDTO;

public class FormUploadWidget extends Composite {

  @UiTemplate(value = "FormUploadWidgetUiBinder.ui.xml")
  public interface FormUploadWidgetUiBinder extends UiBinder<Widget, FormUploadWidget> {
  }
  
  private FormUploadWidgetUiBinder uiBinder = GWT.create(FormUploadWidgetUiBinder.class);
	
  @UiField
	FormPanel uploadForm;
	@UiField
	FileUpload fileUpload;
	@UiField
	HTMLPanel imagePanel;
	@UiField
	Image imagePreview;
	@UiField
	Image loadingImage;
	//@UiField
	Image uploadAnchorIcon;
	
	private Panel container;
	private List<ImageDTO> uploadedImages = new ArrayList<ImageDTO>();
	
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
		initWidget(uiBinder.createAndBindUi(this));
		setupUi();
    setupHandlers();
	}

	private void setupUi() {
    uploadForm.setEncoding(FormPanel.ENCODING_MULTIPART);
    uploadForm.setMethod(FormPanel.METHOD_POST);
    fileUpload.setEnabled(false);
    displayImage(ZResources.IMPL.noGeneralImage().getSafeUri().asString());
    container.add(this);
  }

  public void enableUploadButton() {
	  fileUpload.setEnabled(true);
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
	
	// Currently supporting only 1 image
	public void displayImagePreview(String encodedUrl) {
		if (encodedUrl != null) {
      ImageDTO imageDto = ImageUtil.parseImageUrl(encodedUrl);
      showLoadingImage(false);
		  displayImage(imageDto.getUrl());
			uploadedImages.clear();
		  uploadedImages.add(imageDto);
		}
	}
	
	private void showLoadingImage(boolean show) {
	  StyleHelper.show(loadingImage.getElement(), show);
  }

	private void setupHandlers() {
//		uploadAnchorIcon.addClickHandler(new ClickHandler() {
//
//			@Override
//			public void onClick(ClickEvent event) {
//			  fileUpload.getElement().<InputElement> cast().click();
//			}
//		});

		fileUpload.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				onUpload();
			}
		});
  }

  public boolean hasImage() {
    return uploadedImages.size() > 0;
  }
  
  public List<ImageDTO> getImage() {
    if (hasImage()) {
      return uploadedImages;
    }
    
    return Collections.emptyList();
  }

  public void reset() {
    uploadedImages.clear();
    displayImage(ZResources.IMPL.noGeneralImage().getSafeUri().asString());
  }

  private void displayImage(String image) {
    imagePreview.setUrl(image);
//    imagePreview.setHeight("300px");
//    imagePreview.addLoadHandler(new LoadHandler() {
//
//      @Override
//      public void onLoad(LoadEvent event) {
//        imagePanel.setHeight(imagePreview.getHeight()+"px");
//      }
//    });
  }
  
  private void onUpload() {
    uploadForm.submit();
    loadingImage.setUrl(ZResources.IMPL.loadingImageSmall().getSafeUri().asString());
    showLoadingImage(true);
  }
}
