package com.ziplly.app.client.widget;

import com.github.gwtbootstrap.client.ui.Heading;
import com.github.gwtbootstrap.client.ui.Modal;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.ziplly.app.client.resource.ZResources;

public class LoadingPanelWidget extends Composite {
	
	private static final String IMAGE_HEIGHT = "40px";
	
	HTMLPanel panel = new HTMLPanel("");
	Modal modal = new Modal();
	
	public LoadingPanelWidget() {
		initWidget(panel);
		HPanel container = new HPanel();
		Image image = new Image();
		image.setUrl(ZResources.IMPL.loadingImageLarge().getSafeUri());
		image.setHeight(IMAGE_HEIGHT);
		container.add(image);
		Heading loadingTitle = new Heading(4, "Loading...");
		loadingTitle.getElement().getStyle().setLineHeight(40, Unit.PX);
		container.add(loadingTitle);
		container.getElement().getStyle().setProperty("margin", "auto");
		modal.add(container);
		panel.add(modal);
		panel.getElement().getStyle().setProperty("margin", "auto");
		panel.getElement().getStyle().setProperty("width", "auto");
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
