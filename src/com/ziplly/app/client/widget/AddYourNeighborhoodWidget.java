package com.ziplly.app.client.widget;

import java.util.List;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.Image;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.ziplly.app.client.activities.SignupActivityPresenter;
import com.ziplly.app.client.resource.ZResources;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.model.NeighborhoodDTO;
import com.ziplly.app.shared.FieldVerifier;
import com.ziplly.app.shared.ValidationResult;

public class AddYourNeighborhoodWidget extends Composite {

	private static AddYourNeighborhoodWidgetUiBinder uiBinder = GWT
	    .create(AddYourNeighborhoodWidgetUiBinder.class);

	interface AddYourNeighborhoodWidgetUiBinder extends UiBinder<Widget, AddYourNeighborhoodWidget> {
	}

	private SignupActivityPresenter presenter;

	private NeighborhoodDTO templateNeighborhood;

	public AddYourNeighborhoodWidget(NeighborhoodDTO root, SignupActivityPresenter presenter) {
		initWidget(uiBinder.createAndBindUi(this));
		this.templateNeighborhood = root;
		this.presenter = presenter;
		clear();
	}

	private void clear() {
		message.setVisible(false);
		neighborhoodNameCg.setType(ControlGroupType.NONE);
		neighborhoodNameTextBox.setText("");
		neighborhoodNameError.setText("");
		neighborhoodNameError.setVisible(false);
		StyleHelper.show(neighborhoodLoadingImage.getElement(), false);
		StyleHelper.show(neighborhoodListBox.getElement(), false);
  }

	@UiField
	Image neighborhoodLoadingImage;
	
	@UiField
	Modal modal;
	
	@UiField
	Alert message;
	
	@UiField
	ListBox neighborhoodListBox;
	
	@UiField
	ControlGroup neighborhoodNameCg;
	@UiField
	TextBox neighborhoodNameTextBox;
	@UiField
	HelpInline neighborhoodNameError;
	
	@UiField
	Button submitBtn;
	
	@UiField
	Button cancelBtn;

	private List<NeighborhoodDTO> neighborhoods;
	
	public void displayNeighborhoods(List<NeighborhoodDTO> neighborhoods) {
		showLoading(false);
		if (neighborhoods.size() == 0) {
			displayMessage(StringConstants.NO_NEIGHBORHOODS_FOUND, AlertType.INFO);
			StyleHelper.show(neighborhoodListBox.getElement(), false);
			return;
		}
		
		this.neighborhoods = neighborhoods;
		for(NeighborhoodDTO n : neighborhoods) {
			neighborhoodListBox.addItem(n.getName());
		}
		StyleHelper.show(neighborhoodListBox.getElement(), true);
	}
	
	public void show(boolean display) {
		if (display) {
			modal.show();
		} else {
			modal.hide();
		}
	}
	
	@UiHandler(value="cancelBtn")
	public void onClick(ClickEvent event) {
		show(false);
	}
	
	@UiHandler("submitBtn")
	public void submit(ClickEvent event) {
		boolean isEmpty = FieldVerifier.isEmpty(neighborhoodNameTextBox.getText());
		if (!isEmpty) {
			ValidationResult result = FieldVerifier.validateName(neighborhoodNameTextBox.getText());
			if (!result.isValid()) {
				neighborhoodNameCg.setType(ControlGroupType.ERROR);
				neighborhoodNameError.setText(result.getErrors().get(0).getErrorMessage());
				return;
			}
			String neighborhoodName = FieldVerifier.sanitize(neighborhoodNameTextBox.getText());
			boolean confirm = Window.confirm("Are you sure you want to add neighborhood "+neighborhoodName);
			if (!confirm) {
				return;
			}
			
			templateNeighborhood.setName(neighborhoodName);
			presenter.createNeighborhood(templateNeighborhood);
		} else {
			NeighborhoodDTO selectedNeighborhood = neighborhoods.get(neighborhoodListBox.getSelectedIndex());
			presenter.setCurrentNeighborhood(selectedNeighborhood);
			show(false);
		}
	}
	
	public void displayMessage(String msg, AlertType type) {
		message.setText(msg);
		message.setType(type);
		message.setVisible(true);
	}

	public void showLoading(boolean show) {
		if (show) {
			neighborhoodLoadingImage.setUrl(ZResources.IMPL.loadingImageSmall().getSafeUri().asString());
			StyleHelper.show(neighborhoodLoadingImage.getElement(), true);
			submitBtn.setEnabled(false);
		} else {
			StyleHelper.show(neighborhoodLoadingImage.getElement(), false);
			submitBtn.setEnabled(true);
		}
  }
}
