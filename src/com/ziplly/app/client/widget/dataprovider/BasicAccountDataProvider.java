package com.ziplly.app.client.widget.dataprovider;

import com.google.gwt.view.client.AbstractDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.inject.Inject;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.view.ResidentsView;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.PersonalAccountDTO;
import com.ziplly.app.shared.GetEntityListAction;
import com.ziplly.app.shared.GetEntityResult;

public class BasicAccountDataProvider extends AbstractDataProvider<PersonalAccountDTO>{
	private ResidentsView view;
	@Inject
	private CachingDispatcherAsync dispatcher;
	private AccountDTO account;
	
	@Inject
	public BasicAccountDataProvider(CachingDispatcherAsync dispatcher) {
		this.dispatcher = dispatcher;
	}
	
	@Override
	protected void onRangeChanged(HasData<PersonalAccountDTO> display) {
//		dispatcher.execute(new GetEntityListAction(account), new DispatcherCallbackAsync<GetEntityResult>() {
//			@Override
//			public void onSuccess(GetEntityResult result) {
//				view.getResidentsList().setRowData(result.getAccounts());
//			}
//		});
	}

	public void setView(ResidentsView view) {
		this.view = view;
	}
	
	public void setAccount(AccountDTO account) {
		this.account = account;
	}
}
