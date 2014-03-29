package com.ziplly.app.client.view;

import java.util.List;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.ziplly.app.client.activities.SignupActivityPresenter;
import com.ziplly.app.model.NeighborhoodDTO;
import com.ziplly.app.model.PersonalAccountDTO;

public interface ISignupView<T extends SignupActivityPresenter> extends View<T> {
	void reset();

	void setPresenter(T businessSignupActivity);

	void clear();

	void displayAccount(PersonalAccountDTO a);

	void displayMessage(String accountDoesNotExist, AlertType error);

	void displayNeighborhoods(List<NeighborhoodDTO> neighbordhoods);

	void displayNotYetLaunchedWidget();

	void displayNeighborhoodListLoading(boolean b);

	void displayNeighborhood(NeighborhoodDTO neighborhoodDTO);

	void displayNewNeighborhood(NeighborhoodDTO neighborhoodDTO);

	void displayNeighborhoodList(List<NeighborhoodDTO> neighbordhoods);

	void displayErrorDuringNeighborhoodSelection(String failedToAddNeighborhood, AlertType error);
}
