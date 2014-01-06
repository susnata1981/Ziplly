package com.ziplly.app.client.widget;

import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.RootPanel;

public class CssStyleHelper {
	public static Icon getIcon(IconType type) {
		Icon icon = new Icon();
		icon.setType(type);
		return icon;
	}
	
	public static void setBackgroundImage(ImageResource image) {
		RootPanel.get("wrapper").getElement().getStyle().setProperty("background", 
				"url("+image.getSafeUri().asString()+") no-repeat center center fixed");
	}
	
	public static void clearBackgroundImage() {
		RootPanel.get("wrapper").getElement().getStyle().clearBackgroundImage();
	}
}
