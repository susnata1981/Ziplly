package com.ziplly.app.client.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.web.bindery.event.shared.EventBus;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.view.AbstractAccountView;
import com.ziplly.app.client.widget.cell.TweetCell;
import com.ziplly.app.client.widget.dataprovider.TweetDataProvider;
import com.ziplly.app.model.TweetDTO;

public class CommunityWallWidget extends AbstractAccountView {
	private static final int PAGE_SIZE = 10;

	private static CommunityWallWidgetUiBinder uiBinder = GWT
			.create(CommunityWallWidgetUiBinder.class);

	interface CommunityWallWidgetUiBinder extends
			UiBinder<Widget, CommunityWallWidget> {
	}

	public CommunityWallWidget(CachingDispatcherAsync dispatcher, EventBus eventBus) {
		super(dispatcher, eventBus);
	}

	@UiField(provided = true)
	SimplePager pager;
	
	@UiField(provided=true)
	CellList<TweetDTO> tweetList;
	
	AsyncDataProvider<TweetDTO> dataProvider;
	
	@Override
	protected void internalOnUserLogin() {
		dataProvider = new TweetDataProvider(this);
		dataProvider.addDataDisplay(tweetList);
		pager.setDisplay(tweetList);
	}

	@Override
	protected void initWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	protected void postInitWidget() {
		dataProvider = new TweetDataProvider(this);
	}

	@Override
	protected void setupUiElements() {
		tweetList = new CellList<TweetDTO>(new TweetCell(eventBus));
		tweetList.setPageSize(PAGE_SIZE);
		pager = new SimplePager();
	}

	public CellList<TweetDTO> getTweetList() {
		return tweetList;
	}
}