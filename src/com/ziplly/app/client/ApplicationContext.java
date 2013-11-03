package com.ziplly.app.client;

import com.ziplly.app.client.view.HomeView;
import com.ziplly.app.client.view.IHomeView;
import com.ziplly.app.model.AccountDTO;

public class ApplicationContext {
	private IHomeView homeView;
	private AccountDTO account;

	public ApplicationContext() {
	}
	
	public AccountDTO getAccount() {
		return account;
	}

	public void setAccount(AccountDTO account) {
		this.account = account;
	}

	public IHomeView getHomeView() {
		return homeView;
	}

	public void setHomeView(IHomeView homeView2) {
		this.homeView = homeView2;
	}
}
