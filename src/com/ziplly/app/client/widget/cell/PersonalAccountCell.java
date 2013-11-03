package com.ziplly.app.client.widget.cell;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.ziplly.app.model.PersonalAccountDTO;

public class PersonalAccountCell extends AbstractCell<PersonalAccountDTO>{

	public PersonalAccountCell() {
		super(BrowserEvents.CLICK);
	}
	
	@Override
	public void render(com.google.gwt.cell.client.Cell.Context context,
			PersonalAccountDTO value, SafeHtmlBuilder sb) {
		
		if (value != null) {
			sb.appendHtmlConstant(
					"<div class='pcell'><img src='"+value.getImageUrl()+"' width=100 height=80/>"+
							"<a href='>"+GWT.getModuleBaseURL()+"+'>"+value.getDisplayName()+"</a></div>"
			);
		}
	}

}
