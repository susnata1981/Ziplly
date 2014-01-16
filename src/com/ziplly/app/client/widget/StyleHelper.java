package com.ziplly.app.client.widget;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.user.client.Element;

public class StyleHelper {
	
	public static void show(Element elem, boolean show) {
		Display display = show ? Display.BLOCK : Display.NONE;
		elem.getStyle().setDisplay(display);
	}
	
	public static boolean isVisible(Element elem) {
		return !elem.getStyle().getDisplay().equalsIgnoreCase(Display.NONE.name()); 
	}
}