package com.ziplly.app.client.widget.dataprovider;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import com.ziplly.app.client.widget.CommunityWallWidget;
import com.ziplly.app.model.TweetDTO;

public class TweetDataProvider extends AsyncDataProvider<TweetDTO>{
	private CommunityWallWidget cww;

	public TweetDataProvider(CommunityWallWidget cww) {
		this.cww = cww;
	}
	
	@Override
	protected void onRangeChanged(HasData<TweetDTO> display) {
		final Range range = display.getVisibleRange();
		cww.getService().getTweets(cww.getAccount(), new AsyncCallback<List<TweetDTO>>() {
			
			@Override
			public void onSuccess(List<TweetDTO> result) {
				updateRowData(range.getStart(), result);
				cww.getTweetList().setRowCount(result.size());
			}
			
			@Override
			public void onFailure(Throwable caught) {
				
			}
		});
	}

}
