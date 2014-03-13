package com.ziplly.app.client.view;

import com.ziplly.app.client.activities.Presenter;
import com.ziplly.app.client.widget.BusinessAccountWidgetModal;
import com.ziplly.app.client.widget.IAccountWidgetModal;
import com.ziplly.app.client.widget.PersonalAccountWidgetModal;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.BusinessAccountDTO;
import com.ziplly.app.model.PersonalAccountDTO;

public class WidgetFactory {
	public static PersonalAccountWidgetModal pAccountModal = new PersonalAccountWidgetModal();
	public static BusinessAccountWidgetModal bAccountModal = new BusinessAccountWidgetModal();

	// Can't figure out how to enable type checking on return type
	public static <T extends AccountDTO>
	    IAccountWidgetModal<? extends AccountDTO>
	    getAccountWidgetModal(T input, Presenter presenter) {
		if (input instanceof PersonalAccountDTO) {
			pAccountModal.setPresenter(presenter);
			pAccountModal.setWidth("30%");
			return pAccountModal;
		} else if (input instanceof BusinessAccountDTO) {
			bAccountModal.setPresenter(presenter);
			bAccountModal.setWidth("25%");
			return bAccountModal;
		}

		throw new IllegalArgumentException();
	}
}
