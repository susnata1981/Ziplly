package com.ziplly.app.client.widget;

import com.github.gwtbootstrap.client.ui.ListBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class ShareSettingsWidget extends Composite {
	private static ShareSettingsWidgetUiBinder uiBinder = GWT
			.create(ShareSettingsWidgetUiBinder.class);

	interface ShareSettingsWidgetUiBinder extends
			UiBinder<Widget, ShareSettingsWidget> {
	}

	@UiField(provided=true)
	ListBox shareSettingsList;
	
	public ShareSettingsWidget() {
		shareSettingsList = new ListBox();
		for(ShareSetting shareSetting : ShareSetting.values()) {
			shareSettingsList.addItem(shareSetting.name().toLowerCase());
		}
		initWidget(uiBinder.createAndBindUi(this));
	}

	public ShareSetting getSelection() {
		int index = shareSettingsList.getSelectedIndex();
		if (index < ShareSetting.values().length) {
			return ShareSetting.values()[index];
		}
		
		// default
		return ShareSetting.values()[0];
	}
	
	public void setSelection(ShareSetting setting) {
		shareSettingsList.setSelectedIndex(setting.ordinal());
	}
}
