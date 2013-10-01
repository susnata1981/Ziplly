package com.ziplly.app.client.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.Widget;
import com.ziplly.app.client.view.AbstractAccountView;
import com.ziplly.app.model.TweetDTO;

public class CommunityWallWidget extends AbstractAccountView {

	private static CommunityWallWidgetUiBinder uiBinder = GWT
			.create(CommunityWallWidgetUiBinder.class);

	interface CommunityWallWidgetUiBinder extends
			UiBinder<Widget, CommunityWallWidget> {
	}

	public CommunityWallWidget(SimpleEventBus eventBus) {
		super(eventBus);
	}

//	@UiField(provided=true)
	CellList<TweetDTO> tweetList;
	
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
		
	}

}