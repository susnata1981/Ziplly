package com.ziplly.app.client.view.common.action;

import com.google.gwt.event.shared.EventBus;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.widget.blocks.FormUploadWidget;
import com.ziplly.app.shared.GetImageUploadUrlResult;

public class UploadFormHandler extends DispatcherCallbackAsync<GetImageUploadUrlResult> {
  private FormUploadWidget formUploadWidget;

  public UploadFormHandler(EventBus eventBus, FormUploadWidget formUploadWidget) {
    super(eventBus);
    this.formUploadWidget = formUploadWidget;
  }
  
  @Override
  public void onSuccess(GetImageUploadUrlResult result) {
    if (result.getImageUrl() != null) {
      formUploadWidget.resetUploadForm();
      formUploadWidget.setUploadFormActionUrl(result.getImageUrl());
      formUploadWidget.enableUploadButton();
    }
  }
}
