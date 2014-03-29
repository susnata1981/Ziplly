package com.ziplly.app.client.widget;

import java.util.ArrayList;
import java.util.List;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.Image;
import com.github.gwtbootstrap.client.ui.Label;
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
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.ziplly.app.client.activities.SignupActivityPresenter;
import com.ziplly.app.client.resource.ZResources;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.model.NeighborhoodDTO;
import com.ziplly.app.shared.FieldVerifier;
import com.ziplly.app.shared.ValidationResult;

public class AddYourNeighborhoodWidget extends Composite {
	private static final String NONE_SELECTED = "None";

	private static AddYourNeighborhoodWidgetUiBinder uiBinder = GWT
	    .create(AddYourNeighborhoodWidgetUiBinder.class);

	interface AddYourNeighborhoodWidgetUiBinder extends UiBinder<Widget, AddYourNeighborhoodWidget> {
	}

	private SignupActivityPresenter presenter;

	private NeighborhoodDTO rootNeighborhood;

	public AddYourNeighborhoodWidget(NeighborhoodDTO root, SignupActivityPresenter presenter) {
		initWidget(uiBinder.createAndBindUi(this));
		this.rootNeighborhood = root;
		this.presenter = presenter;
		displayParentNeighborhoodInfo();
		clear();
	}

	private void displayParentNeighborhoodInfo() {
		NeighborhoodDTO n = rootNeighborhood.getParentNeighborhood();
		List<String> places = new ArrayList<String>();
		while (n != null) {
			places.add(n.getName());
			n = n.getParentNeighborhood();
		}
		
		int len = places.size();
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<len; i++) {
			sb.append(places.get(i));
			if ( i != len - 1) {
				sb.append(" -> ");
			}
		}
		neighborhoodInfoPanel.add(new Label(sb.toString()));
		neighborhoodInfoPanel.add(new HTMLPanel("<br>"));
		neighborhoodInfoPanel.add(new Label("Select your neighborhood"));
  }

	private void clear() {
		message.setVisible(false);
		neighborhoodNameTextBox.setText("");
		StyleHelper.show(neighborhoodLoadingImage.getElement(), false);
		StyleHelper.show(neighborhoodListBox.getElement(), false);
		clearError();
  }

	private void clearError() {
		neighborhoodNameCg.setType(ControlGroupType.NONE);
		neighborhoodNameError.setText("");
		neighborhoodNameError.setVisible(false);
	}
	
	@UiField
	Image neighborhoodLoadingImage;
	
	@UiField
	Modal modal;
	
	@UiField
	Alert message;
	
	@UiField
	HTMLPanel neighborhoodInfoPanel; 
	
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
		neighborhoodListBox.addItem(NONE_SELECTED);
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
	
	@UiHandler("cancelBtn")
	public void onClick(ClickEvent event) {
		show(false);
	}
	
	@UiHandler("submitBtn")
	public void submit(ClickEvent event) {
		clearError();
		boolean isEmpty = FieldVerifier.isEmpty(neighborhoodNameTextBox.getText());
		if (!isEmpty) {
			ValidationResult result = FieldVerifier.validateName(neighborhoodNameTextBox.getText());
			if (!result.isValid()) {
				neighborhoodNameCg.setType(ControlGroupType.ERROR);
				neighborhoodNameError.setText(result.getErrors().get(0).getErrorMessage());
				neighborhoodNameError.setVisible(true);
				return;
			}
			String neighborhoodName = FieldVerifier.sanitize(neighborhoodNameTextBox.getText());
			
			if (existsName(neighborhoodName)) {
				neighborhoodNameCg.setType(ControlGroupType.ERROR);
				neighborhoodNameError.setText(StringConstants.DUPLICATE_NEIGHBORHOOD_NAME);
				neighborhoodNameError.setVisible(true);
				return;
			}
			
			boolean confirm = Window.confirm("Are you sure you want to add neighborhood \""+neighborhoodName+"\"");
			if (!confirm) {
				return;
			}
			
			rootNeighborhood.setName(neighborhoodName);
			presenter.createNeighborhood(rootNeighborhood);
		} else {
			String name = neighborhoodListBox.getItemText(neighborhoodListBox.getSelectedIndex());
			if (name.equals(NONE_SELECTED)) {
				displayMessage(StringConstants.ADD_OR_SELECT_NEIGHBORHOOD, AlertType.ERROR);
				return;
			}
			
			NeighborhoodDTO selectedNeighborhood = findNeighborhood(name);
			if (selectedNeighborhood == null) {
				displayMessage(StringConstants.FAILURE, AlertType.ERROR);
				return;
			}
			
			boolean confirm = Window.confirm("Are you sure your neighborhood is \""+selectedNeighborhood.getName()+"\"");
			if (!confirm) {
				return;
			}
			
			presenter.setCurrentNeighborhood(selectedNeighborhood);
			show(false);
		}
	}
	
	private boolean existsName(String neighborhoodName) {
		for(NeighborhoodDTO n : neighborhoods) {
			if (n.getName().equals(neighborhoodName)) {
				return true;
			}
		}
		return false;
  }

	private NeighborhoodDTO findNeighborhood(String name) {
		for(NeighborhoodDTO n : neighborhoods) {
			if (n.getName().equals(name)) {
				return n;
			}
		}
		
		return null;
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
	
	public static String reverseIt(String source) {
    int i, len = source.length();
    StringBuffer dest = new StringBuffer(len);

    for (i = (len - 1); i >= 0; i--)
      dest.append(source.charAt(i));
    return dest.toString();
  }
}
