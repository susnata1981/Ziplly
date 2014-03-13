package com.ziplly.app.client.widget;

import java.util.ArrayList;
import java.util.List;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Controls;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.RadioButton;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.github.gwtbootstrap.client.ui.constants.BackdropType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.ziplly.app.model.NeighborhoodDTO;

public class NeighborhoodSelectorWidget extends Composite implements HasClickHandlers {

	private static NeighborhoodSelectorWidgetUiBinder uiBinder = GWT
	    .create(NeighborhoodSelectorWidgetUiBinder.class);

	interface NeighborhoodSelectorWidgetUiBinder extends UiBinder<Widget, NeighborhoodSelectorWidget> {
	}

	public NeighborhoodSelectorWidget() {
		initWidget(uiBinder.createAndBindUi(this));
		modalPanel.setCloseVisible(false);
		modalPanel.setBackdrop(BackdropType.STATIC);
		modalPanel.setKeyboard(false);
	}

	@UiField
	Modal modalPanel;

	@UiField
	Controls radioButtonControl;

	@UiField
	Button submitBtn;

	@UiField
	Alert message;

	private List<NeighborhoodDTO> neighborhoods;
	private List<RadioButton> radioButtons = new ArrayList<RadioButton>();

	public void displayNeighborhoods(List<NeighborhoodDTO> neighborhoods) {
		radioButtonControl.clear();
		this.neighborhoods = neighborhoods;
		for (NeighborhoodDTO n : neighborhoods) {
			RadioButton rb = new RadioButton(Long.toString(n.getNeighborhoodId()));
			rb.setText(n.getName());
			radioButtonControl.add(rb);
			radioButtons.add(rb);
		}
	}

	public void show(boolean show) {
		message.setVisible(false);
		if (show) {
			modalPanel.show();
		} else {
			modalPanel.hide();
		}
	}

	public NeighborhoodDTO getSelection() {
		int count = radioButtonControl.getWidgetCount();
		for (int i = 0; i < count; i++) {
			RadioButton rb = (RadioButton) radioButtonControl.getWidget(i);
			if (rb.getValue()) {
				return neighborhoods.get(i);
			}
		}
		return null;
	}

	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return submitBtn.addClickHandler(handler);
	}

	public void displayMessage(String msg, AlertType type) {
		message.setText(msg);
		message.setType(type);
		message.setVisible(true);
	}
}
