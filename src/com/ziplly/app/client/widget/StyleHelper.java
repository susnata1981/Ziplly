package com.ziplly.app.client.widget;

import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.constants.IconType;

public class StyleHelper {
	public static Icon getIcon(IconType type) {
		Icon icon = new Icon();
		icon.setType(type);
		return icon;
	}
}
