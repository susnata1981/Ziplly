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

	interface AddLocationModalUiBinder extends UiBinder<Widget, AddLocationModal> {
	}

	private static AddLocationModalUiBinder uiBinder = GWT.create(AddLocationModalUiBinder.class);

	@UiField
	Button addBtn;

	@UiField
	TextBox address;
	// Address
	@UiField
	ControlGroup addressCg;

	@UiField
	HelpInline addressError;
	@UiField
	Button cancelBtn;
	@UiField
	Alert message;

	@UiField
	Modal modal;
	@UiField
	Controls neighborhoodControl;
	@UiField
	HTMLPanel neighborhoodListPanel;

	// Neighborhood
	@UiField
	Image neighborhoodLoadingImage;
	private final Map<RadioButton, NeighborhoodDTO> neighborhoodRadioButtonMap =
			new HashMap<RadioButton, NeighborhoodDTO>();
	List<NeighborhoodDTO> neighborhoods;

	// Zip
	@UiField
	TextBox zip;
	@UiField
	ControlGroup zipCg;

	@UiField
	HelpInline zipError;
	public AddLocationModal() {
		initWidget(uiBinder.createAndBindUi(this));
		hide();
		StyleHelper.show(message.getElement(), false);
		StyleHelper.show(neighborhoodControl.getElement(), false);
	}

	@UiHandler("cancelBtn")
	public void cancel(final ClickEvent event) {
		clearError();
		modal.hide();
	}

	public void clearError() {
		addressCg.setType(ControlGroupType.NONE);
		addressError.setText("");
		addressError.setVisible(false);
		zipCg.setType(ControlGroupType.NONE);
		zipError.setText("");
		zipError.setVisible(false);
	}

	private void clearMessage() {
		message.setText("");
		StyleHelper.show(message.getElement(), false);
	}

	public void displayMessage(final String msg, final AlertType info) {
		message.setText(msg);
		message.setType(info);
		StyleHelper.show(message.getElement(), true);
	}

	public void displayNeighborhoodListLoading(final boolean display) {
		if (display) {
			neighborhoodLoadingImage.setUrl(ZResources.IMPL.loadingImageSmall().getSafeUri());
			StyleHelper.show(neighborhoodLoadingImage.getElement(), true);
		} else {
			StyleHelper.show(neighborhoodLoadingImage.getElement(), false);
		}
	}

	public void displayNeighborhoods(final List<NeighborhoodDTO> neighborhoods) {
		clearMessage();
		neighborhoodListPanel.clear();
		this.neighborhoods = neighborhoods;
		for (NeighborhoodDTO n : neighborhoods) {
			RadioButton rb = new RadioButton("neighborhood");
			rb.setText(n.getName());
			neighborhoodRadioButtonMap.put(rb, n);
			neighborhoodListPanel.add(rb);
		}
		StyleHelper.show(neighborhoodControl.getElement(), true);
	}

	public Button getAddLocationButton() {
		return addBtn;
	}

	public LocationDTO getLocation() {
		LocationDTO location = new LocationDTO();
		location.setAddress(FieldVerifier.sanitize(address.getText()));
		for (RadioButton rb : neighborhoodRadioButtonMap.keySet()) {
			if (rb.getValue()) {
				location.setNeighborhood(neighborhoodRadioButtonMap.get(rb));
				break;
			}
		}
		location.setType(LocationType.OTHER);
		return location;
	}

	public TextBox getZipTextBox() {
		return zip;
	}

	public void hide() {
		modal.hide();
	}

	void setControlMessageForZipcodeField(final String msg, final boolean msgVisible, final ControlGroupType type) {
		zipCg.setType(type);
		zipError.setText(msg);
		zipError.setVisible(msgVisible);
	}

	public void show(final boolean b) {
		if (b) {
			modal.show();
		} else {
			modal.hide();
		}
	}

	public boolean validate() {
		boolean isValid = validateZip();
		isValid &= validateAddress();

		return isValid;
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

	public boolean validateZip() {
		String zipInput = zip.getText().trim();
		ValidationResult validateZip = FieldVerifier.validateZip(zipInput);
		if (!validateZip.isValid()) {
			setControlMessageForZipcodeField(
					validateZip.getErrors().get(0).getErrorMessage(),
					true,
					ControlGroupType.ERROR);
			return false;
		}
		return true;
	}
}
