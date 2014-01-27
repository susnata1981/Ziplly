package com.ziplly.app.client.view;

import com.google.gwt.user.client.ui.IsWidget;
import com.ziplly.app.client.activities.Presenter;
import com.ziplly.app.model.AccountDTO;

public interface View<T extends Presenter> extends IsWidget {
	void setPresenter(T presenter);
	void clear();
}
