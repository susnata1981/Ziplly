package com.ziplly.app.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.ziplly.app.client.widget.CommunityWallWidget;

public class HomeView extends AbstractAccountView {

	private static HomeViewUiBinder uiBinder = GWT
			.create(HomeViewUiBinder.class);

	interface HomeViewUiBinder extends UiBinder<Widget, HomeView> {
	}

	public HomeView(SimpleEventBus eventBus) {
		super(eventBus);
	}

	@UiField
	HTMLPanel communityWallPanel;
	
	@UiField(provided=true)
	CommunityWallWidget cww;
	
	@Override
	protected void internalOnUserLogin() {
	}

	@Override
	protected void initWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	protected void postInitWidget() {
	}

	@Override
	protected void setupUiElements() {
		cww = new CommunityWallWidget(eventBus);
	}
}
