package com.ziplly.app.client.widget.cell;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.ziplly.app.model.ConversationDTO;

public class ConversationCell extends AbstractCell<ConversationDTO> {
	private SimpleEventBus eventBus;

	public ConversationCell(SimpleEventBus eventBus) {
		super(BrowserEvents.CLICK);
		this.eventBus = eventBus;
	}

	@Override
	public void render(com.google.gwt.cell.client.Cell.Context context,
	    ConversationDTO value,
	    SafeHtmlBuilder sb) {

		if (value == null) {
			return;
		}
		String content = "<div>" +
		// "<a href=''"+value.getMessages().get(0).getSubject()+"</a>"+
		    "</div>";
		sb.appendHtmlConstant(content);
	}

	@Override
	public void onBrowserEvent(Context context,
	    Element parent,
	    ConversationDTO value,
	    NativeEvent event,
	    ValueUpdater<ConversationDTO> valueUpdater) {

		// Window.alert("Conversation:"+value.getReceiver().getDisplayName());
	}
}
