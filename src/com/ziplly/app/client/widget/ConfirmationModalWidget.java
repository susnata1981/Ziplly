package com.ziplly.app.client.widget;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Modal;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class ConfirmationModalWidget extends Composite {
	public static interface ConfirmationModalCallback {
		public void confirm();
		public void cancel();
	}

	public interface ConfirmationModalWidgetUiBinder extends UiBinder<Widget, ConfirmationModalWidget> {
	}
	
	private static ConfirmationModalWidgetUiBinder confirmModalWidgetUiBinder = GWT.create(ConfirmationModalWidgetUiBinder.class);
	
	private final ConfirmationModalCallback callback;
	private final String message;

	@UiField
	Modal modal;
	
	@UiField
	Button yesBtn;
	
	@UiField
	Button noBtn;
	
	public ConfirmationModalWidget(String message, ConfirmationModalCallback callback) {
		this.message = message;
		this.callback = callback;
		
		initWidget(confirmModalWidgetUiBinder.createAndBindUi(this));
		
		yesBtn.addClickHandler(new ClickHandler() {

			@Override
      public void onClick(ClickEvent event) {
				ConfirmationModalWidget.this.callback.confirm();
      }
			
		});
		noBtn.addClickHandler(new ClickHandler() {

			@Override
      public void onClick(ClickEvent event) {
				show(false);
      }
		});
		modal.hide();
	}

	public void setWidth(String width) {
		modal.setWidth(width);
	}

	public void show(boolean b) {
		if (b) {
			modal.show();
		} else {
			modal.hide();
		}
	}
}
