package com.ziplly.app.client.widget.dataprovider;

import java.util.logging.Logger;

import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import com.ziplly.app.client.ZipllyServiceAsync;
import com.ziplly.app.client.widget.ConversationWidget;
import com.ziplly.app.model.Conversation;

public class ConversationDataProvider extends AsyncDataProvider<Conversation> {
	Logger logger = Logger.getLogger("ConversationDataProvider");
	private ZipllyServiceAsync service;
	private ConversationWidget cw;

	public ConversationDataProvider(ConversationWidget cw,
			ZipllyServiceAsync service) {
		this.cw = cw;
		this.service = service;
	}

	@Override
	protected void onRangeChanged(HasData<Conversation> display) {
		final Range range = display.getVisibleRange();
		System.out.println("Account id:" + cw.getAccount().getAccountId());
//		service.getMessages(range.getStart(), range.getLength(),
//				cw.getAd().account.getId(),
//				new AsyncCallback<GetMessageResponse>() {
//					@Override
//					public void onSuccess(GetMessageResponse result) {
//						System.out.println("Received conversations: "
//								+ result.conversations.size());
//						updateRowData(range.getStart(), result.conversations);
//					}
//
//					@Override
//					public void onFailure(Throwable caught) {
//						logger.log(Level.SEVERE, "Error retrieving messages");
//					}
//				});
	}

}
