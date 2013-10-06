package com.ziplly.app.client.view;

import com.google.gwt.event.shared.SimpleEventBus;
import com.ziplly.app.client.ZipllyServiceAsync;
import com.ziplly.app.client.widget.AccountWidget;
import com.ziplly.app.client.widget.ConversationWidget;
import com.ziplly.app.client.widget.LoginWidget;
import com.ziplly.app.client.widget.LogoutWidget;

public class WidgetFactory {
	
	public static AccountWidget getAccountWidget(SimpleEventBus eventBus) {
		return new AccountWidget(eventBus,true);
	}
	
	public static LoginWidget getLoginWidget(ZipllyServiceAsync service, SimpleEventBus eventBus) {
		return new LoginWidget(service, eventBus);
	}
	
	public static LogoutWidget getLogoutWidget(SimpleEventBus eventBus) {
		return new LogoutWidget(eventBus);
	}

	public static ConversationWidget getConversationWidget(
			ZipllyServiceAsync service, 
			SimpleEventBus eventBus) {
		return new ConversationWidget(eventBus);
	}
}
