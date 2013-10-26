package com.ziplly.app.client.activities;

import com.ziplly.app.client.view.IAccountView;
import com.ziplly.app.model.AccountDTO;

public abstract class AbstractAccountViewRenderer<T extends AccountDTO> implements IAccountViewRenderer<T>{
	protected IAccountView<T> view;

	public AbstractAccountViewRenderer(IAccountView<T> view) {
		this.setView(view);
	}

	public IAccountView<T> getView() {
		return view;
	}

	public void setView(IAccountView<T> view) {
		this.view = view;
	}
}
