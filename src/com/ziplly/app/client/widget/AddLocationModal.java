package com.ziplly.app.client.widget;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.Controls;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.Image;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.RadioButton;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.ziplly.app.client.resource.ZResources;
import com.ziplly.app.model.LocationDTO;
import com.ziplly.app.model.LocationType;
import com.ziplly.app.model.NeighborhoodDTO;
import com.ziplly.app.shared.FieldVerifier;
import com.ziplly.app.shared.ValidationResult;

public class AddLocationModal extends Composite {

	private static AddLocationModalUiBinder uiBinder = GWT.create(AddLocationModalUiBinder.class);

	interface AddLocationModalUiBinder extends UiBinder<Widget, AddLocationModal> {
	}

	public AddLocationModal() {
		initWidget(uiBinder.createAndBindUi(this));
		hide();
		StyleHelper.show(message.getElement(), false);
		StyleHelper.show(neighborhoodControl.getElement(), false);
	}

	@UiField
	Modal modal;
	@UiField
	Alert message;
	
	// Zip
	@UiField
	TextBox zip;
	@UiField
	ControlGroup zipCg;
	@UiField
	HelpInline zipError;
	
	// Neighborhood
	@UiField
	Image neighborhoodLoadingImage;
	@UiField
	Controls neighborhoodControl;
	@UiField
	HTMLPanel neighborhoodListPanel;
	
	// Address
	@UiField
	ControlGroup addressCg;
	@UiField
	HelpInline addressError;
	@UiField
	TextBox address;
	
	@UiField
	Button addBtn;
	@UiField
	Button cancelBtn;
	
	private List<NeighborhoodDTO> neighborhoods;
	private Map<RadioButton, NeighborhoodDTO> neighborhoodRadioButtonMap = new HashMap<RadioButton, NeighborhoodDTO>();
	
	public void show(boolean b) {
		if (b) {
			modal.show();
		} else {
			modal.hide();
		}
	}
	
	public void hide() {
		modal.hide();
	}
	
	@UiHandler("cancelBtn")
	public void cancel(ClickEvent event) {
		clearError();
		modal.hide();
	}
	
	public Button getAddLocationButton() {
		return addBtn;
	}
	
	public void displayNeighborhoods(List<NeighborhoodDTO> neighborhoods) {
		clearMessage();
		neighborhoodListPanel.clear();
		this.neighborhoods = neighborhoods;
		for(NeighborhoodDTO n : neighborhoods) {
			RadioButton rb = new RadioButton("neighborhood");
			rb.setText(n.getName());
			neighborhoodRadioButtonMap.put(rb, n);
			neighborhoodListPanel.add(rb);
		}
		StyleHelper.show(neighborhoodControl.getElement(), true);
	}

	private void clearMessage() {
		message.setText("");
		StyleHelper.show(message.getElement(), false);
	}
	
	public boolean validate() {
		boolean isValid = validateZip();
		isValid &= validateAddress();
		
		return isValid;
	}
	
	public void displayNeighborhoodListLoading(boolean display) {
		if (display) {
			neighborhoodLoadingImage.setUrl(ZResources.IMPL.loadingImageSmall().getSafeUri());
			StyleHelper.show(neighborhoodLoadingImage.getElement(), true);
		} else {
			StyleHelper.show(neighborhoodLoadingImage.getElement(), false);
		}
	}

	public void displayMessage(String msg, AlertType info) {
		message.setText(msg);
		message.setType(info);
		StyleHelper.show(message.getElement(), true);
	}

	public LocationDTO getLocation() {
		LocationDTO location = new LocationDTO();
		location.setAddress(FieldVerifier.sanitize(address.getText()));
		for(RadioButton rb : neighborhoodRadioButtonMap.keySet()) {
			if (rb.getValue()) {
				location.setNeighborhood(neighborhoodRadioButtonMap.get(rb));
				break;
			}
		}
		location.setType(LocationType.OTHER);
		return location;
	}
	
	public boolean validateZip() {
		String zipInput = zip.getText().trim();
		ValidationResult validateZip = FieldVerifier.validateZip(zipInput);
		if (!validateZip.isValid()) {
			setControlMessageForZipcodeField(validateZip.getErrors().get(0).getErrorMessage(),
					true,
					ControlGroupType.ERROR);
			return false;
		}
		return true;
	}
	
	public boolean validateAddress() {
		String addr = FieldVerifier.sanitize(address.getText());
		ValidationResult result = FieldVerifier.validateString(addr, FieldVerifier.MAX_ADDRESS_LENGTH);
		if (!result.isValid()) {
			addressCg.setType(ControlGroupType.ERROR);
			addressError.setText(result.getErrors().get(0).getErrorMessage());
			addressError.setVisible(true);
			return false;
		}
		
		return true;
	}
	
	void setControlMessageForZipcodeField(String msg, boolean msgVisible, ControlGroupType type) {
		zipCg.setType(type);
		zipError.setText(msg);
		zipError.setVisible(msgVisible);
	}

	public void clearError() {
		addressCg.setType(ControlGroupType.NONE);
		addressError.setText("");
		addressError.setVisible(false);
		zipCg.setType(ControlGroupType.NONE);
		zipError.setText("");
		zipError.setVisible(false);
	}

	public TextBox getZipTextBox() {
		return zip;
	}
}
