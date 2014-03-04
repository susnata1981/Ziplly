package com.ziplly.app.client.widget;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.RootPanel;

public class StyleHelper {
	
	public static void show(Element elem, boolean show) {
		Display display = show ? Display.BLOCK : Display.NONE;
		elem.getStyle().setDisplay(display);
	}
	
	public static void show(Element elem, Display d) {
		elem.getStyle().setDisplay(d);
	}
	
	public static boolean isVisible(Element elem) {
		return !elem.getStyle().getDisplay().equalsIgnoreCase(Display.NONE.name()); 
	}

	public static void show(com.google.gwt.dom.client.Element contentElem, boolean show) {
		Display display = show ? Display.BLOCK : Display.NONE;
		contentElem.getStyle().setDisplay(display);
	}

	public static void setBackgroundImage(Element element, String imageUrl) {
		element.getStyle().setProperty("background", 
				"url(" + imageUrl + ") no-repeat center center fixed");
		element.getStyle().setProperty("backgroundSize", "cover");
	}
	
	public static void setBackgroundImage(ImageResource image) {
		RootPanel.get().getElement().getStyle().setProperty("background", 
				"url(" + image.getSafeUri().asString() + ") no-repeat center center fixed");
		
		RootPanel.get().getElement().getStyle().setProperty("backgroundSize", "cover");
	}

	public static void setBackgroundImage(String imageUrl) {
		RootPanel.get().getElement().getStyle().setProperty("background", 
				"url("+ imageUrl +") no-repeat center center fixed");
		
		RootPanel.get().getElement().getStyle().setProperty("backgroundSize", "cover");
		
	}

	public static void clearBackground() {
		RootPanel.get().getElement().getStyle().clearBackgroundImage();
	}
}
