package com.ziplly.app.client.view;

import java.util.List;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.user.client.ui.FormPanel;
import com.ziplly.app.client.activities.SignupActivityPresenter;
import com.ziplly.app.model.NeighborhoodDTO;
import com.ziplly.app.model.PersonalAccountDTO;

public interface ISignupView<T extends SignupActivityPresenter> extends View<T> {
	void displayProfileImagePreview(String imageUrl);
	void setImageUploadUrl(String url);
	void reset();
	void addUploadFormHandler(FormPanel.SubmitCompleteHandler handler);
	void setPresenter(T businessSignupActivity);
	void clear();
	void hideProfileImagePreview();
	void displayAccount(PersonalAccountDTO a);
	void displayMessage(String accountDoesNotExist, AlertType error);
	void resetLoginForm();
	void displayNeighborhoods(List<NeighborhoodDTO> neighbordhoods);
	void displayNotYetLaunchedWidget();
}
