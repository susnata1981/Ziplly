package com.ziplly.app.client.widget;

import com.ziplly.app.client.activities.Presenter;
import com.ziplly.app.model.AccountDTO;

public interface IAccountWidgetModal<T extends AccountDTO> {
	void show(T account);
	void hide();
	void setPresenter(Presenter presenter);
}
