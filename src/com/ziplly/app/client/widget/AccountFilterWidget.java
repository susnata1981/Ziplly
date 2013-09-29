package com.ziplly.app.client.widget;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.github.gwtbootstrap.client.ui.ListBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.ziplly.app.client.view.event.CategorySelectedEvent;
import com.ziplly.app.model.InterestList;

public class AccountFilterWidget extends Composite {

	private static AccountFilterWidgetUiBinder uiBinder = GWT
			.create(AccountFilterWidgetUiBinder.class);

	interface AccountFilterWidgetUiBinder extends
			UiBinder<Widget, AccountFilterWidget> {
	}

	interface Style extends CssResource {
		String error();
		String valid();
	}
	
	private SimpleEventBus eventBus;

	public AccountFilterWidget(SimpleEventBus eventBus) {
		this.eventBus = eventBus;
		initWidget(uiBinder.createAndBindUi(this));
		setup();
	}
	
	void setup() {
		Map<com.ziplly.app.model.InterestList.Activity, List<String>> interests = InterestList.getInstance().getInterests();
		for(Entry<com.ziplly.app.model.InterestList.Activity, List<String>> entry : interests.entrySet()) {
			for(String interest : entry.getValue()) {
				interestListBox.addItem(interest);
			}
		}
	}

	@UiField
	HTMLPanel disntaceFilter;

	@UiField
	HTMLPanel interestFilter;

	@UiField
	TextBox zipCode;
	
	@UiField
	Style style;

	@UiField
	ListBox distanceListBox;
	
	@UiField
	ListBox interestListBox;
	
	@UiHandler("zipCode")
	void zipCodeValidator(BlurEvent event){
		if (zipCode.getText().length() != 5) {
			zipCode.addStyleName(style.error());
		} else {
			zipCode.addStyleName(style.valid());
		}
	}
	
	@UiHandler("distanceListBox")
	public void filterByDistance(ChangeEvent event) {
		String val = distanceListBox.getValue(distanceListBox.getSelectedIndex());
		// TODO
	}
	
	@UiHandler("interestListBox")
	public void filterByInterest(ChangeEvent event) {
		String category = interestListBox.getValue(interestListBox.getSelectedIndex());
		eventBus.fireEvent(new CategorySelectedEvent(category));
	}
}