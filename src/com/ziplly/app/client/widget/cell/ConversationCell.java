package com.ziplly.app.client.widget.cell;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.ziplly.app.model.Conversation;

public class ConversationCell extends AbstractCell<Conversation> {
	private SimpleEventBus eventBus;

	public ConversationCell(SimpleEventBus eventBus) {
		super(BrowserEvents.CLICK);
		this.eventBus = eventBus;
	}

	@Override
	public void render(com.google.gwt.cell.client.Cell.Context context,
			Conversation value, SafeHtmlBuilder sb) {
		
		if (value == null) {
			return;
		}
		String content = "<div>" +
				"<a href=''"+value.getMessages().get(0).getSubject()+"</a>"+
				"</div>";
		sb.appendHtmlConstant(content);
	}
	
	@Override
	public void onBrowserEvent(Context context, Element parent, Conversation value,
            NativeEvent event, ValueUpdater<Conversation> valueUpdater) {
		
//		Window.alert("Conversation:"+value.getReceiver().getDisplayName());
	}
}
