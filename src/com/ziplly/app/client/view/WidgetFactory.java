package com.ziplly.app.client.view;

import com.ziplly.app.client.activities.HomePresenter;
import com.ziplly.app.client.widget.IAccountWidgetModal;
import com.ziplly.app.client.widget.PersonalAccountWidgetModal;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.PersonalAccountDTO;


public class WidgetFactory {
	public static PersonalAccountWidgetModal pAccountModal = new PersonalAccountWidgetModal();
	
	// Can't figure out how to enable type checking on return type
	public static <T extends AccountDTO> IAccountWidgetModal<? extends AccountDTO> getAccountWidgetModal(T input, HomePresenter presenter) {
		if (input instanceof PersonalAccountDTO) {
			pAccountModal.setPresenter(presenter);
			return pAccountModal;
		} else {
			// business account
			return null;
		}
	}
}
